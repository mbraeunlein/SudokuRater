import io.LogLevel;
import io.Logger;
import io.SudokuReader;
import io.arffWriter;

import java.util.*;
import java.io.*;

import classificator.kNearestNeighbor;

import model.*;
import solver.*;
import ui.*;

public class Main {
	public static void main(String args[]) throws Exception {

		// configure the logger
		Logger.setPersist(true);
		Logger.addLogLevel(LogLevel.GeneralInformation);
		Logger.addLogLevel(LogLevel.Error);
		Logger.addLogLevel(LogLevel.Classification);
		// Logger.addLogLevel(LogLevel.SolvingMethods);
		// Logger.addLogLevel(LogLevel.Debug);
		// Logger.addLogLevel(LogLevel.FeatureVector);

		arffWriter aw = new arffWriter("sudokus.arff");

		// lists with the computed feature vectors
		ArrayList<FeatureVector> hodokuEasyVectors = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> hodokuMiddleVectors = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> hodokuHardVectors = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> hodokuUnfairVectors = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> hodokuExtremeVectors = new ArrayList<FeatureVector>();

		ArrayList<FeatureVector> soEinDingSehrEinfach = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> soEinDingEinfach = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> soEinDingStandard = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> soEinDingModerat = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> soEinDingAnspruchsvoll = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> soEinDingSehrAnspruchsvoll = new ArrayList<FeatureVector>();
		ArrayList<FeatureVector> soEinDingTeuflisch = new ArrayList<FeatureVector>();

		HashMap<Classification, ArrayList<FeatureVector>> classificatedVectors = new HashMap<Classification, ArrayList<FeatureVector>>();

		SudokuReader sr = null;
		kNearestNeighbor kn = new kNearestNeighbor();

		/*
		 * sr = new SudokuReader("sudokus.txt"); MainWindow mw = new
		 * MainWindow(); mw.draw(); Sudoku sudoku = sr.read().get(0); Solver
		 * solver = new Solver(sudoku); mw.setSolver(solver); solver.solve();
		 * System.out.println(solver.getFeatureVector());
		 * 
		 * if (true) return;
		 */

		// read sudokus generated by hodoku (classification easy) from textfile
		sr = new SudokuReader("hodoku-easy.txt");
		ArrayList<Sudoku> sudokusHodokuEasy = sr.read();

		Logger.log(LogLevel.GeneralInformation, "classification: hodoku easy");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuEasy.size()
				+ " sudokus loaded.");

		hodokuEasyVectors = getFeatureVectors(sudokusHodokuEasy);

		// read sudokus generated by hodoku (classification middle) from
		// textfile
		sr = new SudokuReader("hodoku-middle.txt");
		ArrayList<Sudoku> sudokusHodokuMiddle = sr.read();

		Logger.log(LogLevel.GeneralInformation, "classification: hodoku middle");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuMiddle.size()
				+ " sudokus loaded.");

		hodokuMiddleVectors = getFeatureVectors(sudokusHodokuMiddle);

		// read sudokus generated by hodoku (classification hard) from
		// textfile
		sr = new SudokuReader("hodoku-hard.txt");
		ArrayList<Sudoku> sudokusHodokuHard = sr.read();

		Logger.log(LogLevel.GeneralInformation, "classification: hodoku hard");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuHard.size()
				+ " sudokus loaded.");

		hodokuHardVectors = getFeatureVectors(sudokusHodokuHard);

		// read sudokus generated by hodoku (classification unfair) from
		// textfile
		sr = new SudokuReader("hodoku-unfair.txt");
		ArrayList<Sudoku> sudokusHodokuUnfair = sr.read();

		Logger.log(LogLevel.GeneralInformation, "classification: hodoku unfair");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuUnfair.size()
				+ " sudokus loaded.");

		hodokuUnfairVectors = getFeatureVectors(sudokusHodokuUnfair);

		// read sudokus generated by hodoku (classification extreme) from
		// textfile
		sr = new SudokuReader("hodoku-extreme.txt");
		ArrayList<Sudoku> sudokusHodokuExtreme = sr.read();

		Logger.log(LogLevel.GeneralInformation,
				"classification: hodoku extreme");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuExtreme.size()
				+ " sudokus loaded.");

		hodokuExtremeVectors = getFeatureVectors(sudokusHodokuExtreme);


		
		// write all Feature Vectors to an arff file
		classificatedVectors.put(Classification.HodokuEasy, hodokuEasyVectors);
		classificatedVectors.put(Classification.HodokuMiddle, hodokuMiddleVectors);
		classificatedVectors.put(Classification.HodokuHard, hodokuHardVectors);
		classificatedVectors.put(Classification.HodokuUnfair, hodokuUnfairVectors);
		classificatedVectors.put(Classification.HodokuExtreme, hodokuExtremeVectors);
		
		aw.writeToFile(classificatedVectors);
		
		Logger.log(LogLevel.GeneralInformation,
				"Estimated all feature vectors of the sudokus, starting with cross checking");

		// 5 iterations cross checking
		for (int i = 0; i < 5; i++) {
			kn = new kNearestNeighbor();

			System.out.println();
			System.out.println();
			Logger.log(LogLevel.Classification, "Cross checking iteration "
					+ (i + 1));
			System.out.println();

			for (int j = 0; j < hodokuEasyVectors.size(); j++) {
				if (j < i * (hodokuEasyVectors.size() / 5)
						|| j >= (i + 1) * (hodokuEasyVectors.size() / 5)) {
					kn.train(Classification.HodokuEasy,
							hodokuEasyVectors.get(j));
				}
			}

			for (int j = 0; j < hodokuMiddleVectors.size(); j++) {
				if (j < i * (hodokuMiddleVectors.size() / 5)
						|| j >= (i + 1) * (hodokuMiddleVectors.size() / 5)) {
					kn.train(Classification.HodokuMiddle,
							hodokuMiddleVectors.get(j));
				}
			}

			for (int j = 0; j < hodokuHardVectors.size(); j++) {
				if (j < i * (hodokuHardVectors.size() / 5)
						|| j >= (i + 1) * (hodokuHardVectors.size() / 5)) {
					kn.train(Classification.HodokuHard,
							hodokuHardVectors.get(j));
				}
			}

			for (int j = 0; j < hodokuUnfairVectors.size(); j++) {
				if (j < i * (hodokuUnfairVectors.size() / 5)
						|| j >= (i + 1) * (hodokuUnfairVectors.size() / 5)) {
					kn.train(Classification.HodokuUnfair,
							hodokuUnfairVectors.get(j));
				}
			}

			for (int j = 0; j < hodokuExtremeVectors.size(); j++) {
				if (j < i * (hodokuExtremeVectors.size() / 5)
						|| j >= (i + 1) * (hodokuExtremeVectors.size() / 5)) {
					kn.train(Classification.HodokuExtreme,
							hodokuExtremeVectors.get(j));
				}
			}

			// all sudokus trained, classify the not trained sudokus
			int totalCount = 0;
			int rightCount = 0;

			for (int j = 0; j < hodokuEasyVectors.size(); j++) {
				if (!(j < i * (hodokuEasyVectors.size() / 5) || j >= (i + 1)
						* (hodokuEasyVectors.size() / 5))) {
					if (kn.classify(hodokuEasyVectors.get(j), 20).equals(
							Classification.HodokuEasy)) {
						rightCount++;
					}
					totalCount++;
				}
			}
			Logger.log(LogLevel.Classification, "HodokuEasy: " + rightCount
					+ " of " + totalCount + " sudokus correctly classified");

			totalCount = 0;
			rightCount = 0;
			for (int j = 0; j < hodokuMiddleVectors.size(); j++) {
				if (!(j < i * (hodokuMiddleVectors.size() / 5) || j >= (i + 1)
						* (hodokuMiddleVectors.size() / 5))) {
					if (kn.classify(hodokuMiddleVectors.get(j), 20).equals(
							Classification.HodokuMiddle)) {
						rightCount++;
					}
					totalCount++;
				}
			}
			Logger.log(LogLevel.Classification, "HodokuMiddle: " + rightCount
					+ " of " + totalCount + " sudokus correctly classified");

			totalCount = 0;
			rightCount = 0;

			/*
			 * int easy = 0; int middle = 0; int unfair = 0; int extreme = 0;
			 */

			for (int j = 0; j < hodokuHardVectors.size(); j++) {
				if (!(j < i * (hodokuHardVectors.size() / 5) || j >= (i + 1)
						* (hodokuHardVectors.size() / 5))) {
					if (kn.classify(hodokuHardVectors.get(j), 20).equals(
							Classification.HodokuHard)) {
						rightCount++;
					}/*
					 * switch (kn.classify(hodokuHardVectors.get(j), 20)) { case
					 * HodokuEasy: easy++; break; case HodokuMiddle: middle++;
					 * break; case HodokuHard: rightCount++; break; case
					 * HodokuUnfair: unfair++; break; case HodokuExtreme:
					 * extreme++; break; }
					 */
					totalCount++;
				}
			}
			Logger.log(LogLevel.Classification, "HodokuHard: " + rightCount
					+ " of " + totalCount + " sudokus correctly classified");
			/*
			 * Logger.log(LogLevel.Classification, easy +
			 * " classified as easy"); Logger.log(LogLevel.Classification,
			 * middle + " classified as middle");
			 * Logger.log(LogLevel.Classification, unfair +
			 * " classified as unfair"); Logger.log(LogLevel.Classification,
			 * extreme + " classified as extreme");
			 */

			totalCount = 0;
			rightCount = 0;
			for (int j = 0; j < hodokuUnfairVectors.size(); j++) {
				if (!(j < i * (hodokuUnfairVectors.size() / 5) || j >= (i + 1)
						* (hodokuUnfairVectors.size() / 5))) {
					if (kn.classify(hodokuUnfairVectors.get(j), 20).equals(
							Classification.HodokuUnfair)) {
						rightCount++;
					}
					totalCount++;
				}
			}
			Logger.log(LogLevel.Classification, "HodokuUnfair: " + rightCount
					+ " of " + totalCount + " sudokus correctly classified");

			totalCount = 0;
			rightCount = 0;
			for (int j = 0; j < hodokuExtremeVectors.size(); j++) {
				if (!(j < i * (hodokuExtremeVectors.size() / 5) || j >= (i + 1)
						* (hodokuExtremeVectors.size() / 5))) {
					if (kn.classify(hodokuExtremeVectors.get(j), 20).equals(
							Classification.HodokuExtreme)) {
						rightCount++;
					}
					totalCount++;
				}
			}
			Logger.log(LogLevel.Classification, "HodokuExtreme: " + rightCount
					+ " of " + totalCount + " sudokus correctly classified");
		}
		


		/*
		 * // read sudokus generated by soEinDing (classification sehr einfach)
		 * // from textfile sr = new SudokuReader("sehrEinfach.txt");
		 * ArrayList<Sudoku> sudokusSoEinDingSehrEinfach =
		 * sr.readFromSoEinDing();
		 * 
		 * Logger.log(LogLevel.GeneralInformation,
		 * "classification: soEinDing Sehr Einfach");
		 * Logger.log(LogLevel.GeneralInformation,
		 * sudokusSoEinDingSehrEinfach.size() + " sudokus loaded.");
		 * 
		 * soEinDingSehrEinfach =
		 * getFeatureVectors(sudokusSoEinDingSehrEinfach);
		 * 
		 * // read sudokus generated by soEinDing (classification einfach) //
		 * from textfile sr = new SudokuReader("einfach.txt"); ArrayList<Sudoku>
		 * sudokusSoEinDingEinfach = sr.readFromSoEinDing();
		 * 
		 * Logger.log(LogLevel.GeneralInformation,
		 * "classification: soEinDing Einfach");
		 * Logger.log(LogLevel.GeneralInformation,
		 * sudokusSoEinDingEinfach.size() + " sudokus loaded.");
		 * 
		 * soEinDingEinfach = getFeatureVectors(sudokusSoEinDingEinfach);
		 * 
		 * // read sudokus generated by soEinDing (classification standard) //
		 * from textfile sr = new SudokuReader("standard.txt");
		 * ArrayList<Sudoku> sudokusSoEinDingStandard = sr.readFromSoEinDing();
		 * 
		 * Logger.log(LogLevel.GeneralInformation,
		 * "classification: soEinDing Standard");
		 * Logger.log(LogLevel.GeneralInformation,
		 * sudokusSoEinDingStandard.size() + " sudokus loaded.");
		 * 
		 * soEinDingStandard = getFeatureVectors(sudokusSoEinDingStandard);
		 * 
		 * // read sudokus generated by soEinDing (classification moderat) //
		 * from textfile sr = new SudokuReader("moderat.txt"); ArrayList<Sudoku>
		 * sudokusSoEinDingModerat = sr.readFromSoEinDing();
		 * 
		 * Logger.log(LogLevel.GeneralInformation,
		 * "classification: soEinDing Moderat");
		 * Logger.log(LogLevel.GeneralInformation,
		 * sudokusSoEinDingModerat.size() + " sudokus loaded.");
		 * 
		 * soEinDingModerat = getFeatureVectors(sudokusSoEinDingModerat);
		 * 
		 * // read sudokus generated by soEinDing (classification anspruchsvoll)
		 * // from textfile sr = new SudokuReader("anspruchsvoll.txt");
		 * ArrayList<Sudoku> sudokusSoEinDingAnspruchsvoll = sr
		 * .readFromSoEinDing();
		 * 
		 * Logger.log(LogLevel.GeneralInformation,
		 * "classification: soEinDing Anspruchsvoll");
		 * Logger.log(LogLevel.GeneralInformation,
		 * sudokusSoEinDingAnspruchsvoll.size() + " sudokus loaded.");
		 * 
		 * soEinDingAnspruchsvoll =
		 * getFeatureVectors(sudokusSoEinDingAnspruchsvoll);
		 * 
		 * // read sudokus generated by soEinDing (classification sehr //
		 * anspruchsvoll) // from textfile sr = new
		 * SudokuReader("sehrAnspruchsvoll.txt"); ArrayList<Sudoku>
		 * sudokusSoEinDingSehrAnspruchsvoll = sr .readFromSoEinDing();
		 * 
		 * Logger.log(LogLevel.GeneralInformation,
		 * "classification: soEinDing Sehr Anspruchsvoll");
		 * Logger.log(LogLevel.GeneralInformation,
		 * sudokusSoEinDingSehrAnspruchsvoll.size() + " sudokus loaded.");
		 * 
		 * soEinDingSehrAnspruchsvoll =
		 * getFeatureVectors(sudokusSoEinDingSehrAnspruchsvoll);
		 * 
		 * // read sudokus generated by soEinDing (classification teuflisch) //
		 * from textfile sr = new SudokuReader("teuflisch.txt");
		 * ArrayList<Sudoku> sudokusSoEinDingTeuflisch = sr.readFromSoEinDing();
		 * 
		 * Logger.log(LogLevel.GeneralInformation,
		 * "classification: soEinDing Teuflisch");
		 * Logger.log(LogLevel.GeneralInformation,
		 * sudokusSoEinDingTeuflisch.size() + " sudokus loaded.");
		 * 
		 * soEinDingTeuflisch = getFeatureVectors(sudokusSoEinDingTeuflisch);
		 */

		Logger.exit();
	}

	public static ArrayList<FeatureVector> getFeatureVectors(
			ArrayList<Sudoku> sudokus) throws Exception {

		ArrayList<FeatureVector> featureVectors = new ArrayList<FeatureVector>();

		int solvedCount = 0;
		int notSolvedCount = 0;

		for (int i = 0; i < sudokus.size(); i++) {
			// Save the start condition of the sudoku for debug purposes
			String startSudoku = sudokus.get(i).toString();

			// get the current sudoku
			Sudoku sudoku = sudokus.get(i);
			Solver solver = new Solver(sudoku);

			// draw them while solving
			// mw.setSolver(solver);

			// start solving
			sudoku = solver.solve();

			// get the feature vector
			FeatureVector fv = solver.getFeatureVector();
			featureVectors.add(fv);

			Logger.log(LogLevel.FeatureVector, fv.toString());

			if (solver.solutionCheck())
				solvedCount++;
			else {
				Logger.log(LogLevel.Debug, "start sudoku: \n" + startSudoku);
				Logger.log(LogLevel.Debug, "end sudoku: \n" + sudoku.toString());
				notSolvedCount++;
			}
		}

		Logger.log(LogLevel.GeneralInformation, "Solved " + solvedCount
				+ " sudokus");
		Logger.log(LogLevel.GeneralInformation, "Couldn�t solve "
				+ notSolvedCount + " sudokus");

		System.out.println();

		int total = solvedCount + notSolvedCount;

		return featureVectors;
	}
}
