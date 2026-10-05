package com.lazarm.sudoku;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the Sudoku puzzle generation endpoint.
 *
 * Mirrors the Lambda route:  GET /puzzle?difficulty={easy|mid|hard}
 * Exposed here as:           GET /api/puzzle?difficulty={easy|mid|hard}
 *
 * Response JSON (identical to what the frontend fetch() call expects):
 * {
 *   "puzzle":   [[int x9] x9],
 *   "solution": [[int x9] x9]
 * }
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class SudokuController {

    private final SudokuService sudokuService;

    public SudokuController(SudokuService sudokuService) {
        this.sudokuService = sudokuService;
    }

    /**
     * GET /api/puzzle?difficulty=easy|mid|hard
     *
     * @param difficulty puzzle difficulty; defaults to "easy" when omitted
     * @return 200 OK with a {@link PuzzleResult} serialised as JSON
     */
    @GetMapping("/puzzle")
    public ResponseEntity<PuzzleResult> getPuzzle(
            @RequestParam(defaultValue = "easy") String difficulty) {

        PuzzleResult result = sudokuService.generate(difficulty);
        return ResponseEntity.ok(result);
    }
}
