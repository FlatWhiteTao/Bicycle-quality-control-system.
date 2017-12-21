/**
 * 
 * The Inspector is a thread and has two main tasks
 * 1. Inspect bicycle
 * 2. Request an empty segment three and if so, occupies robot to deliver back 
 *
 */

public class Inspector extends Thread {
	
	// the bicycle under inspection
	protected Bicycle inspectingBicycle = null;
	
	// communicating components
	protected Robot robot;
	protected Sensor sensor;
	
	
	/**
	 * Create a inspector 
	 *  
	 */
	public Inspector(){
		
	}
	
	/**
	 * Set up interactions between inspector robot and inspector sensor
	 * @param robot
	 * @param sensor
	 */
	public void setupInteraction(Robot robot, Sensor sensor){
		this.robot = robot;
		this.sensor = sensor;
	}
	
	
	/**
	 * Inspect Bicycle
	 * @throws InterruptedException
	 */
	private void inspectBicycle() throws InterruptedException{
		    
			System.out.println(inspectingBicycle+ " the inspection starts");
			// let the inspection time pass...
			Thread.sleep(Params.INSPECT_TIME);
			
	        // Get the inspection outcome and print it out
			if(inspectingBicycle.isDefective()){
				// if the bicycle is a real defective one
				System.out.println(inspectingBicycle+ " has been inspected and it is defective");
				System.out.println(inspectingBicycle+ " waits to be sent back to the belt");
				// when the inspection finishes, inspector requests the sensor to find an empty segment
				sensor.requestEmpty(true);
			}else{
				// if the bicycle is not defective then remove the tag
				System.out.println(inspectingBicycle+ " has been inspected and it is not defective");
				// remove the tag
				inspectingBicycle.setNotTagged();
				System.out.println(inspectingBicycle+ " the tag is removed");
				System.out.println(inspectingBicycle+ " waits to be sent back to the belt");
				// when the inspection finishes, inspector requests the sensor to find an empty segment
				sensor.requestEmpty(true);
			}
			
			while(sensor.toBeSensedBicycle!=null){
				
			}
			// Captures the empty segment three and call robot to return the inspected bicycle
			robot.occupyRobot("Inspector",this.inspectingBicycle);
			// reset the inspecting bicycle to be null
			this.inspectingBicycle = null;
			sensor.requestEmpty(false);
	}
	
	
	/**
	 * Get the bicycle from the robot
	 * @param bicycle
	 * @throws InterruptedException
	 */
	public void getBicycle(Bicycle bicycle) throws InterruptedException{
		
		// get bicycle from the robot arm and put to the workshop
		this.inspectingBicycle = bicycle; 
		
	}
	
	/**
	 * the run method of inspector thread
	 */
    public void run(){
		
		while (!isInterrupted()) {
	        
        	if(inspectingBicycle!=null){
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