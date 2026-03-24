package com.example.kombatgame.game;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameState {

    public enum Phase {
        SETUP,
        RUNNING,
        FINISHED
    }

    // getter
    @Getter
    private final GameConfig config;
    @Getter
    private final Player player1;
    @Getter
    private final Player player2;
    @Getter
    private final Grid board;
    private final GameMode mode;

    @Getter
    private List<MinionType> baseTypes;

    private final Random random = new Random();

    private final StrategyEvaluator evaluator;

    @Getter
    private Player currentPlayer;
    @Getter
    private int globalTurn = 1;
    private int completedTurns = 0;

    @Getter
    private Phase phase = Phase.SETUP;

    private boolean purchasedThisTurn = false;
    private boolean spawnedThisTurn = false;

    private boolean p1InitialSpawnDone = false;
    private boolean p2InitialSpawnDone = false;

    @Getter
    private String gameOverReason;

    public GameState(GameConfig config, GameMode mode) {
        if (config == null || mode == null)
            throw new IllegalArgumentException("Config and mode must not be null");

        this.config = config;
        this.mode = mode;

        this.player1 = new Player(1, config);
        this.player2 = new Player(2, config);

        configureMode();
        initBaseTypes();

        List<String> defaults = new ArrayList<>();
        for (MinionType t : baseTypes) {
            defaults.add(t.getName());
        }

        selectMinionTypes(player1, defaults);
        selectMinionTypes(player2, defaults);

        this.board = new Grid(8);
        this.evaluator = new StrategyEvaluator();

        this.currentPlayer = player1;
        initSpawnZones();
    }

    // mode
    private void configureMode() {
        switch (mode) {
            case DUEL -> {
                player1.setBot(false);
                player2.setBot(false);
            }
            case SOLITAIRE -> {
                player1.setBot(false);
                player2.setBot(true);
            }
            case AUTO -> {
                player1.setBot(true);
                player2.setBot(true);
            }
        }
    }

    // spawn zone
    private void initSpawnZones() {
        // player1 top-left
        player1.addSpawnableHex(1, 1);
        player1.addSpawnableHex(1, 2);
        player1.addSpawnableHex(2, 1);
        player1.addSpawnableHex(2, 2);
        player1.addSpawnableHex(1, 3);

        // player2 bottom-right
        player2.addSpawnableHex(8, 8);
        player2.addSpawnableHex(8, 7);
        player2.addSpawnableHex(7, 8);
        player2.addSpawnableHex(7, 7);
        player2.addSpawnableHex(8, 6);
    }

    // default minion
    private void initBaseTypes() {
        baseTypes = new ArrayList<>();

        baseTypes.add(new MinionType(
                "Warrior",
                5,
                DEFAULT_STRATEGY.replace("baseCost", "8")
        ));

        baseTypes.add(new MinionType(
                "Archer",
                3,
                DEFAULT_STRATEGY.replace("baseCost", "10")
        ));

        baseTypes.add(new MinionType(
                "Tank",
                8,
                DEFAULT_STRATEGY.replace("baseCost", "16")
        ));

        baseTypes.add(new MinionType(
                "Sniper",
                2,
                DEFAULT_STRATEGY.replace("baseCost", "14")
        ));

        baseTypes.add(new MinionType(
                "Scout",
                1,
                DEFAULT_STRATEGY.replace("baseCost", "4")
        ));
    }

    private static final String DEFAULT_STRATEGY = """
            {
                op = opponent
            
                if (Budget - 2) then {} else done
            
                if (op)
                then {
                    dir = op % 10
            
                    if (op / 10 - 1)
                    then
                        if (dir - 5) then move downleft
                        else if (dir - 4) then move down
                        else if (dir - 3) then move downright
                        else if (dir - 2) then move upright
                        else if (dir - 1) then move up
                        else move upleft
                    else {
                        cost = baseCost
            
                        if (Budget - cost)
                        then
                            if (dir - 5) then shoot downleft cost
                            else if (dir - 4) then shoot down cost
                            else if (dir - 3) then shoot downright cost
                            else if (dir - 2) then shoot upright cost
                            else if (dir - 1) then shoot up cost
                            else shoot upleft cost
                        else move up
                    }
                }
                else {
                    r = random % 6
            
                    if (r - 5) then move downleft
                    else if (r - 4) then move down
                    else if (r - 3) then move downright
                    else if (r - 2) then move upright
                    else if (r - 1) then move up
                    else move upleft
                }
            }
            """;

    // setup phase
    public boolean initialSpawn(String typeName, int r, int c) {

        if (phase != Phase.SETUP) return false;

        Player p = currentPlayer;

        MinionType type = p.getTypeByName(typeName);
        if (type == null) return false;
        if (!p.isSpawnable(r, c)) return false;
        if (!board.isEmpty(r, c)) return false;

        Minion m = new Minion(
                r,
                c,
                (int) config.getInitHp(),
                type,
                p
        );

        board.addMinion(m);
        p.addMinion(m);

        if (p == player1) p1InitialSpawnDone = true;
        else p2InitialSpawnDone = true;

        switchTurn();

        if (p1InitialSpawnDone && p2InitialSpawnDone) {
            lockSetupPhase();
            phase = Phase.RUNNING;
        }

        return true;
    }

    public void lockSetupPhase() {
        player1.lockSetup();
        player2.lockSetup();
    }

    // turn flow
    public void startTurn() {

        if (phase != Phase.RUNNING) return;

        purchasedThisTurn = false;   // reset
        spawnedThisTurn = false;     // reset

        Player p = currentPlayer;

        // increment budget
        p.setBudget(p.getBudget() + config.getTurnBudget());

        // increment turn
//        p.incrementTurn();

        double m = p.getBudget();
//        long t = p.getTurnCount();
        long t = globalTurn;

        // interest calculator
        if (m >= 1 && t >= 1) {

            double b = config.getInterestPct();
            double r = b * Math.log10(m) * Math.log(t);
            double interest = m * r / 100.0;

            p.setBudget(p.getBudget() + interest);
        }

        // cap budget
        if (p.getBudget() > config.getMaxBudget()) {
            p.setBudget(config.getMaxBudget());
        }

    }

    // purchase hex
    public boolean purchaseHex(int r, int c) {

        if (phase != Phase.RUNNING) return false;
        if (purchasedThisTurn) return false;   // buy 1 hex for 1 turn
        if (!board.inBounds(r, c)) return false;

        Player p = currentPlayer;

        if (!isAdjacentToSpawnable(p, r, c)) return false;
        if (!board.isEmpty(r, c)) return false;

        long cost = config.getHexPurchaseCost();
        if (!p.spendBudget(cost)) return false;

        p.addSpawnableHex(r, c);
        purchasedThisTurn = true;    // mark : already bought
        return true;
    }

    // adjacent spawn able
    private boolean isAdjacentToSpawnable(Player p, int r, int c) {

        int[][] dirs;

        if (r % 2 == 0) {
            // EVEN ROW
            dirs = new int[][]{
                    {-1, 0}, {-1, -1},
                    {0, -1}, {0, 1},
                    {1, 0}, {1, -1}
            };
        } else {
            // ODD ROW
            dirs = new int[][]{
                    {-1, 0}, {-1, 1},
                    {0, -1}, {0, 1},
                    {1, 0}, {1, 1}
            };
        }

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (p.isSpawnable(nr, nc)) return true;
        }

        return false;
    }

    // can purchase
    public boolean canPurchase(int r, int c) {

        if (!board.inBounds(r, c)) return false; // must be on the board

        if (board.getMinion(r, c) != null) return false; // must not have minions.

        if (currentPlayer.isSpawnable(r, c)) return false; // must not be spawnable yet

        if (!isAdjacentToSpawnable(currentPlayer, r, c)) return false; // adjacent old spawnable

        return currentPlayer.canPurchaseHex(); // must have enough money
    }


    //minion

    // check spawn permission
    public boolean canSpawnAt(Player p, int r, int c) {

        if (!p.isSpawnable(r, c)) return false;

        if (!p.canSpawn()) return false;

        if (!board.inBounds(r, c)) return false;

        if (board.getMinion(r, c) != null) return false;

        return p.getBudget() >= config.getSpawnCost();
    }

    public boolean canSpawn(int r, int c) {
        return canSpawnAt(currentPlayer, r, c);
    }

    public boolean selectMinionTypes(
            Player player,
            List<String> selectedNames
    ) {
        if (phase != Phase.SETUP) return false;
        if (player == null) return false;

        player.getMinionTypes().clear();

        for (String name : selectedNames) {

            for (MinionType base : baseTypes) {

                if (base.getName().equals(name)) {

                    // clone immutable
                    MinionType copy =
                            base.withConfig(
                                    base.getDefense(),
                                    base.getStrategySource()
                            );

                    player.addMinionType(copy);
                }
            }
        }

        return true;
    }

    public boolean updateMinionType(
            Player player,
            String typeName,
            int newDefense,
            String newStrategy
    ) {
        if (player == null) return false;
        if (phase != Phase.SETUP) return false;

        MinionType oldType = player.getTypeByName(typeName);
        if (oldType == null) return false;

        MinionType newType =
                oldType.withConfig(newDefense, newStrategy);

        player.replaceMinionType(typeName, newType);

        return true;
    }

    public boolean spawnMinion(String typeName, int r, int c) {

        if (phase != Phase.RUNNING) return false;
        if (spawnedThisTurn) return false;   // 1 spawn for 1 turn
        if (!board.inBounds(r, c)) return false;

        Player p = currentPlayer;

        if (currentPlayer.getSpawnsLeft() <= 0) {
            System.out.println("Spawn limit reached. Cannot spawn.");
            return false;
        }

        MinionType type = p.getTypeByName(typeName);
        if (type == null) return false;

        if (!p.canSpawn()) return false;
        if (!p.isSpawnable(r, c)) return false;
        if (!board.isEmpty(r, c)) return false;

        long cost = config.getSpawnCost();
        if (!p.spendBudget(cost)) return false;

        Minion m = new Minion(
                r,
                c,
                (int) config.getInitHp(),
                type,
                p
        );

        board.addMinion(m);
        p.addMinion(m);
        p.incrementSpawnsUsed();

        spawnedThisTurn = true;   // mark already spawn
        return true;
    }

    private void cleanupDeadMinions() {

        removeDeadFromPlayer(player1);
        removeDeadFromPlayer(player2);
    }

    private void removeDeadFromPlayer(Player p) {

        Iterator<Minion> it = p.getMinions().iterator();

        while (it.hasNext()) {
            Minion m = it.next();

            if (!m.isAlive()) {
                board.removeMinion(m);
                it.remove();
            }
        }
    }

    // execution
    public void executeMinions() {
        List<Minion> snapshot =
                new ArrayList<>(currentPlayer.getMinions());

        for (Minion m : snapshot) {

            if (!m.isAlive()) continue;

            evaluator.evaluate(m, this);

            cleanupDeadMinions();

            if (isGameOver()) {
                return;
            }
        }
    }

    // turn
    // use this to btn end turn (gameState.endTurn();)
    public void endTurn() {
        if (isGameOver()) {
            return;
        }
        if (currentPlayer == player2) {
            globalTurn++;
        }
        if (currentPlayer == player2) {
            completedTurns++;
        }

        switchTurn();
    }

    // btn surrender
    public void surrender() {

        if (phase == Phase.FINISHED) return;

        Player loser = currentPlayer;
        Player winner = (loser == player1) ? player2 : player1;

        gameOverReason = "Player " + loser.getId() + " surrendered. Player "
                + winner.getId() + " wins.";

        phase = Phase.FINISHED;
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == player1)
                ? player2
                : player1;
    }

    // end game
    public boolean isGameOver() {

        if (phase == Phase.FINISHED) return true;

        // one player has no minions
        if (player1.hasNoMinions() && !player2.hasNoMinions()) {
            gameOverReason = "Player 1 has no minions left.";
            phase = Phase.FINISHED;
            return true;
        }

        if (player2.hasNoMinions() && !player1.hasNoMinions()) {
            gameOverReason = "Player 2 has no minions left.";
            phase = Phase.FINISHED;
            return true;
        }

        // both reached max turns globalTurn
//        if (player1.getTurnCount() >= config.getMaxTurns()
//                && player2.getTurnCount() >= config.getMaxTurns()) {
        if (completedTurns  >= config.getMaxTurns() ){
            phase = Phase.FINISHED;

            int m1 = player1.getMinions().size();
            int m2 = player2.getMinions().size();

            if (m1 > m2) {
                gameOverReason = "Maximum turns reached. Player 1 has more minions.";
            } else if (m2 > m1) {
                gameOverReason = "Maximum turns reached. Player 2 has more minions.";
            } else {
                long hp1 = player1.sumHP();
                long hp2 = player2.sumHP();

                if (hp1 > hp2) {
                    gameOverReason = "Maximum turns reached. Equal minions. Player 1 has higher total HP.";
                } else if (hp2 > hp1) {
                    gameOverReason = "Maximum turns reached. Equal minions. Player 2 has higher total HP.";
                } else {
                    if (player1.getBudget() > player2.getBudget()) {
                        gameOverReason = "Maximum turns reached. Equal HP. Player 1 has higher budget.";
                    } else if (player2.getBudget() > player1.getBudget()) {
                        gameOverReason = "Maximum turns reached. Equal HP. Player 2 has higher budget.";
                    } else {
                        gameOverReason = "Maximum turns reached. Completely tied.";
                    }
                }
            }

            return true;
        }


        return false;
    }

    public Player determineWinner() {

        // if one side eliminated
        if (player1.hasNoMinions() && !player2.hasNoMinions())
            return player2;

        if (player2.hasNoMinions() && !player1.hasNoMinions())
            return player1;

        // compare number of minions
        int m1 = player1.getMinions().size();
        int m2 = player2.getMinions().size();

        if (m1 != m2) {
            return (m1 > m2) ? player1 : player2;
        }

        // compare total HP
        long hp1 = player1.sumHP();
        long hp2 = player2.sumHP();

        if (hp1 != hp2) {
            return (hp1 > hp2) ? player1 : player2;
        }

        // compare remaining budget
        if (player1.getBudget() != player2.getBudget()) {
            return (player1.getBudget() > player2.getBudget())
                    ? player1
                    : player2;
        }

        return null; // tie
    }

    // bot
    private void botTurnAction() {

        Player p = currentPlayer;

        int mapSize = p.getSpawnableHexes().size();
        double budget = p.getBudget();
        double maxBudget = config.getMaxBudget();

        boolean smallMap = mapSize < 8;
        boolean aggressive = budget >= maxBudget * 0.8;
        boolean enemyNearby = hasEnemyNearby(p);

        // close opponent -> spawn
        if (enemyNearby) {
            if (trySpawn(p)) return;
        }

        // budget > 80% -> aggressive mode (spawn minion and buy)
        if (aggressive) {
            if (trySpawn(p)) return;
            if (tryPurchase(p)) return;
            return;
        }

        // small map -> buy hex
        if (smallMap) {
            if (tryPurchase(p)) return;
            if (trySpawn(p)) return;
            return;
        }

        // normal -> 50/50
        boolean spawnFirst = random.nextBoolean();

        if (spawnFirst) {
            if (trySpawn(p)) return;
            if (tryPurchase(p)) return;
        } else {
            if (tryPurchase(p)) return;
            if (trySpawn(p)) return;
        }
    }

    private boolean hasEnemyNearby(Player p) {

        Player enemy = (p == player1) ? player2 : player1;

        int[][] dirs = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1},
                {-1, 1}, {1, -1}
        };

        for (String hex : p.getSpawnableHexes()) {

            String[] parts = hex.split(",");
            int r = Integer.parseInt(parts[0]);
            int c = Integer.parseInt(parts[1]);

            for (int[] d : dirs) {

                int nr = r + d[0];
                int nc = c + d[1];

                if (!board.inBounds(nr, nc)) continue;

                Minion m = board.getMinion(nr, nc);

                if (m != null && m.getOwner() == enemy) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean trySpawn(Player p) {

        if (!p.canSpawn()) return false;

        if (p.getMinionTypes().isEmpty()) return false;
        if (p.getBudget() < config.getSpawnCost()) return false;

        double budget = p.getBudget();
        double maxBudget = config.getMaxBudget();
        boolean enemyNearby = hasEnemyNearby(p);
        int mapSize = p.getSpawnableHexes().size();

        // create weight list
        List<MinionType> types = p.getMinionTypes();
        List<Integer> weights = new ArrayList<>();

        for (MinionType t : types) {

            int weight = 10; // base weight

            String name = t.getName();

            // have many budget -> tank
            if (budget >= maxBudget * 0.8 && name.equalsIgnoreCase("Tank")) {
                weight += 30;
            }

            // closer opponet -> warrior
            if (enemyNearby && name.equalsIgnoreCase("Warrior")) {
                weight += 25;
            }

            // large map -> Scout
            if (mapSize > 12 && name.equalsIgnoreCase("Scout")) {
                weight += 20;
            }

            weights.add(weight);
        }

        // select weighted random
        int totalWeight = 0;
        for (int w : weights) totalWeight += w;

        int r = random.nextInt(totalWeight);

        int chosenIndex = 0;
        int cumulative = 0;

        for (int i = 0; i < weights.size(); i++) {
            cumulative += weights.get(i);
            if (r < cumulative) {
                chosenIndex = i;
                break;
            }
        }

        String typeName = types.get(chosenIndex).getName();

        // random position
        List<String> hexes = new ArrayList<>(p.getSpawnableHexes());
        java.util.Collections.shuffle(hexes);

        for (String hex : hexes) {

            String[] parts = hex.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            if (board.isEmpty(row, col)) {
                return spawnMinion(typeName, row, col);
            }
        }

        return false;
    }

    private boolean tryPurchase(Player p) {

        if (!p.canPurchaseHex()) return false;

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {

                if (canPurchase(r, c)) {
                    return purchaseHex(r, c);
                }
            }
        }

        return false;
    }

    public void botInitialSpawn() {

        Player p = getCurrentPlayer();

        // random type
        List<MinionType> types = p.getMinionTypes();
        MinionType type = types.get(new Random().nextInt(types.size()));

        // find spawnable hex must be free
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                if (canSpawnInitial(r, c)) {
                    initialSpawn(type.getName(), r, c);
                    return;
                }
            }
        }
    }

    public boolean canSpawnInitial(int r, int c) {

        if (phase != Phase.SETUP) return false;

        if (!board.inBounds(r, c)) return false;

        Player p = currentPlayer;

        // be spawnable zone of player
        if (!p.isSpawnable(r, c)) return false;

        // must be free
        if (!board.isEmpty(r, c)) return false;

        return true;
    }

    // getter UI
    public TurnSummary playTurn() {

        TurnSummary summary = new TurnSummary();

        startTurn();

        if (currentPlayer.isBot()) {
            botTurnAction();
        }

        executeMinions();

        if (phase != Phase.FINISHED) {
            endTurn();
        }

        return summary;
    }
}