package game;

import strategy.ast.Direction;

public class GameLogic {

    private final Grid board;
    private final Player player1;
    private final Player player2;
    private final GameConfig config;

    public GameLogic(Grid board, Player p1, Player p2, GameConfig config) {
        this.board = board;
        this.player1 = p1;
        this.player2 = p2;
        this.config = config;
    }

    // movement
    public boolean canMove(Minion m, Direction dir) {
        int nr = m.getRow() + dir.dr;
        int nc = m.getCol() + dir.dc;

        if (!board.inBounds(nr, nc)) return false;
        if (board.isOccupied(nr, nc)) return false;

        return true;
    }

    public boolean moveMinion(Minion minion, Direction dir) {
        Player p = minion.getOwner();

        // Not enough money -> fail
        if (!p.spendBudget(1)) return false;

        int nr = minion.getRow() + dir.dr;
        int nc = minion.getCol() + dir.dc;

        // cant move -> fail
        if (!board.inBounds(nr, nc)) return false;
        if (board.isOccupied(nr, nc)) return false;

        board.moveMinion(minion, nr, nc);
        return true;
    }

    // combat
    public boolean shoot(Minion attacker, Direction dir, long cost) {
        Player p = attacker.getOwner();

        if (!p.spendBudget(cost+1)) return false;

        int tr = attacker.getRow() + dir.dr;
        int tc = attacker.getCol() + dir.dc;

        if (!board.inBounds(tr, tc)) return false;

        Minion target = board.getMinion(tr, tc);
        if (target == null) return false;

        target.takeDamage((int) cost);

        if (target.getHp() <= 0) {
            board.removeMinion(target);
        }

        return true;
    }

    // info
    public Minion getOpponent(Minion self, Direction dir) {
        int tr = self.getRow() + dir.dr;
        int tc = self.getCol() + dir.dc;

        Minion m = board.getMinion(tr, tc);
        if (m == null) return null;
        if (m.getOwner() == self.getOwner()) return null;

        return m;
    }

    public boolean hasOpponent(Minion self) {
        for (Minion m : board.getAllMinions()) {
            if (m.getOwner() != self.getOwner()) {
                return true;
            }
        }
        return false;
    }

    // getter UI
    public Grid getBoard() {
        return board;
    }

    public Player getPlayer(Player owner) {
        return owner;
    }

    public GameConfig getConfig() {
        return config;
    }
}
