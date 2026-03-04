package game;

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

    private final GameConfig config;
    private final Player player1;
    private final Player player2;
    private final Grid board;
    private final GameMode mode;
    private List<MinionType> baseTypes;

    private final Random random = new Random();

    private final StrategyEvaluator evaluator;
    private Player currentPlayer;
    private Phase phase = Phase.SETUP;

    private boolean p1InitialSpawnDone = false;
    private boolean p2InitialSpawnDone = false;

    public GameState(GameConfig config, GameMode mode) {
        if (config == null || mode == null)
            throw new IllegalArgumentException("Config and mode must not be null");

        this.config = config;
        this.mode = mode;

        this.player1 = new Player(config);
        this.player2 = new Player(config);

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

    public GameConfig getConfig() {
        return config;
    }

    // spawn zone
    private void initSpawnZones() {
        // player1 top-left
        player1.addSpawnableHex(1,1);
        player1.addSpawnableHex(1,2);
        player1.addSpawnableHex(2,1);
        player1.addSpawnableHex(2,2);
        player1.addSpawnableHex(1,3);

        // player2 bottom-right
        player2.addSpawnableHex(8,8);
        player2.addSpawnableHex(8,7);
        player2.addSpawnableHex(7,8);
        player2.addSpawnableHex(7,7);
        player2.addSpawnableHex(8,6);
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
        if (!p.isSpawnable(r,c)) return false;
        if (!board.isEmpty(r,c)) return false;

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
            currentPlayer = player1;
        }

        return true;
    }

    public void lockSetupPhase() {
        player1.lockSetup();
        player2.lockSetup();
    }

    // TURN FLOW
    public void startTurn() {

        if (phase != Phase.RUNNING) return;

        Player p = currentPlayer;

        // เพิ่มงบก่อน
        p.setBudget(p.getBudget() + config.getTurnBudget());

        double m = p.getBudget();
        long t = p.getTurnCount() + 1;   // ใช้ turn ใหม่

        // คิดดอกเบี้ย
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

        // increment turn
        p.incrementTurn();
    }

    // purchasw hex
    public boolean purchaseHex(int r, int c) {

        if (phase != Phase.RUNNING) return false;
        if (!board.inBounds(r, c)) return false;

        Player p = currentPlayer;

        if (!isAdjacentToSpawnable(p, r, c)) return false;
        if (!board.isEmpty(r, c)) return false;

        long cost = config.getHexPurchaseCost();
        if (!p.spendBudget(cost)) return false;

        p.addSpawnableHex(r, c);
        return true;
    }

    private boolean isAdjacentToSpawnable(Player p, int r, int c) {

        int[][] dirs = {
                {-1,0},{1,0},{0,-1},{0,1},{-1,1},{1,-1}
        };

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];
            if (p.isSpawnable(nr,nc)) return true;
        }

        return false;
    }

    public boolean canPurchase(int r, int c) {

        // ต้องอยู่ใน board
        if (!board.inBounds(r, c)) return false;

        // ต้องไม่มี minion
        if (board.getMinion(r, c) != null) return false;

        // ต้องยังไม่เป็น spawnable
        if (currentPlayer.isSpawnable(r, c)) return false;

        // ต้องติดกับ spawnable เดิม
        if (!isAdjacentToSpawnable(r, c)) return false;

        // ต้องมีเงินพอ
        return currentPlayer.canPurchaseHex();
    }

    private boolean isAdjacentToSpawnable(int r, int c) {

        int[][] dirs = {
                {-1, 0}, {1, 0},
                {0, -1}, {0, 1},
                {-1, 1}, {1, -1}   // hex skew
        };

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];

            if (currentPlayer.isSpawnable(nr, nc)) {
                return true;
            }
        }

        return false;
    }

    //minion
    public boolean canSpawn(int r, int c) {
        return board.inBounds(r, c)
                && board.getMinion(r, c) == null
                && currentPlayer.canSpawnAt(r, c);
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
        if (!board.inBounds(r, c)) return false;

        Player p = currentPlayer;

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

        return true;
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
                phase = Phase.FINISHED;
                return;
            }
        }
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

    public void endTurn() {
        if (isGameOver()) {
            phase = Phase.FINISHED;
            return;
        }

        switchTurn();
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
        if (player1.hasNoMinions()) return true;
        if (player2.hasNoMinions()) return true;

        // both reached max turns
        if (player1.getTurnCount() >= config.getMaxTurns()
                && player2.getTurnCount() >= config.getMaxTurns()) {
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

    // UI getters
    public TurnSummary playTurn() {

        TurnSummary summary = new TurnSummary();

        startTurn();

        if (currentPlayer.isBot()) {
            botTurnAction();
        }

        // human → UI เรียก purchaseHex / spawnMinion เอง

        executeMinions();

        if (phase != Phase.FINISHED) {
            endTurn();
        }

        return summary;
    }
    public List<MinionType> getBaseTypes() {
        return baseTypes;
    }

    public Phase getPhase() {
        return phase;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Grid getBoard() {
        return board;
    }

    public void runGame() {

        while (!isGameOver()) {

            startTurn();

            if (currentPlayer.isBot()) {
                botTurnAction();
            }

            executeMinions();

            if (phase != Phase.FINISHED) {
                endTurn();
            }
        }
    }

//    private void botTurnAction() {
//
//        Player p = currentPlayer;
//
//        // พยายาม spawn ก่อน
//        if (!p.getMinionTypes().isEmpty()) {
//
//            for (String hex : p.getSpawnableHexes()) {
//
//                String[] parts = hex.split(",");
//                int r = Integer.parseInt(parts[0]);
//                int c = Integer.parseInt(parts[1]);
//
//                if (board.isEmpty(r, c) &&
//                        p.getBudget() >= config.getSpawnCost()) {
//
//                    String typeName =
//                            p.getMinionTypes().get(0).getName();
//
//                    spawnMinion(typeName, r, c);
//                    return;
//                }
//            }
//        }
//
//        // ถ้า spawn ไม่ได้ ค่อยลองซื้อ
//        if (p.canPurchaseHex()) {
//
//            for (int r = 1; r <= 8; r++) {
//                for (int c = 1; c <= 8; c++) {
//
//                    if (canPurchase(r, c)) {
//                        purchaseHex(r, c);
//                        return;
//                    }
//                }
//            }
//        }
//    }

    private void botTurnAction() {

        Player p = currentPlayer;

        int mapSize = p.getSpawnableHexes().size();
        double budget = p.getBudget();
        double maxBudget = config.getMaxBudget();

        boolean smallMap = mapSize < 8;
        boolean aggressive = budget >= maxBudget * 0.8;
        boolean enemyNearby = hasEnemyNearby(p);

        // 🔥 ศัตรูอยู่ใกล้ → รีบ spawn
        if (enemyNearby) {
            if (trySpawn(p)) return;
        }

        // 💰 เงินเกิน 80% → aggressive mode
        if (aggressive) {
            if (trySpawn(p)) return;
            if (tryPurchase(p)) return;
            return;
        }

        // 🗺️ map ยังเล็ก → ซื้อก่อน
        if (smallMap) {
            if (tryPurchase(p)) return;
            if (trySpawn(p)) return;
            return;
        }

        // ⚖️ ปกติ → 50/50
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
                {-1,0},{1,0},
                {0,-1},{0,1},
                {-1,1},{1,-1}
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

        if (p.getMinionTypes().isEmpty()) return false;
        if (p.getBudget() < config.getSpawnCost()) return false;

        double budget = p.getBudget();
        double maxBudget = config.getMaxBudget();
        boolean enemyNearby = hasEnemyNearby(p);
        int mapSize = p.getSpawnableHexes().size();

        // สร้าง weight list
        List<MinionType> types = p.getMinionTypes();
        List<Integer> weights = new ArrayList<>();

        for (MinionType t : types) {

            int weight = 10; // base weight

            String name = t.getName();

            // เงินเยอะ → Tank โอกาสสูง
            if (budget >= maxBudget * 0.8 && name.equalsIgnoreCase("Tank")) {
                weight += 30;
            }

            // ศัตรูใกล้ → Warrior โอกาสสูง
            if (enemyNearby && name.equalsIgnoreCase("Warrior")) {
                weight += 25;
            }

            // map ใหญ่ → Scout โอกาสสูง
            if (mapSize > 12 && name.equalsIgnoreCase("Scout")) {
                weight += 20;
            }

            weights.add(weight);
        }

        // เลือกแบบ weighted random
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

        // สุ่มตำแหน่ง
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

        // เลือก type แบบสุ่ม
        List<MinionType> types = p.getMinionTypes();
        MinionType type = types.get(new Random().nextInt(types.size()));

        // หา spawnable hex ว่าง
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

        // ต้องเป็น spawnable zone ของ player
        if (!p.isSpawnable(r, c)) return false;

        // ต้องว่าง
        if (!board.isEmpty(r, c)) return false;

        return true;
    }

}