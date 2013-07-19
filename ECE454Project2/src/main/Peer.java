package main;

import client.ClientStateManager;
import client.ConnectionDispatcher;
import data.PropertiesOfPeer;
import server.FileServer;

public class Peer {
	
	public static void main(String [ ] args)
	{
		final PropertiesOfPeer properties = new PropertiesOfPeer();
		final ClientStateManager clientStateManager = new ClientStateManager();
			
		UserInputThread userInputThread = new UserInputThread();
		userInputThread.start();
		
		// 2) Invoke the File Client Thread		
//		ClientBroadcastUp fileClientThread = new ClientBroadcastUp(PropertiesOfPeer.ipAddrPortNumMappingAll);
//		fileClientThread.start();
//		
//		// Broadcast thread for status
//		ClientBroadcastStatus statusBroadcastThread = new ClientBroadcastStatus(PropertiesOfPeer.ipAddrPortNumMappingAll);
//		statusBroadcastThread.start();
////		
//		System.out.println("Running file client thread");
		
		//Debugging stuff
		//PrintOutIpPortAliveMapThread debugThread = new PrintOutIpPortAliveMapThread();
		//debugThread.start();
		
		//PrintOutStatusMappingsOfOtherPeers debugThread2 = new PrintOutStatusMappingsOfOtherPeers();
		//debugThread2.start();
		
		//Shutdown the server!
		//CloseThisConnectionThread closePeerThread = new CloseThisConnectionThread();
		//closePeerThread.start();

	}
	
	

}
