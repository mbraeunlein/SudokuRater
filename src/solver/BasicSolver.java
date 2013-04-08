package solver;

import model.*;

public class BasicSolver {
	public Sudoku scan(Sudoku sudoku) throws Exception {
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				Field field = sudoku.getField(row, column);
				// only update possibilities if the field isnt filled yet
				if (field.number == 0) {
					Field[] rowNumbers = sudoku.getRow(row);
					// for every number in the row remove the possibility in the current field
					for(int i = 0; i < rowNumbers.length; i++) {
						if(rowNumbers[i].number != 0) {
							sudoku.removePosibility(row, column, rowNumbers[i].number);
						}
					}
					
					Field[] columnNumbers = sudoku.getColumn(column);
					// for every number in the column remove the possibility in the current field
					for(int i = 0; i < columnNumbers.length; i++) {
						if(columnNumbers[i].number != 0) {
							sudoku.removePosibility(row, column, columnNumbers[i].number);
						}
					}
					
					Field[] blockNumbers = sudoku.getBlock(sudoku.getContainingBlockNumber(row, column));
					// for every number in the block remove the possibility in the current field
					for(int i = 0; i < blockNumbers.length; i++) {
						if(blockNumbers[i].number != 0) {
							sudoku.removePosibility(row, column, blockNumbers[i].number);
						}
					}
				}
			}
		}
		return sudoku;
	}
}
