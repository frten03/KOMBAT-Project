package com.example.kombatgame.game;

import lombok.Getter;

@Getter
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

}