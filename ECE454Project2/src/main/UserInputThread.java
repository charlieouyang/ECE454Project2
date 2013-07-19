package main;

import java.util.Scanner;

import client.ConnectionDispatcher;

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
						// Shutdown the server!
						//CloseThisConnectionThread closePeerThread = new CloseThisConnectionThread();
						//closePeerThread.start();
						PropertiesOfPeer.peerUp = false;
						//fileServerThread.stopTheThread();
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
				
				
				
//				else if (input.equals("insert")){
//					System.out.println("[** KEYBOARD INPUT **]	Please enter full file name");
//					String fileName = scanner.next(); 
//					
//					if (fileName.equals("end")){
//						continue;
//					}
//					PropertiesOfPeer.peerConcurrencyManager.insertFile(fileName);
//					PropertiesOfPeer.updateCurrentPeerStatus();
//					if (PropertiesOfPeer.peerUp) {
//						PropertiesOfPeer.broadcastStatus();
//						// Refresh and broadcast status
//					}
//				}
//				else if (input.equals("query")) {
//					PropertiesOfPeer.printStatusInformation();
//				}
				
				
				
				
			}
		} catch (Exception e) {
			System.err.println("User console error");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void InitializeSystem(){
		
	}
}