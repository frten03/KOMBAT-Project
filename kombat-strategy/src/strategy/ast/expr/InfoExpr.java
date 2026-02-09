package strategy.ast.expr;

import strategy.ast.Direction;
import strategy.ast.core.Expression;
import strategy.eval.EvalContext;

public class InfoExpr implements Expression {
    public InfoExpr(Kind kind, Direction dir) {
        this.kind = kind;
        this.dir = dir;
    }

    public enum Kind { OPPONENT, NEARBY, ALLY }
    private final Kind kind;
    private final Direction dir;

    @Override
    public long eval(EvalContext ctx) {
        if (kind == Kind.OPPONENT)
            return ctx.api.opponent();   // ใช้ของจริงจากเกม
        return ctx.api.nearby(dir);
    }
}

