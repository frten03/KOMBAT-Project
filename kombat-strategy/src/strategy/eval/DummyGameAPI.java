package strategy.eval;

import strategy.ast.Direction;

public class DummyGameAPI implements GameAPI {

    // ===== configurable fields =====
    private long budget = 100;
    private long maxBudget = 1000;
    private long interest = 5;
    private long spawnsLeft = 3;
    private long randomValue = 42;
    private int row = 2;
    private int col = 3;

    private long nearbyValue = 0;
    private long allyValue = 0;
    private long opponentValue = 0;

    //setters for test control
    public void setBudget(long budget) {
        this.budget = budget;
    }

    public void setNearby(long value) {
        this.nearbyValue = value;
    }

    public void setAlly(long value) {
        this.allyValue = value;
    }

    public void setOpponent(long value) {
        this.opponentValue = value;
    }

    public void setRandom(long value) {
        this.randomValue = value;
    }

    //action (no real state change)

    @Override
    public void move(Direction dir) {
        System.out.println("MOVE " + dir);
        budget -= 1;
    }

    @Override
    public void shoot(Direction dir, long cost) {
        System.out.println("SHOOT " + dir + " cost=" + cost);
        budget -= (cost + 1);
    }

    // info expressions

    @Override
    public long nearby(Direction dir) {
        return nearbyValue;
    }

    @Override
    public long ally() {
        return allyValue;
    }

    @Override
    public long opponent() {
        return opponentValue;
    }

    // special variables

    @Override
    public long getBudget() {
        return budget;
    }

    @Override
    public long getMaxBudget() {
        return maxBudget;
    }

    @Override
    public long getInterest() {
        return interest;
    }

    @Override
    public long getSpawnsLeft() {
        return spawnsLeft;
    }

    @Override
    public long getRandom() {
        return randomValue;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCol() {
        return col;
    }
}