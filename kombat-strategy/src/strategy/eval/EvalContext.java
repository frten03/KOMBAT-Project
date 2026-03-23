package strategy.eval;

import java.util.HashMap;
import java.util.Map;

public class EvalContext {
    private boolean done = false;
    public final GameAPI api;
    private final Map<String, Long> localVars = new HashMap<>();
    private final Map<String, Long> globalVars;
    private ActionResult result = new ActionResult();

    public EvalContext(GameAPI api, Map<String, Long> globalVars) {
        this.api = api;
        this.globalVars = globalVars;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone() {
        this.done = true;
    }

    public long getVariable(String name) {
        if (Character.isUpperCase(name.charAt(0))) {
            return globalVars.getOrDefault(name, 0L);
        } else {
            return localVars.getOrDefault(name, 0L);
        }
    }

    public void setVariable(String name, long value) {

        if (isSpecial(name)) return; // no-op

        if (Character.isUpperCase(name.charAt(0))) {
            globalVars.put(name, value);
        } else {
            localVars.put(name, value);
        }
    }

    public long resolveIdentifier(String name) {

        return switch (name) {
            case "row" -> api.getRow();
            case "col" -> api.getCol();
            case "Budget" -> api.getBudget();
            case "Int" -> api.getInterest();
            case "MaxBudget" -> api.getMaxBudget();
            case "SpawnsLeft" -> api.getSpawnsLeft();
            case "random" -> api.getRandom();
            default -> getVariable(name);
        };
    }

    public boolean isSpecial(String name) {
        return switch (name) {
            case "row", "col", "Budget", "Int",
                 "MaxBudget", "SpawnsLeft", "random" -> true;
            default -> false;
        };
    }

    public enum TerminationReason {
        DONE,
        DIVISION_BY_ZERO,
        BUDGET_INSUFFICIENT,
        LOOP_LIMIT
    }

    private TerminationReason terminationReason = null;

    public void terminate(TerminationReason reason) {
        this.terminationReason = reason;
        this.done = true;
    }

    public TerminationReason getTerminationReason() {
        return terminationReason;
    }

    public ActionResult getResult() {
        return result;
    }

}

//เช็คว่า strategy done?
// game state now?

/*implement GameAPI ยังไงก็ได้ตาม game logic (hex / grid / combat)
*/
