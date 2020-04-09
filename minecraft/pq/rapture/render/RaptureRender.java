package pq.rapture.render;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.module.HUDHelper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.ModuleManager;
import pq.rapture.protection.Protection;
import pq.rapture.protection.Requester;
import pq.rapture.rxdy.EventRenderHUD;
import pq.rapture.rxdy.event.EventManager;
import pq.rapture.util.Colors;
import pq.rapture.util.FontUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * This class must be hooked in Minecraft in the method initGame > ingameGUI.
 */
public class RaptureRender extends GuiIngame {


    private int[] titelColor; //0xFFEC8B || 0xFFF3B9
    private int coordsColor; //0xFFFAF0

	public RaptureRender(Minecraft mcIn) {
		super(mcIn);
        JsonElement element = Requester.newRequester().username(Protection.getHWID()).requestType(Requester.RequestType.IS_ENABLED).build().request();
        JsonPrimitive prim = element.getAsJsonPrimitive();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> myMap = new GsonBuilder().setPrettyPrinting().create().fromJson(prim.getAsString(), type);
        Protection.isValidGAUTH = myMap.get("success").equalsIgnoreCase("true");
        if (Protection.shouldEnableWithSerial()) {
            new RaptureClient();
			coordsColor = Protection.getColor("7361DD439B41CC25D9287627DFB14536");
			titelColor = new int[] {Protection.getColor("E103D5BD3DCAA6B20404F9D304E512B5"), Protection.getColor("3BC9858D6E822B3F4F8F112DDE74250B")};
		}
	}

	@Override
	public void renderGameOverlay(float partialTicks) {
		Wrapper.partialTicks = partialTicks;
		super.renderGameOverlay(partialTicks);
		if (RaptureClient.isEnabled) renderClient();
	}

	public void renderClient() {
		if (Wrapper.getGameSettings().showDebugInfo || Wrapper.getGameSettings().hideGUI) return;

		EventManager.getInstance().fire(new EventRenderHUD());

		if (HUDHelper.watermark) {
			String name = RaptureClient.getName() + " " + Wrapper.getMinecraft().debug.split(" ")[0];
			
			for(int i = 0; i < name.length(); i++) {
				String character = name.substring(i, i + 1);
				if (HUDHelper.TTFFont) {
					float length = Wrapper.getFont().getStringWidth(name.substring(0, i)) * 1.5F;
					FontUtil.drawScaledTTF(Colors.BOLD + character, 2 + length, 2, i % 2 == 0 ? titelColor[0] : titelColor[1], 1.25F, true);
				} else {
					float length = Wrapper.getFontRenderer().getStringWidth(name.substring(0, i)) * 1.2F;
					FontUtil.drawScaledString(Colors.BOLD + character, 2 + length, 2, i % 2 == 0 ? titelColor[0] : titelColor[1], 1F, true);
				}
			}
		}

		if (HUDHelper.coords)
			FontUtil.drawScaledString(String.format("[%s, %s, %s]", (int) Wrapper.getPlayer().posX, (int) Wrapper.getPlayer().posY,
					(int) Wrapper.getPlayer().posZ), 2, Wrapper.getScaledResolution().getScaledHeight() - 10
					- (getChatGUI().getChatOpen() ? 14 : 0), coordsColor, 1.0F, true);

		Wrapper.getTabGui().drawScreen(2, HUDHelper.watermark ? (HUDHelper.TTFFont ? 15 : 13) : 2);

		ArrayList<Module> enabledModules = new ArrayList<>();
		for(Module m : ModuleManager.modules) {
			if (m.getState() && m.isVisible() && HUDHelper.arrayList) {
				enabledModules.add(m);
			}
		}

		if (HUDHelper.fancyArray) {
			Collections.sort(enabledModules, new Comparator<Module>() {
				@Override
				public int compare(Module o1, Module o2) {
					if (Wrapper.getFontRenderer().getStringWidth(o1.getName()) < Wrapper.getFontRenderer().getStringWidth(o2.getName()))
						return 1;
					else
						return -1;
				}
			});
		}

		double height = 2;
		for(Module m : enabledModules) {
			String name = m.getName();
			if (HUDHelper.TTFFont) {
				float length = Wrapper.getFont().getStringWidth(name) * 1.2F;
				FontUtil.drawScaledTTF(name, Wrapper.getScaledResolution().getScaledWidth() - length - 3, height, m.getColor(), 1.2F, true);
				height += 9.3;
			} else {
				float length = Wrapper.getFontRenderer().getStringWidth(name);
				FontUtil.drawScaledString(name, Wrapper.getScaledResolution().getScaledWidth() - length - 2, height, m.getColor(), 1.0F, true);
				height += 9.2;
			}
		}
	}
}
