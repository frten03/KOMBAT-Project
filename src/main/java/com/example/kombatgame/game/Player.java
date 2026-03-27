package com.example.kombatgame.game;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Player {
    @Getter
    private int id;
    private final GameConfig config;
    @Getter
    private double budget;
    @Setter
    @Getter
    private boolean isBot;
    @Getter
    private long turnCount = 1;
    private long spawnsUsed = 0;
    private int minionCounter = 0;
    @Getter
    private Set<String> spawnableHexes = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    @Getter
    private final List<Minion> minions = new CopyOnWriteArrayList<>();

    @Getter
    private List<MinionType> minionTypes = new CopyOnWriteArrayList<>();
    private boolean setupLocked = false;

    public Player(int id, GameConfig config) {
        if (config == null) throw new IllegalArgumentException("Config cannot be null");
        if (config.getInitBudget() < 0) {
            throw new IllegalArgumentException("Budget must be non-negative");
        }
        this.id = id;
        this.config = config;
        this.budget = config.getInitBudget();
    }

    // Helper for Coordinate Consistency
    private String getHexKey(int c, int r) {
        return c + "," + r;
    }

    public void addSpawnableHex(int c, int r) {
        spawnableHexes.add(getHexKey(c, r));
    }

    public boolean isSpawnable(int c, int r) {
        return spawnableHexes.contains(getHexKey(c, r));
    }

    public void setBudget(double budget) { this.budget = budget; }
    public void setBudget(long budget) { this.budget = (double) budget; }

    public boolean spendBudget(long cost) {
        if (budget < cost) return false;
        budget -= cost;
        return true;
    }

    public long getBudgetAsLong() { return (long) budget; }
    public long getMaxBudget() { return config.getMaxBudget(); }

    public void incrementTurn() { turnCount++; }

    public double calculateInterestRate() {
        if (budget < 1) return 0;
        double b = config.getInterestPct();
        // ป้องกัน Math Error ถ้า turnCount เป็น 0 หรือ 1 ในบางกรณี
        return b * Math.log10(budget) * Math.log(Math.max(2, turnCount));
    }

    public long getInterestRateAsLong() { return (long) calculateInterestRate(); }

    // --- Spawn Logic ---
    public boolean canSpawn() { return spawnsUsed < config.getMaxSpawns(); }
    public long getSpawnsLeft() { return Math.max(0, config.getMaxSpawns() - spawnsUsed); }
    public void incrementSpawnsUsed() { spawnsUsed++; }

    // --- Minion Management ---
    public void addMinion(Minion m) {
        if (m != null) minions.add(m);
    }

    public int getNextMinionNumber() {
        return ++minionCounter;
    }

    public void removeMinion(Minion m) { minions.remove(m); }
    public boolean hasNoMinions() { return minions.isEmpty(); }

    public long sumHP() {
        return minions.stream().mapToLong(Minion::getHp).sum();
    }

    // --- Minion Type Setup (Critical for 500 Error prevention) ---
    public void addMinionType(MinionType type) {
        if (setupLocked) throw new IllegalStateException("Cannot modify types: Setup is locked");
        if (type != null) minionTypes.add(type);
    }

    public MinionType getTypeByName(String name) {
        if (name == null) return null;
        return minionTypes.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void replaceMinionType(String name, MinionType newType) {
        for (int i = 0; i < minionTypes.size(); i++) {
            if (minionTypes.get(i).getName().equals(name)) {
                minionTypes.set(i, newType);
                return;
            }
        }
    }

    public void chooseMinionTypes(List<MinionType> baseTypes, List<String> selectedNames) {
        if (setupLocked) throw new IllegalStateException("Setup locked");
        if (baseTypes == null || selectedNames == null) return;

        minionTypes.clear();
        for (String name : selectedNames) {
            for (MinionType base : baseTypes) {
                if (base.getName().equals(name)) {
                    minionTypes.add(base.withConfig(base.getDefense(), base.getStrategySource()));
                }
            }
        }
    }

    public void lockSetup() { this.setupLocked = true; }
    public boolean isSetupLocked() { return setupLocked; }

    public boolean canPurchaseHex() {
        return budget >= config.getHexPurchaseCost();
    }

}