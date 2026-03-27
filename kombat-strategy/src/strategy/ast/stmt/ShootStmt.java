package strategy.ast.stmt;

import game.GameLogic;
import game.RealGameAPI;
import strategy.ast.Direction;
import strategy.ast.core.Expression;
import strategy.eval.ActionLog;
import strategy.eval.EvalContext;

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
