package pq.rapture.rxdy.event;

import net.minecraft.block.BlockPortal;
import net.minecraft.util.BlockPos;

/**
 * Created by Haze on 6/16/2015.
 * Project Rapture.
 */
public class EventBlockClick extends Event {

    private BlockPos block;

    public EventBlockClick(BlockPos block) {
        this.block = block;
    }

    public BlockPos getBlock() {
        return block;
    }

}
