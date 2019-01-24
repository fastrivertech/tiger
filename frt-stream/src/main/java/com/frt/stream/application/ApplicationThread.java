/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved.
 * 
 * $Id:					$: Id of last commit                
 * $Revision:			$: Revision of last commit 
 * $Author: cye			$: Author of last commit       
 * $Date:	10-10-2018	$: Date of last commit
 */
package com.frt.stream.application;

/*
 * ApplicationThread class
 * @author chaye
 */
public class ApplicationThread extends Thread {

	/*
	 * ThreadState.CREATED --> ThreadState.RUNNING --> ThreadState.STOPPED 
	 */
	enum ApplicationState {
		CREATED("CREATED"), RUNNING("RUNNING"), STOPPED("STOPPED");
		
		private String code;
		
		ApplicationState(String code) {
			this.code = code;
		}
		
		public String getApplicationCode() {
			return this.code;
		}
	}	
	
	private ApplicationState appState = ApplicationState.CREATED;
	private final Object appLock = new Object();
	private ParticipatingApplication participatingApplication;
	
	public ApplicationThread(ParticipatingApplication participatingApplication) {
		this.participatingApplication = participatingApplication;
	}
	
	public ApplicationState setState(final ApplicationState newState) {
		synchronized (appLock) {
			if (appState == ApplicationState.CREATED &&
			    newState == ApplicationState.RUNNING) {
				appState = newState;
			} else if (appState == ApplicationState.RUNNING &&
					   newState == ApplicationState.STOPPED) {
				appState = newState;				
			} else if (appState == ApplicationState.STOPPED) {
			}
			return this.appState;
		}		
	}
		
	@Override
	public void run() {
		while (appState == ApplicationState.RUNNING) {
			participatingApplication.run();
		}
	}
	
	public void close() {
		setState(ApplicationState.STOPPED);
	}
}
