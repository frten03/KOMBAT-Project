public class Evaluator {
    private InterfaceGameAPI api;

    public Evaluator(InterfaceGameAPI api) {
        this.api = api;
    }

    public void evalExample() {
        if (api.getOpponent(Direction.UP) != null) {
            api.shoot(Direction.UP, 2);
        } else if (api.canMove(Direction.UP)) {
            api.moveMinion(Direction.UP);
        }
    }
}
