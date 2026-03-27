package com.example.kombatgame.strategy.ast.stmt;

import com.example.kombatgame.game.GameLogic;
import com.example.kombatgame.game.RealGameAPI;
import com.example.kombatgame.strategy.ast.Direction;
import com.example.kombatgame.strategy.ast.core.Expression;
import com.example.kombatgame.strategy.eval.ActionLog;
import com.example.kombatgame.strategy.eval.EvalContext;

public class ShootStmt extends ActionStmt {
    private final Direction dir;
    private final Expression cost;

    public ShootStmt(Direction dir, Expression cost) {
        this.dir = dir;
        this.cost = cost;
    }

    @Override
    public void eval(EvalContext ctx) {
        if (ctx.isDone()) return;

        long c = cost.eval(ctx);
        if (ctx.isDone()) return;

        long totalCost = c + 1;
        long budget = ctx.api.getBudget();

        if (budget < totalCost) {
            return; // NO-OP, ไม่ terminate
        }

        ctx.api.shoot(dir, c);

        Object rawResult = ctx.api.getLastShootResult();
        if (rawResult instanceof GameLogic.ShootResult shootResult && shootResult.hit) {
            ctx.getResult().addAction(
                    new ActionLog(
                            ActionLog.Type.SHOOT,
                            dir,
                            c,
                            shootResult.target.getOwner().getId(),
                            shootResult.target.getMinionNumber(),
                            shootResult.damageDealt,
                            shootResult.targetHpAfter,
                            shootResult.killed
                    )
            );
        } else {
            ctx.getResult().addAction(
                    new ActionLog(ActionLog.Type.SHOOT, dir, c)
            );
        }
    }
}
