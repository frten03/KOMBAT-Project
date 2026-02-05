public interface InterfaceGameAPI {
    boolean canMove(Direction dir);

    void moveMinion(Direction dir);

    void shoot(Direction dir, long cost);

    Minion getOpponent(Direction dir);
}
