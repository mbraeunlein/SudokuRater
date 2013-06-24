package solver;

import java.util.*;

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
			
			for(int i = 1; i < 10; i++) {
				fv.addNumberCount(i, sudoku.numberCount(i));
			}
			
			for(int i = 1; i < 10; i++) {
				fv.addPossibilityCount(i, sudoku.possibilityCount(i));
				System.out.println(sudoku.possibilityCount(i));
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
		if (sudoku.numberCount(0) == 81) {
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
					System.out.println("no naked single found");
					count++;
					break;
				}
			case 1:
				if (hiddenSingles()) {
					count = 0;
					changed = true;
					break;
				} else {
					System.out.println("no hidden single found");
					count++;
					break;
				}
			case 2:
				if (nakedTupel()) {
					count = 0;
					changed = true;
					break;
				} else {
					System.out.println("no naked tupel found");
					count++;
					break;
				}

			case 3:
				if (hiddenTupel()) {
					count = 0;
					changed = true;
					break;
				} else {
					System.out.println("no hidden tupel found");
					count++;
					break;
				}

			case 4:
				if (nakedTripel()) {
					count = 0;
					changed = true;
					break;
				} else {
					System.out.println("no naked tripel found");
					count++;
					break;
				}
			case 5:
				if (hiddenTripel()) {
					count = 0;
					changed = true;
					break;
				} else {
					System.out.println("no naked tripel found");
					count++;
					break;
				}
			case 6:
				if (crossing()) {
					count = 0;
					changed = true;
					break;
				} else {
					System.out.println("no crossing found");
					count++;
					break;
				}
			case 7:
				changed = false;
				System.out.println("not solvable");
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
			int id) throws Exception {

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

	// computes the disjunction (or) of two lists
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

	private boolean nakedSingles() throws Exception {

		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				Field field = sudoku.getField(row, column);
				if (field.number == 0)
					if (field.posibilities.size() == 1) {
						int number = field.posibilities.get(0);
						sudoku.setField(row, column, new Field(number));
						System.out.println("naked single row " + row
								+ " column " + column + " number " + number);
						fv.addMethod(Method.NakedSingles, number);
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
						System.out.println("hidden single  row " + f
								+ " column " + positions.get(0) + " number "
								+ number);
						sudoku.setField(f, positions.get(0), new Field(number));
						fv.addMethod(Method.HiddenSingles, number);
						return true;
					}
				}
				if (colPositionList.containsKey(number)) {
					ArrayList<Integer> positions = colPositionList.get(number);
					if (positions.size() == 1) {
						System.out.println("hidden single row "
								+ positions.get(0) + " column " + f
								+ " number " + number);
						sudoku.setField(positions.get(0), f, new Field(number));
						fv.addMethod(Method.HiddenSingles, number);
						return true;
					}
				}
				if (blockPositionList.containsKey(number)) {
					ArrayList<Integer> positions = blockPositionList
							.get(number);
					if (positions.size() == 1) {
						System.out.println("hidden single block " + f
								+ " position " + positions.get(0) + " number "
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

	private boolean nakedTupel() throws Exception {
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
											fv.addMethod(Method.NakedTupel, number);
											posCount++;
										}
									}
								}
							}

							if (posCount > 0) {
								System.out.println("naked tupel row " + f
										+ " positions " + i + ", " + j
										+ " removed " + posCount
										+ " possibilities");
								return true;
							}
						}
					}

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
											fv.addMethod(Method.NakedTupel, number);
											posCount++;
										}
									}
								}
							}

							if (posCount > 0) {
								System.out.println("naked tupel col " + f
										+ " positions " + i + ", " + j
										+ " removed " + posCount
										+ " possibilities");
								return true;
							}
						}
					}

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
											fv.addMethod(Method.NakedTupel, number);
											posCount++;
										}
									}
								}
							}

							if (posCount > 0) {
								System.out.println("naked tupel block " + f
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

	private boolean hiddenTupel() throws Exception {

		// iterate over every figure
		for (int f = 0; f < 9; f++) {
			// iterate over every figure
			HashMap<Integer, ArrayList<Integer>> rowPos = getPositionList(
					Figure.Row, f);
			HashMap<Integer, ArrayList<Integer>> colPos = getPositionList(
					Figure.Column, f);
			HashMap<Integer, ArrayList<Integer>> blockPos = getPositionList(
					Figure.Block, f);

			// compare 2 numbers
			for (int i = 1; i < 10; i++) {
				for (int j = i + 1; j < 10; j++) {
					ArrayList<Integer> rowNumber1 = rowPos.get(i);
					ArrayList<Integer> rowNumber2 = rowPos.get(j);

					ArrayList<Integer> colNumber1 = colPos.get(i);
					ArrayList<Integer> colNumber2 = colPos.get(j);

					ArrayList<Integer> blockNumber1 = blockPos.get(i);
					ArrayList<Integer> blockNumber2 = blockPos.get(j);

					if (rowNumber1 != null && rowNumber2 != null) {
						ArrayList<Integer> disj = disjunction(rowNumber1,
								rowNumber2);

						if (disj.size() == 2) {
							int posCount = 0;

							for (int number = 1; number < 10; number++) {
								if (number != i && number != j) {
									if (sudoku.getField(f, disj.get(0)).posibilities
											.contains(number)) {
										sudoku.removePosibility(f, disj.get(0),
												number);
										fv.addMethod(Method.HiddenTupel, number);
										posCount++;
									}
									if (sudoku.getField(f, disj.get(1)).posibilities
											.contains(number)) {
										sudoku.removePosibility(f, disj.get(1),
												number);
										fv.addMethod(Method.HiddenTupel, number);
										posCount++;
									}
								}
							}

							if (posCount > 0) {
								System.out.println("hidden tupel row " + f
										+ " numbers " + i + ", " + j
										+ " removed " + posCount
										+ " possibilities");
								return true;
							}
						}
					}

					if (colNumber1 != null && colNumber2 != null) {
						ArrayList<Integer> disj = disjunction(colNumber1,
								colNumber2);

						if (disj.size() == 2) {
							int posCount = 0;

							for (int number = 1; number < 10; number++) {
								if (number != i && number != j) {
									if (sudoku.getField(disj.get(0), f).posibilities
											.contains(number)) {
										sudoku.removePosibility(disj.get(0), f,
												number);
										fv.addMethod(Method.HiddenTupel, number);
										posCount++;
									}
									if (sudoku.getField(disj.get(0), f).posibilities
											.contains(number)) {
										sudoku.removePosibility(disj.get(0), f,
												number);
										fv.addMethod(Method.HiddenTupel, number);
										posCount++;
									}
								}
							}

							if (posCount > 0) {
								System.out.println("hidden tupel column " + f
										+ " numbers " + i + ", " + j
										+ " removed " + posCount
										+ " possibilities");
								return true;
							}
						}
					}

					if (blockNumber1 != null && blockNumber2 != null) {
						ArrayList<Integer> disj = disjunction(blockNumber1,
								blockNumber2);

						if (disj.size() == 2) {
							int posCount = 0;

							for (int number = 1; number < 10; number++) {
								if (number != i && number != j) {

									if (sudoku.get(Figure.Block, f)[disj.get(0)].posibilities
											.contains(number)) {
										sudoku.removePossibilityInBlock(f,
												disj.get(0), number);
										fv.addMethod(Method.HiddenTupel, number);
										posCount++;
									}
									if (sudoku.get(Figure.Block, f)[disj.get(1)].posibilities
											.contains(number)) {
										sudoku.removePossibilityInBlock(f,
												disj.get(1), number);
										fv.addMethod(Method.HiddenTupel, number);
										posCount++;
									}
								}
							}

							if (posCount > 0) {
								System.out.println("hidden tupel block " + f
										+ " numbers " + i + ", " + j
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

	private boolean nakedTripel() throws Exception {

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
												fv.addMethod(Method.NakedTripel, number);
												posCount++;
											}
										}
									}
								}

								if (posCount > 0) {
									System.out.println("naked tripel row " + f
											+ " positions " + i + ", " + j
											+ ", " + k + " removed " + posCount
											+ " possibilities");
									return true;
								}
							}
						}

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
												fv.addMethod(Method.NakedTripel, number);
												posCount++;
											}
										}
									}
								}

								if (posCount > 0) {
									System.out.println("naked tripel column "
											+ f + " positions " + i + ", " + j
											+ ", " + k + " removed " + posCount
											+ " possibilities");
									return true;
								}
							}
						}

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
												fv.addMethod(Method.NakedTripel, number);
												posCount++;
											}
										}
									}
								}

								if (posCount > 0) {
									System.out.println("naked tripel block "
											+ f + " positions " + i + ", " + j
											+ ", " + k + " removed " + posCount
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

	private boolean hiddenTripel() throws Exception {

		// iterate over every figure
		for (int f = 0; f < 9; f++) {
			HashMap<Integer, ArrayList<Integer>> rowPos = getPositionList(
					Figure.Row, f);
			HashMap<Integer, ArrayList<Integer>> colPos = getPositionList(
					Figure.Column, f);
			HashMap<Integer, ArrayList<Integer>> blockPos = getPositionList(
					Figure.Block, f);

			// compare 3 numbers
			for (int i = 1; i < 10; i++) {
				for (int j = i + 1; j < 10; j++) {
					for (int k = j + 1; k < 10; k++) {
						ArrayList<Integer> rowNumber1 = rowPos.get(i);
						ArrayList<Integer> rowNumber2 = rowPos.get(j);
						ArrayList<Integer> rowNumber3 = rowPos.get(k);

						ArrayList<Integer> colNumber1 = colPos.get(i);
						ArrayList<Integer> colNumber2 = colPos.get(j);
						ArrayList<Integer> colNumber3 = colPos.get(k);

						ArrayList<Integer> blockNumber1 = blockPos.get(i);
						ArrayList<Integer> blockNumber2 = blockPos.get(j);
						ArrayList<Integer> blockNumber3 = blockPos.get(k);

						if (rowNumber1 != null && rowNumber2 != null
								&& rowNumber3 != null) {
							ArrayList<Integer> disj = disjunction(rowNumber1,
									disjunction(rowNumber2, rowNumber3));

							if (disj.size() == 3) {
								int posCount = 0;

								for (int number = 1; number < 10; number++) {
									if (number != i && number != j
											&& number != k) {
										for (int pos = 0; pos < 3; pos++) {
											if (sudoku.getField(f,
													disj.get(pos)).posibilities
													.contains(number)) {
												sudoku.removePosibility(f,
														disj.get(pos), number);
												fv.addMethod(Method.HiddenTripel, number);
												posCount++;
											}
										}
									}
								}
								if (posCount > 0) {
									System.out.println("hidden tripel row "
											+ f + " numbers " + i + ", " + j
											+ ", " + k + " removed " + posCount
											+ " possibilities");
									return true;
								}
							}
						}
						
						if (colNumber1 != null && colNumber2 != null
								&& colNumber3 != null) {
							ArrayList<Integer> disj = disjunction(colNumber1,
									disjunction(colNumber2, colNumber3));

							if (disj.size() == 3) {
								int posCount = 0;

								for (int number = 1; number < 10; number++) {
									if (number != i && number != j
											&& number != k) {
										for (int pos = 0; pos < 3; pos++) {
											if (sudoku.getField(disj.get(pos),
													f).posibilities
													.contains(number)) {
												sudoku.removePosibility(disj.get(pos),
														f, number);
												fv.addMethod(Method.HiddenTripel, number);
												posCount++;
											}
										}
									}
								}
								if (posCount > 0) {
									System.out.println("hidden tripel column "
											+ f + " numbers " + i + ", " + j
											+ ", " + k + " removed " + posCount
											+ " possibilities");
									return true;
								}
							}
						}
						
						if (blockNumber1 != null && blockNumber2 != null
								&& blockNumber3 != null) {
							ArrayList<Integer> disj = disjunction(blockNumber1,
									disjunction(blockNumber2, blockNumber3));

							if (disj.size() == 3) {
								int posCount = 0;

								for (int number = 1; number < 10; number++) {
									if (number != i && number != j
											&& number != k) {
										for (int pos = 0; pos < 3; pos++) {
											if (sudoku.get(Figure.Block, f)[disj.get(pos)].posibilities
													.contains(number)) {
												sudoku.removePossibilityInBlock(f,
														disj.get(pos), number);
												fv.addMethod(Method.HiddenTripel, number);
												posCount++;
											}
										}
									}
								}
								if (posCount > 0) {
									System.out.println("hidden tripel block "
											+ f + " numbers " + i + ", " + j
											+ ", " + k + " removed " + posCount
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

	private boolean crossing() throws Exception {

		// iterate over every row
		for (int r = 0; r < 9; r++) {
			Field[] row = sudoku.get(Figure.Row, r);
			// list of possibilities for every rowblock
			ArrayList<ArrayList<Integer>> blockLists = new ArrayList<ArrayList<Integer>>();

			// iterate over every block in the row
			for (int rowBlock = 0; rowBlock < 3; rowBlock++) {
				// get the possibilities of every position in the rowblock
				ArrayList<Integer> p1 = row[rowBlock * 3].posibilities;
				ArrayList<Integer> p2 = row[rowBlock * 3 + 1].posibilities;
				ArrayList<Integer> p3 = row[rowBlock * 3 + 2].posibilities;
				// ArrayList<Integer> blockList = conjunction(p1,
				// conjunction(p2, p3));
				ArrayList<Integer> blockList = disjunction(p1,
						disjunction(p2, p3));
				// add a list of this blocks possibilities
				blockLists.add(blockList);
			}

			// iterate over every number
			for (int number = 1; number < 10; number++) {
				ArrayList<Integer> dis01 = disjunction(blockLists.get(0),
						blockLists.get(1));
				ArrayList<Integer> dis02 = disjunction(blockLists.get(0),
						blockLists.get(2));
				ArrayList<Integer> dis12 = disjunction(blockLists.get(1),
						blockLists.get(2));

				int blockNumber = 6;
				if (r < 6)
					blockNumber = 3;
				if (r < 3)
					blockNumber = 0;

				if (blockLists.get(0).contains(number)
						&& !dis12.contains(number)) {
					int posCount = 0;
					Field[] block = sudoku.get(Figure.Block, blockNumber);

					for (int pos = 0; pos < 9; pos++) {
						int rowNumber = r % 3;
						if (pos != 3 * rowNumber && pos != 3 * rowNumber + 1
								&& pos != 3 * rowNumber + 2) {
							if (block[pos].posibilities.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber,
										pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing row " + r + " block "
								+ blockNumber + " number " + number
								+ " removed " + posCount + " possibilities");
						return true;
					}
				}
				if (blockLists.get(1).contains(number)
						&& !dis02.contains(number)) {
					blockNumber++;
					int posCount = 0;
					Field[] block = sudoku.get(Figure.Block, blockNumber);

					for (int pos = 0; pos < 9; pos++) {
						int rowNumber = r % 3;
						if (pos != 3 * rowNumber && pos != 3 * rowNumber + 1
								&& pos != 3 * rowNumber + 2) {
							if (block[pos].posibilities.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber,
										pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing row " + r + " block "
								+ blockNumber + " number " + number
								+ " removed " + posCount + " possibilities");
						return true;
					}
				}
				if (blockLists.get(2).contains(number)
						&& !dis01.contains(number)) {
					blockNumber += 2;
					int posCount = 0;
					Field[] block = sudoku.get(Figure.Block, blockNumber);

					for (int pos = 0; pos < 9; pos++) {
						int rowNumber = r % 3;
						if (pos != 3 * rowNumber && pos != 3 * rowNumber + 1
								&& pos != 3 * rowNumber + 2) {
							if (block[pos].posibilities.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber,
										pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing row " + r + " block "
								+ blockNumber + " number " + number
								+ " removed " + posCount + " possibilities");
						return true;
					}
				}
			}
		}

		// iterate over every column
		for (int c = 0; c < 9; c++) {
			Field[] col = sudoku.get(Figure.Column, c);
			// list of possibilities for every colblock
			ArrayList<ArrayList<Integer>> blockLists = new ArrayList<ArrayList<Integer>>();

			// iterate over every block in the column
			for (int colBlock = 0; colBlock < 3; colBlock++) {
				// get the possibilities of every position in the rowblock
				ArrayList<Integer> p1 = col[colBlock * 3].posibilities;
				ArrayList<Integer> p2 = col[colBlock * 3 + 1].posibilities;
				ArrayList<Integer> p3 = col[colBlock * 3 + 2].posibilities;
				// ArrayList<Integer> blockList = conjunction(p1,
				// conjunction(p2, p3));
				ArrayList<Integer> blockList = disjunction(p1,
						disjunction(p2, p3));
				// add a list of this blocks possibilities
				blockLists.add(blockList);
			}

			// iterate over every number
			for (int number = 1; number < 10; number++) {
				ArrayList<Integer> dis01 = disjunction(blockLists.get(0),
						blockLists.get(1));
				ArrayList<Integer> dis02 = disjunction(blockLists.get(0),
						blockLists.get(2));
				ArrayList<Integer> dis12 = disjunction(blockLists.get(1),
						blockLists.get(2));

				int blockNumber = 2;
				if (c < 6)
					blockNumber = 1;
				if (c < 3)
					blockNumber = 0;

				if (blockLists.get(0).contains(number)
						&& !dis12.contains(number)) {
					int posCount = 0;
					Field[] block = sudoku.get(Figure.Block, blockNumber);

					for (int pos = 0; pos < 9; pos++) {
						int colNumber = c % 3;
						if (pos != colNumber && pos != colNumber + 3
								&& pos != colNumber + 6) {
							if (block[pos].posibilities.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber,
										pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing column " + c + " block "
								+ blockNumber + " number " + number
								+ " removed " + posCount + " possibilities");
						return true;
					}
				}
				if (blockLists.get(1).contains(number)
						&& !dis02.contains(number)) {
					blockNumber += 3;
					int posCount = 0;
					Field[] block = sudoku.get(Figure.Block, blockNumber);

					for (int pos = 0; pos < 9; pos++) {
						int colNumber = c % 3;
						if (pos != colNumber && pos != colNumber + 3
								&& pos != colNumber + 6) {
							if (block[pos].posibilities.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber,
										pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing column " + c + " block "
								+ blockNumber + " number " + number
								+ " removed " + posCount + " possibilities");
						return true;
					}
				}
				if (blockLists.get(2).contains(number)
						&& !dis01.contains(number)) {
					blockNumber += 6;
					int posCount = 0;
					Field[] block = sudoku.get(Figure.Block, blockNumber);

					for (int pos = 0; pos < 9; pos++) {
						int colNumber = c % 3;
						if (pos != colNumber && pos != colNumber + 3
								&& pos != colNumber + 6) {
							if (block[pos].posibilities.contains(number)) {
								sudoku.removePossibilityInBlock(blockNumber,
										pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing column " + c + " block "
								+ blockNumber + " number " + number
								+ " removed " + posCount + " possibilities");
						return true;
					}
				}
			}

		}

		// iteratve over every block
		for (int b = 0; b < 9; b++) {
			Field[] block = sudoku.get(Figure.Block, b);
			// list of possibilities for every colblock
			ArrayList<ArrayList<Integer>> blockLists = new ArrayList<ArrayList<Integer>>();

			// check for row crossings
			// iterate over every row in the block

			for (int blockRow = 0; blockRow < 3; blockRow++) {
				// get the possibilities of every position in the blockrow
				ArrayList<Integer> p1 = block[blockRow * 3].posibilities;
				ArrayList<Integer> p2 = block[blockRow * 3 + 1].posibilities;
				ArrayList<Integer> p3 = block[blockRow * 3 + 2].posibilities;

				ArrayList<Integer> blockList = disjunction(p1,
						disjunction(p2, p3));
				// add a list of this blocks possibilities
				blockLists.add(blockList);
			}

			// iterate over every number
			for (int number = 1; number < 10; number++) {
				ArrayList<Integer> dis01 = disjunction(blockLists.get(0),
						blockLists.get(1));
				ArrayList<Integer> dis02 = disjunction(blockLists.get(0),
						blockLists.get(2));
				ArrayList<Integer> dis12 = disjunction(blockLists.get(1),
						blockLists.get(2));

				int rowNumber = 6;
				if (b < 6)
					rowNumber = 3;
				if (b < 3)
					rowNumber = 0;

				if (blockLists.get(0).contains(number)
						&& !dis12.contains(number)) {
					int posCount = 0;
					Field[] row = sudoku.get(Figure.Row, rowNumber);

					for (int pos = 0; pos < 9; pos++) {
						int blockNumber = b % 3;
						if (pos != 3 * blockNumber
								&& pos != 3 * blockNumber + 1
								&& pos != 3 * blockNumber + 2) {
							if (row[pos].posibilities.contains(number)) {
								sudoku.removePosibility(rowNumber, pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing block " + b + " row "
								+ rowNumber + " number " + number + " removed "
								+ posCount + " possibilities");
						return true;
					}
				}
				if (blockLists.get(1).contains(number)
						&& !dis02.contains(number)) {
					rowNumber++;
					int posCount = 0;
					Field[] row = sudoku.get(Figure.Row, rowNumber);

					for (int pos = 0; pos < 9; pos++) {
						int blockNumber = b % 3;
						if (pos != 3 * blockNumber
								&& pos != 3 * blockNumber + 1
								&& pos != 3 * blockNumber + 2) {
							if (row[pos].posibilities.contains(number)) {
								sudoku.removePosibility(rowNumber, pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing block " + b + " row "
								+ rowNumber + " number " + number + " removed "
								+ posCount + " possibilities");
						return true;
					}
				}
				if (blockLists.get(2).contains(number)
						&& !dis01.contains(number)) {
					rowNumber += 2;
					int posCount = 0;
					Field[] row = sudoku.get(Figure.Row, rowNumber);

					for (int pos = 0; pos < 9; pos++) {
						int blockNumber = b % 3;
						if (pos != 3 * blockNumber
								&& pos != 3 * blockNumber + 1
								&& pos != 3 * blockNumber + 2) {
							if (row[pos].posibilities.contains(number)) {
								sudoku.removePosibility(rowNumber, pos, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing block " + b + " row "
								+ rowNumber + " number " + number + " removed "
								+ posCount + " possibilities");
						return true;
					}
				}
			}

			// check for col crossings
			// iterate over every column in the block
			blockLists = new ArrayList<ArrayList<Integer>>();
			for (int blockRow = 0; blockRow < 3; blockRow++) {
				// get the possibilities of every position in the blockrow
				ArrayList<Integer> p1 = block[blockRow].posibilities;
				ArrayList<Integer> p2 = block[blockRow + 3].posibilities;
				ArrayList<Integer> p3 = block[blockRow + 6].posibilities;

				ArrayList<Integer> blockList = disjunction(p1,
						disjunction(p2, p3));
				// add a list of this blocks possibilities
				blockLists.add(blockList);
			}

			// iterate over every number
			for (int number = 1; number < 10; number++) {
				ArrayList<Integer> dis01 = disjunction(blockLists.get(0),
						blockLists.get(1));
				ArrayList<Integer> dis02 = disjunction(blockLists.get(0),
						blockLists.get(2));
				ArrayList<Integer> dis12 = disjunction(blockLists.get(1),
						blockLists.get(2));

				int rowNumber = 6;
				if (b < 6)
					rowNumber = 3;
				if (b < 3)
					rowNumber = 0;

				int colNumber = 3 * (b % 3);

				if (blockLists.get(0).contains(number)
						&& !dis12.contains(number)) {
					int posCount = 0;
					Field[] col = sudoku.get(Figure.Column, colNumber);

					for (int pos = 0; pos < 9; pos++) {

						if (pos != rowNumber && pos != rowNumber + 1
								&& pos != rowNumber + 2) {
							if (col[pos].posibilities.contains(number)) {
								sudoku.removePosibility(pos, colNumber, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing block " + b + " column "
								+ colNumber + " number " + number + " removed "
								+ posCount + " possibilities");
						return true;
					}
				}
				if (blockLists.get(1).contains(number)
						&& !dis02.contains(number)) {
					colNumber += 1;
					int posCount = 0;
					Field[] col = sudoku.get(Figure.Column, colNumber);

					for (int pos = 0; pos < 9; pos++) {
						if (pos != rowNumber && pos != rowNumber + 1
								&& pos != rowNumber + 2) {
							if (col[pos].posibilities.contains(number)) {
								sudoku.removePosibility(pos, colNumber, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing block " + b + " column "
								+ colNumber + " number " + number + " removed "
								+ posCount + " possibilities");
						return true;
					}
				}
				if (blockLists.get(2).contains(number)
						&& !dis01.contains(number)) {
					colNumber += 2;
					int posCount = 0;
					Field[] col = sudoku.get(Figure.Column, colNumber);

					for (int pos = 0; pos < 9; pos++) {
						if (pos != rowNumber && pos != rowNumber + 1
								&& pos != rowNumber + 2) {
							if (col[pos].posibilities.contains(number)) {
								sudoku.removePosibility(pos, colNumber, number);
								fv.addMethod(Method.Crossing, number);
								posCount++;
							}
						}
					}
					if (posCount > 0) {
						System.out.println("crossing block " + b + " column "
								+ colNumber + " number " + number + " removed "
								+ posCount + " possibilities");
						return true;
					}
				}
			}
		}
		return false;
	}

}
