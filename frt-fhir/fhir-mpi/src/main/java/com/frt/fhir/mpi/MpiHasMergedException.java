package com.frt.fhir.mpi;

public class MpiHasMergedException extends RuntimeException {

	/**
	 * MpiHasMergedException Constructor
	 */
	public MpiHasMergedException() {
		super();
	}

	/**
	 * MpiHasMergedException Constructor
	 * 
	 * @param m Message string
	 */
	public MpiHasMergedException(String m) {
		super(m);
	}

	/**
	 * MpiHasMergedException Constructor
	 * 
	 * @param m Message string
	 * @param t Throwable inherited
	 */
	public MpiHasMergedException(String m, Throwable t) {
		super(m, t);
	}

	/**
	 * MpiHasMergedException Constructor
	 * 
	 * @param t Throwable inherited
	 */
	public MpiHasMergedException(Throwable t) {
		super(t);
	}

}
