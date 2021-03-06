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
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.explosive.fireball.Fireball;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;

public class CmdFireball implements UltimateCommand {

    @Override
    public String getName() {
        return "fireball";
    }

    @Override
    public String getPermission() {
        return "uc.fireball";
    }

    @Override
    public String getUsage() {
        return "/<command> [Type]";
    }

    @Override
    public Text getDescription() {
        return Text.of("Shoot a fireball.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("fireskull");
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (!r.isPlayer(cs)) {
            return CommandResult.empty();
        }
        Player p = (Player) cs;
        if (!r.perm(p, "uc.fireball", true)) {
            return CommandResult.empty();
        }
        Fireball ball = (Fireball) p.getWorld().createEntity(EntityTypes.FIREBALL, p.getLocation().getPosition()).get();
        ball.offer(Keys.DIRECTION, p.get(Keys.DIRECTION).get()); //TODO wait for api
        EntityType type = EntityTypes.FIREBALL;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("small")) {
                type = EntityTypes.SMALL_FIREBALL;
            } else if (args[0].equalsIgnoreCase("arrow")) {
                type = EntityTypes.TIPPED_ARROW; //TODO correct arrow type?
            } else if (args[0].equalsIgnoreCase("skull")) {
                type = EntityTypes.WITHER_SKULL;
            } else if (args[0].equalsIgnoreCase("egg")) {
                type = EntityTypes.EGG;
            } else if (args[0].equalsIgnoreCase("snowball")) {
                type = EntityTypes.SNOWBALL;
            } else if (args[0].equalsIgnoreCase("expbottle")) {
                type = EntityTypes.THROWN_EXP_BOTTLE;
            } else if (args[0].equalsIgnoreCase("large")) {
                type = EntityTypes.FIREBALL;
            } else if (args[0].equalsIgnoreCase("dragon")) {
                type = EntityTypes.DRAGON_FIREBALL;
            }
        }
        Projectile projectile = (Projectile) p.getWorld().createEntity(type, p.getLocation().getPosition()).get();
        projectile.setShooter(p);
        projectile.setVelocity();
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return null;
    }
}
