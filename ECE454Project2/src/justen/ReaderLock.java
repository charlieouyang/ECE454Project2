package justen;

public final class ReaderLock extends Lock {
	private static ReaderLock instance = null;
	
	protected ReaderLock() {
		
	}
	
	public static ReaderLock getInstance() {
		if (instance == null)
			instance = new ReaderLock();
		return instance;
	}
}
