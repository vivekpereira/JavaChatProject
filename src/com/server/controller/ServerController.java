package com.server.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


public class ServerController implements ServerControllerInterface {

	/**
	 * SessionMap is used to keep the session of each client Key: Session ID -
	 * created on opening a session Data: Instance of SessionClient Class
	 */
	static Map<String, Set<String>> sessionMap = new HashMap<String, Set<String>>();

	/**
	 * ClientList is used to hold the list of registered clients after successfully
	 * connected to the server List is updated when the client socket connection is
	 * disconnected or when a new client is registered
	 */
	static List<String> clientList = new ArrayList<String>();
	/**
	 * ClientConnection is used to map the client with their specific
	 * connections(Output/Input Stream)
	 */
	static Map<String, ClientConnection> clientConnectionMap = new HashMap<>();

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private static ServerSocket server;
	private static Socket connection;

	public class ClientConnection extends Thread {
		private ObjectOutputStream output;
		private ObjectInputStream input;
		private Socket connection;
		private String clientId;

		public ClientConnection(Socket connection) {
			this.connection = connection;
			try {
				this.output = new ObjectOutputStream(connection.getOutputStream());
				this.input = new ObjectInputStream(connection.getInputStream());
				System.out.println("jdsbdkjbnskjnd");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void run() {

			try {

				boolean status = true;
				output.writeObject(
						"Connected to server...\n Enter your username \n Please use the following commands \n 1. CLIENTLIST : To display connected Clients \n 2. OPEN:<client username> to open session with username \n 3. SEND:<client username>:<message> to send message to the connected client \n 4. CLOSE:<client username> to close session with client \n 5. DISCONNECT to disconnect from the server \n 6. STATUS for server status(Up/Down)");
				while (status) {

					clientId = (String) input.readObject();
					if (clientList.contains(clientId)) {
						output.writeObject("Please select a unique Username");
						status = true;

					} else {
						output.writeObject("Successfully Registered");
						clientList.add(clientId);
						clientConnectionMap.put(clientId, this);
						status = false;
					}
				}
			} catch (ClassNotFoundException classNotFoundException) {

			} catch (IOException e) {

			}

			do {

				try {

					String message = (String) input.readObject();
					// Displaying clientlist
					if (message.equals("CLIENTLIST")) {

						String clientListTmp = getClientList().toString();
						if (clientListTmp.contains(clientId)) {
							clientListTmp = clientListTmp.replace(clientId, "");
							clientListTmp = clientListTmp.replaceAll(",", " ");
						}
						output.writeObject(clientListTmp);
					} else if (message.contains("OPEN:")) {

						String[] mssgContent = message.split(":");

						if (openSession(clientId, mssgContent[1])) {
							output.writeObject("Successfully open session with " + mssgContent[1]);
						} else {
							output.writeObject("Could not open with " + mssgContent[1]);
						}

					} else if (message.contains("SEND:")) {
						String[] mssgContent = message.split(":");

						if (!sendMessage(clientId, mssgContent[1], mssgContent[2])) {
							output.writeObject("Could not send message ");
						}

					} else if (message.contains("CLOSE:")) {
						String[] mssgContent = message.split(":");

						if (closeSession(clientId, mssgContent[1])) {
							output.writeObject("Closed session with " + mssgContent[1]);
						} else {
							output.writeObject("Could close session");
						}

					} else if (message.equals("DISCONNECT")) {

						disconnectUser(clientId);
					} else if (message.equals("STATUS")) {
						if (displayStatus()) {
							output.writeObject("Server is up and running.....");
						}

					}

				} catch (ClassNotFoundException classNotFoundException) {
					classNotFoundException.getMessage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(connection.isConnected());
					e.printStackTrace();
				}
				System.out.println(connection.isClosed());
			} while (!connection.isClosed());
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Constructor to start the server
	public ServerController() throws Exception {
		server = new ServerSocket(6789, 100);
	}

	@Override
	public boolean displayStatus() {
		if (!connection.isClosed())
			return true;

		return false;

	}

	@Override
	public boolean registerUser(String clientID) {

		// connecting the client and server - start
		try {

			ClientConnection clientConnection = new ClientConnection(connection);
			clientConnection.start();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// connecting the client and server - end

		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getClientList() {
		return clientList;
	}

	@Override
	public boolean openSession(String requestClient, String connectClient) {
		ClientConnection contClient = clientConnectionMap.get(connectClient);
		if (contClient != null && contClient.connection.isConnected()) {
			Set<String> connnectedClients = new HashSet<>();
			if (sessionMap.containsKey(requestClient)) {
				connnectedClients = sessionMap.get(requestClient);
				connnectedClients.add(connectClient);
			} else {
				connnectedClients.add(connectClient);
			}
			sessionMap.put(requestClient, connnectedClients);
			return true;
		}
		return false;
	}

	@Override
	public boolean closeSession(String requestClient, String connectClient) {
		if (sessionMap.get(requestClient) != null)
			sessionMap.get(requestClient).remove(connectClient);
		return true;
	}

	@Override
	public boolean sendMessage(String requestClient, String connectClient, String message) {
		ClientConnection client = ServerController.clientConnectionMap.get(connectClient);
		if (client != null && client.connection.isConnected()) {
			Set<String> connectedClients = sessionMap.get(requestClient);
			Set<String> cntClients = sessionMap.get(connectClient);
			if (connectClient != null && cntClients != null && cntClients.contains(requestClient)
					&& connectedClients.contains(connectClient)) {
				ClientConnection connectClientCont = ServerController.clientConnectionMap.get(connectClient);
				try {
					connectClientCont.output.writeObject(requestClient + ":" + message);
				} catch (IOException e) {

					e.getMessage();
				}
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean disconnectUser(String requestClient) {
		sessionMap.remove(requestClient);
		ClientConnection clientCont = clientConnectionMap.get(requestClient);
		try {
			clientCont.connection.close();
			clientCont.output.close();
			clientCont.input.close();
			clientConnectionMap.remove(requestClient);
			clientList.remove(requestClient);
			return true;
		} catch (IOException e) {

			e.printStackTrace();
		}
		return false;
	}

	public static void main(String args[]) throws Exception {

		ServerController serverCont = new ServerController();

		while (true) {
			connection = ServerController.server.accept();

			String clientId = connection.getInetAddress().getHostAddress();
			System.out.println(clientId);
			serverCont.registerUser(clientId);

			System.out.println("serverController.getClientList()" + clientList);

		}

	}

}