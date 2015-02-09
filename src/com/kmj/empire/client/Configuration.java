package com.kmj.empire.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Configuration {
	
	private String username;
	private String password;
	private String serverAddress;
	private int serverPort;
	
	private static Configuration configuration;
	
	public static final String CONFIGURATION_FILE = "resources/client_configuration";
	
	private Configuration() {
		username = "";
		password = "";
		serverAddress = "";
		serverPort = 0;
	}

	static {
		Configuration configuration = new Configuration();
	}
	
	public static Configuration getInstance() { return configuration; }
	
	// Getters and Setters
	public void setUsername(String username) { this.username = username; }
	public void setPassword(String password) { this.password = password; }
	public void setServerAddress(String serverAddress) { this.serverAddress = serverAddress; }
	public void setServerPort(int serverPort) { this.serverPort = serverPort; }
	
	public String getUsername() { return username; }
	public String getPassword() { return password; }
	public String getServerAddress() { return serverAddress; }
	public int getServerPort() { return serverPort; }
	
	// Load the serialized Configuration from disk.
	public void save() {
		File configurationFile = new File(CONFIGURATION_FILE);
		
		// Check for file.
		if(!configurationFile.exists() || configurationFile.isDirectory()) {
			System.out.println("The configuration file \'" + CONFIGURATION_FILE + "\' could not be " +
					"opened because it does not exist, or it is a directory.");
		}
		else {
			try {
				FileInputStream fis = new FileInputStream(configurationFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				configuration = (Configuration) ois.readObject();
				ois.close();
				fis.close();
				System.out.println("Loaded configuration from file.");
			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch(IOException e) {
				System.out.println("The configuration file \'" + CONFIGURATION_FILE + "\' does not exist.");
			}
		}
	}
	
	public void load() {
		
	}
}
