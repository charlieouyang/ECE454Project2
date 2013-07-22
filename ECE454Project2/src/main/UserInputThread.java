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
				
				else if (input.equals("open")){
					if (PropertiesOfPeer.peerUp) {
						
						System.out.println("[** KEYBOARD INPUT **]	Please enter file name");  
						String fileName = scanner.next(); 
						System.out.println("[** KEYBOARD INPUT **]	Please enter file operation (r/w)");  
						char operation = scanner.next().charAt(0); 
						
						PropertiesOfPeer.fileOperations.open(fileName, operation);
						
					} else {
						System.out.println("[** SYSTEM NOTIFICATION **]	System is shut down");
					}
				}
				
				else if (input.equals("close")){
					if (PropertiesOfPeer.peerUp) {
						
						System.out.println("[** KEYBOARD INPUT **]	Please enter file name");  
						String fileName = scanner.next(); 
						
						PropertiesOfPeer.fileOperations.close(fileName);
						
					} else {
						System.out.println("[** SYSTEM NOTIFICATION **]	System is shut down");
					}
				}
				
				else if (input.equals("create")){
					if (PropertiesOfPeer.peerUp) {
						
						System.out.println("[** KEYBOARD INPUT **]	Please enter new file name");  
						String fileName = scanner.next(); 
						
						PropertiesOfPeer.fileOperations.create(fileName);
						
					} else {
						System.out.println("[** SYSTEM NOTIFICATION **]	System is shut down");
					}
				}
				
				else if (input.equals("delete")){
					if (PropertiesOfPeer.peerUp) {
						
						System.out.println("[** KEYBOARD INPUT **]	Please enter file name you want to delete");  
						String fileName = scanner.next(); 
						
						//Call Pinto's delete method
						PropertiesOfPeer.fileOperations.delete(fileName);
						
					} else {
						System.out.println("[** SYSTEM NOTIFICATION **]	System is shut down");
					}
				}
			}
		} catch (Exception e) {
			System.err.println("User console error");
		}
	}
	
}