package pq.rapture.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.world.WorldSettings.GameType;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.event.annotations.ETarget;

public class Freecam extends Module {

	public static EntityPlayerSP oldPlayer;
	private static GameType gameType;
	private double x, y, z;
	private float yaw, pitch;

	public Freecam() {
		super(Protection.decrypt("83AE2166D0992F2A90E44E21B327C70A"), new String[] {}, Protection.decrypt("372F2F130F649FC0056F4E187E8C55CBB89BA458912F0B2070D047487842CBBD"), Type.WORLD, "NONE", 0xFFc0e9b7);
	}

	@ETarget(eventClasses = { EventPacketSend.class })
	public void onEvent(EventPacketSend send) {
		Packet packet = send.getPacket();
		if(packet instanceof C03PacketPlayer || packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement
				|| packet instanceof C02PacketUseEntity || packet instanceof C0BPacketEntityAction || packet instanceof C0APacketAnimation)
			send.cancel();
		if(packet instanceof C03PacketPlayer) {
			sendPacket(new C03PacketPlayer(true));
		}
	}

	@Override
	public boolean onEnable() {
		if (getPlayer() == null)
			return false;

		Freecam.oldPlayer = getPlayer();
		x = getPlayer().posX;y = getPlayer().posY + 0.15;z = getPlayer().posZ;yaw = getPlayer().rotationYaw;pitch = getPlayer().rotationPitch;
		getPlayer().capabilities.isFlying = true;
		NetworkPlayerInfo playerInfo = getSendQueue().getPlayerInfo(getMinecraft().thePlayer.getGameProfile().getId());
		Freecam.gameType = playerInfo.getGameType();
		playerInfo.setGameType(GameType.SPECTATOR);
		sendPacket(new C0BPacketEntityAction(getPlayer(), Action.STOP_SPRINTING));
		sendPacket(new C0BPacketEntityAction(getPlayer(), Action.START_SNEAKING));
		return super.onEnable();
	}

	@Override
	public boolean onDisable() {
		if(!getPlayer().isSneaking())
			sendPacket(new C0BPacketEntityAction(getPlayer(), Action.STOP_SNEAKING));
		NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getNetHandler()
				.getPlayerInfo(getMinecraft().thePlayer.getGameProfile().getId());
		playerInfo.setGameType(Freecam.gameType);
		getPlayer().capabilities.isFlying = false;
		getPlayer().noClip = false;
		getPlayer().setPositionAndRotation(x, y, z, yaw, pitch);
		getPlayer().setVelocity(0, 0, 0);
		oldPlayer = null;
		return super.onDisable();
	}

}
