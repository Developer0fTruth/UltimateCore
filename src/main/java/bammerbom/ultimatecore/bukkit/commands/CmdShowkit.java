/*
 * This file is part of UltimateCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) Bammerbom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bammerbom.ultimatecore.bukkit.commands;

import bammerbom.ultimatecore.bukkit.UltimateCommand;
import bammerbom.ultimatecore.bukkit.api.UC;
import bammerbom.ultimatecore.bukkit.api.UKit;
import bammerbom.ultimatecore.bukkit.r;
import bammerbom.ultimatecore.bukkit.resources.utils.DateUtil;
import bammerbom.ultimatecore.bukkit.resources.utils.ItemUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CmdShowkit implements UltimateCommand {

    @Override
    public String getName() {
        return "showkit";
    }

    @Override
    public String getPermission() {
        return "uc.showkit";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }

    @Override
    public void run(final CommandSender cs, String label, String[] args) {
        if (!r.perm(cs, "uc.showkit", false, true)) {
            return;
        }
        if (!r.checkArgs(args, 0)) {
            r.sendMes(cs, "showkitUsage");
            return;
        }
        if (!UC.getServer().getKitNames().contains(args[0])) {
            r.sendMes(cs, "kitNotFound", "%Kit", args[0]);
            return;
        }
        UKit kit = UC.getServer().getKit(args[0]);
        r.sendMes(cs, "showkitContains", "%Kit", kit.getName());
        if (kit.getCooldown() == 0) {
            r.sendMes(cs, "kitList3", "%Cooldown", r.mes("kitNoCooldown"));
        } else if (kit.getCooldown() == -1) {
            r.sendMes(cs, "kitList3", "%Cooldown", r.mes("kitOnlyOnce"));
        } else {
            r.sendMes(cs, "kitList3", "%Cooldown", DateUtil.format(kit.getCooldown()));
        }
        for (ItemStack stack : kit.getItems()) {
            HashMap<String, Object> map = ItemUtil.serialize(stack);
            StringBuilder sb = new StringBuilder();
            for (String key : map.keySet()) {
                sb.append(key + ":" + map.get(key) + " ");
            }
            r.sendMes(cs, "showkitItem", "%Item", ChatColor.stripColor(sb.toString()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args, String curs, Integer curn) {
        return null;
    }
}
