package com.example.kombatgame.game;

import java.util.List;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TurnSummary {

    private int turnNumber;
    private int playerId;

    private double budgetBefore;
    private double budgetAfterInterest;
    private double budgetAfterTurn;

    private final List<ActionResult> actions = new ArrayList<>();

    private String gameStatus; // ongoing | finished
    private Integer winnerId;

    public void addAction(ActionResult action) {
        actions.add(action);
    }
}