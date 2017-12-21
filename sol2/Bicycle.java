import java.util.Random;

/**
 * A class representing a bicycle moving through quality control
 */
public class Bicycle {
    protected static Random r = new Random();

    // specifies whether the bicycle needs checking
    protected boolean tagged = false;

    // specifies whether the bicycle is defective
    protected boolean defective = false;

    // the ID of this bicycle
    protected int id;

    // the next ID that can be allocated
    protected static int nextId = 1;

    // create a new bicycle with a given ID
    private Bicycle(int id) {
        this.id = id;
        if (r.nextFloat() < Params.TAG_PROB) {
            tagged = true;
            if (r.nextFloat() < Params.DEFECT_PROB) {
            	defective = true;
            }
        }
    }

    /**
     * Get a new Bicycle instance with its unique ID
     */
    public static Bicycle getInstance() {
        return new Bicycle(nextId++);
    }

    /**
     * @return the ID of this bicycle
     */
    public int getId() {
        return id;
    }

    /**
     * Mark this bicycle as defective
     */
    public void setDefective() {
        defective = true;
    }

    /**
     * @return true if and only if this bicycle is marked as tagged
     */
    public boolean isTagged() {
        return tagged;
    }

    /**
     * Mark this bicycle as tagged
     */
    public void setTagged() {
        tagged = true;
    }

    /**
     * Remove this bicycle's tag
     */
    public void setNotTagged() {
        tagged = false;
    }
    
    /**
     * @return true if and only if this bicycle is marked as defective
     */
    public boolean isDefective() {
        return defective;
    }

    public String toString() {
    	String tFlag = tagged ? "t" : "-";
    	String dFlag = defective ? "d" : "-";
        return "B:" + String.format("%03d", id) + "(" + tFlag + dFlag + ")";
    }
}
