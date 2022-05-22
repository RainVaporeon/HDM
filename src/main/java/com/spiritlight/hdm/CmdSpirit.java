package com.spiritlight.hdm;

import net.minecraft.command.CommandBase;
// import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class CmdSpirit extends CommandBase {
    Style style;

    @Override
    public @NotNull String getName() {
        return "hdm";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public @NotNull String getUsage(@NotNull ICommandSender iCommandSender) {
        return "/hdm (add/remove/list)";
    }

    @Override
    public void execute(@NotNull MinecraftServer minecraftServer, @NotNull ICommandSender sender, String @NotNull [] args) { // yeet CommandException
        if (args.length == 0 || args[0].equals("help")) {
            sender.sendMessage(new TextComponentString(HDM.prefix + " /hdm list/add/remove <extra..>"));
        } else {
            switch (args[0]) {
                case "add":
                    if (args.length < 2) {
                        sender.sendMessage(new TextComponentString(HDM.prefix + " §cYou need to supply a message!"));
                    } else {
                        String[] a = Arrays.copyOfRange(args, 1, args.length);
                        String m = String.join(" ", a);
                        add(m, sender);
                    }
                    break;
                case "remove":
                    if (args.length < 2) {
                        sender.sendMessage(new TextComponentString(HDM.prefix + " §cYou need to supply a message!"));
                    } else {
                        String[] a = Arrays.copyOfRange(args, 1, args.length);
                        String m = String.join(" ", a);
                        remove(m, sender);
                    }
                    break;
                case "list":
                    list(sender);
                    break;
                case "format":
                    sender.sendMessage(new TextComponentString(HDM.prefix + "Custom Message Formatting (For all messages)"));
                    sender.sendMessage(new TextComponentString(HDM.prefix + "{name}: Player name"));
                    sender.sendMessage(new TextComponentString(HDM.prefix + "{NAME}: Text coloring"));
                    sender.sendMessage(new TextComponentString(HDM.prefix + "§4DARK_RED §cRED §6GOLD §eYELLOW §2DARK_GREEN"));
                    sender.sendMessage(new TextComponentString(HDM.prefix + "§aGREEN §bAQUA §3DARK_AQUA §1DARK_BLUE §9BLUE"));
                    sender.sendMessage(new TextComponentString(HDM.prefix + "§dLIGHT_PURPLE §5DARK_PURPLE §fWHITE §7GRAY §8DARK_GRAY"));
                    sender.sendMessage(new TextComponentString(HDM.prefix + "§0BLACK §rRESET §lBOLD §mSTRIKE §nUNDERLINE §oITALIC"));
                    break;
                default:
                    sender.sendMessage(new TextComponentString(HDM.prefix + " You need to supply something."));
                    break;
            }
        }
    }

    // cc §
    private void add(String s, ICommandSender sender) {
        // add player to list
        if (HDM.customMessages.contains(s)) {
            sender.sendMessage(new TextComponentString(HDM.prefix + " §cThis message already exists."));
        } else {
            sender.sendMessage(new TextComponentString(HDM.prefix + " §aAdded message §b" + s + "!"));
            HDM.customMessages.add(s);
            refreshConfig();
        }
        messageDeDupe();
    }

    private void remove(String s, ICommandSender sender) {
        // add player to list
        int index = -1;
        try {
            index = Integer.parseInt(s);
        } catch (NumberFormatException ignored) {}
        if (HDM.customMessages.contains(s) || (index != -1 && HDM.customMessages.size() >= index)) {
            sender.sendMessage(textFormat(HDM.prefix + " §bRemoved message §b" + s + "!", "HYBRID", "§6Click to revert this change!", "/hdm add " + s));
            if(index != -1) {
                HDM.customMessages.remove(HDM.customMessages.get(index));
            } else
            HDM.customMessages.remove(s);
            refreshConfig();
        } else {
            sender.sendMessage(new TextComponentString(HDM.prefix + " §cThis message does not exist."));
        }
    }

    private void list(ICommandSender sender) {
        sender.sendMessage(new TextComponentString(HDM.prefix + " Here's the current message lists:"));
        for(int i=0; i<HDM.customMessages.size(); i++) {
            sender.sendMessage(textFormat(i + ". " + HDM.customMessages.get(i), "HYBRID", "§6Click to remove!", "/hdm remove " + i));
        }
    }

    static void refreshConfig() {
        try {
            ConfigSpirit.writeConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TextComponentString textFormat(String str, String modificationType, String action, String extra) {
        String xaction = TextParserSpirit.parseString(action, null);
        TextComponentString s = new TextComponentString(TextParserSpirit.parseString(str, extra));
        switch (modificationType) {
            case "SHOW_TEXT":
                style = s.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(xaction)));
                return s;
            case "RUN_COMMAND":
                style = s.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
                return s;
            case "HYBRID":
                style = s.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextParserSpirit.parseString(action, null))));
                style = s.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, extra));
                return s;
            default:
                throw new IllegalArgumentException("Invalid textFormat modificationType");
        }
    }

    static void messageDeDupe() {
        HDM.customMessages = new ArrayList<>(new LinkedHashSet<>(HDM.customMessages));
    }
}
