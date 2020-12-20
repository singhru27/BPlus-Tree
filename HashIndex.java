import java.util.*;

// Creating a new index based off of a hash index with posting list

public class HashIndex {

	// A tree has a root node, and an order
	public HashMap<Integer, Integer> keyPairs;

	// Required methods to implement. DO NOT change the function signatures of
	// these methods.

	// Instantiate a new HashIndex implementing the same functions as the B+ Tree
	public HashIndex() {
		keyPairs = new HashMap<Integer, Integer>();
	}

	// Given a key, returns the value associated with that key or null if doesn't
	// exist
	public Integer get(Integer key) {
		return keyPairs.get(key);

	}

	// Insert a key-value pair into the tree. This tree does not need to support
	// duplicate keys
	public void insert(Integer key, Integer value) {
		keyPairs.put(key, value);

	}

	// Delete a key and its value from the tree
	public void delete(Integer key) {
		keyPairs.remove(key);
	}

	// Returns a range of values
	public TupleIDSet getRange(Integer low, Integer high) {
		TupleIDSet allTuples = new TupleIDSet();
		
		while (low.compareTo(high) <= 0) {
			if (keyPairs.containsKey(low)) {
				allTuples.add(keyPairs.get(low));
			}
			low += 1;
		}
		
		return allTuples;

	}

}