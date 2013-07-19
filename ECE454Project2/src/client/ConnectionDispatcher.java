package client;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

//This class will spawn all of the connection threads that will connect to other hosts

public class ConnectionDispatcher extends Thread {
	private ArrayList<Entry> ipAddrPortNumMapping;
	
	public ConnectionDispatcher(ArrayList<Entry> ipAddrPortNumMapping) {
		this.ipAddrPortNumMapping = ipAddrPortNumMapping;
	}
	
	@Override
	public void run() {
		try {
			//Do a broadcast for Peer status		    
		    for (Map.Entry<String, Integer> entry : ipAddrPortNumMapping) {
		    	String ipAddress = entry.getKey();
		        int portNumber = entry.getValue();
		        
		        ConnectionInstance clientConnectionThread = new ConnectionInstance(ipAddress, portNumber);
		        clientConnectionThread.start();
		    }
		} catch (Exception e) {
			System.err.println("Screwed up on FileClient");
			e.printStackTrace();
		}
	}

}
