package strategy.ast.stmt;

import strategy.ast.core.Expression;
import strategy.ast.core.Statement;
import strategy.eval.EvalContext;

public class IfStmt implements Statement {
    private final Expression cond;
    private final Statement thenStmt;

    public IfStmt(Expression cond, Statement thenStmt) {
        this.cond = cond;
        this.thenStmt = thenStmt;
    }

    public void eval(EvalContext ctx) {
        if (cond.eval(ctx) > 0 && !ctx.isEvaluationStopped())
            thenStmt.eval(ctx);
    }
}
