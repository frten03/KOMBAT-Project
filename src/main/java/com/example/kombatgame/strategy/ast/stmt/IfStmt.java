package com.example.kombatgame.strategy.ast.stmt;

import com.example.kombatgame.strategy.ast.core.Expression;
import com.example.kombatgame.strategy.ast.core.Statement;
import com.example.kombatgame.strategy.eval.EvalContext;

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
