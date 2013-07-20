package main;

import java.io.File;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Map.Entry;

import client.ClientStateManager;
import client.ConnectionDispatcher;

import data.FileWrapper;
import data.Message;
import data.Message.MESSAGE_TYPE;
import data.PropertiesOfPeer;
import server.FileServer;

public class UserInputThread extends Thread {
	FileServer fileServerThread;
	
	public UserInputThread() {
	}

	@Override
	public void run() {
		try {
			while (true){
				Scanner scanner = new Scanner (System.in);
				System.out.println("[** KEYBOARD INPUT **]	Please enter operation");  
				String input = scanner.next(); 
				
				if (input.equals("leave")) {
					if (PropertiesOfPeer.peerUp) {
						PropertiesOfPeer.peerUp = false;
					} else {
						System.out.println("[** SYSTEM NOTIFICATION **]	System is already shutdown");
					}
				}
				else if (input.equals("join")){
					if (!PropertiesOfPeer.peerUp) {
						// Turn the server on
						PropertiesOfPeer.peerUp = true;
						
						fileServerThread = new FileServer(PropertiesOfPeer.portNumber);
						fileServerThread.start();
						System.out.println("[** SYSTEM NOTIFICATION **]	Running file server thread");
						
						ConnectionDispatcher fileClientThread = new ConnectionDispatcher(PropertiesOfPeer.ipAddrPortNumMappingAll);
						fileClientThread.start();
						
						//Need one for status thread
						//Need one for checking for new files thread
					} else {
						System.out.println("[** SYSTEM NOTIFICATION **]	System is already up");
					}
				}
				
				else if (input.equals("request")){
					if (PropertiesOfPeer.peerUp) {
						
						System.out.println("[** KEYBOARD INPUT **]	Please enter file name");  
						String input2 = scanner.next(); 
						
						Iterator<Entry> it = PropertiesOfPeer.ipAddrPortNumMappingAlive.iterator();
						while (it.hasNext()) {
							Entry entry = it.next();
							
							ClientStateManager.GetFileFromOtherPeer((String)entry.getKey() + ":" + 
									Integer.toString((Integer)entry.getValue()), input2);
						}
						
					} else {
						System.out.println("[** SYSTEM NOTIFICATION **]	System is shut down");
					}
				}
				
				else if (input.equals("status")){
					PropertiesOfPeer.broadcastStatus();
				}
			}
		} catch (Exception e) {
			System.err.println("User console error");
		}
	}
	
}