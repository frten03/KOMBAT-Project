package strategy.ast.stmt;

import strategy.ast.core.Statement;
import strategy.eval.EvalContext;

public class DoneStmt implements Statement {
    @Override
    public void eval(EvalContext ctx) {
        ctx.setDone();
    }
}
