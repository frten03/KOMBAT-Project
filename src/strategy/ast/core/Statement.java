package strategy.ast.core;

import strategy.eval.EvalContext;

public interface Statement extends ASTNode { // เปลี่ยน state
    void eval(EvalContext ctx);
}
