package justen;

public final class ReturnCode {
	// SUCCESS
	public static final int SUCCESS = 0;
	
	// ERRORS
	public static final int INCORRECT_OPERATION = -1;
	public static final int FILE_ALREADY_EXISTS = -2;
	public static final int OTHER_DEVICE_HAS_LOCK = -3;
	public static final int HOST_NOT_ALIVE = -4;
	public static final int FILE_DOES_NOT_EXIST = -5;
	
	public static final int COULD_NOT_CREATE_FILE = -998;
	public static final int GO_FUCK_YOURSELF = -999;
	
	// WARNINGS
	public static final int FILE_NOT_OPEN = 1;
	
	public static String getReturnCodeMeaning(int returnCode) {
		if (returnCode == -1)
			return "Incorrect open operation";
		else if (returnCode == -2)
			return "File already exists";
		else if (returnCode == -3)
			return "Another device has a lock on the file";
		else if (returnCode == -4)
			return "Device containing file is offline";
		else if (returnCode == -5)
			return "File does not exist";
		else if (returnCode == -998)
			return "Could not create file (???)";
		else if (returnCode == -999)
			return "Go fuck yourself";
		else if (returnCode == 1)
			return "File not open";
		else
			return "Invalid return code";
//		else if (returnCode == )
//			return "";
//		else if (returnCode == )
//			return "";
//		
//
//		else if (returnCode == )
//			return "";
	}
}
