package strategy.ast.stmt;

import strategy.ast.core.Expression;
import strategy.ast.core.Statement;
import strategy.eval.EvalContext;

public class IfStmt implements Statement {
    private final Expression cond;
    private final Statement thenStmt;
    private final Statement elseStmt;

    public IfStmt(Expression cond, Statement thenStmt, Statement elseStmt) {
        this.cond = cond;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    @Override
    public void eval(EvalContext ctx) {
        if (ctx.isDone()) return;

        if (cond.eval(ctx) > 0) {
            thenStmt.eval(ctx);
        } else {
            elseStmt.eval(ctx);
        }
    }
}
