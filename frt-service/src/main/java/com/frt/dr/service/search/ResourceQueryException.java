package com.frt.dr.service.search;

public class ResourceQueryException extends RuntimeException {
	private static final long serialVersionUID = -8321293485415818762L;
	
	/**
	 * ResourceQueryException Constructor
	 */
	public ResourceQueryException() {
		super();
	}

	/**
	 * ResourceQueryException Constructor
	 * 
	 * @param m Message string
	 */
	public ResourceQueryException(String m) {
		super(m);
	}

	/**
	 * ResourceQueryException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public ResourceQueryException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * ResourceQueryException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public ResourceQueryException(Throwable t) {
		super(t);
	}

}	
