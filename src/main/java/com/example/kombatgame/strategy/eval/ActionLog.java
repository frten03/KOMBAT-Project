package com.example.kombatgame.strategy.eval;

import com.example.kombatgame.strategy.ast.Direction;

public class ActionLog {

    public enum Type {
        MOVE,
        SHOOT
    }

    private final Type type;
    private final Direction direction;
    private final long value;

    public ActionLog(Type type, Direction direction, long value) {
        this.type = type;
        this.direction = direction;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + " " + direction + " " + value;
    }
}