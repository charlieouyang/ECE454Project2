package server;

import data.Message;
import data.Message.MESSAGE_TYPE;
import data.PropertiesOfPeer;

public class ServerDecipherMessageRepo {

	public static Message DecipherMessageAndReturn(Message incomingMessage) {

		Message.MESSAGE_TYPE type = incomingMessage.getType();
		Message returnMessage = null;

		if (type.equals(Message.MESSAGE_TYPE.PEER_LEAVING)) {
			returnMessage = ReceiveClosingConnectionFromPeer(incomingMessage);
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