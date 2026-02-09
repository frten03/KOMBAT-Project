package strategy.eval;

import strategy.ast.Direction;

/**
 * RecordingDummyGameAPI
 *
 * Test double สำหรับใช้ใน JUnit tests
 * ทำหน้าที่บันทึกว่ามีการเรียกคำสั่งจาก Evaluator อะไรบ้าง
 * โดยไม่พึ่ง game logic จริง
 */
public class RecordingDummyGameAPI implements GameAPI {

    // ===== Record action calls =====
    public int moveCount = 0;
    public int shootCount = 0;

    public Direction lastMoveDir = null;
    public Direction lastShootDir = null;
    public long lastShootCost = -1;

    // ===== Action commands =====

    @Override
    public void move(Direction dir) {
        moveCount++;
        lastMoveDir = dir;
    }

    @Override
    public void shoot(Direction dir, long cost) {
        shootCount++;
        lastShootDir = dir;
        lastShootCost = cost;
    }

    // ===== Info expressions (stub behavior) =====

    /**
     * ใช้สำหรับทดสอบ nearby <direction>
     * คืนค่าเป็น 1 เสมอ เพื่อให้ condition เป็น true
     */
    @Override
    public long nearby(Direction dir) {
        return 1;
    }

    /**
     * ยังไม่ใช้ใน phase นี้
     */
    @Override
    public long ally() {
        return 0;
    }

    /**
     * ใช้สำหรับทดสอบ if opponent / while opponent
     * คืนค่าเป็น 1 เพื่อให้เข้า branch
     */
    @Override
    public long opponent() {
        System.out.println("GameAPI.opponent()");
        return 1; // หรือ 0 ตาม test
    }

}
