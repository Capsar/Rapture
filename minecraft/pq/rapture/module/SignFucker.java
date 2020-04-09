package pq.rapture.module;

import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.util.ChatComponentText;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.rxdy.event.EventPlaceSign;
import pq.rapture.rxdy.event.annotations.ETarget;

import java.util.Random;

/**
 * Created by Haze on 7/3/2015.
 */
public class SignFucker extends Module {

    public Random random;

    public SignFucker(){
        super("SignFucker", null, "Fucks up signs when you place them.", Type.EXPLOITS, "NONE", 0xFFeffeff);
        random = new Random();
    }

    @ETarget(eventClasses = EventPlaceSign.class)
    public void onPlaceSign(EventPlaceSign sign){
        /* EXPERIMENTAL AS FUCK */
        sign.setShouldPlaceBlock(true);
        sign.setShouldOpenGui(false);
        sendPacket(new C12PacketUpdateSign(sign.getSign().getPos(), sign.getSign().signText));
        //4 lines
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 4; i++){
            for(int i2 = 0; i2 < 15; i2++){
                builder.append((char) random.nextInt());
            }
            if(sign.getSign() != null) {
                ((ChatComponentText) sign.getSign().signText[i]).setText(builder.toString());
                builder.delete(0, builder.length());
            }
        }
    }

}
