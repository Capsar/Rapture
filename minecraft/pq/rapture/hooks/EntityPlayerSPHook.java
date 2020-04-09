package pq.rapture.hooks;

import pq.rapture.Wrapper;
import pq.rapture.module.NarniaPhase;
import pq.rapture.module.Phase;
import pq.rapture.module.SkipPhase;
import pq.rapture.module.SpeedMine;
import pq.rapture.rxdy.EventEntityInsideOpaqueBlock;
import pq.rapture.rxdy.EventJump;
import pq.rapture.rxdy.EventMove;
import pq.rapture.rxdy.EventPostMotion;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventPushOutOfBlock;
import pq.rapture.rxdy.event.EventManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.world.World;

/**
 * 	This class must be initialized in PlayerControllerMP at method "func_178892_a" around line 500.
 */
public class EntityPlayerSPHook extends EntityPlayerSP {

	public EntityPlayerSPHook(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
		super(mcIn, worldIn, netHandler, statFile);
	}

	@Override
	public void onUpdateWalkingPlayer() {
		EventPreMotion preMotion = new EventPreMotion(rotationYaw, rotationPitch);
		EventManager.getInstance().fire(preMotion);
		if (!preMotion.isCancelled()) super.onUpdateWalkingPlayer();
		EventManager.getInstance().fire(new EventPostMotion());
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		EventEntityInsideOpaqueBlock insideBlock = new EventEntityInsideOpaqueBlock();
		EventManager.getInstance().fire(insideBlock);
		if (insideBlock.isCancelled()) return false;
		return super.isEntityInsideOpaqueBlock();
	}

	@Override
	protected boolean pushOutOfBlocks(double x, double y, double z) {
		EventPushOutOfBlock pushOut = new EventPushOutOfBlock();
		EventManager.getInstance().fire(pushOut);
		if (pushOut.isCancelled()) return false;
		return super.pushOutOfBlocks(x, y, z);
	}

	@Override
	public float getToolDigEfficiency(Block p_180471_1_) {
		float strength = this.inventory.getStrVsBlock(p_180471_1_);
		super.getToolDigEfficiency(p_180471_1_);
		if (strength > 1.0F) {
			int enchantmentmodifier = EnchantmentHelper.getEfficiencyModifier(this);
			ItemStack currentItem = this.inventory.getCurrentItem();
			if (Wrapper.getMod(SpeedMine.class).shouldMine && Wrapper.getMod(SpeedMine.class).getState()) {
				SpeedMine mine = Wrapper.getMod(SpeedMine.class);
				switch (enchantmentmodifier) {
				case 0:
					strength *= 1.35;
					break;
				case 1:
					strength *= 1.8;
					break;
				case 2:
					strength *= 1.9;
					break;
				case 3:
					strength *= 2.2;
					break;
				case 4:
					strength *= 5.2;
					break;
				case 5:
					strength *= 5.3;
					break;
				}
				if (p_180471_1_ instanceof BlockDirt && enchantmentmodifier > 1) {
					strength *= 0.5;
				}
				strength *= mine.speed;

			} else if (enchantmentmodifier > 0 && currentItem != null) {
				if (enchantmentmodifier > 0 && currentItem != null) {
					strength += enchantmentmodifier * enchantmentmodifier + 1;
				}
			}
		}

		if (this.isPotionActive(Potion.digSpeed)) {
			strength *= 1.0F + (this.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
		}

		if (this.isPotionActive(Potion.digSlowdown)) {
			float var5 = 1.0F;

			switch (this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
			case 0:
				var5 = 0.3F;
				break;
			case 1:
				var5 = 0.09F;
				break;
			case 2:
				var5 = 0.0027F;
				break;
			case 3:
			default:
				var5 = 8.1E-4F;
			}

			strength *= var5;
		}

		if (this.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
			strength /= 5.0F;
		}

		if (!this.onGround) {
			strength /= 5.0F;
		}
		return strength;
	}

	@Override
	public void moveEntity(double x, double y, double z) {

		if (this == Minecraft.getMinecraft().thePlayer) {
			EventMove event = new EventMove(x, y, z, this);
			EventManager.getInstance().fire(event);
			x = event.getX();
			y = event.getY();
			z = event.getZ();
		}

		super.moveEntity(x, y, z);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
	}

	@Override
	public void jump() {
		EventJump eventJump = new EventJump();
		EventManager.getInstance().fire(eventJump);
		if (!eventJump.isCancelled()) super.jump();
	}
}
