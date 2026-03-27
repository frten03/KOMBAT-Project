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
    private final int targetPlayerId;
    private final int targetMinionNumber;
    private final int damageDealt;
    private final int targetHpAfter;
    private final boolean killed;

    public ActionLog(Type type, Direction direction, long value) {
        this.type = type;
        this.direction = direction;
        this.value = value;
        this.targetPlayerId = 0;
        this.targetMinionNumber = 0;
        this.damageDealt = 0;
        this.targetHpAfter = 0;
        this.killed = false;
    }

    public ActionLog(Type type, Direction direction, long value,
                     int targetPlayerId, int targetMinionNumber,
                     int damageDealt, int targetHpAfter, boolean killed) {
        this.type = type;
        this.direction = direction;
        this.value = value;
        this.targetPlayerId = targetPlayerId;
        this.targetMinionNumber = targetMinionNumber;
        this.damageDealt = damageDealt;
        this.targetHpAfter = targetHpAfter;
        this.killed = killed;
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

    public int getTargetPlayerId() {
        return targetPlayerId;
    }

    public int getTargetMinionNumber() {
        return targetMinionNumber;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public int getTargetHpAfter() {
        return targetHpAfter;
    }

    public boolean isKilled() {
        return killed;
    }

    public boolean hasCombatResult() {
        return targetPlayerId != 0 || targetMinionNumber != 0;
    }

    @Override
    public String toString() {
        return type + " " + direction + " " + value;
    }
}