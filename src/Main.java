import io.Crawler;
import io.SudokuReader;

import java.util.*;
import java.io.*;

import model.*;
import solver.*;
import ui.*;

public class Main {
	public static void main(String args[]) throws Exception {
		// draw the main window
		
		 MainWindow mw = new MainWindow(); mw.draw();
		 

		// crawl sudokus from webpage
		// Crawler crawler = new Crawler();

		// read sudokus from textfile
		SudokuReader sr = new SudokuReader(new File("einfach.txt"));
		ArrayList<Sudoku> sudokus = sr.readFromSoEinDing();
		for (int i = 0; i < sudokus.size(); i++) {
			Sudoku sudoku = sudokus.get(i);
			Solver solver = new Solver(sudoku);
			mw.setSolver(solver);
			
			for(int j = 0; j < 10; j++) {
				System.out.println(j + ": " + sudoku.numberCount(j));
			}
			
			sudoku = solver.solve();
			
			
			FeatureVector fv = solver.getFeatureVector();
			System.out.println("Sudoku " + i
					+ "\n----------------------------\n");
			System.out.println(fv.toString());
		}

	}
}
