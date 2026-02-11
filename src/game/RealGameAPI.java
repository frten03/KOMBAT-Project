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
        if (done) return ;
        logic.moveMinion(self, dir); // budget / fail logic อยู่ข้างในแล้ว
    }

    @Override
    public void shoot(Direction dir, long cost) {
        if (done) return;
        logic.shoot(self, dir, cost);
    }

    @Override
    public long opponent() {
        // ตัวอย่าง demo
//        return logic.getOpponent(self, Direction.UP) == null ? 0 : 1;
        // check edge no direction
        return logic.hasOpponent(self) ? 1 : 0;
    }

//    @Override
//    public long nearby(Direction dir) {
//        return logic.getOpponent(self, dir) == null ? 0 : 1;
//    }

    // เผื่อเก็บค่าไปเช็คว่าเป็น opponent
    @Override
    public long nearby(Direction dir) {

        int nr = self.getRow() + dir.dr;
        int nc = self.getCol() + dir.dc;

        if (!logic.getBoard().inBounds(nr, nc))
            return -1;

        Minion m = logic.getBoard().getMinion(nr, nc);

        if (m == null)
            return 0;

        if (m.getOwner() == self.getOwner())
            return 1;

        return 2;
    }


    @Override
    public long ally() {
        return 1; // self
    }
}
