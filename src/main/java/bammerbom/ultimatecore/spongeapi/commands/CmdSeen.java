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
import bammerbom.ultimatecore.spongeapi.api.UPlayer;
import bammerbom.ultimatecore.spongeapi.r;
import bammerbom.ultimatecore.spongeapi.resources.utils.DateUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;

public class CmdSeen implements UltimateCommand {

    @Override
    public String getName() {
        return "seen";
    }

    @Override
    public String getPermission() {
        return "uc.seen";
    }

    @Override
    public String getUsage() {
        return "/<command> <Player>";
    }

    @Override
    public Text getDescription() {
        return Text.of("View information about a player.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (!r.perm(cs, "uc.seen", true)) {
            return CommandResult.empty();
        }
        if (!r.checkArgs(args, 0)) {
            r.sendMes(cs, "seenUsage");
            return CommandResult.empty();
        }
        GameProfile p = r.searchGameProfile(args[0]).orElse(null);
        if (p == null) {
            r.sendMes(cs, "seenNotFound", "%Player", args[0]);
            return CommandResult.empty();
        }
        UPlayer pl = UC.getPlayer(p);
        r.sendMes(cs, "seenMessage", "%Player", r.getDisplayName(p), "%Status", (r.searchPlayer(p.getUniqueId()).isPresent() ? r.mes("seenOnline") : r.mes("seenOffline")), "%Time",
                DateUtil.formatDateDiff(pl.getLastConnectMillis()));
        //Last location
        if (pl.getOnlinePlayer() != null && pl.getOnlinePlayer().getLocation() != null) {
            String loc = pl.getOnlinePlayer().getWorld().getName() + " " + pl.getOnlinePlayer().getLocation().getBlockX() + " " + pl.getOnlinePlayer().getLocation().getBlockY() + " " + pl
                    .getOnlinePlayer().getLocation().getBlockZ();
            r.sendMes(cs, "seenLocation", "%Location", loc);
        }
        //Ban
        r.sendMes(cs, "seenBanned", "%Banned", pl.isBanned() ? r.mes("yes") : r.mes("no"));
        if (pl.isBanned()) {
            r.sendMes(cs, "seenBantime", "%Bantime", pl.getBanTimeLeft() >= 0 ? DateUtil.format(pl.getBanTimeLeft()) : r.mes("banForever"));
            r.sendMes(cs, "seenBanreason", "%Reason", pl.getBanReason());
        }
        //Mute
        r.sendMes(cs, "seenMuted", "%Muted", pl.isMuted() ? r.mes("yes") : r.mes("no"));
        if (pl.isMuted()) {
            r.sendMes(cs, "seenMutetime", "%Mutetime", pl.getMuteTimeLeft() >= 0 ? DateUtil.format(pl.getMuteTimeLeft()) : r.mes("banForever"));
            r.sendMes(cs, "seenMutereason", "%Reason", pl.getMuteReason());
        }
        //Deaf
        r.sendMes(cs, "seenDeaf", "%Deaf", pl.isDeaf() ? r.mes("yes") : r.mes("no"));
        if (pl.isDeaf()) {
            r.sendMes(cs, "seenDeaftime", "%Deaftime", pl.getDeafTimeLeft() >= 0 ? DateUtil.format(pl.getDeafTimeLeft()) : r.mes("banForever"));
        }
        //Jailed
        r.sendMes(cs, "seenJailed", "%Jailed", pl.isJailed() ? r.mes("yes") : r.mes("no"));
        if (pl.isJailed()) {
            r.sendMes(cs, "seenJailtime", "%Jailtime", pl.getJailTimeLeft() >= 0 ? DateUtil.format(pl.getJailTimeLeft()) : r.mes("banForever"));
        }
        //Frozen
        r.sendMes(cs, "seenFrozen", "%Frozen", pl.isFrozen() ? r.mes("yes") : r.mes("no"));
        if (pl.isFrozen()) {
            r.sendMes(cs, "seenFrozentime", "%Frozentime", pl.getFrozenTimeLeft() >= 0 ? DateUtil.format(pl.getFrozenTimeLeft()) : r.mes("banForever"));
        }
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return null;
    }
}
