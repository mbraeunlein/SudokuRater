import java.util.*;
import model.*;

public class Main {
	public static void main(String args[]) throws Exception {
		String s = "211111112211111111111111111111111111111111111111111111111111111111111111111111111";
		String s2 = "023456789123456789123456789123456789123456789123456789123456789123456789123456789";
		//Sudoku sudoku = new Sudoku(s);
		Sudoku sudoku2 = new Sudoku(s2);
		//System.out.println(sudoku.toString());
		
		sudoku2.removePosibility(0, 0, 1);
		ArrayList<Integer> pos = sudoku2.getField(0, 0).posibilities;
		for (int i = 0; i < pos.size(); i++) {
			System.out.println(pos.get(i));
		}

		/*
		 * System.out.println(sudoku2.toString()); Field[] row =
		 * sudoku2.getBlock(2); for(int i = 0; i < 9; i++){
		 * System.out.println(row[i].toString()); }
		 */
	}
}
