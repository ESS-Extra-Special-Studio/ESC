package uk.co.extraspecialstudio.extraspecial.esc.ui;

import java.util.Arrays;

/**
 * Grid and weighted-cell layout on top of {@link EscRect#splitRows} / {@link EscRect#splitColumns}.
 * Use for keyboard diagrams, icon grids, and any UI that divides a parent rect into cells.
 */
public final class EscGridLayout {

    private EscGridLayout() {
    }

    public static float[] equalWeights(int count) {
        if (count <= 0) {
            return new float[0];
        }
        float[] weights = new float[count];
        Arrays.fill(weights, 1f);
        return weights;
    }

    /** Split a row rect into weighted column cells. */
    public static EscRect[] rowCells(EscRect row, float[] columnWeights, int gap) {
        if (row == null || columnWeights == null || columnWeights.length == 0) {
            return new EscRect[0];
        }
        return row.splitColumns(columnWeights, gap);
    }

    /** Split a column rect into weighted row cells. */
    public static EscRect[] columnCells(EscRect column, float[] rowWeights, int gap) {
        if (column == null || rowWeights == null || rowWeights.length == 0) {
            return new EscRect[0];
        }
        return column.splitRows(rowWeights, gap);
    }

    /** Equal row bands within a parent rect. */
    public static EscRect[] rows(EscRect parent, int count, int gap) {
        if (parent == null || count <= 0) {
            return new EscRect[0];
        }
        return parent.splitRows(equalWeights(count), gap);
    }

    /** Equal column bands within a parent rect. */
    public static EscRect[] columns(EscRect parent, int count, int gap) {
        if (parent == null || count <= 0) {
            return new EscRect[0];
        }
        return parent.splitColumns(equalWeights(count), gap);
    }

    /**
     * Equal {@code columns}×{@code rows} grid. {@code grid[row][col]}.
     */
    public static EscRect[][] grid(EscRect parent, int columns, int rows, int gap) {
        if (parent == null || columns <= 0 || rows <= 0) {
            return new EscRect[0][0];
        }
        EscRect[] rowRects = rows(parent, rows, gap);
        EscRect[][] out = new EscRect[rows][columns];
        for (int r = 0; r < rows; r++) {
            out[r] = rowCells(rowRects[r], equalWeights(columns), gap);
        }
        return out;
    }
}
