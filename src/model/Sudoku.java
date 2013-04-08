package model;

import java.util.*;

public class Sudoku {
	// Erst Reihe dann Spalte
	private Field[][] board = new Field[9][9];

	public Sudoku() {
	}

	// Ein String aus 81 Ziffern zwischen 0 und 9 repräsentiert ein Sudoku Feld
	// 0 steht hierbei für nicht ausgefüllte Felder
	public Sudoku(String sudokuString) {
		try {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					board[i][j] = new Field(sudokuString.charAt(9 * i + j) - 48);
					if (board[i][j].number == 0) {
						board[i][j].posibilities.add(1);
						board[i][j].posibilities.add(2);
						board[i][j].posibilities.add(3);
						board[i][j].posibilities.add(4);
						board[i][j].posibilities.add(5);
						board[i][j].posibilities.add(6);
						board[i][j].posibilities.add(7);
						board[i][j].posibilities.add(8);
						board[i][j].posibilities.add(9);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error while parsing Sudoku." + e.getMessage());
		}
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (board[i][j].number == 0)
					s += "_";
				else
					s += board[i][j];
				s += " ";
			}
			s += "\n";
		}
		return s;
	}

	public Field[] getRow(int number) throws Exception {
		if (number < 0 || number > 8) {
			throw new Exception("the number has to be between 0 and 8");
		}
		Field[] row = new Field[9];
		for (int i = 0; i < 9; i++) {
			row[i] = board[number][i];
		}
		return row;
	}

	public Field[] getColumn(int number) throws Exception {
		if (number < 0 || number > 8) {
			throw new Exception("the number has to be between 0 and 8");
		}
		Field[] column = new Field[9];
		for (int i = 0; i < 9; i++) {
			column[i] = board[i][number];
		}
		return column;
	}

	public Field[] getBlock(int number) throws Exception {
		if (number < 0 || number > 8) {
			throw new Exception("the number has to be between 0 and 8");
		}
		Field[] block = new Field[9];

		int xStart = number % 3;
		int yStart = 0;

		if (number > 2)
			yStart = 1;
		if (number > 5)
			yStart = 2;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				block[3 * i + j] = board[yStart * 3 + i][xStart * 3 + j];
			}
		}

		System.out.println(xStart + " / " + yStart);
		return block;
	}

	public int constraintCount() {
		int count = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				count += board[i][j].posibilities.size();
			}
		}
		return count;
	}

	public void OnePosibilityFillIn() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (board[i][j].number != 0)
					if (board[i][j].posibilities.size() == 1)
						board[i][j].number = board[i][j].posibilities.get(0);
			}
		}
	}

	public void removePosibility(int row, int column, int number)
			throws Exception {
		if (row < 0 || row > 8)
			throw new Exception("row has to be between 0 and 8");
		if (column < 0 || column > 8)
			throw new Exception("column has to be between 0 and 8");
		if (number < 0 || number > 8)
			throw new Exception("number has to be between 0 and 8");

		ArrayList<Integer> newPosibilities = new ArrayList<Integer>();
		ArrayList<Integer> posibilities = board[row][column].posibilities;
		for (int i = 0; i < posibilities.size(); i++) {
			int pos = posibilities.get(i);
			if (pos != number)
				newPosibilities.add(number);
		}

		if (newPosibilities.size() == 0)
			throw new Exception("no possibilities left");

		if (newPosibilities.size() > 1)
			board[row][column].posibilities = newPosibilities;
		if (newPosibilities.size() == 1)
			board[row][column].number = newPosibilities.get(0);
	}
}
