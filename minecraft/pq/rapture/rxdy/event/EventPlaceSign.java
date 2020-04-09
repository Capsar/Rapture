package pq.rapture.rxdy.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;

/**
 * Created by Haze on 7/3/2015.
 */
public class EventPlaceSign extends Event {

    private TileEntitySign sign;
    private Boolean shouldOpenGui, shouldPlaceBlock;

    public Boolean getShouldOpenGui() {
        return shouldOpenGui;
    }

    public void setShouldOpenGui(Boolean shouldOpenGui) {
        this.shouldOpenGui = shouldOpenGui;
    }

    public Boolean getShouldPlaceBlock() {
        return shouldPlaceBlock;
    }

    public void setShouldPlaceBlock(Boolean shouldPlaceBlock) {
        this.shouldPlaceBlock = shouldPlaceBlock;
    }

    public EventPlaceSign(TileEntitySign sign) {
        this.sign = sign;
    }

    public TileEntitySign getSign() {
        return sign;
    }

    public void setSign(TileEntitySign sign) {
        this.sign = sign;
    }
}
