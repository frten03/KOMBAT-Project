package strategy.ast.core;

import strategy.eval.EvalContext;

public interface Statement extends ASTNode {
    void eval(EvalContext ctx);
}
