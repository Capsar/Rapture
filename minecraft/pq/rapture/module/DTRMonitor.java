package pq.rapture.module;

import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import pq.rapture.RaptureClient;
import pq.rapture.Wrapper;
import pq.rapture.command.base.Command;
import pq.rapture.module.base.Module;
import pq.rapture.module.base.Type;
import pq.rapture.rxdy.EventPacketGet;
import pq.rapture.rxdy.event.annotations.ETarget;
import pq.rapture.util.TimeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DTRMonitor extends Module {

    private boolean openForListMessage = false, scanningDTR = false, isScanningDTR, stopScraping;
    private String[] factionDTRBuffer = new String[]{};
    private TimeHelper timer = TimeHelper.getTimer();
    private int factionPages;
    public DTRMonitor() {
        super("DTRMon", new String[]{}, "DTR Monitor Wrapper and Holder", Type.CORE, "NONE", 0xFFffffff);
        this.setVisible(false);
        this.toggle(false);
        getCommandManager().commands.add(new Command(new String[]{"dtrm"}, "DTR monitor") {
            @Override
            protected void onCommand(String[] args, String message) {
                if(args.length > 2){
                    addChat(ChatEnum.NOTIFY, "Please just do .dtrm");
                } else {
                    new Thread(() -> {
                        openForListMessage = true;
                        sendPacket(new C01PacketChatMessage("/f list"));
                        while(!openForListMessage){
                            if(timer.hasDelayRun(1000)){
                                timer.reset();
                            }
                        }
                        if(factionPages != 0) {
                            addChat(ChatEnum.NOTIFY, String.format("Found %s faction pages, Scraping DTR...", factionPages));
                            for (int i = 0; i < factionPages; i++) {
                                if (!stopScraping)
                                    getPage(i);
                            }
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public boolean onDisable() {
        return false;
    }

    @ETarget(eventClasses = EventPacketGet.class)
    public void onPacketGet(EventPacketGet e){
        if(openForListMessage){
            if(e.getPacket() instanceof S02PacketChat){
                S02PacketChat chat = (S02PacketChat) e.getPacket();
                if(chat.func_148915_c().getFormattedText().contains("Faction List") && chat.func_148915_c().getFormattedText().contains("[")){
                    String fixed = "";
                    Matcher matcher = Pattern.compile("\\[(.*?)\\]").matcher(chat.func_148915_c().getFormattedText().replaceAll("§", ""));
                    while(matcher.find())
                        fixed = matcher.group();
                    System.out.println(String.format("gotti %s", fixed));
                    String temp = fixed.split("/")[0].replaceAll("§", "");
                    System.out.println(String.format("temp = %s", temp));
                    factionPages = Integer.parseInt(temp);
                    openForListMessage = false;
                }
            }
        } else if(scanningDTR){
            if(e.getPacket() instanceof S02PacketChat){
                S02PacketChat chat = (S02PacketChat) e.getPacket();
                System.out.println("hi mom!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                // faction line check 2skiddy4u
                if(chat.func_148915_c().getFormattedText().contains("Online") && chat.func_148915_c().getFormattedText().contains("DTR")){
                    System.out.println("HELLO!");
                    e.cancel();
                    List<String> strBuff = new ArrayList<>();
                    String curDTR = "-1";
                    Matcher DTRMatcher = Pattern.compile("\\((.*?)\\)").matcher(chat.func_148915_c().getFormattedText());
                    while(DTRMatcher.find())
                        curDTR = DTRMatcher.group();
                    //find matcher to use with amount of players online
                    //or i can do this (?)
                    String leftSide = chat.func_148915_c().getFormattedText().split("/")[0];
                    String usersOn = ""+leftSide.charAt(leftSide.length());
                    if(Integer.parseInt(usersOn) <= 0)
                        stopScraping = true;
                    strBuff.add(String.format("%s: dtr %s", leftSide, curDTR));
                    factionDTRBuffer = strBuff.toArray(new String[]{});
                } else if(chat.func_148915_c().getFormattedText().contains("Faction List")){
                    isScanningDTR = false;
                }
            }
        }
    }

    public String[] getPage(int page){
        sendPacket(new C01PacketChatMessage(String.format("/f list %s", page)));
        scanningDTR = true;
        final String[][] finalStringList = {new String[]{}};
        new Thread(() -> {
            while(isScanningDTR){
                if(timer.hasDelayRun(200))
                    timer.reset();
            }
            finalStringList[0] = factionDTRBuffer;
            for(String s : finalStringList[0])
                addChat(ChatEnum.NOTIFY, s);
            scanningDTR = false;
        }).start();
        return finalStringList[0];
    }


}
