
/**
 * Parameters that influence the behaviour of the system 
 */

public class Params {

	// the maximum amount of time the producer waits
	public final static int PRODUCER_MAX_SLEEP = 3000;
	
	// the minimum amount of time the consumer waits
	public final static int CONSUMER_MIN_SLEEP = 500;
	
	// the maximum amount of time the consumer waits
	public final static int CONSUMER_MAX_SLEEP = 2800;
	
	// the amount of time it takes to move the belt
	public final static int BELT_MOVE_TIME = 900;
	
	// the amount of time it takes the robot to move a bicycle
	public final static int ROBOT_MOVE_TIME = 900;
	
	// the amount of time it takes to inspect a bicycle
	public final static int INSPECT_TIME = 5000;
		
	// probability that a bicycle is tagged
	public final static double TAG_PROB = 0.1;
	
	// probability that a tagged bicycle is defective
	public final static double DEFECT_PROB = 0.7;
	
	// the segment number of the placement of the sensor on the belt
	public final static int SEN_LOC = 3;
	
}
