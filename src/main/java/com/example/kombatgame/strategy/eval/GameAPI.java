package com.example.kombatgame.strategy.eval;

import com.example.kombatgame.strategy.ast.Direction;
//ใช้เรียก grid ผ่าน API

public interface GameAPI {
    void move(Direction dir);
    void shoot(Direction dir, long cost);

    // สำหรับ info expression ไว้ใช้ต่อ
    long nearby(Direction dir);
    long ally();
    long opponent();

    // for Global var
    long getBudget();
    long getMaxBudget();
    long getInterest();
    long getSpawnsLeft();
    long getRandom();
    int getRow();
    int getCol();

    default Object getLastShootResult() {
        return null;
    }
}

