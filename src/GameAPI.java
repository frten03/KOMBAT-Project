public interface GameAPI {
    boolean canMove(Direction dir);

    boolean moveMinion(Direction dir);

    boolean shoot(Direction dir, long cost);

    Minion getOpponent(Direction dir);
}
