Project Requirements:
Java 8 or higher

Project Overview:
This project involved the creation and binding of a BPlus Tree indexing scheme to a column store database. The logic for BPlusTree creation was added into the BPusTree.java file, and logic for binding to the table itself is included in Table.java. A detailed overview of the project is given in the Project Overview file

Running Instructions:
```bash
make testtree 
```

runs a test suite of selected test cases to ensure the correctness of the BPlusTree

```bash
make testtable 
```

ensures that the BPlusTree has been correctly bound to the column store database

```bash
make bench 
```
provides a benchmark comparison of insertion, deletion, and selection times when using selection on the clustered/nonclustered BPlusTree attribrutes against the unindexed attributes.

If using Windows, use the following terminal command line instructions:

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