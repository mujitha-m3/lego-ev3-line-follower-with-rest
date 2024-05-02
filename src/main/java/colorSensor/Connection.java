package main.java.colorSensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import data.Config;

public class Connection implements Runnable {
	public Config config;
	URL url = null;

	public Connection() {
		super();
		this.config = MainClass.config;
	}

	public void run() {
		try {
			try {
				/*
				 * Blocking call to ensure calibration is complete
				 */
				config.latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // Handle thread interruption
				return; // Early exit if the thread is interrupted
			}

			while (true) {
				String s = null;
				HttpURLConnection conn = null;
				InputStreamReader isr = null;
				BufferedReader br = null;
				try {
					url = new URL("http://192.168.1.145:8080/rest/legoservice/setvalues");
					/// System.out.print(url);
					conn = (HttpURLConnection) url.openConnection();
					// System.out.println(conn.toString());
					InputStream is = null;

					try {
						is = conn.getInputStream();
					} catch (Exception e) {
						System.out.println("Exception conn.getInputSteam()");

					}
					isr = new InputStreamReader(is);
					br = new BufferedReader(isr);
					while ((s = br.readLine()) != null) {
						String[] fetchedValue = s.split("#");

						String condition = fetchedValue[1];
						boolean value = true;
						if (condition.equalsIgnoreCase("true")) {
							value = true;
						} else {
							value = false;
						}
						// System.out.println("Return value : " +s);

						// Convert the fetched value to an integer
						String speed = fetchedValue[0];
						// System.out.print(speed);
						config.setBASE_SPEED(Float.parseFloat(speed));
						config.setRunning(value);

					}

					Thread.sleep(1000);

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Some problem!");
				} finally {
					try {
						if (br != null) {
							br.close();
						}
						if (isr != null) {
							isr.close();
						}
						if (conn != null) {
							conn.disconnect();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
			Thread.currentThread().interrupt(); // Preserve interrupt status
			return; // Exit the method
		}

	}
}
