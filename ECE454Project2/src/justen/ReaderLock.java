package justen;

import java.io.Serializable;

public final class ReaderLock extends Lock implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ReaderLock instance = null;
	
	protected ReaderLock() {
		
	}
	
	public static ReaderLock getInstance() {
		if (instance == null)
			instance = new ReaderLock();
		return instance;
	}
}
