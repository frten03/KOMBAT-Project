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

    public void eval(EvalContext ctx) {
        int count = 0;
        while (cond.eval(ctx) > 0 && count < 10000 && !ctx.isEvaluationStopped()) {
            body.eval(ctx);
            count++;
        }
    }
}
