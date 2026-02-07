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
        return 1;   // สมมติว่ามีอะไรอยู่ใกล้เสมอ
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