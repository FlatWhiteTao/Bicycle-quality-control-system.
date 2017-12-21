/**
 * The bicycle quality control belt
 */
public class Belt {

    // the items in the belt segments
    protected Bicycle[] segment;

    // the length of this belt and the main belt and short belt can be then differentiated
    protected int beltLength;
    // the name of the belt
    protected String beltName;
    // The semaphore that instructs belt to be paused and resumed
    protected boolean isPaused = false;
    // The semaphore that indicates that if the sensor finish sensing
    protected boolean finishSensing = false;
    // The semaphore that indicates that if the consumer is free or not
    protected boolean isConsumerAvailable = true;
   
    //Four components that the belt will interact with
    protected Sensor sensor;
    protected Robot robot;
    protected Inspector inspector;
    protected Consumer consumer;
    
    // to help format output trace
    final private static String indentation = "                  ";

    /**
     * Create different types of belt according to the belt length passed in 
     * @param Length : the belt length 
     */
    public Belt(int length){
		this.beltLength = length;
    	segment = new Bicycle[beltLength];
    	for (int i = 0; i < segment.length; i++) {
            segment[i] = null;
        }
    	
    	if(length == Params.MBT_LEN){
    		this.beltName = "Main Belt";
    	}
    	if(length == Params.SBT_LEN){
    		this.beltName = "Shorter Belt";
    	}
    }
    
    /**
     * Set up the interactions between belt and other components
     * @param sensor :interaction component
     * @param robot :interaction component
     * @param inspector :interaction component
     * @param consumer :interaction component
     */
    public void setupInteraction(Sensor sensor,Robot robot,Inspector inspector,Consumer consumer){
    	this.sensor = sensor;
    	this.robot = robot;
    	this.inspector = inspector;
    	this.consumer = consumer;
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

    	// while there is another bicycle in the way, block this thread *
        while (segment[index] != null) {
            wait();
        }
        
        // insert the element at the specified location
        segment[index] = bicycle;

        // print events trace
        if(this.beltLength==Params.MBT_LEN){
        	 System.out.println(bicycle + " arrived on Main Belt");
        }
        if(this.beltLength==Params.SBT_LEN){
        	System.out.println(
            		indentation +
            		bicycle +
                    " [ Inspector -> Shorter Belt ]" );
        	
        	 System.out.println(bicycle + " arrived on Shorter Belt");
        }
      //when the bicycle finally arrives on the shorter belt then release the robot 
      		robot.releaseRobot();
        
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
        if(bicycle.isTagged()){
        		System.out.println(bicycle + " departed (Recycled) [" + this.beltName + "]");
        	}else{
        		System.out.println(bicycle + " departed (Shipped) [" + this.beltName + "]");
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
    	
    	// The move conditions of two belts are different, thus we need apply different conditions 
    	
    	// in terms of the main belt
    	if(this.beltLength == Params.MBT_LEN){
    		// add another condition : the belt receives the pause semaphore
    		while (isEmpty() || segment[segment.length-1] != null || this.isPaused) {
    	            wait();
    	        }
    	}else if(this.beltLength == Params.SBT_LEN){
            // in terms of the short belt
    		while (segment[segment.length-1] != null) {
    	            wait();
    	        }
    	}
       
    	// double check that a bicycle cannot fall of the end
        if (segment[segment.length-1] != null) {
            String message = "Bicycle fell off end of " + " belt";
            throw new OverloadException(message);
        }

        // move the elements along, making position 0 null
        for (int i = segment.length-1; i > 0; i--) {
            if (this.segment[i-1] != null) {
            	// when the bicycle arrives segment 3 (on main belt) or 
            	// arrives on the last segment (on both belts)
            	// further process needs to be applied
            	if(i==Params.SEN_LOC || i==segment.length-1){
            		processElement(i);
            	}
            	else{
                 
            		System.out.println(
                		indentation +
                		this.segment[i-1] +
                        " [ s" + (i) + " -> s" + (i+1) +" ]" + "(" + this.beltName +")");
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
     * Notify the belts that if the consumer is currently free
     * @param isConsumerFree
     */
    
    public synchronized void setConsumerStatus(boolean isConsumerAvailable){
    	this.isConsumerAvailable = isConsumerAvailable;
    	notifyAll();
    }
    
    
    /**
     * Process the element on the sensor's location or the element arrives on the last segment
     * @param index : the sensor location or the last segment location
     * @throws InterruptedException
     */
    public void processElement(int index) throws InterruptedException{
    	
    		if(index==Params.SEN_LOC){
    			// if new element on the segment three is a bicycle, sense it
    			senseBicycle(segment[index-1]);
    		}else if(index==this.beltLength-1) {
    			// if the 
    			consumeBicycle(segment[index-1]);
    		}
    } 
    
    /**
     * Sense a bicycle on segment three and tagged will be sent to the inspector
     * @param bicycle
     * @throws InterruptedException
     */
    
    public void senseBicycle(Bicycle bicycle) throws InterruptedException{
    	// print event trace
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
                    " [ s" + (Params.SEN_LOC) + " -> s" + (Params.SEN_LOC+1) +" ]"+ "(" + this.beltName +")");
	
    	   // reset the finisheSening Semaphore
    	   this.finishSensing = false;
    	   resumeBelt();
        }else{
        	// if this bicycle is tagged
        	// Reset the finisheSening Semaphore
	    	   this.finishSensing = false;
     	    // Print event trace
	    	   System.out.println(bicycle +" is tagged and will be inspected");
	    	// wait until the inspector workshop is free. This avoids deadlock *
               while(inspector.inspectingBicycle!=null){
               	wait();
               }
            // wait until the robot is free.
               while(robot.isOccupied){
               	wait();
               }
            // print event trace
               System.out.println(bicycle +" is picked up by Robot");
    	       System.out.println(
               		indentation +
               		bicycle +
                       " [ s" + (Params.SEN_LOC) + " -> Robot ]");
    	       
    	       // Set segment 3 to be empty 
    	       segment[Params.SEN_LOC-1]=null;
    	       // occupy the robot
    	       robot.occupyRobot("MainBelt",bicycle);
    	       // resume belt 
		       resumeBelt();
	    }
    }
    
    /**
     * Ready to put a bicycle to the consumer
     * @param bicycle
     * @throws InterruptedException 
     */
    
    public void consumeBicycle(Bicycle bicycle) throws InterruptedException{
    	
    	System.out.println(
 	            indentation +
 	            bicycle +
 	               " [ s" + (this.beltLength-1) + " -> s" + (this.beltLength) +" ]" + "(" + this.beltName +")");
		
		//wait until the consumer is free to occupy
		while(!this.isConsumerAvailable){
			wait();
		}
		// occupy the consumer 
		consumer.occupyConsumer(this.beltName,true);
	}
   
}
