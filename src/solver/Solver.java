package solver;

import java.util.*;

import model.*;

public class Solver {

	private Sudoku sudoku;
	private int count;

	public Solver(Sudoku sudoku) {
		try {
			this.sudoku = sudoku;
			updateConstraints();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		count = 0;
	}

	public Sudoku getSudoku() {
		return sudoku;
	}
	
	public Sudoku step() {
		try {
			updateConstraints();
			
			switch (count) {
			case 0:
				if(nakedSingles()) {
					count = 0;
					break;
				} else {
					System.out.println("no naked single found");
					count++;
					break;
				}
			case 1:
				if(hiddenSingles()) {
					count = 0;
					break;
				} else {
					System.out.println("no hidden single found");
					count++;
					break;
				}
			case 2:
				if(nakedTupel()) {
					count = 0;
					break;
				} else {
					System.out.println("no naked tupel found");
					count++;
					break;
				}
			case 3:
				if(nakedTripel()) {
					count = 0;
					break;
				} else {
					System.out.println("no naked tripel found");
					count++;
					break;
				}
			case 4:
				if(crossing()) {
					count = 0;
					break;
				} else {
					System.out.println("no crossing found");
					count++;
					break;
				}
			case 5:
				System.out.println("not solvable");
				return sudoku;
			}
		} catch (Exception ex) {

		}
		return sudoku;
	}

	private boolean crossing() throws Exception {
		// iterate over every block
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				// iterate over every row in the block
				// and create 3 lists of numbers
				ArrayList<ArrayList<Integer>> numberLists = new ArrayList<ArrayList<Integer>>();
				numberLists.add(new ArrayList<Integer>());
				numberLists.add(new ArrayList<Integer>());
				numberLists.add(new ArrayList<Integer>());

				for (int crossRow = 0; crossRow < 3; crossRow++) {
					// iterate over every field in the row
					for (int f = 0; f < 3; f++) {
						Field field = sudoku.getField(3 * row + crossRow, 3
								* col + f);
						// iterate over every possibility of the field
						for (int i = 0; i < field.posibilities.size(); i++) {
							if (!numberLists.get(crossRow).contains(
									field.posibilities.get(i)))
								numberLists.get(crossRow).add(
										field.posibilities.get(i));
						}
					}
				}

				for (int number = 1; number < 10; number++) {
					if (numberLists.get(0).contains(number)
							&& !numberLists.get(1).contains(number)
							&& !numberLists.get(2).contains(number)) {
						// crossing found, check if there are possibilities to
						// remove
						int posCount = 0;
						for (int f = 0; f < 9; f++) {
							// ignore the fields, that belong to the block
							if (f != 3 * col && f != 3 * col + 1
									&& f != 3 * col + 2) {
								Field field = sudoku.getField(3 * row, f);
								if (field.posibilities.contains(number)) {
									sudoku.removePosibility(3 * row, f, number);
									System.out.println("removing " + 3 * row
											+ " / " + f + " - " + number);
									posCount++;
								}
							}
						}
						if (posCount > 0) {
							System.out.println("row - block - crossing block "
									+ (3 * row + col) + " row " + (3 * row)
									+ " number " + number + " removed "
									+ posCount + " possibilities");
							return true;

						}
					}
					if (!numberLists.get(0).contains(number)
							&& numberLists.get(1).contains(number)
							&& !numberLists.get(2).contains(number)) {
						// crossing found, check if there are possibilities to
						// remove
						int posCount = 0;
						for (int f = 0; f < 9; f++) {
							// ignore the fields, that belong to the block
							if (f != col * 3 && f != col * 3 + 1
									&& f != col * 3 + 2) {
								Field field = sudoku.getField(3 * row + 1, f);
								if (field.posibilities.contains(number)) {
									sudoku.removePosibility(3 * row + 1, f,
											number);
									posCount++;
								}
							}
						}
						if (posCount > 0) {
							System.out.println("row - block - crossing block "
									+ (3 * row + col) + " row " + (3 * row + 1)
									+ " number " + number + " removed "
									+ posCount + " possibilities");
							return true;

						}
					}
					if (!numberLists.get(0).contains(number)
							&& !numberLists.get(1).contains(number)
							&& numberLists.get(2).contains(number)) {
						// crossing found, check if there are possibilities to
						// remove
						int posCount = 0;
						for (int f = 0; f < 9; f++) {
							// ignore the fields, that belong to the block
							if (f != col * 3 && f != col * 3 + 1
									&& f != col * 3 + 2) {
								Field field = sudoku.getField(3 * row + 2, f);
								if (field.posibilities.contains(number)) {
									sudoku.removePosibility(3 * row + 2, f,
											number);
									posCount++;
								}
							}
						}
						if (posCount > 0) {
							System.out.println("row - block - crossing block "
									+ (3 * row + col) + " row " + (3 * row + 2)
									+ " number " + number + " removed "
									+ posCount + " possibilities");
							return true;

						}
					}
				}
			}
		}

		// iterate over every block
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				// iterate over every row in the block
				// and create 3 lists of numbers
				ArrayList<ArrayList<Integer>> numberLists = new ArrayList<ArrayList<Integer>>();
				numberLists.add(new ArrayList<Integer>());
				numberLists.add(new ArrayList<Integer>());
				numberLists.add(new ArrayList<Integer>());

				for (int crossCol = 0; crossCol < 3; crossCol++) {
					// iterate over every field in the row
					for (int f = 0; f < 3; f++) {
						Field field = sudoku.getField(3 * row + f, 3 * col
								+ crossCol);
						// iterate over every possibility of the field
						for (int i = 0; i < field.posibilities.size(); i++) {
							if (!numberLists.get(crossCol).contains(
									field.posibilities.get(i)))
								numberLists.get(crossCol).add(
										field.posibilities.get(i));
						}
					}
				}

				for (int number = 1; number < 10; number++) {
					if (numberLists.get(0).contains(number)
							&& !numberLists.get(1).contains(number)
							&& !numberLists.get(2).contains(number)) {
						// crossing found, check if there are possibilities to
						// remove
						int posCount = 0;
						for (int f = 0; f < 9; f++) {
							// ignore the fields, that belong to the block
							if (f != 3 * row && f != 3 * row + 1
									&& f != 3 * row + 2) {
								Field field = sudoku.getField(f, 3 * col);
								if (field.posibilities.contains(number)) {
									sudoku.removePosibility(f, 3 * col, number);
									System.out.println("removing " + f + " / "
											+ 3 * col + " - " + number);
									System.out.println("removing row " + f
											+ " col " + (3 * col) + " - "
											+ number);
									posCount++;
								}
							}
						}
						if (posCount > 0) {
							System.out.println("col - block - crossing block "
									+ (3 * row + col) + " col " + (3 * col)
									+ " number " + number + " removed "
									+ posCount + " possibilities");
							return true;

						}
					}
					if (!numberLists.get(0).contains(number)
							&& numberLists.get(1).contains(number)
							&& !numberLists.get(2).contains(number)) {
						// crossing found, check if there are possibilities to
						// remove
						int posCount = 0;
						for (int f = 0; f < 9; f++) {
							// ignore the fields, that belong to the block
							if (f != 3 * row && f != 3 * row + 1
									&& f != 3 * row + 2) {
								Field field = sudoku.getField(f, 3 * col + 1);
								if (field.posibilities.contains(number)) {
									sudoku.removePosibility(f, 3 * col + 1,
											number);
									System.out.println("removing row " + f
											+ " col " + (3 * col + 1) + " - "
											+ number);
									posCount++;
								}
							}
						}
						if (posCount > 0) {
							System.out.println("col - block - crossing block "
									+ (3 * row + col) + " col " + (3 * col + 1)
									+ " number " + number + " removed "
									+ posCount + " possibilities");
							return true;

						}
					}
					if (!numberLists.get(0).contains(number)
							&& !numberLists.get(1).contains(number)
							&& numberLists.get(2).contains(number)) {
						// crossing found, check if there are possibilities to
						// remove
						int posCount = 0;
						for (int f = 0; f < 9; f++) {
							// ignore the fields, that belong to the block
							if (f != 3 * row && f != 3 * row + 1
									&& f != 3 * row + 2) {
								Field field = sudoku.getField(f, 3 * col + 2);
								if (field.posibilities.contains(number)) {
									sudoku.removePosibility(f, 3 * col + 2,
											number);
									System.out.println("removing row " + f
											+ " col " + (3 * col + 2) + " - "
											+ number);
									posCount++;
								}
							}
						}
						if (posCount > 0) {
							System.out.println("col - block - crossing block "
									+ (3 * row + col) + " col " + (3 * col + 2)
									+ " number " + number + " removed "
									+ posCount + " possibilities");
							return true;

						}
					}
				}
			}
		}

		// iterate over every row
		for (int row = 0; row < 9; row++) {
			// iterate over 3 elements of the row (segment)
			// create a list of possible numbers in the segments
			ArrayList<ArrayList<Integer>> numberLists = new ArrayList<ArrayList<Integer>>();
			numberLists.add(new ArrayList<Integer>());
			numberLists.add(new ArrayList<Integer>());
			numberLists.add(new ArrayList<Integer>());
			for (int seg = 0; seg < 3; seg++) {
				// iterate over every field in the segment
				for (int f = 0; f < 3; f++) {
					Field field = sudoku.getField(row, 3 * seg + f);
					// iterate over every possibility of the field
					for (int pos = 0; pos < field.posibilities.size(); pos++) {
						if (!numberLists.get(seg).contains(
								field.posibilities.get(pos)))
							numberLists.get(seg).add(
									field.posibilities.get(pos));
					}
				}
			}

			// if a number appears in one segment only it can be deleted from
			// the rest of the block
			// iterate over every number
			for (int number = 1; number < 10; number++) {
				if (numberLists.get(0).contains(number)
						&& !numberLists.get(1).contains(number)
						&& !numberLists.get(2).contains(number)) {
					// crossing found, check if there are possibilities to
					// remove
					int posCount = 0;
					int blockNumber = 6;
					if (row < 6)
						blockNumber = 3;
					if (row < 3)
						blockNumber = 0;
					for (int f = 0; f < 9; f++) {
						if (f != row % 3 * 3 && f != row % 3 * 3 + 1
								&& f != row % 3 * 3 + 2) {
							if (sudoku.get(Figure.Block, blockNumber)[f].posibilities
									.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber, f,
										number);
								System.out.println("row " + row);
								System.out.println("removing block "
										+ blockNumber + " position " + f
										+ " - " + number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("block - row - crossing block "
								+ blockNumber + " row " + row + " number "
								+ number + " removed " + posCount
								+ " possibilities");
						return true;
					}
				}
				if (!numberLists.get(0).contains(number)
						&& numberLists.get(1).contains(number)
						&& !numberLists.get(2).contains(number)) {
					// crossing found, check if there are possibilities to
					// remove
					int posCount = 0;
					int blockNumber = 7;
					if (row < 6)
						blockNumber = 4;
					if (row < 3)
						blockNumber = 1;
					for (int f = 0; f < 9; f++) {
						if (f != row % 3 * 3 && f != row % 3 * 3 + 1
								&& f != row % 3 * 3 + 2) {
							if (sudoku.get(Figure.Block, blockNumber)[f].posibilities
									.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber, f,
										number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("block - row - crossing block "
								+ blockNumber + " row " + row + " number "
								+ number + " removed " + posCount
								+ " possibilities");
						return true;
					}
				}
				if (!numberLists.get(0).contains(number)
						&& !numberLists.get(1).contains(number)
						&& numberLists.get(2).contains(number)) {
					// crossing found, check if there are possibilities to
					// remove
					int posCount = 0;
					int blockNumber = 8;
					if (row < 6)
						blockNumber = 5;
					if (row < 3)
						blockNumber = 2;
					for (int f = 0; f < 9; f++) {
						if (f != row % 3 * 3 && f != row % 3 * 3 + 1
								&& f != row % 3 * 3 + 2) {
							if (sudoku.get(Figure.Block, blockNumber)[f].posibilities
									.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber, f,
										number);
								System.out.println("removing block "
										+ blockNumber + " position " + f
										+ " - " + number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("block - row - crossing block "
								+ blockNumber + " row " + row + " number "
								+ number + " removed " + posCount
								+ " possibilities");
						return true;
					}
				}
			}
		}

		// iterate over every column
		for (int col = 0; col < 9; col++) {
			// iterate over 3 elements of the row (segment)
			// create a list of possible numbers in the segments
			ArrayList<ArrayList<Integer>> numberLists = new ArrayList<ArrayList<Integer>>();
			numberLists.add(new ArrayList<Integer>());
			numberLists.add(new ArrayList<Integer>());
			numberLists.add(new ArrayList<Integer>());
			for (int seg = 0; seg < 3; seg++) {
				// iterate over every field in the segment
				for (int f = 0; f < 3; f++) {
					Field field = sudoku.getField(3 * seg + f, col);
					// iterate over every possibility of the field
					for (int pos = 0; pos < field.posibilities.size(); pos++) {
						if (!numberLists.get(seg).contains(
								field.posibilities.get(pos)))
							numberLists.get(seg).add(
									field.posibilities.get(pos));
					}
				}
			}

			// if a number appears in one segment only it can be deleted from
			// the rest of the block
			// iterate over every number
			for (int number = 1; number < 10; number++) {
				if (numberLists.get(0).contains(number)
						&& !numberLists.get(1).contains(number)
						&& !numberLists.get(2).contains(number)) {
					// crossing found, check if there are possibilities to
					// remove
					int posCount = 0;
					int blockNumber = 2;
					if (col < 6)
						blockNumber = 1;
					if (col < 3)
						blockNumber = 0;
					for (int f = 0; f < 9; f++) {
						if (f != col % 3 && f != col % 3 + 3
								&& f != col % 3 + 6) {
							if (sudoku.get(Figure.Block, blockNumber)[f].posibilities
									.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber, f,
										number);
								System.out.println("removing block "
										+ blockNumber + " position " + f
										+ " - " + number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("block - col - crossingüü block "
								+ blockNumber + " col " + col + " number "
								+ number + " removed " + posCount
								+ " possibilities");
						return true;
					}
				}
				if (!numberLists.get(0).contains(number)
						&& numberLists.get(1).contains(number)
						&& !numberLists.get(2).contains(number)) {
					// crossing found, check if there are possibilities to
					// remove
					int posCount = 0;
					int blockNumber = 5;
					if (col < 6)
						blockNumber = 4;
					if (col < 3)
						blockNumber = 3;
					for (int f = 0; f < 9; f++) {
						if (f != col % 3 && f != col % 3 + 3
								&& f != col % 3 + 6) {
							if (sudoku.get(Figure.Block, blockNumber)[f].posibilities
									.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber, f,
										number);
								System.out.println("removing block "
										+ blockNumber + " position " + f
										+ " - " + number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("block - col - crossing block "
								+ blockNumber + " col " + col + " number "
								+ number + " removed " + posCount
								+ " possibilities");
						return true;
					}
				}
				if (!numberLists.get(0).contains(number)
						&& !numberLists.get(1).contains(number)
						&& numberLists.get(2).contains(number)) {
					// crossing found, check if there are possibilities to
					// remove
					int posCount = 0;
					int blockNumber = 8;
					if (col < 6)
						blockNumber = 7;
					if (col < 3)
						blockNumber = 6;
					for (int f = 0; f < 9; f++) {
						if (f != col % 3 && f != col % 3 + 3
								&& f != col % 3 + 6) {
							if (sudoku.get(Figure.Block, blockNumber)[f].posibilities
									.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber, f,
										number);
								System.out.println("removing block "
										+ blockNumber + " position " + f
										+ " - " + number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("block - col - crossing block "
								+ blockNumber + " col " + col + " number "
								+ number + " removed " + posCount
								+ " possibilities");
						return true;
					}
				}
			}
		}

		return false;
	}

	// ------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------
	// NEW
	// ------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------

	private void updateConstraints() throws Exception {
		for(int row = 0; row < 9; row++) {
			for(int col = 0; col < 9; col++) {
				int number = sudoku.getField(row, col).number;
				if(number != 0) {
					for(int pos = 0; pos < 9; pos++) {
						if(sudoku.getField(row, pos).posibilities.contains(number))
							sudoku.removePosibility(row, pos, number);
						if(sudoku.getField(pos, col).posibilities.contains(number))
							sudoku.removePosibility(pos, col, number);
						if(sudoku.get(Figure.Block, sudoku.getContainingBlockNumber(row, col))[pos].posibilities.contains(number))
							sudoku.removePossibilityInBlock(sudoku.getContainingBlockNumber(row, col), pos, number);
					}
				}
			}
		}
	}

	private HashMap<Integer, ArrayList<Integer>> getPositionList(Figure figure,
			int id, Sudoku sudoku) throws Exception {

		HashMap<Integer, ArrayList<Integer>> positionList = new HashMap<Integer, ArrayList<Integer>>();

		Field[] fields = sudoku.get(figure, id);

		// iterate over every field of the figure
		for (int i = 0; i < 9; i++) {
			Field field = fields[i];
			// iterate over every possibility of the field
			for (int pos = 0; pos < field.posibilities.size(); pos++) {
				int number = field.posibilities.get(pos);

				// list of positions, where the number appears
				ArrayList<Integer> positions = new ArrayList<Integer>();

				// if the number already appeared then fetch the current
				// position list
				if (positionList.containsKey(number))
					positions = positionList.get(number);
				positions.add(i);

				// update the list
				positionList.put(number, positions);
			}
		}
		return positionList;
	}

	private boolean nakedSingles() throws Exception {
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				Field field = sudoku.getField(row, column);
				if (field.number == 0)
					if (field.posibilities.size() == 1) {
						int number = field.posibilities.get(0);
						sudoku.setField(row, column, new Field(number));
						System.out.println("exclude possibilities: " + row
								+ " / " + column + " -> " + number);
						return true;
					}
			}
		}
		return false;
	}
	
	private boolean hiddenSingles() throws Exception {

		// iterate over every figure
		for (int f = 0; f < 9; f++) {
			HashMap<Integer, ArrayList<Integer>> rowPositionList = getPositionList(
					Figure.Row, f, sudoku);
			HashMap<Integer, ArrayList<Integer>> colPositionList = getPositionList(
					Figure.Column, f, sudoku);
			HashMap<Integer, ArrayList<Integer>> blockPositionList = getPositionList(
					Figure.Block, f, sudoku);

			// iterate over every possible number
			for (int number = 1; number < 10; number++) {
				if (rowPositionList.containsKey(number)) {
					ArrayList<Integer> positions = rowPositionList.get(number);
					if (positions.size() == 1) {
						System.out.println("single -> row " + f + " col "
								+ positions.get(0) + " number " + number);
						sudoku.setField(f, positions.get(0), new Field(number));
						return true;
					}
				}
				if (colPositionList.containsKey(number)) {
					ArrayList<Integer> positions = colPositionList.get(number);
					if (positions.size() == 1) {
						System.out.println("single -> row " + positions.get(0)
								+ " col " + f + " number " + number);
						sudoku.setField(positions.get(0), f, new Field(number));
						return true;
					}
				}
				if (blockPositionList.containsKey(number)) {
					ArrayList<Integer> positions = blockPositionList
							.get(number);
					if (positions.size() == 1) {
						System.out.println("single -> block " + f
								+ " position " + positions.get(0) + " number "
								+ number);
						sudoku.setFieldInBlock(f, positions.get(0), new Field(
								number));
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean nakedTupel() throws Exception {
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
							return true;
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
							return true;
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
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean nakedTripel() throws Exception {
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
								return true;
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
								return true;
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
								return true;
							}
						}
					}
				}
			}

		}

		return false;
	}

	private boolean hiddenTupel() throws Exception {

		// iterate over every figure
		for (int f = 0; f < 9; f++) {
			HashMap<Integer, ArrayList<Integer>> rowPositionList = getPositionList(
					Figure.Row, f, sudoku);
			HashMap<Integer, ArrayList<Integer>> colPositionList = getPositionList(
					Figure.Column, f, sudoku);
			HashMap<Integer, ArrayList<Integer>> blockPositionList = getPositionList(
					Figure.Block, f, sudoku);
		}

		return false;
	}

}
