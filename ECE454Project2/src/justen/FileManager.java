package justen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class FileManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashSet<String> localFiles;
	private HashSet<String> remoteFiles;
	private HashSet<String> openFiles;
	private HashMap<String, ArrayList<Integer>> versionMap;
	private HashMap<String, Lock> lockMap;
	
	private final Object lock = new Object();
	
	public FileManager() {
		localFiles = new HashSet<String>();
		remoteFiles = new HashSet<String>();
		openFiles = new HashSet<String>();
		versionMap = new HashMap<String, ArrayList<Integer>>();
		lockMap = new HashMap<String, Lock>();
	}
	
	/*
	 version map: test1.pdf : [0,0,1,1]
	 localFiles: test2_v0.pdf, test1_v3.pdf
	 remoteFiles = test1_v0.pdf, test1_v1.pdf
	 openFiles = test1_v0.pdf
	 */
	
	public HashSet<String> getLocalFiles() {
		return localFiles;
	}
	
	public HashSet<String> getRemoteFiles() {
		return remoteFiles;
	}
	
	public void openFile(String fileName) { // fileName = test1_v0.pdf
		openFiles.add(fileName);
	}
	
	/**
	 * 
	 * @param fileName (test1_v0.pdf)
	 * @return
	 */
	public boolean isFileOpen(String fileName) { // fileName = test1_v0.pdf
		return openFiles.contains(fileName);
	}
	
	public void closeFile(String fileName) { // fileName = test1_v0.pdf
		openFiles.remove(fileName);
	}
	
	public HashMap<String, ArrayList<Integer>> getVersionMap() {
		return versionMap;
	}
	
	public boolean fileExistsInVersionMap(String fileName) {// fileName = test1.pdf
		return versionMap.containsKey(fileName);
	}
	
	public ArrayList<String> getLogicalView() {
		ArrayList<String> allFiles = new ArrayList<String>();
		for (Entry<String, ArrayList<Integer>> e : versionMap.entrySet()) {
			String fileName = e.getKey();
			ArrayList<Integer> temp = e.getValue();
			String properName = fileName.substring(0, fileName.lastIndexOf("."));
			String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			for (int i = 0; i < temp.size(); i++) {
				allFiles.add(properName + "_v" + i + "." + extension);
			}
		}
		return allFiles;
	}
	
	public HashMap<String, Lock> getLockMap() {
		return lockMap;
	}
	
	// filename contains version num
	public void setLock(String fileName, Lock lock) { // fileName = test1.pdf
		lockMap.put(fileName, lock);
	}
	
	/**
	 * Returns type of lock on file. If null, no lock on file.
	 * @param fileName
	 * @return lock type
	 */
	public Lock getLockType(String fileName) {
		if (lockMap.containsKey(fileName))
			return lockMap.get(fileName);
		
		return null;
	}
	
	public void createNewFile(String fileName) { // fileName = test1.pdf
		String properName = fileName.substring(0, fileName.lastIndexOf("."));
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
		localFiles.add(properName + "_v0." + extension);
		ArrayList<Integer> bitString = new ArrayList<Integer>();
		bitString.add(1);
		versionMap.put(fileName, bitString);
	}
	
	public void processStatusUpdate(Status s) {
		synchronized(lock) {
			processVersionMap(s.fileVersionMap);
		}
	}
	
	private void processVersionMap(HashMap<String, ArrayList<Integer>> map) {
		
		for(String file : map.keySet()) { // all files other peer sees
			// file = charlie.pdf
			
			
			if (versionMap.containsKey(file)) { // i know of this file
				
			}
		}
		
		for (String file : map.keySet()) {
			if (!localFiles.contains(file) && !remoteFiles.contains(file)) {
				// we have the file, check versions
				ArrayList<Integer> remoteBitString = map.get(file);
				ArrayList<Integer> localBitString = versionMap.get(file);
				if (remoteBitString.size() > localBitString.size()) {
					// new version
					int j = localBitString.size();
					localBitString.ensureCapacity(remoteBitString.size());
					for (int i = j; i < localBitString.size(); i++) {
						localBitString.set(i, 0);
					}
					versionMap.put(file, localBitString);
				}
			}
			else
			{
			}
		}
	}
	
	public boolean saveNewFileVersion(String fileName, int versionNum) { // fileName = test1.pdf
		if (!versionMap.containsKey(fileName)) // should be create
			return false;

		ArrayList<Integer> temp = versionMap.get(fileName); // test1.pdf=[0,0,1,1]
		if (temp.size() >= versionNum) // shouldn't happen
			temp.set(versionNum, 1);
		else // higher version num
			temp.add(1); // assume that there is time between new file saves
		versionMap.put(fileName, temp);
		return true;
	}
	
	public boolean deleteSingleFile(String fileName) { // fileName = test1_v0.pdf
		if (!localFiles.contains(fileName))
			return false;
		
		int vNum = getVersionNumberFromFile(fileName);
		
		// ok we have file, let's remove from local and from version map
		localFiles.remove(fileName);
		ArrayList<Integer> temp = versionMap.get(getProperName(fileName));
		temp.set(vNum, null);
		
		if (!allFilesDeleted(temp))
			versionMap.put(getProperName(fileName), temp);
		else
			versionMap.remove(getProperName(fileName));
		return true;
	}
	
	private boolean allFilesDeleted(ArrayList<Integer> bitString) {
		boolean allGone = true;
		for (int i = 0; i < bitString.size(); i++) {
			if (bitString.get(i) != null) {
				allGone = false;
				break;
			}
		}
		return allGone;
	}
	
	public boolean deleteAllVersionsOfFile(String fileName) { // fileName = test1.pdf
		ArrayList<String> versionsToDelete = new ArrayList<String>();
		for (String file : localFiles) {
			String temp = getProperName(file);
			if (fileName.equals(temp)) 
				versionsToDelete.add(file);
		}
		
		if (versionsToDelete.size() != 0) {
			for (String file : versionsToDelete)
				localFiles.remove(file);
		}
		
		versionMap.remove(fileName);
		return true;
	}
	
	/**
	 * Takes in filename with version and gives just file name
	 * @param fileName (test1_v0.pdf)
	 * @return test1.pdf
	 */
	public static String getProperName(String fileName) {
		String properName = fileName.substring(0, fileName.lastIndexOf("_")); //test1
		String extension = fileName.substring(fileName.lastIndexOf(".")); //.pdf
		return properName + extension;
	}
	
	public static int getVersionNumberFromFile(String fileName) {
		return Integer.parseInt(
				fileName.substring(fileName.lastIndexOf("_v") + 2, 
				fileName.lastIndexOf(".")));
	}
	
	/**
	 * 
	 * @param fileName (test1_v0.pdf)
	 * @return
	 */
	public boolean fileExists(String fileName) { // fileName = test1_v0.pdf
		return localFiles.contains(fileName) || remoteFiles.contains(fileName);
	}
	
	public void addRemoteFile(String fileName) { // fileName = test1_v0.pdf
		remoteFiles.add(fileName);
	}
	
	/**
	 * 
	 * @param fileName (test1_v0.pdf)
	 * @return
	 */
	public boolean containsFileLocally(String fileName) { // fileName = test1_v0.pdf
		return localFiles.contains(fileName);
	}
	
	public boolean containsFileRemotely(String filename) {
		return remoteFiles.contains(filename);
	}
}
