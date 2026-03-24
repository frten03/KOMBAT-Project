package com.example.kombatgame.game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player {
    @Getter
    private int id;

    private final GameConfig config;

    // budget
    @Setter
    @Getter
    private double budget;

    // set Bot
    @Getter
    @Setter
    private boolean isBot;

    // turn
    @Getter
    private long turnCount = 1;
    private long spawnsUsed = 0;

    @Getter
    private Set<String> spawnableHexes = new HashSet<>();

    // minion
    @Getter
    private final List<Minion> minions = new ArrayList<>();

    @Getter
    private List<MinionType> minionTypes = new ArrayList<>();
    private boolean setupLocked = false;

    public Player(int id, GameConfig config) {
        if (config.getInitBudget() < 0) {
            throw new IllegalArgumentException("Budget must be non-negative");
        }
        this.id = id;
        this.config = config;
        this.budget = config.getInitBudget();
    }

    public boolean spendBudget(long cost) {
        if (budget < cost) {
            return false;
        }
        budget -= cost;
        return true;
    }

    public long getBudgetAsLong() {
        return (long) budget;
    }

    public long getMaxBudget() {
        return config.getMaxBudget();
    }

    public void incrementTurn() {
        turnCount++;
    }

    // interest rate
    public double calculateInterestRate() {
        if (budget < 1) return 0;

        double b = config.getInterestPct();
        return b * Math.log10(budget) * Math.log(turnCount);
    }

    public long getInterestRateAsLong() {
        return (long) calculateInterestRate();
    }

    // spawn system
    public boolean canSpawn() {
        return spawnsUsed < config.getMaxSpawns();
    }

    public long getSpawnsLeft() {
        return config.getMaxSpawns() - spawnsUsed;
    }

    public void incrementSpawnsUsed() {
        spawnsUsed++;
    }

    public void addSpawnableHex(int r, int c) {
        spawnableHexes.add(r + "," + c);
    }

    public boolean isSpawnable(int r, int c) {
        return spawnableHexes.contains(r + "," + c);
    }

    public void addMinion(Minion m) {
        minions.add(m);
    }

    public void removeMinion(Minion m) {
        minions.remove(m);
    }

    public boolean hasNoMinions() {
        return minions.isEmpty();
    }

    public long sumHP() {
        long total = 0;
        for (Minion m : minions) {
            total += m.getHp();
        }
        return total;
    }
    public void addMinionType(MinionType type) {
        if (setupLocked)
            throw new IllegalStateException("Cannot modify types after game start");
        minionTypes.add(type);
    }

    public MinionType getTypeByName(String name) {
        for (MinionType t : minionTypes) {
            if (t.getName().equalsIgnoreCase(name)) return t;
        }
        return null;
    }

    public void replaceMinionType(String name, MinionType newType) {

        for (int i = 0; i < minionTypes.size(); i++) {
            if (minionTypes.get(i).getName().equals(name)) {
                minionTypes.set(i, newType);
                return;
            }
        }
    }

    public void chooseMinionTypes(
            List<MinionType> baseTypes,
            List<String> selectedNames
    ) {
        if (setupLocked)
            throw new IllegalStateException("Setup locked");

        minionTypes.clear();

        for (String name : selectedNames) {

            for (MinionType base : baseTypes) {

                if (base.getName().equals(name)) {

                    // clone แบบ immutable
                    minionTypes.add(
                            base.withConfig(
                                    base.getDefense(),
                                    base.getStrategySource()
                            )
                    );
                }
            }
        }
    }

    public void lockSetup() {
        setupLocked = true;
    }

    // check purchase permission
    public boolean canPurchaseHex() {
        return budget >= config.getHexPurchaseCost();
    }

}