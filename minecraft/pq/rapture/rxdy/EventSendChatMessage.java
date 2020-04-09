package pq.rapture.rxdy;

import pq.rapture.rxdy.event.Event;


public class EventSendChatMessage extends Event {
	
	private String message;

	public EventSendChatMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
