package solver;

import io.LogLevel;
import io.Logger;

import java.util.*;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;
import model.*;

public class Solver {

	private Sudoku sudoku;
	private int count;
	private boolean changed = true;
	private FeatureVector fv = new FeatureVector();

	public Solver(Sudoku sudoku) {
		try {
			this.sudoku = sudoku;
			updateConstraints();

			for (int i = 1; i < 10; i++) {
				int count = sudoku.numberCount(i);
				fv.addNumberCount(count);
			}

			for (int i = 1; i < 10; i++) {
				int count = sudoku.possibilityCount(i);
				fv.addPossibilityCount(count);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		count = 0;
	}

	public Sudoku getSudoku() {
		return sudoku;
	}

	public FeatureVector getFeatureVector() {
		return fv;
	}

	public Sudoku step() {
		if (sudoku.numberCount(0) == 0) {
			changed = false;
			return sudoku;
		}

		try {
			updateConstraints();

			switch (count) {
			case 0:
				if (nakedSingles()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods, "no naked single found");
					count++;
					break;
				}

			case 1:
				if (hiddenSingles()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods,
							"no hidden single found");
					count++;
					break;
				}

			case 2:
				if (intersections()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods,
							"no hidden single found");
					count++;
					break;
				}

			case 3:
				if (nakedPairs()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods, "no naked pair found");
					count++;
					break;
				}
			case 4:
				if (hiddenPairs()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods, "no hidden pair found");
					count++;
					break;
				}
			case 5:
				if (nakedTriples()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods, "no naked triple found");
					count++;
					break;
				}
			case 6:
				if (hiddenTriples()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods,
							"no hidden triple found");
					count++;
					break;
				}
			case 7:
				if (xWing()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods,
							"no hidden triple found");
					count++;
					break;
				}
			case 8:
				if (swordFish()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods,
							"no hidden triple found");
					count++;
					break;
				}
			case 9:
				if (jellyFish()) {
					count = 0;
					changed = true;
					break;
				} else {
					Logger.log(LogLevel.SolvingMethods,
							"no hidden triple found");
					count++;
					break;
				}
			case 10:
				changed = false;
				// compute how many numbers would have to be computed by
				// backtracking
				for (int i = 1; i < 10; i++) {
					fv.addMethod(Method.Backtracking, i,
							9 - sudoku.numberCount(i));
				}

				Logger.log(LogLevel.Debug, "not solvable");
				return sudoku;
			}
		} catch (Exception ex) {

		}
		return sudoku;
	}

	public Sudoku solve() {
		while (changed) {
			step();
		}
		return sudoku;
	}

	// LÖSUNGSTCHNIKEN

	// http://sudoku.soeinding.de/strategie/strategie02a.php
	private boolean nakedSingles() {

		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				Field field = sudoku.getField(row, column);
				if (field.number == 0)
					if (field.posibilities.size() == 1) {
						int number = field.posibilities.get(0);
						sudoku.setField(row, column, new Field(number));
						Logger.log(LogLevel.SolvingMethods, "naked single row "
								+ row + " column " + column + " number "
								+ number);
						fv.addMethod(Method.NakedSingles, number);
						return true;
					}
			}
		}
		return false;
	}

	// http://sudoku.soeinding.de/strategie/strategie02b.php
	private boolean hiddenSingles() {

		// iterate over every figure
		for (int f = 0; f < 9; f++) {
			HashMap<Integer, ArrayList<Integer>> rowPositionList = getPositionList(
					Figure.Row, f);
			HashMap<Integer, ArrayList<Integer>> colPositionList = getPositionList(
					Figure.Column, f);
			HashMap<Integer, ArrayList<Integer>> blockPositionList = getPositionList(
					Figure.Block, f);

			// iterate over every possible number
			for (int number = 1; number < 10; number++) {
				if (rowPositionList.containsKey(number)) {
					ArrayList<Integer> positions = rowPositionList.get(number);
					if (positions.size() == 1) {
						Logger.log(LogLevel.SolvingMethods,
								"hidden single  row " + f + " column "
										+ positions.get(0) + " number "
										+ number);
						sudoku.setField(f, positions.get(0), new Field(number));
						fv.addMethod(Method.HiddenSingles, number);
						return true;
					}
				}
				if (colPositionList.containsKey(number)) {
					ArrayList<Integer> positions = colPositionList.get(number);
					if (positions.size() == 1) {
						Logger.log(LogLevel.SolvingMethods,
								"hidden single row " + positions.get(0)
										+ " column " + f + " number " + number);
						sudoku.setField(positions.get(0), f, new Field(number));
						fv.addMethod(Method.HiddenSingles, number);
						return true;
					}
				}
				if (blockPositionList.containsKey(number)) {
					ArrayList<Integer> positions = blockPositionList
							.get(number);
					if (positions.size() == 1) {
						Logger.log(LogLevel.SolvingMethods,
								"hidden single block " + f + " position "
										+ positions.get(0) + " number "
										+ number);
						sudoku.setFieldInBlock(f, positions.get(0), new Field(
								number));
						fv.addMethod(Method.HiddenSingles, number);
						return true;
					}
				}
			}
		}
		return false;
	}

	// http://hodoku.sourceforge.net/de/tech_intersections.php
	private boolean intersections() {
		// block-row intersections

		// iterate over every block
		for (int blockId = 0; blockId < 9; blockId++) {
			Field[] block = sudoku.get(Figure.Block, blockId);

			// generate 3 lists with all possibilities of the row
			ArrayList<Integer> rowList1 = new ArrayList<Integer>();
			ArrayList<Integer> rowList2 = new ArrayList<Integer>();
			ArrayList<Integer> rowList3 = new ArrayList<Integer>();

			ArrayList<Integer> colList1 = new ArrayList<Integer>();
			ArrayList<Integer> colList2 = new ArrayList<Integer>();
			ArrayList<Integer> colList3 = new ArrayList<Integer>();

			// iterate over every field
			for (int i = 0; i < 3; i++) {
				if (block[i].number == 0)
					rowList1 = disjunction(rowList1, block[i].posibilities);
				if (block[i * 3].number == 0)
					colList1 = disjunction(colList1, block[i * 3].posibilities);
				if (block[3 + i].number == 0)
					rowList2 = disjunction(rowList2, block[3 + i].posibilities);
				if (block[i * 3 + 1].number == 0)
					colList2 = disjunction(colList2,
							block[i * 3 + 1].posibilities);
				if (block[6 + i].number == 0)
					rowList3 = disjunction(rowList3, block[6 + i].posibilities);
				if (block[i * 3 + 2].number == 0)
					colList3 = disjunction(colList3,
							block[i * 3 + 2].posibilities);
			}

			// compute the numbers that are in one row/column only

			// first row
			ArrayList<Integer> firstRow = difference(
					difference(rowList1, rowList2), rowList3);
			// second row
			ArrayList<Integer> secondRow = difference(
					difference(rowList2, rowList1), rowList3);
			// thrird row
			ArrayList<Integer> thirdRow = difference(
					difference(rowList3, rowList1), rowList2);

			// first column
			ArrayList<Integer> firstColumn = difference(
					difference(colList1, colList2), colList3);

			// second column
			ArrayList<Integer> secondColumn = difference(
					difference(colList2, colList1), colList3);

			// third column
			ArrayList<Integer> thirdColumn = difference(
					difference(colList3, colList1), colList2);

			ArrayList<ArrayList<Integer>> allRowLists = new ArrayList<ArrayList<Integer>>();
			allRowLists.add(firstRow);
			allRowLists.add(secondRow);
			allRowLists.add(thirdRow);

			ArrayList<ArrayList<Integer>> allColumnLists = new ArrayList<ArrayList<Integer>>();
			allColumnLists.add(firstColumn);
			allColumnLists.add(secondColumn);
			allColumnLists.add(thirdColumn);

			// iterate over every row/column in the block
			for (int listNumber = 0; listNumber < 3; listNumber++) {

				// get the numbers that are only in one row/column only
				ArrayList<Integer> numbersRow = allRowLists.get(listNumber);
				ArrayList<Integer> numbersColumn = allColumnLists
						.get(listNumber);

				if (numbersRow.size() > 0) {
					// compute the global offset of the row by the block ID
					int offset = 0;

					if (blockId >= 3)
						offset = 3;
					if (blockId >= 6)
						offset = 6;

					Field[] row = sudoku.get(Figure.Row, offset + listNumber);

					int posCount = 0;

					// iterate over all fields of the row
					for (int fieldId = 0; fieldId < 9; fieldId++) {
						// check if the fields of the row are not in the current
						// block
						if (!(blockId % 3 == 0
								&& (fieldId == 0 || fieldId == 1 || fieldId == 2)
								|| blockId % 3 == 1
								&& (fieldId == 3 || fieldId == 4 || fieldId == 5) || blockId % 3 == 2
								&& (fieldId == 6 || fieldId == 7 || fieldId == 8))) {
							// iterate over all found numbers
							for (int i = 0; i < numbersRow.size(); i++) {
								// if there is a possibility in the row then
								// remove it
								if (row[fieldId].posibilities
										.contains(numbersRow.get(i))) {
									sudoku.removePosibility(
											offset + listNumber, fieldId,
											numbersRow.get(i));
									fv.addMethod(Method.Intersections,
											numbersRow.get(i));
									posCount++;
								}
							}
						}
					}

					if (posCount > 0) {
						Logger.log(LogLevel.SolvingMethods,
								"block-row intersection block " + blockId
										+ " removed " + posCount
										+ " possibilities");
						return true;
					}
				}

				if (numbersColumn.size() > 0) {

					// compute the global offset of the column by the block ID
					int offset = blockId % 3 * 3;

					Field[] column = sudoku.get(Figure.Column, offset
							+ listNumber);

					int posCount = 0;

					// iterate over all fields in the column
					for (int fieldId = 0; fieldId < 9; fieldId++) {
						// check if the fields of the column are not in the
						// current block
						if ((blockId < 3 && fieldId != 0 && fieldId != 1 && fieldId != 2)
								|| (blockId >= 3 && blockId < 6 && fieldId != 3
										&& fieldId != 4 && fieldId != 5)
								|| (blockId >= 6 && fieldId != 6
										&& fieldId != 7 && fieldId != 8)) {
							// iterate over all found numbers
							for (int i = 0; i < numbersColumn.size(); i++) {
								if (column[fieldId].posibilities
										.contains(numbersColumn.get(i))) {
									sudoku.removePosibility(fieldId, offset
											+ listNumber, numbersColumn.get(i));
									fv.addMethod(Method.Intersections,
											numbersColumn.get(i));
									posCount++;
								}
							}
						}
					}

					if (posCount > 0) {
						Logger.log(LogLevel.SolvingMethods,
								"block-column intersection block " + blockId
										+ " removed " + posCount
										+ " possibilities");
						return true;
					}
				}
			}

		}

		// row-block intersections

		// iterate over every row and column
		for (int figure = 0; figure < 9; figure++) {

			// get the row and the column
			Field[] row = sudoku.get(Figure.Row, figure);
			Field[] column = sudoku.get(Figure.Column, figure);

			// generate 3 lists with all possibilities of the row / column in
			// the block
			ArrayList<Integer> rowList1 = new ArrayList<Integer>();
			ArrayList<Integer> rowList2 = new ArrayList<Integer>();
			ArrayList<Integer> rowList3 = new ArrayList<Integer>();

			ArrayList<Integer> colList1 = new ArrayList<Integer>();
			ArrayList<Integer> colList2 = new ArrayList<Integer>();
			ArrayList<Integer> colList3 = new ArrayList<Integer>();

			// iterate over every field
			for (int i = 0; i < 3; i++) {
				if (row[i].number == 0)
					rowList1 = disjunction(rowList1, row[i].posibilities);
				if (column[i].number == 0)
					colList1 = disjunction(colList1, column[i].posibilities);
				if (row[i + 3].number == 0)
					rowList2 = disjunction(rowList2, row[i + 3].posibilities);
				if (column[i + 3].number == 0)
					colList2 = disjunction(colList2, column[i + 3].posibilities);
				if (row[i + 6].number == 0)
					rowList3 = disjunction(rowList3, row[i + 6].posibilities);
				if (column[i + 6].number == 0)
					colList3 = disjunction(colList3, column[i + 6].posibilities);
			}

			// compute the numbers that are in one block only
			// first row
			ArrayList<Integer> firstRow = difference(
					difference(rowList1, rowList2), rowList3);
			// second row
			ArrayList<Integer> secondRow = difference(
					difference(rowList2, rowList1), rowList3);
			// thrird row
			ArrayList<Integer> thirdRow = difference(
					difference(rowList3, rowList1), rowList2);

			// first column
			ArrayList<Integer> firstColumn = difference(
					difference(colList1, colList2), colList3);

			// second column
			ArrayList<Integer> secondColumn = difference(
					difference(colList2, colList1), colList3);

			// third column
			ArrayList<Integer> thirdColumn = difference(
					difference(colList3, colList1), colList2);

			ArrayList<ArrayList<Integer>> allRowLists = new ArrayList<ArrayList<Integer>>();
			allRowLists.add(firstRow);
			allRowLists.add(secondRow);
			allRowLists.add(thirdRow);

			ArrayList<ArrayList<Integer>> allColumnLists = new ArrayList<ArrayList<Integer>>();
			allColumnLists.add(firstColumn);
			allColumnLists.add(secondColumn);
			allColumnLists.add(thirdColumn);

			// iterate over every row/column in the block
			for (int listNumber = 0; listNumber < 3; listNumber++) {
				ArrayList<Integer> rowList = allRowLists.get(listNumber);
				ArrayList<Integer> colList = allColumnLists.get(listNumber);

				// rows
				if (rowList.size() > 0) {
					// compute the block id
					int blockOffset = 0;
					if (figure > 2)
						blockOffset = 3;
					if (figure > 5)
						blockOffset = 6;
					int blockId = blockOffset + listNumber;

					Field[] block = sudoku.get(Figure.Block, blockId);

					int posCount = 0;

					// iterate over every field in the block
					for (int fieldId = 0; fieldId < 9; fieldId++) {
						// check if the fields of the block are not in the
						// current row
						if ((figure % 3) * 3 != fieldId
								&& (figure % 3) * 3 + 1 != fieldId
								&& (figure % 3) * 3 + 2 != fieldId) {
							Field field = block[fieldId];

							// iterate over all found numbers
							for (int i = 0; i < rowList.size(); i++) {
								int number = rowList.get(i);

								if (field.number == 0
										&& field.posibilities.contains(number)) {
									sudoku.removePossibilityInBlock(blockId,
											fieldId, number);
									fv.addMethod(Method.Intersections, number);
									posCount++;
								}
							}
						}
					}

					if (posCount > 0) {
						Logger.log(LogLevel.SolvingMethods,
								"row-block intersection row " + figure
										+ " removed " + posCount
										+ " possibilities");
						return true;
					}
				}

				// columns
				if (colList.size() > 0) {
					// compute the block id
					int blockOffset = 0;
					if (figure > 2)
						blockOffset = 1;
					if (figure > 5)
						blockOffset = 2;
					int blockId = blockOffset + 3 * listNumber;

					Field[] block = sudoku.get(Figure.Block, blockId);

					int posCount = 0;

					// iterate over every field in the block
					for (int fieldId = 0; fieldId < 9; fieldId++) {
						// check if the fields of the block are not in the
						// current row
						if (figure % 3 != fieldId && figure % 3 + 3 != fieldId
								&& figure % 3 + 6 != fieldId) {
							Field field = block[fieldId];

							// iterate over all found numbers
							for (int i = 0; i < colList.size(); i++) {
								int number = colList.get(i);

								if (field.number == 0
										&& field.posibilities.contains(number)) {
									sudoku.removePossibilityInBlock(blockId,
											fieldId, number);
									fv.addMethod(Method.Intersections, number);
									posCount++;
								}
							}
						}
					}

					if (posCount > 0) {
						Logger.log(LogLevel.SolvingMethods,
								"col-block intersection column " + figure
										+ " removed " + posCount
										+ " possibilities");
						return true;
					}
				}
			}

		}

		return false;
	}

	private boolean nakedPairs() {
		// iterate over every figure
		for (int f = 0; f < 9; f++) {
			Field[] row = sudoku.get(Figure.Row, f);
			Field[] col = sudoku.get(Figure.Column, f);
			Field[] block = sudoku.get(Figure.Block, f);

			// compare 2 fields
			for (int i = 0; i < 9; i++) {
				for (int j = i + 1; j < 9; j++) {
					ArrayList<Integer> rowPos1 = row[i].posibilities;
					ArrayList<Integer> rowPos2 = row[j].posibilities;

					ArrayList<Integer> colPos1 = col[i].posibilities;
					ArrayList<Integer> colPos2 = col[j].posibilities;

					ArrayList<Integer> blockPos1 = block[i].posibilities;
					ArrayList<Integer> blockPos2 = block[j].posibilities;

					// row
					if (rowPos1.size() != 0 && rowPos2.size() != 0) {
						ArrayList<Integer> disj = disjunction(rowPos1, rowPos2);

						if (disj.size() == 2) {
							int posCount = 0;

							for (int pos = 0; pos < 9; pos++) {
								if (pos != i && pos != j) {
									for (int number = 0; number < 2; number++) {
										if (row[pos].posibilities.contains(disj
												.get(number))) {
											sudoku.removePosibility(f, pos,
													disj.get(number));
											fv.addMethod(Method.NakedPair,
													number);
											posCount++;
										}
									}
								}
							}

							if (posCount > 0) {
								Logger.log(LogLevel.SolvingMethods,
										"naked tupel row " + f + " positions "
												+ i + ", " + j + " removed "
												+ posCount + " possibilities");
								return true;
							}
						}
					}

					// column
					if (colPos1.size() != 0 && colPos2.size() != 0) {
						ArrayList<Integer> disj = disjunction(colPos1, colPos2);

						if (disj.size() == 2) {
							int posCount = 0;

							for (int pos = 0; pos < 9; pos++) {
								if (pos != i && pos != j) {
									for (int number = 0; number < 2; number++) {
										if (col[pos].posibilities.contains(disj
												.get(number))) {
											sudoku.removePosibility(pos, f,
													disj.get(number));
											fv.addMethod(Method.NakedPair,
													number);
											posCount++;
										}
									}
								}
							}

							if (posCount > 0) {
								Logger.log(LogLevel.SolvingMethods,
										"naked tupel col " + f + " positions "
												+ i + ", " + j + " removed "
												+ posCount + " possibilities");
								return true;
							}
						}
					}

					// block
					if (blockPos1.size() != 0 && blockPos2.size() != 0) {
						ArrayList<Integer> disj = disjunction(blockPos1,
								blockPos2);

						if (disj.size() == 2) {
							int posCount = 0;

							for (int pos = 0; pos < 9; pos++) {
								if (pos != i && pos != j) {
									for (int number = 0; number < 2; number++) {
										if (block[pos].posibilities
												.contains(disj.get(number))) {
											sudoku.removePossibilityInBlock(f,
													pos, disj.get(number));
											fv.addMethod(Method.NakedPair,
													number);
											posCount++;
										}
									}
								}
							}

							if (posCount > 0) {
								Logger.log(LogLevel.SolvingMethods,
										"naked tupel block " + f
												+ " positions " + i + ", " + j
												+ " removed " + posCount
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

	private boolean hiddenPairs() {
		// compare two numbers

		// iterate over every figure
		for (int figure = 0; figure < 9; figure++) {
			// get the position lists
			HashMap<Integer, ArrayList<Integer>> rowPositionList = getPositionList(
					Figure.Row, figure);
			HashMap<Integer, ArrayList<Integer>> colPositionList = getPositionList(
					Figure.Column, figure);
			HashMap<Integer, ArrayList<Integer>> blockPositionList = getPositionList(
					Figure.Block, figure);

			for (int n1 = 1; n1 < 10; n1++) {
				for (int n2 = n1 + 1; n2 < 10; n2++) {
					// row
					if (rowPositionList.containsKey(n1)
							&& rowPositionList.containsKey(n2)) {

						ArrayList<Integer> disj = disjunction(
								rowPositionList.get(n1),
								rowPositionList.get(n2));
						if (disj.size() == 2) {
							int posCount = 0;

							for (int number = 1; number < 10; number++) {
								if (number != n1 && number != n2) {
									Field field1 = sudoku.getField(figure,
											disj.get(0));
									Field field2 = sudoku.getField(figure,
											disj.get(1));

									if (field1.posibilities.contains(number)) {
										sudoku.removePosibility(figure,
												disj.get(0), number);
										fv.addMethod(Method.HiddenPair, number);
										posCount++;
									}
									if (field2.posibilities.contains(number)) {
										sudoku.removePosibility(figure,
												disj.get(1), number);
										fv.addMethod(Method.HiddenPair, number);
										posCount++;
									}
								}
							}

							if (posCount > 0) {
								Logger.log(LogLevel.SolvingMethods,
										"hidden pair row " + figure
												+ " positions " + disj.get(0)
												+ ", " + disj.get(1)
												+ " removed " + posCount
												+ " possibilities");
								return true;
							}
						}

					}

					// column
					if (colPositionList.containsKey(n1)
							&& colPositionList.containsKey(n2)) {

						ArrayList<Integer> disj = disjunction(
								colPositionList.get(n1),
								colPositionList.get(n2));
						if (disj.size() == 2) {
							int posCount = 0;

							for (int number = 1; number < 10; number++) {
								if (number != n1 && number != n2) {
									Field field1 = sudoku.getField(disj.get(0),
											figure);
									Field field2 = sudoku.getField(disj.get(1),
											figure);

									if (field1.posibilities.contains(number)) {
										sudoku.removePosibility(disj.get(0),
												figure, number);
										fv.addMethod(Method.HiddenPair, number);
										posCount++;
									}
									if (field2.posibilities.contains(number)) {
										sudoku.removePosibility(disj.get(1),
												figure, number);
										fv.addMethod(Method.HiddenPair, number);
										posCount++;
									}
								}
							}

							if (posCount > 0) {
								Logger.log(LogLevel.SolvingMethods,
										"hidden pair column " + figure
												+ " positions " + disj.get(0)
												+ ", " + disj.get(1)
												+ " removed " + posCount
												+ " possibilities");
								return true;
							}
						}

					}

					// block
					if (blockPositionList.containsKey(n1)
							&& blockPositionList.containsKey(n2)) {

						ArrayList<Integer> disj = disjunction(
								blockPositionList.get(n1),
								blockPositionList.get(n2));
						if (disj.size() == 2) {
							int posCount = 0;

							for (int number = 1; number < 10; number++) {
								if (number != n1 && number != n2) {
									Field[] block = sudoku.get(Figure.Block,
											figure);
									Field field1 = block[disj.get(0)];
									Field field2 = block[disj.get(1)];

									if (field1.posibilities.contains(number)) {
										sudoku.removePossibilityInBlock(figure,
												disj.get(0), number);
										fv.addMethod(Method.HiddenPair, number);
										posCount++;
									}
									if (field2.posibilities.contains(number)) {
										sudoku.removePossibilityInBlock(figure,
												disj.get(1), number);
										fv.addMethod(Method.HiddenPair, number);
										posCount++;
									}
								}
							}

							if (posCount > 0) {
								Logger.log(LogLevel.SolvingMethods,
										"hidden pair block " + figure
												+ " positions " + disj.get(0)
												+ ", " + disj.get(1)
												+ " removed " + posCount
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

	private boolean nakedTriples() {
		// iterate over every figure
		for (int f = 0; f < 9; f++) {
			Field[] row = sudoku.get(Figure.Row, f);
			Field[] col = sudoku.get(Figure.Column, f);
			Field[] block = sudoku.get(Figure.Block, f);

			// compare 3 fields
			for (int i = 0; i < 9; i++) {
				for (int j = i + 1; j < 9; j++) {
					for (int k = j + 1; k < 9; k++) {
						ArrayList<Integer> rowPos1 = row[i].posibilities;
						ArrayList<Integer> rowPos2 = row[j].posibilities;
						ArrayList<Integer> rowPos3 = row[k].posibilities;

						ArrayList<Integer> colPos1 = col[i].posibilities;
						ArrayList<Integer> colPos2 = col[j].posibilities;
						ArrayList<Integer> colPos3 = col[k].posibilities;

						ArrayList<Integer> blockPos1 = block[i].posibilities;
						ArrayList<Integer> blockPos2 = block[j].posibilities;
						ArrayList<Integer> blockPos3 = block[k].posibilities;

						// row
						if (rowPos1.size() != 0 && rowPos2.size() != 0
								&& rowPos3.size() != 0) {
							ArrayList<Integer> disj = disjunction(rowPos1,
									disjunction(rowPos2, rowPos3));

							if (disj.size() == 3) {
								int posCount = 0;

								for (int pos = 0; pos < 9; pos++) {
									if (pos != i && pos != j && pos != k) {
										for (int number = 0; number < 3; number++) {
											if (row[pos].posibilities
													.contains(disj.get(number))) {
												sudoku.removePosibility(f, pos,
														disj.get(number));
												fv.addMethod(
														Method.NakedTriple,
														number);
												posCount++;
											}
										}
									}
								}

								if (posCount > 0) {
									Logger.log(LogLevel.SolvingMethods,
											"naked triple row " + f
													+ " positions " + i + ", "
													+ j + ", " + k
													+ " removed " + posCount
													+ " possibilities");
									return true;
								}
							}
						}

						// column
						if (colPos1.size() != 0 && colPos2.size() != 0
								&& colPos3.size() != 0) {
							ArrayList<Integer> disj = disjunction(colPos1,
									disjunction(colPos2, colPos3));

							if (disj.size() == 3) {
								int posCount = 0;

								for (int pos = 0; pos < 9; pos++) {
									if (pos != i && pos != j && pos != k) {
										for (int number = 0; number < 3; number++) {
											if (col[pos].posibilities
													.contains(disj.get(number))) {
												sudoku.removePosibility(pos, f,
														disj.get(number));
												fv.addMethod(
														Method.NakedTriple,
														number);
												posCount++;
											}
										}
									}
								}

								if (posCount > 0) {
									Logger.log(LogLevel.SolvingMethods,
											"naked triple col " + f
													+ " positions " + i + ", "
													+ j + ", " + k
													+ " removed " + posCount
													+ " possibilities");
									return true;
								}
							}
						}

						// block
						if (blockPos1.size() != 0 && blockPos2.size() != 0
								&& blockPos3.size() != 0) {
							ArrayList<Integer> disj = disjunction(blockPos1,
									disjunction(blockPos2, blockPos3));

							if (disj.size() == 3) {
								int posCount = 0;

								for (int pos = 0; pos < 9; pos++) {
									if (pos != i && pos != j && pos != k) {
										for (int number = 0; number < 3; number++) {
											if (block[pos].posibilities
													.contains(disj.get(number))) {
												sudoku.removePossibilityInBlock(
														f, pos,
														disj.get(number));
												fv.addMethod(
														Method.NakedTriple,
														number);
												posCount++;
											}
										}
									}
								}

								if (posCount > 0) {
									Logger.log(LogLevel.SolvingMethods,
											"naked triple block " + f
													+ " positions " + i + ", "
													+ j + ", " + k
													+ " removed " + posCount
													+ " possibilities");
									return true;
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean hiddenTriples() {
		// compare three numbers

		// iterate over every figure
		for (int figure = 0; figure < 9; figure++) {
			// get the position lists
			HashMap<Integer, ArrayList<Integer>> rowPositionList = getPositionList(
					Figure.Row, figure);
			HashMap<Integer, ArrayList<Integer>> colPositionList = getPositionList(
					Figure.Column, figure);
			HashMap<Integer, ArrayList<Integer>> blockPositionList = getPositionList(
					Figure.Block, figure);

			for (int n1 = 1; n1 < 10; n1++) {
				for (int n2 = n1 + 1; n2 < 10; n2++) {
					for (int n3 = n2 + 1; n3 < 10; n3++) {
						// row
						if (rowPositionList.containsKey(n1)
								&& rowPositionList.containsKey(n2)
								&& rowPositionList.containsKey(n3)) {

							ArrayList<Integer> disj = disjunction(
									rowPositionList.get(n1),
									disjunction(rowPositionList.get(n2),
											rowPositionList.get(n3)));
							if (disj.size() == 3) {
								int posCount = 0;

								// System.out.println("figure " + figure);
								// System.out.println("n1 " + n1);
								// System.out.println("n2 " + n2);
								// System.out.println("n3 " + n3);
								// System.out.println(disj);

								for (int number = 1; number < 10; number++) {
									if (number != n1 && number != n2
											&& number != n3) {
										Field field1 = sudoku.getField(figure,
												disj.get(0));
										Field field2 = sudoku.getField(figure,
												disj.get(1));
										Field field3 = sudoku.getField(figure,
												disj.get(2));

										if (field1.posibilities
												.contains(number)) {
											sudoku.removePosibility(figure,
													disj.get(0), number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
										if (field2.posibilities
												.contains(number)) {
											sudoku.removePosibility(figure,
													disj.get(1), number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
										if (field3.posibilities
												.contains(number)) {
											sudoku.removePosibility(figure,
													disj.get(2), number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
									}
								}

								if (posCount > 0) {
									Logger.log(
											LogLevel.SolvingMethods,
											"hidden triple row " + figure
													+ " positions "
													+ disj.get(0) + ", "
													+ disj.get(1) + ", "
													+ disj.get(2) + " removed "
													+ posCount
													+ " possibilities");
									return true;
								}
							}

						}

						// column
						if (colPositionList.containsKey(n1)
								&& colPositionList.containsKey(n2)
								&& colPositionList.containsKey(n3)) {

							ArrayList<Integer> disj = disjunction(
									colPositionList.get(n1),
									disjunction(colPositionList.get(n2),
											colPositionList.get(n3)));
							if (disj.size() == 3) {
								int posCount = 0;

								for (int number = 1; number < 10; number++) {
									if (number != n1 && number != n2
											&& number != n3) {
										Field field1 = sudoku.getField(
												disj.get(0), figure);
										Field field2 = sudoku.getField(
												disj.get(1), figure);
										Field field3 = sudoku.getField(
												disj.get(2), figure);

										if (field1.posibilities
												.contains(number)) {
											sudoku.removePosibility(
													disj.get(0), figure, number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
										if (field2.posibilities
												.contains(number)) {
											sudoku.removePosibility(
													disj.get(1), figure, number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
										if (field3.posibilities
												.contains(number)) {
											sudoku.removePosibility(
													disj.get(2), figure, number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
									}
								}

								if (posCount > 0) {
									Logger.log(
											LogLevel.SolvingMethods,
											"hidden triple column " + figure
													+ " positions "
													+ disj.get(0) + ", "
													+ disj.get(1) + ", "
													+ disj.get(2) + " removed "
													+ posCount
													+ " possibilities");
									return true;
								}
							}

						}

						// block
						if (blockPositionList.containsKey(n1)
								&& blockPositionList.containsKey(n2)
								&& blockPositionList.containsKey(n3)) {

							ArrayList<Integer> disj = disjunction(
									blockPositionList.get(n1),
									disjunction(blockPositionList.get(n2),
											blockPositionList.get(n3)));
							if (disj.size() == 3) {
								int posCount = 0;

								for (int number = 1; number < 10; number++) {
									if (number != n1 && number != n2
											&& number != n3) {
										Field[] block = sudoku.get(
												Figure.Block, figure);
										Field field1 = block[disj.get(0)];
										Field field2 = block[disj.get(1)];
										Field field3 = block[disj.get(2)];

										if (field1.posibilities
												.contains(number)) {
											sudoku.removePossibilityInBlock(
													figure, disj.get(0), number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
										if (field2.posibilities
												.contains(number)) {
											sudoku.removePossibilityInBlock(
													figure, disj.get(1), number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
										if (field3.posibilities
												.contains(number)) {
											sudoku.removePossibilityInBlock(
													figure, disj.get(2), number);
											fv.addMethod(Method.HiddenTriple,
													number);
											posCount++;
										}
									}
								}

								if (posCount > 0) {
									Logger.log(
											LogLevel.SolvingMethods,
											"hidden pair block " + figure
													+ " positions "
													+ disj.get(0) + ", "
													+ disj.get(1) + ", "
													+ disj.get(2) + " removed "
													+ posCount
													+ " possibilities");
									return true;
								}
							}

						}
					}
				}
			}
		}

		return false;
	}

	private boolean xWing() {
		for (int number = 1; number < 10; number++) {
			for (int f1 = 0; f1 < 9; f1++) {
				for (int f2 = f1 + 1; f2 < 9; f2++) {

					ArrayList<Integer> rowPositionList1 = getPositionList(
							Figure.Row, f1).get(number);
					ArrayList<Integer> rowPositionList2 = getPositionList(
							Figure.Row, f2).get(number);

					ArrayList<Integer> colPositionList1 = getPositionList(
							Figure.Column, f1).get(number);
					ArrayList<Integer> colPositionList2 = getPositionList(
							Figure.Column, f2).get(number);

					// rows
					if (rowPositionList1 != null && rowPositionList2 != null
							&& areEqual(rowPositionList1, rowPositionList2)
							&& rowPositionList1.size() == 2) {

						int posCount = 0;

						// iterate over the columns
						for (int i = 0; i < 9; i++) {
							// dont remove the possibilities in the crossing
							// rows
							if (i != f1 && i != f2) {
								Field field1 = sudoku.getField(i,
										rowPositionList1.get(0));
								Field field2 = sudoku.getField(i,
										rowPositionList1.get(1));
								if (field1.posibilities.contains(number)) {
									sudoku.removePosibility(i,
											rowPositionList1.get(0), number);
									posCount++;
								}
								if (field2.posibilities.contains(number)) {
									sudoku.removePosibility(i,
											rowPositionList1.get(1), number);
									posCount++;
								}
							}
						}

						if (posCount > 0) {
							Logger.log(LogLevel.SolvingMethods, "x Wing rows "
									+ f1 + ", " + f2 + " number " + number
									+ " removed " + posCount + " possibilities");
							fv.addMethod(Method.xWing, number, posCount);
							return true;
						}
					}

					// columns
					if (colPositionList1 != null && colPositionList2 != null
							&& areEqual(colPositionList1, colPositionList2)
							&& colPositionList1.size() == 2) {

						int posCount = 0;

						// iterate over the rows
						for (int i = 0; i < 9; i++) {
							// dont remove the possibilities in the crossing
							// columns
							if (i != f1 && i != f2) {
								Field field1 = sudoku.getField(
										colPositionList1.get(0), i);
								Field field2 = sudoku.getField(
										colPositionList1.get(1), i);
								if (field1.posibilities.contains(number)) {
									sudoku.removePosibility(
											colPositionList1.get(0), i, number);
									posCount++;
								}
								if (field2.posibilities.contains(number)) {
									sudoku.removePosibility(
											colPositionList1.get(1), i, number);
									posCount++;
								}
							}
						}

						if (posCount > 0) {
							Logger.log(LogLevel.SolvingMethods,
									"x Wing columns " + f1 + ", " + f2
											+ " number " + number + " removed "
											+ posCount + " possibilities");
							fv.addMethod(Method.xWing, number, posCount);
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean swordFish() {

		for (int number = 1; number < 10; number++) {
			for (int f1 = 0; f1 < 9; f1++) {
				for (int f2 = f1 + 1; f2 < 9; f2++) {
					for (int f3 = f2 + 1; f3 < 9; f3++) {

						ArrayList<Integer> rowPositionList1 = getPositionList(
								Figure.Row, f1).get(number);
						ArrayList<Integer> rowPositionList2 = getPositionList(
								Figure.Row, f2).get(number);
						ArrayList<Integer> rowPositionList3 = getPositionList(
								Figure.Row, f3).get(number);

						ArrayList<Integer> colPositionList1 = getPositionList(
								Figure.Column, f1).get(number);
						ArrayList<Integer> colPositionList2 = getPositionList(
								Figure.Column, f2).get(number);
						ArrayList<Integer> colPositionList3 = getPositionList(
								Figure.Column, f3).get(number);

						// rows
						if (rowPositionList1 != null
								&& rowPositionList2 != null
								&& rowPositionList3 != null) {

							ArrayList<Integer> rowPositionList = disjunction(
									rowPositionList1,
									disjunction(rowPositionList2,
											rowPositionList3));

							if (rowPositionList.size() == 3) {
								int posCount = 0;

								// iterate over the columns
								for (int i = 0; i < 9; i++) {
									// dont remove the possibilities in the
									// crossing
									// rows
									if (i != f1 && i != f2 && i != f3) {
										Field field1 = sudoku.getField(i,
												rowPositionList.get(0));
										Field field2 = sudoku.getField(i,
												rowPositionList.get(1));
										Field field3 = sudoku.getField(i,
												rowPositionList.get(2));
										if (field1.posibilities
												.contains(number)) {
											sudoku.removePosibility(i,
													rowPositionList.get(0),
													number);
											posCount++;
										}
										if (field2.posibilities
												.contains(number)) {
											sudoku.removePosibility(i,
													rowPositionList.get(1),
													number);
											posCount++;
										}
										if (field3.posibilities
												.contains(number)) {
											sudoku.removePosibility(i,
													rowPositionList.get(2),
													number);
											posCount++;
										}
									}
								}

								if (posCount > 0) {
									Logger.log(LogLevel.SolvingMethods,
											"swordfish rows " + f1 + ", " + f2
													+ ", " + f3 + " number "
													+ number + " removed "
													+ posCount
													+ " possibilities");
									fv.addMethod(Method.swordFish, number,
											posCount);
									return true;
								}
							}
						}

						// columns
						if (colPositionList1 != null
								&& colPositionList2 != null
								&& colPositionList3 != null) {

							ArrayList<Integer> colPositionList = disjunction(
									colPositionList1,
									disjunction(colPositionList2,
											colPositionList3));
							if (colPositionList.size() == 3) {
								int posCount = 0;

								// iterate over the rows
								for (int i = 0; i < 9; i++) {
									// dont remove the possibilities in the
									// crossing
									// columns
									if (i != f1 && i != f2 && i != f3) {
										Field field1 = sudoku.getField(
												colPositionList.get(0), i);
										Field field2 = sudoku.getField(
												colPositionList.get(1), i);
										Field field3 = sudoku.getField(
												colPositionList.get(2), i);
										if (field1.posibilities
												.contains(number)) {
											sudoku.removePosibility(
													colPositionList.get(0), i,
													number);
											posCount++;
										}
										if (field2.posibilities
												.contains(number)) {
											sudoku.removePosibility(
													colPositionList.get(1), i,
													number);
											posCount++;
										}
										if (field3.posibilities
												.contains(number)) {
											sudoku.removePosibility(
													colPositionList.get(2), i,
													number);
											posCount++;
										}
									}
								}

								if (posCount > 0) {
									Logger.log(LogLevel.SolvingMethods,
											"swordfish columns " + f1 + ", "
													+ f2 + ", " + f3
													+ " number " + number
													+ " removed " + posCount
													+ " possibilities");
									fv.addMethod(Method.swordFish, number,
											posCount);
									return true;
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean jellyFish() {

		for (int number = 1; number < 10; number++) {
			for (int f1 = 0; f1 < 9; f1++) {
				for (int f2 = f1 + 1; f2 < 9; f2++) {
					for (int f3 = f2 + 1; f3 < 9; f3++) {
						for (int f4 = f3 + 1; f4 < 9; f4++) {

							ArrayList<Integer> rowPositionList1 = getPositionList(
									Figure.Row, f1).get(number);
							ArrayList<Integer> rowPositionList2 = getPositionList(
									Figure.Row, f2).get(number);
							ArrayList<Integer> rowPositionList3 = getPositionList(
									Figure.Row, f3).get(number);
							ArrayList<Integer> rowPositionList4 = getPositionList(
									Figure.Row, f4).get(number);

							ArrayList<Integer> colPositionList1 = getPositionList(
									Figure.Column, f1).get(number);
							ArrayList<Integer> colPositionList2 = getPositionList(
									Figure.Column, f2).get(number);
							ArrayList<Integer> colPositionList3 = getPositionList(
									Figure.Column, f3).get(number);
							ArrayList<Integer> colPositionList4 = getPositionList(
									Figure.Column, f4).get(number);

							// rows
							if (rowPositionList1 != null
									&& rowPositionList2 != null
									&& rowPositionList3 != null
									&& rowPositionList4 != null) {

								ArrayList<Integer> rowPositionList = disjunction(
										rowPositionList1,
										disjunction(
												rowPositionList2,
												disjunction(rowPositionList3,
														rowPositionList4)));

								if (rowPositionList.size() == 4) {
									int posCount = 0;

									// iterate over the columns
									for (int i = 0; i < 9; i++) {
										// dont remove the possibilities in the
										// crossing
										// rows
										if (i != f1 && i != f2 && i != f3
												&& i != f4) {
											Field field1 = sudoku.getField(i,
													rowPositionList.get(0));
											Field field2 = sudoku.getField(i,
													rowPositionList.get(1));
											Field field3 = sudoku.getField(i,
													rowPositionList.get(2));
											Field field4 = sudoku.getField(i,
													rowPositionList.get(3));
											if (field1.posibilities
													.contains(number)) {
												sudoku.removePosibility(i,
														rowPositionList.get(0),
														number);
												posCount++;
											}
											if (field2.posibilities
													.contains(number)) {
												sudoku.removePosibility(i,
														rowPositionList.get(1),
														number);
												posCount++;
											}
											if (field3.posibilities
													.contains(number)) {
												sudoku.removePosibility(i,
														rowPositionList.get(2),
														number);
												posCount++;
											}
											if (field4.posibilities
													.contains(number)) {
												sudoku.removePosibility(i,
														rowPositionList.get(3),
														number);
												posCount++;
											}
										}
									}

									if (posCount > 0) {
										Logger.log(LogLevel.SolvingMethods,
												"jellyfish rows " + f1 + ", "
														+ f2 + ", " + f3 + ", "
														+ f4 + " number "
														+ number + " removed "
														+ posCount
														+ " possibilities");
										fv.addMethod(Method.jellyFish, number,
												posCount);
										return true;
									}
								}
							}

							// columns
							if (colPositionList1 != null
									&& colPositionList2 != null
									&& colPositionList3 != null
									&& colPositionList4 != null) {

								ArrayList<Integer> colPositionList = disjunction(
										colPositionList1,
										disjunction(
												colPositionList2,
												disjunction(colPositionList3,
														colPositionList4)));
								if (colPositionList.size() == 4) {
									int posCount = 0;

									// iterate over the rows
									for (int i = 0; i < 9; i++) {
										// dont remove the possibilities in the
										// crossing
										// columns
										if (i != f1 && i != f2 && i != f3
												&& i != f4) {
											Field field1 = sudoku.getField(
													colPositionList.get(0), i);
											Field field2 = sudoku.getField(
													colPositionList.get(1), i);
											Field field3 = sudoku.getField(
													colPositionList.get(2), i);
											Field field4 = sudoku.getField(
													colPositionList.get(3), i);
											if (field1.posibilities
													.contains(number)) {
												sudoku.removePosibility(
														colPositionList.get(0),
														i, number);
												posCount++;
											}
											if (field2.posibilities
													.contains(number)) {
												sudoku.removePosibility(
														colPositionList.get(1),
														i, number);
												posCount++;
											}
											if (field3.posibilities
													.contains(number)) {
												sudoku.removePosibility(
														colPositionList.get(2),
														i, number);
												posCount++;
											}
											if (field4.posibilities
													.contains(number)) {
												sudoku.removePosibility(
														colPositionList.get(3),
														i, number);
												posCount++;
											}
										}
									}

									if (posCount > 0) {
										Logger.log(LogLevel.SolvingMethods,
												"swordfish columns " + f1
														+ ", " + f2 + ", " + f3
														+ ", " + f4
														+ " number " + number
														+ " removed "
														+ posCount
														+ " possibilities");
										fv.addMethod(Method.jellyFish, number,
												posCount);
										return true;
									}
								}
							}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean remotePairs() {
		// iterate over
		return false;
	}
	// HILFSFUNKTIONEN
	private ArrayList<Tuple> getChainOfTuples() {
		ArrayList<Tuple> chain = new ArrayList<Tuple>();
		
		return chain;
	}
	
	private void updateConstraints() throws Exception {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				int number = sudoku.getField(row, col).number;
				if (number != 0) {
					for (int pos = 0; pos < 9; pos++) {
						if (sudoku.getField(row, pos).posibilities
								.contains(number))
							sudoku.removePosibility(row, pos, number);
						if (sudoku.getField(pos, col).posibilities
								.contains(number))
							sudoku.removePosibility(pos, col, number);
						if (sudoku.get(Figure.Block,
								sudoku.getContainingBlockNumber(row, col))[pos].posibilities
								.contains(number))
							sudoku.removePossibilityInBlock(
									sudoku.getContainingBlockNumber(row, col),
									pos, number);
					}
				}
			}
		}
	}

	private HashMap<Integer, ArrayList<Integer>> getPositionList(Figure figure,
			int id) {

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

	private ArrayList<Integer> disjunction(ArrayList<Integer> l1,
			ArrayList<Integer> l2) {

		ArrayList<Integer> newList = new ArrayList<Integer>();

		for (int i = 0; i < l1.size(); i++) {
			if (!newList.contains(l1.get(i)))
				newList.add(l1.get(i));
		}
		for (int i = 0; i < l2.size(); i++) {
			if (!newList.contains(l2.get(i)))
				newList.add(l2.get(i));
		}

		return newList;
	}

	private ArrayList<Integer> conjunction(ArrayList<Integer> l1,
			ArrayList<Integer> l2) {
		ArrayList<Integer> conj = new ArrayList<Integer>();

		for (int i = 0; i < l1.size(); i++) {
			if (l2.contains(l1.get(i)))
				conj.add(l1.get(i));
		}

		return conj;
	}

	private ArrayList<Integer> difference(ArrayList<Integer> l1,
			ArrayList<Integer> l2) {
		ArrayList<Integer> difference = new ArrayList<Integer>();

		difference.addAll(l1);
		difference.removeAll(l2);

		return difference;
	}

	private ArrayList<Vector<Integer>> getBuddies(int row, int column)
			throws Exception {
		ArrayList<Vector<Integer>> buddyList = new ArrayList<Vector<Integer>>();

		// search the row, the column and the block
		int blockNumber = sudoku.getContainingBlockNumber(row, column);
		Field[] block = sudoku.get(Figure.Block, blockNumber);
		for (int i = 0; i < 9; i++) {
			if (i != column) {
				Vector<Integer> v = new Vector<Integer>();
				v.add(row);
				v.add(i);
				buddyList.add(v);
			}

			if (i != row) {
				Vector<Integer> v = new Vector<Integer>();
				v.add(i);
				v.add(column);
				buddyList.add(v);
			}

			// calculate x and y position of the block
			int xBlockOffset = blockNumber % 3;
			int yBlockOffset = 0;

			if (blockNumber > 2)
				yBlockOffset = 1;
			if (blockNumber > 5)
				yBlockOffset = 2;

			// calculate x and y position in the block
			int xInBlockOffset = i % 3;
			int yInBlockOffset = 0;

			if (i > 2)
				yInBlockOffset = 1;
			if (i > 5)
				yInBlockOffset = 2;

			int xOffset = 3 * xBlockOffset + xInBlockOffset;
			int yOffset = 3 * yBlockOffset + yInBlockOffset;

			if (yOffset != row || xOffset != column) {
				Vector<Integer> v = new Vector<Integer>();
				v.add(yOffset);
				v.add(xOffset);
				buddyList.add(v);

			}

		}

		return buddyList;
	}

	private boolean areEqual(ArrayList<Integer> l1, ArrayList<Integer> l2) {
		if (l1.containsAll(l2) && l2.containsAll(l1))
			return true;
		return false;
	}

	public boolean solutionCheck() throws Exception {
		if (sudoku.numberCount(0) != 0) {
			Logger.log(LogLevel.Debug,
					"solution check failed because there were empty fields in the sudoku");
			return false;
		}

		for (int i = 0; i < 9; i++) {
			HashMap<Integer, Integer> rowNumberCount = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> colNumberCount = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> blockNumberCount = new HashMap<Integer, Integer>();
			Field[] row = sudoku.get(Figure.Row, i);
			Field[] col = sudoku.get(Figure.Column, i);
			Field[] block = sudoku.get(Figure.Block, i);

			for (int j = 0; j < 9; j++) {
				if (rowNumberCount.containsKey(row[j].number)) {
					Logger.log(LogLevel.Error,
							"solution check failed because number " + row[j]
									+ " appeared twice in row " + i);
					return false;
				} else {
					rowNumberCount.put(row[j].number, 1);
				}

				if (colNumberCount.containsKey(col[j].number)) {
					Logger.log(LogLevel.Error,
							"solution check failed because number " + col[j]
									+ " appeared twice in column " + i);
					return false;
				} else {
					colNumberCount.put(col[j].number, 1);
				}

				if (blockNumberCount.containsKey(block[j].number)) {
					Logger.log(LogLevel.Error,
							"solution check failed because number " + block[j]
									+ " appeared twice in block " + i);
					return false;
				} else {
					blockNumberCount.put(block[j].number, 1);
				}

			}
		}

		return true;
	}

	private class Tuple {
		public int row;
		public int column;
		
		public Tuple(int row, int column) {
			this.row = row;
			this.column = column;
		}
	}
}
