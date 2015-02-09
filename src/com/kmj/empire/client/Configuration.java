package com.kmj.empire.client;

public class Configuration {
	
	private String username;
	private String password;
	private String serverAddress;
	private int serverPort;
	
	private static Configuration configuration;
	
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
}
