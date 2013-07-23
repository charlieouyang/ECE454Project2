package justen;

import java.io.Serializable;

public final class WriterLock extends Lock implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static WriterLock instance = null;
	
	protected WriterLock() {
		
	}
	
	public static WriterLock getInstance() {
		if (instance == null)
			instance = new WriterLock();
		return instance;
	}
}
