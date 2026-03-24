package com.example.kombatgame.strategy.ast.expr;

import com.example.kombatgame.strategy.ast.core.Expression;
import com.example.kombatgame.strategy.eval.EvalContext;

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