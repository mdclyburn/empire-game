package com.kmj.empire.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

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
	public void setUsername(String username) throws BadConfigurationException {
		// The username:
		// - cannot be blank
		if(username.length() == 0)
			throw new BadConfigurationException("The username cannot be blank.");
		this.username = username;
	}
	
	public void setPassword(String password) throws BadConfigurationException {
		// The password:
		// - cannot be blank
		if(password.length() == 0)
			throw new BadConfigurationException("The password cannot be blank.");
		this.password = password;
	}
	
	public void setServerAddress(String serverAddress) throws BadConfigurationException {
		// The server address:
		// - cannot be blank
		if(serverAddress.length() == 0)
			throw new BadConfigurationException("The server address cannot be blank.");
		this.serverAddress = serverAddress;
	}
	public void setServerPort(int serverPort) throws BadConfigurationException {
		// The server port:
		// - must be greater than or equal to 0
		// - must be less than 65536
		if(serverPort < 0)
			throw new BadConfigurationException("The port number cannot be less than zero.");
		else if(serverPort > 65535)
			throw new BadConfigurationException("The port number cannot be greater than 65535.");
		this.serverPort = serverPort;
	}
	
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
		File configurationFile = new File(CONFIGURATION_FILE);
		
		// Check for file.
		if(!configurationFile.exists() || configurationFile.isDirectory()) {
			System.out.println("The configuration file \'" + CONFIGURATION_FILE + "\' could not be " +
					"opened because it does not exist, or it is a directory.");
		}
		else {
			try {
				FileOutputStream fos = new FileOutputStream(configurationFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(configuration);
				oos.close();
				fos.close();
			}
			catch(IOException e) {
				System.out.println("Failed to save configuration to file.");
			}
		}
	}
}
