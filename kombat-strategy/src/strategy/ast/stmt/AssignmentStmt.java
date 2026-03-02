package strategy.ast.stmt;

import strategy.ast.core.Statement;
import strategy.ast.core.Expression;
import strategy.eval.EvalContext;

public class AssignmentStmt implements Statement {

    private final String name;
    private final Expression expr;

    public AssignmentStmt(String name, Expression expr) {
        this.name = name;
        this.expr = expr;
    }

    @Override
    public void eval(EvalContext ctx) {
        long value = expr.eval(ctx);
        ctx.setVariable(name, value);
    }
}