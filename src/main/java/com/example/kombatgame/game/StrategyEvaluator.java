package com.example.kombatgame.game;

import com.example.kombatgame.strategy.ast.core.Statement;
import com.example.kombatgame.strategy.eval.EvalContext;
import com.example.kombatgame.strategy.eval.GameAPI;
import com.example.kombatgame.strategy.parser.Parser;
import com.example.kombatgame.strategy.tokenizer.Tokenizer;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
public class StrategyEvaluator {

    public void evaluate(Minion m, GameState state) {
        try {
            String strategy = m.getType().getStrategySource();

            Parser parser = new Parser(new Tokenizer(strategy));
            Statement stmt = parser.parseStrategy();

            GameLogic logic = new GameLogic(
                    state.getBoard(),
                    state.getPlayer1(),
                    state.getPlayer2(),
                    state.getConfig()
            );
            GameAPI api = new RealGameAPI(logic, m);
            EvalContext ctx = new EvalContext(api, new HashMap<>());

            stmt.eval(ctx);
        } catch (Exception e) {
            System.out.println("Strategy error: " + e.getMessage());
        }
    }
}