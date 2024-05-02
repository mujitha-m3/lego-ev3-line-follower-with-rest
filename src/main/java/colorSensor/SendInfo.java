package main.java.colorSensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import data.Config;

public class SendInfo implements Runnable {

	public Config config;
	URL url = null;

	public SendInfo() {
		super();
		this.config = MainClass.config;
	}

	public void run() {
		try {
			try {
				config.latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // Handle thread interruption
				return; // Early exit if the thread is interrupted
			}

			while (true) {
				HttpURLConnection conn = null;

				try {
					String baseUrl = "http://192.168.1.145:8080/rest/legoservice/retrivesettingsfromrobot";
					String speedParam = "speed=" + config.getBASE_SPEED();
					String isRunningParam = "isrunning=" + config.isRunning();

					// Construct the URL with parameters
					String urlString = baseUrl + "?" + speedParam + "&" + isRunningParam;

					url = new URL(urlString);
					// System.out.print(url);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					int responseCode = conn.getResponseCode();
					// System.out.println("Response code: " + responseCode);
					if (responseCode == HttpURLConnection.HTTP_OK) {
						// Read response if needed
						// System.out.println("HTTP success");
					} else {
						System.out.println("HTTP request failed");
					}
					// System.out.println(conn.toString());
					Thread.sleep(1000);

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Some problem!");
				} finally {
					try {

						if (conn != null) {
							conn.disconnect();
						}
					} catch (Exception e) {
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
