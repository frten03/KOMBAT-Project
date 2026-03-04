package game;

public class GameConfig {

    private final long spawnCost;
    private final long hexPurchaseCost;
    private final long initBudget;
    private final long initHp;
    private final long turnBudget;
    private final long maxBudget;
    private final long interestPct;
    private final long maxTurns;
    private final long maxSpawns;

    public GameConfig(long spawnCost,
                      long hexPurchaseCost,
                      long initBudget,
                      long initHp,
                      long turnBudget,
                      long maxBudget,
                      long interestPct,
                      long maxTurns,
                      long maxSpawns) {

        this.spawnCost = spawnCost;
        this.hexPurchaseCost = hexPurchaseCost;
        this.initBudget = initBudget;
        this.initHp = initHp;
        this.turnBudget = turnBudget;
        this.maxBudget = maxBudget;
        this.interestPct = interestPct;
        this.maxTurns = maxTurns;
        this.maxSpawns = maxSpawns;
    }

    public long getSpawnCost() {
        return spawnCost;
    }
    public long getHexPurchaseCost() {
        return hexPurchaseCost;
    }
    public long getInitBudget() {
        return initBudget;
    }
    public long getInitHp() {
        return initHp;
    }
    public long getTurnBudget() {
        return turnBudget;
    }
    public long getMaxBudget() {
        return maxBudget;
    }
    public long getInterestPct() {
        return interestPct;
    }
    public long getMaxTurns() {
        return maxTurns;
    }
    public long getMaxSpawns() {
        return maxSpawns;
    }
}