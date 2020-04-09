package pq.rapture.module;

import net.minecraft.network.play.client.C03PacketPlayer;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.rxdy.EventPreMotion;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.TimeHelper;

public class TestModule extends Module {

    private TimeHelper timer = TimeHelper.getTimer();

    public TestModule() {
        super("b", new String[]{}, "A", Type.EXPLOITS, "NONE", 0xFF232311);
    }

    @ETarget(eventClasses = EventPreMotion.class)
    public void onPreMotion(){
        if(timer.hasDelayRun(333)) {
            for (int i = 0; i < 2; i++) {
                switch (getPlayer().getHorizontalFacing()){
                    case NORTH:
                        //-z
                        System.out.println(i);
                        if(i==0) {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX - 0.3, getPlayer().posY, getPlayer().posZ - 0.5, getPlayer().onGround));
                        } else {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX + 0.6, getPlayer().posY, getPlayer().posZ - 0.5, getPlayer().onGround));
                        }
                        break;
                    case SOUTH:
                        //+z
                        if(i==0) {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX - 0.3, getPlayer().posY, getPlayer().posZ + 0.5, getPlayer().onGround));
                        } else {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX + 0.6, getPlayer().posY, getPlayer().posZ + 0.5, getPlayer().onGround));
                        }
                        break;
                    case EAST:
                        //+x
                        if(i==0) {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX + 0.5, getPlayer().posY, getPlayer().posZ - 0.3, getPlayer().onGround));
                        } else {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX + 0.5, getPlayer().posY, getPlayer().posZ + 0.6, getPlayer().onGround));
                        }
                        break;
                    case WEST:
                        //-x
                        if(i==0) {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX - 0.5, getPlayer().posY, getPlayer().posZ - 0.3, getPlayer().onGround));
                        } else {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(getPlayer().posX - 0.5, getPlayer().posY, getPlayer().posZ + 0.6, getPlayer().onGround));
                        }
                        break;
                }
            }
            timer.reset();
        }
    }

}
