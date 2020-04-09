package pq.rapture.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventBlockBreak;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.EventBlockClick;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SpeedMine extends Module {

	public boolean shouldMine = true;
	public float speed = 1.1F;

	public SpeedMine() {
		super(Protection.decrypt("A8947DA3A6D93EF562975D5549A534E7"), new String[] {}, Protection.decrypt("53DAC4CEFB6D2A33E8B802D629298E99B7DF1E05540A90FD8D228DE01E660754"), Type.WORLD, "NONE", 0xFF83f865);
	}

	@ETarget(eventClasses = { EventBlockClick.class })
	public void blockClick(EventBlockClick e) {
		if (getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			getPlayer().inventory.currentItem = getBestTool(e.getBlock());
			sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem));
		}
	}

	@ETarget(eventClasses = EventBlockBreak.class)
	public void listenPacket(EventBlockBreak bb) {
		sendPacket(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, bb.getBlock(), bb.getSide()));
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void renderGUI(EventPreMotion pre) {
		getPlayerController().blockHitDelay = 0;
	}

	public int getBestTool(BlockPos blockPos) {
		Block block = GameUtil.getBlock(blockPos);
		int slot = -1, fortune = 0;
		float mineSpeed = 0.1F;
		boolean checkFortune = block instanceof BlockOre && block.getItemDropped(getWorld().getBlockState(blockPos), new Random(), 1) != Item.getItemFromBlock(block);
		if (checkFortune) {
			for (int hotbar = 36; hotbar < 45; hotbar++) {
				ItemStack itemStack = getPlayer().inventoryContainer.getSlot(hotbar).getStack();
				if (itemStack == null) continue;
				int fortuneOfItem = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack);
				float blockDamage = itemStack.getItem().getStrVsBlock(itemStack, block);
				if (fortuneOfItem > fortune || (fortuneOfItem == fortune && blockDamage > mineSpeed)) {
					fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack);
					mineSpeed = blockDamage;
					slot = hotbar - 36;
				}
			}
			if (mineSpeed > 1.0F && fortune > 0) return slot;
		} else {
			for (int hotbar = 36; hotbar < 45; hotbar++) {
				ItemStack itemStack = getPlayer().inventoryContainer.getSlot(hotbar).getStack();
				if (itemStack == null) continue;
				float blockDamage = itemStack.getItem().getStrVsBlock(itemStack, block);
				if (block != null && blockDamage > mineSpeed) {
					mineSpeed = blockDamage;
					slot = hotbar - 36;
				}
			}
			if (mineSpeed > 1.0F) return slot;
		}

		return getPlayer().inventory.currentItem;
	}

}
