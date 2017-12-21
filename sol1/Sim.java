/**
 * The driver of the simulation 
 */

public class Sim {
    /**
     * Create all components and start all of the threads.
     */
    public static void main(String[] args) {
        
        Belt belt = new Belt();
        Producer producer = new Producer(belt);
        Consumer consumer = new Consumer(belt);
        BeltMover mover = new BeltMover(belt);
        Sensor sensor = new Sensor(belt);
        Robot robot = new Robot(belt);
        Inspector inspector = new Inspector();
        belt.setupInteraction(sensor, robot);
        robot.setupInteraction(inspector);
        inspector.setupInteraction(robot, sensor);
        
        consumer.start();
        producer.start();
        mover.start();
        sensor.start();
        robot.start();
        inspector.start();


        while (consumer.isAlive() && 
               producer.isAlive() && 
               mover.isAlive()    &&
               sensor.isAlive()   &&
               robot.isAlive()    &&   
               inspector.isAlive() )
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                BicycleHandlingThread.terminate(e);
            }

        // interrupt other threads
        consumer.interrupt();
        producer.interrupt();
        mover.interrupt();
        sensor.interrupt();
        robot.interrupt();
        inspector.interrupt();

        System.out.println("Sim terminating");
        System.out.println(BicycleHandlingThread.getTerminateException());
        System.exit(0);
    }
}
