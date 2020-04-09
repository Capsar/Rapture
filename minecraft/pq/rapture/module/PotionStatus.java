package pq.rapture.module;

import java.util.Iterator;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import pq.rapture.Wrapper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventRenderHUD;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.FontUtil;

public class PotionStatus extends Module {

	public PotionStatus() {
		super(Protection.decrypt("1A495147A95BC9167713B1A39F4D7ABF"), new String[] { "" }, Protection.decrypt("90672BA1E851F4027A91F6E366838425E9ACD5E44C34C08AB6994C7695F29943"), Type.RENDER, "NONE", 0xFFf51245);
	}

	@ETarget(eventClasses = { EventRenderHUD.class })
	public void renderGUI(EventRenderHUD event) {
		if (!getPlayer().getActivePotionEffects().isEmpty()) {
			int h = -10 - (Wrapper.getMinecraft().ingameGUI.getChatGUI().getChatOpen() ? 14 : 0);
			for (Iterator list = getPlayer().getActivePotionEffects().iterator(); list.hasNext(); h -= 10) {
				PotionEffect effect = (PotionEffect) list.next();
				displayDebuffEffectText(effect, getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight() + h);
			}
		}
	}

	protected void displayDebuffEffectText(PotionEffect poteffect, int width, int height) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
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
		FontUtil.drawStringWithShadow(potionEffect, width - getFontRenderer().getStringWidth(potionEffect + " : " + duration), height,	pot.getLiquidColor());
		FontUtil.drawStringWithShadow(": " + duration, width - getFontRenderer().getStringWidth(" : " + duration), height, 8355711);
	}
}
