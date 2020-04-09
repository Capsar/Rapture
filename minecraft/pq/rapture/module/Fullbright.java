package pq.rapture.module;

import net.minecraft.client.multiplayer.WorldClient;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.util.TimeHelper;

/**
 * Created by Haze on 6/17/2015. Project Rapture.
 */
public class Fullbright extends Module {

	private TimeHelper helper;
	private float curLight = 0;
	private Thread enableThread, disableThread;
	
	public Fullbright() {
		super(Protection.decrypt("B98E7262FD2580D7177145EB7BBADD7E"), new String[] {}, Protection.decrypt("C7142FD4C1DB69F65089E330130AB3D96A41F60204A39794908B7329FC609ECD"), Type.RENDER, "B", 0xFF21ffcb);
		helper = new TimeHelper();
		curLight = 0;
	}


	public void editTable(WorldClient world, float value) {
		if (world == null) return;
		float[] table = world.provider.lightBrightnessTable;
		for (int index = 0; index < table.length; index++)
			table[index] = table[index] > value ? table[index] : value;
		world.provider.lightBrightnessTable = table;
	}
	
	
	
	@Override
	public boolean onEnable() {
		getGameSettings().gammaSetting = 31;
		return super.onEnable();
	}

	@Override
	public boolean onDisable() {
		getGameSettings().gammaSetting = 0;
		return super.onDisable();
	}

}
