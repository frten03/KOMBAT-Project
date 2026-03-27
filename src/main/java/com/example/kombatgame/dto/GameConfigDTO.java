package com.example.kombatgame.dto;

public class GameConfigDTO {
    public long spawnCost;
    public long hexPurchaseCost;
    public long initBudget;
    public long initHp;
    public long turnBudget;
    public long maxBudget;
    public long interestPct;
    public long maxTurns;
    public long maxSpawns;

    public GameConfigDTO() {}

    public GameConfigDTO(long spawnCost, long hexPurchaseCost, long initBudget, long initHp,
                         long turnBudget, long maxBudget, long interestPct, long maxTurns, long maxSpawns) {
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
}
