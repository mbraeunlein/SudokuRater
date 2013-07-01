package io;

import java.io.*;
import java.util.*;

import model.*;

public class SudokuReader {
	private BufferedReader br = null;

	public SudokuReader(String file) {
		try {
			br = new BufferedReader(new FileReader(new File("sudokus\\" + file)));
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

	public ArrayList<Sudoku> readFromSoEinDing() {
		ArrayList<Sudoku> sudokus = new ArrayList<Sudoku>();

		try {
			// read the file
			String file = "";
			if (br != null) {
				String line = br.readLine();
				while (line != null) {
					if (line.startsWith("|")) {
						file += line;
					}
					line = br.readLine();
				}
			}

			file = file.replace(" ", "0");
			file = file.replace("|", "");

			String sudokuString = "";
			
			for (int i = 0; i < file.length(); i = i + 81) {
				for (int j = 0; j < 81; j++) {
					sudokuString += file.charAt(i + j);
				}
				sudokus.add(new Sudoku(sudokuString));
				sudokuString = "";
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
