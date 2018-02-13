package com.server.controller;


public class SessionClient {

	private String sessionID;

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID
	 *            the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * @return the clientID
	 */
	public String getClientID() {
		return clientID;
	}

	/**
	 * @param clientID
	 *            the clientID to set
	 */
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	/**
	 * @return the connectedClientID
	 */
	public String getConnectedClientID() {
		return connectedClientID;
	}

	/**
	 * @param connectedClientID
	 *            the connectedClientID to set
	 */
	public void setConnectedClientID(String connectedClientID) {
		this.connectedClientID = connectedClientID;
	}

	private String clientID;
	private String connectedClientID;

}
