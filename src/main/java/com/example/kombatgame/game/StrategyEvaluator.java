package com.example.kombatgame.game;

import com.example.kombatgame.strategy.ast.core.Statement;
import com.example.kombatgame.strategy.eval.ActionResult;
import com.example.kombatgame.strategy.eval.EvalContext;
import com.example.kombatgame.strategy.eval.GameAPI;
import com.example.kombatgame.strategy.parser.Parser;
import com.example.kombatgame.strategy.tokenizer.Tokenizer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class StrategyEvaluator {

    private final Map<String, Statement> strategyCache = new ConcurrentHashMap<>();

    public ActionResult evaluate(Minion m, GameState state) {
        try {
            String strategy = m.getType().getStrategySource();

            Statement stmt = strategyCache.computeIfAbsent(strategy,
                    s -> new Parser(new Tokenizer(s)).parseStrategy());

            GameLogic logic = new GameLogic(
                    state.getBoard(),
                    state.getPlayer1(),
                    state.getPlayer2(),
                    state.getConfig()
            );
            GameAPI api = new RealGameAPI(logic, m);
            EvalContext ctx = new EvalContext(api, new HashMap<>());

            stmt.eval(ctx);
            return ctx.getResult();
        } catch (Exception e) {
            System.out.println("Strategy error: " + e.getMessage());
            return new ActionResult();
        }
    }
}