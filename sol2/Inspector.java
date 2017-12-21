/**
 * The Inspector is a thread and has two main tasks
 * 1. Inspect bicycle
 * 2. Request an empty segment one of the shorter belt and if so, occupies robot to deliver 
 */

public class Inspector extends Thread{
	
	// the bicycle under inspection
	protected Bicycle inspectingBicycle = null;
	
	
	// interaction components
	protected Robot robot;
	protected Belt shorterBelt;
	
	/**
	 * Create a inspector 
	 * 
	 */
	public Inspector(){
		
	}
	
	
	/**
	 * Set up interactions between inspector robot and shorter belt
	 * @param robot
	 * @param sensor
	 */
	public void setupInteraction(Robot robot, Belt shorterBelt){
		this.robot = robot;
		this.shorterBelt = shorterBelt;
	}
	
	
	/**
	 * Inspect Bicycle
	 * @throws InterruptedException
	 */
	private synchronized void inspectBicycle() throws InterruptedException{
		    
			System.out.println(inspectingBicycle+ " the inspection starts");
			//inspection time
			Thread.sleep(Params.INSPECT_TIME);
			
			// Get the inspection outcome and print it out
			if(inspectingBicycle.isDefective()){
				// if the bicycle is a real defective one
				System.out.println(inspectingBicycle+ " has been inspected and it is defective");
				System.out.println(inspectingBicycle+ " waits to be sent back to the shorter belt");
			}
			else{
				// if the bicycle is not defective then remove the tag
				System.out.println(inspectingBicycle+ " has been inspected and it is not defective");
				// remove the tag
				inspectingBicycle.setNotTagged();
				System.out.println(inspectingBicycle+ " the tag is removed");
				System.out.println(inspectingBicycle+ " waits to be sent back to the shorter belt");
			}
			
			readyToDeliver(inspectingBicycle);
	}
	
	/**
	 * Wait to invoke robot to send the inspected bicycle to the shorter belt
	 * @param bicycle : inspected bicycle that will be send to the shorter belt
	 * @throws InterruptedException
	 */
	public void readyToDeliver(Bicycle bicycle) throws InterruptedException{
		
		// Check if the first segment of shorter belt is empty			
		if(shorterBelt.segment[0] == null){
			// it is empty then occupies the robot 
			robot.occupyRobot("Inspector",bicycle);
		}else{
			//if not, then waits
			while(shorterBelt.segment[0]!=null){
					wait();
			}
			// until catch an empty one, occupy the robot 
			robot.occupyRobot("Inspector",bicycle);
		}
			// reset the inspecting bicycle to be null
			this.inspectingBicycle = null;
			notifyAll();
	}
	
	
	/**
	 * Get the bicycle from the robot
	 * @param bicycle
	 * @throws InterruptedException
	 */
	public void getBicycle(Bicycle bicycle) throws InterruptedException{
		
		// get bicycle from the robot arm and put to the workshop
		this.inspectingBicycle = bicycle; 
		// release the robot after delivering to the belt
	    robot.releaseRobot();
		
	}
	
	/**
	 * the run method of inspector
	 */
    public void run(){
		
		while (!isInterrupted()) {
	        
        	if(this.inspectingBicycle!=null){
        		// inspect the bicycle when the workshop is not null
				try {
					inspectBicycle();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }
    }

}
