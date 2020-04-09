package pq.rapture.render.click;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.module.OpenClickGUI;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.ModuleManager;
import pq.rapture.module.base.Type;
import pq.rapture.render.GUIUtil;
import pq.rapture.render.click.ClickItemFactory.ClickItem;
import pq.rapture.util.FontUtil;
import pq.rapture.util.TimeHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Haze
 */
public class ClickGUI extends GuiScreen {

	private ScaledResolution resolution;
	private List<ClickItem> itemList = new ArrayList<>(), definitiveItemList = new ArrayList<>();
	private TimeHelper timer = TimeHelper.getTimer(), cursorTimer = new TimeHelper();
	public float listY, nameY, mouseFirstX, mouseFirstY;
	public double indiX = 0, indiY = 0, indiWidth = 5, indiHeight, indiClickY;
	public boolean isDoneDown = false, isDraggingIndicator, shouldDrawCursor;
	public float leftBoxWidth = 100, editableBoxWidth = 105;
	public int event = Mouse.getEventDWheel();
	public StringBuilder searchText;
	public Module foundModule;
	private static ClickGUI instance = null;

	public static ClickGUI getInstance() {
		if (instance == null) instance = new ClickGUI();
		return instance;
	}

	public ClickGUI() {
		searchText = new StringBuilder();
		for (Type type : Type.values()) {
			if (type != Type.CORE) {
				for (Module module : Wrapper.getModuleManager().getModules(type)) {
					if (module instanceof OpenClickGUI) continue;
					ClickItem item = ClickItemFactory.getItem(module, 4 + Wrapper.getFontRenderer().FONT_HEIGHT);
					itemList.add(item);
				}
			}
		}
		definitiveItemList = itemList;
		isDoneDown = false;
		isDraggingIndicator = false;
		nameY = 0;
		listY = 0;
		indiX = 0;
		indiY = 0;
		indiWidth = 5;
		indiHeight = itemList.size() / 4;
		if (indiHeight < 30) indiHeight = 30;
	}

	@Override
	public void onGuiClosed() {
		editableBoxWidth = 95;
		isDoneDown = false;
		isDraggingIndicator = false;
		nameY = 0;
		listY = 0;
		indiX = 0;
		indiY = 0;
		indiWidth = 5;
		indiHeight = itemList.size() / 4;
		if (indiHeight < 30) indiHeight = 30;
		super.onGuiClosed();
	}

	@Override
	public void onResize(Minecraft mcIn, int p_175273_2_, int p_175273_3_) {
		super.onResize(mcIn, p_175273_2_, p_175273_3_);
		isDoneDown = false;
		isDraggingIndicator = false;
		nameY = 0;
		listY = 0;
		indiX = 0;
		indiY = 0;
		indiWidth = 5;
		indiHeight = itemList.size() / 4;
		for (ClickItem item : itemList) {
			item.showProperties = false;
		}
		if (indiHeight < 30) indiHeight = 30;
	}

	public Module findModule(String text) {
		for (Module m : ModuleManager.modules) {
			if (m.getName().contains(text)) return m;
		}
		return null;
	}

	public ArrayList<ClickItem> getModules(String prefix) {
		return (ArrayList<ClickItem>) itemList.stream().filter(item -> item.getModule().getName().toLowerCase().contains(prefix.toLowerCase())).collect(Collectors.toList());
	}

	public ArrayList<ClickItem> getMoudlesInType(Type type){
		return (ArrayList<ClickItem>) definitiveItemList.stream().filter(item -> item.getModule().getType() == type).collect(Collectors.toList());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (searchText.toString().isEmpty()) {
			itemList = definitiveItemList;
			foundModule = null;
		} else {
			ArrayList<ClickItem> newItemList = getModules(searchText.toString());
			for(Type type : Type.values()){
				if(searchText.toString().equalsIgnoreCase(type.name())){
					newItemList = getMoudlesInType(type);
					break;
				}
			}
			if (newItemList.size() >= 1) {
				itemList = newItemList;
			} else if (!itemList.containsAll(definitiveItemList)) {
				itemList = definitiveItemList;
			}
		}

		if (cursorTimer.hasDelayRun(500)) {
			shouldDrawCursor = !shouldDrawCursor;
			cursorTimer.reset();
		}

		if (!searchText.toString().isEmpty() && findModule(searchText.toString()) != null) {
			foundModule = findModule(searchText.toString());
		}

		GL11.glPushMatrix();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		double totalDelay = 0.5;
		int fps = Integer.valueOf(Wrapper.getMinecraft().debug.split(" ")[0]);
		int distance = Wrapper.getScaledResolution().getScaledHeight();
		double delay = fps * totalDelay;

		if (nameY < Wrapper.getScaledResolution().getScaledHeight()) {
			timer.reset();
			nameY += distance / delay;
			if (nameY > Wrapper.getScaledResolution().getScaledHeight()) nameY = Wrapper.getScaledResolution().getScaledHeight();
		} else {
			isDoneDown = true;
		}

		GUIUtil.drawRect(0, 0, leftBoxWidth, nameY - 21, 0xFF232323);
		GUIUtil.drawRect(0, nameY - 21, leftBoxWidth, Wrapper.getScaledResolution().getScaledHeight(), 0xFF383838);
		if (searchText.toString().isEmpty()) {
			FontUtil.drawScaledString("Rapture", 20, nameY - 16, 0xFFffffff, 1.5F, true);
		} else {
			FontUtil.drawScaledString(searchText.toString(), 8, nameY - 16, itemList.size() >= 1 ? 0xFFffffff : 0xFFf42727, 1.1F, true);
			if (shouldDrawCursor) {
				float w = 8.2F + (fontRendererObj.getStringWidth(searchText.toString()) * 1.1F);
				GUIUtil.drawRect(w, nameY - 16, w + 1.6, nameY - 6, 0xFFffffff);
			}
		}
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GUIUtil.scissor(0, 0, Wrapper.getScaledResolution().getScaledWidth(), nameY - 20);
	
		// modules
		float height = listY;
		for (ClickItem item : itemList) {
			item.drawItem(mouseX, mouseY, height);
			height += item.getHeight();
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GUIUtil.scissor(0, 0, 5, nameY - 20);

		// scrollbar
		GUIUtil.drawRect(0, 0, 5, nameY - 20, 0xFF383838);

		// indicator
		indiY = listY * multi();

		GUIUtil.drawRect(indiX, indiY, indiX + indiWidth, indiY + indiHeight, 0xFF656565);

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		int dWheel = Mouse.getDWheel();
		shiftList(dWheel / 17.0F);
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GL11.glPopMatrix();
	}

	private double multi() {
		double scrollMultiplier = -1.0;
		float height = 0;
		for (ClickItem item : itemList)
			height += item.getHeight();
		double bottom = nameY - 20;
		scrollMultiplier *= (bottom - indiHeight) / (height - bottom);
		return scrollMultiplier;
	}

	private void shiftList(float amount) {
		listY += amount;
		float height = 0;
		for (ClickItem item : itemList)
			height -= item.getHeight();
		
		if (listY > 0 || -height < nameY + 20) listY = 0;
		else {
			if (listY - nameY + 20 < height) listY = height + nameY - 20;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (ClickItem item : itemList) {
			if (item.handleKeyPress(keyCode)) return;
		}
		if ((Keyboard.KEY_DELETE == keyCode || keyCode == 1)) {
			Wrapper.getMod(pq.rapture.module.OpenClickGUI.class).toggle(true);
		} else if (keyCode == Keyboard.KEY_GRAVE) {
			searchText.setLength(0);
		} else if (keyCode == Keyboard.KEY_BACK && searchText.length() > 0) {
			searchText.setLength(searchText.length() - 1);
		} else if (Character.isAlphabetic(typedChar) || Character.isSpaceChar(typedChar)) {
			searchText.append(typedChar);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// 0 = left 1 = right
		if (mouseButton == 0) {
			if (mouseX > 0 && mouseX < indiX + indiWidth) {
				if (mouseY > indiY && mouseY < indiY + indiHeight) {
					indiClickY = mouseY - indiY;
				} else {
					indiClickY = indiHeight / 2;
				}

				isDraggingIndicator = true;
				mouseFirstX = mouseX;
				mouseFirstY = mouseY;
				listY = (float) ((mouseY - indiClickY) / multi());
				shiftList(0);
			} else {
				float height = listY;
				for (ClickItem item : itemList) {
					item.handleClick(mouseX, mouseY, height, 0);
					height += item.getHeight();
				}
			}
		} else {
			float height = listY;
			for (ClickItem item : itemList) {
				if (mouseButton == 1 && (mouseX < 0 || mouseX > leftBoxWidth)) continue;
				item.handleClick(mouseX, mouseY, height, mouseButton);
				height += item.getHeight();
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		isDraggingIndicator = false;
		mouseFirstX = -1;
		mouseFirstY = -1;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		// 0 = left 1 = right
		if (clickedMouseButton == 0) {
			if (mouseFirstX > 0 && mouseFirstX < indiX + indiWidth) {
				listY = (float) ((mouseY - indiClickY) / multi());
				shiftList(0);
			} else {
				float height = listY;
				for (ClickItem item : itemList) {
					item.handleDrag(mouseX, mouseY, height);
					height += item.getHeight();
				}
			}
		}
	}
}
