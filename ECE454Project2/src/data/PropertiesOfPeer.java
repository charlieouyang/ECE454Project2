package data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PropertiesOfPeer {
	//Global variables for the file server and client to access
	public static String ipAddress = "localhost";
	public static int portNumber = 2000;			//Port number of this host
	public static String PeerName = ipAddress + "." + portNumber;
	public static ArrayList<Entry> ipAddrPortNumMappingAll = new ArrayList<Entry>();
	public static ArrayList<Entry> ipAddrPortNumMappingAlive = new ArrayList<Entry>();
	public static boolean peerUp = false;
	
	//File management and synchronization stuff
	//public static ConcurrencyManager peerConcurrencyManager;
	//public static Status currentPeerStatus;
	//public static Hashtable<String, Status> listOfOtherPeersStatus;		//PeerName mapped to status
	ArrayList<String> listOfCompleteFiles;
	ArrayList<String> listOfIncompleteFiles;
	ArrayList<String> listOfChunks;
	
	//Chunk sending managment and stuff
	//public static Hashtable<String, TorrentMetaData> listOfFilesToGet;
	//public static ArrayList<String> fileListAlreadyGetting;
	//public static Hashtable<String, TorrentMetaData> inProcessOfGettingChunks;
	
	public PropertiesOfPeer(){
		//Stuff to keep track of files/chunks and other peers
		//peerConcurrencyManager = new ConcurrencyManager(PeerName);
		//currentPeerStatus = new Status(peerConcurrencyManager);
		//listOfOtherPeersStatus = new Hashtable<String, Status>();
		
		//listOfCompleteFiles = new ArrayList<String>();
		//listOfIncompleteFiles = new ArrayList<String>();
		//listOfChunks = new ArrayList<String>();
		//listOfFilesToGet = new Hashtable<String, TorrentMetaData>();		
		//fileListAlreadyGetting = new ArrayList<String>();
		//inProcessOfGettingChunks = new Hashtable<String, TorrentMetaData>();
		
		//List of ip address to port number mappings
		Map.Entry<String, Integer> entry1 = new MyEntry<String, Integer>("localhost", 1000);
		ipAddrPortNumMappingAll.add(entry1);
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
	
	
//	//This will be used to update the peer's status info
//	public static void updateCurrentPeerStatus(){
//		currentPeerStatus = new Status(peerConcurrencyManager);
//	}
//	
//	public static synchronized Status getCurrentPeerStatus(){
//		Status tempStatus = currentPeerStatus;
//		updateCurrentPeerStatus();
//		return tempStatus;
//	}
	
	
	
	/*
	
	//Receive status from another peer and update that info
	public static void updateOtherPeersStatus(Message incomingMessageStatusFromAnotherPeer){
		//Gotta make sure the data object is actually of the status class
		Status anotherPeerStatus = null;
		Object statusData = incomingMessageStatusFromAnotherPeer.getData();
		String senderName = incomingMessageStatusFromAnotherPeer.getSenderName();
		
		//Don't know if the class comparison thing is correct
		if (statusData != null && statusData instanceof Status){
			anotherPeerStatus = (Status) incomingMessageStatusFromAnotherPeer.getData();
			listOfOtherPeersStatus.put(senderName, anotherPeerStatus);
			
			//Update your local metaData file in accordance to response metaData
			Hashtable<String, TorrentMetaData> metaDataTable = anotherPeerStatus.allMetaData;
			UpdateThisPeerMetaDataTable(metaDataTable);
		}
		else{
			//Don't do anything because it's an unknown object...
			System.err.println("It's not a status object...");
		}
	}
	
	*/
	
	/*
	
	public static void UpdateThisPeerMetaDataTable(Hashtable<String, TorrentMetaData> listOfFilesAndMetaData){
		//This is replacing all existing entries and adding new ones
		
		if(currentPeerStatus.allMetaData == null){
			//System.out.println("Current size of allMetaDataForThisPeer : " + currentPeerStatus.allMetaData.size());
		}
		else{
			//System.out.println("It's not null");
		}

		Iterator<Map.Entry<String, TorrentMetaData>> otherPeersMetaData = listOfFilesAndMetaData.entrySet().iterator();

		while (otherPeersMetaData.hasNext()) {
			Map.Entry<String, TorrentMetaData> entry = otherPeersMetaData.next();
			if (!currentPeerStatus.allMetaData.containsKey(entry.getKey()))	{
				listOfFilesToGet.put(entry.getKey(), entry.getValue());
			}
		}

		peerConcurrencyManager.mergeMetaData(listOfFilesAndMetaData);
		currentPeerStatus.allMetaData.putAll(listOfFilesAndMetaData);
	}
	
	public static void broadcastStatus(){
		ClientBroadcastStatus statusBroadcastThread = new ClientBroadcastStatus(PropertiesOfPeer.ipAddrPortNumMappingAll);
		statusBroadcastThread.start();
	}
	
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
