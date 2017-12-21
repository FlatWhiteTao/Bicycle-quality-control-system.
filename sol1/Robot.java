/**
 * 
 * The Robot is a thread and will start working when the system starting 
 * It has two main tasks:
 * 1.Deliver the tagged bicycle form the belt to the inspector
 * 2.Deliver the inspected bicycle from the inspector to the belt
 *
 */
public class Robot extends Thread {
	
	// The robotic arm that stores the bicycle during the delivery 
	protected Bicycle roboticArm = null;
	// the semaphore indicates if the robot is occupied or not
	protected boolean isOccupied = false;
	// the semaphore indicates which component currently occupies the robot
	protected String occupant;
	
	// The components that robot will interact with	
	protected Belt belt;
	//protected Sensor sensor;
	protected Inspector inspector;
	
	final private static String indentation = "                  ";
	
	/**
	 * Create a robot and communicate with belt, sensor and inspector 
	 * @param belt 
	 * @param sensor
	 * @param inspector
	 */
	public Robot (Belt belt){
		this.belt = belt;
	}
	/**
	 * Set up interaction components
	 * @param inspector : the interaction component
	 */
	public void setupInteraction(Inspector inspector){
		this.inspector = inspector;
	}
	
	
	/**
	 * Occupy robot and set occupant information
	 * @param occupant : the component that occupies the robot
	 * @param bicycle : the bicycle that will be stored on the robotic arm temporarily
	 * @throws InterruptedException
	 */
	public synchronized void occupyRobot(String occupant, Bicycle bicycle) throws InterruptedException {
			// Set occupant information
			this.occupant = occupant;
			this.isOccupied = true;
			this.roboticArm = bicycle;
			notifyAll();
	}
	
	/**
	 * Robot will take different delivery actions based on the different occupants  
	 * @throws InterruptedException
	 */
	public synchronized void Delivery() throws InterruptedException{
		
		// if the occupant is the belt
		if(this.occupant == "Belt"){
			// the robot will take actions to deliver the bicycle to the inspector
			deliverToInspector();
		}
		// if the occupant is the inspector
		if(this.occupant == "Inspector"){
			//the robot will take actions to deliver the bicycle back the belt
			deliverToBelt();
		}
	}
	
	/**
	 * Robot take actions to deliver bicycle to the inspector
	 * @throws InterruptedException
	 */
	
	public void deliverToInspector() throws InterruptedException {
		
		// robot will wait until the inspector is empty
	    while (inspector.inspectingBicycle!=null){
	    	
	    }
	    // print trace
	    System.out.println(
        		indentation +
        		roboticArm +
                " [ Robot -> Inspector]");
	    
	    // pass delivering time
		Thread.sleep(Params.ROBOT_MOVE_TIME);
		// Notify the inspector to get the bicycle 
		inspector.getBicycle(roboticArm);
	    // Reset the robot's resource
	    releaseRobot();
	}
	
	/**
	 * Robot take actions to deliver bicycle back the belt
	 * @throws InterruptedException
	 */
	public void deliverToBelt() throws InterruptedException{
		
		// check if segment is empty 
		if(belt.peek(Params.SEN_LOC-1)==null){
			// if empty, robot deliver back the inspected bicycle, otherwise, keep waitting
			Thread.sleep(Params.ROBOT_MOVE_TIME);
			belt.put(this.roboticArm, Params.SEN_LOC-1);
			releaseRobot();
			belt.resumeBelt();
		}
		
	}
	
	/**
	 * Release the robot
	 */
	public synchronized void releaseRobot(){
		
			this.roboticArm = null;
			this.occupant = null;
			this.isOccupied = false;
			notifyAll();
	}
	
	/**
	 * run method for inspector thread
	 */
	public void run(){
		while (!isInterrupted()) {
            try {
            	if(this.isOccupied){
            		Delivery(); 
            	}
            } 
            catch (InterruptedException e) 
                {
            	e.printStackTrace();
		   }
		}
	}
}
