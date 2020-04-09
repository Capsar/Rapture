package pq.rapture.util;

import net.minecraft.network.play.client.C03PacketPlayer;
import pq.rapture.Wrapper;

public class PacketUtil extends Wrapper {

	public static void sendPlayerPosPacket(double x, double y, double z, boolean ground) {
		sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
	}

	public static void sendPlayerLookPacket(double x, double y, double z, float yaw, float pitch, boolean ground) {
		sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw, pitch, ground));
	}

	public static void sendLookPacket(float yaw, float pitch, boolean b) {
		sendPlayerLookPacket(getPlayer().posX, getPlayer().posY, getPlayer().posZ, yaw, pitch, getPlayer().onGround);
	}

	public static void addPlayerPacket(boolean onGround) {
		addPlayerLookPacket(getPlayer().posX, getPlayer().posY, getPlayer().posZ, getPlayer().rotationYaw, getPlayer().rotationPitch, onGround);
	}
	
	public static void sendPlayerPacket(boolean onGround) {
		sendPlayerLookPacket(getPlayer().posX, getPlayer().posY, getPlayer().posZ, getPlayer().rotationYaw, getPlayer().rotationPitch, onGround);
	}

	public static void addPlayerOffsetPacket(double x, double y, double z, boolean ground) {
		addPlayerLookPacket(getPlayer().posX + x, getPlayer().posY + y, getPlayer().posZ + z, getPlayer().rotationYaw,
				getPlayer().rotationPitch, ground);
	}

	public static void addPlayerLookPacket(double posX, double posY, double posZ, float rotyaw, float rotpitch, boolean ground) {
		addPacket(new C03PacketPlayer.C06PacketPlayerPosLook(posX, posY, posZ, rotyaw, rotpitch, ground));
	}

	public static void sendPlayerOffsetPacket(double x, double y, double z, boolean ground) {
		sendPlayerLookPacket(getPlayer().posX + x, getPlayer().posY + y, getPlayer().posZ + z, getPlayer().rotationYaw,
				getPlayer().rotationPitch, ground);
	}

}
