package com.frt.stream.data;

public interface ParticipatingApplication {
			
	void initialize() 
		throws StreamDataException;

	void close();
	
}
