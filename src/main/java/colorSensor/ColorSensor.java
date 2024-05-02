package main.java.colorSensor;

import data.Config;
import lejos.hardware.Button;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;


public class ColorSensor implements Runnable 
{

	
    private Config config;
    
	/**
	 * EV3 color sensor
	 */
	private EV3ColorSensor colorSensor;
	
	/**
	 * Provides samples from the color sensor
	 */
	private SampleProvider sampleProv;
	
	/**
	 * Stores the latest sensor reading
	 */
    private float[] sample;
    
    /**
     * Initializes a new instance of the ColorSensor class.
     * Sets up the color sensor and prepares it for reading values in the red mode.
     * 
     * @param config The shared configuration object where sensor values and thresholds will be stored.
     */
    public ColorSensor() {
    	this.config = MainClass.config;
    	
    	/**
    	 * Initialize color sensor at port S4
    	 */
    	this.colorSensor = new EV3ColorSensor(SensorPort.S4);
    	
    	/**
    	 * Use red mode for line detection
    	 */
    	this.sampleProv = colorSensor.getRedMode();
    	
    	/**
    	 * Allocate space for sensor reading
    	 */
    	this.sample = new float[sampleProv.sampleSize()];
    }
    
    /**
     * The main execution method for the color sensor thread.
     * Starts with sensor calibration and enters a loop for continuously reading sensor values.
     * Updates the shared configuration with the latest sensor value at a fixed interval.
     */
    @Override
    public void run() 
    {
    	/**
    	 * Perform calibration before starting the main loop
    	 */
    	calibrate();
    	
    	/**
    	 * Main loop for continuous sensor reading. 
    	 * Updates the sensor value in Config class with actual sensor value.
    	 */
    	while (true) 
    	{
            float value = getSensorValue();
            config.setSensorValue(value);
            Delay.msDelay(70);
    	}
    }

    /**
     * Performs calibration by reading sensor values on the line and the floor.
     * Calculates and sets the necessary thresholds in the shared configuration based on these readings.
     * Requires manual intervention to place the sensor correctly and press a button to proceed.
     */
    public void calibrate()
    {
        /**
         * Set line value in configuration, based on sensor value returned while on line
         */
	    System.out.println("Place on line, then press any button.");
	    Button.waitForAnyPress();
	    sampleProv.fetchSample(sample, 0);
	    config.LINE_VALUE = (float) sample[0];
	    System.out.println(config.LINE_VALUE);
	    
	    /**
	     * Set floor value in configuration, based on sensor value returned while on floor
	     */
	    System.out.println("Place on floor, then press any button.");
	    Button.waitForAnyPress();
	    sampleProv.fetchSample(sample, 0);
	    config.FLOOR_VALUE = (float) sample[0];
	    System.out.println(config.FLOOR_VALUE);   

	    /**
	     * Define threshold value in configuration, 
	     * as the calculated average of floor and line values
	     */
	    config.THRESHOLD = (config.LINE_VALUE + config.FLOOR_VALUE) / 2;
	    System.out.println("Threshold set at: " + config.THRESHOLD);

	    /**
	     * Calculate and set thresholds based on calibrated floor and line values,
	     * threshold percentages have been fine tuned to aid in smooth line following
	     */
	    config.setOnFloorThreshold(0.8f * (config.FLOOR_VALUE - config.LINE_VALUE));
	    config.setFloorThreshold(0.7f * (config.FLOOR_VALUE - config.LINE_VALUE));
	    config.setOnLineThreshold(0.19f * (config.FLOOR_VALUE - config.LINE_VALUE));
	    config.setLineThreshold(0.25f * (config.FLOOR_VALUE - config.LINE_VALUE));
        
	    /**
	     * Signal the main application that calibration is complete
	     */
        config.latch.countDown();
	    
	    /**
	     * Clear screen after calibration messages
	     */
        for (int i = 0; i < 7; i++) {
        	System.out.println("");
        }
    }
    
    /**
     * Closes the color sensor to free up system resources.
     */
    public void close() {
    	if (colorSensor != null) 
    	{
    		colorSensor.close();
    	}
    }
    
    /**
     * Fetches the latest sensor value from the color sensor.
     * 
     * @return The latest sensor value as a float.
     */
    public float getSensorValue() {
        sampleProv.fetchSample(sample, 0);
        config.SENSOR_VALUE = (float) sample[0];
        return config.SENSOR_VALUE;
    }
    
}
