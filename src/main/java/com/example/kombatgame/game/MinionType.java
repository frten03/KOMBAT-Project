package com.example.kombatgame.game;

public class MinionType {

    private final String name;
    private final int defense;
    private final String strategySource;

    public MinionType(String name, int defense, String strategySource) {
        this.name = name;
        this.defense = defense;
        this.strategySource = strategySource;
    }

    // method edit immutable
    public MinionType withConfig(int newDefense, String newStrategySource) {
        return new MinionType(this.name, newDefense, newStrategySource);
    }

    // getter
    public String getName() { return name; }
    public int getDefense() { return defense; }
    public String getStrategySource() { return strategySource; }
}