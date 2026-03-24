package com.example.kombatgame.strategy.ast.stmt;

import com.example.kombatgame.strategy.ast.core.Statement;
import com.example.kombatgame.strategy.eval.EvalContext;

public class DoneStmt implements Statement {
    @Override
    public void eval(EvalContext ctx) {
        ctx.setDone();
    }
}
