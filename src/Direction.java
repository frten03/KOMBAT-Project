public enum Direction {
    UP(-1, 0),
    UPRIGHT(-1, +1),
    DOWNRIGHT(0, +1),
    DOWN(+1, 0),
    DOWNLEFT(+1, -1),
    UPLEFT(0, -1);

    public final int dr;
    public final int dc;

    Direction(int dr, int dc) {
        this.dr = dr;
        this.dc = dc;
    }
}
