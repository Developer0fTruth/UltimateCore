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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;

public class CmdKick implements UltimateCommand {

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getPermission() {
        return "uc.kick";
    }

    @Override
    public String getUsage() {
        return "/<command> <Player> [Reason]";
    }

    @Override
    public Text getDescription() {
        return Text.of("Kick someone from the server.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (!r.perm(cs, "uc.kick", true)) {
            return CommandResult.empty();
        }
        if (r.checkArgs(args, 0) == false) {
            r.sendMes(cs, "kickUsage");
            return CommandResult.empty();
        }
        Player target = r.searchPlayer(args[0]).get();
        if (target == null) {
            r.sendMes(cs, "playerNotFound", "%Player", args[0]);
            return CommandResult.empty();
        }
        if (cs.getName().equalsIgnoreCase(target.getName())) {
            r.sendMes(cs, "kickSelf");
            return CommandResult.empty();
        }
        if (r.checkArgs(args, 1) == false) {
            Sponge.getServer().getBroadcastChannel().send(r.mes("kickBroadcast", "%Kicker", r.getDisplayName(cs), "%Player", target.getName()));
            Sponge.getServer().getBroadcastChannel().send(r.mes("kickBroadcast2", "%Reason", r.mes("kickDefaultReason")));
            target.kick(r.mes("kickMessage", "%Reason", r.mes("kickDefaultReason")));
        } else {
            Sponge.getServer().getBroadcastChannel().send(r.mes("kickBroadcast", "%Kicker", r.getDisplayName(cs), "%Player", target.getName()));
            Sponge.getServer().getBroadcastChannel().send(r.mes("kickBroadcast2", "%Reason", r.getFinalArg(args, 1)));
            target.kick(r.mes("kickMessage", "%Reason", r.getFinalArg(args, 1)));
        }
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return null;
    }
}
