package game;

import java.util.HashMap;
import java.util.Map;

public class Grid {
    private static final int SIZE = 8;
    private final Map<String, Minion> hexs = new HashMap<>();

    private String key(int r, int c) {
        return r + "," + c;
    }

    public boolean inBounds(int r, int c) {
        return r >= 1 && r <= SIZE && c >= 1 && c <= SIZE;
    }

    public boolean isOccupied(int r, int c) {
        return hexs.containsKey(key(r, c));
    }

    public Minion getMinion(int r, int c) {
        return hexs.get(key(r, c));
    }

    public void addMinion(Minion m) {
        hexs.put(key(m.getRow(), m.getCol()), m);
    }

    public void moveMinion(Minion m, int nr, int nc) {
        hexs.remove(key(m.getRow(), m.getCol()));
        m.setPosition(nr, nc);
        hexs.put(key(nr, nc), m);
    }

    public void removeMinion(Minion m) {
        String k = key(m.getRow(), m.getCol());
        if (hexs.get(k) == m) {
            hexs.remove(k);
        }
    }

    public Iterable<Minion> getAllMinions() {
        return hexs.values();
    }
}
