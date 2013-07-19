package server;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import data.Message;
import data.Message.MESSAGE_TYPE;
import data.PropertiesOfPeer;

public class FileServerThreadWorkDispatcher extends Thread {
	private Socket socket = null;

    public FileServerThreadWorkDispatcher(Socket socket) {
		this.socket = socket;
    }

    //This thread will do 2 things
    //1) Sending out list of files when requested
    //2) Sending requested files
    @Override
	public void run() {

		try {
		    
			//Take client input and see what they want
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());		
			boolean connectionUp = true;
			
			
			
			while (connectionUp) {
				connectionUp = PropertiesOfPeer.peerUp;
				System.out.println("waiting for message");
				Message incomingMessage = (Message) objectInputStream.readObject();
				
				System.out.println("Got a message of type: " + incomingMessage.getType().toString());
				System.out.println("Content: " + incomingMessage.getData().toString());
				System.out.println("");
				
				if (incomingMessage.getType() == MESSAGE_TYPE.PEER_LEAVING){
					String ipAddress = incomingMessage.getIpAddress();
					int portNumber = incomingMessage.getPortNumber();
					PropertiesOfPeer.RemoveEntryFromIPAddrPortNumMappingAlive(ipAddress, portNumber);
					break;
				}
				
//				// Decipher to see what the client wants
//				if (incomingMessage != null) {
//					Message returnMessage = ServerDecipherMessageRepo
//							.DecipherMessageAndReturn(incomingMessage);
//
//					if (returnMessage != null) {
//						String ipAddress = incomingMessage.getIpAddress();
//						int portNumber = incomingMessage.getPortNumber();
//
//						// Send back a response
//						// FileServerThreadSendMessage sendMessageThread = new
//						// FileServerThreadSendMessage(ipAddress, portNumber,
//						// returnMessage);
//						// sendMessageThread.start();
//
//					} else {
//						// No message to return
//					}
//				} else {
//					// do nothing
//				}

			}
			
			
			
			//Only after other peer goes offline
			objectInputStream.close();
		    socket.close();
	
		} catch (IOException e) {
			System.out.println("Connection from client closed");
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
    }
    
}
