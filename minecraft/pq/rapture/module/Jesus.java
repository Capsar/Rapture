package pq.rapture.module;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventBoundingBox;
import pq.rapture.rxdy.EventMove;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;
import pq.rapture.util.PacketUtil;

public class Jesus extends Module {

	public Jesus() {
		super(Protection.decrypt("EBB2DCEB3C3176DD6A522AC5ED952C34"), new String[] {}, Protection.decrypt("637F0A735BD79B011844287DFAF466A315319490761930059D7FEF3DD60AA33A"), Type.MOVEMENT, "NONE", 0xFF81ffe1);
	}

	boolean jesusTick = false;
	private boolean getout = false;
	private int getOutWaterTicks = 0;
	private int standingStillTick;
	private double beginX, beginZ;

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPreMotion.class, EventPacketSend.class, EventBoundingBox.class, EventMove.class })
	public void onEvent(EventPreMotion pre, EventPacketSend send, EventBoundingBox box, EventMove move) {
		if (pre != null) {
			if (GameUtil.getColliding(1) != null && !GameUtil.inLiquid()) {
				jesusTick = !jesusTick;
			} else {
				jesusTick = false;
			}
		} else if (send != null) {
			if (getMod(Blink.class).getState()) return;
			if (send.getPacket() instanceof C03PacketPlayer && GameUtil.getColliding(1) != null) {
				C03PacketPlayer packet = null;
				if (send.getPacket() instanceof C06PacketPlayerPosLook) {
					packet = (C03PacketPlayer) send.getPacket();
				} else if (send.getPacket() instanceof C04PacketPlayerPosition) {
					packet = new C06PacketPlayerPosLook(((C04PacketPlayerPosition) send.getPacket()).getPositionX(),
							((C04PacketPlayerPosition) send.getPacket()).getPositionY(),
							((C04PacketPlayerPosition) send.getPacket()).getPositionZ(), getPlayer().rotationYaw,
							getPlayer().rotationPitch, getPlayer().onGround);
				} else if (send.getPacket() instanceof C05PacketPlayerLook) {
					packet = new C06PacketPlayerPosLook(getPlayer().posX, getPlayer().posY, getPlayer().posZ,
							((C05PacketPlayerLook) send.getPacket()).getYaw(), ((C05PacketPlayerLook) send.getPacket()).getPitch(),
							getPlayer().onGround);
				} else {
					send.cancel();
					return;
				}

				for(AxisAlignedBB a : (List<AxisAlignedBB>) getWorld().getCollidingBoundingBoxes(getPlayer(),
						getPlayer().getEntityBoundingBox().offset(getPlayer().motionX, -0.1, getPlayer().motionZ))) {
					if (GameUtil.getBlock(a.minX, a.minY, a.minZ) != Blocks.lava
							&& GameUtil.getBlock(a.minX, a.minY, a.minZ) != Blocks.water
							&& GameUtil.getBlock(a.minX, a.minY, a.minZ) != Blocks.air) {
						if (packet.getPositionY() - getPlayer().posY == 0 && packet.isOnGround() && !jesusTick
								&& GameUtil.getColliding(0) == null && !GameUtil.isStandingStill() || getPlayer().fallDistance > 0.1) {
							sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getPositionX(), packet.getPositionY() + 0.0000001,
									packet.getPositionZ(), packet.getYaw(), packet.getPitch(), false));
							sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getPositionX(), packet.getPositionY() - 0.0000001,
									packet.getPositionZ(), packet.getYaw(), packet.getPitch(), true));
							send.cancel();
							return;
						}
					}
				}
				packet.setPositionY(packet.getPositionY() + (jesusTick ? 0.0000001 : 0));
				packet.setOnGround(!jesusTick);
				send.setPacket(packet);
				if (getPlayer().isSprinting() && jesusTick && !getPlayer().isInWater()) {
					getPlayer().motionX *= 0.85;
					getPlayer().motionZ *= 0.85;
					sendPacket(new C0BPacketEntityAction(getPlayer(), Action.STOP_SPRINTING));
					sendPacket(send.getPacket());
					send.cancel();
					sendPacket(new C0BPacketEntityAction(getPlayer(), Action.START_SPRINTING));
				}

			} else if (send.getPacket() instanceof C03PacketPlayer && GameUtil.getBlockBelowPlayer() instanceof BlockLiquid) {
				C03PacketPlayer packet = (C03PacketPlayer) send.getPacket();
				if (getPlayer().fallDistance > 3.0) {
					getPlayer().fallDistance = 0;
					packet.setOnGround(true);
				} else if (getPlayer().fallDistance > 0.5) {
					packet.setOnGround(false);
				}
			}
		} else if (box != null) {
			if (box.getBlock() instanceof BlockLiquid && !GameUtil.inLiquid()) {
				if (!getPlayer().isSneaking() && GameUtil.getColliding(1) != null && !getPlayer().isInWater()
						&& !getPlayer().isInsideOfMaterial(Material.lava) && getPlayer().fallDistance <= 4)
					box.setBoundingBox(AxisAlignedBB.fromBounds(box.getX(), box.getY(), box.getZ(), box.getX() + 1, box.getY() + 0.9999999,
							box.getZ() + 1));
			}
		} else if (move != null) {
			int meta = GameUtil.getBlockMetadata(MathHelper.floor_double(getPlayer().posX), MathHelper.floor_double(getPlayer().posY),
					MathHelper.floor_double(getPlayer().posZ));
			if (GameUtil.inLiquid() && !getPlayer().isSneaking()) {
				move.setY(0.09);
				if (GameUtil.getBlockAbovePlayer() != Blocks.water && GameUtil.getBlockAbovePlayer() != Blocks.lava) getout = true;
			}
			if (GameUtil.getColliding(0) != null && getout && !getPlayer().isSneaking() && meta < 5) {
				move.setY(0.08);
				getOutWaterTicks++;
				if (getOutWaterTicks > 5) {
					getOutWaterTicks = 0;
					getout = false;
				}
			}

			if (GameUtil.inLiquid()) {
				if (getGameSettings().keyBindJump.isKeyDown()) {
					move.setY(0.09);
				}
			}
		}
	}
}
