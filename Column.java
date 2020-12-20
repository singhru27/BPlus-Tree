import java.util.*;

// DO NOT CHANGE THE COLUMN'S INHERITANCE STRUCTURE
public class Column extends Vector<Integer> {
	
	boolean isClustered;
	boolean isSecondary;
	
    public Column() {
        super();
        isClustered = false;
        isSecondary = false;
    }
    
    public void setClustered () {
    	isClustered = true;
    }
    
    public void setSecondary () {
    	isSecondary = true;
    }
    
    public boolean isClustered () {
    	return isClustered;
    }
    
    public boolean isSecondary () {
    	return isSecondary;
    }
}
