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
import bammerbom.ultimatecore.bukkit.r;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdAdminchat implements UltimateCommand {
    static String format = ChatColor.translateAlternateColorCodes('&', r.getCnfg().getString("Chat.AdminchatFormat").replace("@1", r.positive + "").replace("@2", r.neutral + "").replace
            ("@3", r.negative + ""));

    @Override
    public String getName() {
        return "adminchat";
    }

    @Override
    public String getPermission() {
        return "uc.adminchat";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("ac");
    }

    @Override
    public void run(final CommandSender cs, String label, String[] args) {
        if (!r.perm(cs, "uc.adminchat", false, true)) {
            return;
        }
        if (!r.checkArgs(args, 0)) {
            r.sendMes(cs, "adminchatUsage");
            return;
        }
        String msg = r.getFinalArg(args, 0);
        for (Player p : r.getOnlinePlayers()) {
            if (r.perm(p, "uc.adminchat", false, false)) {
                p.sendMessage(format.replace("%Player", cs.getName()).replace("%Message", msg));
            }
        }
        Bukkit.getServer().getConsoleSender().sendMessage(format.replace("%Player", cs.getName()).replace("%Message", msg));
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args, String curs, Integer curn) {
        return new ArrayList<>();
    }
}
