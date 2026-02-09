package strategy.eval;

public class EvalContext {
    private boolean done = false;
    public final GameAPI api;

    public EvalContext(GameAPI api) {
        this.api = api;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone() {
        this.done = true;
    }
}


/*implement GameAPI ยังไงก็ได้ตาม game logic (hex / grid / combat)
*/