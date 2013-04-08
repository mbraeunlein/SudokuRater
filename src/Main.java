import io.SudokuReader;

import java.util.*;
import java.io.*;
import model.*;
import solver.*;

public class Main {
	public static void main(String args[]) throws Exception {
		SudokuReader sr = new SudokuReader(new File("sudokus.txt"));
		ArrayList<Sudoku> sudokus = sr.read();
		
		for (int i = 0; i < sudokus.size(); i++) {
			System.out.println(sudokus.get(i).toString());
		}
		BasicSolver basicSolver = new BasicSolver();
		
		ArrayList<Integer> pos = sudokus.get(3).getField(0, 1).posibilities;
		for(int i = 0; i < pos.size(); i++) {
			System.out.println(pos.get(i));
		}
		
		
		Sudoku scanned = sudokus.get(3);
		int oldCount = scanned.numberCount();
		
		while (oldCount <  basicSolver.scan(scanned).numberCount()) {
			oldCount = basicSolver.scan(scanned).numberCount();
			System.out.println(scanned.toString());
		}
	}
}
