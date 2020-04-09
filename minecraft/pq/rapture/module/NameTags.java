package pq.rapture.module;

import java.util.*;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.render.GUIUtil;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.EventRenderNametag;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.rxdy.event.annotations.EventAllowance;
import pq.rapture.rxdy.event.utility.EventAllowanceEnum;
import pq.rapture.util.FontUtil;
import pq.rapture.util.GameUtil;

public class NameTags extends Module implements HasValues {

	public NameTags() {
		super(Protection.decrypt("59AE59A09E92A3A8300D60A079F3E5EB"), new String[] {}, Protection.decrypt("3DEF1DFC75B22A9C6A92BF26299198266591EC5A90901F0DEE8829C3C9CD7F57"), Type.RENDER, "NONE", 0xFFab9828);
	}

	private RenderItem itemRender = new RenderItem(getMinecraft().getTextureManager(), getRenderItem().getItemModelMesher().getModelManager());
	private boolean armoresp = true, boxes = false, healthBar;

	@EventAllowance(allowance = EventAllowanceEnum.ALLOW_ANY)
	@ETarget(eventClasses = { EventRenderGlobal.class, EventRenderNametag.class })
	public void onEvent(EventRenderGlobal global, EventRenderNametag nametag) {
		if (global != null) {
			List<EntityPlayer> players = new ArrayList<>();
			players.addAll(getWorld().playerEntities);
			players.sort((o1, o2) -> (int) getPlayer().getDistanceToEntity(o2) - (int) getPlayer().getDistanceToEntity(o1));
			for (EntityPlayer e : players) {
				if (e == getPlayer() || e.isDead) continue;

				double eposX = e.lastTickPosX + (e.posX - e.lastTickPosX) * getMinecraft().timer.renderPartialTicks;
				double eposY = e.lastTickPosY + (e.posY - e.lastTickPosY) * getMinecraft().timer.renderPartialTicks;
				double eposZ = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * getMinecraft().timer.renderPartialTicks;
				double x = eposX - getRenderManager().viewerPosX;
				double y = eposY - getRenderManager().viewerPosY;
				double z = eposZ - getRenderManager().viewerPosZ;
				renderNameTag(x, y + e.getEyeHeight() + 0.65, z, e);
			}
		} else if (nametag != null) {
			if (nametag.getLivingbase() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) nametag.getLivingbase();
				if (!player.getCommandSenderName().equals("")) nametag.cancel();
			}
		}
	}

	private void renderNameTag(double x, double y, double z, Entity checkingEntity) {
		double distance = getPlayer().getDistance(checkingEntity.posX, checkingEntity.posY, checkingEntity.posZ);
		float var14 = 2F / 75F;

		GL11.glPushMatrix();
		RenderHelper.enableStandardItemLighting();
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glTranslated(x, y, z);
		GL11.glRotatef(-getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(getGameSettings().thirdPersonView == 2 ? -getRenderManager().playerViewX : getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

		if (distance > 7) {
			GL11.glScaled(-var14 * distance / 6, -var14 * distance / 6, distance);
		} else {
			GL11.glScaled(-var14, -var14, distance);
		}
		FontRenderer fr = getFontRenderer();
		EntityPlayer player = (EntityPlayer) checkingEntity;
		float var16 = -7F;

		String tag = getTag(player);
		String health = getHealth(player);
		int width = !healthBar ? (fr.getStringWidth(tag + health) / 2) + 4 : (fr.getStringWidth(tag) / 2) + 2;

		if (boxes) {
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GUIUtil.drawSexyRect(-width, -7, width, 4, getMod(FriendManager.class).isFriend(player.getDisplayName().getFormattedText()) ? 0x80ccb851 : 0x80333333,
					getMod(FriendManager.class).isFriend(player.getDisplayName().getFormattedText()) ? 0x80ffe766 : 0x80232323);
			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
		}

		if (!healthBar) {
			FontUtil.drawStringVariableWidth(tag, -width + 2, -5, getTagColor(player), 0.5F);
			FontUtil.drawStringVariableWidth(health, (fr.getStringWidth(tag + health) / 2) - fr.getStringWidth(health) + 3, -5, getHealthColor(player), 0.5F);
		} else {
			FontUtil.drawStringVariableWidth(tag, -width + 2, -5, getTagColor(player), 0.5F);
			double size = width * 2;
			double currentHealth = Double.parseDouble(getHealth(player)) * 2;
			double maxHealth = getPlayer().getMaxHealth();
			double extraSize = currentHealth / maxHealth * size;
			GUIUtil.drawRect(-width - 0.1D, -7.2D, -width + extraSize + 0.1D, -6D, getHealthColor(player));
		}

		if (armoresp) {
			renderArmorESP(player);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	private String[] formatForColor(PotionEffect poteffect) {
		Potion pot = Potion.potionTypes[poteffect.getPotionID()];
		String potionEffect = StatCollector.translateToLocal(pot.getName());
		if (poteffect.getAmplifier() == 1) {
			potionEffect += " II";
		} else if (poteffect.getAmplifier() == 2) {
			potionEffect += " III";
		} else if (poteffect.getAmplifier() == 3) {
			potionEffect += " IV";
		}
		String duration = Potion.getDurationString(poteffect);
		return new String[] { potionEffect, duration };
	}

	private static final String GOD = Protection.decrypt("7897610C6F8D06979D424E699E9CBA52");

	private void renderArmorESP(EntityPlayer player) {
		int increment = 18;
		ArrayList<ItemStack> stacks = new ArrayList<>();
		if (player.getCurrentEquippedItem() != null) stacks.add(player.getCurrentEquippedItem());
		for (ItemStack stack : player.getInventory()) {
			if (stack == null) {
				continue;
			}
			stacks.add(stack);
		}

		int amountOfItems = stacks.size();
		int width = amountOfItems * increment;
		int xx = -width / 2;
		int xy = -increment - 8;
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 0.5f);
		for (ItemStack stack : stacks) {
			GUIUtil.drawESPItem(stack, xx, xy, itemRender);
			GUIUtil.renderESPOverLay(stack, xx, xy, itemRender);
			xx += increment;
		}
		xx = -width / 2;
		int timer = 0;
		GL11.glTranslated(0, 0, -0.001);
		for (ItemStack stack : stacks) {
			if (stack.stackSize != 1) {
				FontUtil.drawScaledString(stack.stackSize + "", xx + 20 - getFontRenderer().getStringWidth(stack.stackSize + ""), xy, 0xFFFFFFFF, 0.55F, false);
			}

			timer++;
			NBTTagList enchants = stack.getEnchantmentTagList();
			String tag = "";
			if (stack.getItem() == Items.golden_apple && stack.hasEffect()) {
				tag = GOD;
			} else if (enchants != null) {
				if (enchants.tagCount() >= 6) {
					tag = GOD;
				} else {
					double isz = 8;
					for (int index = 0; index < enchants.tagCount(); ++index) {
						String encName = "";
						final short id = enchants.getCompoundTagAt(index).getShort("id");
						final short level = enchants.getCompoundTagAt(index).getShort("lvl");
						for (int i = 0; i < 256; i++) {
							Enchantment enc = Enchantment.getEnchantmentById(i);
							if (enc != null && id == enc.effectId) {
								encName += enc.getTranslatedName(level).substring(0, 2).toLowerCase();
								if (level > 10) {
									encName += "10+";
								} else {
									encName += level;
								}
								break;
							}
						}
						int xxs = getFontRenderer().getStringWidth(encName) / 2;
						int yxs = timer % 2 == 0 ? -3 : +8;
						FontUtil.drawScaledString(encName, xx, xy + isz, 0xFFFFFFFF, 0.55F, false);
						isz -= 4.5;
					}
				}
			}
			FontUtil.drawScaledString(tag, xx, xy + 10, 0xFFFFFFFF, 0.9F, false);
			xx += increment;
		}
		GL11.glPopMatrix();
	}

	private int getTagColor(EntityPlayer player) {
		if (FriendManager.isFriend(player.getCommandSenderName())) { return 0xFFffe766; }
		return 0xFFFFFF;
	}

	private int getHealthColor(EntityPlayer player) {
		int var8 = (int) Math.round(255.0D - (Double.valueOf(getHealth(player)) * 2.0) * 255.0D / (double) player.getMaxHealth());
		int var10 = 255 - var8 << 8 | var8 << 16;
		return var10;
	}

	private static String getTag(EntityPlayer checkingEntity) {
		return checkingEntity.getDisplayName().getFormattedText();
	}

	private static String getHealth(EntityLivingBase entity) {
		int health = (int) Math.ceil(entity.getHealth());
		float maxHealth = entity.getMaxHealth();
		int nrhealth = (int) (health + 0.5F);
		float rhealth = (float) nrhealth / 2;
		String ihealth = String.valueOf(rhealth).replace(".0", "");
		return ihealth;
	}

	private static final String ARMOR_ESP = "Armor Esp";
	private static final String BOXES = "Boxes";
	private static final String HEALTH_BAR = "Health Bar";

	@Override
	public List<Value> getValues() {
		return Arrays.asList(new Value(ARMOR_ESP, true, false), new Value(BOXES, true, false), new Value(HEALTH_BAR, true, false));
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(BOXES)) return boxes;
		else if (n.equals(ARMOR_ESP)) return armoresp;
		else if (n.equals(HEALTH_BAR)) return healthBar;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(BOXES)) boxes = (Boolean) v;
		else if (n.equals(ARMOR_ESP)) armoresp = (Boolean) v;
		else if (n.equals(HEALTH_BAR)) healthBar = (Boolean) v;
	}
}
