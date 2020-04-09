package pq.rapture.rxdy;

import pq.rapture.Wrapper;
import pq.rapture.rxdy.event.Event;

public class EventRenderHUD extends Event {

	public float getPartialTicks() {
		return Wrapper.partialTicks;
	}
}
