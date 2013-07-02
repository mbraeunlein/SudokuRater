package classificator;

import io.LogLevel;
import io.Logger;

import java.util.*;
import java.util.Map.Entry;

import model.*;

public class kNearestNeighbor {

	private HashMap<Classification, ArrayList<FeatureVector>> examples = new HashMap<Classification, ArrayList<FeatureVector>>();

	public void train(Classification classification, FeatureVector featureVector) {
		ArrayList<FeatureVector> vectors = new ArrayList<FeatureVector>();
		if (examples.containsKey(classification))
			vectors = examples.get(classification);

		vectors.add(featureVector);
		examples.put(classification, vectors);
	}

	public Classification classify(FeatureVector featureVector, int k) {
		Neighbors neighbors = new Neighbors(k);
		Classification[] classifications = Classification.values();

		// iterate over all classifications
		for (int i = 0; i < classifications.length; i++) {
			if (examples.containsKey(classifications[i])) {
				ArrayList<FeatureVector> vectors = examples
						.get(classifications[i]);

				// iterate over all examples in the classification
				for (int j = 0; j < vectors.size(); j++) {
					// compute the distance and add it to the neighbors
					double distance = featureVector.getEuklidDistance(vectors
							.get(j), 2, 1, 1);
					neighbors.add(new Tuple<Double, Classification>(distance, classifications[i]));
				}
			} else {
				Logger.log(LogLevel.Debug,
						"No example for classification " + classifications[i]
								+ " learned!");
			}

		}
		
		// do the majority voting to estimate the classification
		return neighbors.majority();
	}

	// a class that holds a list of distances with the classifications they
	// belong to.
	// it is initialized with a parameter representing the number of neighbors
	// it should hold,
	// if size is k, it only holds the k closest neighbors
	private class Neighbors {
		public ArrayList<Tuple<Double, Classification>> closest = new ArrayList<Tuple<Double, Classification>>();
		private int size;

		public Neighbors(int size) {
			this.size = size;
		}

		public int size() {
			return size;
		}

		public void add(Tuple<Double, Classification> neighbor) {
			for (int i = 0; i < size; i++) {
				try {
					Tuple<Double, Classification> current = closest.get(i);
					if (current.x > neighbor.x) {
						closest.add(i, neighbor);
						if (closest.size() > size)
							closest.remove(size);
						return;
					}
				} catch (IndexOutOfBoundsException e) {
					closest.add(neighbor);
					return;
				}
			}
		}
		
		public Classification majority() {
			HashMap<Classification, Integer> voteMap = new HashMap<Classification, Integer>();
			
			for(int i = 0; i < size; i++) {
				try {
					Tuple<Double, Classification> current = closest.get(i);
					
					int count = 0;
					if(voteMap.containsKey(current.y))
						count = voteMap.get(current.y);
					count++;
					voteMap.put(current.y, count);
					
				} catch (IndexOutOfBoundsException e) {
					
				}
			}
			
			Entry<Classification, Integer> maxEntry = null;

			for(Entry<Classification, Integer> entry : voteMap.entrySet()) {
			    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
			        maxEntry = entry;
			    }
			}
			
			// iterate over all classifications
			return maxEntry.getKey();
		}
	}

	private class Tuple<X, Y> {
		public final X x;
		public final Y y;

		public Tuple(X x, Y y) {
			this.x = x;
			this.y = y;
		}
	}
}
