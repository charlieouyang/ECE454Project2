package justen;

import java.io.Serializable;

public final class NoLock extends Lock implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static NoLock instance = null;
	
	protected NoLock() {
		
	}

	public static NoLock getInstance() {
		if (instance == null)
			instance = new NoLock();
		return instance;
	}
}
