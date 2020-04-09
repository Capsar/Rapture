package pq.rapture.rxdy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import pq.rapture.Wrapper;
import pq.rapture.render.RaptureRender;
import pq.rapture.rxdy.event.Event;

public class EventBoundingBox extends Event {

	private AxisAlignedBB boundingBox;
	private Block block;
	private int x, y, z;
	private BlockPos pos;
	private IBlockState state;

	public EventBoundingBox(AxisAlignedBB boundingBox, Block block, BlockPos pos, IBlockState state) {
		this.boundingBox = boundingBox;
		this.block = block;
		this.pos = pos;
		this.state = state;

		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
	}

	public IBlockState getState() {
		return state;
	}
	
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(AxisAlignedBB entityBoundingBox) {
		this.boundingBox = entityBoundingBox;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public BlockPos getBlockPos() {
		return pos;
	}
	
}
