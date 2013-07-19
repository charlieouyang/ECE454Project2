package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import data.FileWrapper;
import data.Message;
import data.Message.MESSAGE_TYPE;
import data.PropertiesOfPeer;

public class ServerDecipherMessageRepo {

	public static Message DecipherMessageAndReturn(Message incomingMessage) {

		Message.MESSAGE_TYPE type = incomingMessage.getType();
		Message returnMessage = null;

		if (type.equals(Message.MESSAGE_TYPE.FILE)) {
			//Call file storage and reconstruction stuff
			
			//This is just temp.
			FileWrapper fileWrapper = (FileWrapper) incomingMessage.getData();
			File file = new File("C://test//" + fileWrapper.getFileName());
			byte[] content = fileWrapper.getContent();
			
			
			try {
				Files.write(file.toPath(), content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		else {
			System.err.println("Unknown message type... You fucked up!");
		}

		return returnMessage;
	}

	public static Message ReceiveBroadcastStatusFromPeer(Message broadcastReturn) {
		String ipAddress = broadcastReturn.getIpAddress();
		int portNumber = broadcastReturn.getPortNumber();
		PropertiesOfPeer.AddEntryToIPAddrPortNumMappingAlive(ipAddress,
				portNumber);
		return null;
	}

	public static Message GetClosingConnectionMessage() {
		String ipAddress = PropertiesOfPeer.ipAddress;
		int portNumber = PropertiesOfPeer.portNumber;
		Message closingMessage = new Message(ipAddress, portNumber,
				Message.MESSAGE_TYPE.PEER_LEAVING, null);
		return closingMessage;
	}

	public static Message ReceiveClosingConnectionFromPeer(
			Message closingMessage) {
		String ipAddress = closingMessage.getIpAddress();
		int portNumber = closingMessage.getPortNumber();
		PropertiesOfPeer.RemoveEntryFromIPAddrPortNumMappingAlive(ipAddress,
				portNumber);
		return null;
	}

}