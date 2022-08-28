package com.spiritlight.hdm;

import com.google.gson.*;
import net.minecraft.command.CommandBase;
// import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;

public class CmdSpirit extends CommandBase {
    Style style;
    private String clipboardContent;

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
                case "import":
                    sender.sendMessage(new TextComponentString(HDM.prefix + " Importing..."));
                    _import(sender);
                    break;
                case "export":
                    sender.sendMessage(new TextComponentString(HDM.prefix + " Exporting..."));
                    export(sender);
                    break;
                case "exportCopy":
                    if(clipboardContent.isEmpty()) return;
                    copyToClipboard(clipboardContent);
                    sender.sendMessage(new TextComponentString(HDM.prefix + " OK! Exported content to your clipboard."));
                    break;
                default:
                    sender.sendMessage(new TextComponentString(HDM.prefix + " You need to supply something."));
                    break;
            }
        }
    }

    private void _import(ICommandSender sender) {
        String result;
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasStringText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasStringText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
                sender.sendMessage(new TextComponentString(HDM.prefix + " Unable to import. Is your clipboard empty?"));
                return;
            }
        } else {
            sender.sendMessage(new TextComponentString(HDM.prefix + " Unable to import. Is your clipboard empty?"));
            return;
        }
        JsonArray object;
        try {
            object = new Gson().fromJson(result, JsonArray.class);
        } catch (JsonSyntaxException e) {
            sender.sendMessage(new TextComponentString(HDM.prefix + " Invalid importing! Is your clipboard content correct?"));
            return;
        }
        for(JsonElement element : object) {
            HDM.customMessages.add(element.toString().replace("\"", "").replace("\\", ""));
        }
        messageDeDupe();
        sender.sendMessage(new TextComponentString(HDM.prefix + " OK! Imported " + object.size() + " messages and de-duped potentially existing ones."));
    }

    private void export(ICommandSender sender) {
        JsonArray content = new JsonArray();
        for(String s : HDM.customMessages) {
            content.add(s);
        }
        Style style = new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to copy!")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hdm exportCopy"));
        clipboardContent = content.toString();
        sender.sendMessage(new TextComponentString(HDM.prefix + " Exported data! Click here to copy to clipboard.").setStyle(style));
    }

    private void copyToClipboard(String s) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(s);
        clipboard.setContents(selection, null);
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


    // s serves as both a String literal, or an integer indicating the position to remove
    private void remove(String s, ICommandSender sender) {
        String revertChangeStr;
        // add player to list
        int index = -1;
        try {
            index = Integer.parseInt(s);
        } catch (NumberFormatException ignored) {}
        // Ensure content exists and that the index specified is not out of bounds
        if (HDM.customMessages.contains(s) || (index >= 0 && HDM.customMessages.size() > index)) {
            revertChangeStr = HDM.customMessages.get(index); // index is ensured to be within bounds
            sender.sendMessage(textFormat(HDM.prefix + " §bRemoved message §b" + s + "!", "HYBRID", "§6Click to revert this change!", "/hdm add " + revertChangeStr));
            HDM.customMessages.removeIf(str -> str.toLowerCase(Locale.ROOT).equals(revertChangeStr.toLowerCase(Locale.ROOT)));
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
        TextComponentString s = new TextComponentString(TextParserSpirit.parseString(str, null));
        switch (modificationType) {
            case "SHOW_TEXT":
                style = s.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(action)));
                return s;
            case "RUN_COMMAND":
                style = s.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
                return s;
            case "HYBRID":
                style = s.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(action)));
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
