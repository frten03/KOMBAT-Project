package com.example.kombatgame.strategy.ast.stmt;

import com.example.kombatgame.strategy.ast.core.Statement;
import com.example.kombatgame.strategy.ast.core.Expression;
import com.example.kombatgame.strategy.eval.EvalContext;

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
        if(ctx.isSpecial(name)){
            return;
        }
        ctx.setVariable(name, value);
    }
}