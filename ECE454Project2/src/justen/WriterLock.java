package justen;

public final class WriterLock extends Lock {
	private static WriterLock instance = null;
	
	protected WriterLock() {
		
	}
	
	public static WriterLock getInstance() {
		if (instance == null)
			instance = new WriterLock();
		return instance;
	}
}
