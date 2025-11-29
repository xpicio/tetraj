package it.unibo.tetraj.model;

import java.awt.Color;

/**
 * Represents a tetromino piece with position and rotation.
 * This is a mutable class that tracks the current state of a falling piece.
 */
public final class Tetromino {

    private final TetrominoType type;
    private int x;
    private int y;
    private int rotation;

    /**
     * Creates a new tetromino of the specified type at the spawn position.
     *
     * @param type The type of tetromino
     * @param boardWidth The width of the game board (to center the piece)
     */
    public Tetromino(final TetrominoType type, final int boardWidth) {
        this.type = type;
        this.x = (boardWidth - getWidth()) / 2;
        this.y = 0;
        this.rotation = 0;
    }

    /**
     * Creates a copy of an existing tetromino.
     *
     * @param other The tetromino to copy
     */
    public Tetromino(final Tetromino other) {
        this.type = other.type;
        this.x = other.x;
        this.y = other.y;
        this.rotation = other.rotation;
    }

    /**
     * Moves the tetromino by the specified offset.
     *
     * @param dx X offset
     * @param dy Y offset
     */
    public void move(final int dx, final int dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Rotates the tetromino clockwise.
     */
    public void rotateClockwise() {
        rotation = (rotation + 1) % 4;
    }

    /**
     * Rotates the tetromino counter-clockwise.
     */
    public void rotateCounterClockwise() {
        rotation = (rotation + 3) % 4;
    }

    /**
     * Gets the current shape matrix of the tetromino.
     *
     * @return The shape matrix
     */
    public int[][] getShape() {
        return type.getShape(rotation);
    }

    /**
     * Gets the width of the current shape.
     *
     * @return The width in cells
     */
    public int getWidth() {
        return getShape()[0].length;
    }

    /**
     * Gets the height of the current shape.
     *
     * @return The height in cells
     */
    public int getHeight() {
        return getShape().length;
    }

    /**
     * Gets the X position.
     *
     * @return The X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the Y position.
     *
     * @return The Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the position.
     *
     * @param newX The X coordinate
     * @param newY The Y coordinate
     */
    public void setPosition(final int newX, final int newY) {
        this.x = newX;
        this.y = newY;
    }

    /**
     * Gets the color of this tetromino.
     *
     * @return The color
     */
    public Color getColor() {
        return type.getColor();
    }

    /**
     * Gets the type of this tetromino.
     *
     * @return The tetromino type
     */
    public TetrominoType getType() {
        return type;
    }
}
