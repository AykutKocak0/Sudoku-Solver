import java.util.*;

public class SudokuSolver {
    private int[][] sudoku;
    private Map<List<Integer>, List<Integer>> availablePlaces = new HashMap<>();
    private int guessCount = 0;
    private int correctGuessCount = 0;
    private final List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    public SudokuSolver(int[][] sudoku) {
        this.sudoku = sudoku;

    }
    
    private void setAvailableNumbers() {
        availablePlaces.clear();
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {
                if(sudoku[i][j] == 0){
                    availablePlaces.put(Arrays.asList(i, j), new ArrayList<>(numbers));}
            }
        }
    }
    
    private void checker(int row, int col) {
        List<Integer> candidates = availablePlaces.get(Arrays.asList(row, col));
        if (candidates == null) return;

        for (int i = 0; i < 9; i++) {
            candidates.remove(Integer.valueOf(sudoku[row][i]));
            candidates.remove(Integer.valueOf(sudoku[i][col]));
        }
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int i = boxRowStart; i < boxRowStart + 3; i++) {
            for (int j = boxColStart; j < boxColStart + 3; j++) {
                candidates.remove(Integer.valueOf(sudoku[i][j]));
            }
        }
    }

    private void availableNumberRemover(){
        for(int i = 0; i < sudoku.length; i++){
            for(int j = 0; j < sudoku[i].length; j++){
                if(sudoku[i][j] == 0){
                    checker(i, j);
                }

            }
        }
    }
    
    private void basicSolver() {
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {
                if (sudoku[i][j] == 0 && availablePlaces.get(Arrays.asList(i, j)).size() == 1) {
                    int candidate = availablePlaces.get(Arrays.asList(i, j)).get(0);
                    if (isValidPlacement(i, j, candidate)) {
                        sudoku[i][j] = candidate;
                        SudokuVisualizer.updateCell(i, j, candidate);
                        availablePlaces.remove(Arrays.asList(i, j));
                        availableNumberRemover();
                    }
                }
            }
        }
    }
    
    private void rowHiddenSingle(int row) {
        List<Integer> found = new ArrayList<>();
        for (int i = 0; i < sudoku[row].length; i++) {
            if (sudoku[row][i] == 0) {
                found.addAll(availablePlaces.get(Arrays.asList(row, i)));
            }
        }

        for (int i = 0; i < sudoku[row].length; i++) {
            if (sudoku[row][i] == 0) {
                for (int j : availablePlaces.get(Arrays.asList(row, i))) {
                    if (found.contains(j) &&
                            found.indexOf(j) == found.lastIndexOf(j) &&
                            isValidPlacement(row, i, j)) {
                        sudoku[row][i] = j;
                        SudokuVisualizer.updateCell(row, i, j);
                        availablePlaces.remove(Arrays.asList(row, i));
                        availableNumberRemover();
                        break;
                    }
                }
            }
        }
    }

    private void columnHiddenSingle(int col) {
        List<Integer> found = new ArrayList<>();
        for (int i = 0; i < sudoku.length; i++) {
            if (sudoku[i][col] == 0) {
                found.addAll(availablePlaces.get(Arrays.asList(i, col)));
            }
        }

        for (int i = 0; i < sudoku.length; i++) {
            if (sudoku[i][col] == 0) {
                for (int j : availablePlaces.get(Arrays.asList(i, col))) {
                    if (found.contains(j) &&
                            found.indexOf(j) == found.lastIndexOf(j) &&
                            isValidPlacement(i, col, j)) {
                        sudoku[i][col] = j;
                        SudokuVisualizer.updateCell(i,col,j);
                        availablePlaces.remove(Arrays.asList(i, col));
                        availableNumberRemover();
                        break;
                    }
                }
            }
        }
    }

    private void boxHiddenSingle(int boxRow, int boxCol) {
        int row = boxRow * 3;
        int col = boxCol * 3;

        List<Integer> found = new ArrayList<>();
        for (int i = row; i < row + 3; i++) {
            for (int j = col; j < col + 3; j++) {
                if (sudoku[i][j] == 0) {
                    found.addAll(availablePlaces.get(Arrays.asList(i, j)));
                }
            }
        }

        for (int i = row; i < row + 3; i++) {
            for (int j = col; j < col + 3; j++) {
                if (sudoku[i][j] == 0) {
                    for (int k : availablePlaces.get(Arrays.asList(i, j))) {
                        if (found.contains(k) &&
                                found.indexOf(k) == found.lastIndexOf(k) &&
                                isValidPlacement(i, j, k)) {
                            sudoku[i][j] = k;
                            SudokuVisualizer.updateCell(i,j,k);
                            availablePlaces.remove(Arrays.asList(i, j));
                            availableNumberRemover();
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean isValidPlacement(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (sudoku[row][i] == num || sudoku[i][col] == num) {
                return false;
            }
        }
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int i = boxRowStart; i < boxRowStart + 3; i++) {
            for (int j = boxColStart; j < boxColStart + 3; j++) {
                if (sudoku[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void nakedPairsRow(int row) {
        List<List<Integer>> pairs = new ArrayList<>();
        List<List<Integer>> pairsToRemove = new ArrayList<>();

        for (int i = 0; i < sudoku[row].length; i++) {
            if (sudoku[row][i] == 0) {
                List<Integer> pair = availablePlaces.get(Arrays.asList(row, i));
                if (pair != null && pair.size() == 2) {
                    List<Integer> sortedPair = new ArrayList<>(pair);
                    Collections.sort(sortedPair);

                    boolean foundDuplicate = false;
                    for (List<Integer> existingPair : pairs) {
                        List<Integer> sortedExisting = new ArrayList<>(existingPair);
                        Collections.sort(sortedExisting);
                        if (sortedExisting.equals(sortedPair)) {
                            foundDuplicate = true;
                            break;
                        }
                    }

                    if (foundDuplicate) {
                        boolean alreadyAdded = false;
                        for (List<Integer> pr : pairsToRemove) {
                            List<Integer> sortedPr = new ArrayList<>(pr);
                            Collections.sort(sortedPr);
                            if (sortedPr.equals(sortedPair)) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if (!alreadyAdded) {
                            pairsToRemove.add(pair);
                        }
                    }
                    pairs.add(pair);
                }
            }
        }
        for (int i = 0; i < sudoku[row].length; i++) {
            if (sudoku[row][i] == 0) {
                List<Integer> candidates = availablePlaces.get(Arrays.asList(row, i));
                if (candidates == null) continue;

                for (List<Integer> nakedPair : pairsToRemove) {
                    if (candidates.size() > 2 &&
                            (candidates.contains(nakedPair.get(0)) || candidates.contains(nakedPair.get(1)))) {
                        candidates.remove(nakedPair.get(0));
                        candidates.remove(nakedPair.get(1));

                    }
                }
            }
        }
    }

    private void nakedPairsColumn(int col) {
        List<List<Integer>> pairs = new ArrayList<>();
        List<List<Integer>> pairsToRemove = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (sudoku[i][col] == 0) {
                List<Integer> pair = availablePlaces.get(Arrays.asList(i, col));
                if (pair != null && pair.size() == 2) {
                    List<Integer> sortedPair = new ArrayList<>(pair);
                    Collections.sort(sortedPair);

                    boolean foundDuplicate = false;
                    for (List<Integer> existingPair : pairs) {
                        List<Integer> sortedExisting = new ArrayList<>(existingPair);
                        Collections.sort(sortedExisting);
                        if (sortedExisting.equals(sortedPair)) {
                            foundDuplicate = true;
                            break;
                        }
                    }

                    if (foundDuplicate) {
                        boolean alreadyAdded = false;
                        for (List<Integer> pr : pairsToRemove) {
                            List<Integer> sortedPr = new ArrayList<>(pr);
                            Collections.sort(sortedPr);
                            if (sortedPr.equals(sortedPair)) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if (!alreadyAdded) {
                            pairsToRemove.add(pair);
                        }
                    }
                    pairs.add(pair);
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            if (sudoku[i][col] == 0) {
                List<Integer> candidates = availablePlaces.get(Arrays.asList(i, col));
                if (candidates == null) continue;

                for (List<Integer> nakedPair : pairsToRemove) {
                    if (candidates.size() > 2 &&
                            (candidates.contains(nakedPair.get(0)) || candidates.contains(nakedPair.get(1)))) {
                        candidates.remove(nakedPair.get(0));
                        candidates.remove(nakedPair.get(1));

                    }
                }
            }
        }
    }

    private void nakedPairsBox(int boxRow, int boxCol) {
        List<List<Integer>> pairs = new ArrayList<>();
        List<List<Integer>> pairsToRemove = new ArrayList<>();
        int startRow = boxRow * 3;
        int startCol = boxCol * 3;
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (sudoku[i][j] == 0) {
                    List<Integer> pair = availablePlaces.get(Arrays.asList(i, j));
                    if (pair != null && pair.size() == 2) {
                        List<Integer> sortedPair = new ArrayList<>(pair);
                        Collections.sort(sortedPair);

                        boolean foundDuplicate = false;
                        for (List<Integer> existingPair : pairs) {
                            List<Integer> sortedExisting = new ArrayList<>(existingPair);
                            Collections.sort(sortedExisting);
                            if (sortedExisting.equals(sortedPair)) {
                                foundDuplicate = true;
                                break;
                            }
                        }

                        if (foundDuplicate) {
                            boolean alreadyAdded = false;
                            for (List<Integer> pr : pairsToRemove) {
                                List<Integer> sortedPr = new ArrayList<>(pr);
                                Collections.sort(sortedPr);
                                if (sortedPr.equals(sortedPair)) {
                                    alreadyAdded = true;
                                    break;
                                }
                            }
                            if (!alreadyAdded) {
                                pairsToRemove.add(pair);
                            }
                        }
                        pairs.add(pair);
                    }
                }
            }
        }
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (sudoku[i][j] == 0) {
                    List<Integer> candidates = availablePlaces.get(Arrays.asList(i, j));
                    if (candidates == null) continue;

                    for (List<Integer> nakedPair : pairsToRemove) {
                        if (candidates.size() > 2 &&
                                (candidates.contains(nakedPair.get(0)) || candidates.contains(nakedPair.get(1)))) {
                            candidates.remove(nakedPair.get(0));
                            candidates.remove(nakedPair.get(1));
                        }
                    }
                }
            }
        }
    }
    
    private void BoxToLine(int boxRow, int boxCol) {
        int rowStart = boxRow * 3;
        int colStart = boxCol * 3;
        Map<Integer, List<List<Integer>>> places = new HashMap<>();

        for (int i = rowStart; i < rowStart + 3; i++) {
            for (int j = colStart; j < colStart + 3; j++) {
                if (sudoku[i][j] == 0) {
                    List<Integer> available = availablePlaces.get(Arrays.asList(i, j));
                    if (available == null) continue;
                    for (Integer n : available) {
                        places.computeIfAbsent(n, k -> new ArrayList<>()).add(Arrays.asList(i, j));
                    }
                }
            }
        }

        for (Map.Entry<Integer, List<List<Integer>>> entry : places.entrySet()) {
            Integer candidate = entry.getKey();
            List<List<Integer>> positions = entry.getValue();
            if (positions.size() < 2 || positions.size() > 3) continue;

            boolean sameRow = true;
            boolean sameCol = true;
            int baseRow = positions.get(0).get(0);
            int baseCol = positions.get(0).get(1);

            for (List<Integer> pos : positions) {
                if (!pos.get(0).equals(baseRow)) sameRow = false;
                if (!pos.get(1).equals(baseCol)) sameCol = false;
            }
            if (sameRow ^ sameCol) {
                if (sameRow) {
                    int fixedRow = baseRow;
                    for (int j = 0; j < 9; j++) {
                        if (j >= colStart && j < colStart + 3) continue;

                        List<Integer> candidateList = availablePlaces.get(Arrays.asList(fixedRow, j));
                        if (candidateList != null && candidateList.contains(candidate) && candidateList.size() > 1) {
                            candidateList.remove(candidate);

                        }
                    }
                } else {
                    int fixedCol = baseCol;
                    for (int i = 0; i < 9; i++) {
                        if (i >= rowStart && i < rowStart + 3) continue;

                        List<Integer> candidateList = availablePlaces.get(Arrays.asList(i, fixedCol));
                        if (candidateList != null && candidateList.contains(candidate) && candidateList.size() > 1) {
                            candidateList.remove(candidate);

                        }
                    }
                }
            }
        }
    }
    
    private void checkXWingRows(Map<Integer, List<List<Integer>>> positions) {
        for (Map.Entry<Integer, List<List<Integer>>> entry : positions.entrySet()) {
            int candidate = entry.getKey();
            List<List<Integer>> positionList = entry.getValue();

            Map<Integer, List<Integer>> rowToCols = new HashMap<>();
            for (List<Integer> pos : positionList) {
                rowToCols.computeIfAbsent(pos.get(0), k -> new ArrayList<>()).add(pos.get(1));
            }

            List<Integer> candidateRows = new ArrayList<>();
            for (Map.Entry<Integer, List<Integer>> rcEntry : rowToCols.entrySet()) {
                if (rcEntry.getValue().size() == 2) {
                    candidateRows.add(rcEntry.getKey());
                }
            }

            for (int i = 0; i < candidateRows.size(); i++) {
                for (int j = i + 1; j < candidateRows.size(); j++) {
                    int row1 = candidateRows.get(i);
                    int row2 = candidateRows.get(j);
                    List<Integer> colsRow1 = rowToCols.get(row1);
                    List<Integer> colsRow2 = rowToCols.get(row2);

                    Collections.sort(colsRow1);
                    Collections.sort(colsRow2);

                    if (colsRow1.equals(colsRow2)) {
                        for (int otherRow = 0; otherRow < 9; otherRow++) {
                            if (otherRow != row1 && otherRow != row2) {
                                for (int col : colsRow1) {
                                    List<Integer> candidates = availablePlaces.get(Arrays.asList(otherRow, col));
                                    if (candidates != null) {
                                        candidates.remove(Integer.valueOf(candidate));

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkXWingColumns(Map<Integer, List<List<Integer>>> positions) {
        for (Map.Entry<Integer, List<List<Integer>>> entry : positions.entrySet()) {
            int candidate = entry.getKey();
            List<List<Integer>> positionList = entry.getValue();

            Map<Integer, List<Integer>> colToRows = new HashMap<>();
            for (List<Integer> pos : positionList) {
                colToRows.computeIfAbsent(pos.get(1), k -> new ArrayList<>()).add(pos.get(0));
            }

            List<Integer> candidateCols = new ArrayList<>();
            for (Map.Entry<Integer, List<Integer>> crEntry : colToRows.entrySet()) {
                if (crEntry.getValue().size() == 2) {
                    candidateCols.add(crEntry.getKey());
                }
            }

            for (int i = 0; i < candidateCols.size(); i++) {
                for (int j = i + 1; j < candidateCols.size(); j++) {
                    int col1 = candidateCols.get(i);
                    int col2 = candidateCols.get(j);
                    List<Integer> rowsCol1 = colToRows.get(col1);
                    List<Integer> rowsCol2 = colToRows.get(col2);

                    Collections.sort(rowsCol1);
                    Collections.sort(rowsCol2);

                    if (rowsCol1.equals(rowsCol2)) {
                        for (int otherCol = 0; otherCol < 9; otherCol++) {
                            if (otherCol != col1 && otherCol != col2) {
                                for (int row : rowsCol1) {
                                    List<Integer> candidates = availablePlaces.get(Arrays.asList(row, otherCol));
                                    if (candidates != null) {
                                        candidates.remove(Integer.valueOf(candidate));

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void xWing() {
        checkXWingRows(getCandidatePositions());
        checkXWingColumns(getCandidatePositions());
    }
    
    private void swordFish(){
        checkSwordFishRow(getCandidatePositions());
        checkSwordFishColumns(getCandidatePositions());
    }
    
    private void checkSwordFishRow(Map<Integer, List<List<Integer>>> candidatePositions) {
        for (Map.Entry<Integer, List<List<Integer>>> entry : candidatePositions.entrySet()) {
            int candidate = entry.getKey();
            List<List<Integer>> positions = entry.getValue();

            Map<Integer, Set<Integer>> rowToCols = new HashMap<>();
            for (List<Integer> pos : positions) {
                int row = pos.get(0);
                int col = pos.get(1);
                rowToCols.computeIfAbsent(row, k -> new HashSet<>()).add(col);
            }

            List<Integer> validRows = new ArrayList<>();
            for (Map.Entry<Integer, Set<Integer>> rc : rowToCols.entrySet()) {
                if (rc.getValue().size() >= 2 && rc.getValue().size() <= 3) {
                    validRows.add(rc.getKey());
                }
            }
            for (int i = 0; i < validRows.size(); i++) {
                for (int j = i + 1; j < validRows.size(); j++) {
                    for (int k = j + 1; k < validRows.size(); k++) {
                        int r1 = validRows.get(i);
                        int r2 = validRows.get(j);
                        int r3 = validRows.get(k);

                        Set<Integer> combinedCols = new HashSet<>();
                        combinedCols.addAll(rowToCols.get(r1));
                        combinedCols.addAll(rowToCols.get(r2));
                        combinedCols.addAll(rowToCols.get(r3));

                        if (combinedCols.size() == 3) {
                            for (int row = 0; row < 9; row++) {
                                if (row == r1 || row == r2 || row == r3) continue;
                                for (int col : combinedCols) {
                                    List<Integer> cellCandidates = availablePlaces.get(Arrays.asList(row, col));
                                    if (cellCandidates != null && cellCandidates.contains(candidate)) {
                                        cellCandidates.remove(Integer.valueOf(candidate));

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void checkSwordFishColumns(Map<Integer, List<List<Integer>>> candidatePositions) {
        for (Map.Entry<Integer, List<List<Integer>>> entry : candidatePositions.entrySet()) {
            int candidate = entry.getKey();
            List<List<Integer>> positions = entry.getValue();

            Map<Integer, Set<Integer>> colToRows = new HashMap<>();
            for (List<Integer> pos : positions) {
                int row = pos.get(0);
                int col = pos.get(1);
                colToRows.computeIfAbsent(col, k -> new HashSet<>()).add(row);
            }

            List<Integer> validCols = new ArrayList<>();
            for (Map.Entry<Integer, Set<Integer>> cr : colToRows.entrySet()) {
                if (cr.getValue().size() >= 2 && cr.getValue().size() <= 3) {
                    validCols.add(cr.getKey());
                }
            }

            for (int i = 0; i < validCols.size(); i++) {
                for (int j = i + 1; j < validCols.size(); j++) {
                    for (int k = j + 1; k < validCols.size(); k++) {
                        int c1 = validCols.get(i);
                        int c2 = validCols.get(j);
                        int c3 = validCols.get(k);

                        Set<Integer> combinedRows = new HashSet<>();
                        combinedRows.addAll(colToRows.get(c1));
                        combinedRows.addAll(colToRows.get(c2));
                        combinedRows.addAll(colToRows.get(c3));

                        if (combinedRows.size() == 3) {
                            for (int col = 0; col < 9; col++) {
                                if (col == c1 || col == c2 || col == c3) continue;
                                
                                for (int row : combinedRows) {
                                    List<Integer> cellCandidates = availablePlaces.get(Arrays.asList(row, col));
                                    if (cellCandidates != null && cellCandidates.contains(candidate)) {
                                        cellCandidates.remove(Integer.valueOf(candidate));
                                    }
                                }
                            }
                            
                        }
                    }
                }
            }
        }
    }

    private Map<Integer, List<List<Integer>>> getCandidatePositions(){
        Map<Integer, List<List<Integer>>> candidatePositions = new HashMap<>();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (sudoku[row][col] == 0) {
                    List<Integer> candidates = availablePlaces.get(Arrays.asList(row, col));
                    if (candidates != null) {
                        for (int candidate : candidates) {
                            candidatePositions.computeIfAbsent(candidate, k -> new ArrayList<>()).add(Arrays.asList(row, col));
                        }
                    }
                }
            }
        }
        return candidatePositions;
    }

    private boolean hasZeros() {
        for (int[] integers : sudoku) {
            for (int anInt : integers) {
                if (anInt == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private int[][] deepCloneGrid(int[][] grid) {
        int[][] clone = new int[9][9];
        for (int i = 0; i < 9; i++) {
            clone[i] = grid[i].clone();
        }
        return clone;
    }
    
    private Map<List<Integer>, List<Integer>> deepCopyAvailablePlaces(Map<List<Integer>, List<Integer>> original) {
        Map<List<Integer>, List<Integer>> copy = new HashMap<>();
        for (Map.Entry<List<Integer>, List<Integer>> entry : original.entrySet()) {
            List<Integer> keyCopy = new ArrayList<>(entry.getKey());
            List<Integer> valueCopy = new ArrayList<>(entry.getValue());
            copy.put(keyCopy, valueCopy);
        }
        return copy;
    }

    private void applyNakedPairsRows() {
        for (int i = 0; i < 9; i++) {
            nakedPairsRow(i);
        }
    }

    private void applyNakedPairsColumns() {
        for (int i = 0; i < 9; i++) {
            nakedPairsColumn(i);
        }
    }

    private void applyNakedPairsBoxes() {
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                nakedPairsBox(boxRow, boxCol);
            }
        }
    }
    
    private void applyBoxToLine() {
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                BoxToLine(boxRow, boxCol);
            }
        }
    }

    private void applyRowSolver() {
        for (int i = 0; i < 9; i++) {
            rowHiddenSingle(i);
        }
    }

    private void applyColumnSolver() {
        for (int i = 0; i < 9; i++) {
            columnHiddenSingle(i);
        }
    }

    private void applyBoxSolver() {
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                boxHiddenSingle(boxRow, boxCol);
            }
        }
    }

    public void solveBasics() {
        boolean progressMade;
        boolean finalRunDone= false;

        do {
            progressMade = false;
            setAvailableNumbers();
            availableNumberRemover();
            int[][] before = deepCloneGrid(sudoku);
            applyNakedPairsRows();
            applyNakedPairsColumns();
            applyBoxToLine();
            applyNakedPairsBoxes();
            xWing();
            swordFish();
            basicSolver();
            applyRowSolver();
            applyColumnSolver();
            applyBoxSolver();

            outerLoop:
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (sudoku[i][j] != before[i][j]) {
                        progressMade = true;
                        finalRunDone = false;
                        break outerLoop;
                    }

                }
            }
            if(!progressMade && !finalRunDone) {
                progressMade = true;
                finalRunDone = true;
            }
        } while ((hasZeros() && progressMade));
    }
    
    private boolean solveWithGuessAndBasics() {
        solveBasics();
        if (!hasZeros()) {
            return true;
        }
        int targetRow = -1;
        int targetCol = -1;
        int minOptions = 10;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] == 0) {
                    List<Integer> candidates = availablePlaces.get(Arrays.asList(i, j));
                    if (candidates != null && candidates.size() < minOptions) {
                        minOptions = candidates.size();
                        targetRow = i;
                        targetCol = j;
                    }
                }
            }
        }

        if (targetRow == -1) return true;

        List<Integer> guesses = new ArrayList<>(availablePlaces.get(Arrays.asList(targetRow, targetCol)));

        int[][] sudokuBackup = deepCloneGrid(sudoku);
        Map<List<Integer>, List<Integer>> candidatesBackup = deepCopyAvailablePlaces(availablePlaces);

        for (Integer num : guesses) {
            if (isValidPlacement(targetRow, targetCol, num)) {
                sudoku[targetRow][targetCol] = num;
                SudokuVisualizer.updateCell(targetRow, targetCol, num);
                availableNumberRemover();
                guessCount++;
                correctGuessCount++;

                if (solveWithGuessAndBasics()) {
                    return true;
                }

                sudoku = deepCloneGrid(sudokuBackup);
                availablePlaces = deepCopyAvailablePlaces(candidatesBackup);
                SudokuVisualizer.initializeGrid(sudoku);
                correctGuessCount--;
            }
        }
        return false;
    }
    
    public String solve() {
        guessCount = 0;
        SudokuVisualizer.initializeGrid(sudoku);
        solveWithGuessAndBasics();
        SudokuVisualizer.finish();
        System.out.println("Total guesses: " + guessCount);
        System.out.println("Total correct guesses: " + correctGuessCount);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sb.append(sudoku[i][j] == 0 ? "." : sudoku[i][j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}