package com.example.kombatgame.strategy.ast.expr;

import com.example.kombatgame.strategy.ast.core.Expression;
import com.example.kombatgame.strategy.eval.EvalContext;

public class CompareExpr implements Expression {

    private final Expression left;
    private final Expression right;

    public CompareExpr(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public long eval(EvalContext ctx) {
        return left.eval(ctx) == right.eval(ctx) ? 1 : 0;
    }
}
