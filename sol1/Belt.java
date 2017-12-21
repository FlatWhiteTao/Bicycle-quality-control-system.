/**
 * The bicycle quality control belt
 */
public class Belt {

    // the items in the belt segments
    protected Bicycle[] segment;

    // the length of this belt
    protected int beltLength = 5;
    
    // The semaphore that instructs belt to be paused and resumed
    protected boolean isPaused = false;
    // The semaphore that indicates that if the sensor finish sensing
    protected boolean finishSensing = false;
    
    // Two components that the belt will interact with
    protected Sensor sensor;
    protected Robot robot;

    // to help format output trace
    final private static String indentation = "                  ";

    /**
     * Create a new, empty belt, initialised as empty
     */
    public Belt() {
        segment = new Bicycle[beltLength];
        for (int i = 0; i < segment.length; i++) {
            segment[i] = null;
        }
    }
    
    /**
     * Set up the interactions between belt and sensor, belt and robot
     * @param sensor: interaction component
     * @param robot: interaction component
     */
    
    public void setupInteraction(Sensor sensor,Robot robot){
    	this.sensor = sensor;
    	this.robot = robot;
    }
   
    
    /**
     * Put a bicycle on the belt.
     * 
     * @param bicycle
     *            the bicycle to put onto the belt.
     * @param index
     *            the place to put the bicycle
     * @throws InterruptedException
     *            if the thread executing is interrupted.
     */
    public synchronized void put(Bicycle bicycle, int index)
            throws InterruptedException {

    	// while there is another bicycle in the way, block this thread
        while (segment[index] != null) {
            wait();
        }

        // insert the element at the specified location
        segment[index] = bicycle;
        
     
        if(index==Params.SEN_LOC-1){
        	segment[index].firstTimeSenseing = false;
        	System.out.println(
            		indentation +
            		bicycle +
                    " [inspector -> s" + (Params.SEN_LOC) +" ]");
        }
        // make a note of the event in output trace
        else {
        	System.out.println(bicycle + " arrived");
        }

        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    /**
     * Take a bicycle off the end of the belt
     * 
     * @return the removed bicycle
     * @throws InterruptedException
     *             if the thread executing is interrupted
     */
    public synchronized Bicycle getEndBelt() throws InterruptedException {

        Bicycle bicycle;

        // while there is no bicycle at the end of the belt, block this thread
        while (segment[segment.length-1] == null) {
            wait();
        }

        // get the next item
        bicycle = segment[segment.length-1];
        segment[segment.length-1] = null;

        // make a note of the event in output trace
        System.out.print(indentation + indentation);
        if (bicycle.isTagged()){
        	System.out.println(bicycle + " departed (Recycled)");
        }else{
        	System.out.println(bicycle + " departed (Shipped)");
        }
        

        // notify any waiting threads that the belt has changed
        notifyAll();
        return bicycle;
    }

    /**
     * Move the belt along one segment
     * 
     * @throws OverloadException
     *             if there is a bicycle at position beltLength.
     * @throws InterruptedException
     *             if the thread executing is interrupted.
     */
    public synchronized void move() 
            throws InterruptedException, OverloadException {
        // if there is something at the end of the belt, 
    	// or the belt is empty, do not move the belt
    	// or the belt receives the pause semaphore
        while (isEmpty() || segment[segment.length-1] != null || isPaused) {
            wait();
        }

        // double check that a bicycle cannot fall of the end
        if (segment[segment.length-1] != null) {
            String message = "Bicycle fell off end of " + " belt";
            throw new OverloadException(message);
        }

        // move the elements along, making position 0 null
        for (int i = segment.length-1; i > 0; i--) {
            if (this.segment[i-1] != null) {
            	
            	// if the current processing element is not in the sensor's location
            	if(i!=Params.SEN_LOC){
            		// just keep moving and print the trace
            		System.out.println(
                    		indentation +
                    		this.segment[i-1] +
                            " [ s" + (i) + " -> s" + (i+1) +" ]");
            	}else if (i==Params.SEN_LOC){
            		// if an element arrives on sensor's location, take further actions
            		processElement(Params.SEN_LOC); 
            	}
            	
            }
            segment[i] = segment[i-1];
        }
        segment[0] = null;

        
        // notify any waiting threads that the belt has changed
        notifyAll();
    }

    /**
     * @return the maximum size of this belt
     */
    public int length() {
        return beltLength;
    }

    /**
     * Peek at what is at a specified segment
     * 
     * @param index
     *            the index at which to peek
     * @return the bicycle in the segment (or null if the segment is empty)
     */
    public Bicycle peek(int index) {
        Bicycle result = null;
        if (index >= 0 && index < beltLength) {
            result = segment[index];
        }
        return result;
    }

    /**
     * Check whether the belt is currently empty
     * @return true if the belt is currently empty, otherwise false
     */
    private boolean isEmpty() {
        for (int i = 0; i < segment.length; i++) {
            if (segment[i] != null) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return java.util.Arrays.toString(segment);
    }

    /*
     * @return the final position on the belt
     */
    public int getEndPos() {
        return beltLength-1;
    }
    
    
    /**
     * Pause the belt
     */
    public synchronized void pauseBelt(){
    	this.isPaused = true;
    	notifyAll();
    }
    /**
     * Resume the belt 
     */
    public synchronized void resumeBelt(){
    	this.isPaused = false;
    	notifyAll();
    }
    
    /**
     * Set the semaphore to notify the belt when the sensing finished
     * @param finishSensing : The semaphore that indicates if the sensor finish its work
     */
    public synchronized void setFinishSensing(boolean finishSensing){
    	this.finishSensing = finishSensing;
    	notifyAll();
    }
    
    /**
     * Process the element on the sensor's location
     * @param index : the sensor location
     * @throws InterruptedException
     */
    public void processElement(int index) throws InterruptedException{
    	
    		if(segment[index-1]!=null){
    			// if new element on the segment three is a bicycle, process it
    			processBicycle(segment[index-1]);
    		}else{
    			// An empty arrives element,pause the belt and wait for the inspected bicycle being put
    			pauseBelt();
    		}
    }
    
    /**
     * Further actions for a bicycle element of segment three 
     * @param bicycle
     * @throws InterruptedException
     */
    
    public void processBicycle(Bicycle bicycle) throws InterruptedException{
    	
    	if (bicycle.firstTimeSenseing){
			System.out.println(bicycle +" arrived on segment 3 and will be sensed");
			// pause belt.
			pauseBelt();
			// Wait until the sensor finish sensing
	        while(!finishSensing){
		          wait();
	        }
	        // if the bicycle is not tagged 
	        if(sensor.sensingResult==false){
		       // resume Belt and print trace
	    	   System.out.println(
                		indentation +
                		bicycle +
                        " [ s" + (Params.SEN_LOC) + " -> s" + (Params.SEN_LOC+1) +" ]");
		
	    	   // reset the finisheSening Semaphore
	    	   this.finishSensing = false;
	    	   resumeBelt();
            }else{
        	   // if this bicycle is a tagged one
            	// Reset the finisheSening Semaphore
 	    	   this.finishSensing = false;
        	   // Print event trace
	    	   System.out.println(bicycle +" is tagged and will be inspected");
    	       
    	       // wait until the robot is free
    	       while(robot.isOccupied){
    	    	   
    	       }
    	       System.out.println(bicycle +" is picked up by Robot");
    	       System.out.println(
               		indentation +
               		bicycle +
                       " [ s" + (Params.SEN_LOC) + " -> Robot ]");
    	       
    	       // Set segment 3 to be empty 
    	       segment[Params.SEN_LOC-1]=null;
    	       
    	       // occupy the robot
    	       robot.occupyRobot("Belt",bicycle);
    	       // resume belt 
		       resumeBelt();
         }
	}else if (!bicycle.firstTimeSenseing){
    	 // if this bicycle has been inspected and is sent back from inspector
    	 System.out.println(
         		indentation +
         		bicycle +
                 " [ s" + (Params.SEN_LOC) + " -> s" + (Params.SEN_LOC+1) +" ]");
 		resumeBelt();
	 }
  }
}
