package com.spiritlight.hdm;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import java.util.Arrays;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = HDM.MODID, name = HDM.NAME, version = HDM.VERSION)
public class HDM
{
    public static final String MODID = "hdm";
    public static final String NAME = "Hunted Death Messages";
    public static final String VERSION = "1.0";
    public static String prefix = "§6[§cHDM§6]";
    public static List<String> customMessages = new ArrayList<>();
    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException
    {
        MinecraftForge.EVENT_BUS.register(new MsgEventSpirit());
        ClientCommandHandler.instance.registerCommand(new CmdSpirit());
        ConfigSpirit.getConfig();
        if(customMessages.isEmpty()) {
            // Initialize a row of default messages
            String[] s = new String[] {
                    "[player] skill issued a little too hard.",
                    "Can anyone defeat you? Certainly not [player].",
                    "[player] found the meaning of life.",
                    "[player] failed to dodge the tower aura.",
                    "[player] didn't have enough Agility.",
                    "[player] was in linnyflower's level range.",
                    "[player] was struck down by Salted after duping too many bombs.",
                    "[player] used a shortcut straight to hell.",
                    "You were clearly a better fighter than [player].",
                    "[player] took the easy way out."
            };
            customMessages = new ArrayList<>(Arrays.asList(s));
            CmdSpirit.refreshConfig();
        }
    }
}
