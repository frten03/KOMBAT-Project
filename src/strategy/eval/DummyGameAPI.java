package strategy.eval;

import strategy.ast.Direction;

public class DummyGameAPI implements GameAPI {

    @Override
    public void move(Direction dir) {
        System.out.println("MOVE " + dir);
    }

    @Override
    public void shoot(Direction dir, long cost) {
        System.out.println("SHOOT " + dir + " cost=" + cost);
    }

    // ===== Info Expressions (stub) =====

    @Override
    public long nearby(Direction dir) {
        /*
        return :
        0 = empty
        1 = ally
        2 = opponent
         */
        return 1;
    }

    @Override
    public long ally() {
        return 0;   // ไม่มี ally
    }

    @Override
    public long opponent() {
        return 0;   // ไม่มี opponent
    }

}
