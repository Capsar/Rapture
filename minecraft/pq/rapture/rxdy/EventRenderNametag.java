package pq.rapture.rxdy;

import net.minecraft.entity.EntityLivingBase;
import pq.rapture.Wrapper;
import pq.rapture.render.RaptureRender;
import pq.rapture.rxdy.event.Event;

public class EventRenderNametag extends Event {

	private EntityLivingBase livingbase;

	public EventRenderNametag(EntityLivingBase livingbase) {
		this.livingbase = livingbase;
	}

	public float getPartialTicks() {
		return Wrapper.partialTicks;
	}

	public EntityLivingBase getLivingbase() {
		return livingbase;
	}

	public void setLivingbase(EntityLivingBase livingbase) {
		this.livingbase = livingbase;
	}
}
