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
import bammerbom.ultimatecore.spongeapi.resources.classes.MetaItemStack;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdModify implements UltimateCommand {

    @Override
    public String getName() {
        return "modify";
    }

    @Override
    public String getPermission() {
        return "uc.modify";
    }

    @Override
    public String getUsage() {
        return "/<command> <Meta...>";
    }

    @Override
    public Text getDescription() {
        return Text.of("Modify the meta of the item in your hand.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (!r.perm(cs, "uc.modify", true)) {
            return CommandResult.empty();
        }
        if (!r.isPlayer(cs)) {
            return CommandResult.empty();
        }
        if (!r.checkArgs(args, 0)) {
            r.sendMes(cs, "modifyUsage");
            return CommandResult.empty();
        }
        Player p = (Player) cs;
        ItemStack stack = p.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
        if (stack == null) {
            r.sendMes(cs, "modifyAir");
            return CommandResult.empty();
        }
        try {
            String s = r.getFinalArg(args, 0);
            if (s.startsWith("\\{")) {
                stack = Bukkit.getUnsafe().modifyItemStack(stack, s); //TODO api?
            } else {
                MetaItemStack meta = new MetaItemStack(stack);
                try {
                    meta.parseStringMeta(cs, r.perm(cs, "uc.modify.unsafe", false), args, 0);
                } catch (IllegalArgumentException ex) {
                    if (ex.getMessage() != null && ex.getMessage().contains("Enchantment level is either too low or " + "too high")) {
                        r.sendMes(cs, "enchantUnsafe");
                        return CommandResult.empty();
                    }
                    return CommandResult.empty();
                }
                stack = meta.getItemStack();
            }
        } catch (Exception e) {
            r.sendMes(cs, "giveMetadataFailed");
            return CommandResult.empty();
        }
        p.setItemInHand(stack);
        r.sendMes(cs, "modifyMessage");
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return new ArrayList<>();
    }
}
