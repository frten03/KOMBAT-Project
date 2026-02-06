package strategy.ast.stmt;

import strategy.ast.Direction;
import strategy.eval.EvalContext;

public class MoveStmt extends ActionStmt {
    private final Direction dir;

    public MoveStmt(Direction dir) {
        this.dir = dir;
    }

    @Override
    public void eval(EvalContext ctx) {
        ctx.api.move(dir);
    }
}
