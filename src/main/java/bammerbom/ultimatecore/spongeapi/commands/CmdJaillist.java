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
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdJaillist implements UltimateCommand {

    @Override
    public String getName() {
        return "jaillist";
    }

    @Override
    public String getPermission() {
        return "uc.jaillist";
    }

    @Override
    public String getUsage() {
        return "/<command>";
    }

    @Override
    public Text getDescription() {
        return Text.of("View a list of all currently jailed players.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("jails");
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (r.perm(cs, "uc.jaillist", true) == false) {
            return CommandResult.empty();
        }
        List<GameProfile> jails = UC.getServer().getJailedGameProfiles();
        if (jails == null || jails.isEmpty()) {
            r.sendMes(cs, "jaillistNoJailsFound");
            return CommandResult.empty();
        }
        StringBuilder jaillist = new StringBuilder();
        Integer cur = 0;
        String result;
        for (int i = 0; i < jails.size(); i++) {
            jaillist.append(jails.get(cur).getName() + ", ");
            cur++;

        }
        result = jaillist.substring(0, jaillist.length() - 2);
        r.sendMes(cs, "jaillistJails", "%Jaillist", result);
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        List<String> jails = new ArrayList<>();
        for (GameProfile pl : UC.getServer().getJailedGameProfiles()) {
            jails.add(pl.getName().orElse(""));
        }
        return jails;
    }
}
