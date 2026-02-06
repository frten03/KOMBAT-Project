package strategy.eval;

import strategy.ast.Direction;
//ใช้เรียก grid ผ่าน API

public interface GameAPI {
    void move(Direction dir);
    void shoot(Direction dir, long cost);

    // สำหรับ info expression (ไว้ใช้ต่อ)
    long nearby(Direction dir);
    long ally();
    long opponent();
}

//ตัวอย่าง implement class *สามารถแก้ไขทีหลังได้ ถ้าเราคุยกันแล้วมีอะไรเปลี่ยนแปลง
/*
class RealGameAPI implements GameAPI {
    void move(Direction dir) { ... }
    void shoot(Direction dir, long cost) { ... }
    long nearby(Direction dir) { ... }
    long ally() { ... }
    long opponent() { ... }
}
 */
