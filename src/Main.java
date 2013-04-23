import io.SudokuReader;

import java.util.*;
import java.io.*;
import model.*;
import solver.*;

public class Main {
	public static void main(String args[]) throws Exception {
		SudokuReader sr = new SudokuReader(new File("sudokus.txt"));

		ArrayList<Sudoku> sudokus = sr.read();
		Sudoku sudoku = sudokus.get(5);
//		Solver solver = new Solver();
//		System.out.println(sudoku);
//		solver.crossing(sudoku);
		solve(sudoku);
	}

	public static void solve(Sudoku sudoku) throws Exception {
		System.out.println(sudoku);
		if(sudoku.numberCount() == 81)
			return;
		Solver solver = new Solver();
		int oldNumberCount = sudoku.numberCount();
		sudoku = solver.excludePossibilities(sudoku);
		int newNumberCount = sudoku.numberCount();

		// start solving with the easiest method as long as something changes
		if (oldNumberCount < newNumberCount)
			solve(sudoku);
		else {
			// if nothing changed, try another method
			sudoku = solver.scan(sudoku);
			newNumberCount = sudoku.numberCount();

			// start solving with the easiest method as long as something
			// changes
			if (oldNumberCount < newNumberCount)
				solve(sudoku);
			else {
				int oldPossibilityCount = sudoku.possibilityCount();
				sudoku = solver.tupel(sudoku);
				int newPossibilityCount = sudoku.possibilityCount();
				if (oldPossibilityCount > newPossibilityCount)
					solve(sudoku);
				else {
					sudoku = solver.tripel(sudoku);
					newPossibilityCount = sudoku.possibilityCount();
					if (oldPossibilityCount > newPossibilityCount)
						solve(sudoku);
					else {
						sudoku = solver.crossing(sudoku);
						newPossibilityCount = sudoku.possibilityCount();
						if (oldPossibilityCount > newPossibilityCount)
							solve(sudoku);
					}
				}
			}
		}
	}
}
