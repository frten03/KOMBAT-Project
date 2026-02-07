public class Demo {
    public static void main(String[] args) {

        System.out.println("=== GAME START ===");

        // ----- Board -----
        Grid board = new Grid();

        // ----- Players -----
        Player p1 = new Player(20);
        Player p2 = new Player(20);

        // ----- Minions -----
        Minion m1 = new Minion(3, 3, 10, 1, p1);
        Minion m2 = new Minion(2, 3, 6, 0, p2);

        p1.addMinion(m1);
        p2.addMinion(m2);

        board.addMinion(m1);
        board.addMinion(m2);

        // ----- Print Initial State -----
        System.out.println("Initial State:");
        System.out.println(m1);
        System.out.println(m2);
        System.out.println("P1 budget = " + p1.getBudget());
        System.out.println("P2 budget = " + p2.getBudget());
        System.out.println("------------------");

        // ----- Core Logic -----
        GameLogic logic = new GameLogic(board);

        // ----- Evaluator Context -----
        EvaluatorContext ctx = new EvaluatorContext(m1, logic);

        // ----- Evaluator -----
        Evaluator evaluator = new Evaluator(ctx);

        // ----- Run example strategy -----
        evaluator.evalExample();

        // ----- Print Final State -----
        System.out.println("------------------");
        System.out.println("Final State:");
        System.out.println(m1);
        System.out.println(m2);
        System.out.println("P1 budget = " + p1.getBudget());
        System.out.println("P2 budget = " + p2.getBudget());

        System.out.println("=== GAME END ===");
    }
}
