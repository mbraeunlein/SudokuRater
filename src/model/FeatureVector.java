package model;

import java.util.HashMap;

public class FeatureVector {
	private HashMap<Method, HashMap<Integer, Integer>> methods = new HashMap<Method, HashMap<Integer, Integer>>();
	private HashMap<Integer, Integer> numbers = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> possibilities = new HashMap<Integer, Integer>();
	
	public void addMethod(Method method, int number) {
		HashMap<Integer, Integer> removedCounterList = new HashMap<Integer, Integer>();
		if(methods.containsKey(method))
			removedCounterList = methods.get(method);
		
		int removedCounter = 0;
		if(removedCounterList.containsKey(number))
			removedCounter = removedCounterList.get(number);
		
		removedCounter ++;
		
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
		for(int i = 0; i < m.length; i++) {
			s += "\n\n\t" + m[i].toString() + "\n\n";
			
			HashMap<Integer, Integer> removedCounterList = new HashMap<Integer, Integer>();
			if(methods.containsKey(m[i]))
				removedCounterList = methods.get(m[i]);
			
			// iterate over every number
			for(int j = 1; j < 10; j++) {
				int count = 0;
				if(removedCounterList.containsKey(j))
					count = removedCounterList.get(j);
				s += "\t\t" + j +": " + count + "\n";
			}
		}
		
		
		s += "\n\n\namount of already filled in numbers at the start\n\n";
		// iterate over every number
		for(int i = 1; i < 10; i++) {
			int count = 0;
			if(numbers.containsKey(i))
				count = numbers.get(i);
			
			s += "\t" + i + ": " + count +"\n";
		}
		
		s += "\n\n\namount of possible fields at the start\n\n";
		// iterate over every number
		for(int i = 1; i < 10; i++) {
			int count = 0;
			if(possibilities.containsKey(i))
				count = possibilities.get(i);
			
			s += "\t" + i + ": " + count +"\n";
		}
		return s;
	}
}
