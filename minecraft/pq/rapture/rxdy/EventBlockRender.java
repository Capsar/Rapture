package pq.rapture.rxdy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import pq.rapture.Wrapper;
import pq.rapture.render.RaptureRender;
import pq.rapture.rxdy.event.Event;

public class EventBlockRender extends Event {

	private IBlockState state;
	private BlockPos pos;
	private Block block;
	private int x, y, z;

	public EventBlockRender(IBlockState state, BlockPos pos) {
		this.block = state.getBlock();
		this.state = state;
		this.pos = pos;
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();

	}

	public BlockPos getPos() {
		return pos;
	}

	public IBlockState getState() {
		return state;
	}
	
	public Block getBlock() {
		return block;
	}
}
