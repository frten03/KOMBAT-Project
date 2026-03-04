package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Player {

    private final GameConfig config;

    private double budget;
    private boolean isBot;
    private long turnCount = 1;
    private long spawnsUsed = 0;

    private Set<String> spawnableHexes = new HashSet<>();
    private final List<Minion> minions = new ArrayList<>();

    private List<MinionType> minionTypes = new ArrayList<>();
    private boolean setupLocked = false;

    public Player(GameConfig config) {
        if (config.getInitBudget() < 0) {
            throw new IllegalArgumentException("Budget must be non-negative");
        }
        this.config = config;
        this.budget = config.getInitBudget();
    }

    // Set Bot
    public void setBot(boolean bot) {
        this.isBot = bot;
    }

    public boolean isBot() {
        return isBot;
    }

    // BUDGET
    public double getBudget() {
        return budget;
    }

    public boolean spendBudget(long cost) {
        if (budget < cost) {
            return false;
        }
        budget -= cost;
        return true;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public long getBudgetAsLong() {
        return (long) budget;  // for Budget special variable
    }

    public long getMaxBudget() {
        return config.getMaxBudget();
    }

    // TURN
    public long getTurnCount() {
        return turnCount;
    }

    public void incrementTurn() {
        turnCount++;
    }

    // INTEREST RATE (Int special variable)
    public double calculateInterestRate() {
        if (budget < 1) return 0;

        double b = config.getInterestPct();
        return b * Math.log10(budget) * Math.log(turnCount);
    }

    public long getInterestRateAsLong() {
        return (long) calculateInterestRate();
    }

    // SPAWN SYSTEM
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

    public Set<String> getSpawnableHexes() {
        return spawnableHexes;
    }

    // MINIONS
    public List<Minion> getMinions() {
        return minions;
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

    public List<MinionType> getMinionTypes() {
        return minionTypes;
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

    // CHECK PURCHASE PERMISSION
    public boolean canPurchaseHex() {
        return budget >= config.getHexPurchaseCost();
    }

    // CHECK SPAWN PERMISSION
    public boolean canSpawnAt(int r, int c) {

        // ต้องเป็น spawnable
        if (!isSpawnable(r, c)) return false;

        // ต้องยังไม่เกิน max spawns
        if (!canSpawn()) return false;

        // ต้องมีเงินพอ
        return budget >= config.getSpawnCost();
    }
}