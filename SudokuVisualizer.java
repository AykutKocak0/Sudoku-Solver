public class SudokuVisualizer {

    public static void initializeGrid(int[][] grid) {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0) {
                System.out.println("+-----------+-----------+-----------+");
            }
            for (int j = 0; j < 9; j++) {
                if(grid[i][j] == 0) {
                System.out.print("|   ");}
                else {
                    System.out.print("| "+grid[i][j]+" ");
                }
            }
            System.out.println("|");
            System.out.println("+-----------+-----------+-----------+");
        }

        System.out.print("\033[H");
    }

    public static void updateCell(int row, int col, int value) {
        int extraSeparators = 0;
        if (row >= 3) extraSeparators++;
        if (row >= 6) extraSeparators++;

        int terminalRow = 2 * row + 2 + extraSeparators;
        int terminalCol = 3 + col * 4;

        System.out.printf("\033[%d;%dH", terminalRow, terminalCol);

        if (value == 0) {
            System.out.print(".");
        } else {
            System.out.print(value);
        }
        System.out.flush();

        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void finish() {
        System.out.print("\033[23;1H");
    }
}
