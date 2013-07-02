package model;

import java.util.HashMap;

public class FeatureVector {
	// Häufigkeitsverteilung
	private HashMap<Method, HashMap<Integer, Integer>> methods = new HashMap<Method, HashMap<Integer, Integer>>();
	private HashMap<Integer, Integer> numbers = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> possibilities = new HashMap<Integer, Integer>();

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
			numbers.put(i, 0);
		}

		// iterate over every possibilitiy
		for (int i = 1; i < 10; i++) {
			possibilities.put(i, 0);
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

	public void addNumberCount(int number, int count) {
		numbers.put(number, count);
	}

	public void addPossibilityCount(int number, int count) {
		possibilities.put(number, count);
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
		for (int i = 1; i < 10; i++) {
			int count = 0;
			if (numbers.containsKey(i))
				count = numbers.get(i);

			s += "\t" + i + ": " + count + "\n";
		}

		s += "\n\n\namount of possible fields at the start\n\n";
		// iterate over every possibility
		for (int i = 1; i < 10; i++) {
			int count = 0;
			if (possibilities.containsKey(i))
				count = possibilities.get(i);

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
