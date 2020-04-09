package pq.rapture.module;

import oracle.jrockit.jfr.settings.EventDefault;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.rxdy.EventDeath;
import pq.rapture.rxdy.event.annotations.ETarget;

public class Ghost extends Module {
    public Ghost() {
        super("Ghost", new String[]{}, "Ghost on Vanilla servers.", Type.EXPLOITS, "NONE", 0xFFd3ffec);
    }

    private int color;
    private boolean inGhost;

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public boolean onEnable() {
        color = 0xFF232323;
        inGhost = false;
        return super.onEnable();
    }

    @ETarget(eventClasses = EventDeath.class)
    public void onDeath(EventDeath death){
        death.cancel();
        inGhost = true;
        color = 0xFFd3ffec;
    }

    @Override
    public boolean onDisable() {
        color = 0xFF232323;
        inGhost = false;
        return super.onDisable();
    }
}
