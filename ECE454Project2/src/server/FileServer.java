package server;

import java.io.IOException;
import java.net.ServerSocket;

import data.PropertiesOfPeer;

public class FileServer extends Thread {
	private int portNumber;							//This is the port number that THIS host will run on
	private ServerSocket serverSocket = null;

	public FileServer(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public void stopTheThread() throws IOException{
		if (serverSocket != null){
			serverSocket.close();
		}
		//Thread.currentThread().interrupt();
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(portNumber);

			// make this false when you want to disconnect the host
			while (PropertiesOfPeer.peerUp) {
				new FileServerThreadWorkDispatcher(serverSocket.accept()).start();
			}

			// Is this still neccessary since we're closing the socket
			// from the thread???
			if (serverSocket != null){
				serverSocket.close();
			}
		} catch (IOException e) {
			
		}
	}
}
