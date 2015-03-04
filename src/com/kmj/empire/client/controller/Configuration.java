/**
 * Holds all the client-side configuration data. This class also saves the data
 * with the appropriate method call to the location specified by the String
 * CONFIGURATION_FILE.
 */
package com.kmj.empire.client.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.kmj.empire.common.exceptions.BadConfigurationException;

public class Configuration implements Serializable {

	private String username;
	private String password;
	private String serverAddress;
	private int serverPort;

	private static Configuration configuration;

	public static final String CONFIGURATION_FILE = "resources/client_configuration";

	/**
	 * Default constructor for the Configuration object. All
	 * fields are blanked out or set to zero initially.
	 */
	private Configuration() {
		username = "";
		password = "";
		serverAddress = "";
		serverPort = 0;
	}

	// Initialize the Configuration object.
	static {
		configuration = new Configuration();
	}

	/**
	 * Returns the configuration object.
	 * @return The singleton Configuration object.
	 */
	public static Configuration getInstance() { return configuration; }

	/**
	 * Sets the username into the configuration. This method checks if the provided
	 * username is blank before returning.
	 * @param username The username to be applied
	 * @throws BadConfigurationException
	 */
	public void setUsername(String username) throws BadConfigurationException {
		// The username:
		// - cannot be blank
		if(username.length() == 0)
			throw new BadConfigurationException("The username cannot be blank.");
		this.username = username;
	}

	/**
	 * Sets the password into the configuration. This method checks if the provided
	 * password is blank before returning.
	 * @param password The password to be applied
	 * @throws BadConfigurationException
	 */
	public void setPassword(String password) throws BadConfigurationException {
		// The password:
		// - cannot be blank
		if(password.length() == 0)
			throw new BadConfigurationException("The password cannot be blank.");
		this.password = password;
	}

	/**
	 * Sets the server's address into the configuration. This method checks if the provided
	 * address is blank before returning.
	 * @param serverAddress The address of the server
	 * @throws BadConfigurationException
	 */
	public void setServerAddress(String serverAddress) throws BadConfigurationException {
		// The server address:
		// - cannot be blank
		if(serverAddress.length() == 0)
			throw new BadConfigurationException("The server address cannot be blank.");
		this.serverAddress = serverAddress;
	}
	
	/**
	 * Sets the server's port into the configuration. This method checks if the provided
	 * port is within the appropriate range before returning.
	 * @param serverPort
	 * @throws BadConfigurationException
	 */
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

	/**
	 * Returns the set username.
	 * @return The username
	 */
	public String getUsername() { return username; }
	
	/**
	 * Returns the set password.
	 * @return The password
	 */
	public String getPassword() { return password; }
	
	/**
	 * Returns the set server address.
	 * @return The server address
	 */
	public String getServerAddress() { return serverAddress; }
	
	/**
	 * Returns the set server port.
	 * @return The server port
	 */
	public int getServerPort() { return serverPort; }

	/**
	 * Deserializes the Configuration object from the file specified by
	 * CONFIGURATION_FILE. This method will gracefully return if no configuration
	 * is located in the specified location or if it cannot otherwise reach that
	 * location.
	 */
	public void load() {
		File configurationFile = new File(CONFIGURATION_FILE);

		// Check for file before attempting a load.
		if(!configurationFile.isFile()) {
			System.out.println("There is no configuration to load from " + configurationFile.getAbsolutePath() + ".");
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
				System.out.println("Failed to load configuration from " + configurationFile.getPath() + ".");
			}
		}
	}

	/**
	 * Serializes the Configuration object to disk.
	 */
	public void save() {
		File configurationFile = new File(CONFIGURATION_FILE);

		try {
			FileOutputStream fos = new FileOutputStream(configurationFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(configuration);
			oos.close();
			fos.close();
		}
		catch(IOException e) {
			System.out.println("Failed to save configuration to " + configurationFile.getPath() + ".");
		}
	}

	private static final long serialVersionUID = 7316808087757863475L;
}
