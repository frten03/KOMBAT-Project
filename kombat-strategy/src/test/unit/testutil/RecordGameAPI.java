package test.unit.testutil;

import game.GameLogic;
import game.Grid;
import game.Minion;
import game.Player;
import lombok.Getter;
import strategy.ast.Direction;
import strategy.eval.ActionLog;
import strategy.eval.GameAPI;

import java.util.*;

public class RecordGameAPI implements GameAPI {

    private final GameLogic logic;
    private final Minion self;
    private boolean done = false;

    public RecordGameAPI() {
        this.logic = null;
        this.self = null;
    }

    public RecordGameAPI(GameLogic logic, Minion self) {
        this.logic = logic;
        this.self = self;
    }

    private Long overrideOpponent = null;
    private Long overrideAlly = null;
    private Long overrideBudget = null;
    private final Map<Direction, Long> overrideNearby = new EnumMap<>(Direction.class);

    public void setOpponent(long v) { overrideOpponent = v; }
    public void setAlly(long v) { overrideAlly = v; }
    public void setBudget(long v) { overrideBudget = v; }
    public void setNearby(Direction d, long v) { overrideNearby.put(d, v); }

    public void clearOverrides() {
        overrideOpponent = null;
        overrideAlly = null;
        overrideBudget = null;
        overrideNearby.clear();
    }

    public int moveCount = 0;
    public int shootCount = 0;
    public Direction lastMoveDir = null;
    public Direction lastShootDir = null;
    public long lastShootCost = -1;

    @Getter
    private final List<ActionLog> logs = new ArrayList<>();

    public void clearLogs() { logs.clear(); }

    @Override
    public void move(Direction dir) {
        if (done) return;

        moveCount++;
        lastMoveDir = dir;
        logs.add(new ActionLog(ActionLog.Type.MOVE, dir, 0));

        if (logic != null) {
            boolean success = logic.moveMinion(self, dir);
            if (!success) done = true;
        } else {
            // test mode → simulate budget
            if (getBudget() < 1) {
                done = true;
            } else {
                overrideBudget = getBudget() - 1;
            }
        }
    }

    @Override
    public void shoot(Direction dir, long cost) {
        if (done) return;

        long total = cost + 1;

        if (getBudget() < total) return;

        // record
        shootCount++;
        lastShootDir = dir;
        lastShootCost = cost;
        logs.add(new ActionLog(ActionLog.Type.SHOOT, dir, cost));

        if (logic != null) {
            logic.shoot(self, dir, cost);
        } else {
            overrideBudget = getBudget() - total;
        }
    }

    @Override
    public long opponent() {
        if (overrideOpponent != null) return overrideOpponent;
        return (logic == null) ? 0 : findClosest(false);
    }

    @Override
    public long ally() {
        if (overrideAlly != null) return overrideAlly;
        return (logic == null) ? 0 : findClosest(true);
    }

    @Override
    public long nearby(Direction dir) {
        if (overrideNearby.containsKey(dir)) {
            return overrideNearby.get(dir);
        }

        if (logic == null) return 0;

        Grid board = logic.getBoard();
        int r = self.getRow();
        int c = self.getCol();

        for (int dist = 1; dist <= 8; dist++) {
            int nr = r + dir.dr * dist;
            int nc = c + dir.dc * dist;

            if (!board.inBounds(nr, nc)) break;

            Minion m = board.getMinion(nr, nc);
            if (m == null) continue;

            int x = String.valueOf(m.getHp()).length();
            int y = String.valueOf(m.getDefense()).length();

            long val = 100L * x + 10L * y + dist;
            return (m.getOwner() == self.getOwner()) ? -val : val;
        }

        return 0;
    }

    private long findClosest(boolean ally) {
        Grid board = logic.getBoard();
        int r = self.getRow();
        int c = self.getCol();

        Direction[] dirs = {
                Direction.UP,
                Direction.UPRIGHT,
                Direction.DOWNRIGHT,
                Direction.DOWN,
                Direction.DOWNLEFT,
                Direction.UPLEFT
        };

        for (int dist = 1; dist <= 8; dist++) {
            for (int i = 0; i < dirs.length; i++) {
                Direction d = dirs[i];
                int nr = r + d.dr * dist;
                int nc = c + d.dc * dist;

                if (!board.inBounds(nr, nc)) continue;

                Minion m = board.getMinion(nr, nc);
                if (m == null) continue;

                boolean same = m.getOwner() == self.getOwner();

                if (ally && same) return dist * 10L + (i + 1);
                if (!ally && !same) return dist * 10L + (i + 1);
            }
        }
        return 0;
    }

    @Override
    public long getBudget() {
        if (overrideBudget != null) return overrideBudget;
        return (logic == null) ? 100 : (long) logic.getPlayer(self.getOwner()).getBudget();
    }

    @Override
    public long getMaxBudget() {
        return (logic == null) ? 1000 : logic.getConfig().getMaxBudget();
    }

    @Override
    public long getInterest() {
        if (logic == null) return 0;

        Player p = logic.getPlayer(self.getOwner());

        double m = p.getBudget();
        if (m < 1) return 0;

        double b = logic.getConfig().getInterestPct();
        double t = p.getTurnCount();

        return (long) (b * Math.log10(m) * Math.log(t));
    }

    @Override
    public long getSpawnsLeft() {
        return (logic == null) ? 10 :
                logic.getPlayer(self.getOwner()).getSpawnsLeft();
    }

    private final Random random = new Random();

    @Override
    public long getRandom() {
        return random.nextInt(1000);
    }

    @Override
    public int getRow() {
        return (self == null) ? 0 : self.getRow();
    }

    @Override
    public int getCol() {
        return (self == null) ? 0 : self.getCol();
    }
}