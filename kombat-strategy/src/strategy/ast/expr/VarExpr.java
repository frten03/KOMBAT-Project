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
        return ctx.getVariable(name);
    }
}