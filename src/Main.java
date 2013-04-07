import model.*;


public class Main {
	public static void main(String args[]) {
		String s = "211111112211111111111111111111111111111111111111111111111111111111111111111111111";
		Sudoku sudoku = new Sudoku(s);
		System.out.println(sudoku.toString());
		System.out.println(sudoku.IsInFigure(2, Figure.Column, new Position(1, 8)));
	}
}
