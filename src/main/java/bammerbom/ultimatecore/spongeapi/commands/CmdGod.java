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
import bammerbom.ultimatecore.spongeapi.resources.utils.DateUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;

public class CmdGod implements UltimateCommand {

    @Override
    public String getName() {
        return "god";
    }

    @Override
    public String getPermission() {
        return "uc.god";
    }

    @Override
    public String getUsage() {
        return "/<command> [Player] [Time]";
    }

    @Override
    public Text getDescription() {
        return Text.of("Enable/Disable god mode for a certain time.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("godmode");
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (!r.perm(cs, "uc.god", true)) {
            return CommandResult.empty();
        }
        if (r.checkArgs(args, 0) == false) {
            if (!r.isPlayer(cs)) {
                return CommandResult.empty();
            }
            Player p = (Player) cs;
            if (UC.getPlayer(p).isGod()) {
                UC.getPlayer(p).setGod(false);
                r.sendMes(cs, "godSelf", "%Status", r.mes("off"));
            } else {
                UC.getPlayer(p).setGod(true);
                r.sendMes(cs, "godSelf", "%Status", r.mes("on"));
            }
            return CommandResult.empty();
        }
        if (DateUtil.parseDateDiff(args[0]) >= 1) {
            Long t = DateUtil.parseDateDiff(args[0]);
            if (!r.isPlayer(cs)) {
                return CommandResult.empty();
            }
            Player p = (Player) cs;
            UC.getPlayer(p).setGod(true, t);
            r.sendMes(cs, "godSelfT", "%Status", r.mes("on"), "%Time", DateUtil.format(t));
            return CommandResult.empty();
        }
        if (!r.perm(cs, "uc.god.others", true)) {
            return CommandResult.empty();
        }
        GameProfile banp = r.searchGameProfile(args[0]).orElse(null);
        if (banp == null) {
            r.sendMes(cs, "playerNotFound", "%Player", args[0]);
            return CommandResult.empty();
        }
        /*//Info
         if(r.checkArgs(args, 1) == false){
         UC.getPlayer(banp).setGod(!UC.getPlayer(banp).isGod());
         r.sendMes(cs, "godOthersSelfMessage", "%Player", banp.getName(), "%Status", (UC.getPlayer(banp).isGod() ? r
         .mes("on") : r.mes("off")));
         if(banp.isOnline()) r.sendMes((Player) banp, "godOthersOtherMessage", "%Player", banp.getName(), "%Status",
         (UC.getPlayer(banp).isGod() ? r.mes("on") : r.mes("off")));
         return CommandResult.empty();
         }*/
        Long time = 0L;
        if (r.checkArgs(args, 1) && DateUtil.parseDateDiff(args[1]) >= 1) {
            time = DateUtil.parseDateDiff(args[1]);
        }
        //Permcheck
        if (!r.perm(cs, "uc.god.time", false) && !r.perm(cs, "uc.god", false) && time == 0L) {
            r.sendMes(cs, "noPermissions");
            return CommandResult.empty();
        }
        if (!r.perm(cs, "uc.god.perm", false) && !r.perm(cs, "uc.god", false) && time != 0L) {
            r.sendMes(cs, "noPermissions");
            return CommandResult.empty();
        }
        UC.getPlayer(banp).setGod(!UC.getPlayer(banp).isGod(), time);
        if (time == 0L) {
            r.sendMes(cs, "godOthersSelfMessage", "%Player", r.getDisplayName(banp), "%Status", (UC.getPlayer(banp).isGod() ? r.mes("on") : r.mes("off")));
            if (r.searchPlayer(banp.getUniqueId()).isPresent()) {
                r.sendMes((CommandSource) banp, "godOthersOtherMessage", "%Player", r.getDisplayName(banp), "%Status", (UC.getPlayer(banp).isGod() ? r.mes("on") : r.mes("off")));
            }
        } else {
            r.sendMes(cs, "godOthersSelfMessageT", "%Player", r.getDisplayName(banp), "%Status", (UC.getPlayer(banp).isGod() ? r.mes("on") : r.mes("off")), "%Time", DateUtil.format(time));
            if (r.searchPlayer(banp.getUniqueId()).isPresent()) {
                r.sendMes((CommandSource) banp, "godOthersOtherMessageT", "%Player", r.getDisplayName(cs), "%Status", (UC.getPlayer(banp).isGod() ? r.mes("on") : r.mes("off")), "%Time",
                        DateUtil.format(time));
            }
        }
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return null;
    }

}
