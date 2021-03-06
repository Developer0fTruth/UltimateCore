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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CmdFirework implements UltimateCommand {
    public static Random ran = new Random();

    @Override
    public String getName() {
        return "firework";
    }

    @Override
    public String getPermission() {
        return "uc.firework";
    }

    @Override
    public String getUsage() {
        return "/<command> [clear] [random] [power:<amount>] [color:<COLOR[,COLOR]]>] [fade:<COLOR[,COLOR]]>] [shape:<star/ball/large/creeper/burst>] [effect:<trail/twinkle>[," +
                "trail/twinkle]]";
    }

    @Override
    public Text getDescription() {
        return Text.of("Spawn in or modify firework.");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        if (!r.isPlayer(cs)) {
            return CommandResult.empty();
        }
        if (!r.perm(cs, "uc.firework", true)) {
            return CommandResult.empty();
        }
        Player p = (Player) cs;
        Boolean spawnin = !(p.getItemInHand().getType() == Material.FIREWORK);
        ItemStack stack = p.getItemInHand().getType() == Material.FIREWORK ? p.getItemInHand() : new ItemStack(Material.FIREWORK);
        if (!r.checkArgs(args, 0)) {
            r.sendMes(cs, "fireworkUsage");
            return CommandResult.empty();
        }
        if (args[0].equalsIgnoreCase("clear") && (p.getItemInHand().getType() == Material.FIREWORK)) {
            FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
            fmeta.clearEffects();
            stack.setItemMeta(fmeta);
            r.sendMes(cs, "fireworkClear");
            return CommandResult.empty();
        }

        if (args[0].equalsIgnoreCase("random")) {
            List<FireworkEffect.Type> effecttype = Arrays.asList(FireworkEffect.Type.values());
            ArrayList<Color> effectcolor = new ArrayList<>();
            effectcolor.add(Color.WHITE);
            effectcolor.add(Color.SILVER);
            effectcolor.add(Color.GRAY);
            effectcolor.add(Color.BLACK);
            effectcolor.add(Color.RED);
            effectcolor.add(Color.MAROON);
            effectcolor.add(Color.YELLOW);
            effectcolor.add(Color.OLIVE);
            effectcolor.add(Color.LIME);
            effectcolor.add(Color.GREEN);
            effectcolor.add(Color.AQUA);
            effectcolor.add(Color.TEAL);
            effectcolor.add(Color.BLUE);
            effectcolor.add(Color.NAVY);
            effectcolor.add(Color.FUCHSIA);
            effectcolor.add(Color.PURPLE);
            effectcolor.add(Color.ORANGE);

            FireworkMeta fm = (FireworkMeta) stack.getItemMeta();
            fm.clearEffects();
            fm.addEffect(FireworkEffect.builder().flicker(ran.nextBoolean()).trail(ran.nextBoolean()).with(effecttype.get(ran.nextInt(effecttype.size()))).withColor(effectcolor.get(ran
                    .nextInt(effectcolor.size()))).withFade(effectcolor.get(ran.nextInt(effectcolor.size()))).build());
            int number = ran.nextInt(3);
            number++;
            fm.setPower(number);

            stack.setItemMeta(fm);
            if (spawnin) {
                r.sendMes(cs, "fireworkSpawnin");
                p.getInventory().addItem(stack);
            } else {
                r.sendMes(cs, "fireworkModify");
            }
            return CommandResult.empty();
        }

        MetaItemStack mStack = new MetaItemStack(stack);
        for (String arg : args) {
            if (arg.equalsIgnoreCase("power") || arg.equalsIgnoreCase("p")) {
                FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                try {
                    int power = Integer.parseInt(arg.split(":")[1]);
                    try {
                        mStack.addFireworkMeta(true, "power:" + power);
                    } catch (Exception e) {
                        r.sendMes(cs, "fireworkFailed");
                    }
                } catch (NumberFormatException e) {
                    r.sendMes(cs, "fireworkFailed");
                    return CommandResult.empty();
                }
                stack.setItemMeta(fmeta);
            } else if (r.isInt(arg)) {
                stack.setAmount(Integer.parseInt(arg));
            } else {
                try {
                    mStack.addFireworkMeta(true, arg);
                } catch (Exception e) {
                    r.sendMes(cs, "fireworkFailed");
                    return CommandResult.empty();
                }
            }
        }
        if (mStack.isValidFirework()) {
            FireworkMeta fmeta = (FireworkMeta) mStack.getItemStack().getItemMeta();
            FireworkEffect effect = mStack.getFireworkBuilder().build();
            fmeta.addEffect(effect);
            stack.setItemMeta(fmeta);
        } else {
            r.sendMes(cs, "fireworkFailed");
            return CommandResult.empty();
        }
        if (spawnin) {
            r.sendMes(cs, "fireworkSpawnin");
            p.getInventory().addItem(stack);
        } else {
            r.sendMes(cs, "fireworkModify");
        }
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return null;
    }
}
