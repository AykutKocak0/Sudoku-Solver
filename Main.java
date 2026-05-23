
public class Main {
    public static void main(String[] args) {
        // int[][] sudoku = {
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0},
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0},
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0},
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0},
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0},
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0},
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0},
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0},
        //         {0, 0, 0, 0, 0, 0, 0, 0, 0}
        // };
        int[][] sudoku = new int[9][9];
        String numbers = "260008000080009600000050000094300500002070000050000804035800000000000301700060000";
        sudoku = stringToArray(numbers);


        SudokuSolver ss = new SudokuSolver(sudoku);
        ss.solve();
    }
    private static int[][] stringToArray(String numbers) {
        int[][] sudokuArray = new int[9][9];
        int index = 0; 
        
        for (char c : numbers.toCharArray()) {
            if (index >= 81) break; 
            
            int row = index / 9;
            int column = index % 9;
            
            sudokuArray[row][column] = Character.getNumericValue(c);
            
            index++; 
        }
        
        return sudokuArray;
    }

}
