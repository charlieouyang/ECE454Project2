package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import client.ClientStateManager;
import data.Message.MESSAGE_TYPE;
import justen.FileManager;
import justen.FileOperations;
import justen.Status;

public class PropertiesOfPeer {
	//Global variables for the file server and client to access
	public static String ipAddress = "localhost";
	public static int portNumber = 2000;			//Port number of this host
	public static String PeerName = ipAddress + ":" + portNumber;
	public static String folderPeerName = ipAddress + "." + portNumber;
	public static boolean peerUp = false;
	public static boolean firstTimeStarting = true;
	
	public static ArrayList<Entry> ipAddrPortNumMappingAll = new ArrayList<Entry>();
	public static ArrayList<Entry> ipAddrPortNumMappingAlive = new ArrayList<Entry>();
	public static HashMap<String, Status> deviceAndStatusMap = new HashMap<String, Status>();
	
	//File Management
	public static FileManager fileManager = new FileManager();
	public static FileOperations fileOperations = new FileOperations(fileManager);
	
	public PropertiesOfPeer(){
		//List of ip address to port number mappings
		Map.Entry<String, Integer> entry1 = new MyEntry<String, Integer>("localhost", 1000);
		ipAddrPortNumMappingAll.add(entry1);
		
		InitializeDeviceAndStatusMap();
	}
	
	private static void InitializeDeviceAndStatusMap(){
		Iterator<Entry> it = ipAddrPortNumMappingAll.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			Status status = null;
			
			deviceAndStatusMap.put((String)entry.getKey() + ":" + Integer.toString((Integer)entry.getValue()), status);
		}
	}
	
	public static void AddEntryToIPAddrPortNumMappingAlive(String ipAddress, int portNumber){
		Map.Entry<String, Integer> entry = new MyEntry<String, Integer>(ipAddress, portNumber);
		ipAddrPortNumMappingAlive.add(entry);
	}
	
	public static void RemoveEntryFromIPAddrPortNumMappingAlive(String receivedIpAddress, int receivedPortNumber){
		//Map.Entry<String, Integer> entry = new MyEntry<String, Integer>(ipAddress, portNumber);
		//ipAddrPortNumMappingAlive.remove(entry);
		
		Iterator<Entry> it = ipAddrPortNumMappingAlive.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			if (entry.getKey().equals(receivedIpAddress)
					&& entry.getValue().equals(receivedPortNumber)) {
				it.remove();
			}
		}
		System.out.println("Removed entry from Alive list");
	}
	
	public static boolean CheckIfThisHostIsStillAlive(String receivedIpAddress, int receivedPortNumber){
		//Map.Entry<String, Integer> entry = new MyEntry<String, Integer>(ipAddress, portNumber);
		//ipAddrPortNumMappingAlive.remove(entry);
		
		boolean stillAlive = false;
		
		Iterator<Entry> it = ipAddrPortNumMappingAlive.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			if (entry.getKey().equals(receivedIpAddress)
					&& entry.getValue().equals(receivedPortNumber)) {
				
				stillAlive = true;
			}
		}
		return stillAlive;
	}
	
	public static void ShutDownThisPeer(){
		peerUp = false;

		Iterator<Entry> it = ipAddrPortNumMappingAlive.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			it.remove();
		}
	}
	
	public static void StartThisPeer(){
		peerUp = true;
	}
	
	//Receive status from another peer and update that info
	public static void updateOtherPeersStatus(Message incomingMessageStatusFromAnotherPeer){
		//Gotta make sure the data object is actually of the status class
		Status anotherPeerStatus = null;
		Object statusData = incomingMessageStatusFromAnotherPeer.getData();
		String senderName = incomingMessageStatusFromAnotherPeer.getIpAddress() 
				+ ":" + Integer.toString(incomingMessageStatusFromAnotherPeer.getPortNumber());
		
		//Don't know if the class comparison thing is correct
		if (statusData != null && statusData instanceof Status){
			anotherPeerStatus = (Status) statusData;
			deviceAndStatusMap.put(senderName, anotherPeerStatus);
			fileManager.processStatusUpdate(anotherPeerStatus);
		}
		else{
			//Don't do anything because it's an unknown object...
			System.err.println("It's not a status object...");
		}
	}
	
	public static void broadcastStatus(){
		Iterator<Entry> it = ipAddrPortNumMappingAlive.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			Message statusBroadcastMessage = new Message(PropertiesOfPeer.ipAddress, PropertiesOfPeer.portNumber, MESSAGE_TYPE.STATUS_UPDATE, getCurrentStatus());
			ClientStateManager.AddNewMessageToQueue((String)entry.getKey() + ":" + Integer.toString((Integer)entry.getValue()), statusBroadcastMessage);
		}
	}
	
	public static Status getCurrentStatus(){
		return new Status(fileManager);
	}
	
	public static void deleteFileBroadcast(String fileName){
		Iterator<Entry> it = ipAddrPortNumMappingAlive.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			Message deleteFileBroadcastMessage = new Message(PropertiesOfPeer.ipAddress, PropertiesOfPeer.portNumber, MESSAGE_TYPE.DELETEFILE, fileName);
			ClientStateManager.AddNewMessageToQueue((String)entry.getKey() + ":" + Integer.toString((Integer)entry.getValue()), deleteFileBroadcastMessage);
		}
	}
	
	public static void deleteFileAllVersionsBroadcast(String fileName){
		Iterator<Entry> it = ipAddrPortNumMappingAlive.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			Message deleteFileBroadcastMessage = new Message(PropertiesOfPeer.ipAddress, PropertiesOfPeer.portNumber, MESSAGE_TYPE.DELETEALLVERSIONSOFFILE, fileName);
			ClientStateManager.AddNewMessageToQueue((String)entry.getKey() + ":" + Integer.toString((Integer)entry.getValue()), deleteFileBroadcastMessage);
		}
	}
	
	public static void addNewDeviceBroadcast(String device){
		Iterator<Entry> it = ipAddrPortNumMappingAlive.iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			Message deleteFileBroadcastMessage = new Message(PropertiesOfPeer.ipAddress, PropertiesOfPeer.portNumber, MESSAGE_TYPE.ADDNEWDEVICE, device);
			ClientStateManager.AddNewMessageToQueue((String)entry.getKey() + ":" + Integer.toString((Integer)entry.getValue()), deleteFileBroadcastMessage);
		}
	}
	
	/*
	public static void printStatusInformation() {
		if (currentPeerStatus.fileNameIndexMap.size() == 0) {
			System.out.println("No files on this peer.");
			return;
		}
		System.out.println("Print Peer File Information..");
		for (Entry<String, Integer> e : currentPeerStatus.fileNameIndexMap.entrySet()) {
			System.out.println("Printing information for file: " + e.getKey());
			System.out.println("Local Replication: " + currentPeerStatus.fractionPresentLocally(e.getValue()));
			System.out.println("System Level Replication: " + currentPeerStatus.fractionPresent(e.getValue()));
			System.out.println("Least Replicated Chunk: " + currentPeerStatus.minimumReplicationLevel(e.getValue()));
			System.out.println("Average Weighted Replication Level: " + currentPeerStatus.averageReplicationLevel(e.getValue()));
		}
	}
	*/
}
