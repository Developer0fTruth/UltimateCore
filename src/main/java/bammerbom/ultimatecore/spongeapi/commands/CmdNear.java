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
import bammerbom.ultimatecore.spongeapi.r;
import bammerbom.ultimatecore.spongeapi.resources.utils.LocationUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdNear implements UltimateCommand {

    @Override
    public String getName() {
        return "near";
    }

    @Override
    public String getPermission() {
        return "uc.near";
    }

    @Override
    public String getUsage() {
        return "/<command> [Range]";
    }

    @Override
    public Text getDescription() {
        return Text.of("View all nearby players.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (!r.perm(cs, "uc.near", true)) {
            return CommandResult.empty();
        }
        if (!r.isPlayer(cs)) {
            return CommandResult.empty();
        }
        Player p = (Player) cs;
        StringBuilder builder = new StringBuilder("");
        Boolean a = true;
        Integer range = 250;
        if (r.checkArgs(args, 0)) {
            if (!r.isInt(args[0])) {
                r.sendMes(cs, "numberFormat", "%Number", args[0]);
                return CommandResult.empty();
            }
            range = r.normalize(Integer.parseInt(args[0]), 1, 5000);
        }
        for (Entity e : r.getNearbyPlayers(p, range.doubleValue())) {
            Player t = (Player) e;
            if (t.equals(cs)) {
                continue;
            }
            if (!a) {
                builder.append(", ");
            }
            builder.append(t.getName());
            builder.append("(" + LocationUtil.getDistance(t.getLocation(), p.getLocation()).intValue() + ")");
            a = false;
        }
        if (a) {
            builder.append(r.mes("nearNone"));
        }
        r.sendMes(cs, "nearMessage", "%Range", range, "%Players", builder.toString());
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return new ArrayList<>();
    }
}
