package com.example.kombatgame.strategy.ast.core;

import com.example.kombatgame.strategy.eval.EvalContext;

public interface Statement extends ASTNode { // เปลี่ยน state
    void eval(EvalContext ctx);
}
