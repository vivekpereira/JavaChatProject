package com.client.controller;

import java.util.ArrayList;

/**
 * 		   The user of this interface has control over the
 *         client The user can utilize the functions provided to interact with
 *         the server and other clients
 */
public interface ClientControllerInterface {

	/**
	 * @return success message if server running and ready Used to check the if the
	 *         server is ready
	 */
	void getServerStatus();

	/**
	 * @param ClientID
	 *            - clientID to connect to the server
	 * @return success message on connecting to the server Used to connect to the
	 *         server with the clientID provided
	 */
	void connectServer();

	/**
	 * @return List of clients currently connected to the server Used to get the
	 *         clients connected to the server
	 */
	void getClientIDs();

	/**
	 * @param ClientID
	 *            - Client for which the session needs to be opened
	 * @return Success message if successfully opened
	 */
	void openSession(String clientID);

	/**
	 * @param ClientID
	 *            - Client for which the session needs to be closed
	 * @return Success message if successfully closed
	 */
	void closeSession(String clientID);

	/**
	 * @param ClientID
	 *            - Receiver of the message
	 * @param message
	 *            - message to be sent
	 * @return Success message if successfully sent
	 */
	void sendMessage(String clientID, String message);

	

}