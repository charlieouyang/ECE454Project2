package client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;

import data.Message;
import data.Message.MESSAGE_TYPE;
import data.MyEntry;
import data.PropertiesOfPeer;

//This will take care of each individual connection with another peer
//It will request for a list of files that another peer has

public class ConnectionInstance extends Thread {
	private Socket socket;
	private String ipAddress;
	private int portNumber;

	public ConnectionInstance(String ipAddress, int portNumber) throws Exception {
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}

	// Contact the peer and ask for a list of files
	@Override
	public void run() {
		System.out.println("Connecting to: " + ipAddress + ", " + portNumber);
		
		try {
			boolean stillTryingToConnect = PropertiesOfPeer.peerUp;
			
			while (stillTryingToConnect) {
				try {
					stillTryingToConnect = PropertiesOfPeer.peerUp;
					socket = new Socket(ipAddress, portNumber);
					if (socket != null) {
						break;
					}
				} catch (Exception e) {
					// handle exceptions
					// possibly add a sleep period
					System.err.println("Can't connect to " + ipAddress + " " + portNumber +
							"... waiting for 5 sec");
					Thread.sleep(5000);
				}
			}
			
			if (stillTryingToConnect == true){
				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				
				PropertiesOfPeer.ipAddrPortNumMappingAlive.add(new MyEntry<String, Integer>(ipAddress, portNumber));
				
				while (stillTryingToConnect){
					stillTryingToConnect = PropertiesOfPeer.peerUp;
					
					//Connection is setup
					//Make sure the connection is still supposed to be up
					if (PropertiesOfPeer.CheckIfThisHostIsStillAlive(ipAddress, portNumber)){
						Message dummyMessage = new Message(PropertiesOfPeer.ipAddress, PropertiesOfPeer.portNumber, MESSAGE_TYPE.TEST, "harrrroooooo");
						oos.writeObject(dummyMessage);
						Thread.sleep(2000);
						
					}
					else{
						break;
					}
				}
				
				if (PropertiesOfPeer.CheckIfThisHostIsStillAlive(ipAddress, portNumber) && !PropertiesOfPeer.peerUp){
					Message closingMessage = new Message(ipAddress, portNumber, MESSAGE_TYPE.PEER_LEAVING, "shutting down connection!");
					oos.writeObject(closingMessage);
				}
				
				oos.close();
			}
			//socket.close();
			
		} catch (Exception e) {
			System.out.println("Connection to: " + ipAddress + ":" + portNumber + " terminated");
		}
	}

}