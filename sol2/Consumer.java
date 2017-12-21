import java.util.Random;

/**
 * A consumer continually tries to take bicycles from the end of a quality control belt
 */

public class Consumer extends BicycleHandlingThread {

    // the belt from which the consumer takes the bicycles
    protected Belt belt;
    protected Belt shorterBelt;
    // the semaphore indicates if the consumer is occupied or not
    protected boolean isOccupied = false;
 	//  the semaphore indicates which component currently occupies the robot
 	protected String occupant;

    /**
     * Create a new Consumer that consumes from a belt
     */
    public Consumer(Belt belt,Belt shorterBelt) {
        super();
        this.belt = belt;
        this.shorterBelt = shorterBelt;
    }
    /**
     * a synchronized method to ensure that the consumer consume a bicycle from just one belt at a time
     * @throws InterruptedException 
     */
    public synchronized void consumeBicycle() throws InterruptedException{
    	
    	// if the consumer now is occupied by Main Belt
    	if(this.occupant == "Main Belt"){
    		//consume the bicycle from the main belt
    	    consumeMainBeltBicycle();
    	}
    	// if the consumer now is occupied by Shorter Belt
    	if(this.occupant == "Shorter Belt"){
    		//consume the bicycle from the shorter belt 
    		consumeShorterBeltBicycle();
    	}
    }
   
    /**
     * Consume bicycle and then release (Main belt)
     * @throws InterruptedException
     */
    
    public synchronized void consumeMainBeltBicycle() throws InterruptedException{
    	// get the last bicycle and consume
    	belt.getEndBelt();
    	// release the consumer resource
    	releaseConsumer();
    	// inform the main belt and shorter belt
    	notifyBelts(true);
    }
    
    /**
     * Consume bicycle and then release (Shorter belt)
     * @throws InterruptedException
     */
    public synchronized void consumeShorterBeltBicycle() throws InterruptedException{
    	// get the last bicycle and consume
    	shorterBelt.getEndBelt();
    	// release the consumer resource
    	releaseConsumer();
    	// inform the main belt and shorter belt
    	notifyBelts(true);
    }
   
    /**
     * occupy the consumer
     * @param occupant : who wants to occupy the consumer
     * @param isOccpuied : set the occupy status 
     */
    public synchronized void occupyConsumer(String occupant,boolean isOccpuied){
    	//occupy the consumer
    	this.isOccupied = true;
    	this.occupant = occupant;
    	//inform the main and shorter belts that the consumer now is not available currently
    	notifyBelts(false);
    }
    
    /**
     * notify main belt or short belt that the consumer is available or not
     */
    public void notifyBelts(boolean isConsumerAvailable){
    	belt.setConsumerStatus(isConsumerAvailable);
    	shorterBelt.setConsumerStatus(isConsumerAvailable);
    	notifyAll();
    }
    /**
     * release the consumer resource
     */
    public void releaseConsumer(){
    	this.isOccupied = false;
    	this.occupant = null;
    }

    /**
     * Loop indefinitely trying to get bicycles from the quality control belt
     */
    public void run() {
        while (!isInterrupted()) {
            try {
            	if(this.isOccupied){
            	  consumeBicycle();
            	}
            	// let some time pass ...
                Random random = new Random();
                int sleepTime = Params.CONSUMER_MIN_SLEEP + 
                		random.nextInt(Params.CONSUMER_MAX_SLEEP - 
                				Params.CONSUMER_MIN_SLEEP);
                sleep(sleepTime);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Consumer terminated");
    }
}
