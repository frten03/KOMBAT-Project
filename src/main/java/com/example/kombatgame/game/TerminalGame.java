package com.example.kombatgame.game;

import com.example.kombatgame.strategy.parser.Parser;
import com.example.kombatgame.strategy.tokenizer.Tokenizer;

import java.util.*;

public class TerminalGame {

    private static final int SIZE = 8;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // CONFIG SETUP
        long spawnCost   = readConfigLong(sc, "spawn_cost: ", false);
        long hexCost     = readConfigLong(sc, "hex_purchase_cost: ", false);
        long initBudget  = readConfigLong(sc, "init_budget: ", false);
        long initHp      = readConfigLong(sc, "init_hp: ", true);
        long turnBudget  = readConfigLong(sc, "turn_budget: ", false);
        long maxBudget   = readConfigLong(sc, "max_budget: ", true);
        long interest    = readConfigLong(sc, "interest_pct: ", true);
        long maxTurns    = readConfigLong(sc, "max_turns: ", true);
        long maxSpawns   = readConfigLong(sc, "max_spawns: ", false);

        GameConfig config = new GameConfig(
                spawnCost, hexCost, initBudget, initHp,
                turnBudget, maxBudget, interest,
                maxTurns, maxSpawns
        );

        int modeChoice;

        while (true) {
            System.out.println("\nChoose mode: 1=DUEL 2=SOLITAIRE 3=AUTO");
            String input = sc.nextLine().trim();

            try {
                modeChoice = Integer.parseInt(input);

                if (modeChoice >= 1 && modeChoice <= 3) {
                    break;
                } else {
                    System.out.println("Please enter 1, 2, or 3.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        GameMode mode = switch (modeChoice) {
            case 1 -> GameMode.DUEL;
            case 2 -> GameMode.SOLITAIRE;
            default -> GameMode.AUTO;
        };

        GameState game = new GameState(config, mode);

        // MINION TYPE SETUP
        System.out.println("\n=== DEFAULT MINION TYPES ===");

        for (MinionType t : game.getBaseTypes()) {
            System.out.println("- " + t.getName()
                    + " DEF=" + t.getDefense());
        }

        int typeCount;

        while (true) {

            System.out.print("\nNumber of types to use (1-5): ");
            String input = sc.nextLine().trim();

            try {
                typeCount = Integer.parseInt(input);

                if (typeCount < 1 || typeCount > 5) {
                    System.out.println("Must choose between 1 and 5 types.");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        Set<String> selected = new LinkedHashSet<>();

        while (selected.size() < typeCount) {

            System.out.print("Choose type name: ");
            String name = sc.nextLine();

            boolean exists = false;
            for (MinionType t : game.getBaseTypes()) {
                if (t.getName().equals(name)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                System.out.println("Type does not exist.");
                continue;
            }

            if (selected.contains(name)) {
                System.out.println("Duplicate type name.");
                continue;
            }

            selected.add(name);
        }

        // select subset
        game.selectMinionTypes(game.getPlayer1(), new ArrayList<>(selected));
        game.selectMinionTypes(game.getPlayer2(), new ArrayList<>(selected));

        for (String name : selected) {

            System.out.println("\nCustomize " + name);

            // ดึงค่า default จาก baseTypes
            MinionType baseType = null;
            for (MinionType t : game.getBaseTypes()) {
                if (t.getName().equals(name)) {
                    baseType = t;
                    break;
                }
            }

            if (baseType == null) continue;

            int defense = baseType.getDefense();
            String strategySource = baseType.getStrategySource();

            // DEFENSE
            System.out.println("Current defense: " + defense);
            System.out.print("New defense (press Enter to keep): ");

            String defenseInput = sc.nextLine();

            if (!defenseInput.isBlank()) {
                try {
                    int newDefense = Integer.parseInt(defenseInput);

                    if (newDefense < 0) {
                        System.out.println("Defense cannot be negative. Using default.");
                    } else {
                        defense = newDefense;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Using default.");
                }
            }

            // STRATEGY (Editable + Line Error)
            while (true) {

                System.out.println("\nCurrent strategy:");
                System.out.println("---------------------------------");

                if (strategySource.isBlank()) {
                    System.out.println("(empty)");
                } else {
                    String[] oldLines = strategySource.split("\n");
                    for (int i = 0; i < oldLines.length; i++) {
                        System.out.println((i + 1) + ": " + oldLines[i]);
                    }
                }

                System.out.println("---------------------------------");
                System.out.println("Enter new strategy (END to finish).");
                System.out.println("Press ENTER immediately to keep current.");

                StringBuilder sb = new StringBuilder();

                String firstLine = sc.nextLine();

                // ถ้ากด Enter ทันที → keep ของเดิม
                if (firstLine.isBlank()) {
                    break;
                }

                sb.append(firstLine).append("\n");

                while (true) {
                    String line = sc.nextLine();
                    if (line.equals("END")) break;
                    sb.append(line).append("\n");
                }

                String newStrategy = sb.toString().trim();

                try {
                    Tokenizer tokenizer = new Tokenizer(newStrategy);
                    Parser parser = new Parser(tokenizer);
                    parser.parseStatement();

                    System.out.println("Strategy OK");
                    strategySource = newStrategy;
                    break;

                } catch (Exception e) {

                    System.out.println("=================================");
                    System.out.println("STRATEGY ERROR");

                    String message = e.getMessage();
                    System.out.println("Reason: " + message);

                    String[] lines = newStrategy.split("\n");
                    int errorLine = extractLineNumber(message);

                    if (errorLine >= 1 && errorLine <= lines.length) {

                        for (int j = 0; j < lines.length; j++) {
                            if (j + 1 == errorLine) {
                                System.out.println((j + 1) + ": " + lines[j] + "  <<< ERROR HERE");
                            } else {
                                System.out.println((j + 1) + ": " + lines[j]);
                            }
                        }

                    } else {
                        for (int j = 0; j < lines.length; j++) {
                            System.out.println((j + 1) + ": " + lines[j]);
                        }
                    }

                    System.out.println("=================================");
                    System.out.println("Please re-enter strategy.\n");
                }
            }

            game.updateMinionType(game.getPlayer1(), name, defense, strategySource);
            game.updateMinionType(game.getPlayer2(), name, defense, strategySource);
        }

        // INITIAL SPAWN
        System.out.println("\n=== INITIAL SPAWN PHASE ===");

        while (game.getPhase() == GameState.Phase.SETUP) {

            Player p = game.getCurrentPlayer();

            printBoard(game, p);
            System.out.println(currentPlayerName(game));

            if (p.isBot()) {

                // ให้ bot เลือกเอง
                game.botInitialSpawn();
                System.out.println("Bot spawned.");

                continue;
            }

            // HUMAN SPAWN
            System.out.print("Choose type: ");
            String type = sc.nextLine();

            int r;
            while (true) {
                System.out.print("Row: ");
                String input = sc.nextLine().trim();

                try {
                    r = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please enter a valid integer.");
                }
            }

            int c;
            while (true) {
                System.out.print("Col: ");
                String input = sc.nextLine().trim();

                try {
                    c = Integer.parseInt(input);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please enter a valid integer.");
                }
            }

            boolean ok = game.initialSpawn(type, r, c);
            System.out.println(ok ? "Spawned." : "Failed.");
        }

        // MAIN GAME LOOP
        while (!game.isGameOver()) {
            game.startTurn();

            Player p = game.getCurrentPlayer();

            System.out.println("\n----------------------------------");
            System.out.println("global Turn: " + game.getGlobalTurn());
            System.out.println("Player Turn: " + p.getTurnCount());
            System.out.println(currentPlayerName(game));
            System.out.println("HP: " + p.sumHP());
            System.out.println("Budget: " + p.getBudget());
            System.out.println("Spawns left: " + p.getSpawnsLeft());

            if (p.isBot()) {
                game.playTurn();   // bot ทำครบในนี้
                printBoard(game, game.getCurrentPlayer());
                continue;
            }

            printBoard(game, game.getCurrentPlayer());

            if (!p.isBot()) {

                while (true) {

                    if (!readYesNo(sc, "Buy hex? (y/n): ")) break;

                    System.out.println("Buyable hexes:");
                    for (int r = 1; r <= SIZE; r++) {
                        for (int c = 1; c <= SIZE; c++) {
                            if (game.canPurchase(r, c)) {
                                System.out.print("(" + r + "," + c + ") ");
                            }
                        }
                    }
                    System.out.println();

                    int r;
                    while (true) {
                        System.out.print("Row: ");
                        String input = sc.nextLine().trim();

                        try {
                            r = Integer.parseInt(input);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number. Please enter a valid integer.");
                        }
                    }
                    int c;
                    while (true) {
                        System.out.print("Col: ");
                        String input = sc.nextLine().trim();
                        try {
                            c = Integer.parseInt(input);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number. Please enter a valid integer.");
                        }
                    }

                    if (game.purchaseHex(r, c)) {
                        System.out.println("Purchased.");
                        break;
                    } else {
                        System.out.println("Failed. Try again.");
                    }
                }

                if (p.getSpawnsLeft() > 0) {

                    if (readYesNo(sc, "Spawn minion? (y/n): ")) {

                        System.out.println("Spawnable hexes:");
                        for (int r = 1; r <= SIZE; r++) {
                            for (int c = 1; c <= SIZE; c++) {
                                if (game.canSpawn(r, c)) {
                                    System.out.print("(" + r + "," + c + ") ");
                                }
                            }
                        }
                        System.out.println();

                        System.out.print("Type: ");
                        String type = sc.nextLine();

                        int r;
                        while (true) {
                            System.out.print("Row: ");
                            String input = sc.nextLine().trim();
                            try {
                                r = Integer.parseInt(input);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number.");
                            }
                        }

                        int c;
                        while (true) {
                            System.out.print("Col: ");
                            String input = sc.nextLine().trim();
                            try {
                                c = Integer.parseInt(input);
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid number.");
                            }
                        }

                        if (game.spawnMinion(type, r, c)) {
                            System.out.println("Spawned.");
                        } else {
                            System.out.println("Spawn failed.");
                        }
                    }

                } else {
                    System.out.println("Spawn limit reached. Cannot spawn.");
                }
            }

            game.executeMinions();
            game.endTurn();
        }

        System.out.println("\n=== GAME OVER ===");
        Player winner = game.determineWinner();

        System.out.println("Final Stats:");
        System.out.println("Player 1 - Minions: "
                + game.getPlayer1().getMinions().size()
                + " | HP: " + game.getPlayer1().sumHP()
                + " | Budget: " + game.getPlayer1().getBudget());

        System.out.println("Player 2 - Minions: "
                + game.getPlayer2().getMinions().size()
                + " | HP: " + game.getPlayer2().sumHP()
                + " | Budget: " + game.getPlayer2().getBudget());

        if (winner == null) {
            System.out.println("Tie.");
        } else {
            System.out.println("Winner: " +
                    (winner == game.getPlayer1() ? "Player 1" : "Player 2"));
        }
        System.out.println("Reason: " + game.getGameOverReason());
    }

    private static final String RESET = "\u001B[0m";

    private static final String BG_BLUE = "\u001B[44m";
    private static final String BG_RED  = "\u001B[41m";

    private static final String WHITE_TEXT = "\u001B[97m";

    private static void printBoard(GameState game, Player current) {

        Grid board = game.getBoard();

        System.out.println("\n===== HEX BOARD =====");

        // column header
        System.out.print("     ");
        for (int c = 1; c <= SIZE; c++) {
            System.out.printf("%-8d", c);
        }
        System.out.println();

        for (int r = 1; r <= SIZE; r++) {

            if (r % 2 == 0) System.out.print("   ");
            else System.out.print(" ");

            System.out.printf("%-3d", r);

            for (int c = 1; c <= SIZE; c++) {

                String hex;
                Minion m = board.getMinion(r, c);

                if (m != null) {
                    int owner = (m.getOwner() == game.getPlayer1()) ? 1 : 2;
                    int hp = m.getHp();

                    String text = String.format(" %d:%03d ", owner, hp);

                    if (owner == 1) {
                        hex = BG_BLUE + WHITE_TEXT + text + RESET;
                    } else {
                        hex = BG_RED + WHITE_TEXT + text + RESET;
                    }
                } else if (game.canSpawn(r, c)) {
                    hex = "[S]";
                } else if (game.canPurchase(r, c)) {
                    hex = "[$]";
                } else {
                    hex = "[ ]";
                }

                System.out.printf("%-8s", hex);
            }

            System.out.println();
        }

        System.out.println("\nLegend: [1]=P1  [2]=P2  [S]=Spawn  [$]=Buy");
    }

    private static String currentPlayerName(GameState game) {
        return game.getCurrentPlayer() == game.getPlayer1()
                ? "Player 1 Turn"
                : "Player 2 Turn";
    }

    private static long readConfigLong(Scanner sc, String prompt, boolean mustBePositive) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                long value = Long.parseLong(input);

                if (value < 0) {
                    System.out.println("Value cannot be negative.");
                    continue;
                }

                if (mustBePositive && value == 0) {
                    System.out.println("Value must be greater than 0.");
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static int extractLineNumber(String message) {

        if (message == null) return -1;

        try {
            String[] parts = message.trim().split(" ");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception e) {
            return -1;
        }
    }

    private static boolean readYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            System.out.println("Invalid input. Please enter y or n.");
        }
    }
}