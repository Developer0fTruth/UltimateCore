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
package bammerbom.ultimatecore.spongeapi.commands;

import bammerbom.ultimatecore.spongeapi.UltimateCommand;
import bammerbom.ultimatecore.spongeapi.api.UC;
import bammerbom.ultimatecore.spongeapi.r;
import bammerbom.ultimatecore.spongeapi.resources.classes.ErrorLogger;
import bammerbom.ultimatecore.spongeapi.resources.utils.LocationUtil;
import bammerbom.ultimatecore.spongeapi.resources.utils.StringUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CmdHome implements UltimateCommand {

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getPermission() {
        return "uc.home";
    }

    @Override
    public String getUsage() {
        return "/<command> [Player:]<Home>";
    }

    @Override
    public Text getDescription() {
        return Text.of("Teleport to your home.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("homes", "homelist");
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (r.checkArgs(args, 0) == false) {
            if (!(r.isPlayer(cs))) {
                return CommandResult.empty();
            }
            if (!r.perm(cs, "uc.home", true)) {
                return CommandResult.empty();
            }
            Player p = (Player) cs;
            ArrayList<String> homes = UC.getPlayer(p).getHomeNames();

            if (homes.size() == 1) {
                String home = homes.get(0);
                try {
                    //Teleport
                    Location location = UC.getPlayer(p).getHome(home.toLowerCase());
                    LocationUtil.teleport(p, location, Cause.builder().build(), true, true); //TODO Cause
                    r.sendMes(cs, "homeTeleport", "%Home", home);
                } catch (Exception ex) {
                    r.sendMes(cs, "homeInvalid", "%Home", home);
                    ErrorLogger.log(ex, "Failed to load home: " + home);
                }
                return CommandResult.empty();
            }

            String a = StringUtil.joinList(homes);
            //
            Set<String> multihomes = r.getCnfg().getConfigurationSection("Command.HomeLimits").getKeys(false);
            Integer limit = 1;
            if (multihomes != null) {
                for (String s : multihomes) {
                    if (r.perm(cs, "uc.sethome." + s.toLowerCase(), false)) {
                        if (limit < r.getCnfg().getInt("Command.HomeLimits." + s)) {
                            limit = r.getCnfg().getInt("Command.HomeLimits." + s);
                        }
                    }
                }
            }
            if (r.perm(cs, "uc.sethome.unlimited", false)) {
                limit = 999999;
            }
            String limitformat = limit == 999999 ? r.mes("unlimited").toPlain() : (limit + "");
            //
            if (a.equalsIgnoreCase("") || a.equalsIgnoreCase(null)) {
                r.sendMes(cs, "homeNoHomesFound");
            } else {
                r.sendMes(cs, "homeList", "%Homes", a, "%Current", homes.size(), "%Max", limitformat);
            }
        } else {
            GameProfile t;
            if (r.perm(cs, "uc.home", true) == false) {
                return CommandResult.empty();
            }
            if (args[0].contains(":")) {
                if (!r.perm(cs, "uc.home.others", true)) {
                    return CommandResult.empty();
                }
                if (args[0].endsWith(":") || args[0].endsWith(":list")) {
                    t = r.searchPlayer(args[0].split(":")[0]).orElse(null).getProfile();
                    if (t == null) {
                        r.sendMes(cs, "playerNotFound", "%Player", args[0].split(":")[0]);
                        return CommandResult.empty();
                    }
                    ArrayList<String> homes = UC.getPlayer(t).getHomeNames();
                    String a = "";
                    Integer b = 0;
                    try {
                        Integer amount = homes.toArray().length;
                        for (int i = 0; i < amount; i++) {
                            a = a + homes.get(b) + ", ";
                            b++;

                        }
                        a = a.substring(0, a.length() - 2);
                    } catch (IndexOutOfBoundsException ex) {
                    }
                    if (a.equalsIgnoreCase("") || a.equalsIgnoreCase(null)) {
                        r.sendMes(cs, "homeNoHomesFound");
                        return CommandResult.empty();
                    } else {
                        Set<String> multihomes = r.getCnfg().getConfigurationSection("Command.HomeLimits").getKeys(false);
                        Integer limit = 1;
                        if (multihomes != null) {
                            for (String s : multihomes) {
                                if (r.perm(cs, "uc.sethome." + s.toLowerCase(), false)) {
                                    if (limit < r.getCnfg().getInt("Command.HomeLimits." + s)) {
                                        limit = r.getCnfg().getInt("Command.HomeLimits." + s);
                                    }
                                }
                            }
                        }
                        if (r.perm(cs, "uc.sethome.unlimited", false)) {
                            limit = 999999;
                        }
                        String limitformat = limit == 999999 ? r.mes("unlimited").toPlain() : (limit + "");
                        r.sendMes(cs, "homeList", "%Homes", a, "%Current", homes.size(), "%Max", limitformat);
                        return CommandResult.empty();
                    }
                }
                if (!r.isPlayer(cs)) {
                    return CommandResult.empty();
                }
                Player p = (Player) cs;
                t = r.searchGameProfile(args[0].split(":")[0]).orElse(null);
                if (t == null) {
                    r.sendMes(cs, "playerNotFound", "%Player", args[0].split(":")[0]);
                    return CommandResult.empty();
                }
                List<String> homes = UC.getPlayer(t).getHomeNames();
                if (!homes.contains(args[0].split(":")[1].toLowerCase())) {
                    r.sendMes(cs, "homeNotExist", "%Home", args[0]);
                    return CommandResult.empty();
                }
                try {
                    //Teleport
                    Location location = UC.getPlayer(t).getHome(args[0].toLowerCase().split(":")[1]);
                    if (r.isPlayer(cs)) {
                        LocationUtil.teleport(p, location, Cause.builder().build(), true, true);
                    }
                    r.sendMes(cs, "homeTeleport", "%Home", args[0]);
                } catch (Exception ex) {
                    r.sendMes(cs, "homeInvalid", "%Home", args[0]);
                    ErrorLogger.log(ex, "Failed to load home: " + args[0]);
                }
                return CommandResult.empty();
            }
            if (!r.isPlayer(cs)) {
                return CommandResult.empty();
            }
            Player p = (Player) cs;
            //Exist
            List<String> homes = UC.getPlayer(p).getHomeNames();
            if (!homes.contains(args[0].toLowerCase())) {
                r.sendMes(cs, "homeNotExist", "%Home", args[0]);
                return CommandResult.empty();
            }
            try {
                //Teleport
                Location location = UC.getPlayer(p).getHome(args[0].toLowerCase());
                LocationUtil.teleport(p, location, Cause.builder().build(), true, true);
                r.sendMes(cs, "homeTeleport", "%Home", args[0]);
            } catch (Exception ex) {
                r.sendMes(cs, "homeInvalid", "%Home", args[0]);
                ErrorLogger.log(ex, "Failed to load home: " + args[0]);
            }
        }
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        if (!r.isPlayer(cs)) {
            return new ArrayList<>();
        }
        return UC.getPlayer((Player) cs).getHomeNames();
    }
}
