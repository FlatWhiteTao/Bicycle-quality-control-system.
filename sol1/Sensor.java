/**
 * 
 * The Sensor is a thread and will start working when the system starting
 * It has two main tasks in the Solution one
 * 1.Identify if a bicycle has a tag 
 * 2.Check if the segment three of the belt is empty
 *   
 */
public class Sensor extends Thread {

	    // the current running belt
		protected Belt belt;
        // the bicycle to be sensed
        protected volatile Bicycle toBeSensedBicycle;
		// the sensing result
		protected boolean sensingResult = false;
		// the semaphore that indicates if the inspector has requested for a empty belt segment three
		protected boolean emptyRequest = false;
		
		
		/**
		 * Constructor of sensor 
		 * @param belt : the running belt
		 */
		public Sensor(Belt belt){
			this.belt = belt;
		}
		
		
		/**
		 * The main function of the sensor which aims to achieve:
		 * 1.Check if the bicycle has a tag or not
		 * 2.Check if the belt's segment three is empty when inspector sends an empty request
		 */
		public void Sense(){
			
			// Get the bicycle that needs to be sensed from the belt
			getBicycle();
			
			// Check if the bicycle has a tag or not
			if (this.toBeSensedBicycle!=null){
				// Sense the bicycle
				senseBicycle(this.toBeSensedBicycle);
				// Notify the belt that the sensing is finished
				belt.setFinishSensing(true);
			}
			
			if(emptyRequest){
				// if sensor got an empty request
				if(this.toBeSensedBicycle!=null){
					// if the current segment three is taken, move the belt
					belt.resumeBelt();
				}else{
					// if the current segment three is empty, pause the belt
					belt.pauseBelt();
				}
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
		 * Listen the empty request 
		 * @param requestForEmpty
		 */
		public synchronized void requestEmpty(boolean requestEmpty){
			this.emptyRequest = requestEmpty;
			notifyAll();
		}
		
		/**
		 * sensor run method
		 */
		public void run(){
		
			while (!isInterrupted()) {
	            // if get a signal that belt request sense	
				Sense();
			}
	   }	
		
}
