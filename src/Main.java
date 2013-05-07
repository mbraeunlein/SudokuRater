import io.SudokuReader;

import java.util.*;
import java.io.*;

import model.*;
import solver.*;
import ui.*;

public class Main {
	public static void main(String args[]) throws Exception {
		MainWindow mw = new MainWindow();
		mw.draw();
		
		SudokuReader sr = new SudokuReader(new File("sudokus.txt"));

		ArrayList<Sudoku> sudokus = sr.read();
		Sudoku sudoku = sudokus.get(2);
		Solver solver = new Solver(sudoku);
		System.out.println(sudoku);
		mw.setSolver(solver);
	}
}
