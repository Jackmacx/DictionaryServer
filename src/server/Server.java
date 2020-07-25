/*********************************************************************
* Author:     Jack Macumber (817548)
* Date:       April 2020

Implements code for Assignment 1 of Distributed Systems
*********************************************************************/

package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.net.ServerSocketFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Server {
	
	private static int port;
	
	// Request counter
	// private static int requests = 0;

	private static Dictionary dict; 
	
	public static void main(String[] args)
	{
		// Handle Command Line Arguments
		if (args.length < 1 || args.length > 2) {
			System.out.println("Expects arguments: <port> [<dictionary>]");
			return;
		}
		
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Expects arguments: <port> [<dictionary>]");
			return;
		}
		
		if (args.length == 2) {
			dict = new Dictionary(args[1]);
		} else {
			dict = new Dictionary();
		}
		
		// Start Server Socket
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		try(ServerSocket server = factory.createServerSocket(port)) {
			while(true) {
				Socket client = server.accept();
				// requests++;
							
				// Start a new thread for a connection
				Thread t = new Thread(() -> serveClient(client));
				t.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private static void serveClient(Socket client) {
		try(Socket clientSocket = client)  {
			// Output and Input Stream
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
		    
			// Stay open to handle requests
			while (true) {
				// Read input
				String msg;
				try {
			    	msg = input.readUTF();
				} catch (EOFException e) {
					// Nothing to read at the moment
					continue;
				}
			    // System.out.println("Got a Request: " + msg);
			    
				// Convert input to json
			    JSONObject request = new JSONObject();
		    	try {
		    		JSONParser parser = new JSONParser();
		    		request = (JSONObject) parser.parse(msg);
		    	}  catch (Exception e){
		    		e.printStackTrace();
				}
		    	//System.out.println("Got a request of type: " + (String) request.get("type"));
		    	
		    	JSONObject reply = new JSONObject();
		    	boolean suc;
		    	switch ((String) request.get("type")) {
		    		
		    		case "Search":
		    			// Handle Search Request
		    			String res = dict.search((String) request.get("term"));
		    			if (res != null) {
		    				reply.put("data", res);
		    			} else {
			    			reply.put("err", "Search Term Could Not be Found");
			    			
			    			String data = "";
			    			ArrayList<String> near = dict.near((String) request.get("term"));
			    			if (near.size() != 0) {
			    				data = "Possible words:\n\n";
			    				for (String n: near) {
			    					data += n.toLowerCase() + ", ";
			    				}
			    			}
			    			reply.put("data", data);
			    			
		    			}
		    			break;
		    		case "Add":
		    			// Handle Add Request
		    			String info = (String) request.get("info");
		    			if (info.equals("")) {
		    				reply.put("err", "Please add Definition");
		    			} else {
			    			suc = dict.add((String) request.get("term"), info);
			    			if (suc) {
			    				reply.put("data", info);
				    			reply.put("err", "Added to Dictionary");
			    			} else {
			    				reply.put("data", info);
				    			reply.put("err", "Failed to Add Term it Already Exists");
			    			}
		    			}
		    			
		    			break;
		    		case "Delete":
		    			// Handle Delete Request
		    			suc = dict.delete((String) request.get("term"));
		    			if (suc) {
		    				reply.put("data", (String) request.get("info"));
			    			reply.put("err", "Term Removed from Dictionary");
		    			} else {
		    				reply.put("data", (String) request.get("info"));
			    			reply.put("err", "Term Already Nonexistant");
		    			}
		    			break;
		    	}
		    	
		    	// Send reply back to client
		    	output.writeUTF(reply.toString());
		    	output.flush();
			}
		} 
		catch (IOException e) {
			// Connection Ended
		}  catch (Exception e) {
			// Unexpected error
			e.printStackTrace();
		}
	}

}
