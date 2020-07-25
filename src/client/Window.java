/*********************************************************************
* Author:     Jack Macumber (817548)
* Date:       April 2020

Implements code for Assignment 1 of Distributed Systems
*********************************************************************/

package client;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.json.simple.JSONObject;
import java.awt.Color;
import java.awt.EventQueue;

public class Window {

	private JFrame frame;
	
	private Client client;

	public static void main(String[] args) 
	{
		// Handle Command Arguments
		if (args.length != 2) {
			System.out.println("Expects arguments: <server-ip> <server-port>");
			return;
		}
		
		int port;
		String ip = args[0];
		try {
			port = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("Expects arguments: <server-ip> <server-port>");
			return;
		}
		
		/**
		 * Launch the application.
		 */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frame.setVisible(true);
					window.client = new Client(port, ip);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public Window() {	
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setForeground(new Color(255, 0, 0));
		textPane.setBackground(new Color(240, 240, 240));
		
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		JTextField textField;
		textField = new JTextField();
		textField.setColumns(10);
		
		// Searching Words
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {					
				JSONObject action = new JSONObject();
				action.put("type", "Search");
				action.put("term", textField.getText());
				
				JSONObject result = client.send(action);
				textPane.setText(forceString(result.get("err")));
				textArea.setText(forceString(result.get("data")));
			}
		});
		
		// Adding Words
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				JSONObject action = new JSONObject();
				action.put("type", "Add");
				action.put("term", textField.getText());
				action.put("info", textArea.getText());
				
				JSONObject result = client.send(action);
				textPane.setText(forceString(result.get("err")));
				textArea.setText(forceString(result.get("data")));
			}
		});
		
		// Deleting Words
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				JSONObject action = new JSONObject();
				action.put("type", "Delete");
				action.put("term", textField.getText());
				
				JSONObject result = client.send(action);
				textPane.setText(forceString(result.get("err")));
				textArea.setText(forceString(result.get("data")));
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(textPane, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSearch)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
						.addComponent(textArea, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 424, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSearch)
						.addComponent(btnAdd)
						.addComponent(btnDelete)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
	
	// Passes Strings Through While Turning Invalid Formats into Empty Strings
	private static String forceString(Object s) {
		String r = "";
		try {
			if (s != null) {
				r = (String) s;
			}
		} catch (Exception e) {
			return "";
		}
		return r;
	}
}
