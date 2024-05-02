package main.java.colorSensor;

import data.Config;
import data.LineFollowingTimeTracker;
import lejos.hardware.Button;
import main.java.motors.MotorControl;
import main.java.ultraSonicSensor.UltrasonicSensor;

public class MainClass {
	public static Config config = new Config();

	public static void main(String[] args) {
		ColorSensor colorSensor = new ColorSensor();
		MotorControl motorControl = new MotorControl();
		UltrasonicSensor ultrasonic = new UltrasonicSensor();
		LineFollowingTimeTracker tracker = new LineFollowingTimeTracker();
		SendInfo info = new SendInfo();
		Connection conn = new Connection();
		Thread colorSensorThread = new Thread(colorSensor);
		Thread motorControlThread = new Thread(motorControl);
		Thread UltrasonicSensor = new Thread(ultrasonic);
		Thread connThread = new Thread(conn);
		Thread sendInfothread = new Thread(info);
		Thread tracker_tread = new Thread(tracker);
		colorSensorThread.start();
		motorControlThread.start();
		UltrasonicSensor.start();
		connThread.start();
		sendInfothread.start();
		tracker_tread.start();
		while (true) {
			while (Button.ESCAPE.isUp()) {
				//
				if (!config.isRunning()) {
					System.out.print("Total running time :" + config.TIME);
					System.exit(0);
				}
			}

			System.exit(0);
		}

	}
}