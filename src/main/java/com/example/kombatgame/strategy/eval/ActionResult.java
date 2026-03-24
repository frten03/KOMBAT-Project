package com.example.kombatgame.strategy.eval;

import java.util.ArrayList;
import java.util.List;

public class ActionResult {

    private final List<ActionLog> actions = new ArrayList<>();

    private boolean done;

    private String terminationReason;

    public void addAction(ActionLog log) {
        actions.add(log);
    }

    public List<ActionLog> getActions() {
        return actions;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    public void setTerminationReason(String reason) {
        this.terminationReason = reason;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

}