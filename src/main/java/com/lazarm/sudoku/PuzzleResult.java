package com.lazarm.sudoku;

/**
 * Response payload returned by the REST endpoint.
 *
 * JSON shape (identical to the Lambda response the frontend already consumes):
 * {
 *   "puzzle":   [[int x9] x9],
 *   "solution": [[int x9] x9]
 * }
 */
public record PuzzleResult(int[][] puzzle, int[][] solution) {}
