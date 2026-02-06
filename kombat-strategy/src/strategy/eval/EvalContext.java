package strategy.eval;

public class EvalContext {
    private boolean evaluationStopped = false;
    public final GameAPI api;

    public EvalContext(GameAPI api) {
        this.api = api;
    }

    public boolean isEvaluationStopped() {
        return evaluationStopped;
    }

    public void stopEvaluation() {
        this.evaluationStopped = true;
    }
}

/*implement GameAPI ยังไงก็ได้ตาม game logic (hex / grid / combat)
*/