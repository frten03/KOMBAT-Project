package com.example.kombatgame.strategy.ast.stmt;

import com.example.kombatgame.strategy.ast.core.Statement;
import com.example.kombatgame.strategy.eval.EvalContext;
import java.util.List;

public class BlockStmt implements Statement {
    private final List<Statement> statements;

    public BlockStmt(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() { return statements; }

    public void eval(EvalContext ctx) {
        for (Statement s : statements) {
            if (ctx.isDone()) return;
            s.eval(ctx);
        }
    }
}
