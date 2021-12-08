package org.cis120.Othello;

import java.util.LinkedHashSet;

public class ValidMove implements Comparable<ValidMove> {
    private final int x;
    private final int y;
    private final LinkedHashSet<int[]> flippedDisks;

    public ValidMove(int xParam, int yParam, LinkedHashSet<int[]> flippedParam) {
        x = xParam;
        y = yParam;
        flippedDisks = flippedParam;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public LinkedHashSet<int[]> getFlippedDisks() {
        return new LinkedHashSet<>(flippedDisks);
    }

    @Override
    public int compareTo(ValidMove o) {
        if (this.x > o.getX()) {
            return 1;
        } else if (this.x < o.getX()) {
            return -1;
        } else {
            return Integer.compare(this.y, o.getY());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != ValidMove.class) {
            return false;
        }
        ValidMove v = (ValidMove) obj;
        return this.x == v.getX() && this.y == v.getY();
    }

}
