package pq.rapture.module;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.rxdy.EventBlockBreak;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.TimeHelper;

/**
 * Created by Haze on 7/15/2015.
 */
public class CivBreak extends Module {

    public CivBreak(){
        super("CivBreak", null, "Breaks shit", Type.EXPLOITS, "E", 0xFF2323FA);
        timer = new TimeHelper();
    }


    private TimeHelper timer;
    private Minecraft mc = Minecraft.getMinecraft();


    private BlockPos block = null;


    public float[] getBlockRotations(BlockPos pos, float rotation) {
        double var4 = pos.getX() - mc.thePlayer.posX + 0.5;
        double var6 = pos.getZ() - mc.thePlayer.posZ + 0.5;
        double var8 = pos.getY() - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - 1);
        double var14 = (double) MathHelper.sqrt_double(var4 * var4 + var6 * var6);
        float yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(var8, var14) * 180.0D / Math.PI));
        yaw = updateRotation(mc.thePlayer.rotationYaw, yaw, rotation);
        pitch = updateRotation(mc.thePlayer.rotationPitch, pitch, rotation);
        return new float[]{yaw, pitch};
    }

    private float updateRotation(float par1, float par2, float par3) {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);
        if (var4 > par3) {
            var4 = par3;
        }
        if (var4 < -par3) {
            var4 = -par3;
        }
        return par1 + var4;
    }


    @ETarget(eventClasses = {EventBlockBreak.class})
    public void listenBlockBreak(EventBlockBreak e){
        if(block == null)
            block = e.getBlock();
    }

    @ETarget(eventClasses = {EventPreMotion.class})
    public void listenPreMotion(){
        if(block != null){
            if(timer.hasDelayRun(200)){
                mc.thePlayer.swingItem();
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, block, mc.thePlayer.getHorizontalFacing()));
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, block, mc.thePlayer.getHorizontalFacing()));
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block, mc.thePlayer.getHorizontalFacing()));
                timer.reset();
            }
        }
    }


    @Override
    public boolean onDisable() {
        block = null;
        return super.onDisable();
    }

}
