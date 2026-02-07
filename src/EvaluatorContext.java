public class EvaluatorContext implements GameAPI {

    private Minion self;
    private GameLogic logic;

    public EvaluatorContext(Minion self, GameLogic logic) {
        this.self = self;
        this.logic = logic;
    }

    @Override
    public boolean canMove(Direction dir) {
        return logic.canMove(self, dir);
    }

    @Override
    public boolean moveMinion(Direction dir) {
        return logic.moveMinion(self, dir);
    }

    @Override
    public boolean shoot(Direction dir, long cost) {
        return logic.shoot(self, dir, cost);
    }

    @Override
    public Minion getOpponent(Direction dir) {
        return logic.getOpponent(self, dir);
    }
}
