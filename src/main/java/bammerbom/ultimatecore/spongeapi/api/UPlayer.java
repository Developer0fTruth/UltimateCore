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
package bammerbom.ultimatecore.spongeapi.api;

import bammerbom.ultimatecore.spongeapi.UltimateFileLoader;
import bammerbom.ultimatecore.spongeapi.jsonconfiguration.JsonConfig;
import bammerbom.ultimatecore.spongeapi.r;
import bammerbom.ultimatecore.spongeapi.resources.utils.InventoryUtil;
import bammerbom.ultimatecore.spongeapi.resources.utils.ItemUtil;
import bammerbom.ultimatecore.spongeapi.resources.utils.LocationUtil;
import bammerbom.ultimatecore.spongeapi.resources.utils.TextColorUtil;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.Location;

import java.io.File;
import java.time.Instant;
import java.util.*;

public class UPlayer {

    static Random ra = new Random();
    static boolean tpspawn = r.getCnfg().getBoolean("Command.Jail.spawn");
    String name = null;
    UUID uuid = null;
    Object[] lastLocation = null;
    Boolean deaf = null;
    Long deaftime = null;
    Boolean freeze = null;
    Long freezetime = null;
    Boolean god = null;
    Long godtime = null;
    HashMap<String, Object[]> homes = null;
    UUID onlineInv = null;
    UUID offlineInv = null;
    Boolean jailed = null;
    Long jailtime = null;
    String jail = null;
    UUID reply = null;
    Boolean spy = null;
    Boolean mute = null;
    Long mutetime = null;
    String mutereason = null;
    Text nickname = null;
    HashMap<ItemType, List<String>> pts = null;
    Boolean inRecipeView = false;
    Boolean vanish = null;
    Long vanishtime = null;
    Long lastconnect = null;
    Boolean inTeleportMenu = false;
    Boolean inCmdEnchantingtable = false;
    Boolean teleportEnabled = null;
    boolean afk = false;
    long lastaction = System.currentTimeMillis();
    String lastip;
    String lasthostname = null;
    String afkmessage = null;

    public UPlayer(GameProfile p) {
        name = p.getName().orElse("");
        uuid = p.getUniqueId();
    }

    public UPlayer(User p) {
        name = p.getName();
        uuid = p.getUniqueId();
    }

    public UPlayer(UUID uuid) {
        GameProfile p = r.searchGameProfile(uuid).get();
        name = p.getName().orElse("");
        this.uuid = p.getUniqueId();
    }

    private void save() {
        UC.uplayers.remove(this);
        UC.uplayers.add(this);
    }

    public User getPlayer() {
        UserStorageService serv = Sponge.getServiceManager().provide(UserStorageService.class).get();
        return serv.get(uuid).get();
    }

    public GameProfile getGameProfile() {
        return r.searchGameProfile(uuid).get();
    }

    public Player getOnlinePlayer() {
        return r.searchPlayer(uuid).get();
    }

    public String getAfkMessage() {
        return afkmessage;
    }

    public void setAfkMessage(String mes) {
        afkmessage = mes;
    }

    public long getLastConnectMillis() {
        if (lastconnect != null) {
            return lastconnect;
        }
        final JsonConfig conf = getPlayerConfig();
        if (conf.get("lastconnect") != null) {
            lastconnect = conf.getLong("lastconnect");
            save();
            return lastconnect;
        } else {
            lastconnect = getPlayer().get(Keys.LAST_DATE_PLAYED).get().toEpochMilli();
            save();
            return lastconnect;
        }
    }

    public void updateLastConnectMillis() {
        lastconnect = System.currentTimeMillis();
        final JsonConfig conf = getPlayerConfig();
        conf.set("lastconnect", System.currentTimeMillis());
        conf.save();
        save();
    }

    public void updateLastConnectMillis(Long millis) {
        lastconnect = millis;
        final JsonConfig conf = getPlayerConfig();
        conf.set("lastconnect", millis);
        conf.save();
        save();
    }

    public String getLastIp() {
        if (lastip != null) {
            return lastip;
        }
        final JsonConfig conf = getPlayerConfig();
        if (conf.get("ip") != null) {
            lastip = conf.getString("ip");
            save();
            return lastip;
        } else {
            if (getPlayer().isOnline()) {
                setLastIp(getOnlinePlayer().getConnection().getAddress().toString().split("/")[1].split(":")[0]);
                return lastip;
            }
            return null;
        }
    }

    public void setLastIp(String ip) {
        lastip = ip;
        final JsonConfig conf = getPlayerConfig();
        conf.set("ip", ip);
        conf.save();
        save();
    }

    public String getLastHostname() {
        if (lasthostname != null) {
            return lasthostname;
        }
        final JsonConfig conf = getPlayerConfig();
        if (conf.get("hostname") != null) {
            lastip = conf.getString("hostname");
            save();
            return lastip;
        } else {
            if (getPlayer().isOnline()) {
                setLastHostname(getOnlinePlayer().getConnection().getAddress().getHostName());
                return lastip;
            }
            return null;
        }
    }

    public void setLastHostname(String ip) {
        lasthostname = ip;
        final JsonConfig conf = getPlayerConfig();
        conf.set("hostname", ip);
        conf.save();
        save();
    }

    //Configuration
    public JsonConfig getPlayerConfig() {
        return UltimateFileLoader.getPlayerConfig(getPlayer());
    }

    public File getPlayerFile() {
        return UltimateFileLoader.getPlayerFile(getGameProfile());
    }

    public void setLastLocation() {
        if (!getPlayer().isOnline()) {
            return;
        }
        setLastLocation(getOnlinePlayer().getLocation(), getOnlinePlayer().getRotation());
    }

    /**
     * Returns the /back location for a player.
     *
     * @return An array of a Location (0) and a Vector3d (1)
     */
    public Object[] getLastLocation() {
        if (lastLocation == null) {
            if (!getPlayerConfig().contains("lastlocation")) {
                return null;
            }
            Object[] loc = LocationUtil.convertStringToLocation(getPlayerConfig().getString("lastlocation"));
            lastLocation = loc;
            save();
            return loc;
        }
        return lastLocation;
    }

    public void setLastLocation(Location loc, Vector3d rot) {
        lastLocation = new Object[]{loc, rot};
        JsonConfig conf = getPlayerConfig();
        conf.set("lastlocation", loc == null ? null : LocationUtil.convertLocationToString(loc, rot));
        conf.save();
        save();
    }

    public boolean isBanned() {
        if (getPlayer() == null || getPlayer().getName() == null) {
            return false;
        }
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        if (service.isBanned(r.searchGameProfile(uuid).get())) {
            return true;
        }
        for (Ban.Ip ip : service.getIpBans()) {
            if (getLastIp() != null && ip.getAddress().getAddress().toString().split("/")[1].split(":")[0].equalsIgnoreCase(getLastIp())) {
                return true;
            }
        }
        return false;
    }

    public boolean isIpBanned() {
        if (getBanTime() != null && getBanTime() >= 1 && getBanTimeLeft() <= 1 && getPlayerConfig().getBoolean("banned")) {
            unban();
            return false;
        }
        if (getPlayer() == null || getPlayer().getName() == null) {
            return false;
        }
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        for (Ban.Ip ip : service.getIpBans()) {
            if (getLastIp() != null && ip.getAddress().getAddress().toString().split("/")[1].split(":")[0].equalsIgnoreCase(getLastIp())) {
                return true;
            }
        }
        return false;
    }

    public Long getBanTime() {
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        if (service.getBanFor(r.searchGameProfile(uuid).get()).isPresent()) {
            Ban ban = service.getBanFor(r.searchGameProfile(uuid).get()).get();
            if (ban.getExpirationDate().isPresent()) {
                return ban.getExpirationDate().get().getEpochSecond();
            } else {
                return -1L;
            }
        }
        for (Ban.Ip ip : service.getIpBans()) {
            if (getLastIp() != null && ip.getAddress().getAddress().toString().split("/")[1].split(":")[0].equalsIgnoreCase(getLastIp())) {
                if (ip.getExpirationDate().isPresent()) {
                    return ip.getExpirationDate().get().getEpochSecond();
                } else {
                    return -1L;
                }
            }
        }
        save();
        return -1L;

    }

    public Long getBanTimeLeft() {
        return getBanTime() - System.currentTimeMillis();
    }

    public Text getBanReason() {
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        if (service.getBanFor(r.searchGameProfile(uuid).get()).isPresent()) {
            Ban ban = service.getBanFor(r.searchGameProfile(uuid).get()).get();
            if (ban.getReason().isPresent()) {
                return ban.getReason().get();
            }
        }
        for (Ban.Ip ip : service.getIpBans()) {
            if (getLastIp() != null && ip.getAddress().getAddress().toString().split("/")[1].split(":")[0].equalsIgnoreCase(getLastIp())) {
                if (ip.getReason().isPresent()) {
                    return ip.getReason().get();
                }
            }
        }
        save();
        return r.mes("banDefaultReason");
    }

    public void unban() {
        save();
        JsonConfig conf = getPlayerConfig();
        conf.set("banned", false);
        conf.set("bantime", null);
        conf.set("banreason", null);
        conf.save();
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        if (service.getBanFor(r.searchGameProfile(uuid).get()).isPresent()) {
            service.removeBan(service.getBanFor(r.searchGameProfile(uuid).get()).get());
        }
        for (Ban.Ip ip : service.getIpBans()) {
            if (getLastIp() != null && ip.getAddress().getAddress().toString().split("/")[1].split(":")[0].equalsIgnoreCase(getLastIp())) {
                service.removeBan(ip);
            }
        }
    }

    public void ban(Long time, Text reason, CommandSource source) {
        BanService service = Sponge.getServiceManager().provide(BanService.class).get();
        service.addBan(Ban.builder().reason(reason).profile(r.searchGameProfile(uuid).get()).source(source).expirationDate(Instant.ofEpochMilli(time)).build());
    }

    public void ban(Long time) {
        ban(time, null, null);
    }

    public void ban(Text reason) {
        ban(null, reason, null);
    }

    public void ban() {
        ban(null, null, null);
    }

    public void ban(Long time, CommandSource source) {
        ban(time, null, source);
    }

    public void ban(Text reason, CommandSource source) {
        ban(null, reason, source);
    }

    public void ban(CommandSource source) {
        ban(null, null, source);
    }

    public void ban(Long time, Text reason) {
        ban(time, reason, null);
    }

    public boolean isDeaf() {
        if (getDeafTime() >= 1 && getDeafTimeLeft() <= 1 && (deaf != null ? deaf : getPlayerConfig().getBoolean("deaf"))) {
            setDeaf(false);
            if (getPlayer().isOnline()) {
                r.sendMes(getOnlinePlayer(), "undeafTarget");
            }
            return false;
        }
        if (deaf != null) {
            return deaf;
        }
        if (!getPlayerConfig().contains("deaf")) {
            deaf = false;
            save();
            return false;
        }
        deaf = getPlayerConfig().getBoolean("deaf");
        save();
        return getPlayerConfig().getBoolean("deaf");
    }

    public void setDeaf(Boolean deaf) {
        setDeaf(deaf, -1L);
    }

    public Long getDeafTime() {
        if (deaftime != null) {
            return deaftime;
        }
        if (!getPlayerConfig().contains("deaftime")) {
            deaftime = 0L;
            save();
            return 0L;
        }
        deaftime = getPlayerConfig().getLong("deaftime");
        save();
        return getPlayerConfig().getLong("deaftime");

    }

    public Long getDeafTimeLeft() {
        return getDeafTime() - System.currentTimeMillis();
    }

    public void setDeaf(Boolean dea, Long time) {
        JsonConfig conf = getPlayerConfig();
        if (deaftime == null || deaftime == 0L) {
            deaftime = -1L;
        }
        if (time >= 1) {
            time = time + System.currentTimeMillis();
        }
        conf.set("deaf", dea);
        conf.set("deaftime", time);
        conf.save();
        deaf = dea;
        deaftime = deaf ? time : 0L;
        save();
    }

    public boolean isFrozen() {
        if (getFrozenTime() >= 1 && getFrozenTimeLeft() <= 1 && (freeze != null ? freeze : getPlayerConfig().getBoolean("freeze"))) {
            setFrozen(false);
            if (getPlayer().isOnline()) {
                r.sendMes(getOnlinePlayer(), "unfreezeTarget");
            }
            return false;
        }
        if (freeze != null) {
            return freeze;
        }
        if (!getPlayerConfig().contains("freeze")) {
            freeze = false;
            return false;
        }
        freeze = getPlayerConfig().getBoolean("freeze");
        save();
        return getPlayerConfig().getBoolean("freeze");
    }

    public void setFrozen(Boolean fr) {
        setFrozen(fr, -1L);
    }

    public Long getFrozenTime() {
        if (freezetime != null) {
            return freezetime;
        }
        if (!getPlayerConfig().contains("freezetime")) {
            freezetime = 0L;
            save();
            return 0L;
        }
        freezetime = getPlayerConfig().getLong("freezetime");
        save();
        return getPlayerConfig().getLong("freezetime");

    }

    public Long getFrozenTimeLeft() {
        return getFrozenTime() - System.currentTimeMillis();
    }

    public void setFrozen(Boolean fr, Long time) {
        JsonConfig conf = getPlayerConfig();
        if (freezetime == null || freezetime == 0L) {
            freezetime = -1L;
        }
        if (time >= 1) {
            time = time + System.currentTimeMillis();
        }
        conf.set("freeze", fr);
        conf.set("freezetime", time);
        conf.save();
        freeze = fr;
        freezetime = fr ? time : 0L;
        save();
    }

    public boolean isGod() {
        if (getGodTime() >= 1 && getGodTimeLeft() <= 1 && (god != null ? god : getPlayerConfig().getBoolean("god"))) {
            setGod(false);
            if (getPlayer().isOnline()) {
                r.sendMes(getOnlinePlayer(), "ungodTarget");
            }
            return false;
        }
        if (god != null) {
            return god;
        }
        if (!getPlayerConfig().contains("god")) {
            god = false;
            save();
            return false;
        }
        god = getPlayerConfig().getBoolean("god");
        save();
        return getPlayerConfig().getBoolean("god");
    }

    public void setGod(Boolean fr) {
        setGod(fr, -1L);
    }

    public Long getGodTime() {
        if (godtime != null) {
            return godtime;
        }
        if (!getPlayerConfig().contains("godtime")) {
            godtime = 0L;
            save();
            return 0L;
        }
        godtime = getPlayerConfig().getLong("godtime");
        save();
        return getPlayerConfig().getLong("godtime");

    }

    public Long getGodTimeLeft() {
        return getGodTime() - System.currentTimeMillis();
    }

    public void setGod(Boolean fr, Long time) {
        JsonConfig conf = getPlayerConfig();
        if (godtime == null || godtime == 0L) {
            godtime = -1L;
        }
        if (time >= 1) {
            time = time + System.currentTimeMillis();
        }
        conf.set("god", fr);
        conf.set("godtime", time);
        conf.save();
        god = fr;
        godtime = fr ? time : 0L;
        save();
    }

    public HashMap<String, Object[]> getHomes() {
        if (homes != null) {
            return homes;
        }
        homes = new HashMap<>();
        JsonConfig conf = getPlayerConfig();
        if (!conf.contains("homes")) {
            return homes;
        }
        for (String hname : conf.listKeys("homes", false)) {
            try {
                homes.put(hname, LocationUtil.convertStringToLocation(conf.getString("homes." + hname)));
            } catch (Exception ex) {
                r.log(r.negative + "Home " + getPlayer().getName() + ":" + hname + " has been removed. (Invalid location)");
            }
        }
        save();
        return homes;
    }

    public void setHomes(HashMap<String, Object[]> nh) {
        homes = nh;
        save();
        JsonConfig conf = getPlayerConfig();
        conf.set("homes", null);
        for (String s : nh.keySet()) {
            try {
                conf.set("homes." + s.toLowerCase(), LocationUtil.convertLocationToString((Location) nh.get(s)[0], (Vector3d) nh.get(s)[1]));
            } catch (Exception ex) {
                r.log("Invalid home: " + getPlayer().getName() + "/" + s);
            }
        }
        conf.save();
    }

    public ArrayList<String> getHomeNames() {
        ArrayList<String> h = new ArrayList<>();
        h.addAll(getHomes().keySet());
        return h;
    }

    public void addHome(String s, Location l, Vector3d rot) {
        HashMap<String, Object[]> h = getHomes();
        h.put(s.toLowerCase(), new Object[]{l, rot});
        setHomes(h);
    }

    public void removeHome(String s) {
        HashMap<String, Object[]> h = getHomes();
        h.remove(s.toLowerCase());
        setHomes(h);
    }

    public Object[] getHome(String s) {
        return getHomes().get(s.toLowerCase());
    }

    public void clearHomes() {
        setHomes(new HashMap<>());
    }

    public boolean isInOnlineInventory() {
        return onlineInv != null;
    }

    public Optional<Player> getInOnlineInventory() {
        if (onlineInv == null) {
            return null;
        }
        return r.searchPlayer(onlineInv);
    }

    public void setInOnlineInventory(Player p) {
        if (p == null) {
            onlineInv = null;
        } else {
            onlineInv = p.getUniqueId();
        }
        save();
    }

    public boolean isInOfflineInventory() {
        return offlineInv != null;
    }

    public Optional<GameProfile> getInOfflineInventory() {
        if (offlineInv == null) {
            return null;
        }
        return r.searchGameProfile(offlineInv);
    }


    public void setInOfflineInventory(GameProfile p) {
        if (p == null) {
            offlineInv = null;
        } else {
            offlineInv = p.getUniqueId();
        }
        save();
    }

    public void updateLastInventory() {
        JsonConfig conf = getPlayerConfig();
        conf.set("lastinventory", InventoryUtil.convertInventoryToString(getOnlinePlayer().getInventory()));
        conf.save();
    }

    public Inventory getLastInventory() {
        JsonConfig conf = getPlayerConfig();
        if (!conf.contains("lastinventory")) {
            return null;
        }
        return InventoryUtil.convertStringToInventory(conf.getString("lastinventory"), r.mes("inventoryOfflineTitle", "%Name", name));
    }

    public void jail() {
        jail(null, null);
    }

    public void jail(String n) {
        jail(n, null);
    }

    public void jail(Long l) {
        jail(new ArrayList<>(UC.getServer().getJails().keySet()).get(ra.nextInt(UC.getServer().getJails().keySet().size())), l);
    }

    public void jail(String n, Long l) {
        jailed = true;
        jail = n;
        if (l >= 1) {
            l = l + System.currentTimeMillis();
        }
        jailtime = l;
        JsonConfig conf = getPlayerConfig();
        conf.set("jailed", true);
        conf.set("jail", n);
        conf.set("jailtime", l == null ? 0L : l);
        conf.save();
        save();
    }

    public void unjail() {
        jailed = false;
        jail = null;
        jailtime = null;
        JsonConfig conf = getPlayerConfig();
        conf.set("jailed", false);
        conf.set("jail", null);
        conf.set("jailtime", null);
        conf.save();
        save();
        if (tpspawn && getOnlinePlayer() != null) {
            if (UC.getPlayer(getPlayer()).getSpawn(false) == null) {
                LocationUtil.teleport(getOnlinePlayer(), getOnlinePlayer().getWorld().getSpawnLocation(), Cause.builder().build(), false, false);
            } else {
                LocationUtil.teleport(getOnlinePlayer(), (Location) UC.getPlayer(getPlayer()).getSpawn(false)[0], Cause.builder().build(), false, false);
                getOnlinePlayer().setRotation((Vector3d) UC.getPlayer(getPlayer()).getSpawn(false)[1]);
            }
        }
    }

    public boolean isJailed() {
        if (getJailTime() >= 1 && getJailTimeLeft() <= 1 && (jailed != null ? jailed : getPlayerConfig().getBoolean("jailed"))) {
            unjail();
            if (getPlayer().isOnline()) {
                r.sendMes(getOnlinePlayer(), "unjailTarget");
                unjail();
            }
            return false;
        }
        if (jailed != null) {
            return jailed;
        }
        if (!getPlayerConfig().contains("jailed")) {
            jailed = false;
            save();
            return false;
        }
        jailed = getPlayerConfig().getBoolean("jailed");
        save();
        return getPlayerConfig().getBoolean("jailed");
    }

    public Long getJailTime() {
        if (jailtime != null) {
            return jailtime;
        }
        if (!getPlayerConfig().contains("jailtime")) {
            jailtime = 0L;
            save();
            return 0L;
        }
        jailtime = getPlayerConfig().getLong("jailtime");
        save();
        return getPlayerConfig().getLong("jailtime");

    }

    public Long getJailTimeLeft() {
        return getJailTime() - System.currentTimeMillis();
    }

    public String getJail() {
        if (jail != null) {
            return jail;
        }
        if (!isJailed()) {
            return null;
        }
        return getPlayerConfig().getString("jail");
    }

    public Optional<Player> getReply() {
        if (reply != null) {
            return r.searchPlayer(reply);
        }
        if (!getPlayerConfig().contains("reply")) {
            return Optional.empty();
        }
        return r.searchPlayer(UUID.fromString(getPlayerConfig().getString("reply")));
    }

    public void setReply(Player pl) {
        reply = pl.getUniqueId();
        JsonConfig conf = getPlayerConfig();
        conf.set("reply", pl.getUniqueId().toString());
        conf.save();
        save();
    }

    public boolean isSpy() {
        if (spy != null) {
            return spy;
        }
        if (!getPlayerConfig().contains("spy")) {
            return false;
        }
        spy = getPlayerConfig().getBoolean("spy");
        save();
        return spy;
    }

    public void setSpy(Boolean sp) {
        spy = sp;
        JsonConfig conf = getPlayerConfig();
        conf.set("spy", sp);
        conf.save();
        save();
    }

    public boolean isMuted() {
        if (getMuteTime() >= 1 && getMuteTimeLeft() <= 1 && (mute != null ? mute : getPlayerConfig().getBoolean("mute"))) {
            setMuted(false);
            if (getPlayer().isOnline()) {
                r.sendMes(getOnlinePlayer(), "unmuteTarget");
            }
            return false;
        }
        if (mute != null) {
            return mute;
        }
        if (!getPlayerConfig().contains("mute")) {
            mute = false;
            save();
            return false;
        }
        mute = getPlayerConfig().getBoolean("mute");
        save();
        return getPlayerConfig().getBoolean("mute");
    }

    public void setMuted(Boolean fr) {
        setMuted(fr, -1L, null);
    }

    public Long getMuteTime() {
        if (mutetime != null) {
            return mutetime;
        }
        if (!getPlayerConfig().contains("mutetime")) {
            mutetime = 0L;
            return 0L;
        }
        mutetime = getPlayerConfig().getLong("mutetime");
        save();
        return getPlayerConfig().getLong("mutetime");

    }

    public String getMuteReason() {
        if (mutereason != null) {
            return mutereason;
        }
        if (!getPlayerConfig().contains("mutereason")) {
            mutereason = null;
            return null;
        }
        mutereason = getPlayerConfig().getString("mutereason");
        save();
        return getPlayerConfig().getString("mutereason");

    }

    public Long getMuteTimeLeft() {
        return getMuteTime() - System.currentTimeMillis();
    }

    public void setMuted(Boolean fr, Long time, String reason) {
        JsonConfig conf = getPlayerConfig();
        if (mutetime == null || mutetime == 0L) {
            mutetime = -1L;
        }
        if (time >= 1) {
            time = time + System.currentTimeMillis();
        }
        if ((reason == null || reason == "") && fr) {
            reason = r.mes("muteDefaultReason").toPlain();
        }
        conf.set("mute", fr);
        conf.set("mutetime", time);
        conf.set("mutereason", reason);
        conf.save();
        mute = fr;
        mutetime = fr ? time : -1L;
        mutereason = reason;
        save();
    }

    public Text getDisplayName() {
        if (getNick() != null) {
            return getNick();
        }
        if (getPlayer().isOnline()) {
            if (getOnlinePlayer().get(Keys.DISPLAY_NAME).isPresent()) {
                return getOnlinePlayer().get(Keys.DISPLAY_NAME).get();
            }
        }
        return Text.of(getPlayer().getName());
    }

    public Text getNick() {
        if (nickname != null) {
            return nickname;
        }
        JsonConfig data = getPlayerConfig();
        if (data.get("nick") == null) {
            return null;
        }
        String nick = TextColorUtil.translateAlternate(data.getString("nick"));
        if (getPlayer().isOnline()) {
            getPlayer().getPlayer().get().offer(Keys.DISPLAY_NAME, Text.of(nick.replace("&y", "")));
        }
        if (getPlayer().isOnline() && r.perm((CommandSource) getPlayer(), "uc.chat.rainbow", false)) {
            nick = nick.replaceAll("&y", r.getRandomTextColor() + "");
        }
        nickname = Text.of(nick + TextColors.RESET);
        save();
        return nickname;
    }

    public void setNick(String str) {
        nickname = str == null ? null : Text.of(str + TextColors.RESET);
        save();
        if (str != null) {
            if (getPlayer().isOnline()) {
                getPlayer().getPlayer().get().offer(Keys.DISPLAY_NAME, Text.of(nickname.toPlain().replace("&y", "")));
            }
        } else {
            if (getPlayer().isOnline()) {
                getPlayer().getPlayer().get().offer(Keys.DISPLAY_NAME, Text.of(getPlayer().getPlayer().get().getName()));
            }
        }
        JsonConfig data = getPlayerConfig();
        data.set("nick", str);
        data.save(UltimateFileLoader.getPlayerFile(getPlayer().getProfile()));
    }

    public void clearAllPowertools() {
        for (ItemType mat : pts.keySet()) {
            clearPowertool(mat);
        }
        if (pts != null) {
            pts.clear(); //Just to make sure
        }
        save();
    }

    public void clearPowertool(ItemType mat) {
        if (pts == null) {
            JsonConfig data = getPlayerConfig();
            pts = new HashMap<>();
            if (data.contains("powertool")) {
                for (String s : data.listKeys("powertool", false)) {
                    ArrayList<String> l = (ArrayList<String>) data.getStringList("powertool." + s);
                    pts.put(ItemUtil.searchItem(s).getItem(), l);
                }
            }
        }
        pts.remove(mat);
        JsonConfig data = getPlayerConfig();
        data.set("powertool." + mat.toString(), null);
        data.save();
        save();
    }

    public List<String> getPowertools(ItemType mat) {
        if (mat == null || mat == ItemTypes.NONE) {
            return null;
        }
        if (pts == null) {
            JsonConfig data = getPlayerConfig();
            pts = new HashMap<>();
            if (data.contains("powertool")) {
                for (String s : data.listKeys("powertool", false)) {
                    ArrayList<String> l = (ArrayList<String>) data.getStringList("powertool." + s);
                    pts.put(ItemUtil.searchItem(s).getItem(), l);
                }
            }
        }
        save();
        if (pts.containsKey(mat)) {
            return new ArrayList<>(pts.get(mat));
        }
        return null;
    }

    public boolean hasPowertools() {
        if (pts == null) {
            JsonConfig data = getPlayerConfig();
            pts = new HashMap<>();
            if (data.contains("powertool")) {
                for (String s : data.listKeys("powertool", false)) {
                    ArrayList<String> l = (ArrayList<String>) data.getStringList("powertool." + s);
                    pts.put(ItemUtil.searchItem(s).getItem(), l);
                }
            }
            save();
        }
        return !pts.isEmpty();
    }

    public boolean hasPowertool(ItemType mat) {
        if (pts == null) {
            JsonConfig data = getPlayerConfig();
            pts = new HashMap<>();
            if (data.contains("powertool")) {
                for (String s : data.listKeys("powertool", false)) {
                    ArrayList<String> l = (ArrayList<String>) data.getStringList("powertool." + s);
                    pts.put(ItemUtil.searchItem(s).getItem(), l);
                }
            }
            save();
        }
        return pts.containsKey(mat);
    }

    public void setPowertool(ItemType mat, List<String> cmds) {
        JsonConfig data = getPlayerConfig();
        if (pts == null) {
            pts = new HashMap<>();
            if (data.contains("powertool")) {
                for (String s : data.listKeys("powertool", false)) {
                    ArrayList<String> l = (ArrayList<String>) data.getStringList("powertool." + s);
                    pts.put(ItemUtil.searchItem(s).getItem(), l);
                }
            }
        }
        pts.put(mat, cmds);
        data.set("powertool." + mat.toString(), cmds);
        data.save();
        save();
    }

    public void addPowertool(ItemType mat, String c) {
        List<String> ps = getPowertools(mat);
        ps.add(c);
        setPowertool(mat, ps);
    }

    public void removePowertool(ItemType mat, String c) {
        List<String> ps = getPowertools(mat);
        if (!ps.contains(c)) {
            return;
        }
        ps.remove(c);
        setPowertool(mat, ps);
    }

    public boolean isInRecipeView() {
        return inRecipeView;
    }

    public void setInRecipeView(Boolean b) {
        inRecipeView = b;
        save();
    }

    public boolean isInTeleportMenu() {
        return inTeleportMenu;
    }

    public void setInTeleportMenu(Boolean b) {
        inTeleportMenu = b;
        save();
    }

    public boolean isInCommandEnchantingtable() {
        return inCmdEnchantingtable;
    }

    public void setInCommandEnchantingtable(Boolean b) {
        inCmdEnchantingtable = b;
        save();
    }

    public boolean hasTeleportEnabled() {
        if (teleportEnabled != null) {
            return teleportEnabled;
        }
        if (!getPlayerConfig().contains("teleportenabled")) {
            return true;
        }
        teleportEnabled = getPlayerConfig().getBoolean("teleportenabled");
        save();
        return teleportEnabled;
    }

    public void setTeleportEnabled(Boolean tpe) {
        teleportEnabled = tpe;
        JsonConfig conf = getPlayerConfig();
        conf.set("teleportenabled", tpe);
        conf.save();
        save();
    }

    public boolean isVanish() {
        if (getVanishTime() >= 1 && getVanishTimeLeft() <= 1 && (vanish != null ? vanish : getPlayerConfig().getBoolean("vanish"))) {
            setVanish(false);
            if (getPlayer().isOnline()) {
                r.sendMes(getOnlinePlayer(), "unvanishTarget");
            }
            return false;
        }
        if (vanish != null) {
            return vanish;
        }
        if (!getPlayerConfig().contains("vanish")) {
            vanish = false;
            save();
            return false;
        }
        vanish = getPlayerConfig().getBoolean("vanish");
        save();
        return getPlayerConfig().getBoolean("vanish");
    }

    public void setVanish(Boolean fr) {
        setVanish(fr, -1L);
    }

    public Long getVanishTime() {
        if (vanishtime != null) {
            return vanishtime;
        }
        if (!getPlayerConfig().contains("vanishtime")) {
            vanishtime = 0L;
            save();
            return 0L;
        }
        vanishtime = getPlayerConfig().getLong("vanishtime");
        save();
        return getPlayerConfig().getLong("vanishtime");

    }

    public Long getVanishTimeLeft() {
        return getVanishTime() - System.currentTimeMillis();
    }

    public void setVanish(Boolean fr, Long time) {
        JsonConfig conf = getPlayerConfig();
        if (vanishtime == null || vanishtime == 0L) {
            vanishtime = -1L;
        }
        if (time >= 1) {
            time = time + System.currentTimeMillis();
        }
        conf.set("vanish", fr);
        conf.set("vanishtime", time);
        conf.save();
        vanish = fr;
        vanishtime = fr ? time : 0L;
        if (getOnlinePlayer() != null) {
            for (Player pl : r.getOnlinePlayers()) {
                if (fr) {
                    pl.offer(Keys.INVISIBLE, true);
                } else {
                    pl.offer(Keys.INVISIBLE, false);
                }
            }
        }
        save();
    }

    public boolean isAfk() {
        return afk;
    }

    public void setAfk(boolean news) {
        if (news == false && getPlayer().isOnline()) {
            getOnlinePlayer().clearTitle();
        }
        afk = news;
        save();
    }

    public long getLastActivity() {
        return lastaction;
    }

    public void setLastActivity(Long last) {
        lastaction = last;
        save();
    }

    public void updateLastActivity() {
        setLastActivity(System.currentTimeMillis());
        if (isAfk()) {
            setAfk(false);
            Sponge.getServer().getBroadcastChannel().send(r.mes("afkUnafk", "%Player", UC.getPlayer(getPlayer()).getDisplayName()));
        }
    }

    public Object[] getSpawn(Boolean firstjoin) {
        JsonConfig conf = new JsonConfig(UltimateFileLoader.Dspawns);
        String loc;
        Player p = r.searchPlayer(uuid).get();

        PermissionService service = Sponge.getServiceManager().provide(PermissionService.class).get();
        Subject maingroup = p.getSubjectData().getParents(new HashSet<>()).get(0);

        Boolean world = conf.contains("worlds.world." + p.getWorld().getName() + ".global");
        String world_ = world ? conf.getString("worlds.world." + p.getWorld().getName() + ".global") : null;
        Boolean group = maingroup != null && conf.contains("global.group." + maingroup.getIdentifier());
        String group_ = (group ? conf.getString("global.group." + maingroup) : null);
        Boolean gw = maingroup != null && conf.contains("worlds.world." + p.getWorld().getName() + ".group." + maingroup);
        String gw_ = gw ? conf.getString("worlds.world." + p.getWorld().getName() + ".group." + maingroup) : null;
        if (firstjoin && conf.contains("global.firstjoin")) {
            loc = conf.getString("global.firstjoin");
        } else if (gw) {
            loc = gw_;
        } else if (world && group) {
            if (r.getCnfg().getBoolean("Command.Spawn.WorldOrGroup")) {
                loc = world_;
            } else {
                loc = group_;
            }
        } else if (world) {
            loc = world_;
        } else if (group) {
            loc = group_;
        } else if (conf.contains("global")) {
            loc = conf.getString("global");
        } else {
            return null;
        }
        return LocationUtil.convertStringToLocation(loc);
    }


}
