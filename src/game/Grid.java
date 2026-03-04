package game;

import java.util.ArrayList;
import java.util.List;

public class Grid {

    private final int size;
    private final Minion[][] board;

    public Grid(int size) {
        this.size = size;
        this.board = new Minion[size+1][size+1]; // เริ่ม index ที่ 1
    }

    public boolean inBounds(int r, int c) {
        return r >= 1 && r <= size && c >= 1 && c <= size;
    }

    public boolean isOccupied(int r, int c) {
        return board[r][c] != null;
    }

    public Minion getMinion(int r, int c) {
        return board[r][c];
    }

    public void addMinion(Minion m) {
        board[m.getRow()][m.getCol()] = m;
    }

    public void moveMinion(Minion m, int nr, int nc) {
        board[m.getRow()][m.getCol()] = null;
        m.setPosition(nr, nc);
        board[nr][nc] = m;
    }

    public void removeMinion(Minion m) {
        board[m.getRow()][m.getCol()] = null;
    }

    public List<Minion> getAllMinions() {
        List<Minion> list = new ArrayList<>();
        for (int r = 1; r <= size; r++) {
            for (int c = 1; c <= size; c++) {
                if (board[r][c] != null) {
                    list.add(board[r][c]);
                }
            }
        }
        return list;
    }

    public boolean isEmpty(int r, int c) {
        return board[r][c] == null;
    }
}
