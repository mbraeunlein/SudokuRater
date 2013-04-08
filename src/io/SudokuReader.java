package io;

import java.io.*;
import java.util.*;
import model.*;

public class SudokuReader {
	BufferedReader br = null;

	public SudokuReader(File file) {
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("the given file was not found");
		}
	}

	public ArrayList<Sudoku> read() {
		ArrayList<Sudoku> sudokus = new ArrayList<Sudoku>();

		try {
			if (br != null) {
				String line = br.readLine();
				while (line != null) {
					sudokus.add(new Sudoku(line));
					line = br.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return sudokus;
	}
}
