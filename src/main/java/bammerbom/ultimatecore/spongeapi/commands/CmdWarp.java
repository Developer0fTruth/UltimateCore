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
import bammerbom.ultimatecore.spongeapi.resources.utils.LocationUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.Arrays;
import java.util.List;

public class CmdWarp implements UltimateCommand {

    @Override
    public String getName() {
        return "warp";
    }

    @Override
    public String getPermission() {
        return "uc.warp";
    }

    @Override
    public String getUsage() {
        return "/<command> <Warp>";
    }

    @Override
    public Text getDescription() {
        return Text.of("Teleport yourself to a warp.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("warps", "warplist");
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (!r.checkArgs(args, 0)) {
            if (!r.perm(cs, "uc.warplist", true)) {
                return CommandResult.empty();
            }
            List<String> warps = UC.getServer().getWarpNames();
            if (warps == null || warps.isEmpty()) {
                r.sendMes(cs, "warpNoWarpsFound");
                return CommandResult.empty();
            }
            StringBuilder warplist = new StringBuilder();
            Integer cur = 0;
            String result;
            for (int i = 0; i < warps.size(); i++) {
                warplist.append(warps.get(cur) + ", ");
                cur++;

            }
            result = warplist.substring(0, warplist.length() - 2);
            r.sendMes(cs, "warpWarps", "%Warps", result);
        } else {
            if (!(r.isPlayer(cs))) {
                return CommandResult.empty();
            }
            //Exist
            Player p = (Player) cs;
            if (!r.perm(p, "uc.warp", false) && !r.perm(p, "uc.warp." + args[0], false)) {
                r.sendMes(cs, "noPermissions");
                return CommandResult.empty();
            }
            if (UC.getServer().getWarp(args[0]) == null) {
                r.sendMes(cs, "warpNotExist", "%Warp", args[0]);
                return CommandResult.empty();
            }

            //Teleport
            Location loc = UC.getServer().getWarp(args[0]);
            LocationUtil.teleportUnsafe(p, loc, Cause.builder().build(), true);
            r.sendMes(cs, "warpMessage", "%Warp", args[0]);
        }
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return UC.getServer().getWarpNames();
    }
}
