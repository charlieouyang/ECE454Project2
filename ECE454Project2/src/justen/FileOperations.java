package justen;

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
		
		if (!fm.containsFileLocally(fileName, versionNumber)) {
			// check if device is online
		}
		else {
			// we have file locally
			
		}
			
		
		return 0;
	}
}
