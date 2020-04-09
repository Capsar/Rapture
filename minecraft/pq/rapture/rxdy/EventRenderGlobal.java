package pq.rapture.rxdy;

import pq.rapture.Wrapper;
import pq.rapture.render.RaptureRender;
import pq.rapture.rxdy.event.Event;

public class EventRenderGlobal extends Event {

	public float getPartialTicks() {
		return Wrapper.partialTicks;
	}
}
