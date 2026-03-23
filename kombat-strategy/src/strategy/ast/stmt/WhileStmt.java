package strategy.ast.stmt;

import strategy.ast.core.Expression;
import strategy.ast.core.Statement;
import strategy.eval.EvalContext;

public class WhileStmt implements Statement {
    private final Expression cond;
    private final Statement body;

    public WhileStmt(Expression cond, Statement body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void eval(EvalContext ctx) {

        int counter = 0;

        while (!ctx.isDone()) {

            long condition = cond.eval(ctx);
            if (ctx.isDone()) return;

            if (condition <= 0) return;

            if (counter >= 10000) {
                ctx.terminate(EvalContext.TerminationReason.LOOP_LIMIT);
                return;
            }

            body.eval(ctx);
            counter++;
        }
    }
}
