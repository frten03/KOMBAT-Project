package strategy.ast.expr;

import strategy.ast.core.Expression;
import strategy.eval.EvalContext;

public class NumberExpr implements Expression {
    private final long value;

    public NumberExpr(long value) {
        this.value = value;
    }

    public long eval(EvalContext ctx) {
        return value;
    }
}
