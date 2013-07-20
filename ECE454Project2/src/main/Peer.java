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
		
		//Still need to do...
		/*
		 * 1) Create a file
		 * 2) Read in a file/write to a file
		 * 3) Delete a file
		 * 4) Tagging... that's all you Pinto
		 * 5) Changing a file locally (From navigating to the folder and changing a file) will update the system
		 */
	}
	
	

}
