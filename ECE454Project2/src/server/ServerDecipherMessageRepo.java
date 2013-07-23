package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import client.ClientStateManager;
import client.ConnectionDispatcher;
import client.ConnectionInstance;
import justen.Status;
import data.FileWrapper;
import data.Message;
import data.MyEntry;
import data.Message.MESSAGE_TYPE;
import data.PropertiesOfPeer;

public class ServerDecipherMessageRepo {

	public static Message DecipherMessageAndReturn(Message incomingMessage) throws Exception {

		Message.MESSAGE_TYPE type = incomingMessage.getType();
		Message returnMessage = null;

		if (type.equals(Message.MESSAGE_TYPE.FILE)) {
			//Call file storage and reconstruction stuff
			//This is just temp.
			FileWrapper fileWrapper = (FileWrapper) incomingMessage.getData();
			File file = new File(PropertiesOfPeer.folderPeerName + "//" + fileWrapper.getFileName());
			byte[] content = fileWrapper.getContent();
			
			
			try {
				Files.write(file.toPath(), content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if (type.equals(Message.MESSAGE_TYPE.FILE_REQUEST)) {
			String fileName = (String) incomingMessage.getData();
			File file = new File(PropertiesOfPeer.folderPeerName + "//" + fileName);
			
			byte[] content = null;
			
			try {
				content = Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				System.err.println("File not found");
				return null;
			}
			
			FileWrapper fileWrapper = new FileWrapper(file.getName(), content);
			
			Message fileMessage = new Message (PropertiesOfPeer.ipAddress, PropertiesOfPeer.portNumber, MESSAGE_TYPE.FILE, fileWrapper);
			ClientStateManager.AddNewMessageToQueue(incomingMessage.getIpAddress() + ":" + incomingMessage.getPortNumber(), fileMessage);
		}
		
		else if (type.equals(Message.MESSAGE_TYPE.STATUS_UPDATE)) {
			String theirIpAddress = incomingMessage.getIpAddress();
			int theirPortNumber = incomingMessage.getPortNumber();
			
			if (!PropertiesOfPeer.CheckIfThisHostIsStillAlive(theirIpAddress, theirPortNumber) && !PropertiesOfPeer.firstTimeStarting){
				ConnectionInstance newConnectionInstance = new ConnectionInstance(theirIpAddress, theirPortNumber);
				newConnectionInstance.start();
				PropertiesOfPeer.AddEntryToIPAddrPortNumMappingAlive(theirIpAddress, theirPortNumber);
			}
			
			PropertiesOfPeer.updateOtherPeersStatus(incomingMessage);
			PropertiesOfPeer.firstTimeStarting = false;
		}
		else if (type.equals(Message.MESSAGE_TYPE.DELETEFILE)) {
			//CALL DELETE ONE FILE
			String fileName = (String) incomingMessage.getData();
			if (!PropertiesOfPeer.fileManager.deleteSingleFile(fileName)){
				
			}
		}
		else if (type.equals(Message.MESSAGE_TYPE.DELETEALLVERSIONSOFFILE)) {
			//CALL DELETE ALL VERSIONS OF FILE
			String fileName = (String) incomingMessage.getData();
			PropertiesOfPeer.fileManager.deleteAllVersionsOfFile(fileName);
		}
		else if (type.equals(Message.MESSAGE_TYPE.ADDNEWDEVICE)) {
			String deviceName = (String) incomingMessage.getData();
			String hostName = deviceName.substring(0, deviceName.indexOf(':'));
			int portNumber = Integer.parseInt(deviceName.substring(deviceName.indexOf(':') + 1, deviceName.length()));
			Map.Entry<String, Integer> entry1 = new MyEntry<String, Integer>(hostName, portNumber);
			PropertiesOfPeer.ipAddrPortNumMappingAll.add(entry1);
			ConnectionInstance connection = new ConnectionInstance(hostName, portNumber);
			connection.start();
		}
		else {
			System.err.println("Unknown message type... You fucked up!");
		}

		return returnMessage;
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