package com.example.kombatgame.game;

import java.util.ArrayList;
import java.util.List;

public class Grid {

    private final int size;
    private final Minion[][] board;
    private List<Minion> cachedMinions = null;

    public Grid(int size) {
        this.size = size;
        this.board = new Minion[size][size];
    }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }

    public boolean isOccupied(int r, int c) {
        return board[r][c] != null;
    }

    public Minion getMinion(int r, int c) {
        return board[r][c];
    }

    public void addMinion(Minion m) {
        board[m.getRow()][m.getCol()] = m;
        cachedMinions = null;
    }

    public void moveMinion(Minion m, int nr, int nc) {
        board[m.getRow()][m.getCol()] = null;
        m.setPosition(nr, nc);
        board[nr][nc] = m;
        cachedMinions = null;
    }

    public void removeMinion(Minion m) {
        board[m.getRow()][m.getCol()] = null;
        cachedMinions = null;
    }

    public List<Minion> getAllMinions() {
        if (cachedMinions == null) {
            cachedMinions = new ArrayList<>();
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if (board[r][c] != null) {
                        cachedMinions.add(board[r][c]);
                    }
                }
            }
        }
        return new ArrayList<>(cachedMinions);
    }

    public boolean isEmpty(int r, int c) {
        return board[r][c] == null;
    }
}
