/**
 * The Sensor is a thread and will start working when the system starting
 * It has one task in the Solution Two
 * 1.Identify if a bicycle has a tag 
 */
public class Sensor extends Thread {
	
	// the current running belt
	protected Belt belt;
	// the bicycle to be sensed
	protected volatile Bicycle toBeSensedBicycle;
	// the sensing result
	protected boolean sensingResult = false;
	
	
	/**
	 * Create a sensor 
	 * @param belt : belt that sensor located
	 */
	public Sensor(Belt belt){
		this.belt = belt;
	}
	
	/**
	 * Sense the bicycle and notify the belt when the sensing finished
	 */
	public void Sense(){
		// Get the bicycle that needs to be sensed from the belt
		getBicycle();
		if (this.toBeSensedBicycle!=null){
				// Sense the bicycle
				senseBicycle(this.toBeSensedBicycle);
				// Notify the belt that the sensing is finished
				belt.setFinishSensing(true);
		}
	}
	
	
	/**
	 * This function will get the bicycle that needs to be sensed
	 */
	public void getBicycle(){
		this.toBeSensedBicycle = belt.peek(Params.SEN_LOC-1);
	}
	
	/**
	 * Sense the bicycle and set the result
	 * @param bicycle
	 */
	public void senseBicycle(Bicycle bicycle){
		
		if (bicycle.isTagged()){
			this.sensingResult = true;
		}else{
			this.sensingResult = false;
		}
    }
	
	/**
	 * sensor run method
	 */
	public void run(){
		while (!isInterrupted()) {
            Sense();
		}
   }	
	
}


