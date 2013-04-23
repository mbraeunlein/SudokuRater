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
						System.out.println("scan: " + posCount.i + " / "
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
						System.out.println("scan: " + posCount.j + " / "
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
						if (count > 0) {
							System.out.println("tupel " + tupel1[1] + " and "
									+ tupel1[2] + " at row " + i
									+ " positions " + tupel1[0] + " and "
									+ tupel2[0] + " removed " + count
									+ " possibilities");
							return sudoku;
						}
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
						if (count > 0) {
							System.out.println("tupel " + tupel1[1] + " and "
									+ tupel1[2] + " at column " + i
									+ " positions " + tupel1[0] + " and "
									+ tupel2[0] + " removed " + count
									+ " possibilities");
							return sudoku;
						}
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
						if (count > 0) {
							System.out.println("tupel " + tupel1[1] + " and "
									+ tupel1[2] + " at block " + i
									+ " positions " + tupel1[0] + " and "
									+ tupel2[0] + " removed " + count
									+ " possibilities");
							return sudoku;
						}
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
							int count = 0;
							for (int m = 0; m < 9; m++) {
								if (m != tripel1[0] && m != tripel2[0]
										&& m != tripel3[0]) {
									if (row[m].posibilities
											.contains(tripel1[1])) {
										sudoku.removePosibility(i, m,
												tripel1[1]);
										count++;
									}
									if (row[m].posibilities
											.contains(tripel1[2])) {
										sudoku.removePosibility(i, m,
												tripel1[2]);
										count++;
									}
									if (row[m].posibilities
											.contains(tripel1[3])) {
										sudoku.removePosibility(i, m,
												tripel1[3]);
										count++;
									}
								}
							}
							if (count > 0) {
								System.out.println("tripel "
										+ numberList.get(0) + ", "
										+ numberList.get(1) + " and "
										+ numberList.get(2) + " at row " + i
										+ " positions " + tripel1[0] + ", "
										+ tripel2[0] + " and " + tripel3[0]
										+ " removed " + count
										+ " possibilities");
								return sudoku;
							}
						}
					}
				}
			}

			// search for tripel
			Field[] column = sudoku.get(Figure.Column, i);
			for (int j = 0; j < 9; j++) {
				ArrayList<Integer> possibilities = column[j].posibilities;
				if (possibilities.size() == 2) {
					int[] tripel = new int[4];
					tripel[0] = j;
					tripel[1] = possibilities.get(0);
					tripel[2] = possibilities.get(1);
					tripel[3] = 0;
					columnTripel.add(tripel);
				}
				if (possibilities.size() == 3) {
					int[] tripel = new int[4];
					tripel[0] = j;
					tripel[1] = possibilities.get(0);
					tripel[2] = possibilities.get(1);
					tripel[3] = possibilities.get(2);
					columnTripel.add(tripel);
				}
			}
			// compare the tripels
			for (int j = 0; j < columnTripel.size(); j++) {
				for (int k = j + 1; k < columnTripel.size(); k++) {
					for (int l = k + 1; l < columnTripel.size(); l++) {
						int[] tripel1 = columnTripel.get(j);
						int[] tripel2 = columnTripel.get(k);
						int[] tripel3 = columnTripel.get(l);
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
							int count = 0;
							for (int m = 0; m < 9; m++) {
								if (m != tripel1[0] && m != tripel2[0]
										&& m != tripel3[0]) {
									if (column[m].posibilities
											.contains(tripel1[1])) {
										sudoku.removePosibility(m, i,
												tripel1[1]);
										count++;
									}
									if (column[m].posibilities
											.contains(tripel1[2])) {
										sudoku.removePosibility(m, i,
												tripel1[2]);
										count++;
									}
									if (column[m].posibilities
											.contains(tripel1[3])) {
										sudoku.removePosibility(m, i,
												tripel1[3]);
										count++;
									}
								}
							}
							if (count > 0) {
								System.out.println("tripel "
										+ numberList.get(0) + ", "
										+ numberList.get(1) + " and "
										+ numberList.get(2) + " at column " + i
										+ " positions " + tripel1[0] + ", "
										+ tripel2[0] + " and " + tripel3[0]
										+ " removed " + count
										+ " possibilities");
								return sudoku;
							}
						}
					}
				}
			}

			// search for tripel
			Field[] block = sudoku.get(Figure.Block, i);
			for (int j = 0; j < 9; j++) {
				ArrayList<Integer> possibilities = block[j].posibilities;
				if (possibilities.size() == 2) {
					int[] tripel = new int[4];
					tripel[0] = j;
					tripel[1] = possibilities.get(0);
					tripel[2] = possibilities.get(1);
					tripel[3] = 0;
					blockTripel.add(tripel);
				}
				if (possibilities.size() == 3) {
					int[] tripel = new int[4];
					tripel[0] = j;
					tripel[1] = possibilities.get(0);
					tripel[2] = possibilities.get(1);
					tripel[3] = possibilities.get(2);
					blockTripel.add(tripel);
				}
			}
			// compare the tripels
			for (int j = 0; j < blockTripel.size(); j++) {
				for (int k = j + 1; k < blockTripel.size(); k++) {
					for (int l = k + 1; l < blockTripel.size(); l++) {
						int[] tripel1 = blockTripel.get(j);
						int[] tripel2 = blockTripel.get(k);
						int[] tripel3 = blockTripel.get(l);
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
							int count = 0;
							for (int m = 0; m < 9; m++) {
								if (m != tripel1[0] && m != tripel2[0]
										&& m != tripel3[0]) {
									if (block[m].posibilities
											.contains(tripel1[1])) {
										sudoku.removePossibilityInBlock(i, m,
												tripel1[1]);
										count++;
									}
									if (block[m].posibilities
											.contains(tripel1[2])) {
										sudoku.removePossibilityInBlock(i, m,
												tripel1[2]);
										count++;
									}
									if (block[m].posibilities
											.contains(tripel1[3])) {
										sudoku.removePossibilityInBlock(i, m,
												tripel1[3]);
										count++;
									}
								}
							}
							if (count > 9) {
								System.out.println("tripel "
										+ numberList.get(0) + ", "
										+ numberList.get(1) + " and "
										+ numberList.get(2) + " in block " + i
										+ " positions " + tripel1[0] + ", "
										+ tripel2[0] + " and " + tripel3[0]
										+ " removed " + count
										+ " possibilities");
								return sudoku;
							}
						}
					}
				}
			}

		}

		return sudoku;
	}

	public Sudoku crossing(Sudoku sudoku) throws Exception {
		sudoku = updateConstraints(sudoku);

		// iterate over every block (row and column)
		for (int rowNumber = 0; rowNumber < 3; rowNumber++) {
			for (int colNumber = 0; colNumber < 3; colNumber++) {
				// get the current block
				Field[] block = sudoku.get(Figure.Block, 3 * rowNumber
						+ colNumber);

				// iterate over every row that crosses the block
				for (int crossRowNumber = 0; crossRowNumber < 3; crossRowNumber++) {
					// get the current row
					Field[] row = sudoku.get(Figure.Row, 3 * rowNumber
							+ crossRowNumber);

					// iterate over every number
					for (int number = 1; number < 10; number++) {
						boolean crossing = true;
						if (sudoku.isIn(Figure.Row, 3 * rowNumber
								+ crossRowNumber, number)
								|| sudoku.isIn(Figure.Block, 3 * rowNumber
										+ colNumber, number)) {
							crossing = false;
						}
						// iterate over every field in the row
						for (int field = 0; field < 9; field++) {
							// the fields of the row, that are also fields
							// of the block are not relevant
							if (!(field == colNumber * 3)
									&& !(field == colNumber * 3 + 1)
									&& !(field == colNumber * 3 + 2))
								// if an other field of the row contains the
								// number as a possibility the crossing rule
								// is not applicable
								if (row[field].posibilities.contains(number))
									crossing = false;
						}
						if (crossing) {
							// if a crossing was found then remove the
							// possibilities
							int posCount = 0;
							for (int field = 0; field < 9; field++) {
								if (!(field == crossRowNumber * 3)
										&& !(field == crossRowNumber * 3 + 1)
										&& !(field == crossRowNumber * 3 + 2))
									if (block[field].posibilities
											.contains(number)) {
										sudoku.removePossibilityInBlock(
												(3 * rowNumber + colNumber),
												field, number);
										posCount++;
									}
							}

							if (posCount > 0) {
								System.out.println("crossing block "
										+ (3 * rowNumber + colNumber) + " row "
										+ (3 * rowNumber + crossRowNumber)
										+ " number " + number + " ... removed "
										+ posCount + " possibilities");
								return sudoku;
							}
						}

					}
				}
			}
		}

		for (int rowNumber = 0; rowNumber < 3; rowNumber++) {
			for (int colNumber = 0; colNumber < 3; colNumber++) {
				// get the block
				Field[] block = sudoku.get(Figure.Block, 3 * rowNumber
						+ colNumber);

				for (int crossColNumber = 0; crossColNumber < 3; crossColNumber++) {
					// get the crossing column
					Field[] col = sudoku.get(Figure.Column, colNumber * 3
							+ crossColNumber);

					for (int number = 1; number < 10; number++) {
						boolean crossing = true;
						if (sudoku.isIn(Figure.Column, 3 * colNumber
								+ crossColNumber, number)
								|| sudoku.isIn(Figure.Block, 3 * rowNumber
										+ colNumber, number)) {
							crossing = false;
						}
						for (int field = 0; field < 9; field++) {
							if (!(field == rowNumber * 3)
									&& !(field == rowNumber * 3 + 1)
									&& !(field == rowNumber * 3 + 2)) {
								if (col[field].posibilities.contains(number))
									crossing = false;
							}
						}
						if (crossing) {
							int posCount = 0;
							for (int field = 0; field < 9; field++) {
								if (!(field == crossColNumber)
										&& !(field == crossColNumber + 3)
										&& !(field == crossColNumber + 6)) {
									if (block[field].posibilities
											.contains(number)) {
										sudoku.removePossibilityInBlock(3
												* rowNumber + colNumber, field,
												number);
										posCount++;
									}
								}
							}
							if (posCount > 0) {
								System.out.println("crossing block "
										+ (3 * rowNumber + colNumber)
										+ " column "
										+ (3 * colNumber + crossColNumber)
										+ " number " + number + " ... removed "
										+ posCount + " possibilities");
								return sudoku;
							}
						}

					}
				}
			}
		}

		/*
		// iterate over every row and column
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				Field[] block = sudoku.get(Figure.Block, 3 * row + col);
				// iterate over every row in the block
				for (int crossRow = 0; crossRow < 3; crossRow++) {
					ArrayList<ArrayList<Integer>> numberList = new ArrayList<ArrayList<Integer>>();
					// iterate over every field in the row
					for (int f = 0; f < 3; f++) {
						Field field = sudoku.getField(3 * row + crossRow, 3
								* col + f);
						// iterate over the possibilities of every field
						for (int i = 0; i < field.posibilities.size(); i++) {
							// add all possibilities to a list of numbers for
							// the specific row
							if (!numberList.get(f).contains(
									field.posibilities.get(i)))
								numberList.get(f)
										.add(field.posibilities.get(i));
						}
					}
					// iterate over every possible number
					for (int number = 1; number < 10; number++) {
						if(numberList.get(0).contains(number)) {
							
						}
					}
				}
			}
		}*/

		return sudoku;
	}
}
