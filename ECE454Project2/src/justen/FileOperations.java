package justen;

import java.util.ArrayList;
import java.util.Map.Entry;

import data.PropertiesOfPeer;

public class FileOperations 
{
	FileManager fm;
	
	public FileOperations(FileManager fm) {
		this.fm = fm;
		
	}
	
	public int open(String fileName, char operation) {
		if (operation != 'r' || operation != 'w')
			return -1;
		
		if (!fm.fileExists(fileName))
			return -1;
		
		int v = fileName.lastIndexOf("_") + 2; 
		String vNum = fileName.substring(v, fileName.lastIndexOf("."));
		int versionNumber = Integer.parseInt(vNum);
		String fileNameProper = fileName.substring(0, fileName.indexOf("_"));
		
		if (!fm.containsFileLocally(fileName, versionNumber)) {
			if (operation == 'w' && (otherDeviceHasReadLock(fileName) || !otherDeviceHasWriteLock(fileName)))
				return -1; // prevents downloading file if other device has lock
			
			if (operation == 'r' && otherDeviceHasWriteLock(fileName))
				return -1;
			
			
			String device = getDeviceForFile(fileNameProper, versionNumber);
			int port = Integer.parseInt(device.substring(device.indexOf(":") + 1, device.length()));
			
			// check if device is online
			if (!PropertiesOfPeer.CheckIfThisHostIsStillAlive(device.substring(0, device.indexOf(":")), port)) 
				return -1;
			
			// TODO: get file from peer
//			if (openFile(fileName, operation))
//				PropertiesOfPeer.broadcastStatus();
//			else
//				return -1;
			
		}
		else {
			// we have file locally
			if (openFile(fileName, operation))
				PropertiesOfPeer.broadcastStatus();
			else
				return -1;
		}
			
		
		return 0;
	}
	
	public int close(String fileName) {
		if (!fm.fileExists(fileName)) 
			return -1;
		if (!fm.isFileOpen(fileName))
			return 1;
		
		return closeFile(fileName);
	}
	
	public int read(String fileName, char[] buf, int offset, int bufsize) {
		return 0;
	}
	
	private int closeFile(String fileName) {
		if (!fm.getLockMap().containsKey(fileName)) 
			return -1;
		else {
			if (fm.getLockMap().get(fileName) instanceof ReaderLock) {
				fm.getLockMap().put(fileName, null);
				fm.closeFile(fileName);
				PropertiesOfPeer.broadcastStatus();
			}
			else if (fm.getLockMap().get(fileName) instanceof WriterLock) {
				// TODO: save file
				fm.getLockMap().put(fileName, null);
				fm.closeFile(fileName);
				PropertiesOfPeer.broadcastStatus();
			}
			else // lock was null wtf?
				return -1;
		}
		return 0;
			
	}
	
	private String getDeviceForFile(String fileName, int versionNumber) {
		for (Entry<String, Status> e : PropertiesOfPeer.deviceAndStatusMap.entrySet()) {
			Status s = e.getValue();
			if (s.fileVersionMap.containsKey(fileName)) {
				if (s.fileVersionMap.get(fileName).get(versionNumber) == 1)
					return e.getKey();
			}
		}
		return "";
	}
	
	private boolean openFile(String fileName, char operation) {
		if (otherDeviceHasWriteLock(fileName))
			return false;
		if (operation == 'r') {
			fm.setLock(fileName, ReaderLock.getInstance());
		}
		if (operation == 'w') {
			if (otherDeviceHasReadLock(fileName))
				return false;
			fm.setLock(fileName, WriterLock.getInstance());
		}
		fm.openFile(fileName);
		return true;
	}
	
	private boolean otherDeviceHasReadLock(String fileName) {
		for (Entry<String, Status> e : PropertiesOfPeer.deviceAndStatusMap.entrySet()) {
			Status s = e.getValue();
			if (s.lockMap.containsKey(fileName))
			{
				if (s.lockMap.get(fileName) != null && s.lockMap.get(fileName) == ReaderLock.getInstance())
					return true;
			}
		}
		return false;
	}
	
	private boolean otherDeviceHasWriteLock(String fileName) {
		for (Entry<String, Status> e : PropertiesOfPeer.deviceAndStatusMap.entrySet()) {
			Status s = e.getValue();
			if (s.lockMap.containsKey(fileName))
			{
				if (s.lockMap.get(fileName) != null && s.lockMap.get(fileName) == WriterLock.getInstance())
					return true;
			}
		}
		return false;
	}
}
