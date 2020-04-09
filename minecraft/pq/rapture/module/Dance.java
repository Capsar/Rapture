package pq.rapture.module;

import net.minecraft.item.ItemBow;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.TimeHelper;

/**
 * Created by Haze on 7/5/2015.
 */
public class Dance extends Module {

	private TimeHelper timeHelper;

	public Dance() {
		super(Protection.decrypt("51BC1DFCAA465B50307B8F2B71B47A11"), null, Protection.decrypt("13BBA0EB5A2F6571AAB6ECA03433C13E633962A5661FC6200028A6B8FA9158B1"), Type.MOVEMENT, "NONE", 0xFF232323);
		timeHelper = new TimeHelper();
	}

	@ETarget(eventClasses = EventPreMotion.class)
	public void listenToPre(EventPreMotion eventPreMotion) {
		if (timeHelper.hasDelayRun(250)) {
			if (getPlayer().isUsingItem() && getPlayer().inventory.getCurrentItem().getItem() instanceof ItemBow) {
				getPlayer().swingItem();
			}
			timeHelper.reset();
		}
	}

}
