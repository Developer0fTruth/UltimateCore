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
import bammerbom.ultimatecore.bukkit.resources.utils.MinecraftServerUtil;
import bammerbom.ultimatecore.bukkit.resources.utils.MinecraftServerUtil.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class CmdMinecraftservers implements UltimateCommand {

    @Override
    public String getName() {
        return "minecraftservers";
    }

    @Override
    public String getPermission() {
        return "uc.minecraftservers";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("mcservers");
    }

    @Override
    public void run(final CommandSender cs, String label, String[] args) {
        if (!r.perm(cs, "uc.minecraftservers", true, true)) {
            return;
        }
        if (!r.getCnfg().getBoolean("Metrics")) {
            r.sendMes(cs, "minecraftserversDisabled");
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MinecraftServerUtil.runcheck();

                String os = "";
                for (MinecraftServer str : MinecraftServerUtil.online) {
                    if (!os.equals("")) {
                        os = os + ", " + ChatColor.GREEN + str.toString().toLowerCase() + r.positive + "";
                    } else {
                        os = os + ChatColor.GREEN + str.toString().toLowerCase() + r.positive + "";
                    }
                }
                for (MinecraftServer str : MinecraftServerUtil.problems) {
                    if (!os.equals("")) {
                        os = os + ", " + ChatColor.GOLD + str.toString().toLowerCase() + r.positive + "";
                    } else {
                        os = os + ChatColor.GOLD + str.toString().toLowerCase() + r.positive + "";
                    }
                }
                for (MinecraftServer str : MinecraftServerUtil.offline) {
                    if (!os.equals("")) {
                        os = os + ", " + ChatColor.DARK_RED + str.toString().toLowerCase() + r.positive + "";
                    } else {
                        os = os + ChatColor.DARK_RED + str.toString().toLowerCase() + r.positive + "";
                    }
                }
                for (MinecraftServer str : MinecraftServerUtil.unknown) {
                    if (!os.equals("")) {
                        os = os + ", " + ChatColor.GRAY + str.toString().toLowerCase() + r.positive + "";
                    } else {
                        os = os + ChatColor.GRAY + str.toString().toLowerCase() + r.positive + "";
                    }
                }
                r.sendMes(cs, "minecraftserversMessage", "%Servers", ChatColor.RESET + os);

            }
        });
        thread.setName("UltimateCore: Server Check Thread");
        thread.start();
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String alias, String[] args, String curs, Integer curn) {
        return null;
    }
}


