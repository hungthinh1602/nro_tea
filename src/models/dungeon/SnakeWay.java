package models.dungeon;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import utils.Functions;
import models.boss.boss_list.SnakeWay.SAIBAMEN;
import models.boss.boss_list.SnakeWay.NADIC;
import models.boss.boss_list.SnakeWay.CADICH;
import models.boss.Boss;
import consts.BossStatus;
import models.clan.Clan;
import models.map.Zone;
import models.mob.Mob;
import models.player.Player;
import services.ItemTimeService;
import services.map.MapService;
import services.Service;
import services.map.ChangeMapService;
import utils.Util;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import server.Maintenance;
import services.map.ItemMapService;
import utils.TimeUtil;

@Data
public class SnakeWay implements Runnable {

    public static final long POWER_CAN_GO_TO_CDRD = 2000000000;
    public static final int AVAILABLE = 50;
    public static final int TIME_CON_DUONG_RAN_DOC = 1800000;

    public int id;
    public byte level;
    public final List<Zone> zones;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;
    private long lastTimeUpdateMessage;
    private boolean kickoutcdrd;
    private long timeKickOutCDRD;
    public List<Boss> bosses = new ArrayList<>();
    public boolean endCDRD;
    public boolean allMobsDead;

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public SnakeWay(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning && isOpened) {
            try {
                long startTime = System.currentTimeMillis();
                update();
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = 150 - elapsedTime;
                if (sleepTime > 0) {
                    Functions.sleep(sleepTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (isOpened) {
            if (Util.canDoWithTime(lastTimeOpen, TIME_CON_DUONG_RAN_DOC) || (kickoutcdrd && Util.canDoWithTime(timeKickOutCDRD, 60000))) {
                finish();
                dispose();
            }

            boolean allCharactersDead = true;
            for (Zone zone : zones) {
                for (Mob mob : zone.mobs) {
                    if (!mob.isDie()) {
                        allCharactersDead = false;
                        break;
                    }
                }
            }
            if (allCharactersDead) {
                allMobsDead = true;
            }

            if (!kickoutcdrd && (endCDRD || Util.canDoWithTime(lastTimeOpen, TIME_CON_DUONG_RAN_DOC - 60000))) {
                kickoutcdrd = true;
                timeKickOutCDRD = System.currentTimeMillis();
            }
            if (kickoutcdrd && Util.canDoWithTime(lastTimeUpdateMessage, 10000)) {
                lastTimeUpdateMessage = System.currentTimeMillis();
                for (Zone zone : zones) {
                    List<Player> players = zone.getPlayers();
                    for (Player pl : players) {
                        Service.gI().sendThongBao(pl, "Trận chiến với người Xayda sẽ kết thúc sau " + TimeUtil.getTimeLeft(timeKickOutCDRD, 60) + " nữa");
                    }

                }
            }

        }
    }

    public void openConDuongRanDoc(Player plOpen, Clan clan, byte level) {
        try {
            this.level = level;
            this.lastTimeOpen = System.currentTimeMillis();
            this.clan = clan;
            this.clan.lastTimeOpenConDuongRanDoc = this.lastTimeOpen;
            this.clan.playerOpenConDuongRanDoc = plOpen;
            this.clan.ConDuongRanDoc = this;
            this.isOpened = true;
            this.init();
            sendTextConDuongRanDoc();
        } catch (Exception e) {
            plOpen.clan.lastTimeOpenConDuongRanDoc = 0;
            this.dispose();
        }
    }

    private void init() {
        //Hồi sinh quái
        for (Zone zone : this.zones) {
            List<Mob> mobs = zone.mobs;
            for (int i = 0; i < mobs.size(); i++) {
                Mob mob = mobs.get(i);
                if (i == 5) {
                    mob.lvMob = 1;
                    mob.point.dame = (int) level * 100 * mob.tempId * 12;
                    mob.point.maxHp = (int) level * 1000 * mob.tempId * 12;
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                } else {
                    mob.lvMob = 0;
                    mob.point.dame = (int) level * 100 * mob.tempId;
                    mob.point.maxHp = (int) level * 1000 * mob.tempId;
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                }
            }

            if (zone.map.mapId == 144) {
                try {
                    long bossDamage = (200000 * level);
                    long bossMaxHealth = (2000000 * level);
                    for (int i = 6; i > 0; i--) {
                        bossDamage = Math.min(bossDamage, 200000000L);
                        bossMaxHealth = Math.min(bossMaxHealth, 2000000000L);
                        bosses.add(new SAIBAMEN(
                                zone,
                                clan,
                                i,
                                (int) bossDamage,
                                (int) bossMaxHealth
                        ));
                    }
                    bossDamage = Math.min(bossDamage * 5, 200000000L);
                    bossMaxHealth = Math.min(bossMaxHealth * 5, 2000000000L);
                    bosses.add(new NADIC(
                            zone,
                            clan,
                            (int) bossDamage,
                            (int) bossMaxHealth
                    ));
                    bossDamage = Math.min(bossDamage * 10, 200000000L);
                    bossMaxHealth = Math.min(bossMaxHealth * 10, 2000000000L);
                    bosses.add(new CADICH(
                            zone,
                            clan,
                            (int) bossDamage,
                            (int) bossMaxHealth
                    ));
                } catch (Exception exception) {
                }
            }
        }
        new Thread(this, "Con Đường Rắn Độc: " + this.clan.name).start();
    }

    //kết thúc con đường rắn độc
    public void finish() {
        for (Zone zone : zones) {
            for (int i = zone.getPlayers().size() - 1; i >= 0; i--) {
                if (i < zone.getPlayers().size()) {
                    Player pl = zone.getPlayers().get(i);
                    kickOutOfCDRD(pl);
                }
            }

        }
    }

    private void kickOutOfCDRD(Player player) {
        if (MapService.gI().isMapConDuongRanDoc(player.zone.map.mapId)) {
            ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1038);
        }
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    private void sendTextConDuongRanDoc() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextConDuongRanDoc(pl);
        }
    }

    private void removeTextConDuongRanDoc() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().removeTextConDuongRanDoc(pl);
        }
    }

    public long getNumBossAlive() {
        return bosses.stream().filter(boss -> boss.bossStatus != BossStatus.REST).count();
    }

    public void dispose() {
        // remove bosses
        for (Boss boss : bosses) {
            if (!boss.isDie()) {
                boss.leaveMap();
            }
        }
        for (Zone zone : zones) {
            for (int i = zone.items.size() - 1; i >= 0; i--) {
                if (i < zone.items.size()) {
                    ItemMapService.gI().removeItemMap(zone.items.get(i));
                }
            }
        }
        this.removeTextConDuongRanDoc();
        this.bosses.clear();
        this.allMobsDead = false;
        this.endCDRD = false;
        this.isOpened = false;
        this.clan.ConDuongRanDoc = null;
        this.clan = null;
        this.kickoutcdrd = false;
    }
}
