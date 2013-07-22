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
		if (operation != 'r' && operation != 'w')
			return ReturnCode.INCORRECT_OPERATION;
		
		if (!fm.fileExists(fileName))
			return ReturnCode.FILE_DOES_NOT_EXIST;
		
		int v = fileName.lastIndexOf("_") + 2; 
		String vNum = fileName.substring(v, fileName.lastIndexOf("."));
		int versionNumber = Integer.parseInt(vNum);
		String fileNameProper = fileName.substring(0, fileName.indexOf("_"));
		String extension = fileName.substring(fileName.lastIndexOf("."));
		
		if (!fm.containsFileLocally(fileNameProper + extension, versionNumber)) {
			if (operation == 'w' && (otherDeviceHasReadLock(fileName) || !otherDeviceHasWriteLock(fileName)))
				return ReturnCode.OTHER_DEVICE_HAS_LOCK; // prevents downloading file if other device has lock
			
			if (operation == 'r' && otherDeviceHasWriteLock(fileName))
				return ReturnCode.OTHER_DEVICE_HAS_LOCK;
			
			
			String device = getDeviceForFile(fileNameProper, versionNumber);
			int port = Integer.parseInt(device.substring(device.indexOf(":") + 1, device.length()));
			
			if (!PropertiesOfPeer.CheckIfThisHostIsStillAlive(device.substring(0, device.indexOf(":")), port)) 
				return ReturnCode.HOST_NOT_ALIVE;
			
			ClientStateManager.GetFileFromOtherPeer(device, fileName);
			if (openFile(fileName, operation))
				PropertiesOfPeer.broadcastStatus();
			else
				return ReturnCode.OTHER_DEVICE_HAS_LOCK;
			
		}
		else {
			// we have file locally
			if (openFile(fileName, operation))
				PropertiesOfPeer.broadcastStatus();
			else
				return ReturnCode.OTHER_DEVICE_HAS_LOCK;
		}
		return 0;
	}
	
	public int close(String fileName) {
		if (!fm.fileExists(fileName)) 
			return ReturnCode.FILE_DOES_NOT_EXIST;
		if (!fm.isFileOpen(fileName))
			return ReturnCode.FILE_NOT_OPEN;
		
		return closeFile(fileName);
	}
	
	public int create(String fileName) {
		if (fm.fileExists(fileName))
			return ReturnCode.FILE_ALREADY_EXISTS;
		
		String properName = fileName.substring(0, fileName.indexOf("."));
		String extension = fileName.substring(fileName.indexOf("."));
		File file = new File(PropertiesOfPeer.folderPeerName + "\\" + properName + "_v0" + extension);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return ReturnCode.COULD_NOT_CREATE_FILE;
		}
		
		fm.addLocalFile(fileName);
		PropertiesOfPeer.broadcastStatus();
		return ReturnCode.SUCCESS;
	}
	
	public int delete(String fileName) {
		if (!fm.fileExists(fileName))
			return ReturnCode.FILE_DOES_NOT_EXIST;
		if (otherDeviceHasReadLock(fileName) || otherDeviceHasWriteLock(fileName))
			return ReturnCode.OTHER_DEVICE_HAS_LOCK;
		
		int v = fileName.lastIndexOf("_") + 2; 
		String vNum = fileName.substring(v, fileName.lastIndexOf("."));
		int versionNumber = Integer.parseInt(vNum);
		if (fm.containsFileLocally(fileName, versionNumber)) {
			// delete file
			fm.removeLocalFile(fileName, false);
		}
		
		PropertiesOfPeer.deleteFileBroadcast(fileName);
		PropertiesOfPeer.broadcastStatus();
		return ReturnCode.SUCCESS;
	}
	
	public int deleteAll(String fileName) {
		if (fm.getVersionMap().containsKey(fileName))
			return ReturnCode.FILE_DOES_NOT_EXIST;
		// check if any of the devices have a lock on any of the files
		int numVersions = fm.getVersionMap().get(fileName).size();
		String properName = fileName.substring(0, fileName.lastIndexOf("."));
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		for (int i = 0; i < numVersions; i++) {
			if (anyDeviceHasLockOnFile(properName, extension, i))
				return ReturnCode.OTHER_DEVICE_HAS_LOCK;
		}
		fm.removeLocalFile(fileName, true);
		
		PropertiesOfPeer.deleteFileAllVersionsBroadcast(fileName);
		PropertiesOfPeer.broadcastStatus();
		return ReturnCode.SUCCESS;
	}
	
	private int closeFile(String fileName) {
		if (!fm.getLockMap().containsKey(fileName)) 
			return ReturnCode.FILE_DOES_NOT_EXIST;
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
				return ReturnCode.GO_FUCK_YOURSELF;
		}
		return ReturnCode.SUCCESS;
			
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
			if (s == null)
				continue;
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
			if (s == null)
				continue;
			if (s.lockMap.containsKey(fileName))
			{
				if (s.lockMap.get(fileName) != null && s.lockMap.get(fileName) == WriterLock.getInstance())
					return true;
			}
		}
		return false;
	}
}
