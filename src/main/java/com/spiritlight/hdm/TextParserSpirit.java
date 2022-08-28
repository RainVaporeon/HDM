package com.spiritlight.hdm;

import javax.annotation.Nullable;
import java.util.Objects;

public class TextParserSpirit {
    // This one is quite hideous, do not blame
        public static String parseString(String s, @Nullable String name) {
            // Still will parse ${mob} separately.
            if(Objects.equals(name, null))
                name = "[player]";
            return s.replace("{BLACK}", "§0")
                    .replace("{DARK_BLUE}", "§1")
                    .replace("{DARK_GREEN}", "§2")
                    .replace("{DARK_AQUA}", "§3")
                    .replace("{DARK_RED}", "§4")
                    .replace("{DARK_PURPLE}", "§5")
                    .replace("{GOLD}", "§6")
                    .replace("{GRAY}", "§7")
                    .replace("{DARK_GRAY}", "§8")
                    .replace("{BLUE}", "§9")
                    .replace("{GREEN}", "§a")
                    .replace("{AQUA}", "§b")
                    .replace("{RED}", "§c")
                    .replace("{LIGHT_PURPLE}", "§d")
                    .replace("{YELLOW}", "§e")
                    .replace("{WHITE}", "§f")
                    .replace("{RESET}", "§r")
                    .replace("{ITALIC}", "§o")
                    .replace("{STRIKE}", "§m")
                    .replace("{UNDERLINE}", "§n")
                    .replace("{BOLD}", "§l") // pain
                    .replace("[player]", name)
                    .replace("{name}", name);
        }
}
