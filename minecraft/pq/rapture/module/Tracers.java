package pq.rapture.module;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Reflector;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.RenderUtil;

public class Tracers extends Module implements HasValues {

	private boolean source;

	public Tracers() {
		super(Protection.decrypt("6F6C8FA312D5076B098DA3A946D8283B"), new String[] {}, Protection.decrypt("9F1652717EC87BD2DDF52A0035787A851028A2F8E07FF63E20F8088944AA26B8"), Type.RENDER, "NONE", 0xFFe65d1f);
	}

	@Override
	public boolean onEnable() {
		return super.onEnable();
	}

	@Override
	public boolean onDisable() {
		return super.onDisable();
	}

	@ETarget(eventClasses = { EventRenderGlobal.class })
	public void onEvent(EventRenderGlobal send) {
		for (EntityPlayer e : (List<EntityPlayer>) getWorld().playerEntities) {
			if (e == getPlayer()) continue;
			if (e.getDistanceToEntity(getPlayer()) <= 200 && e.canBeCollidedWith()) {
				double eposX = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) getMinecraft().timer.renderPartialTicks;
				double eposY = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) getMinecraft().timer.renderPartialTicks;
				double eposZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) getMinecraft().timer.renderPartialTicks;
				double x = eposX - getRenderManager().viewerPosX;
				double y = eposY - getRenderManager().viewerPosY;
				double z = eposZ - getRenderManager().viewerPosZ;
				float yaw = e.renderYawOffset * 0.017453292F;
				double motionX = (double) (MathHelper.sin(yaw) * 0.61F) / 2;
				double motionZ = -(double) (MathHelper.cos(yaw) * 0.61F) / 2;
				setColours(e);
				RenderUtil.preRender();
				GL11.glLineWidth(1.5F);

				double posxx = 0;
				double posyy = 0;
				double poszz = 0;

				if (Freecam.oldPlayer != null) {
					posxx = (Freecam.oldPlayer.lastTickPosX + (Freecam.oldPlayer.posX - Freecam.oldPlayer.lastTickPosX) * (double) getMinecraft().timer.renderPartialTicks) - getRenderManager().viewerPosX;
					posyy = (Freecam.oldPlayer.lastTickPosY + (Freecam.oldPlayer.posY - Freecam.oldPlayer.lastTickPosY) * (double) getMinecraft().timer.renderPartialTicks) - getRenderManager().viewerPosY;
					poszz = (Freecam.oldPlayer.lastTickPosZ + (Freecam.oldPlayer.posZ - Freecam.oldPlayer.lastTickPosZ) * (double) getMinecraft().timer.renderPartialTicks) - getRenderManager().viewerPosZ;
				}

				RenderUtil.prePointRender();
				GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3d(posxx, getPlayer().isSneaking() ? posyy + 1.5395 : posyy + 1.6195D, poszz);
				GL11.glVertex3d(x, y, z);
				GL11.glEnd();

				setColours(e);
				GL11.glLineWidth(2F);

				if (e.isSneaking()) {
					GL11.glBegin(GL11.GL_LINES);
					GL11.glVertex3d(x, y, z);
					GL11.glVertex3d(x + motionX, y + 0.7, z + motionZ);
					GL11.glEnd();

					GL11.glBegin(GL11.GL_LINES);
					GL11.glVertex3d(x + motionX, y + 0.7, z + motionZ);
					GL11.glVertex3d(x, y + e.height - 0.1, z);
					GL11.glEnd();
				} else {
					GL11.glBegin(GL11.GL_LINES);
					GL11.glVertex3d(x, y, z);
					GL11.glVertex3d(x, y + e.height, z);
					GL11.glEnd();
				}
				RenderUtil.postPointRender();
				GL11.glColor3f(1F, 1F, 1F);
				RenderUtil.postRender();

			}
		}
	}

	private void setColours(EntityPlayer player) {

		if (FriendManager.isFriend(player.getCommandSenderName())) {
			GL11.glColor3d(255.0 / 255.0, 231.0 / 255.0, 102.0 / 255.0);
		} else {
			int var8 = (int) Math.round(255.0D - getPlayer().getDistanceToEntity(player) * 255.0D / 60.0);
			int var10 = 255 - var8 << 8 | var8 << 16;
			float red = (float) (var10 >> 16 & 255) / 255.0F;
			float green = (float) (var10 >> 8 & 255) / 255.0F;
			GL11.glColor4f(red, green, 0, 1);
		}
	}


	public static final String SOURCE = "Source";
	@Override
	public List<Value> getValues() {
		return Arrays.asList(new Value(SOURCE, true, false));
	}

	@Override
	public Object getValue(String n) {
		if(n.equals(SOURCE)) return source;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if(n.equals(SOURCE)) source = (Boolean) v;
	}
}
