import java.util.*;

// The BPlusTree class. You'll need to fill the methods in. DO NOT change the
// function signatures of these methods. Our checker relies on these methods and
// assume these function signatures.

public class BPlusTree {

	// A tree has a root node, and an order
	public Node root;
	public Integer _order;

	// Required methods to implement. DO NOT change the function signatures of
	// these methods.

	// Instantiate a BPlusTree with a specific order
	public BPlusTree(Integer order) {
		_order = order;
	}

	// Given a key, returns the value associated with that key or null if doesn't
	// exist
	public Integer get(Integer key) {
		if (root == null) {
			return null;
		}
		return root.get(key);
	}

	// Insert a key-value pair into the tree. This tree does not need to support
	// duplicate keys
	public void insert(Integer key, Integer value) {
		/*
		 * If the root has not yet been initialized, we create a new root
		 */
		if (root == null) {
			root = new LNode(_order);
			// Adding in the information to the node
			root.insert(key, value);
		}
		// Otherwise, we call insert on the root. If the root splits, we create two
		// children
		Split<Node> newChildren = root.insert(key, value);
		// If newRoot returns null, no split was needed
		if (newChildren == null) {
			return;
		}
		// Otherwise, we need to recurse the split up
		INode newRoot = new INode(_order, newChildren);
		root = newRoot;

	}

	// Delete a key and its value from the tree
	public void delete(Integer key) {
		root.delete(key);
	}

	// Returns a range of values
	public TupleIDSet getRange(Integer low, Integer high) {
		TupleIDSet allTuples = new TupleIDSet();
		// Returning tuples from the tree
		return root.getRange(low, high, allTuples);
	}

}

// DO NOT change this enum. There are two types of nodes; an Internal node, and
// a Leaf node
enum NodeType {
	LEAF, INTERNAL,
}

// This class encapsulates the pair of left and right nodes after a split
// occurs, along with the key that divides the two nodes. Both leaf and internal
// nodes split. For this reason, we use Java's generics (e.g. <T extends Node>).
// This is a helper class. Your implementation might not need to use this class
class Split<T extends Node> {
	public Integer key;
	public T left;
	public T right;

	public Split(Integer k, T l, T r) {
		key = k;
		right = r;
		left = l;
	}
}

// An abstract class for the node. Both leaf and internal nodes have the a few
// attributes in common.
abstract class Node {

	// DO NOT edit this attribute. You should use to store the keys in your
	// nodes. Our checks for correctness rely on this attribute. If you change
	// it, your tree will not be correct according to our checker. Values in
	// this array that are not valid should be null.
	public Integer[] keys;

	// Do NOT edit this attribute. You should use it to keep track of the number
	// of CHILDREN or VALUES this node has. Our checks for correctness rely on
	// this attribute. If you change it, your tree will not be correct according
	// to our checker.
	public Integer numChildren;

	// DO NOT edit this method.
	abstract NodeType nodeType();

	// You may edit everything that occurs in this class below this line.
	// *********************************************************************
	public Integer _order;

	// Both leaves and nodes need to keep track of a few things:
	// Their parent
	// A way to tell another class whether it is a leaf or a node

	// A node is instantiated by giving it an order, and a node type
	public Node(Integer order, NodeType nt) {
		_order = order;
	}

	// A few things both leaves and internal nodes need to do. You are likely
	// going to need to implement these functions. Our correctness checks rely
	// on the structure of the keys array, and values and children arrays in the
	// leaf and child nodes so you may choose to forgo these functions.

	// You might find that printing your nodes' contents might be helpful in
	// debugging. The function signature here assumes spaces are used to
	// indicate the level in the tree.
	// abstract void print(Integer nspaces);

	// Abstract method to search for key
	abstract Integer search(Integer key);

	/*
	 * You might want to implement an insert method. We use the Split class to
	 * indicate whether a node split as a result of an insert because splits in
	 * lower levels of the tree may propagate upward.
	 */
	abstract Split insert(Integer key, Integer value);

	/*
	 * You might want to implement a delete method that traverses down the tree
	 * calling a child's delete method until you hit the leaf.
	 */
	abstract void delete(Integer key);

	// You might want to implement a get method that behaves similar to the
	// delete method. Here, the get method recursively calls the child's get
	// method and returns the integer up the recursion.
	abstract Integer get(Integer key);

	// Method to return a range of values
	abstract TupleIDSet getRange(Integer low, Integer high, TupleIDSet allTuples);

	/*
	 * Method to return middle element of a node. TESTED AND WORKING
	 */
	public Integer mid() {
		return _order / 2;
	}

	// You might want to implement a split method for nodes that need to split.
	// We use the split class defined above to encapsulate the information
	// resulting from the split.
	Split split() {
		return null;
	} // Note the use of split here

	// You might want to implement a helper function that cleans up a node. Note
	// that the keys, values, and children of a node should be null if it is
	// invalid. Java's memory manager won't garbage collect if there are
	// references hanging about.
	void cleanEntries() {
	}
}

// A leaf node (LNode) is an instance of a Node
class LNode extends Node {
	// DO NOT edit this attribute. You should use to store the values in your
	// leaf node. Our checks for correctness rely on this attribute. If you
	// change it, your tree will not be correct according to our checker. Values
	// in this array that are not valid should be null.
	public Integer[] values;

	// DO NOT edit this method;
	public NodeType nodeType() {

		return NodeType.LEAF;

	};
	// You may edit everything that occurs in this class below this line.
	// *************************************************************************

	// A leaf has siblings on the left and on the right.
	public Node _rightSibling;

	// A leaf node is instantiated with an order
	public LNode(Integer order) {
		// Because this is also a Node, we instantiate the Node (abstract)
		// superclass, identifying itself as a leaf.
		super(order, NodeType.LEAF);
		/*
		 * Creating holders for the keys and values
		 */
		keys = new Integer[order];
		values = new Integer[order];
		_rightSibling = null;
		numChildren = 0;
	}

	/*
	 * Returns the key in keys s.t key >= required value
	 */
	@Override
	public Integer search(Integer key) {
		for (Integer i = 0; i < keys.length; i++) {
			// If we have reached the end of possible values, return num_children
			if (keys[i] == null) {
				return numChildren;
			}
			// Else return the desired key
			if (keys[i].compareTo(key) >= 0) {

				return i;
			}
		}
		// If we reach the end of the node, we return the numChildren again. (Should
		// never reach this, as the node will split
		// before reaching this point
		return numChildren;
	}

	/*
	 * Inserts the desired key into the leaf. If the size has reached max capacity,
	 * we split and return the split object to be used by the parent
	 */
	@Override
	public Split<Node> insert(Integer key, Integer value) {
		Integer index = this.search(key);
		// If the index = numChildren, we insert the key at the very end of the node
		if (index.equals(numChildren)) {
			keys[index] = key;
			values[index] = value;
			numChildren += 1;
		}
		// If the given key is already in keys, we simply replace the value
		else if (keys[index].equals(key)) {
			values[index] = value;
		}
		// Otherwise, we have reached a case in which we need to shift the values
		// down
		else {
			System.arraycopy(keys, index, keys, index + 1, keys.length - index - 1);
			System.arraycopy(values, index, values, index + 1, values.length - index - 1);
			keys[index] = key;
			values[index] = value;
			numChildren += 1;
		}

		// If node doesn't require splitting, return null
		if (numChildren.compareTo(_order) < 0) {
			return null;
		}
		/*
		 * Indicates that we have reached a point that requires splitting. Create a new
		 * leaf node, encapsulate into a Split object, and return to the caller
		 */
		LNode rightNode = new LNode(_order);
		Integer midIndex = this.mid();
		Integer midKey = keys[midIndex];
		Integer temp = numChildren;
		// Copying elements over into the right node
		for (Integer i = midIndex; i < temp; i++) {
			Integer currKey = keys[i];
			Integer currValue = values[i];
			// Inserting into right node
			rightNode.insert(currKey, currValue);
			// Setting to null in the current node and decrementing children
			keys[i] = null;
			values[i] = null;
			numChildren -= 1;
		}
		// Setting the sibling pointer
		if (_rightSibling != null) {
			rightNode._rightSibling = _rightSibling;
		}
		_rightSibling = rightNode;
		// Returning the wrapper
		Split<Node> newNodeSet = new Split<Node>(midKey, this, rightNode);
		return newNodeSet;
	}

	/*
	 * Deletes the chosen key/value pair from the leaf node TESTED AND WORKING
	 */
	@Override
	public void delete(Integer key) {
		Integer index = this.search(key);
		// If it doesn't exist, do nothing
		if (!keys[index].equals(key) || index.equals(numChildren)) {
			return;
		}
		// If it does exist, shift the array back
		System.arraycopy(keys, index + 1, keys, index, keys.length - index - 1);
		System.arraycopy(values, index + 1, values, index, values.length - index - 1);
		numChildren -= 1;
	}

	/*
	 * Retrieves the chosen value from the leaf node, if it exists
	 */
	@Override
	public Integer get(Integer key) {
		Integer index = this.search(key);
		// If it doesn't exist, do nothing
		if (index.equals(numChildren)  || !keys[index].equals(key) ) {
			return null;
		}
		// If it exists, return the chosen value
		return values[index];
	}
	
	/*
	 * Retrieves a list of chosen tuples in range
	 */
	@Override
	public TupleIDSet getRange(Integer low, Integer high, TupleIDSet allTuples) {
		int index = this.search(low);
		// Handling the case in which the index is larger than anything in the current node
		if (index == this.numChildren) {
			if (this._rightSibling != null) {
				return this._rightSibling.getRange(low, high, allTuples);
			} else {
				return allTuples;
			}
		}
		// Looping through
		while (this.keys[index].compareTo(high) <= 0) {
			// Adding to all tuples
			allTuples.add(values[index]);
			index += 1;
			// Handling the case in which the index is larger than anything in the current node
			if (index == this.numChildren) {
				if (this._rightSibling != null) {
					return this._rightSibling.getRange(keys[index - 1], high, allTuples);
				} else {
					return allTuples;
				}
			}
		}
		return allTuples;
	}
}

// An internal node (INode) is an instance of a Node
class INode extends Node {

	// DO NOT edit this attribute. You should use to store the children of this
	// internal node. Our checks for correctness rely on this attribute. If you
	// change it, your tree will not be correct according to our checker. Values
	// in this array that are not valid should be null.
	// An INode (as opposed to a leaf) has children. These children could be
	// either leaves or internal nodes. We use the abstract Node class to tell
	// Java that this is the case. Using this abstract class allows us to call
	// abstract functions regardless of whether it is a leaf or an internal
	// node. For example, children[x].get() would work regardless of whether it
	// is a leaf or internal node if the get function is an abstract method in
	// the Node class.
	public Node[] children;

	// DO NOT edit this method;
	public NodeType nodeType() {
		return NodeType.INTERNAL;
	};

	// You may edit everything that occurs in this class below this line.
	// *************************************************************************

	/*
	 * A leaf node is instantiated with an order, and a Split object (since internal
	 * nodes are always created via splits of the child nodes
	 */

	public INode(Integer order, Split<Node> splitNode) {

		// Because this is also a Node, we instantiate the Node (abstract)
		// superclass, identifying itself as a leaf.
		super(order, NodeType.INTERNAL);
		// Instantiating keys and values
		children = new Node[order + 1];
		keys = new Integer[order];
		numChildren = 0;
		// Creating the initial children of this node, if this was a node that was split
		// "upward"
		if (splitNode != null) {
			children[0] = splitNode.left;
			children[1] = splitNode.right;
			keys[0] = splitNode.key;
			numChildren = 2;
		}

	}

	/*
	 * Search Method
	 */
	@Override
	public Integer search(Integer key) {

		for (Integer i = 0; i < keys.length; i++) {
			// If the searched for key is larger than the entire array, return earliest null
			// element
			if (keys[i] == null) {
				return numChildren - 1;
			}
			// If the desired index is reached, return that index
			if (keys[i].compareTo(key) >= 0) {
				return i;
			}
		}
		/*
		 * If we reach the end of the node, we simply return numChildren - 1. This
		 * should never be reached
		 */
		return numChildren - 1;
	}

	/*
	 * Inserts the desired key into the leaf. If the size has reached max capacity,
	 * then we split
	 */
	@Override
	public Split<Node> insert(Integer key, Integer value) {
		Integer index = this.search(key);
		Split<Node> newNode;
		// Inserting into left child/last child if key not equal to value
		if (index.equals((numChildren - 1)) || key.compareTo(keys[index]) < 0) {
			newNode = children[index].insert(key, value);
			// Otherwise inserting into right child.
		} else {
			newNode = children[index + 1].insert(key, value);

		}
		// If neither caused a problem, then we can simply return null
		if (newNode == null) {
			return null;
		}
		// Otherwise, we need to insert the newly created right node into the tree
		Integer newIndex = this.search(newNode.key);
		// Adding to the end if the key is greater than all keys
		if (newIndex.equals(numChildren - 1)) {
			keys[newIndex] = newNode.key;
			children[newIndex + 1] = newNode.right;
		} else {
			// Otherwise, we need to insert into the middle by copying everything down
			System.arraycopy(keys, newIndex, keys, newIndex + 1, keys.length - newIndex - 1);
			System.arraycopy(children, newIndex + 1, children, newIndex + 2, children.length - (newIndex + 1) - 1);
			keys[newIndex] = newNode.key;
			children[newIndex + 1] = newNode.right;
		}

		numChildren += 1;
		// Handling the case requiring further splits
		if (numChildren.compareTo(_order) > 0) {
			INode rightNode = new INode(_order, null);
			Integer midIndex = this.mid();
			Integer startRight = midIndex + 1;
			Integer midKey = keys[midIndex];
			keys[midIndex] = null;
			// Transferring over all keys and values to the right node
			for (Integer i = midIndex + 1; i < keys.length; i++) {
				rightNode.keys[i - startRight] = keys[i];
				keys[i] = null;
			}
			for (Integer i = midIndex + 1; i < children.length; i++) {
				rightNode.children[i - startRight] = children[i];
				rightNode.numChildren += 1;
				children[i] = null;
				numChildren -= 1;
			}
			// Passing the information up
			Split<Node> newNodeSet = new Split<Node>(midKey, this, rightNode);

			return newNodeSet;
			// Else returning null
		} else {
			return null;
		}
	}

	/*
	 * Deletes the chosen key/value pair from the leaf node, propagating down the
	 * chain
	 */
	@Override
	public void delete(Integer key) {
		Integer index = this.search(key);
		if (index.equals(numChildren - 1) || key.compareTo(keys[index]) < 0) {
			children[index].delete(key);
		} else {
			children[index + 1].delete(key);
		}
	}

	/*
	 * Retrieves the chosen value from the leaf node, if it exists
	 */
	@Override
	public Integer get(Integer key) {
		Integer index = this.search(key);
		if (index.equals(numChildren - 1) || key.compareTo(keys[index]) < 0) {
			return children[index].get(key);
		} else {
			return children[index + 1].get(key);
		}
	}

	/*
	 * Retrieves a range of values
	 */
	@Override
	public TupleIDSet getRange(Integer low, Integer high, TupleIDSet allTuples) {
		Integer index = this.search(low);
		if (index.equals(numChildren - 1) || low.compareTo(keys[index]) < 0) {
			return children[index].getRange(low, high, allTuples);
		} else {
			return children[index + 1].getRange(low, high, allTuples);
		}
	}
}
