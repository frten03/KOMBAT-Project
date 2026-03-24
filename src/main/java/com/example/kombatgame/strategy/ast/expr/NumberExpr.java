package com.example.kombatgame.strategy.ast.expr;

import com.example.kombatgame.strategy.ast.core.Expression;
import com.example.kombatgame.strategy.eval.EvalContext;

public class NumberExpr implements Expression {
    private final long value;

    public NumberExpr(long value) {
        this.value = value;
    }

    public long eval(EvalContext ctx) {
        return value;
    }
}
