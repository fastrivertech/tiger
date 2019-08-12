package com.frt.fhir.mpi;

public class MpiProviderException extends RuntimeException {

	/**
	 * MpiProviderException Constructor
	 */
	public MpiProviderException() {
		super();
	}

	/**
	 * MpiProviderException Constructor
	 * 
	 * @param m Message string
	 */
	public MpiProviderException(String m) {
		super(m);
	}

	/**
	 * MpiProviderException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public MpiProviderException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * MpiProviderException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public MpiProviderException(Throwable t) {
		super(t);
	}
	
}
