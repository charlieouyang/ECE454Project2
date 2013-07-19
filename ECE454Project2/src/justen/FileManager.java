package justen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FileManager {
	private HashSet<String> localFiles;
	private HashSet<String> remoteFiles;
	private HashMap<String, ArrayList<Integer>> versionMap;
	
	public FileManager() {
		localFiles = new HashSet<String>();
		remoteFiles = new HashSet<String>();
		versionMap = new HashMap<String, ArrayList<Integer>>();
	}
	
	public HashSet<String> getLocalFiles() {
		return localFiles;
	}
	
	public HashSet<String> getRemoteFiles() {
		return remoteFiles;
	}
	
	public HashMap<String, ArrayList<Integer>> getAllFiles() {
		return versionMap;
	}
	
	public void addLocalFile(String fileName) {
		localFiles.add(fileName);
		ArrayList<Integer> bitString = new ArrayList<Integer>(1);
		bitString.set(0, 1);
		versionMap.put(fileName, bitString);
	}
	
	public void processStatusUpdate(HashMap<String, Integer> map) {
		for (String file : map.keySet()) {
			
		}
	}	
	
	public boolean addLocalFileVersion(String fileName, int versionNum) {
		if (!versionMap.containsKey(fileName))
			return false;

		ArrayList<Integer> temp = versionMap.get(fileName);
		if (temp.size() >= versionNum)
			temp.set(versionNum, 1);
		else // higher version num
			temp.add(1); // assume that there is time between new file saves
		versionMap.put(fileName, temp);
		
		return true;
	}
	
	public void addRemoteFile(String fileName) {
		remoteFiles.add(fileName);
	}
	
	public boolean containsFileLocally(String fileName, int versionNum) {
		if (!versionMap.containsKey(fileName))
			return false;
		else
		{
			ArrayList<Integer> temp = versionMap.get(fileName);
			if (versionNum > temp.size())
				return false;
			return (temp.get(versionNum) == 1);
		}
	}
	
	public boolean containsFileRemotely(String filename) {
		return remoteFiles.contains(filename);
	}	
}
