package main;

import java.util.ArrayList;
import java.util.Scanner;
import justen.ReturnCode;
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

				else if (input.equals("open")) {
//					System.out
//							.println("[** KEYBOARD INPUT **]	Please enter file name");
					String fileName = scanner.next();
//					System.out
//							.println("[** KEYBOARD INPUT **]	Please enter file operation (r/w)");
					char operation = scanner.next().charAt(0);

					int returnValue = PropertiesOfPeer.fileOperations.open(
							fileName, operation);
					if (returnValue != 0) {
						System.out.println(ReturnCode.getReturnCodeMeaning(returnValue));
					}
					else{
						System.out.println("File open success!");
					}
				}

				else if (input.equals("close")) {
//					System.out
//							.println("[** KEYBOARD INPUT **]	Please enter file name");
					String fileName = scanner.next();

					int returnValue = PropertiesOfPeer.fileOperations
							.close(fileName);

					if (returnValue != 0) {
						System.out.println(ReturnCode.getReturnCodeMeaning(returnValue));
					}
					else{
						System.out.println("File closing success!");
					}
				}

				else if (input.equals("create")) {
//					System.out.println("[** KEYBOARD INPUT **]	Please enter new file name");
					String fileName = scanner.next();

					int returnValue = PropertiesOfPeer.fileOperations.create(fileName);

					if (returnValue != 0) {
						System.out.println(ReturnCode.getReturnCodeMeaning(returnValue));
					}
					else{
						System.out.println("File creating success!");
					}

				}

				else if (input.equals("logicalview")) {

					ArrayList<String> output = PropertiesOfPeer.fileManager
							.getLogicalView();
					String outputString = "";
					for (String fileName : output) {
						outputString += fileName + "\n";
					}

					System.out.println(outputString);
						
				}
				
				else if (input.equals("localview")) {
					for (String fileName : PropertiesOfPeer.fileManager.getLocalFiles())
						System.out.println(fileName + "\n");
				}
				
				else if (input.equals("remoteview")) {
					for (String fileName : PropertiesOfPeer.fileManager.getRemoteFiles())
						System.out.println(fileName + "\n");
				}

				else if (input.equals("delete")) {
//					System.out
//							.println("[** KEYBOARD INPUT **]	Please enter file name you want to delete");
					String fileName = scanner.next();

					// Call Pinto's delete method
					int returnValue = PropertiesOfPeer.fileOperations.delete(fileName);
					
					if (returnValue != 0) {
						System.out.println(ReturnCode.getReturnCodeMeaning(returnValue));
					}
					else{
						System.out.println("File deletion success!");
					}
				}
				
				else if (input.equals("deleteall")) {
					String fileName = scanner.next();
					
					int returnValue = PropertiesOfPeer.fileOperations.deleteAll(fileName);
					
					if (returnValue != 0) {
						System.out.println(ReturnCode.getReturnCodeMeaning(returnValue));
					}
					else {
						System.out.println("All file versions deleted sucessfully!");
					}
				}
			}
		} catch (Exception e) {
			System.err.println("User console error");
		}
	}
	
}