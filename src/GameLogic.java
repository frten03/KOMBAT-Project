public class GameLogic {

    private Grid board;

    public GameLogic(Grid board) {
        this.board = board;
    }

    // ===== movement =====
    public boolean canMove(Minion m, Direction dir) {
        int nr = m.getRow() + dir.dr;
        int nc = m.getCol() + dir.dc;

        if (!board.inBounds(nr, nc))
            return false;
        if (board.isOccupied(nr, nc))
            return false;

        return true;
    }

    public void moveMinion(Minion m, Direction dir) {
        m.getOwner().decreaseBudget(1);

        if (!canMove(m, dir))
            return;

        int nr = m.getRow() + dir.dr;
        int nc = m.getCol() + dir.dc;
        board.moveMinion(m, nr, nc);
    }

    // ===== combat =====
    public void shoot(Minion attacker, Direction dir, long cost) {
        Player p = attacker.getOwner();
        if (p.getBudget() < cost)
            return;

        int tr = attacker.getRow() + dir.dr;
        int tc = attacker.getCol() + dir.dc;

        if (!board.inBounds(tr, tc))
            return;

        Minion target = board.getMinion(tr, tc);
        if (target == null)
            return;

        p.decreaseBudget(cost);
        target.takeDamage((int) cost);

        if (target.getHp() <= 0) {
            board.removeMinion(target);
        }
    }

    public Minion getOpponent(Minion self, Direction dir) {
        int tr = self.getRow() + dir.dr;
        int tc = self.getCol() + dir.dc;

        Minion m = board.getMinion(tr, tc);
        if (m == null)
            return null;
        if (m.getOwner() == self.getOwner())
            return null;

        return m;
    }
}
