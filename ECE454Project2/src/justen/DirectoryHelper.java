package justen;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class DirectoryHelper {
	
	public static boolean createAllDirectories(String peerName) {
		return createDirectory(peerName);
	}
	
	public static boolean createDirectory(String name)
	{
		File dir = new File(name);
		if (dir.isDirectory())
			deleteFolder(dir);
		return dir.mkdir();
	}
	
	/*
	 * Recursively deletes all files in directory
	 * */
	public static void deleteFolder(File folder)
	{
		File[] files = folder.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
}
