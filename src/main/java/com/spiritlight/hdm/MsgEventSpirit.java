package com.spiritlight.hdm;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;

public class MsgEventSpirit {
    private static final Random random = new Random();
    @SubscribeEvent
    public void onMessage(ClientChatReceivedEvent event) {
        // Split messages
        // String[] message = event.getMessage().getUnformattedText().split(" "); //Message Array
        int index = match(event.getMessage().getUnformattedText());
        if (index != -1) { // Name matched
            // Make new message string
            String nickname = event.getMessage().getUnformattedText().substring(0, index).trim();
            String playerName = ""; // Get player name
            boolean isNicknamed = false;
            try {
                playerName = event.getMessage().getSiblings().get(0).getStyle().getHoverEvent().getValue().getUnformattedText();
                isNicknamed = true;
            } catch (NullPointerException|IndexOutOfBoundsException ignored) {
                // don't care didnt ask
            }
            // Message prefix added with dark red modifier
            String deathMessage = "§6" + HDM.customMessages.get(random.nextInt(HDM.customMessages.size()));
            // Declare initial types
            TextComponentString text;
            Style style;
            // Behavior
            text = new TextComponentString(TextParserSpirit.parseString(deathMessage, (isNicknamed ? playerName : nickname)));
            if(isNicknamed) {
                style = text.getStyle();
                style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(nickname + " is " + playerName)));
                text.setStyle(style);
            }
            event.setMessage(text);
        }
    }

    private int match(String s) {
        for (String deathMessage : deathMessages)
            if (s.contains(deathMessage))
                return s.indexOf(deathMessage);
        return -1;
    }

    private static final String[] deathMessages = new String[] {
            "has died.",
            "passed away.",
            "kicked the bucket.",
            "was killed.",
            "has perished.",
            "met their demise.",
            "is six feet under.",
            "has been slain.",
            "bit the dust.",
            "met their fate."
    };
}
