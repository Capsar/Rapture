package pq.rapture.module;

import org.lwjgl.input.Keyboard;

import pq.rapture.Wrapper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.render.click.ClickGUI;

/**
 * @author Haze
 */
public class OpenClickGUI extends Module {

	public OpenClickGUI() {
		super("ClickGUI", null, "A Click Gui", Type.RENDER, "RSHIFT", 0xFF232323);
		setVisible(false);
	}

	@Override
	public boolean onEnable() {
		getMinecraft().displayGuiScreen(ClickGUI.getInstance());
		return super.onEnable();
	}

	@Override
	public boolean onDisable() {
		getMinecraft().displayGuiScreen(null);
		return super.onDisable();
	}

}
