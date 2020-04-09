package pq.rapture.rxdy;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import pq.rapture.rxdy.event.Event;

/**
 * Created by Haze on 7/15/2015.
 */
public class EventBlockBreak extends Event {
    private BlockPos block;
	private EnumFacing side;

    public EventBlockBreak(BlockPos block, EnumFacing side) {
        this.block = block;
		this.side = side;
    }

    public EnumFacing getSide() {
		return side;
	}
    
    public BlockPos getBlock() {
        return block;
    }

    public void setBlock(BlockPos block) {
        this.block = block;
    }
}
