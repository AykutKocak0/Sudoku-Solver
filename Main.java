
public class Main {
    public static void main(String[] args) {
        int[][] sudoku = {
                {3, 0, 0, 0, 4, 9, 0, 0, 0},
                {0, 0, 0, 6, 0, 0, 5, 0, 1},
                {7, 5, 2, 0, 0, 1, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 7, 0, 0},
                {5, 0, 0, 3, 9, 6, 0, 0, 0},
                {0, 0, 8, 1, 5, 0, 0, 9, 6},
                {0, 0, 3, 0, 1, 0, 0, 6, 0},
                {0, 0, 4, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 2, 8, 0, 0, 0}
        };


        SudokuSolver ss = new SudokuSolver(sudoku);
        ss.solve();
        }
    }
