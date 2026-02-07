public class Evaluator {

    private GameAPI api;
    private boolean done = false;

    public Evaluator(GameAPI api) {
        this.api = api;
    }

    public boolean isDone() {
        return done;
    }

    private void stopIf(boolean condition) {
        if (condition) {
            done = true;
        }
    }

    // demo strategy
    public void evalExample() {
        if (done) return;

        System.out.println("Evaluator running strategy...");

        // if (opponent) then shoot up 2 else move up
        if (api.getOpponent(Direction.UP) != null) {

            boolean success = api.shoot(Direction.UP, 1);
            stopIf(!success);

        } else {

            boolean success = api.moveMinion(Direction.UP);
            stopIf(!success);
        }
    }
}
