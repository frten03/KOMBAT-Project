public class EvaluatorContext implements InterfaceGameAPI {

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
    public void moveMinion(Direction dir) {
        logic.moveMinion(self, dir);
    }

    @Override
    public void shoot(Direction dir, long cost) {
        logic.shoot(self, dir, cost);
    }

    @Override
    public Minion getOpponent(Direction dir) {
        return logic.getOpponent(self, dir);
    }
}
