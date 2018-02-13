package com.client.controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class ClientController extends JFrame implements ClientControllerInterface {

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	private Socket connection;
	private String clientID = "";
	private String clientIDTemp = "";

	JPanel buttonPanel1 = new JPanel();
	JPanel buttonPanel2 = new JPanel();
	JPanel textPanel = new JPanel();
	JPanel textFieldPanel = new JPanel();
	JPanel controlPanel = new JPanel();
	JPanel mainPanel = new JPanel();

	JButton refresh = new JButton("REFRESH");
	JButton connect = new JButton("CONNECT");
	JButton close = new JButton("DISCONNECT");

	String[] clients = {};
	JPanel clientPanel = new JPanel();

	JComboBox<String> clientComboBox = new JComboBox<>(clients);

	// To start the client
	public ClientController() {
		super("Instant Messaging");
		userText = new JTextField("", 51);
		userText.setEditable(true);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		refresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				getClientIDs();

			}
		});
		connect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				openSession((String) clientComboBox.getSelectedItem());

			}
		});

		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				try {

					output.writeObject("DISCONNECT");
					output.flush();
					connection.close();
					output.close();
					input.close();
				} catch (IOException e1) {

					e1.printStackTrace();
				}

			}
		});

		userText.setSize(51, 50);
		textPanel.add(userText);
		textFieldPanel.setLayout(new FlowLayout());
		textFieldPanel.setSize(50, 10);
		// mainPanel.setBounds(40,80,200,200);
		buttonPanel1.setLayout(new FlowLayout());

		chatWindow = new JTextArea();
		chatWindow.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		buttonPanel1.setFont(new Font("Arial", Font.BOLD, 16));
		textPanel.setLayout(new BorderLayout());
		textPanel.add(userText, BorderLayout.NORTH);
		textPanel.add(new JScrollPane(chatWindow), BorderLayout.CENTER);

		clientPanel.setLayout(new FlowLayout());

		buttonPanel1.add(refresh);
		buttonPanel1.add(connect);
		buttonPanel1.add(close);
		buttonPanel1.add(clientComboBox);

		GridLayout gLayout = new GridLayout(3, 0);
		mainPanel.setLayout(gLayout);
		controlPanel.add(buttonPanel1, BorderLayout.CENTER);

		controlPanel.add(textFieldPanel);
		controlPanel.setLayout(new GridLayout(2, 0));
		mainPanel.add(controlPanel);
		mainPanel.add(textPanel);
		this.getContentPane().add(mainPanel);
		setSize(800, 500); // Sets the window size
		setVisible(true);

	}

	@Override
	public void getServerStatus() {
		sendMessage("STATUS");
		showMessage(readInput());
	}

	@Override
	public void connectServer() {

		try {

			this.connection = new Socket("localhost", 6789);
			this.output = new ObjectOutputStream(this.connection.getOutputStream());
			this.output.flush();
			this.input = new ObjectInputStream(this.connection.getInputStream());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void getClientIDs() {
		try {
			sendMessage("CLIENTLIST");
			String clientIDS = readInput();
			clientIDS = clientIDS.replace("[", "").replace("]", "");
			clientIDS = clientIDS.replace(" ", "");
			List<String> clientList = new LinkedList<String>(Arrays.asList(clientIDS.split(",")));
			int index = clientList.indexOf(clientID);
			clientList.remove(index);

			DefaultComboBoxModel model = new DefaultComboBoxModel(clientList.toArray());
			clientComboBox.setModel(model);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void openSession(String clientID) {
		sendMessage("OPEN:" + clientID);
		showMessage(readInput());
	}

	@Override
	public void closeSession(String clientID) {
		sendMessage("CLOSE:" + clientID);
		showMessage(readInput());

	}

	@Override
	public void sendMessage(String clientID, String message) {
		sendMessage("SEND:" + clientID + ":" + message);
		showMessage(readInput());
	}

	private void sendMessage(String message) {
		try {

			output.writeObject(message);
			output.flush();
			clientIDTemp = message;
			if (clientID.equals("")) {
				clientID = message;
				this.setTitle(clientID);
			}
			if (!(message.contains("CONNECT:") || message.equals("CLIENTLIST") || message.equals("CONNECTSERVER")
					|| message.equals("STATUS"))) {
				message = message.replaceAll(".*:", "");
				showMessage("\n" + clientID + " :" + message);
			}

		} catch (IOException ioException) {
			if (message.equals("STATUS")) {
				chatWindow.append("\n Server is offline...");
			} else {
				chatWindow.append("\n Oops! Something went wrong!");
			}

		}
	}

	private void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatWindow.append(message);
			}
		});
	}

	public String readInput() {
		String message = "";
		try {
			message = (String) input.readObject();
			if (message.equals("Successfully Registered")) {
				clientID = clientIDTemp;
				this.setTitle(clientID);
			}
		} catch (ClassNotFoundException | IOException e) {

			try {
				connection.close();
				output.close();
				input.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e1.printStackTrace();
			}

		}
		return message;
	}

	private void whileChatting() throws IOException {
		String message = "";
		do {

			message = readInput();

			showMessage("\n" + message);

		} while (!connection.isClosed());

	}

	public static void main(String arg[]) throws Exception {

		ClientController clientController = new ClientController();
		try {
			clientController.connectServer();

			clientController.whileChatting();
		} catch (Exception e) {
			clientController.showMessage("\n" + "Server is Offline");
		}
		/*
		 * InputStreamReader IR= new InputStreamReader(soct.getInputStream());
		 * BufferedReader br = new BufferedReader(IR); BufferedReader keyRead = new
		 * BufferedReader(new InputStreamReader(System.in)); String message =
		 * br.readLine(); System.out.println(message); String receiveMessage;
		 */
		/*
		 * while(true) { message = keyRead.readLine(); // keyboard reading
		 * ps.println(message); // sending to server ps.flush(); // flush the data
		 * if((receiveMessage = br.readLine()) != null) //receive from server {
		 * System.out.println(receiveMessage); // displaying at DOS prompt } }
		 */

	}
}