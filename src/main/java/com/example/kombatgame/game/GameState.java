package com.example.kombatgame.game;

import com.example.kombatgame.dto.TurnLogsDTO;
import com.example.kombatgame.strategy.eval.ActionLog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameState {

    public enum Phase {
        SETUP,
        RUNNING,
        FINISHED
    }

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

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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

    @Getter
    private final TurnLogsDTO turnLogs = new TurnLogsDTO();

    public GameState(GameConfig config, GameMode mode) {
        if (config == null || mode == null)
            throw new IllegalArgumentException("Config and mode must not be null");

        this.config = config;
        this.mode = mode;

        this.player1 = new Player(1, config);
        this.player2 = new Player(2, config);

        configureMode();
        initBaseTypes();

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

    // spawn zone - format is (col, row)
    private void initSpawnZones() {
        // player1 top-left
        player1.addSpawnableHex(0, 0);
        player1.addSpawnableHex(1, 0);
        player1.addSpawnableHex(2, 0);
        player1.addSpawnableHex(0, 1);
        player1.addSpawnableHex(1, 1);

        // player2 bottom-right
        player2.addSpawnableHex(5, 7);
        player2.addSpawnableHex(6, 7);
        player2.addSpawnableHex(7, 7);
        player2.addSpawnableHex(6, 6);
        player2.addSpawnableHex(7, 6);
    }

    // default minion
    private void initBaseTypes() {
        baseTypes = new ArrayList<>();

        // Archer - Ranged attacks, variable damage based on distance
        baseTypes.add(new MinionType(
                "Archer",
                2,
                """
                t = t + 1
                r = 0
                
                while (3 - r) {
                
                  op = opponent
                
                  if (op / 10 - 1)
                  then {
                
                    if (op % 10 - 1) then {} else { move up done }
                    if (op % 10 - 2) then {} else { move upright done }
                    if (op % 10 - 3) then {} else { move downright done }
                    if (op % 10 - 4) then {} else { move down done }
                    if (op % 10 - 5) then {} else { move downleft done }
                    move upleft done
                  }
                
                  else if (op)
                  then {
                
                    if (op % 10 - 1) then {} else {
                      if (nearby up)
                      then {
                        cost = 10 ^ (nearby up % 100 + 1)
                        if (Budget - cost) then shoot up cost else shoot up 10
                      }
                      else { move up }
                      done
                    }
                
                    if (op % 10 - 2) then {} else {
                      if (nearby upright)
                      then {
                        cost = 10 ^ (nearby upright % 100 + 1)
                        if (Budget - cost) then shoot upright cost else shoot upright 10
                      }
                      else { move upright }
                      done
                    }
                
                    if (op % 10 - 3) then {} else {
                      if (nearby downright)
                      then {
                        cost = 10 ^ (nearby downright % 100 + 1)
                        if (Budget - cost) then shoot downright cost else shoot downright 10
                      }
                      else { move downright }
                      done
                    }
                
                    if (op % 10 - 4) then {} else {
                      if (nearby down)
                      then {
                        cost = 10 ^ (nearby down % 100 + 1)
                        if (Budget - cost) then shoot down cost else shoot down 10
                      }
                      else { move down }
                      done
                    }
                
                    if (op % 10 - 5) then {} else {
                      if (nearby downleft)
                      then {
                        cost = 10 ^ (nearby downleft % 100 + 1)
                        if (Budget - cost) then shoot downleft cost else shoot downleft 10
                      }
                      else { move downleft }
                      done
                    }
                
                    if (nearby upleft)
                    then {
                      cost = 10 ^ (nearby upleft % 100 + 1)
                      if (Budget - cost) then shoot upleft cost else shoot upleft 10
                    }
                    else { move upleft }
                    done
                  }
                
                  else {
                
                        try = 0
                
                    while (3 - try) {
                
                      dir = random % 6
                
                      if ((dir - 1) * (nearby up % 10 + 1) ^ 2) then {} else { move up done }
                      if ((dir - 2) * (nearby upright % 10 + 1) ^ 2) then {} else { move upright done }
                      if ((dir - 3) * (nearby downright % 10 + 1) ^ 2) then {} else { move downright done }
                      if ((dir - 4) * (nearby down % 10 + 1) ^ 2) then {} else { move down done }
                      if ((dir - 5) * (nearby downleft % 10 + 1) ^ 2) then {} else { move downleft done }
                      if ((nearby upleft % 10 + 1) ^ 2) then { move upleft done } else {}
                
                      try = try + 1
                    }
                    r = r + 1
                  }
                }
                """
        ));

        // Guardian - High defense, shoots cost 15
        baseTypes.add(new MinionType(
                "Guardian",
                8,
                """
                        
                t = t + 1
                r = 0
                
                while (3 - r) {
                        
                  op = opponent
                
                  if (op / 10 - 1)
                  then {
                
                    if (op % 10 - 1) then {} else { move up done }
                    if (op % 10 - 2) then {} else { move upright done }
                    if (op % 10 - 3) then {} else { move downright done }
                    if (op % 10 - 4) then {} else { move down done }
                    if (op % 10 - 5) then {} else { move downleft done }
                    move upleft done
                  }
                
                  else if (op)
                  then {
                
                    if (op % 10 - 1) then {} else { if (Budget - 15) then shoot up 15 else move up done }
                    if (op % 10 - 2) then {} else { if (Budget - 15) then shoot upright 15 else move upright done }
                    if (op % 10 - 3) then {} else { if (Budget - 15) then shoot downright 15 else move downright done }
                    if (op % 10 - 4) then {} else { if (Budget - 15) then shoot down 15 else move down done }
                    if (op % 10 - 5) then {} else { if (Budget - 15) then shoot downleft 15 else move downleft done }
                
                    if (Budget - 15) then shoot upleft 15 else move upleft done
                  }
                
                  else {
                
                        try = 0
                
                    while (3 - try) {
                
                      dir = random % 6
                
                      if (dir - 1) then {} else { move up done }
                      if (dir - 2) then {} else { move upright done }
                      if (dir - 3) then {} else { move downright done }
                      if (dir - 4) then {} else { move down done }
                      if (dir - 5) then {} else { move downleft done }
                      move upleft done
                
                      try = try + 1
                    }
                
                    r = r + 1
                  }
                }
                """
        ));

        // Warrior - Aggressive, shoots cost 30
        baseTypes.add(new MinionType(
                "Warrior",
                5,
                """
                t = t + 1
                
                while (1) {
                
                  op = opponent
                
                  if (op / 10 - 1)
                  then {
                
                    if (op % 10 - 1) then {} else { move up done }
                    if (op % 10 - 2) then {} else { move upright done }
                    if (op % 10 - 3) then {} else { move downright done }
                    if (op % 10 - 4) then {} else { move down done }
                    if (op % 10 - 5) then {} else { move downleft done }
                    move upleft done
                  }
                
                   else if (op)
                  then {
                
                    if (op % 10 - 1) then {} else {
                      if (nearby up)
                      then { if (Budget - 30) then shoot up 30 else move up }
                      else { move up }
                      done
                    }
                
                    if (op % 10 - 2) then {} else {
                      if (nearby upright)
                      then { if (Budget - 30) then shoot upright 30 else move upright }
                      else { move upright }
                      done
                    }
                
                    if (op % 10 - 3) then {} else {
                      if (nearby downright)
                      then { if (Budget - 30) then shoot downright 30 else move downright }
                      else { move downright }
                      done
                    }
                
                    if (op % 10 - 4) then {} else {
                      if (nearby down)
                      then { if (Budget - 30) then shoot down 30 else move down }
                      else { move down }
                      done
                    }
                
                    if (op % 10 - 5) then {} else {
                      if (nearby downleft)
                      then { if (Budget - 30) then shoot downleft 30 else move downleft }
                      else { move downleft }
                      done
                    }
                
                    if (nearby upleft)
                    then { if (Budget - 30) then shoot upleft 30 else move upleft }
                    else { move upleft }
                    done
                  }
                
                   else {
                
                    try = 0
                
                    while (3 - try) {
                
                      dir = random % 6
                
                      if (dir - 1) then {} else { move up done }
                      if (dir - 2) then {} else { move upright done }
                      if (dir - 3) then {} else { move downright done }
                      if (dir - 4) then {} else { move down done }
                      if (dir - 5) then {} else { move downleft done }
                      move upleft done
                
                      try = try + 1
                    }
                  }
                }
                """
        ));

        // Scout - Fast movement, avoids enemies
        baseTypes.add(new MinionType(
                "Scout",
                1,
                """
                m = 0
                
                while (3 - m) {
                
                  op = opponent
                
                  if (op)
                  then {
                
                    if (op % 10 - 1) then {} else { move up done }
                    if (op % 10 - 2) then {} else { move upright done }
                    if (op % 10 - 3) then {} else { move downright done }
                    if (op % 10 - 4) then {} else { move down done }
                    if (op % 10 - 5) then {} else { move downleft done }
                    move upleft done
                  }
                
                  else {
                
                    try = 0
                
                    while (3 - try) {
                
                      dir = random % 6
                
                      if (dir - 1) then {} else { move up done }
                      if (dir - 2) then {} else { move upright done }
                      if (dir - 3) then {} else { move downright done }
                      if (dir - 4) then {} else { move down done }
                      if (dir - 5) then {} else { move downleft done }
                      move upleft done
                
                      try = try + 1
                    }
                  }
                
                  m = m + 1
                }
                """
        ));

        // Mage - Balanced, random movement + shoots cost 10
        baseTypes.add(new MinionType(
                "Mage",
                3,
                """
                t = t + 1
                
                while (1) {
                
                  op = opponent
                
                  if (op)
                  then {
                
                    if (op % 10 - 1) then {} else { if (Budget - 10) then shoot up 10 else move up done }
                    if (op % 10 - 2) then {} else { if (Budget - 10) then shoot upright 10 else move upright done }
                    if (op % 10 - 3) then {} else { if (Budget - 10) then shoot downright 10 else move downright done }
                    if (op % 10 - 4) then {} else { if (Budget - 10) then shoot down 10 else move down done }
                    if (op % 10 - 5) then {} else { if (Budget - 10) then shoot downleft 10 else move downleft done }
                
                    if (Budget - 10) then shoot upleft 10 else move upleft done
                  }
                
                   else {
                
                    try = 0
                
                    while (3 - try) {
                
                      dir = random % 6
                
                      if (dir - 1) then {} else { move up done }
                      if (dir - 2) then {} else { move upright done }
                      if (dir - 3) then {} else { move downright done }
                      if (dir - 4) then {} else { move down done }
                      if (dir - 5) then {} else { move downleft done }
                      move upleft done
                
                      try = try + 1
                    }
                  }
                }
                """
        ));
    }

    // setup phase
    // coordinates are (col, row) with 0-indexed values
    public String getInitialSpawnError(String typeName, int c, int r) {
        return getInitialSpawnErrorForPlayer(typeName, c, r, currentPlayer);
    }

    public String getInitialSpawnErrorForPlayer(String typeName, int c, int r, Player player) {
        if (phase != Phase.SETUP) {
            return "Invalid phase: " + phase + " (expected SETUP)";
        }

        Player p = player;
        if (p == null) {
            return "No player specified";
        }

        System.out.println("[initialSpawn] player=" + p.getId() + ", type=" + typeName + ", pos=" + c + "," + r);
        System.out.println("[initialSpawn] Player available types: " +
                p.getMinionTypes().stream().map(t -> t.getName()).toList());

        MinionType type = p.getTypeByName(typeName);
        if (type == null) {
            return "Type '" + typeName + "' not found. Available: " + p.getMinionTypes().stream().map(MinionType::getName).toList();
        }
        if (!p.isSpawnable(c, r)) {
            return "Hex (" + c + "," + r + ") not spawnable for player " + p.getId() + ". Spawnable: " + p.getSpawnableHexes();
        }
        if (!board.isEmpty(r, c)) {
            return "Hex (" + c + "," + r + ") already occupied";
        }

        return null;
    }

    public boolean initialSpawn(String typeName, int c, int r) {
        return initialSpawnForPlayer(typeName, c, r, currentPlayer);
    }


    public boolean initialSpawnForPlayer(String typeName, int c, int r, Player targetPlayer) {
        String error = getInitialSpawnErrorForPlayer(typeName, c, r, targetPlayer);
        if (error != null) {
            System.out.println("[initialSpawn] FAILED: " + error);
            return false;
        }

        Player p = targetPlayer;
        MinionType type = p.getTypeByName(typeName);

        Minion m = new Minion(r, c, (int) config.getInitHp(), type, p, p.getNextMinionNumber());

        board.addMinion(m);
        p.addMinion(m);
        turnLogs.addLog("P" + p.getId() + " spawned initial minion#" + m.getMinionNumber() + " at (" + c + "," + r + ")");

        if (p == player1) p1InitialSpawnDone = true;
        else p2InitialSpawnDone = true;

        // ใน Duel Mode หากวางเสร็จแล้วให้สลับเทิร์นเพื่อให้คนถัดไปวาง
        switchTurn();

        if (p1InitialSpawnDone && p2InitialSpawnDone) {
            lockSetupPhase();
            this.phase = Phase.RUNNING;
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
        turnLogs.addLog("P" + p.getId() + " +" + config.getTurnBudget() + " budget");

        double m = p.getBudget();
        long t = globalTurn;

        // interest calculator
        if (m >= 1 && t >= 2) {

            double b = config.getInterestPct();
            double r = b * Math.log10(m) * Math.log(t);
            double interest = m * r / 100.0;

            p.setBudget(p.getBudget() + interest);
            if (interest > 0) {
                turnLogs.addLog("P" + p.getId() + " +" + (int)interest + " interest");
            }
        }

        // cap budget
        if (p.getBudget() > config.getMaxBudget()) {
            p.setBudget(config.getMaxBudget());
        }

    }

    // เพิ่ม Helper Method เพื่อเช็คว่าช่องนี้มีใครจองหรือยัง (รวมทั้ง 2 ฝั่ง)
    private boolean isHexOwnedByAnyone(int c, int r) {
        return player1.isSpawnable(c, r) || player2.isSpawnable(c, r);
    }

    // purchase hex - coordinates are (col, row) with 0-indexed values
    public boolean purchaseHex(int c, int r) {

        if (phase != Phase.RUNNING) return false;
        if (purchasedThisTurn) return false;
        if (!board.inBounds(r, c)) return false;

        Player p = currentPlayer;

        if (isHexOwnedByAnyone(c, r)) return false; // ห้ามซื้อทับhexที่มีเจ้าของแล้ว
        if (!isAdjacentToSpawnable(p, c, r)) return false;
        if (!board.isEmpty(r, c)) return false;

        long cost = config.getHexPurchaseCost();
        if (!p.spendBudget(cost)) return false;

        p.addSpawnableHex(c, r);
        purchasedThisTurn = true;
        turnLogs.addLog("P" + p.getId() + " purchased hex at (" + c + "," + r + ")");
        return true;
    }

    public String getPurchaseError(int c, int r) {
        if (phase != Phase.RUNNING) return "Game is not running";
        if (purchasedThisTurn) return "Already purchased a hex this turn";
        if (!board.inBounds(r, c)) return "Hex is out of bounds";

        Player p = currentPlayer;
        if (!isAdjacentToSpawnable(p, c, r)) return "Hex is not adjacent to your territory";
        if (!board.isEmpty(r, c)) return "Hex is already occupied";

        long cost = config.getHexPurchaseCost();
        if (!p.spendBudget(cost)) return "Not enough gold (need " + cost + ")";

        return "Unknown error";
    }

    // adjacent spawn able - coordinates are (col, row)
    // Even-q offset: even columns are pushed down
    private boolean isAdjacentToSpawnable(Player p, int c, int r) {

        int[] dc, dr;

        // Your custom hex grid
        // Directions: E, W, N, S, NW, SE
        if (c % 2 == 0) {
            // EVEN COLUMN
            dc = new int[]{+1, -1, 0, 0, -1, +1};
            dr = new int[]{0, 0, -1, +1, +1, +1};
        } else {
            // ODD COLUMN
            dc = new int[]{+1, -1, 0, 0, -1, +1};
            dr = new int[]{0, 0, -1, +1, -1, -1};
        }

        for (int i = 0; i < 6; i++) {
            int nc = c + dc[i];
            int nr = r + dr[i];

            // ตรวจสอบ InBounds (row, col) ก่อนเสมอ
            if (board.inBounds(nr, nc)) {
                // เช็คว่าพิกัดนั้นเป็นของผู้เล่นคนนี้จริงๆ
                if (p.isSpawnable(nc, nr)) return true;
            }
        }
        return false;
    }



    // can purchase - coordinates are (col, row)
    public boolean canPurchase(int c, int r) {

        if (!board.inBounds(r, c)) return false;

        if (board.getMinion(r, c) != null) return false;

        // ห้ามซื้อถ้ามีเจ้าของแล้ว (เช็คทั้ง p1 และ p2)
        if (isHexOwnedByAnyone(c, r)) return false;

        if (!isAdjacentToSpawnable(currentPlayer, c, r)) return false;

        return currentPlayer.canPurchaseHex();
    }


    //minion

    // check spawn permission - coordinates are (col, row)
    public boolean canSpawnAt(Player p, int c, int r) {

        if (!p.isSpawnable(c, r)) return false;

        if (!p.canSpawn()) return false;

        if (!board.inBounds(r, c)) return false;

        if (board.getMinion(r, c) != null) return false;

        return p.getBudget() >= config.getSpawnCost();
    }

    public boolean canSpawn(int c, int r) {
        return canSpawnAt(currentPlayer, c, r);
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

    public boolean syncMinionTypesFromHost(List<String> selectedNames) {
        if (phase != Phase.SETUP) return false;
        selectMinionTypes(player1, selectedNames);
        selectMinionTypes(player2, selectedNames);
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

        MinionType newType = oldType.withConfig(newDefense, newStrategy);

        player.replaceMinionType(typeName, newType);

        return true;
    }

    // coordinates are (col, row)
    public boolean spawnMinion(String typeName, int c, int r) {

        if (phase != Phase.RUNNING) return false;
        if (spawnedThisTurn) return false;
        if (!board.inBounds(r, c)) return false;

        Player p = currentPlayer;

        if (currentPlayer.getSpawnsLeft() <= 0) {
            System.out.println("Spawn limit reached. Cannot spawn.");
            return false;
        }

        MinionType type = p.getTypeByName(typeName);
        if (type == null) return false;

        if (!p.canSpawn()) return false;
        if (!p.isSpawnable(c, r)) return false;
        if (!board.isEmpty(r, c)) return false;

        long cost = config.getSpawnCost();
        if (!p.spendBudget(cost)) return false;

        Minion m = new Minion(
                r,
                c,
                (int) config.getInitHp(),
                type,
                p,
                p.getNextMinionNumber()
        );

        board.addMinion(m);
        p.addMinion(m);
        p.incrementSpawnsUsed();
        turnLogs.addLog("P" + p.getId() + " spawned minion#" + m.getMinionNumber() + " at (" + c + "," + r + ")");

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
                turnLogs.addLog("P" + p.getId() + " minion#" + m.getMinionNumber() + " died");
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

            var result = evaluator.evaluate(m, this);

            for (ActionLog log : result.getActions()) {
                String desc = "P" + currentPlayer.getId() + " minion#" +
                        m.getMinionNumber() + " " +
                        log.getType() + " " +
                        log.getDirection();
                if (log.getValue() > 0) {
                    desc += " " + log.getValue();
                }
                turnLogs.addLog(desc);

                if (log.getType() == ActionLog.Type.SHOOT && log.hasCombatResult()) {
                    String combatLog = "  -> Hit P" + log.getTargetPlayerId() + " minion#" +
                            log.getTargetMinionNumber() + " for " +
                            log.getDamageDealt() + " dmg (HP: " +
                            log.getTargetHpAfter() + ")";
                    if (log.isKilled()) {
                        combatLog += " [KILLED]";
                    }
                    turnLogs.addLog(combatLog);
                }
            }

            if (isGameOver()) {
                cleanupDeadMinions();
                return;
            }
        }

        cleanupDeadMinions();
    }

    // Round-Based Turn System:
    // - A "round" = Player 1's turn + Player 2's turn
    // - globalTurn represents the current round number (starts at 1)
    // - completedTurns tracks finished rounds (for max turn limit)
    // - Turn flow: P1 plays → P2 plays → Round complete → globalTurn++
    public void endTurn() {
        if (isGameOver()) {
            return;
        }
        turnLogs.addSeparator();
        if (currentPlayer == player2) {
            globalTurn++;
            completedTurns++;
        }
        switchTurn();
    }

    public int getRoundNumber() {
        return globalTurn;
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

        // Reset purchase/spawn flags for the new current player
        this.purchasedThisTurn = false;
        this.spawnedThisTurn = false;
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

        // Simple 50/50 logic: flip coin to decide primary action
        // Order: 1st buy hex, 2nd spawn minion
        boolean buyHexFirst = random.nextBoolean();

        if (buyHexFirst) {
            // Try buy hex first, then spawn
            if (!tryPurchase(p)) {
                trySpawn(p);
            }
        } else {
            // Try spawn first, then buy hex
            if (!trySpawn(p)) {
                tryPurchase(p);
            }
        }
    }

    private boolean hasEnemyNearby(Player p) {

        Player enemy = (p == player1) ? player2 : player1;

        for (String hex : p.getSpawnableHexes()) {

            String[] parts = hex.split(",");
            int c = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);

            int[] dc, dr;

            // Your custom hex grid
            // Directions: E, W, N, S, NW, SE
            if (c % 2 == 0) {
                // EVEN COLUMN
                dc = new int[]{+1, -1, 0, 0, -1, +1};
                dr = new int[]{0, 0, -1, +1, +1, +1};
            } else {
                // ODD COLUMN
                dc = new int[]{+1, -1, 0, 0, -1, +1};
                dr = new int[]{0, 0, -1, +1, -1, -1};
            }

            for (int i = 0; i < 6; i++) {
                int nc = c + dc[i];
                int nr = r + dr[i];

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

            // have many budget -> Guardian (high defense)
            if (budget >= maxBudget * 0.8 && name.equalsIgnoreCase("Guardian")) {
                weight += 30;
            }

            // closer opponent -> Warrior (aggressive)
            if (enemyNearby && name.equalsIgnoreCase("Warrior")) {
                weight += 25;
            }

            // large map -> Scout (fast movement)
            if (mapSize > 12 && name.equalsIgnoreCase("Scout")) {
                weight += 20;
            }

            // Archer for ranged attacks
            if (enemyNearby && name.equalsIgnoreCase("Archer")) {
                weight += 15;
            }

            // Mage for balanced play
            if (name.equalsIgnoreCase("Mage")) {
                weight += 10;
            }

            weights.add(weight);
        }

        // select weighted random
        int totalWeight = 0;
        for (int w : weights) totalWeight += w;

        int randVal = random.nextInt(totalWeight);

        int chosenIndex = 0;
        int cumulative = 0;

        for (int i = 0; i < weights.size(); i++) {
            cumulative += weights.get(i);
            if (randVal < cumulative) {
                chosenIndex = i;
                break;
            }
        }

        String typeName = types.get(chosenIndex).getName();

        // Find best position: backline toward enemy, avoid stacking
        List<String> candidates = getBacklinePositions(p);

        if (candidates.isEmpty()) {
            // Fallback: any valid spawnable hex that's not adjacent to own minion
            candidates = getUnstackedSpawnableHexes(p);
        }

        if (candidates.isEmpty()) {
            return false;
        }

        // Pick random from best candidates
        String chosenHex = candidates.get(random.nextInt(candidates.size()));
        String[] parts = chosenHex.split(",");
        int col = Integer.parseInt(parts[0]);
        int row = Integer.parseInt(parts[1]);

        return spawnMinion(typeName, col, row);
    }

    // Get spawnable hexes that are behind our frontline (backline), toward enemy
    private List<String> getBacklinePositions(Player p) {
        List<String> result = new ArrayList<>();

        // Find our frontline (hexes with friendly minions that are closest to enemy)
        int[] frontlineDist = findFrontlineDistance(p);

        for (String hex : p.getSpawnableHexes()) {
            String[] parts = hex.split(",");
            int c = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);

            if (!board.isEmpty(r, c)) continue; // Must be empty

            // Check if this hex is NOT adjacent to any friendly minion (backline)
            if (hasAdjacentFriendlyMinion(c, r, p)) continue;

            // Score based on distance to enemy (closer = better for aggressive spawn)
            int distToEnemy = estimateDistanceToEnemy(c, r, p);

            // Only consider hexes that are at or behind the frontline
            if (distToEnemy <= frontlineDist[0] + 2) {
                result.add(hex);
            }
        }

        return result;
    }

    // Get spawnable hexes that are not adjacent to own minions
    private List<String> getUnstackedSpawnableHexes(Player p) {
        List<String> result = new ArrayList<>();

        for (String hex : p.getSpawnableHexes()) {
            String[] parts = hex.split(",");
            int c = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);

            if (!board.isEmpty(r, c)) continue;
            if (hasAdjacentFriendlyMinion(c, r, p)) continue;

            result.add(hex);
        }

        // If still empty, allow any valid spawn
        if (result.isEmpty()) {
            for (String hex : p.getSpawnableHexes()) {
                String[] parts = hex.split(",");
                int c = Integer.parseInt(parts[0]);
                int r = Integer.parseInt(parts[1]);
                if (board.isEmpty(r, c)) {
                    result.add(hex);
                }
            }
        }

        return result;
    }

    // Check if hex has any adjacent friendly minion
    private boolean hasAdjacentFriendlyMinion(int c, int r, Player p) {
        int[] dc = {+1, -1, 0, 0, -1, +1};
        int[] dr;

        if (c % 2 == 0) {
            dr = new int[]{0, 0, -1, +1, +1, +1};
        } else {
            dr = new int[]{0, 0, -1, +1, -1, -1};
        }

        for (int i = 0; i < 6; i++) {
            int nc = c + dc[i];
            int nr = r + dr[i];

            if (!board.inBounds(nr, nc)) continue;

            Minion m = board.getMinion(nr, nc);
            if (m != null && m.getOwner() == p) {
                return true;
            }
        }

        return false;
    }

    // Find the frontline distance (closest friendly minion to enemy)
    private int[] findFrontlineDistance(Player p) {
        int minDist = Integer.MAX_VALUE;

        for (Minion m : p.getMinions()) {
            int dist = estimateDistanceToEnemy(m.getCol(), m.getRow(), p);
            if (dist < minDist) {
                minDist = dist;
            }
        }

        // If no minions, return a large value (spawn anywhere)
        if (minDist == Integer.MAX_VALUE) {
            minDist = 20;
        }

        return new int[]{minDist};
    }

    // Estimate distance from a hex to nearest enemy minion
    private int estimateDistanceToEnemy(int c, int r, Player p) {
        Player enemy = (p == player1) ? player2 : player1;
        int minDist = Integer.MAX_VALUE;

        for (Minion m : enemy.getMinions()) {
            int dist = Math.abs(c - m.getCol()) + Math.abs(r - m.getRow());
            if (dist < minDist) {
                minDist = dist;
            }
        }

        // If no enemies, return large value
        if (minDist == Integer.MAX_VALUE) {
            minDist = 50;
        }

        return minDist;
    }

    private boolean tryPurchase(Player p) {

        if (!p.canPurchaseHex()) {
            return false;
        }

        // Find all adjacent empty hexes and score them
        List<int[]> candidates = new ArrayList<>();

        // 0-indexed: 0-7
        for (int c = 0; c < 8; c++) {
            for (int r = 0; r < 8; r++) {
                if (!board.inBounds(r, c)) continue;
                if (p.isSpawnable(c, r)) continue;  // Already owned
                if (!board.isEmpty(r, c)) continue;  // Has minion
                if (!isAdjacentToSpawnable(p, c, r)) continue;  // Must be adjacent to owned

                // Score this hex
                int score = scorePurchaseHex(c, r, p);
                candidates.add(new int[]{c, r, score});
            }
        }

        if (candidates.isEmpty()) {
            return false;
        }

        // Sort by score (higher = better)
        candidates.sort((a, b) -> Integer.compare(b[2], a[2]));

        // Try to purchase the best hex
        for (int[] candidate : candidates) {
            int c = candidate[0];
            int r = candidate[1];

            if (purchaseHex(c, r)) {
                return true;
            }
        }

        return false;
    }

    // Score a potential purchase hex (higher = better)
    private int scorePurchaseHex(int c, int r, Player p) {
        int score = 0;

        // Distance to center (prefer center for strategic control)
        double centerDist = Math.sqrt(Math.pow(c - 3.5, 2) + Math.pow(r - 3.5, 2));
        score += (int)(10 - centerDist); // Closer to center = higher score

        // Distance to nearest enemy (prefer expanding toward enemy)
        Player enemy = (p == player1) ? player2 : player1;
        int minEnemyDist = Integer.MAX_VALUE;
        for (Minion m : enemy.getMinions()) {
            int dist = Math.abs(c - m.getCol()) + Math.abs(r - m.getRow());
            minEnemyDist = Math.min(minEnemyDist, dist);
        }
        if (minEnemyDist < Integer.MAX_VALUE) {
            score += (10 - minEnemyDist); // Closer to enemy = higher score
        }

        // Bonus for flanking enemy spawn area
        if (enemy.getSpawnableHexes().isEmpty()) {
            // Enemy has no more spawnable hexes - corner them!
            score += 20;
        }

        // Avoid hexes that would isolate our territory (prefer expansion along edges)
        int ownAdjacent = countAdjacentOwnedHexes(c, r, p);
        if (ownAdjacent >= 2) {
            score += 5; // Good expansion point
        }

        return score;
    }

    // Count how many adjacent hexes are owned by player
    private int countAdjacentOwnedHexes(int c, int r, Player p) {
        int count = 0;
        int[] dc = {+1, -1, 0, 0, -1, +1};
        int[] dr;

        if (c % 2 == 0) {
            dr = new int[]{0, 0, -1, +1, +1, +1};
        } else {
            dr = new int[]{0, 0, -1, +1, -1, -1};
        }

        for (int i = 0; i < 6; i++) {
            int nc = c + dc[i];
            int nr = r + dr[i];

            if (!board.inBounds(nr, nc)) continue;
            if (p.isSpawnable(nc, nr)) {
                count++;
            }
        }

        return count;
    }

    public void botInitialSpawn() {

        Player p = getCurrentPlayer();

        List<MinionType> types = p.getMinionTypes();

        if (types.isEmpty()) {
            return;
        }

        MinionType type = types.get(new Random().nextInt(types.size()));

        // Find spawnable hexes sorted by distance to center (strategic position)
        List<String> spawnable = new ArrayList<>(p.getSpawnableHexes());

        // Calculate center of board (3.5, 3.5 for 0-7 board)
        double centerC = 3.5;
        double centerR = 3.5;

        // Sort by distance to center (closest first = more strategic)
        spawnable.sort((a, b) -> {
            String[] partsA = a.split(",");
            String[] partsB = b.split(",");
            double distA = Math.sqrt(Math.pow(Double.parseDouble(partsA[0]) - centerC, 2)
                    + Math.pow(Double.parseDouble(partsA[1]) - centerR, 2));
            double distB = Math.sqrt(Math.pow(Double.parseDouble(partsB[0]) - centerC, 2)
                    + Math.pow(Double.parseDouble(partsB[1]) - centerR, 2));
            return Double.compare(distA, distB);
        });

        // Spawn at the most central valid hex
        for (String hex : spawnable) {
            String[] parts = hex.split(",");
            int c = Integer.parseInt(parts[0]);
            int r = Integer.parseInt(parts[1]);

            if (canSpawnInitial(c, r)) {
                initialSpawn(type.getName(), c, r);
                return;
            }
        }
    }

    // coordinates are (col, row) with 0-indexed values
    public boolean canSpawnInitial(int c, int r) {

        if (phase != Phase.SETUP) return false;

        if (!board.inBounds(c, r)) return false;

        Player p = currentPlayer;

        if (!p.isSpawnable(c, r)) return false;

        if (!board.isEmpty(c, r)) return false;

        return true;
    }

    // getter UI
    public TurnSummary playTurn() {

        TurnSummary summary = new TurnSummary();
        System.out.println("[DEBUG] playTurn() - phase=" + phase + ", currentPlayer=" + currentPlayer.getId() + ", isBot=" + currentPlayer.isBot() + ", minions=" + currentPlayer.getMinions().size());

        if (phase == Phase.SETUP) {
            if (currentPlayer.isBot() && currentPlayer.getMinions().isEmpty()) {
                System.out.println("[DEBUG] Bot spawning initial minion...");
                botInitialSpawn();
            } else {
                System.out.println("[DEBUG] Human ending turn, switching...");
                endTurn();
            }
            return summary;
        }

        if (phase == Phase.RUNNING) {
            startTurn();

            if (currentPlayer.isBot()) {
                botTurnAction();
            }

            executeMinions();

            if (phase != Phase.FINISHED) {
                endTurn();
            }
        }

        return summary;
    }
}