package client;

import data.Message;
import data.PropertiesOfPeer;
import data.Message.MESSAGE_TYPE;

public class GetFileManager {
	
	public static int GetFileFromOtherPeer(String peerName, String fileName){
		Message fileRequest = new Message (PropertiesOfPeer.ipAddress, PropertiesOfPeer.portNumber, MESSAGE_TYPE.FILE_REQUEST, fileName);
		ClientStateManager.AddNewMessageToQueue(peerName, fileRequest);
		return 0;
	}
}
