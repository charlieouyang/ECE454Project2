package main;

import client.ClientStateManager;
import client.ConnectionDispatcher;
import data.PropertiesOfPeer;
import server.FileServer;
import justen.DirectoryHelper;

public class Peer {
	
	public static void main(String [ ] args)
	{
		final PropertiesOfPeer properties = new PropertiesOfPeer();
		final ClientStateManager clientStateManager = new ClientStateManager();
		DirectoryHelper.createAllDirectories(properties.folderPeerName);
		
		UserInputThread userInputThread = new UserInputThread();
		userInputThread.start();
	}
	
	

}
