package strategy.ast.expr;

import strategy.ast.core.Expression;
import strategy.eval.EvalContext;

public class VarExpr implements Expression {

    private final String name;

    public VarExpr(String name) {
        this.name = name;
    }

    @Override
    public long eval(EvalContext ctx) {
        if (ctx.isDone()) return 0;
        return ctx.resolveIdentifier(name);
    }
}