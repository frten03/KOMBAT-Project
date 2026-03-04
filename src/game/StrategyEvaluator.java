package game;

import strategy.ast.core.Statement;
import strategy.eval.EvalContext;
import strategy.eval.GameAPI;
import strategy.parser.Parser;
import strategy.tokenizer.Tokenizer;

import java.util.HashMap;

public class StrategyEvaluator {

    public void evaluate(Minion m, GameState state) {
        try {
            String strategy = m.getType().getStrategySource();

            Parser parser = new Parser(new Tokenizer(strategy));
            Statement stmt = parser.parseStatement();

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