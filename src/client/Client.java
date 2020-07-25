/*********************************************************************
* Author:     Jack Macumber (817548)
* Date:       April 2020

Implements code for Assignment 1 of Distributed Systems
*********************************************************************/

package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Client {
	
	private String ip;
	private int port;
	private Socket socket=null;
	private DataInputStream input;
	private DataOutputStream output;
	
	public Client(int port, String ip){
		this.port = port;
		this.ip = ip;
		
		makeSocketConnection();
		
	}
	
	private boolean makeSocketConnection() {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				// Socket Dealt With
			}
		}
		
		try {
			socket = new Socket(ip, port);
			// Output and Input Stream
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			return false; // Failed
		}
		return true; // Success
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject send(JSONObject action) {
		JSONObject result = new JSONObject();	    
		// System.out.println("Sending Request: " + action.toString());
		
		// Ensure connected
		if (socket == null) {
			if (!makeSocketConnection()) {
				result.put("err", "Could not Establish Connection");
				return result;
			}
		}
		
		try {
			// Send Request to Server
			output.writeUTF(action.toString());
		    output.flush();
		
		    // Handle Server Reply
		    try {
		    	JSONParser parser = new JSONParser();
		    	result = (JSONObject) parser.parse(input.readUTF());
		    } catch (Exception e){
		    	e.printStackTrace();
		    		
		    	result.put("err", "Unexpected Network Data Format");
			}
	    
		} catch (IOException e) {
			// Connection has been Lost
			result.put("err", "Lost Network Connection");
			socket = null;
		}
		
		return result;
	}

}
