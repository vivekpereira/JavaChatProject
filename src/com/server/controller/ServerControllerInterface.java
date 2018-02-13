package com.server.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * 		   The user of this interface has control over the
 *         connections of each client to the server. The user can process
 *         various functions pertaining to the client by the server
 *
 */
public interface ServerControllerInterface {

	/**
	 * @return true if server is running and ready
	 */
	boolean displayStatus();

	/**
	 * @param clientID
	 *            - client to be registered in the system
	 * @return true if successfully registered
	 */
	boolean registerUser(String clientID);

	/**
	 * @return list of clients currently connected to the server
	 */
	List<String> getClientList();

	/**
	 * @param RequestClient
	 *            - requesting clientID
	 * @param ConnectClient
	 *            - Establish connection to the connectClient
	 * @return true if session successfully opened
	 */
	boolean openSession(String requestClient, String connectClient);

	/**
	 * @param RequestClient
	 *            - requesting clientID
	 * @param ConnectClient
	 *            - Establish connection to the connectClient
	 * @return true if session successfully closed
	 */
	boolean closeSession(String requestClient, String connectClient);

	/**
	 * @param RequestClient
	 *            - Sender of the message
	 * @param ConnectClient
	 *            - Receiver of the message
	 * @param message
	 *            - message to be sent
	 * @return true if successfully sent
	 */
	boolean sendMessage(String requestClient, String connectClient, String message);

	/**
	 * @param requestClient
	 *            - clientId to disconnect
	 * @return true if successfully disconnected
	 */
	boolean disconnectUser(String requestClient);
}