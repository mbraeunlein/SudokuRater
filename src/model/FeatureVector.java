package model;

import java.util.ArrayList;
import java.util.HashMap;

public class FeatureVector {
	// Häufigkeitsverteilung
	public HashMap<Method, HashMap<Integer, Integer>> methods = new HashMap<Method, HashMap<Integer, Integer>>();
	public ArrayList<Integer> numbers = new ArrayList<Integer>();
	public ArrayList<Integer> possibilities = new ArrayList<Integer>();

	public FeatureVector() {
		Method[] m = Method.values();

		// iterate over every method
		for (int i = 0; i < m.length; i++) {
			HashMap<Integer, Integer> removedCounterList = new HashMap<Integer, Integer>();
			// iterate over every number
			for (int j = 1; j < 10; j++) {
				removedCounterList.put(j, 0);
			}
			methods.put(m[i], removedCounterList);
		}

		// iterate over every number
		for (int i = 1; i < 10; i++) {
			numbers.add(0);
		}

		// iterate over every possibilitiy
		for (int i = 1; i < 10; i++) {
			possibilities.add(0);
		}
	}

	public void addMethod(Method method, int number) {
		addMethod(method, number, 1);
	}

	public void addMethod(Method method, int number, int count) {
		HashMap<Integer, Integer> removedCounterList = new HashMap<Integer, Integer>();
		if (methods.containsKey(method))
			removedCounterList = methods.get(method);

		int removedCounter = 0;
		if (removedCounterList.containsKey(number))
			removedCounter = removedCounterList.get(number);

		removedCounter += count;

		removedCounterList.put(number, removedCounter);
		methods.put(method, removedCounterList);
	}

	public void addNumberCount(int count) {
		numbers.add(count);
		for(int i = 0; i < numbers.size(); i++) {
			if(numbers.get(i) < count) {
				numbers.add(i, count);
				return;
			}
		}
	}

	public void addPossibilityCount(int count) {
		possibilities.add(count);
		for(int i = 0; i < possibilities.size(); i++) {
			if(possibilities.get(i) < count) {
				possibilities.add(i, count);
				return;
			}
		}
	}

	@Override
	public String toString() {
		String s = "";

		s += "Used methods:\n";
		Method[] m = Method.values();

		// iterate over every method
		for (int i = 0; i < m.length; i++) {
			s += "\n\n\t" + m[i].toString() + "\n\n";

			HashMap<Integer, Integer> removedCounterList = new HashMap<Integer, Integer>();
			if (methods.containsKey(m[i]))
				removedCounterList = methods.get(m[i]);

			// iterate over every number
			for (int j = 1; j < 10; j++) {
				int count = 0;
				if (removedCounterList.containsKey(j))
					count = removedCounterList.get(j);
				s += "\t\t" + j + ": " + count + "\n";
			}
		}

		s += "\n\n\namount of already filled in numbers at the start\n\n";
		// iterate over every number
		for (int i = 0; i < 9; i++) {
			int count = numbers.get(i);

			s += "\t" + i + ": " + count + "\n";
		}

		s += "\n\n\namount of possible fields at the start\n\n";
		// iterate over every possibility
		for (int i = 0; i < 9; i++) {
			int count = possibilities.get(i);

			s += "\t" + i + ": " + count + "\n";
		}
		return s;
	}

	public double getEuklidDistance(FeatureVector other) {
		return getEuklidDistance(other, 1, 1, 1);
	}

	public double getEuklidDistance(FeatureVector other, int methodWeight,
			int startNumbersWeight, int startPossibilitiesWeight) {

		double result = 0;

		Method[] m = Method.values();

		// iterate over every method
		for (int i = 0; i < m.length; i++) {

			HashMap<Integer, Integer> removedNumberList = methods.get(m[i]);
			HashMap<Integer, Integer> otherRemovedNumberList = other.methods
					.get(m[i]);

			// iterate over every number
			for (int j = 1; j < 10; j++) {
				// add the distance between the two positions in the vector
				result += methodWeight
						* Math.pow(removedNumberList.get(j)
								- otherRemovedNumberList.get(j), 2);
			}
		}

		// iterate over every number
		for (int i = 1; i < 10; i++) {
			int count = numbers.get(i);
			int otherCount = other.numbers.get(i);

			result += startNumbersWeight * Math.pow(count - otherCount, 2);
		}

		// iterate over every possibility
		for (int i = 1; i < 10; i++) {
			int count = possibilities.get(i);
			int otherCount = other.possibilities.get(i);
			
			result += startPossibilitiesWeight * Math.pow(count - otherCount, 2);
		}

		result = Math.sqrt(result);
		return result;
	}
}
