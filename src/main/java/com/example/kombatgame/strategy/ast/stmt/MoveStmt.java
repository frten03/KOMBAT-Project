package com.example.kombatgame.strategy.ast.stmt;

import com.example.kombatgame.strategy.ast.Direction;
import com.example.kombatgame.strategy.eval.ActionLog;
import com.example.kombatgame.strategy.eval.EvalContext;

public class MoveStmt extends ActionStmt {
    private final Direction dir;

    public MoveStmt(Direction dir) {
        this.dir = dir;
    }

    @Override
    public void eval(EvalContext ctx) {

        if (ctx.isDone()) return;

        long budget = ctx.api.getBudget();

        if (budget < 1) {
            ctx.terminate(EvalContext.TerminationReason.BUDGET_INSUFFICIENT);
            return;
        }

        ctx.api.move(dir);

        ctx.getResult().addAction(
                new ActionLog(ActionLog.Type.MOVE, dir, 0)
        );
    }
}
