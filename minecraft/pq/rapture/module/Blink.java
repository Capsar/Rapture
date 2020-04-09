package pq.rapture.module;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.GameUtil;
import pq.rapture.util.RenderUtil;

public class Blink extends Module {

	public Blink() {
		super(Protection.decrypt("8EB574037A01BEEB9108A52115FAAEEE"), new String[] { "" }, Protection.decrypt("FEC490F7760F3EE1316E7B482B3B2FF89127B1F5ECA8727CE8477892946D5C7A"), Type.EXPLOITS, "NONE", 0xFF0297df);
	}

	int timer;
	public int c03PacketsBuffer, toggleBuffer;
	private static int maximumc03PacketBuffer = 500;
	private EntityOtherPlayerMP spawnEntity;
	private Queue<Packet> list = new LinkedBlockingQueue<Packet>();
	private ArrayList<double[]> positions = new ArrayList();

	@Override
	public boolean onEnable() {
		this.positions.add(new double[] { getPlayer().posX, getPlayer().posY, getPlayer().posZ });
		return super.onEnable();
	}

	@Override
	public boolean onDisable() {
		while (!this.list.isEmpty()) {
			sendPacket(this.list.poll());
		}

		positions.clear();
		getMinecraft().timer.timerSpeed = 1.0F;
		c03PacketsBuffer = 0;
		setName("Blink");
		return super.onDisable();
	}

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventPreMotion.class, EventPacketSend.class, EventRenderGlobal.class })
	public void onEvent(EventPreMotion pre, EventPacketSend send, EventRenderGlobal render) {
		if (pre != null) {
			if (getPlayer().isRiding()) {
				return;
			}
			if (getPlayer().getHealth() <= 0.0f) {
				this.toggle(true);
				return;
			}
			boolean isStandingStill = (getPlayer().motionX == 0 && getPlayer().motionZ == 0 && getPlayer().motionY == -0.0784000015258789)
					|| (getPlayer().moveForward == 0 && getPlayer().moveStrafing == 0 && !getGameSettings().keyBindJump.isKeyDown())
					&& !getPlayer().movementInput.jump;

			if (isStandingStill) {
				pre.cancel();
				return;
			}

			if (timer < 5 && !getMod(Speed.class).getState()) {
				timer++;
			} else if (timer == 5) {
				timer = 0;
				pre.cancel();
				return;
			}
			getMinecraft().timer.timerSpeed = 1.25F;
			c03PacketsBuffer++;
			this.positions.add(new double[] { getPlayer().posX, getPlayer().posY, getPlayer().posZ });
			setName("Blink " + (maximumc03PacketBuffer - c03PacketsBuffer));
			if (c03PacketsBuffer == maximumc03PacketBuffer) {
				this.toggle(true);
			}
		} else if (send != null) {
			boolean isStandingStill = GameUtil.isStandingStill();
			Packet packet = send.getPacket();
			if (packet instanceof C03PacketPlayer || packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity
					|| packet instanceof C09PacketHeldItemChange || packet instanceof C07PacketPlayerDigging
					|| packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C0APacketAnimation
					|| packet instanceof C0CPacketInput) {
				this.list.add(packet);
				send.cancel();
			}
		} else if (render != null) {
			RenderUtil.preRender();
			GL11.glColor3d(0, 0.898, 0.9333);
			GL11.glBegin(GL11.GL_LINE_STRIP);
			GL11.glVertex3d((this.positions.get(0)[0] - getRenderManager().viewerPosX), (this.positions.get(0)[1]
					+ net.minecraft.client.Minecraft.getMinecraft().thePlayer.height - getRenderManager().viewerPosY),
					(this.positions.get(0)[2] - getRenderManager().viewerPosZ));
			for (double[] dd : positions) {
				GL11.glVertex3d((dd[0] - getRenderManager().viewerPosX), (dd[1] - getRenderManager().viewerPosY),
						(dd[2] - getRenderManager().viewerPosZ));
			}
			GL11.glEnd();
			RenderUtil.postRender();
		}
	}
}
