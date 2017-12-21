import java.util.Random;

/**
 * A producer continually tries, at varying time intervals, 
 * to put a bicycle onto a belt
 */
public class Producer extends BicycleHandlingThread {

    // the belt to which the producer puts the bicycles
    protected Belt belt;

    /**
     * Create a new producer to feed a given belt
     */
    Producer(Belt belt) {
        super();
        this.belt = belt;
    }

    /**
     * The thread's main method. 
     * Continually tries to place bicycles on the belt at random intervals.
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                // put a new bicycle on the belt
                Bicycle bicycle = Bicycle.getInstance();
                belt.put(bicycle, 0);

                // sleep for a bit....
                Random random = new Random();
                int sleepTime = random.nextInt(Params.PRODUCER_MAX_SLEEP);
                sleep(sleepTime);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Producer terminated");
    }
}
