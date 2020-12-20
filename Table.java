import java.util.*;

// DO NOT CHANGE THE METHOD SIGNATURE FOR THE METHODS WE GIVE YOU BUT YOU MAY
// CHANGE THE METHOD'S IMPLEMENTATION

public class Table {

	String name = "This is table";
	Hashtable<String, Column> attributes;
	Vector<Boolean> valid;
	Integer numTuples;
	HashMap<Integer, List<Integer>> postingList;
	BPlusTree secondaryIndex;
	BPlusTree primaryIndex;
	Integer minSecondary;
	Integer maxSecondary;
	Integer minClustered;
	Integer maxClustered;

	public Table(String table_name, HashSet<String> attribute_names) {
		name = table_name;
		attributes = new Hashtable<String, Column>();
		valid = new Vector<Boolean>();
		numTuples = 0;

		for (String attribute_name : attribute_names) {
			attributes.put(attribute_name, new Column());
		}
	}

	// Method to sort all of the columns
	public void sort(String attribute) {
		Column indexedAttribute = attributes.get(attribute);
		for (Column col : attributes.values()) {
			// Skipping the column that we are sorting on, for now
			if (col.equals(indexedAttribute)) {
				continue;
			}
			sorterHelper(indexedAttribute, col);
		}
		// Returning to sort the original column
		Collections.sort(indexedAttribute);
	}

	// Helper method for sorting attribute columns
	public static <T extends Comparable<T>> void sorterHelper(final List<T> key, List<?> list) {
		// Create a List of indices
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < key.size(); i++)
			indices.add(i);
		// Sort based off of the key
		Collections.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(Integer i, Integer j) {
				return key.get(i).compareTo(key.get(j));
			}
		});
		Map<Integer, Integer> swapper = new HashMap<Integer, Integer>(indices.size());
		List<Integer> originSwap = new ArrayList<Integer>(indices.size()),
		destSwap = new ArrayList<Integer>(indices.size());
		// create a mapping to sort via swappings
		for (int i = 0; i < key.size(); i++) {
			int newInt = indices.get(i);
			while (i != newInt && swapper.containsKey(newInt))
				newInt = swapper.get(newInt);
			swapper.put(i, newInt);
			destSwap.add(newInt);
			originSwap.add(i);
		}
		// use the swap order to swap each list, based off of that order
		for (int i = 0; i < list.size(); i++)
			Collections.swap(list, originSwap.get(i), destSwap.get(i));
	}

	public void setClusteredIndex(String attribute) {
		primaryIndex = new BPlusTree(100);
		Column indexedAttribute = attributes.get(attribute);
		indexedAttribute.setClustered();
		this.sort(attribute);

		// First pass through, putting ID in the primaryIndex. Only the very first
		// instance of each ID is put in
		for (Integer ID = 0; ID < numTuples; ID++) {
			// If the key is invalid, skip
			if (!valid.get(ID)) {
				continue;
			}
			Integer currValue = indexedAttribute.get(ID);
			// Setting minimum and maximum values
			if (minClustered == null || currValue.compareTo(minClustered) < 0) {
				minClustered = currValue;
			}
			if (maxClustered == null || currValue.compareTo(maxClustered) > 0) {
				maxClustered = currValue;
			}
			// If the B+ Tree does not contain this value, put it in. This ensures that we
			// only
			// put the first instance of each value in
			if (primaryIndex.get(currValue) == null) {
				primaryIndex.insert(currValue, ID);
			}
		}
	}

	public void setSecondaryIndex(String attribute) {
		secondaryIndex = new BPlusTree(100);
		postingList = new HashMap<Integer, List<Integer>>();
		Column indexedAttribute = attributes.get(attribute);
		indexedAttribute.setSecondary();
		// First pass through, putting ID in secondaryIndex or HashMap
		for (Integer ID = 0; ID < numTuples; ID++) {
			// If the key is invalid, skip
			if (!valid.get(ID)) {
				continue;
			}

			Integer currValue = indexedAttribute.get(ID);
			// Setting minimum and maximum values
			if (minSecondary == null || currValue.compareTo(minSecondary) < 0) {
				minSecondary = currValue;
			}
			if (maxSecondary == null || currValue.compareTo(maxSecondary) > 0) {
				maxSecondary = currValue;
			}
			// If the B+ Tree does not contain this value, put it in
			if (secondaryIndex.get(currValue) == null) {
				secondaryIndex.insert(currValue, ID);

			} else {
				// Case for when the posting list already contains this key
				if (postingList.containsKey(currValue)) {
					List<Integer> currList = postingList.get(currValue);
					currList.add(ID);
					// Case for when the posting list does not yet contain the key
				} else {
					List<Integer> newList = new ArrayList<Integer>();
					newList.add(ID);
					postingList.put(currValue, newList);
				}
			}
		}
		/*
		 * Second pass through. If the value in the B+ tree is also associated with a
		 * list in the HashMap, it must be moved over to the posting list
		 */
		for (Integer ID = 0; ID < numTuples; ID++) {
			Integer currValue = indexedAttribute.get(ID);
			if (postingList.containsKey(currValue)) {
				List<Integer> currList = postingList.get(currValue);
				currList.add(secondaryIndex.get(currValue));
			}
		}

	}

	// Insert a tuple into the Table. NO CHANGE NECESSARY
	public void insert(Tuple tuple) {
		if (!attributes.keySet().equals(tuple.keySet())) {
			throw new RuntimeException("Tuples and attributes don't match");
		}
		for (String key : attributes.keySet()) {
			attributes.get(key).add(tuple.get(key));
		}
		numTuples += 1;
		valid.add(true);
	}

	// Loads each tuple into the Table. NO CHANGE NECESSARY
	public void load(Vector<Tuple> data) {
		for (Tuple datum : data) {
			this.insert(datum);
		}
	}

	// Uses a filter to find the qualifying tuples. Returns a set of tupleIDs
	public TupleIDSet filter(Filter f) {

		if (f.binary == false) {
			return filterHelperUnary(f);
		} else {
			return filterHelperBinary(f);
		}
	}

	TupleIDSet filterHelperBinary(Filter f) {
		TupleIDSet left, right;
		if (f.left.binary == true) {
			left = filterHelperBinary(f.left);
		} else {
			left = filterHelperUnary(f.left);
		}

		if (f.right.binary == true) {
			right = filterHelperBinary(f.right);
		} else {
			right = filterHelperUnary(f.right);
		}

		if (f.op == FilterOp.AND) {
			left.retainAll(right);
		} else if (f.op == FilterOp.OR) {
			left.addAll(right);
		}
		return left;
	}

	TupleIDSet filterHelperUnary(Filter f) {
		String attribute = f.attribute;
		Column col = attributes.get(attribute);
		if (col == null) {
			throw new RuntimeException("Column not in Table");
		}

		TupleIDSet result = new TupleIDSet();
		Integer counter = 0;

		// Case in which there is a secondary index
		if (col.isSecondary) {
			result = this.filterSecondaryIndex(f);

		} else if (col.isClustered) {
			result = this.filterClusteredIndex(f);
		} else {
			if ((f.low != null) && (f.high != null)) {
				for (Integer v : col) {
					if ((v >= f.low) && (v <= f.high) && valid.get(counter)) {
						result.add(counter);
					}
					counter++;
				}
			} else if (f.low != null) {
				for (Integer v : col) {
					if ((v >= f.low) && valid.get(counter)) {
						result.add(counter);
					}
					counter++;
				}
			} else if (f.high != null) {
				for (Integer v : col) {
					if ((v <= f.high) && valid.get(counter)) {
						result.add(counter);
					}
					counter++;
				}
			}
		}
		return result;
	}

	TupleIDSet filterClusteredIndex(Filter f) {
		String attribute = f.attribute;
		Column col = attributes.get(attribute);
		TupleIDSet result = new TupleIDSet();

		// Converting to the minimum and maximum of the dataset
		if (f.low == null || f.low.compareTo(minClustered) < 0) {
			f.low = minClustered;
		}
		if (f.high == null || f.high.compareTo(maxClustered) > 0) {
			f.high = maxClustered;
		}
		// Finding the first element matching the low
		Integer firstID = primaryIndex.get(f.low);
		while (firstID == null) {
			f.low += 1;
			firstID = primaryIndex.get(f.low);
			if (f.low.compareTo(f.high) > 0) {
				return result;
			}
		}
		// Adding all elements up until the highest element
		result.add(firstID);
		firstID += 1;
		while(firstID < numTuples && col.get(firstID).compareTo(f.high) <= 0) {
			result.add(firstID);
			firstID += 1;
		}
		return result;
	}

	TupleIDSet filterSecondaryIndex(Filter f) {
		TupleIDSet result = new TupleIDSet();
		// Converting to the minimum and maximum of the dataset
		if (f.low == null || f.low.compareTo(minSecondary) < 0) {
			f.low = minSecondary;
		}
		if (f.high == null || f.high.compareTo(maxSecondary) > 0) {
			f.high = maxSecondary;
		}
		// Case when both low and high are set
		if ((f.low != null) && (f.high != null)) {
			Integer currValue = f.low;
			// Adding to the hashset
			while (currValue.compareTo(f.high) <= 0) {
				if (postingList.containsKey(currValue)) {
					for (Integer ID : postingList.get(currValue)) {
						result.add(ID);
					}
				}
				currValue += 1;
			}
			// Getting all elements from the tree and adding
			TupleIDSet newTuples = secondaryIndex.getRange(f.low, f.high);
			if (newTuples != null) {
				result.addAll(newTuples);
			}
		}
		return result;
	}

	// Deletes a set of tuple ids. If ids is null, deletes all tuples. NO CHANGE
	// NECESSARY
	public void delete(TupleIDSet ids) {

		if (ids == null) {
			for (Integer i = 0; i < valid.size(); i++) {
				valid.set(i, false);
			}
		} else {
			for (Integer id : ids) {
				valid.set(id, false);
			}
		}

	}

	// Update an attribute for a set of tupleIds, to a given value
	// if tupleIds is null, updates all tuples. NO CHANGE NCESSARY
	public void update(String attribute, TupleIDSet ids, Integer value) {
		Column col = attributes.get(attribute);
		if (ids == null) {
			for (Integer i = 0; i < col.size(); i++) {
				col.set(i, value);
			}
		} else {
			for (Integer id : ids) {
				col.set(id, value);
			}
		}
	}

	// Materializes the set of valid tuple ids given. If no tuple ids given,
	// materializes all valid tuples. NO CHANGE NECESSARY
	public MaterializedResults materialize(Set<String> attributes, TupleIDSet tupleIds) {

		MaterializedResults result = new MaterializedResults();

		if (tupleIds != null) {
			for (Integer tupleId : tupleIds) {

				if (!valid.get(tupleId)) {
					throw new RuntimeException("tupleID is not valid");
				}

				Tuple t = new Tuple();
				for (String attribute : attributes) {
					t.put(attribute, this.attributes.get(attribute).get(tupleId));
				}

				result.add(t);

			}
		} else {
			for (Integer i = 0; i < valid.size(); i++) {
				if (valid.get(i)) {
					Tuple t = new Tuple();
					for (String attribute : attributes) {
						t.put(attribute, this.attributes.get(attribute).get(i));
					}
					result.add(t);
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		String v = name + " Columns: " + attributes.keySet();
		return v;
	}
}
