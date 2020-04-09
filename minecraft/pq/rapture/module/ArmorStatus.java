package pq.rapture.module;

import java.util.Arrays;
import java.util.List;

import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.render.GUIUtil;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.EventPacketSend;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.EventRenderHUD;
import pq.rapture.rxdy.event.annotations.ETarget;

public class ArmorStatus extends Module implements HasValues {

	public ArmorStatus() {
		super(Protection.decrypt("599C8EEB6DF86E0BF16C1A78E9513BFD"), new String[] { "" }, Protection.decrypt("771840951458CA13EAC330832BE47284BD92E9F84935F27FB3EB18C688CD07B7"), Type.RENDER, "NONE", 0xFF1a6761);
	}

	@Override
	public boolean onEnable() {
		height = getScaledResolution().getScaledHeight() - 55;
		width = getScaledResolution().getScaledWidth() / 2 + (4 * 18);
		return super.onEnable();
	}
	
	@ETarget(eventClasses = { EventRenderHUD.class })
	public void renderGUI(EventRenderHUD render) {
		int w = -6;
		for (int i = 0; i <= 3; i++) {
			if (getPlayer().inventory.armorInventory[i] == null)
				continue;
			GUIUtil.renderItemAndEffect(getPlayer().inventory.armorInventory[i], width + w, height);
			w -= 18;
		}
		w = -6;
		for (int i = 0; i <= 3; i++) {
			if (getPlayer().inventory.armorInventory[i] == null)
				continue;
			GUIUtil.renderItemOverLay(getPlayer().inventory.armorInventory[i], width + w, height);
			w -= 18;
		}
	}

	public int height, width;

	private static final String H3IGHT = Protection.decrypt("E9343061BA9913330F5C471F3D7F24C2");
	private static final String W1DTH = Protection.decrypt("006DBEB4DB6E9C136A66787855C7FFDE");

	@Override
	public List<Value> getValues() {
		return Arrays.asList(new Value[] { new Value(W1DTH, 0, 800, 2), new Value(H3IGHT, 0, 500, 2) });
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(H3IGHT)) {
			return height;
		} else if (n.equals(W1DTH)) {
			return width;
		}
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(H3IGHT)) {
			height = (Integer) v;
		} else if (n.equals(W1DTH)) {
			width = (Integer) v;
		}
	}

}
