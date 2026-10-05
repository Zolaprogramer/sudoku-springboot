package com.lazarm.sudoku;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Core Sudoku generation service.
 *
 * Logic ported 1-to-1 from the Lambda JavaScript/Python implementation:
 *   - solve()       backtracking solver with per-cell random digit ordering
 *   - isValid()     row / column / 3x3-box constraint check
 *   - generate()    fills a board, deep-copies it as the solution, then removes
 *                   cells according to the requested difficulty level
 *
 * Clue counts (cells that remain visible):
 *   easy → 36 clues  (45 cells removed)
 *   mid  → 28 clues  (53 cells removed)
 *   hard → 22 clues  (59 cells removed)
 */
@Service
public class SudokuService {

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Generates a puzzle/solution pair for the requested difficulty.
     *
     * @param difficulty "easy", "mid", or "hard" (case-insensitive); defaults to "easy"
     * @return a {@link PuzzleResult} containing both the masked puzzle and the full solution
     */
    public PuzzleResult generate(String difficulty) {
        int[][] board = emptyBoard();
        solve(board);

        int[][] solution = deepCopy(board);

        int clues = clueCount(difficulty);
        int cellsToRemove = 81 - clues;

        // Shuffle all 81 cell indices then blank the first N — identical to the
        // Python: random.shuffle(cells); for i in cells[:remove]: board[i//9][i%9] = 0
        List<Integer> indices = new ArrayList<>(81);
        for (int i = 0; i < 81; i++) indices.add(i);
        Collections.shuffle(indices);

        for (int i = 0; i < cellsToRemove; i++) {
            int idx = indices.get(i);
            board[idx / 9][idx % 9] = 0;
        }

        return new PuzzleResult(board, solution);
    }

    // -----------------------------------------------------------------------
    // Core solver
    // -----------------------------------------------------------------------

    /**
     * Fills {@code board} in-place using a randomised backtracking solver.
     * Digits 1-9 are shuffled before trying each empty cell, producing a
     * different completed grid every invocation.
     *
     * @return true when the board is completely and validly filled
     */
    boolean solve(int[][] board) {
        for (int i = 0; i < 81; i++) {
            int row = i / 9;
            int col = i % 9;

            if (board[row][col] != 0) continue;

            List<Integer> digits = shuffledDigits();
            for (int n : digits) {
                if (isValid(board, row, col, n)) {
                    board[row][col] = n;
                    if (solve(board)) return true;
                    board[row][col] = 0;
                }
            }
            return false;   // no digit fits → backtrack
        }
        return true;        // all 81 cells filled
    }

    /**
     * Returns true when placing {@code num} at (row, col) violates no
     * Sudoku constraint (row uniqueness, column uniqueness, 3x3-box uniqueness).
     */
    boolean isValid(int[][] board, int row, int col, int num) {
        // Row check
        for (int c = 0; c < 9; c++) {
            if (board[row][c] == num) return false;
        }

        // Column check
        for (int r = 0; r < 9; r++) {
            if (board[r][col] == num) return false;
        }

        // 3x3 box check
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if (board[r][c] == num) return false;
            }
        }

        return true;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private int clueCount(String difficulty) {
        if (difficulty == null) return 36;
        return switch (difficulty.toLowerCase()) {
            case "mid"  -> 28;
            case "hard" -> 22;
            default     -> 36;   // "easy" and anything unrecognised
        };
    }

    private int[][] emptyBoard() {
        return new int[9][9];
    }

    private int[][] deepCopy(int[][] src) {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++) {
            System.arraycopy(src[r], 0, copy[r], 0, 9);
        }
        return copy;
    }

    private List<Integer> shuffledDigits() {
        List<Integer> digits = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(digits);
        return digits;
    }
}
