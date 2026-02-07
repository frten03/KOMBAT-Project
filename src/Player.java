import java.util.ArrayList;
import java.util.List;

public class Player {
    private long budget;
    private List<Minion> minions;

    public Player(long init_budget) {
        if (init_budget < 0) {
            throw new IllegalArgumentException("Budget must be non-negative");
        }
        this.budget = init_budget;
        this.minions = new ArrayList<>();
    }

    public long getBudget() {
        return budget;
    }

    public boolean spendBudget(long cost) {
        if (budget < cost) return true;
        budget -= cost;
        return false;
    }

    public void decreaseBudget(long amount) {
        budget -= amount;
        if (budget < 0)
            budget = 0;
    }

    public void addMinion(Minion m) {
        minions.add(m);
    }

    public List<Minion> getMinions() {
        return minions;
    }
}
