package io;

import java.io.*;
import model.Sudoku;

public class SudokuWriter {
	BufferedWriter bw;
	
	public SudokuWriter(File file) {
		try {
			bw = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(Sudoku sudoku) {
		
	}
}
