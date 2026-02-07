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
        logic.moveMinion(self, dir); // budget / fail logic อยู่ข้างในแล้ว
    }

    @Override
    public void shoot(Direction dir, long cost) {
        if (done) return;
        logic.shoot(self, dir, cost);
    }

    @Override
    public long opponent() {
        // ตัวอย่าง
        return logic.getOpponent(self, Direction.UP) == null ? 0 : 1;
    }

    @Override
    public long nearby(Direction dir) {
        return logic.getOpponent(self, dir) == null ? 0 : 1;
    }

    @Override
    public long ally() {
        return 1; // self
    }
}
