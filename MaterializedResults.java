import java.util.*;

// Converts list of tuples to a string representation
public class MaterializedResults  extends Vector<Tuple> {
    @Override
    public String toString() {
        String v = "";
        for (Tuple tuple : this) {
            v = v + tuple.toString() + "\n";
        }
        return v;
    }
}
