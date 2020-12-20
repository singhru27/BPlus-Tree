import java.util.*;

class Main {

    // Enter your banner ID here
    public static String bannerid = "B01437507";

    // Write your test code in this method!
    void yourCode() throws Exception {
        BPlusTree tree = new BPlusTree(3);
        tree.insert(5, 5);
        tree.insert(1, 1);
        tree.insert(3, 3);
        tree.insert(6, 6);
        tree.insert(7, 7);
        tree.insert(-5, -5);
        tree.insert(-2, -2);
        tree.insert(0, 0);

        TupleIDSet newTuples = tree.getRange(-1000, 500);
        System.out.println(newTuples);

    }

    // DONT CHANGE BELOW THIS LINE
    public static void main(String args[]) throws Exception {
        Main prog = new Main();

        if (args.length != 1) {
            throw new Exception("invalid arguments");
        }

        if (args[0].equals("-bench")) {
            prog.benchmark();
        } else if (args[0].equals("-testtable")) {
            prog.test();
        } else if (args[0].equals("-testtree")) {
            prog.testTree();
        } else if (args[0].equals("-c")) {
            prog.yourCode();
        } else {
            throw new Exception("invalid arguments");
        }
    }

    void test() throws Exception {
        TableTests test = new TableTests();
        test.testLoad();
        test.testBetween();
        test.testGT();
        test.testLT();
        test.testComposite();
        test.testDelete();
        test.testUpdate();
    }

    void testTree() throws Exception {
        BPlusTreeTests test = new BPlusTreeTests(20, 0);
        test.testInsert(1000);
        test.testGet();
        test.testDelete(1000);
        test.testMix(1000, 80);
        test.testValidate();
    }

    void benchmark() throws Exception {
        Benchmarks bench = new Benchmarks();
        bench.insertBenchmark();
        bench.deleteBenchmark();
//        bench.updateBenchmark("A"); // indexing the primary column
//        bench.updateBenchmark("B"); // indexing the secondary column
//        bench.updateBenchmark("C"); // indexing the unindexed column
        bench.filterBenchmark();
        bench.filterClusteredIndexBenchmark();
        bench.filterSecondaryIndexBenchmark();
        bench.finish();
    }
}
