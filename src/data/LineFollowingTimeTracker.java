package data;
 
import lejos.utility.Delay;
import main.java.colorSensor.MainClass;

public class LineFollowingTimeTracker implements Runnable {
    private long startTime;
    private long elapsedTime; // Define elapsedTime as an instance variable

    public LineFollowingTimeTracker() {
        this.startTime = System.currentTimeMillis();
        this.elapsedTime = 0; // Initialize elapsedTime
    }

    @Override
    public void run() {
    	  try {
	           MainClass.config.latch.await();
	            // Proceed with the rest of the thread's logic after calibration is done
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt(); // Preserve interrupt status
	            return; // Exit the method
	        }
    	  
        while (true) {
            long currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - startTime;
            long time = elapsedTime / 1000 ;
            MainClass.config.TIME = time;
            // Add a small delay to control loop execution frequency
           // System.out.println("Total running time :"+time);
            Delay.msDelay(1000); // Update every second
        }
    }

    
    public long getElapsedTime() {
        return elapsedTime;
    }
}