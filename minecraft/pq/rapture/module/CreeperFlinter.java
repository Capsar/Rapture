package pq.rapture.module;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.TimeHelper;

public class CreeperFlinter extends Module {

	public CreeperFlinter() {
		super(Protection.decrypt("563226FF0A2497CF2D509E3B33B19AB1"), new String[] {}, Protection.decrypt("13541E02A8995E67546151B4DA2E64A96568467BF97E680C3C0F2A06B6982DF4E6BE5606FAA8D135FDDBD8552C5A2E95"), Type.WORLD, "NONE", 0xFFd43b36);
	}

	TimeHelper timeHelp = TimeHelper.getTimer();

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onEvent(EventPreMotion pre) {
		if (getPlayer().getHeldItem() != null && getPlayer().getHeldItem().getItem() instanceof ItemFlintAndSteel
				&& timeHelp.hasDelayRun(200)) {
			for (Entity e : (List<Entity>) getWorld().loadedEntityList) {
				if (e instanceof EntityCreeper && getPlayer().getDistanceToEntity(e) < 4.3) {
					EntityCreeper creeper = (EntityCreeper) e;
					if (!creeper.hasIgnited()) {
						sendPacket(new C02PacketUseEntity(creeper, Action.INTERACT));
						getPlayer().swingItem();
						timeHelp.reset();
					}
				}
			}
		}
	}

}
