package com.example.kombatgame.game;

import lombok.Getter;
import com.example.kombatgame.strategy.ast.Direction;

public class GameLogic {

    @Getter
    private final Grid board;
    private final Player player1;
    private final Player player2;
    @Getter
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

    // combat result
    public static class ShootResult {
        public boolean hit = false;
        public Minion target = null;
        public int damageDealt = 0;
        public int targetHpAfter = 0;
        public boolean killed = false;
    }

    // combat
    public ShootResult shoot(Minion attacker, Direction dir, long cost) {
        ShootResult result = new ShootResult();
        Player p = attacker.getOwner();

        if (!p.spendBudget(cost+1)) return result;

        int tr = attacker.getRow() + dir.dr;
        int tc = attacker.getCol() + dir.dc;

        if (!board.inBounds(tr, tc)) return result;

        Minion target = board.getMinion(tr, tc);
        if (target == null) return result;

        result.hit = true;
        result.target = target;

        int originalHp = target.getHp();
        target.takeDamage((int) cost);
        result.damageDealt = originalHp - target.getHp();
        result.targetHpAfter = target.getHp();

        if (target.getHp() <= 0) {
            board.removeMinion(target);
            result.killed = true;
        }

        return result;
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

    public Player getPlayer(Player owner) {
        return owner;
    }

}
