package pq.rapture.util;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Blocks;
import net.minecraft.src.Reflector;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import pq.rapture.Wrapper;

public class RenderUtil extends Gui {

	public static void preRender() {
		glEnable(GL_BLEND);
		glEnable(GL_LINE_SMOOTH);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_LIGHTING);
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		RenderHelper.disableStandardItemLighting();

	}

	public static void prePointRender() {
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		orientCamera();
	}

	public static void postPointRender() {
		glColor3d(1, 1, 1);
		GlStateManager.popMatrix();
	}

	public static void postRender() {
		glColor3d(1, 1, 1);
		glDepthMask(true);
		glDisable(GL_LINE_SMOOTH);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_LIGHTING);
		glDisable(GL_BLEND);
		RenderHelper.enableStandardItemLighting();
	}

	public static void orientCamera() {
		float p_78467_1_ = Wrapper.getMinecraft().timer.elapsedPartialTicks;
		Entity var2 = Wrapper.getMinecraft().getRenderViewEntity();
		float var3 = var2.getEyeHeight();
		double var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * p_78467_1_;
		double var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * p_78467_1_ + var3;
		double var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * p_78467_1_;

		if (var2 instanceof EntityLivingBase && ((EntityLivingBase) var2).isPlayerSleeping()) {
			var3 = (float) (var3 + 1.0D);
			GlStateManager.translate(0.0F, 0.3F, 0.0F);

			if (!Wrapper.getMinecraft().gameSettings.debugCamEnable) {
				BlockPos var27 = new BlockPos(var2);
				IBlockState var11 = Wrapper.getMinecraft().theWorld.getBlockState(var27);
				Block var29 = var11.getBlock();

				if (Reflector.ForgeHooksClient_orientBedCamera.exists()) {
					Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, new Object[] { Wrapper.getMinecraft().theWorld, var27,
							var11, var2 });
				} else if (var29 == Blocks.bed) {
					int var30 = ((EnumFacing) var11.getValue(BlockDirectional.FACING)).getHorizontalIndex();
					GlStateManager.rotate(var30 * 90, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, -1.0F,
						0.0F);
				GlStateManager.rotate(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * p_78467_1_, -1.0F, 0.0F,
						0.0F);
			}
		} else if (Wrapper.getMinecraft().gameSettings.thirdPersonView > 0) {
			double var28 = 4 + (4 - 4) * p_78467_1_;

			if (Wrapper.getMinecraft().gameSettings.debugCamEnable) {
				GlStateManager.translate(0.0F, 0.0F, (float) (-var28));
			} else {
				float var12 = var2.rotationYaw;
				float var13 = var2.rotationPitch;

				if (Wrapper.getMinecraft().gameSettings.thirdPersonView == 2) {
					var13 += 180.0F;
				}

				double var14 = -MathHelper.sin(var12 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI) * var28;
				double var16 = MathHelper.cos(var12 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI) * var28;
				double var18 = (-MathHelper.sin(var13 / 180.0F * (float) Math.PI)) * var28;

				for (int var20 = 0; var20 < 8; ++var20) {
					float var21 = (var20 & 1) * 2 - 1;
					float var22 = (var20 >> 1 & 1) * 2 - 1;
					float var23 = (var20 >> 2 & 1) * 2 - 1;
					var21 *= 0.1F;
					var22 *= 0.1F;
					var23 *= 0.1F;
					MovingObjectPosition var24 = Wrapper.getMinecraft().theWorld.rayTraceBlocks(new Vec3(var4 + var21, var6 + var22, var8
							+ var23), new Vec3(var4 - var14 + var21 + var23, var6 - var18 + var22, var8 - var16 + var23));

					if (var24 != null) {
						double var25 = var24.hitVec.distanceTo(new Vec3(var4, var6, var8));

						if (var25 < var28) {
							var28 = var25;
						}
					}
				}

				if (Wrapper.getMinecraft().gameSettings.thirdPersonView == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(var2.rotationPitch - var13, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(var2.rotationYaw - var12, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.0F, 0.0F, (float) (-var28));
				GlStateManager.rotate(var12 - var2.rotationYaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(var13 - var2.rotationPitch, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GlStateManager.translate(0.0F, 0.0F, -0.1F);
		}

		if (!Wrapper.getMinecraft().gameSettings.debugCamEnable) {
			GlStateManager.rotate(var2.prevRotationPitch + (var2.rotationPitch - var2.prevRotationPitch) * p_78467_1_, 1.0F, 0.0F, 0.0F);

			if (var2 instanceof EntityAnimal) {
				EntityAnimal var281 = (EntityAnimal) var2;
				GlStateManager.rotate(var281.prevRotationYawHead + (var281.rotationYawHead - var281.prevRotationYawHead) * p_78467_1_
						+ 180.0F, 0.0F, 1.0F, 0.0F);
			} else {
				GlStateManager.rotate(var2.prevRotationYaw + (var2.rotationYaw - var2.prevRotationYaw) * p_78467_1_ + 180.0F, 0.0F, 1.0F,
						0.0F);
			}
		}

		GlStateManager.translate(0.0F, -var3, 0.0F);
		var4 = var2.prevPosX + (var2.posX - var2.prevPosX) * p_78467_1_;
		var6 = var2.prevPosY + (var2.posY - var2.prevPosY) * p_78467_1_ + var3;
		var8 = var2.prevPosZ + (var2.posZ - var2.prevPosZ) * p_78467_1_;
	}

	public static void drawSearchBlock(AxisAlignedBB bb) {
		Tessellator tess = Tessellator.getInstance();
		WorldRenderer world = Wrapper.getWorldRenderer();
		
		world.startDrawing(GL11.GL_QUAD_STRIP);
		world.addVertex(bb.minX, bb.minY, bb.minZ);
		world.addVertex(bb.maxX, bb.minY, bb.minZ);
		
		world.addVertex(bb.minX, bb.maxY, bb.minZ);
		world.addVertex(bb.maxX, bb.maxY, bb.minZ);
		
		world.addVertex(bb.minX, bb.maxY, bb.maxZ);
		world.addVertex(bb.maxX, bb.maxY, bb.maxZ);
		
		world.addVertex(bb.minX, bb.minY, bb.maxZ);
		world.addVertex(bb.maxX, bb.minY, bb.maxZ);
		tess.draw();		
		
		world.startDrawing(GL11.GL_QUAD_STRIP);
		world.addVertex(bb.minX, bb.maxY, bb.minZ);
		world.addVertex(bb.minX, bb.maxY, bb.maxZ);
		
		world.addVertex(bb.minX, bb.minY, bb.minZ);
		world.addVertex(bb.minX, bb.minY, bb.maxZ);
		
		world.addVertex(bb.maxX, bb.minY, bb.minZ);
		world.addVertex(bb.maxX, bb.minY, bb.maxZ);
		
		world.addVertex(bb.maxX, bb.maxY, bb.minZ);
		world.addVertex(bb.maxX, bb.maxY, bb.maxZ);
		tess.draw();		
	}

	private static void drawOutlinedBoundingBoxESP(AxisAlignedBB axisalignedbb) {
		glBegin(GL_LINES);
		glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		glEnd();
		glBegin(GL_LINES);
		glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		glEnd();
		glBegin(GL_LINES);
		glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		glEnd();
		glBegin(GL_LINES);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		glEnd();
		glBegin(GL_LINES);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		glEnd();
		glBegin(GL_LINES);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		glEnd();
	}

}
