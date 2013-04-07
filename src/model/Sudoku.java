package model;

public class Sudoku {
	// Erst Reihe dann Spalte
	private int[][] field = new int[9][9];

	public Sudoku() {
	}

	// Ein String aus 81 Ziffern zwischen 0 und 9 repräsentiert ein Sudoku Feld
	// 0 steht hierbei für nicht ausgefüllte Felder
	public Sudoku(String sudokuString) {
		try {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					field[i][j] = sudokuString.charAt(9 * i + j) - 48;
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
				if (field[i][j] == 0)
					s += "_";
				else
					s += field[i][j];
				s += " ";
			}
			s += "\n";
		}
		return s;
	}
	
	public boolean IsInFigure(int i, Figure f, Position pos) {
		if (f == Figure.Row) {
			for(int j = 0; j < 9; j++) {
				if(j != pos.column)
					if(i == field[pos.row][j])
						return true;
			}
			
		}
		if(f == Figure.Column) {
			for(int j = 0; j < 9; j++) {
				if(j != pos.row)
					if(i == field[j][pos.column])
						return true;
			}
		}
		if(f == Figure.Box) {
			return true;
		}
		return false;
	}
}
