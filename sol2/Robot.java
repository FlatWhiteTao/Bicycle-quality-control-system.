/**
 * 
 * The Robot is a thread and will start working when the system starting 
 * It has two main tasks:
 * 1.Deliver the tagged bicycle form the main belt to the inspector
 * 2.Deliver the inspected bicycle from the inspector to the shorter belt
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
	protected Belt shorterBelt;
	protected Inspector inspector;
	
	final private static String indentation = "                  ";
	
	/**
	 * Create a robot and communicate with belt, sensor and inspector 
	 * @param belt 
	 * @param sensor
	 * @param inspector
	 */
	public Robot (Belt belt, Belt shorterBelt){
		this.belt = belt;
		this.shorterBelt = shorterBelt;
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
	 * Release the robot
	 */
	public synchronized void releaseRobot(){
		
			this.roboticArm = null;
			this.occupant = null;
			this.isOccupied = false;
			notifyAll();
	}
	
	/**
	 * Robot pick up bicycle and deliver 
	 * @throws InterruptedException
	 * @throws OverloadException 
	 */
	public synchronized void Delivery() throws InterruptedException, OverloadException{
		
		
		// if the occupant is the main belt

		if(this.occupant == "MainBelt"){
			//deliver to the inspector
			deliverToInspector();
		}
		// if the  occupant is the inspector
		
		if(this.occupant == "Inspector"){
			//deliver to the belt
			
			deliverToShorterBelt();
		}
	}
	
	/**
	 * Robot delivers bicycle from the main belt to the inspector
	 * @throws InterruptedException
	 */
	
	public void deliverToInspector() throws InterruptedException {
		
		//print the event trace
	    System.out.println(
        		indentation +
        		this.roboticArm +
                " [ Robot -> Inspector]");
	    
		Thread.sleep(Params.ROBOT_MOVE_TIME);
		
	    inspector.getBicycle(this.roboticArm);
	    
		
		
	}
	
	/**
	 * Robot delivers bicycle from the inspector to shorter belt
	 * @throws InterruptedException
	 * @throws OverloadException 
	 */
	public void deliverToShorterBelt() throws InterruptedException, OverloadException{
		
		Thread.sleep(Params.ROBOT_MOVE_TIME);
		// put inspected bicycle to the segment 1 of the shorter belt
		shorterBelt.put(this.roboticArm, 0);
	
	}
	
	/**
	 * run method for inspector thread
	 */
	public void run(){
		while (!isInterrupted()) {
            try {
            	try {
            		if(this.isOccupied){
                		Delivery(); 
                	}

				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} 
            catch (InterruptedException e) 
                {
            	e.printStackTrace();
		   }
		}
	}
}
