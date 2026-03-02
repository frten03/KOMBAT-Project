package strategy.eval;

import java.util.HashMap;
import java.util.Map;

public class EvalContext {
    private boolean done = false;
    public final GameAPI api;
    private final Map<String, Long> vars = new HashMap<>();

    public EvalContext(GameAPI api) {
        this.api = api;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone() {
        this.done = true;
    }

    public long getVariable(String name) {
        return vars.getOrDefault(name, 0L);
    }

    public void setVariable(String name, long value) {
        vars.put(name, value);
    }
}

//เช็คว่า strategy done?
// game state now?

/*implement GameAPI ยังไงก็ได้ตาม game logic (hex / grid / combat)
*/
