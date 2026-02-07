package game;

import java.util.HashMap;
import java.util.Map;

public class Grid {
    private static final int SIZE = 8;
    private final Map<String, Minion> cells = new HashMap<>();

    private String key(int r, int c) {
        return r + "," + c;
    }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    public boolean isOccupied(int r, int c) {
        return cells.containsKey(key(r, c));
    }

    public Minion getMinion(int r, int c) {
        return cells.get(key(r, c));
    }

    public void addMinion(Minion m) {
        cells.put(key(m.getRow(), m.getCol()), m);
    }

    public void moveMinion(Minion m, int nr, int nc) {
        cells.remove(key(m.getRow(), m.getCol()));
        m.setPosition(nr, nc);
        cells.put(key(nr, nc), m);
    }

    public void removeMinion(Minion m) {
        cells.remove(key(m.getRow(), m.getCol()));
    }
}
