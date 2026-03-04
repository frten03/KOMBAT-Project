package game;

import strategy.ast.Direction;
import strategy.eval.GameAPI;

public class RealGameAPI implements GameAPI {

    private final GameLogic logic;
    private final Minion self;
    private boolean done = false;

    public RealGameAPI(GameLogic logic, Minion self) {
        this.logic = logic;
        this.self = self;
    }

    @Override
    public void move(Direction dir) {
        if (done) return;

        boolean success = logic.moveMinion(self, dir);

        // false (money isn't enough.)
        if (!success) {
            done = true;
        }
    }

    @Override
    public void shoot(Direction dir, long cost) {
        if (done) return;
        logic.shoot(self, dir, cost);
    }

    @Override
    public long opponent() {
        return findClosest(false);
    }

    @Override
    public long getBudget() {
        return (long) logic
                .getPlayer(self.getOwner())
                .getBudget();
    }

    @Override
    public long getMaxBudget() {
        return logic.getConfig().getMaxBudget();
    }

    @Override
    public long getInterest() {

        Player p = logic.getPlayer(self.getOwner());

        double m = p.getBudget();
        if (m < 1) return 0;

        double b = logic.getConfig().getInterestPct();
        double t = p.getTurnCount();

        double r = b * Math.log10(m) * Math.log(t);

        return (long) r;
    }

    @Override
    public long getSpawnsLeft() {
        return logic
                .getPlayer(self.getOwner())
                .getSpawnsLeft();
    }

    private final java.util.Random random = new java.util.Random();
    @Override
    public long getRandom() {
        return random.nextInt(1000);
    }

    @Override
    public int getRow() {
        return self.getRow();
    }

    @Override
    public int getCol() {
        return self.getCol();
    }

    @Override
    public long ally() {
        return findClosest(true);
    }

    private long findClosest(boolean findAlly) {

        Grid board = logic.getBoard();
        int selfR = self.getRow();
        int selfC = self.getCol();

        Direction[] dirs = {
                Direction.UPLEFT,
                Direction.UP,
                Direction.UPRIGHT,
                Direction.DOWNLEFT,
                Direction.DOWN,
                Direction.DOWNRIGHT
        };

        for (int dist = 1; dist <= 8; dist++) {

            for (int i = 0; i < dirs.length; i++) {

                Direction d = dirs[i];
                int nr = selfR + d.dr * dist;
                int nc = selfC + d.dc * dist;

                if (!board.inBounds(nr, nc)) continue;

                Minion m = board.getMinion(nr, nc);
                if (m == null) continue;

                boolean sameOwner = m.getOwner() == self.getOwner();

                if (findAlly && sameOwner) {
                    return dist * 10L + (i + 1);
                }

                if (!findAlly && !sameOwner) {
                    return dist * 10L + (i + 1);
                }
            }
        }
        // return 2 digits

        return 0;
    }

    @Override
    public long nearby(Direction dir) {

        Grid board = logic.getBoard();
        int selfR = self.getRow();
        int selfC = self.getCol();

        for (int dist = 1; dist <= 8; dist++) {

            int nr = selfR + dir.dr * dist;
            int nc = selfC + dir.dc * dist;

            if (!board.inBounds(nr, nc)) return 0;

            Minion m = board.getMinion(nr, nc);
            if (m == null) continue;

            int hpDigits = String.valueOf(m.getHp()).length();
            int defDigits = String.valueOf(m.getDefense()).length();

            long value = 100L * hpDigits + 10L * defDigits + dist;

            if (m.getOwner() == self.getOwner()) {
                return -value;
            } else {
                return value;
            }
        }

        return 0;
    }

}
