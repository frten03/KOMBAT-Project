package game;

import java.util.List;
import java.util.ArrayList;

// for UI (1 turn)
public class TurnSummary {

    private int turnNumber;
    private int playerId;

    private double budgetBefore;
    private double budgetAfterInterest;
    private double budgetAfterTurn;

    private List<ActionResult> actions = new ArrayList<>();

    private String gameStatus; // ongoing | finished
    private Integer winnerId;

    public void addAction(ActionResult action) {
        actions.add(action);
    }
}