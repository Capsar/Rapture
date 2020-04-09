package pq.rapture.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.EventRenderGlobal;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.GameUtil;
import pq.rapture.util.PacketUtil;
import pq.rapture.util.RenderUtil;
import pq.rapture.util.TimeHelper;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

public class Nuker extends Module {


    public Nuker() {
        super("Nuker", new String[]{}, "Break fucking blocks", Type.WORLD, "NONE", 0xFFeee9e9);
        timer = new TimeHelper();
    }

    private float spitch, syaw;
    private Queue<BlockPos> posQueue;
    private BlockPos curretnBlock;
    private TimeHelper timer;
    private int blockDamage = 0;

    @ETarget(eventClasses = EventRenderGlobal.class)
    public void renderBlobal(){
        RenderUtil.preRender();
        double x = curretnBlock.getX() - getRenderManager().viewerPosX;
        double y = curretnBlock.getY() - getRenderManager().viewerPosY;
        double z = curretnBlock.getZ() - getRenderManager().viewerPosZ;
        GL11.glTranslated(x, y, z);
        GL11.glColor4d(0.4, 0.5, 0.2, 0.8);
        Block bl = Blocks.stone;
        RenderUtil.postRender();
    }

    public void drawBlock(BlockPos pos){
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        GL11.glPushMatrix();
        RenderUtil.preRender();
        GL11.glLineWidth(2F);
        GL11.glColor4d(0.25, 0.25, 0.50, 0.75);

        GL11.glBegin(GL11.GL_LINES);

        GL11.glVertex3d(x - 0.5, y, z - 0.5);
        GL11.glVertex3d(x - 0.5, y, z + 0.5);

        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x + 0.5, y, z - 0.5);

        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x - 0.5, y, z + 0.5);

        GL11.glVertex3d(x + 0.5, y, z - 0.5);
        GL11.glVertex3d(x - 0.5, y, z - 0.5);

        GL11.glVertex3d(x - 0.5, y, z - 0.5);
        GL11.glVertex3d(x - 0.5, y, z + 0.5);

        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x + 0.5, y, z - 0.5);

        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x - 0.5, y, z + 0.5);

        GL11.glVertex3d(x + 0.5, y, z - 0.5);
        GL11.glVertex3d(x - 0.5, y, z - 0.5);

        GL11.glEnd();

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColor4d(0.25, 0.25, 0.50, 0.25);
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glVertex3d(x - 0.5, y, z - 0.5);
        GL11.glVertex3d(x - 0.5, y, z - 0.5);
        GL11.glVertex3d(x + 0.5, y, z - 0.5);
        GL11.glVertex3d(x + 0.5, y, z - 0.5);

        GL11.glVertex3d(x - 0.5, y, z + 0.5);
        GL11.glVertex3d(x - 0.5, y, z + 0.5);
        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x + 0.5, y, z + 0.5);

        GL11.glVertex3d(x + 0.5, y, z - 0.5);
        GL11.glVertex3d(x + 0.5, y, z - 0.5);
        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x + 0.5, y, z + 0.5);

        GL11.glVertex3d(x - 0.5, y, z - 0.5);
        GL11.glVertex3d(x - 0.5, y, z - 0.5);
        GL11.glVertex3d(x - 0.5, y, z + 0.5);
        GL11.glVertex3d(x - 0.5, y, z + 0.5);

        GL11.glVertex3d(x - 0.5, y, z - 0.5);
        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x - 0.5, y, z - 0.5);

        GL11.glVertex3d(x + 0.5, y, z - 0.5);
        GL11.glVertex3d(x + 0.5, y, z + 0.5);
        GL11.glVertex3d(x - 0.5, y, z + 0.5);
        GL11.glVertex3d(x - 0.5, y, z - 0.5);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_CULL_FACE);

        RenderUtil.postRender();
        GL11.glPopMatrix();
    }

    @ETarget(eventClasses = EventPreMotion.class)
    public void preMotion(){
        List<BlockPos> tempList = new CopyOnWriteArrayList<>(getBlocksInCircleRadius(3));
        if(!tempList.isEmpty()) {
            if (timer.hasDelayRun(200)) {
                for (BlockPos pos : tempList) {
                    System.out.println("dong");
                    System.out.println(String.format("%s, %s", isBlockOkay(pos), blockExists(pos)));
                    if (isBlockOkay(pos) && blockExists(pos)) {
                        faceBlock(pos);
                        curretnBlock = pos;
                        breakBlockLegit(curretnBlock.getX(), curretnBlock.getY(), curretnBlock.getZ(), 100);
                        tempList.remove(pos);
                        System.out.println(String.format("Broke Block %s", GameUtil.getBlock(pos).getLocalizedName()));
                    } else {
                        tempList.remove(pos);
                    }
                }
            }
            timer.reset();
        }
    }

    public EnumFacing getEnumFacing(float posX, float posY, float posZ) {
        return EnumFacing.getFacingFromVector(posX, posY, posZ);
    }

    public void breakBlockLegit(int posX, int posY, int posZ, int delay) {
        if(this.blockDamage == 0) {
            getPlayerController().clickBlock(new BlockPos(posX, posY, posZ), this.getEnumFacing(posX, posY, posZ));
            addPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(posX, posY, posZ), this.getEnumFacing(posX, posY, posZ)));
            if(GameUtil.getBlock(posX, posY, posZ   ).getPlayerRelativeBlockHardness(getPlayer(), getWorld(), new BlockPos(posX, posY, posZ)) >= 1) {
                getPlayerController().clickBlock(new BlockPos(posX, posY, posZ), this.getEnumFacing(posX, posY, posZ));
                this.blockDamage = 0;
                return;
            }
        }
        this.blockDamage += GameUtil.getBlock(posX, posY, posZ).getPlayerRelativeBlockHardness(getPlayer(), getWorld(), new BlockPos(posX, posY, posZ)) / delay;
        if(this.blockDamage >= 1) {
            addPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(posX, posY, posZ), this.getEnumFacing(posX, posY, posZ)));
            getPlayerController().clickBlock(new BlockPos(posX, posY, posZ), this.getEnumFacing(posX, posY, posZ));
            this.blockDamage = 0;
        }
    }

    public boolean  blockExists(BlockPos pos){
        return !(GameUtil.getBlock(pos) instanceof BlockAir);
    }

    public void faceBlock(BlockPos pos){
        float[] data = GameUtil.getAngles((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), getPlayer());
        this.syaw = GameUtil.updateRotation(this.syaw, data[0], 6 + new Random().nextInt(6));
        this.spitch = GameUtil.updateRotation(this.spitch, data[1], 6 + new Random().nextInt(6));
        PacketUtil.addPlayerLookPacket(getPlayer().posX, getPlayer().posY, getPlayer().posZ, this.syaw, this.spitch, getPlayer().onGround);
    }

    public float[] change(BlockPos pos){
        float[] data = GameUtil.getAngles((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), getPlayer());
        return new float[]{data[0] - getPlayer().rotationYaw, data[1] - getPlayer().rotationPitch};
    }

    public boolean isBlockOkay(BlockPos pos){
        return change(pos)[0] <= 185 && change(pos)[1] <= 185 && (Block.getIdFromBlock(GameUtil.getBlock(pos)) != 0 || Block.getIdFromBlock(GameUtil.getBlock(pos)) != 7 );
    }

    @Override
    public boolean onDisable(){
        if(posQueue != null)
            posQueue.clear();
        return super.onDisable();
    }


    public Queue<BlockPos> getBlocksInCircleRadius(int radius){
        Queue<BlockPos> posQueue = new PriorityQueue<>((o1, o2) -> (int) getPlayer().getDistance(o1.getX(), o1.getY(), o1.getZ()) - (int) getPlayer().getDistance(o2.getX(), o2.getY(), o2.getZ()));
        int xp = (int) Math.round(getPlayer().posX);
        int yp = (int) Math.round(getPlayer().posY);
        int zp = (int) Math.round(getPlayer().posZ);
        for(int xb = radius; xb >= -2-radius + 1; xb++){
            for(int yb = radius; yb >= -2-radius+1; yb++){
                for (int zb = radius; zb >= -1 - radius + 1; zb++) {
                    int posX = xb + xp;
                    int posY = yb + yp;
                    int posZ = zb + zp;
                    BlockPos pos = new BlockPos(posX, posY, posZ);
                    if(isBlockOkay(pos) && !posQueue.contains(pos)) {
                        posQueue.add(pos);
                        break;
                    }
                }
            }
        }
        return posQueue;
    }

}
