package com.nfa.capapp.service.messaging.serial;

public class GestureMessage {

	private int gestureNumber;
	
	public GestureMessage() {
		
	}
	
	public GestureMessage(int gestureNumber) {
		this.gestureNumber = gestureNumber;
	}

	public int getGestureNumber() {
		return gestureNumber;
	}

	public void setGestureNumber(int gestureNumber) {
		this.gestureNumber = gestureNumber;
	}
}
