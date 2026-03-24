package com.example.kombatgame.strategy.ast.core;

import com.example.kombatgame.strategy.eval.EvalContext;

public interface Expression extends ASTNode {
    long eval(EvalContext ctx);
}
