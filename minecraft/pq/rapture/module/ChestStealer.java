package pq.rapture.module;

import java.util.List;

import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventMove;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.Inventory;
import pq.rapture.util.TimeHelper;

public class ChestStealer extends Module {

	TimeHelper time = TimeHelper.getTimer();

	public ChestStealer() {
		super(Protection.decrypt("64E348E8D325C79A7AE30018A27051E5"), new String[] { "" }, Protection.decrypt("4F7E6C06DDFA411A35309E43756BE39C1FB358A18B719479495A36442285C4123B8991F60548EB7D796212876D898618"), Type.PLAYER, "NONE", 0xFF12ac19);
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onEvent(EventPreMotion pre) {
		if (pre != null) {
			if (getPlayer().openContainer instanceof ContainerChest) {
				ContainerChest chest = (ContainerChest) getPlayer().openContainer;
				if (chest.getLowerChestInventory().getCommandSenderName().toLowerCase().contains("disposal")
						|| getPlayer().inventory.getItemStack() != null)
					return;
				List<ItemStack> inventoryStacks = (List<ItemStack>) chest.getInventory();
				for (int stackInt = 0; stackInt < inventoryStacks.size() - 36; stackInt++) {
					ItemStack s = inventoryStacks.get(stackInt);
					if (s != null && time.hasDelayRun(60)) {
						Inventory.clickSlot(stackInt, 1, 2);
						time.reset();
						break;
					}
				}
			}
		}
	}
}
