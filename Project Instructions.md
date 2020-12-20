# CS127 Indexing Programming Assignment

Disclaimer: We reserve the right to update this homework (within reason) while
this homework is active. We won't be making massive changes and will probably be
bug fixes or added features. If we do so, we will send a Piazza post about it
and you will be responsible for pulling these changes. To learn how to do it,
please read the Git Setup section below.

## Setup

All the code you need is in this zip. Feel free to use whatever code editor you want--the only requirement is access to a terminal. If you worked locally for ETL then you should be all set for java requirements. 


## Phases

We envision this project works out in 3 large phases. Each phase requires you to
check in with a TA. We'll release sign-in sheets for when these check-ins are
going to occur.

### Phase #1: Code base and B+ Tree

There are two tasks to the first phase:

1. First, get familiar with the codebase. We'll have a recorded recitation to get familiar with the codebase and how to get started.

2. Second, write the B+ tree code.  Want to know how a B+ tree works in a fun
   and interactive visualization? Check this out:

   https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html

#### Some important notes about the B+ tree we expect:

1. Your B+ tree doesn't need to handle duplicate keys. Doing so makes the B+
   tree **much much** harder to implement in some cases. There are easy
   solutions to this but you don't have to worry about them for now. We'll deal
   with it in the second phase.

2. When building your B+ trees, your tree should be able to handle deletes **but
   we don't require you to maintain some load factor on the tree**. In other
   words, you don't need to deal with merging nodes on delete. Besides making
   implementation much easier,  there's a
   specific performance reason for this.

### Phase #2: Bind B+ Tree to your table

You're going to need to bind your BPlusTree code into your Table and modify your
Table to use the B+ Tree. You're going to have to use this as a primary
(clustered) index, and as a secondary index.

For this stage, we recommend first modifying Column.java to make that work with your tree, and then incorportating the modified Column into the Table. 

### Phase #3: Investigate alternative indexing schemes

In this phase, you should explore alternatives to the B+ tree you build. Later
in this document, we'll discuss what counts and doesnt. Test your changes agains
the correctness tests and the benchmark. Feel free to create any necessary files for your alternative schemes.

When you are satisified (or about to run out of time), write a summary of what
you did and why in `writeup.md`. You should confirm that there is some gain to
your alternative indexing scheme by comparing benchmark results. If it's
actually slower, then also explain why.

## Submitting

Zip all of the files in the distributed directory and then submit that to canvas.

For phase 3's writeup, write it in `writeup/writeup.md`. To make it easier to
read, use the markdown syntax. If you are reading this file in a text editor,
you are currently reading this document in markdown syntax. Include this file
in your zip.

## Learning Goals and Tasks

In this programming assignment, students learn how to:

* implement a B+ tree index in a main-memory column-store
* understand the implications of using an index on the rest of the system
* understand the trade-offs of primary and secondary indices
* explore other indexing schemes for different workloads

### Tasks

Students will accomplish the following tasks, in order of priority:

1. Implement a B+ tree
2. Implement a primary and secondary indices using this B+ tree for a `Table`
3. Explore and implement one alternative avenue of indexing a database 
4. Write a one-page summary of optimizations performed on the B+ tree (if any)
   and results of item 3 above

### Grading

Students will be graded in the following way:

1. B: A baseline grade of for getting correct results
2. B+: Minimal working implementation of a B+ tree (pun intended)
3. A-: Implementing primary index for a `Table` and at least one secondary index
4. A: Exploration and implementation of one alternative avenue of indexing

**An important note**: If you submit code that doesnt pass the same tests that
passed when you first got the code, we'll give you an **automatic C**.
This means we should be able to run the table tests without it failing (it does this as installed, so you don't need to worry about first making tests pass). 

## Code

### Summary of files

Although it seems like there's alot of code, most of it is testing code!  The
goal of the code base is to keep things small and streamlined. This allows you
the student to go bazinga(s) over modifications and optimizations without
fear that you shouldn't modify some file.

The files you can (and likely should modify) are the following:
* `Main.java`: contains the main entry point to the code
* `BPlusTree.java`: contains the B+ tree skeleton class. This compiles but
doesn't work! You'll need to fill in the functions. We suggest some functions
for you to make.
* `Column.java`: contains the Column class. You'll modify this to include your
indexing mechanism after you've built your B+ tree.
* `Table.java`: contains the Table class. Defines the interfaces that allow you
to perform range queries on the table class. You'll have to change the
implementation of these methods to account for any changes you've made in the
other files.

You can make any changes to the files above so long as you follow the
instructions on what you are able to change and not. You can also make new Java
files that contain your own code.

The following files don't need to be changed but you should know what they are
used for. It might be helpful to change some of them but it wouldnt be
necessary.
* `Tuple.java`: contains the definition of a Tuple. This is the logical Tuple
outside of the table. The Table takes an instance of a Tuple and stores the
information in the Tuple, not necessarily using the instance of the Tuple.
* `TupleIDSet.java`: contains the definition of a set of Tuple IDS (Integers).
This is what is returned from a filter predicate, for example, the Tuple IDs
where attribute A is between some value x and y.
* `Filter.java`: contains the definition for a filter predicate. It's passed
into the `Table.filter()` method so that the method can execute the code.
* `MaterializedResults.java`: contains the definition for the
MaterializedResults class. An instance of this is returned when you want to get
the tuples from a set of tuple IDs.

The following files deal with tests. Do not edit these files!:
* `Benchmarks.java`: Contains benchmarking code where we measure the query
response time of your Table.
* `BPlusTreeTests.java`: Tests the correctness of your B+ tree. It puts and
removes values from your B+ tree. It also checks the internal node structure of
your B+ tree using the `INodeValidator` class defined in this file.
* `TableTests.java`: Checks that the table's functions are return the right
values.

### What works and what doesn't?

We've written the table so that it works at a very fundamental level. You can
confirm this by running `make testtable` as described below. To ensure
correctness you should modify the code while ensuring the Table's tests stay
correct.

We've given you skeleton code for the B+ tree and so this definitely doesn't
work.

## Building and Running

There are two mechanisms with which to compile and run the code: through java explicitly, or with a makefile. If you're working with windows or on OSX without make installed, use the java instructions. Otherwise, feel free to use either depending on what you're more comfortable with. 

## With regular Java


To compile the code, from a terminal in your project directory run 
```bash
javac *.java
```

To run tests on the code, do either

```bash
java -ea Main -testtable
```
or 
```bash
java -ea Main -testtable
```
depending on whether you want to test the table or the tree. After running testtable, you can run `data_validation/compare_csv.py` to validate the results (you'll need python3 installed to do this).

To run the benchmark, use
```bash
java -ea Main -bench
```

To run your own code (defined in `yourCode()` in `Main.java`) run
```bash
java -ea Main -c
```


## With a Makefile

To compile the code:

```bash
make
```

We included some initial test data in your repo so you don't need to run this.
However, if you need to generate the test data, you'll need Python3 and Pandas
and run:

```bash
make data
```

To run the tests the code:

```bash
make testtable
```

```bash
make testtree
```

Expected testtable output can be found in `data_validation/expected`

We also include a utility with which to test the performance of your b-tree and other indexing mechanism. To see the average times for operations, run the benchmarks. 

To run the benchmarks:

```bash
make bench
```

To clean up class files:

```bash
make clean
```

## Tests for Correctness

### B+ Tree

You can see the tests for correctness in `BPlusTreeTests.java`. If you run this,
these tests will fail. You'll need to define your B+ tree first! It does the
following checks:

1. Inserts data into the tree
2. Most of the data from the tree
3. Inserts and removes data from the tree for some ratio
4. Checks internal nodes for children relationships. Note, that this is a very
   purposely loose check because there are many ways of writing a B+ tree.

If the tree performed correctly, you'll see something similar to the following:

```bash
$ make testtree
Seed for random generator: 0
Successfully inserted: 1000 items
Successfully got all 1000 expected values
Successfully deleted: 500 items with 500 items remaining
Successfully written 806 and deleted 194 and 1000 operations with writeRatio 80
Successfully validated tree
```
Note that the line "Successfully validated tree" must be present for the tree to be valid. It is possible all  other parts are present but there is some internal inconsistency that causes the tree to be invalid.

### Table Tests

You can see the tests for correctness for the Table in the `TableTests.java`. To
test for correctness, it reads the file `data` we gave you in the
`data\_validation` directory. It writes results to the
`data\_validation/results` directory. It then calls a python script to compare
your results to the data in `data\_validation/expected` directory.

It checks to make sure your implementation is correct for the following table
methods:
* `Table.insert`
* `Table.load`
* `Table.filter`
* `Table.update`
* `Table.delete`

In doing so, it also ensures that method for `Table.materialize` works
correctly.

### Benchmarking

Arguably equivalent in importance to correctness is performance. Indices are
built to ensure performant databases. As such, we provide a benchmarking
framework to estimate how fast your implementations run. You can run these
benchmarks by running `make bench` or `java -ea Main -bench`

You can find the benchmarks in the file `benchmark_results.txt`.

## An implementation overview

The code implements a simple column-oriented `Table` supporting only the Integer
data type. This `Table` supports a few essential database operations: inserts,
deletes, filters, and updates. In addition, the table supports a new operator
you might not have seen called "materialize". These are discussed below.

### Tuples
`Tuple.java` contains the code for a `Tuple`. You'll see that the `Tuple` class
is simply a hashtable where the key is a String, and the value is an Integer.
The key is the attribute, and the value is well, the value of that attribute.
For example, the code below inserts an instance of a `Tuple` into the table
`someTable`. This `Tuple` has two attributes: "A" = 1, and "B" = 2.

```Java
Tuple t = new Tuple();
t.put("A", 1);
t.put("B", 2);
some`Table`.insert(t1);
```

### Columns
`Columns.java` contains the code for the *physical representation* for the
*logical attribute* in a `Table`. Because the `Table` is a column-oriented data
storage scheme, the set of values of a given attribute is stored as an array in
contiguous memory. You'll see that this column is simply an `Vector` of
Integers.

### Tables
`Table.java` contains a bulk of the initial code. It has several internal
variables:

* `String name` is the name of the `Table`. It's not used much but there anyways
for future use.
* `Hashtable<String, Column> attributes` is the collection of columns and the
attribute name corresponding to those columns.
* `Vector<Boolean> valid` is an array that indicates whether a tuple is valid
or not (for example, when the tuple is deleted)

Internally, the column-oriented storage allows the `Table` to use the position
of a tuple in its column as the tuple's id. In the code below, `Tuple` t1 will
be referenced internally with id 0, t2 with id 1, and t3 with id 2. `Tuple` t2's
value for attribute "A" can be accessed in the Column corresponding to attribute
A equivalent to `columnA[0]`.

```Java

// Instantiate a new table with two attribtues "A" and "B".
HashSet<String> cols = new HashSet<String>();
cols.add("A");
cols.add("B");
Table someTable = new Table("test_table", cols);

// Insert tuples into the Table
Tuple t1 = new Tuple();
t1.put("A", 1);
t1.put("B", 2);
someTable.insert(t1);

Tuple t2 = new Tuple();
t2.put("A", 1);
t2.put("B", 3);
someTable.insert(t2);

Tuple t3 = new Tuple();
t3.put("A", 3);
t3.put("B", 1);
someTable.insert(t3);
```

The `Table` class provides several methods we describe below.

**insert**: Inserts a `Tuple` into the table by appending the values of the
tuple to each of the columns. Internally marks that tuple as valid by also
pushing `true` into the `valid` variable.

**delete**: Given a set of `Tuple`s' IDs, marks the `Tuple`s' as invalid by
setting the `valid` variable for that id as `false`. *Note:* this extremely
naive approach is done on purpose. At some point, there are performance
implications to not garbage collecting or physically deleting these entries.

**filter**: Given a `Filter`, returns the `HashSet` of valid tuple IDs that
qualify. See the description of a `Filter` later in the document.

**update**: Given a a set of `Tuple`s' IDs, an attribute, and value, updates the
`Tuple`s attribute to the given value.

**materializeResults**: Given a set of attributes and a set of valid tuple ids
(`Integer`s), returns a `MaterializedResults` of the attributes and tuples
requested. This is particularly necessary for column stores because a `Tuple`'s
values are stored separately in columns. This operation is also quite expensive
and is typically only used as the very last step in the query plan. This
technique is called *late tuple materialization*. We describe a
`MaterializedView` later in the document.

**load**: Given a list of `Tuple`s, load all of these tuples into the Table.

**Tip**: the `load` method currently calls `insert` each time. This *might* be
an issue when you are building an index...

### Other stuff relating to Tuples, Columns, and Tables

**Filter**: A recursive data structure. At the bottom of the recursion, a Filter
contains an `attribute`, a `low`, and a `high` value. If the low and high values
are present, then it's a range filter `low <= x <= high`. Note that if `low =
high`, then this is equivalent to an equality filter, it's an equality filter.
If `high` is null and `low` is present, then this is a query is equivalent to
`x >= low`. If vice versa, then it's `x <= high`.

**MaterializedResults**: This is a very lightweight wrapper around
`Vector<Tuple>`. To generate this list you'll need to iterate over the columns
in the `Table` and put together the tuples as previously descibed.

### Indexes and B+ Tree

The B+ tree code in `BPlusTree.java` currently stands alone and is unused
anywhere else besides the tests. After completing the B+ tree code, you're going
to need to write code to use the B+ tree as an index for columns in your table.

#### Other indexes

At the end of the project, you're going to experiment with one of two things:

1. An significant optimization to your B+ tree. Note that this should
   fundamentally change how your B+ tree works. Minor optimizations such as
   changing branching factor doesn't count!

2. An alternative indexing scheme. We'll be discussing this as we progress in
   class. Some options include LSM Trees, Bitmap Indexes, HashMaps. In all
   cases, you DON'T need to implement these yourself - you can use anything
   provided in the Java standard library. You're simply going to need to write
   code on how to use these data structures as an index.

   For example, if you use a HashMap, Java has a bunch of HashMap
   implementations. You can choose one and write code to use it as an index for
   your column or table.

## B+ Tree Help!

B+ trees have two kinds of nodes: `Internal` and `Leaf`. Both nodes need an
array of keys, the number of children it has, optionally, a reference to its
parent. Bot nodes also need to search, split, insert, delete, and get. They
implement these differently.

### LNode

#### Structure

```
Leaf Node:

    degree          // degree of ths node
    keys            // array of keys in the leaf
    values          // array of values in the leaf
    num_children    // number of children
    right_sibling   // instead of keeping the last value as a pointer
```

An interesting thing to note is that we can keep track of the right or left
sibling separately from the values. This means the the contents of the values
array is always the same number as the keys array. Here's a way of visualizing
it for degree=4:

```
"canonical" way:

 keys
 +-----+
 |a|b|c|
 +-------+
 |1|2|3| +---> next leaf
 +-------+
 values

how we'll implement it

 keys
 +-----+
 |a|b|c|
 +-----+  +-+
 |1|2|3|  | +--->next leaf
 +-----+  +-+
 values

```
This change makes leaf logic much easier to deal with and verify.

#### Initialization

Alot of implementations split **before** insert (when full). We split **after**
we insert when full. This ensures there is always room for speedy inserts but
this doesn't really matter in the end. So we need to initialize the leaf so that
we can insert past "full" **then** split:

```
Initialize Leaf:
    degree          <= some number
    keys            <= empty array with space for degree entries
    values          <= empty array with space for degree entries
    num_children    <= 0
    right_sibling   <= null
```

Note that instead of degree-1 keys and values, we allow degree keys and values.
when the number of entries exceeds degree, we split.

#### Search and Mid

We assume that the search returns the first index in keys where the key is >=
the search key:

```
search(k):
    return first index in keys >= k or num_children if k > all keys
```

For splitting nodes, we need to find the middle of the node. This is simply:

```
mid:
    return degree/2 (integer division)
```

#### Insert

Inserting returns a `Split` if this insert resulted in the node splitting.

```
insert(k, v) -> Split:

    index <= search for key in keys by binary search,

    if index = num_children, k > all keys in node:
        put k in keys[index], v in values[index], increment num_children

    else if we found k in keys[index], then set values[index] = v

    otherwise we need to insert k, v in the middle of the node:
        shift keys and values array 1 position down starting from the the index
        position.

        put k in keys[index], v in values[index], increment num_children

    // now check if we need to split
    if num_children = degree, this node splits and returns a SplitResult
    otherwise returns null

```

#### Split

We use a `SplitResult` to encapsulate the result of the splits. This makes it
easy to "promote" a key. We also always split rightward.

here's a visualization of the split:
```
                                    key
keys                       left     +-+  right
+-----+                    +-----+  |b|  +-----+
|a|b|c|                    |a| | |  +-+  |b|c| |
+-----+  +-+               +-----+ +-+   +-----+
|1|2|3|  | +--->next leaf  |1| | | | +-->+2|3| |
+-----+  +-+               +-----+ +-+   +-----+
values

```

Note that we promote the smallest value of the right node! Also note that this
is the only time the keys are duplicated - between leaf and one internal node.

```
SplitResult:
    left node       // the left node resulting from the split
    right node      // the right node resulting from the split
    key             // the key that determines the split (promoted value)


LeafSplit -> SplitResult:

    right <= make a new leaf node with same degree

    copy keys array to the beginning of the right node starting from mid().
    copy values array to the beginning of the right node starting from mid().

    right's num_children <= the number of items you moved to the right node
    num_children <= the number of items remaining in this node

    connect this node and the right node's reference

    returns
    Split:
        left node <= this node
        right node <= right
        key <= mid()

```

#### Delete

We mentioned that you don't need to implement the merging rules when deleting
from the tree. Here's some psuedocode on how to do just that!

```
delete(k):
    index <= search(k)

    if keys[index] != k or index == num_children:
        then k is not in the node, nothing else to do, return

    shift keys array from keys[index + 1] backward to keys[index]
    shift values array from keys[index + 1] backward to keys[index]
    decrement num_children

```

### Sidebar: The first root

The Tree starts with a root node that is an empty leaf. So the insert into the
tree first inserts into a root that is a leaf, then the root needs to change to
an internal node when there is a split.

```
tree insert(k, v):
    root_split <= root.insert(k, v)
    if root_split is not null:
        new root is a new internal node with degree and whos children are the
        left and right nodes defined in a Split structure.
```

### Internal Nodes

#### Structure

```
Internal Node:
    degree          // degree of ths node
    keys            // array of keys in the intenral node
    children        // array of reference to children
    num_children    // number of children
```

Here, the structure is as expected for order = 4

```
 keys
  +-----+
  |k|k|k|
 +-------+
 |c|c|c|c+ note: each item here is a reference to a child node
 +-------+
 values
```

An important thing to note is that the values at children[i] are < keys[i], and
the values at children[i+1] >= keys[i].

#### Initialization

The internal node is largely similar to the leaf node but instead of a values
array, we have a children array. An internal node always only begins to exist
when a split occurs so the only way to initialize an internal node is by using
a SplitResult.

A naive implementation might give the keys array degree-1 entries, and the
children array degree entries. However, Similar to the Leaf, we split **after**
inserting past full so we need room for this to happen. So we actually give keys
array degree entries, and children array degree + 1 entries.

```
Initialize Internal Node(degree, split):

    children <= empty array of references to children of size degree + 1
    keys <= empty array of keys of size degree

    this node only exists because of split so:
    children[0] = split.left
    children[1] = split.right
    keys[0] = split.key

    num_children = 2

```

#### Search and mid

The mid function is the same as the leaf node. The search in an internal node
is a little bit different to help us with fact that the number of keys is now
one less than the number of children

```
search(k):
    return index of first key >= k. If you reach index = num_children - 1,
    you've seen all the valid keys so return num_children - 1.
```

It's important to remember that this search function returns only to the index
to the keys array. So keep the following things in mind:

* The search function we have never refers to a position in the children array.
It always refers to a position in the keys array
* The index returned by our search returns the index in the keys array that is
the first index >= value being searched for, or num_children - 1.
* So why num_children - 1? If there are 3 children, there must be 2 keys. The
two keys in the array have indices 0 and 1, so we return 2 - the index after the
last valid key.

Here's a maybe helpful graphic:

```
                +---+
                |3|7|
 +------+       +---+       +-----+
 |      +-------+ | +-------+7|9  |
 +------+     +---+---+     +-----+
              |       |
              +-------+

search(7) => index = 1
7 = keys[1]
delete at child index+1 = 2

search(9) => num_children-1 = 3-1 = index=2
delete at child index = 2
```

#### Inserts

When we insert into an internal node, we simply continue inserting into the
children of the internal node. If the children split, the internal node might
also need to split, so it returns a SplitResult as well.

```
insert(k, v):
    index = search(k)
    split <= null

    here, we find that we are at the last key or we found the appropriate
    key that's > k. To see why, look again at the Structure section
    if index = num_children-1 or k < keys[index] insert into child at
        children[index]

    else if k = keys[index], insert into children[index + 1]

    if either of the above does not return a split, return null,
    otherwise, we need to insert the split children into this node.

```

#### Inserting Split Children

When the children of an internal node splits, you need to insert the new child
into this internal node. If this insertion results in this node splitting, then
the splitting recurses upward by returning another SplitResult

```
insert\_split(split to\_insert):
    index <= search(to_insert.key)

    if index = num_children-1 then we can put it at the end of the keys and
        children arrays. Remember there are 1 fewer keys than children!:
        keys[index] = to_insert.key
        children[index+1] = to_insert.right

    otherwise it's somewhere in the middle so:
        shift keys down from index
        shift children down from index+1
        set key[index] and children[index+1] to to_insert.key and
        to_insert.right

    increment num_children

    if we now exceed degree (num_children > degree), then this node should
        split, then return the SplitResult

    otherwise return null
```

#### Split

The splitting of this node follows roughly what the leaf node does but we have
to consider what happens when the degree is odd or even. We don't
consider this here but this should get you started. Finally, the promotion works
in the same way. Here's a visualization of the split:

```
 keys                  left       key    right
  +-----+              +-----+    +-+    +-----+
  |a|b|c|    split     |a| | |    |b|    |c| | |
 +-------+  ------>   +-------+   +-+   +-------+
 |1|2|3|4|            |1|2| | |         |3|4| | |
 +-------+            +-----+-+         +-----+-+
 values
```

```
split:
    right <= new internal node of the same degree

    remember that there are n children, and n-1 keys. if you are splitting, this
    means n = degree + 1
    copy keys from this node to the right node starting from mid()
    copy children from this node to the right node starting from mid()

    right.num_children <= to be the number of children moved
    num_children <= mid()

    the key that determined the split is at mid - 1 so:
    return SplitResult(keys[mid-1], this node, and right node)

```

#### Deletes

Again, we don't require you to merge nodes on delete so all you need to do is to
to traverse the tree and mark that as deleted

```
delete(k):
    index = search(k)

    here, we find that we are at the last key or we found the appropriate
    key that's > k. To see why, look again at the Structure section
    if index = num_children-1 or k < keys[index] then delete k in
        children[index]

    else if k = keys[index], delete k in children[index + 1]

```

### Sidebar: Internal Nodes can have children that are leaves or internal nodes

For this reason, we define the `Node` abstract class in the BPlusTree.java file.
It allows the Internal Node and the tree's root node to be either an Internal
Node or a Leaf Node.

Most of the functions you need or may want to define are in this class. Read the
comments of the class to help you figure this out.




