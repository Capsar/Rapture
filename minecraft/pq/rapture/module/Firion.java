package pq.rapture.module;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import pq.rapture.module.base.HasValues;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.protection.Protection;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;

public class Firion extends Module implements HasValues {

	boolean potions = true;
	boolean fire = true;
	boolean regen = false;
	private int delay;

	public Firion() {
		super(Protection.decrypt("4393592F5931CF96E01253CDE9C33830"), new String[] { "" }, Protection.decrypt("947C89E37F23E78E0E50CCE4F44A258B2BA14329DD3339BA748823B15D8A17ED"), Type.COMBAT, "NONE", 0xFFed9e71);
	}

	@ETarget(eventClasses = { EventPreMotion.class })
	public void onEvent(EventPreMotion pre) {
		if (potions) {
			if (getPlayer().isPotionActive(Potion.blindness)) {
				getPlayer().removePotionEffect(Potion.blindness.id);
			}

			if (getPlayer().isPotionActive(Potion.confusion)) {
				getPlayer().removePotionEffect(Potion.confusion.id);
			}

			if (getPlayer().isPotionActive(Potion.digSlowdown)) {
				getPlayer().removePotionEffect(Potion.digSlowdown.id);
			}
		}

		if (getPlayer().onGround) {
			if ((getPlayer().isBurning() && !getPlayer().isPotionActive(Potion.fireResistance) && !getPlayer().isImmuneToFire()
					&& !getPlayer().isInWater() && !getPlayer().isInsideOfMaterial(Material.lava) && !getPlayer().isInsideOfMaterial(
					Material.fire))
					&& fire) {
				for (int i = 0; i < 120; i++) {
					sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX, getPlayer().posY, getPlayer().posZ, getPlayer().onGround));
				}
			}
		}

		for (Iterator<PotionEffect> effect = getPlayer().getActivePotionEffects().iterator(); effect.hasNext();) {
			PotionEffect effectt = effect.next();
			Potion potion = Potion.potionTypes[effectt.getPotionID()];
			if (potion != Potion.poison && potion != Potion.regeneration && potion != Potion.moveSlowdown && potion != Potion.hunger
					&& potion != Potion.weakness) {
				continue;
			} else if (regen && potion == Potion.regeneration && (getPlayer().getHealth() >= 20 || getPlayer().isUsingItem()))
				continue;

			for (int index = 0; index < effectt.getDuration() / 15; index++) {
				sendPacket(new C03PacketPlayer(getPlayer().onGround));
			}
			break;
		}
	}

	private static final String POTIONSS = Protection.decrypt("8C6519457A2AF7B05D3C24B019D21661");
	private static final String FIREE = Protection.decrypt("E1C7383C42C196E17944570813BE2D13");
	private static final String REGENN = Protection.decrypt("3D431074CD141765EAE32CC98FBC6638");
	private static final Value[] PARAMETERS = new Value[] { new Value(POTIONSS, false, true), new Value(FIREE, false, true),
			new Value(REGENN, false, true) };

	@Override
	public List<Value> getValues() {
		return Arrays.asList(PARAMETERS);
	}

	@Override
	public Object getValue(String n) {
		if (n.equals(POTIONSS))
			return potions;
		else if (n.equals(FIREE))
			return fire;
		else if (n.equals(REGENN))
			return regen;
		return null;
	}

	@Override
	public void setValue(String n, Object v) {
		if (n.equals(POTIONSS))
			potions = (Boolean) v;
		else if (n.equals(FIREE))
			fire = (Boolean) v;
		else if (n.equals(REGENN))
			regen = (Boolean) v;

	}

}
