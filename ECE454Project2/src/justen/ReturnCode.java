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
}
