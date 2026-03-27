package com.example.kombatgame.game;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

public class Minion {
    @Getter
    private final String id;
    @Getter
    private final int minionNumber;
    @Getter
    private int row;
    @Getter
    private int col;
    @Getter
    private int hp;
    @Getter
    private int defense;
    @Getter
    private final Player owner;
    @Getter
    private MinionType type;
    @Getter
    private int maxHp;

    public Minion(int row, int col, int hp, int defense, Player owner, int minionNumber) {
        if (hp <= 0) {
            throw new IllegalArgumentException("HP must be positive");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        this.id = UUID.randomUUID().toString();
        this.minionNumber = minionNumber;
        this.row = row;
        this.col = col;
        this.hp = hp;
        this.maxHp = hp;
        this.defense = defense;
        this.owner = owner;
    }

    public Minion(int row, int col, int hp,
                  MinionType type, Player owner, int minionNumber) {

        this(row, col, hp, type.getDefense(), owner, minionNumber);
        this.type = type;
        this.maxHp = hp;
    }

    public void setPosition(int r, int c) {
        this.row = r;
        this.col = c;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int expenditure) {
        int reduced = Math.max(1, expenditure - getDefense());
        hp = Math.max(0, hp - reduced);
    }

    @Override
    public String toString() {
        return "game.Minion{" +
                "pos=(" + row + "," + col + ")" +
                ", hp=" + hp +
                ", def=" + defense +
                ", ownerBudget=" + owner.getBudget() +
                '}';
    }

}
