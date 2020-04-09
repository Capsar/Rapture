package pq.rapture.render.tab;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import pq.rapture.RaptureClient;
import pq.rapture.RaptureClient.KeyBind;
import pq.rapture.Wrapper;
import pq.rapture.module.HUDHelper;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.HasValues.Value;
import pq.rapture.module.base.HasValues.Value.ValueType;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.ModuleManager;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.render.GUIUtil;
import pq.rapture.render.click.ClickGUI;
import pq.rapture.util.FontUtil;
import pq.rapture.util.NumberUtil;

public class TabGUI extends GuiScreen {

	public TabGuiItem mainTabItem;
	public TabGuiItem selectedItem;

	public TabGUI() {
		mainTabItem = new TabGuiItem();
	}

	public void drawScreen(float x, float y) {
		if (Wrapper.getMinecraft().currentScreen instanceof ClickGUI) return;
		mainTabItem.drawItem(x, y);
	}

	public void onKeyDown(KeyBind key) {
		mainTabItem.update();
		if (key.getCode() == Keyboard.KEY_PRIOR) {
			if (selectedItem.editable) {
				TabGuiItem t = selectedItem;
				if (t.value.getType() == ValueType.NORMAL && !t.value.getVClass().equals(Boolean.class)) {
					t.editableProperties.setValue(t.value.getName(), NumberUtil.increase(t));
				}
			}
		} else if (key.getCode() == Keyboard.KEY_NEXT) {
			if (selectedItem.editable) {
				TabGuiItem t = selectedItem;
				if (t.value.getType() == ValueType.NORMAL && !t.value.getVClass().equals(Boolean.class)) {
					t.editableProperties.setValue(t.value.getName(), NumberUtil.decrease(t));
				}
			}
		}
	}

	public void keyPressed(KeyBind key) {
		if (key.getCode() == Keyboard.KEY_PRIOR) {
			if (selectedItem.editable) {
				TabGuiItem t = selectedItem;
				if (t.value.getType() == ValueType.NORMAL && !t.value.getVClass().equals(Boolean.class)) {
					t.editableProperties.setValue(t.value.getName(), NumberUtil.increase(t));
					RaptureClient.safeClient();
				}
			}
		} else if (key.getCode() == Keyboard.KEY_NEXT) {
			if (selectedItem.editable) {
				TabGuiItem t = selectedItem;
				if (t.value.getType() == ValueType.NORMAL && !t.value.getVClass().equals(Boolean.class)) {
					t.editableProperties.setValue(t.value.getName(), NumberUtil.decrease(t));
					RaptureClient.safeClient();
				}
			}
		} else if (key.getCode() == (Keyboard.KEY_RETURN)) {
			TabGuiItem t = selectedItem;
			if (t.module != null) {
				t.module.toggle(true);
			} else if (t.value != null && t.value.getVClass().equals(Boolean.class)) {
				boolean bool = (boolean) t.editableProperties.getValue(t.value.getName());
				t.editableProperties.setValue(t.value.getName(), !bool);
				RaptureClient.safeClient();
			}
		} else if (key.getCode() == (Keyboard.KEY_UP)) {
			if (mainTabItem.browsing) {
				selectedItem.browsing = false;
				selectedItem.selected = false;
				selectedItem = selectedItem.parent.tabItems.get(selectedItem.parent.tabItems.indexOf(selectedItem) - 1);
				selectedItem.selected = true;
			}
		} else if (key.getCode() == (Keyboard.KEY_DOWN)) {
			if (mainTabItem.browsing) {
				selectedItem.browsing = false;
				selectedItem.selected = false;
				selectedItem = selectedItem.parent.tabItems.get(selectedItem.parent.tabItems.indexOf(selectedItem) + 1);
				selectedItem.selected = true;
			}
		} else if (key.getCode() == (Keyboard.KEY_LEFT)) {
			if (!selectedItem.parent.name.equals(selectedItem.name) && !selectedItem.name.isEmpty()) {
				selectedItem.selected = false;
				selectedItem = selectedItem.parent;
				selectedItem.browsing = false;
				selectedItem.selected = true;
			}
		} else if (key.getCode() == (Keyboard.KEY_RIGHT) && !selectedItem.tabItems.isEmpty()) {
			selectedItem.browsing = true;
			selectedItem = selectedItem.getChild();
			selectedItem.selected = true;
		}
		mainTabItem.update();
	}

	public class TabGuiItem {
		// //0xffe766 || 0xFFFFFF || 0xfffaf0
		public int color[] = new int[] { Protection.getColor("90B4FBA1B55E4D5E280322F57274C294"), Protection.getColor("6DBA53E4E941DFB6EB953480493B7B95"), Protection.getColor("8E719B8AF2D3CE126AC5939856F66E16") };
		public String name = "";
		public boolean selected = false;
		public boolean browsing = false;
		public TabGuiItem parent = this;

		public Type type = null;
		public TabList<TabGuiItem> tabItems = new TabList<>();

		public Module module = null;

		public Value value = null;
		public boolean booleanState = false;
		public boolean editable = false;
		public HasValues editableProperties = null;

		public TabGuiItem() {
			for (Type t : Type.values()) {
				if (t == Type.CORE) continue;
				tabItems.add(new TabGuiItem(t, this));
			}
			this.browsing = true;
			this.selected = true;
			selectedItem = tabItems.get(0);
			selectedItem.selected = true;
			selectedItem.browsing = true;
		}

		public TabGuiItem(Type type, TabGuiItem parent) {
			this.parent = parent;
			this.name = Type.getText(type);
			this.type = type;
			for (Module m : ModuleManager.getModules(type))
				tabItems.add(new TabGuiItem(m, this));
		}

		public TabGuiItem(Module module, TabGuiItem parent) {
			this.parent = parent;
			this.name = module.getName();
			this.module = module;
			if (this.module instanceof HasValues) {
				HasValues hep = (HasValues) this.module;
				for (Value v : hep.getValues()) {
					if (v.getType() == ValueType.SAVING || v.getType() == ValueType.DISPLAYLIST) continue;
						tabItems.add(new TabGuiItem(v, hep, this));
				}
			}
		}

		public TabGuiItem(Value value, HasValues hep, TabGuiItem parent) {
			this.parent = parent;
			this.value = value;
			this.editableProperties = hep;
			this.name = value.getName() + (NumberUtil.isBoolean(this.editableProperties, value) ? "" : (" [" + hep.getValue(value.getName()) + "]"));
			if (this.value.getType() == ValueType.NORMAL) {
				this.editable = true;
			}
			if (NumberUtil.isBoolean(this.editableProperties, value)) {
				this.booleanState = true;
			}
		}

		public TabGuiItem getChild() {
			if (!tabItems.isEmpty()) { return tabItems.get(0); }
			return this;
		}

		public void drawItem(float x, float y) {
			float height = this.tabItems.isEmpty() ? 0 : this.tabItems.size() * 9.5F + 2;
			float width = 0;

			for (TabGuiItem tab : this.tabItems) {
				String name = tab.name + (tab.selected ? tab.browsing ? " >" : " <" : "");
				if (HUDHelper.TTFFont) {
					if (!tab.tabItems.isEmpty() && tab.equals(Wrapper.getTabGui().selectedItem)) {
						name += ">";
					}
				}
				float stringWidth = (HUDHelper.TTFFont ? Wrapper.getFont().getStringWidth(name) : Wrapper.getFontRenderer().getStringWidth(name)) * (this.selected ? 1.05F : 1.0F);
				if (stringWidth > width) {
					width = stringWidth;
				}
			}
			width += HUDHelper.TTFFont ? 8 : 6;
			if (height != 0 && this.selected && browsing) {
				GUIUtil.drawSexyRect(x, y, x + width - 3, y + height, 0x50000000, 0x45000000);
				for (int i = 0; i < this.tabItems.size(); i++) {
					TabGuiItem tab = this.tabItems.get(i);
					String name = tab.name + (tab.selected ? tab.browsing ? " >" : " <" : "");
					if (HUDHelper.TTFFont) {
						if (!tab.tabItems.isEmpty() && tab.equals(Wrapper.getTabGui().selectedItem)) {
							name += ">";
						}
						FontUtil.drawScaledTTF(name, x + 2, y + (i * 9.5F) + 2, ((tab.module != null && tab.module.getState()) ? color[0] : (tab.editable && tab.booleanState && NumberUtil.isBoolean(tab.editableProperties, tab.value) ? color[0]
								: color[1])), tab.selected ? 1.05F : 1, true);
					} else {
						FontUtil.drawScaledString(name, x + 2, y + (i * 9.5F) + 2, ((tab.module != null && tab.module.getState()) ? color[0] : (tab.editable && tab.booleanState && NumberUtil.isBoolean(tab.editableProperties, tab.value) ? color[0]
								: color[2])), tab.selected ? 1.05F : 1, true);
					}
					if (!tab.tabItems.isEmpty() && tab.equals(Wrapper.getTabGui().selectedItem) && !HUDHelper.TTFFont) {
						double hei = y + (i * 9.5F);
						double w = x + (Wrapper.getFontRenderer().getStringWidth(name) * 1.04F);
						GUIUtil.drawRect(w + 1, hei + 5, w + 2, hei + 6, (tab.module != null && tab.module.getState()) ? 0xFFFFE766 : 0xFFFFFAF0);
					}
				}
			}
			if (browsing) {
				for (int i = 0; i < this.tabItems.size(); i++) {
					this.tabItems.get(i).drawItem(x + width - 1, y + (i * 9.5F));
				}
			}
		}

		public void update() {
			for (TabGuiItem tab : this.tabItems) {
				tab.update();
				if (tab.value != null) {
					if (!NumberUtil.isBoolean(tab.editableProperties, tab.value)) {
						tab.name = tab.value.getName() + " [" + tab.editableProperties.getValue(tab.value.getName()) + "]";
					} else {
						tab.booleanState = NumberUtil.getBoolean(tab);
					}

				}
			}
		}
	}

}
