package game;

public class Minion {
    private int row;
    private int col;
    private int hp;
    private int defense;
    private final Player owner;
    private MinionType type;

    public Minion(int row, int col, int hp, int defense, Player owner) {
        if (hp <= 0) {
            throw new IllegalArgumentException("HP must be positive");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        this.row = row;
        this.col = col;
        this.hp = hp;
        this.defense = defense;
        this.owner = owner;
    }

    public Minion(int row, int col, int hp,
                  MinionType type, Player owner) {

        this(row, col, hp, type.getDefense(), owner);
        this.type = type;
    }

    public MinionType getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getHp() {
        return hp;
    }

    public int getDefense() {
        return defense;
    }

    public Player getOwner() {
        return owner;
    }

    public void setPosition(int r, int c) {
        this.row = r;
        this.col = c;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int expenditure) {
        int reduced = Math.max(1, expenditure - getDefense());
        hp = Math.max(0, hp - reduced);
    }

    @Override
    public String toString() {
        return "game.Minion{" +
                "pos=(" + row + "," + col + ")" +
                ", hp=" + hp +
                ", def=" + defense +
                ", ownerBudget=" + owner.getBudget() +
                '}';
    }

}
