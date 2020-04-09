package pq.rapture.module;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;

public class FastConsume extends Module {

	private int delay = 15;

	public FastConsume() {
		super(Protection.decrypt("A3BC81CFB0EC5EC726D5F7834E24DA7D"), new String[] { "" }, Protection.decrypt("BE8339B58EC847C003C282B8F0A49D73"), Type.PLAYER, "NONE", 0xFFc425);
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onEvent(EventPreMotion pre) {
		boolean holdingFood = null != getPlayer().inventory.getCurrentItem()
				&& (getPlayer().getCurrentEquippedItem().getItem() instanceof ItemFood || getPlayer().getCurrentEquippedItem().getItem() instanceof ItemPotion);
		if (holdingFood) {
			delay = 16;
			if (getGameSettings().keyBindUseItem.isKeyDown() && getPlayer().onGround) {
				if (getPlayer().getItemInUseDuration() == delay) {
					for (int i = 0; i < 20; i++) {
						addPacket(new C03PacketPlayer(true));
					}
					sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, getPlayer().getHorizontalFacing()));
					getPlayer().stopUsingItem();
				}
				return;
			}
		}
	}
}
