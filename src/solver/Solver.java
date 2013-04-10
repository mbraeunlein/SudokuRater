package solver;

import java.util.*;
import model.*;

public class Solver {

	private Sudoku updateConstraints(Sudoku sudoku) throws Exception {
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				Field field = sudoku.getField(row, column);
				// only update possibilities if the field isnt filled yet
				if (field.number == 0) {
					Field[] rowNumbers = sudoku.get(Figure.Row, row);
					// for every number in the row remove the possibility in the
					// current field
					for (int i = 0; i < rowNumbers.length; i++) {
						if (rowNumbers[i].number != 0) {
							sudoku.removePosibility(row, column,
									rowNumbers[i].number);
						}
					}
					Field[] columnNumbers = sudoku.get(Figure.Column, column);
					// for every number in the column remove the possibility in
					// the current field
					for (int i = 0; i < columnNumbers.length; i++) {
						if (columnNumbers[i].number != 0) {
							sudoku.removePosibility(row, column,
									columnNumbers[i].number);
						}
					}

					Field[] blockNumbers = sudoku.get(Figure.Block,
							sudoku.getContainingBlockNumber(row, column));
					// for every number in the block remove the possibility in
					// the current field
					for (int i = 0; i < blockNumbers.length; i++) {
						if (blockNumbers[i].number != 0) {
							sudoku.removePosibility(row, column,
									blockNumbers[i].number);
						}
					}
				}
			}
		}
		return sudoku;
	}

	public Sudoku excludePossibilities(Sudoku sudoku) throws Exception {
		sudoku = updateConstraints(sudoku);
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				Field field = sudoku.getField(row, column);
				if (field.number == 0)
					if (field.posibilities.size() == 1) {
						int number = field.posibilities.get(0);
						sudoku.setField(row, column, new Field(number));
						System.out.println("exclude possibilities: " + row
								+ " / " + column + " -> " + number);
						return sudoku;
					}
			}
		}
		return sudoku;
	}

	public Sudoku scan(Sudoku sudoku) throws Exception {
		sudoku = updateConstraints(sudoku);
		// iterate over all rows, columns and blocks
		for (int i = 0; i < 9; i++) {
			Field[] row = sudoku.get(Figure.Row, i);
			Field[] column = sudoku.get(Figure.Column, i);
			Field[] block = sudoku.get(Figure.Block, i);

			HashMap<Integer, PossibilityCount> rowNumberCount = new HashMap<Integer, PossibilityCount>();
			HashMap<Integer, PossibilityCount> columnNumberCount = new HashMap<Integer, PossibilityCount>();
			HashMap<Integer, PossibilityCount> blockNumberCount = new HashMap<Integer, PossibilityCount>();

			for (int j = 0; j < 9; j++) {
				Field field = block[j];
				if (field.number == 0) {
					for (int possibility : field.posibilities) {
						if (blockNumberCount.containsKey(possibility)) {
							PossibilityCount posCount = blockNumberCount
									.get(possibility);
							blockNumberCount.put(possibility,
									new PossibilityCount(posCount.times + 1, i,
											j));
						} else {
							blockNumberCount.put(possibility,
									new PossibilityCount(1, i, j));
						}
					}
				}
			}
			for (int j = 1; j < 10; j++) {
				if (blockNumberCount.containsKey(j)) {
					PossibilityCount posCount = blockNumberCount.get(j);
					if (posCount.times == 1) {
						System.out.println("scan: block " + posCount.i
								+ " position " + posCount.j + "  ->:  " + j);
						sudoku.setFieldInBlock(posCount.i, posCount.j,
								new Field(j));
						return sudoku;
					}
				}
			}

			for (int j = 0; j < 9; j++) {
				Field field = row[j];
				if (field.number == 0) {
					for (int possibility : field.posibilities) {
						if (rowNumberCount.containsKey(possibility)) {
							PossibilityCount posCount = rowNumberCount
									.get(possibility);
							rowNumberCount.put(possibility,
									new PossibilityCount(posCount.times + 1, i,
											j));
						} else {
							rowNumberCount.put(possibility,
									new PossibilityCount(1, i, j));
						}
					}
				}
			}
			for (int j = 1; j < 10; j++) {
				if (rowNumberCount.containsKey(j)) {
					PossibilityCount posCount = rowNumberCount.get(j);
					if (posCount.times == 1) {
						System.out.println("rowscan: " + posCount.i + " / "
								+ posCount.j + "  ->:  " + j);
						sudoku.setField(posCount.i, posCount.j, new Field(j));
						return sudoku;
					}
				}
			}

			for (int j = 0; j < 9; j++) {
				Field field = column[j];
				if (field.number == 0) {
					for (int possibility : field.posibilities) {
						if (columnNumberCount.containsKey(possibility)) {
							PossibilityCount posCount = columnNumberCount
									.get(possibility);
							columnNumberCount.put(possibility,
									new PossibilityCount(posCount.times + 1, i,
											j));
						} else {
							columnNumberCount.put(possibility,
									new PossibilityCount(1, i, j));
						}
					}
				}
			}
			for (int j = 1; j < 10; j++) {
				if (columnNumberCount.containsKey(j)) {
					PossibilityCount posCount = columnNumberCount.get(j);
					if (posCount.times == 1) {
						System.out.println("colscan: " + posCount.j + " / "
								+ posCount.i + "  ->:  " + j);
						sudoku.setField(posCount.j, posCount.i, new Field(j));
						return sudoku;
					}
				}
			}
		}
		return sudoku;
	}

	private class PossibilityCount {
		int times;
		int i;
		int j;

		public PossibilityCount(int Times, int I, int J) {
			times = Times;
			i = I;
			j = J;
		}
	}

	public Sudoku tupel(Sudoku sudoku) throws Exception {
		sudoku = updateConstraints(sudoku);
		for (int i = 0; i < 9; i++) {
			ArrayList<int[]> rowTupel = new ArrayList<int[]>();
			ArrayList<int[]> columnTupel = new ArrayList<int[]>();
			ArrayList<int[]> blockTupel = new ArrayList<int[]>();

			// search for tupel
			Field[] row = sudoku.get(Figure.Row, i);
			for (int j = 0; j < 9; j++) {
				ArrayList<Integer> possibilities = row[j].posibilities;
				if (possibilities.size() == 2) {
					int[] tupel = new int[3];
					tupel[0] = j;
					tupel[1] = possibilities.get(0);
					tupel[2] = possibilities.get(1);
					rowTupel.add(tupel);
				}
			}
			// compare the tupels
			for (int j = 0; j < rowTupel.size(); j++) {
				for (int k = j + 1; k < rowTupel.size(); k++) {
					int[] tupel1 = rowTupel.get(j);
					int[] tupel2 = rowTupel.get(k);
					if (tupel1[1] == tupel2[1] && tupel1[2] == tupel2[2]) {
						// tupel found
						System.out.println("tupel " + tupel1[1] + " and "
								+ tupel1[2] + " at row " + i + " positions "
								+ tupel1[0] + " and " + tupel2[0]);
						int count = 0;
						for (int l = 0; l < 9; l++) {
							if (l != tupel1[0] && l != tupel2[0]) {
								if (row[l].posibilities.contains(tupel1[1])) {
									sudoku.removePosibility(i, l, tupel1[1]);
									count++;
								}
								if (row[l].posibilities.contains(tupel1[2])) {
									sudoku.removePosibility(i, l, tupel1[2]);
									count++;
								}
							}
						}
						System.out.println("removed " + count
								+ " possibilities");
					}
				}
			}

			// search for tupel
			Field[] column = sudoku.get(Figure.Column, i);
			for (int j = 0; j < 9; j++) {
				ArrayList<Integer> possibilities = column[j].posibilities;
				if (possibilities.size() == 2) {
					int[] tupel = new int[3];
					tupel[0] = j;
					tupel[1] = possibilities.get(0);
					tupel[2] = possibilities.get(1);
					columnTupel.add(tupel);
				}
			}
			// compare the tupels
			for (int j = 0; j < columnTupel.size(); j++) {
				for (int k = j + 1; k < columnTupel.size(); k++) {
					int[] tupel1 = columnTupel.get(j);
					int[] tupel2 = columnTupel.get(k);
					if (tupel1[1] == tupel2[1] && tupel1[2] == tupel2[2]) {
						// tupel found
						System.out.println("tupel " + tupel1[1] + " and "
								+ tupel1[2] + " at column " + i + " positions "
								+ tupel1[0] + " and " + tupel2[0]);
						int count = 0;
						for (int l = 0; l < 9; l++) {
							if (l != tupel1[0] && l != tupel2[0]) {
								if (column[l].posibilities.contains(tupel1[1])) {
									sudoku.removePosibility(l, i, tupel1[1]);
									count++;
								}
								if (column[l].posibilities.contains(tupel1[2])) {
									sudoku.removePosibility(l, i, tupel1[2]);
									count++;
								}
							}
						}
						System.out.println("removed " + count
								+ " possibilities");
					}
				}
			}

			// search for tupel
			Field[] block = sudoku.get(Figure.Block, i);
			for (int j = 0; j < 9; j++) {
				ArrayList<Integer> possibilities = block[j].posibilities;
				if (possibilities.size() == 2) {
					int[] tupel = new int[3];
					tupel[0] = j;
					tupel[1] = possibilities.get(0);
					tupel[2] = possibilities.get(1);
					blockTupel.add(tupel);
				}
			}
			// compare the tupels
			for (int j = 0; j < blockTupel.size(); j++) {
				for (int k = j + 1; k < blockTupel.size(); k++) {
					int[] tupel1 = blockTupel.get(j);
					int[] tupel2 = blockTupel.get(k);
					if (tupel1[1] == tupel2[1] && tupel1[2] == tupel2[2]) {
						// tupel found
						System.out.println("tupel " + tupel1[1] + " and "
								+ tupel1[2] + " at block " + i + " positions "
								+ tupel1[0] + " and " + tupel2[0]);
						int count = 0;
						for (int l = 0; l < 9; l++) {
							if (l != tupel1[0] && l != tupel2[0]) {
								if (block[l].posibilities.contains(tupel1[1])) {
									sudoku.removePossibilityInBlock(i, l,
											tupel1[1]);
									count++;
								}
								if (block[l].posibilities.contains(tupel1[2])) {
									sudoku.removePossibilityInBlock(i, l,
											tupel1[2]);
									count++;
								}
							}
						}
						System.out.println("removed " + count
								+ " possibilities");
					}
				}
			}
		}

		return sudoku;
	}

	public Sudoku tripel(Sudoku sudoku) throws Exception {
		sudoku = updateConstraints(sudoku);
		for (int i = 0; i < 9; i++) {
			ArrayList<int[]> rowTripel = new ArrayList<int[]>();
			ArrayList<int[]> columnTripel = new ArrayList<int[]>();
			ArrayList<int[]> blockTripel = new ArrayList<int[]>();

			// search for tripel
			Field[] row = sudoku.get(Figure.Row, i);
			for (int j = 0; j < 9; j++) {
				ArrayList<Integer> possibilities = row[j].posibilities;
				if (possibilities.size() == 2) {
					int[] tripel = new int[4];
					tripel[0] = j;
					tripel[1] = possibilities.get(0);
					tripel[2] = possibilities.get(1);
					tripel[3] = 0;
					rowTripel.add(tripel);
				}
				if (possibilities.size() == 3) {
					int[] tripel = new int[4];
					tripel[0] = j;
					tripel[1] = possibilities.get(0);
					tripel[2] = possibilities.get(1);
					tripel[3] = possibilities.get(2);
					rowTripel.add(tripel);
				}
			}
			// compare the tripels
			for (int j = 0; j < rowTripel.size(); j++) {
				for (int k = j + 1; k < rowTripel.size(); k++) {
					for (int l = k + 1; l < rowTripel.size(); l++) {
						int[] tripel1 = rowTripel.get(j);
						int[] tripel2 = rowTripel.get(k);
						int[] tripel3 = rowTripel.get(l);
						ArrayList<Integer> numberList = new ArrayList<Integer>();
						if (!numberList.contains(tripel1[1]))
							numberList.add(tripel1[1]);
						if (!numberList.contains(tripel1[2]))
							numberList.add(tripel1[2]);
						if (!numberList.contains(tripel2[1]))
							numberList.add(tripel2[1]);
						if (!numberList.contains(tripel2[2]))
							numberList.add(tripel2[2]);
						if (!numberList.contains(tripel3[1]))
							numberList.add(tripel3[1]);
						if (!numberList.contains(tripel3[2]))
							numberList.add(tripel3[2]);
						if (tripel1[3] != 0 && !numberList.contains(tripel1[3]))
							numberList.add(tripel1[3]);
						if (tripel2[3] != 0 && !numberList.contains(tripel2[3]))
							numberList.add(tripel2[3]);
						if (tripel2[3] != 0 && !numberList.contains(tripel3[3]))
							numberList.add(tripel2[3]);

						if (numberList.size() == 3) {
							// tripel found
							 System.out.println("tripel " + tripel1[1] +
							 ", "
							 + tripel1[2] + " and " + tripel1[3] + " at row "
							 + i
							 + " positions " + tripel1[0] + ", "
							 + tripel2[0] + " and " + tripel3[0]);
							// int count = 0;
							// for (int m = 0; m < 9; m++) {
							// if (m != tupel1[0] && m != tupel2[0]) {
							// if (row[l].posibilities.contains(tupel1[1])) {
							// sudoku.removePosibility(i, m, tupel1[1]);
							// count++;
							// }
							// if (row[l].posibilities.contains(tupel1[2])) {
							// sudoku.removePosibility(i, m, tupel1[2]);
							// count++;
							// }
							// }
							// }
							// System.out.println("removed " + count
							// + " possibilities");
						}
					}
				}
			}
		}

		return sudoku;
	}
}
