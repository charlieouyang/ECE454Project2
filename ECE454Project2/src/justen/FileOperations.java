package justen;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import client.ClientStateManager;
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
			
			if (!PropertiesOfPeer.CheckIfThisHostIsStillAlive(device.substring(0, device.indexOf(":")), port)) 
				return -1;
			
			ClientStateManager.GetFileFromOtherPeer(device, fileName);
			if (openFile(fileName, operation))
				PropertiesOfPeer.broadcastStatus();
			else
				return -1;
			
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
	
	public int create(String fileName) { //charlie.docx
		if (fm.fileExists(fileName))
			return -1;
		
		String properName = fileName.substring(0, fileName.indexOf("."));
		String extension = fileName.substring(fileName.indexOf(".") + 1);
		File file = new File(properName + "_v1" + extension);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		
		//TODO: Update the file manager
		PropertiesOfPeer.broadcastStatus();
		return 0;
	}
	
	public int delete(String fileName) {
		if (!fm.fileExists(fileName))
			return -1;
		if (otherDeviceHasReadLock(fileName) || otherDeviceHasWriteLock(fileName))
			return -1;
		
		int v = fileName.lastIndexOf("_") + 2; 
		String vNum = fileName.substring(v, fileName.lastIndexOf("."));
		int versionNumber = Integer.parseInt(vNum);
		if (fm.containsFileLocally(fileName, versionNumber)) {
			// delete file
			fm.removeLocalFile(fileName, false);
		}
		
		//TODO: Handle delete broadcast from another peer
		PropertiesOfPeer.deleteFileBroadcast(fileName);
		PropertiesOfPeer.broadcastStatus();
		return 0;
	}
	
	public int deleteAll(String fileName) {
		if (fm.getVersionMap().containsKey(fileName))
			return -1;
		// check if any of the devices have a lock on any of the files
		int numVersions = fm.getVersionMap().get(fileName).size();
		String properName = fileName.substring(0, fileName.lastIndexOf("."));
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		for (int i = 0; i < numVersions; i++) {
			if (anyDeviceHasLockOnFile(properName, extension, i))
				return -1;
		}
		fm.removeLocalFile(fileName, true);
		
		
		//TODO: Handle delete broadcast from another peer
		PropertiesOfPeer.deleteFileAllVersionsBroadcast(fileName);
		PropertiesOfPeer.broadcastStatus();
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
				int v = fileName.lastIndexOf("_") + 2; 
				String vNum = fileName.substring(v, fileName.lastIndexOf("."));
				int versionNumber = Integer.parseInt(vNum);
				String properName = fileName.substring(0, v - 2);
				fm.getLockMap().put(fileName, null);
				fm.closeFile(fileName);
				fm.addLocalFileVersion(properName, versionNumber + 1);
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
	
	private boolean anyDeviceHasLockOnFile(String properFileName, String extension, int versionNumber) {
		for (Entry<String, Status> e : PropertiesOfPeer.deviceAndStatusMap.entrySet()) {
			Status s = e.getValue();
			if (s.lockMap.containsKey(properFileName + "_" + versionNumber + "." + extension)) {
				if (s.lockMap.get(properFileName + "_" + versionNumber + "." + extension) != null)
					return true;
			}
		}
		return false;
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
