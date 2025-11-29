package it.unibo.tetraj.model;

import java.awt.Color;

/**
 * Enumeration of all Tetromino types in Tetris.
 * Each type has a specific shape, color, and rotation states.
 */
public enum TetrominoType {

    /**
     * I-piece (cyan) - straight line.
     */
    I(new int[][][] {
        {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}},
        {{0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}},
        {{0, 0, 0, 0}, {0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}},
        {{0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}},
    }, Color.CYAN),

    /**
     * O-piece (yellow) - square.
     */
    O(new int[][][] {
        {{1, 1}, {1, 1}},
        {{1, 1}, {1, 1}},
        {{1, 1}, {1, 1}},
        {{1, 1}, {1, 1}},
    }, Color.YELLOW),

    /**
     * T-piece (purple) - T shape.
     */
    T(new int[][][] {
        {{0, 1, 0}, {1, 1, 1}, {0, 0, 0}},
        {{0, 1, 0}, {0, 1, 1}, {0, 1, 0}},
        {{0, 0, 0}, {1, 1, 1}, {0, 1, 0}},
        {{0, 1, 0}, {1, 1, 0}, {0, 1, 0}},
    }, Color.MAGENTA),

    /**
     * S-piece (green) - S shape.
     */
    S(new int[][][] {
        {{0, 1, 1}, {1, 1, 0}, {0, 0, 0}},
        {{0, 1, 0}, {0, 1, 1}, {0, 0, 1}},
        {{0, 0, 0}, {0, 1, 1}, {1, 1, 0}},
        {{1, 0, 0}, {1, 1, 0}, {0, 1, 0}},
    }, Color.GREEN),

    /**
     * Z-piece (red) - Z shape.
     */
    Z(new int[][][] {
        {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}},
        {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}},
        {{0, 0, 0}, {1, 1, 0}, {0, 1, 1}},
        {{0, 1, 0}, {1, 1, 0}, {1, 0, 0}},
    }, Color.RED),

    /**
     * J-piece (blue) - J shape.
     */
    J(new int[][][] {
        {{1, 0, 0}, {1, 1, 1}, {0, 0, 0}},
        {{0, 1, 1}, {0, 1, 0}, {0, 1, 0}},
        {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}},
        {{0, 1, 0}, {0, 1, 0}, {1, 1, 0}},
    }, Color.BLUE),

    /**
     * L-piece (orange) - L shape.
     */
    L(new int[][][] {
        {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}},
        {{0, 1, 0}, {0, 1, 0}, {0, 1, 1}},
        {{0, 0, 0}, {1, 1, 1}, {1, 0, 0}},
        {{1, 1, 0}, {0, 1, 0}, {0, 1, 0}},
    }, Color.ORANGE);

    private final int[][][] shapes;
    private final Color color;

    /**
     * Creates a tetromino type with its rotation shapes and color.
     *
     * @param shapes The 4 rotation states of the piece
     * @param color The color of the piece
     */
    TetrominoType(final int[][][] shapes, final Color color) {
        this.shapes = shapes;
        this.color = color;
    }

    /**
     * Gets the shape matrix for a specific rotation.
     *
     * @param rotation The rotation index (0-3)
     * @return The shape matrix
     */
    public int[][] getShape(final int rotation) {
        return shapes[rotation % 4];
    }

    /**
     * Gets the color of this tetromino type.
     *
     * @return The color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets a random tetromino type.
     *
     * @return A random tetromino type
     */
    public static TetrominoType random() {
        final TetrominoType[] types = values();
        return types[(int) (Math.random() * types.length)];
    }
}
