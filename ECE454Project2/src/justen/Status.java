package justen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Status implements Serializable {
	
	private FileManager fileManager;
	
	public HashMap<String, ArrayList<Integer>> fileVersionMap; // fileName, 0001010: bit string for stable file verions
	public HashMap<String, Lock> lockMap;
	
	public Status(FileManager fm) {
		fileVersionMap = fm.getAllFiles();
		lockMap = fm.getLockMap();
	}
}
