package models.mob;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import services.player.InventoryService;
import services.Service;
import services.TaskService;
import services.map.ItemMapService;
import consts.ConstMap;
import consts.ConstMob;
import consts.ConstTask;
import event.EventManager;
import models.item.Item;
import models.map.ItemMap;

import java.util.List;

import models.map.Zone;
import models.player.Location;
import models.player.Pet;
import models.player.Player;
import network.io.Message;
import java.io.IOException;
import server.Maintenance;
import server.Manager;
import utils.Util;

import java.util.ArrayList;
import services.AchievementService;
import services.dungeon.TrainingService;
import server.ServerNotify;
import services.ItemService;
import services.map.MapService;
import models.skill.Skill;
import utils.TimeUtil;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public List<Player> temporaryEnemies = new ArrayList<>();

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;
    public int type = 1;

    private long lastTimeAttackPlayer;
    private long timeAttack = 2000;
    public long lastTimePhucHoi = System.currentTimeMillis();
    public long lastTimeSendEffect = System.currentTimeMillis();

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.type = mob.type;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (long) (this.pTiemNang + Util.nextInt(-2, 2)) / 100L;
    }

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public void setDie() {
        this.lastTimePhucHoi = System.currentTimeMillis();
        this.lastTimeDie = System.currentTimeMillis();
    }

    public void addTemporaryEnemies(Player pl) {
        if (pl != null && !temporaryEnemies.contains(pl)) {
            temporaryEnemies.add(pl);
        }
    }

    public void injured(Player plAtt, long damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if ((this.tempId == ConstMob.MOC_NHAN || this.tempId == ConstMob.MAY_DO_SUC_MANH) && damage > this.point.maxHp / 10) {
                    damage = this.point.maxHp / 10;
                }
            }
            if (MapService.gI().isMapKhiGasHuyDiet(this.zone.map.mapId)) {
                boolean mob76Die = true;
                for (Mob mob : this.zone.mobs) {
                    if (!mob.isDie() && mob.tempId == ConstMob.CO_MAY_HUY_DIET) {
                        mob76Die = false;
                        break;
                    }
                }
                if (!mob76Die && plAtt != null && plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null) {
                    switch (plAtt.playerSkill.skillSelect.template.id) {
                        case Skill.LIEN_HOAN, Skill.ANTOMIC, Skill.MASENKO, Skill.KAMEJOKO ->
                            damage = 1;
                    }
                }
            }
            if (!dieWhenHpFull && !isBigBoss() && !MapService.gI().isMapPhoBan(this.zone.map.mapId) && this.lvMob > 0 && plAtt != null && plAtt.charms.tdOaiHung < System.currentTimeMillis()) {
                damage = (int) ((this.point.maxHp <= 20000000 ? this.point.maxHp * 10 : 2000000000) * (10.0 / 100));
                this.mobAttackPlayer(plAtt);
            }
            if (plAtt != null && plAtt.isBoss && this.tempId > 0 && Util.isTrue(1, 2) && Util.canDoWithTime(lastTimeAttackPlayer, 2500)) {
                this.mobAttackPlayer(plAtt);
                lastTimeAttackPlayer = System.currentTimeMillis();
            }

            if (damage > 2_000_000_000) {
                damage = 2_000_000_000;
            }

            this.point.hp -= damage;
            addTemporaryEnemies(plAtt);
            if (this.isDie()) {
                this.status = 0;
                this.setDie();
                this.temporaryEnemies.clear();
                if (plAtt != null) {
                    this.sendMobDieAffterAttacked(plAtt, (int) damage);
                    TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                    TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                    TaskService.gI().checkDoneClanTaskKillMob(plAtt, this);
                    AchievementService.gI().checkDoneTaskKillMob(plAtt, this);
                }
                if (this.id == 13) {
                    this.zone.isbulon1Alive = false;
                }
                if (this.id == 14) {
                    this.zone.isbulon2Alive = false;
                }
            } else {
                this.sendMobStillAliveAffterAttacked((int) damage, plAtt != null ? (plAtt.nPoint != null && plAtt.nPoint.isCrit) : false);
            }
            if (plAtt != null) {
                if (plAtt.isPl() && plAtt.satellite != null && plAtt.satellite.isDefend) {
                    plAtt.satellite.isDefend = false;
                }
                Service.gI().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
                TrainingService.gI().tangTnsmLuyenTap(plAtt, getTiemNangForPlayer(plAtt, damage));
            }
        }
    }

    public long getTiemNangForPlayer(Player pl, long dame) {
        int levelPlayer = Service.gI().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        long pDameHit = dame * 100 / point.getHpFull();
        long tiemNang = pDameHit * maxTiemNang / 100;
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (pl.nPoint != null) {
            tiemNang = (int) pl.nPoint.calSucManhTiemNang(tiemNang);
        } else {
            return 0;
        }
        if (pl.zone.map.mapId == 122 || pl.zone.map.mapId == 123 || pl.zone.map.mapId == 124) {
            tiemNang *= 2;
        }
        if (pl.zone.map.mapId == 153) {
            tiemNang /= 2;
        }
        if (pl.zone.map.mapId == 156 || pl.zone.map.mapId == 157 || pl.zone.map.mapId == 158 || pl.zone.map.mapId == 159) {
            tiemNang /= 2;
        }
        return tiemNang;
    }

    public void update() {
        if (zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
            if (!isDie()) {
                startDie();
                return;
            }
        }
        if (!this.isDie() && this.tempId == ConstMob.CO_MAY_HUY_DIET && Util.canDoWithTime(lastTimeSendEffect, 1000)) {
            sendEffect(55);
            lastTimeSendEffect = System.currentTimeMillis();
        }

        if (this.isDie() && !Maintenance.isRunning && !isBigBoss()) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    if (this.tempId == ConstMob.BULON && this.zone.isTUTAlive && Util.canDoWithTime(lastTimeDie, 10000)) {
                        this.hoiSinh();
                        this.hoiSinhMobPhoBan();
                        if (this.id == 13) {
                            this.zone.isbulon1Alive = true;
                        }
                        if (this.id == 14) {
                            this.zone.isbulon2Alive = true;
                        }
                    }
                    break;
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    break;
                case ConstMap.MAP_CON_DUONG_RAN_DOC:
                    break;
                case ConstMap.MAP_KHI_GAS_HUY_DIET:
                    break;
                case ConstMap.MAP_TAY_KARIN:
                    break;
                default:
                    if (this.zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
                        return;
                    }
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }
                    if (Util.canDoWithTime(lastTimePhucHoi, 30000) && !isDie()) {
                        lastTimePhucHoi = System.currentTimeMillis();
                        int hpMax = this.point.maxHp;
                        if (this.point.hp < hpMax) {
                            hoi_hp(hpMax / 10);
                        } else {
                            this.sendMobHoiSinh();
                        }
                    }
            }
        }
        effectSkill.update();
        attack();
    }

    public boolean isBigBoss() {
        return (this.tempId == ConstMob.HIRUDEGARN || this.tempId == ConstMob.VUA_BACH_TUOC
                || this.tempId == ConstMob.ROBOT_BAO_VE || this.tempId == ConstMob.GAU_TUONG_CUOP
                || this.tempId == ConstMob.VOI_CHIN_NGA || this.tempId == ConstMob.GA_CHIN_CUA
                || this.tempId == ConstMob.NGUA_CHIN_LMAO || this.tempId == ConstMob.PIANO);
    }

    public void attack() {
        Player player = getPlayerCanAttack();
        if (!isDie() && !effectSkill.isHaveEffectSkill() && tempId != ConstMob.MOC_NHAN && tempId != ConstMob.MAY_DO_SUC_MANH && tempId != ConstMob.CO_MAY_HUY_DIET && !this.isBigBoss() && (this.lvMob < 1 || MapService.gI().isMapPhoBan(this.zone.map.mapId)) && Util.canDoWithTime(lastTimeAttackPlayer, timeAttack)) {
            if (player != null) {
                this.mobAttackPlayer(player);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    public Player getPlayerCanAttack() {
        Player plAttack = getFirstPlayerCanAttack();
        if (plAttack != null) {
            return plAttack;
        }
        int distance = 100;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.isNewPet && (pl.satellite == null || !pl.satellite.isDefend) && (pl.effectSkin == null || !pl.effectSkin.isVoHinh) && (this.tempId > 18 || (this.tempId > 9 && this.type == 4)) || isBigBoss()) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance || isBigBoss()) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
            this.timeAttack = 2000;
        } catch (Exception e) {

        }
        return plAttack;
    }

    private Player getFirstPlayerCanAttack() {
        Player plAtt = null;
        try {
            List<Player> playersMap = zone.getHumanoids();
            int dis = 300;
            if (playersMap != null) {
                for (Player plAttt : playersMap) {
                    if (plAttt.isDie() || plAttt.isBoss || (plAttt.satellite != null && plAttt.satellite.isDefend) || (plAttt.effectSkin != null && plAttt.effectSkin.isVoHinh) || !this.temporaryEnemies.contains(plAttt)) {
                        continue;
                    }
                    int d = Util.getDistance(plAttt, this);
                    if (d <= dis) {
                        dis = d;
                        plAtt = plAttt;
                    }
                }
            }
            this.timeAttack = 1000;
        } catch (Exception e) {

        }
        return plAtt;
    }

    private void mobAttackPlayer(Player player) {
        int dameMob = this.point.getDameAttack();
        if (player.charms != null && player.charms.tdDaTrau > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (player.isPet && ((Pet) player).master.charms != null && ((Pet) player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (this.lvMob > 0 && !MapService.gI().isMapPhoBan(this.zone.map.mapId)) {
            dameMob = (int) (player.nPoint.hpMax * (10.0 / 100));
        }
        if (player.satellite != null && player.satellite.isDefend) {
            dameMob -= dameMob / 5;
        }
        if (player.itemTime != null && player.itemTime.isUseCMS) {
            dameMob = (int) Math.round(dameMob * 0.1);
        }
        if (this.lvMob > 0 && player.charms.tdOaiHung > System.currentTimeMillis()) {
            dameMob = 0;
        }
        int dame = player.injured(null, dameMob, false, true);

        this.sendMobAttackMe(player, dame);
        this.sendMobAttackPlayer(player);
        this.phanSatThuong(player, dame);
    }

    private void sendMobAttackMe(Player player, int dame) {
        if (!player.isPet && !player.isNewPet && !player.isBot) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(dame); //dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(player.nPoint.hp);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    public int lvMob() {
        for (Mob mobMap : this.zone.mobs) {
            if (mobMap.lvMob > 0) {
                return 0;
            }
        }
        this.lvMob = this.tempId > 18 && !isBigBoss() ? Util.isTrue(10, 100) ? 1 : 0 : 0;
        this.point.hp = this.lvMob > 0 ? this.point.maxHp <= 20000000 ? this.point.maxHp * 10 : 2000000000 : this.point.maxHp;
        return this.lvMob;
    }

    public void sendMobHoiSinh() {
        Message msg = null;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(0);
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            this.sendMobMaxHp(this.point.hp);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void hoi_hp(int hp) {
        Message msg = null;
        try {
            this.point.sethp(this.point.gethp() + hp);
            int HP = hp > 0 ? 1 : Math.abs(hp);
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(HP);
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendEffect(int Effect) {
        Message msg = null;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(Effect);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void sendMobDieAffterAttacked(Player plKill, int dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
        }
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isPet && !player.isBot && !player.isNewPet) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(player, item.itemMapId, true);
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
        List<ItemMap> itemReward = new ArrayList<>();

        try {
            // Kiểm tra xem người chơi có phải là bot không
            if (player.isBot) {
                // Nếu là bot, không rơi vật phẩm, trả về danh sách rỗng
                msg.writer().writeByte(0);  // không có vật phẩm
                return itemReward;  // trả về danh sách rỗng
            }

            // Tiến hành các phần thưởng thông thường nếu người chơi không phải bot
            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));

            if (itemTask != null) {
                itemReward.add(itemTask);
            }

            // Gửi thông tin vật phẩm cho người chơi
            msg.writer().writeByte(itemReward.size());  // gửi số lượng vật phẩm

            // Lặp qua từng vật phẩm và ghi thông tin
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);  // id vật phẩm
                msg.writer().writeShort(itemMap.itemTemplate.id);  // id item template
                msg.writer().writeShort(itemMap.x);  // tọa độ x của vật phẩm
                msg.writer().writeShort(itemMap.y);  // tọa độ y của vật phẩm
                msg.writer().writeInt((int) itemMap.playerId);  // id của người chơi
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemReward;
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        if (player.isBoss) {
            return list;
        }
        if (this.tempId == 0) {
            return list;
        }
        int mapid = player.zone.map.mapId;

        if (EventManager.CHRISTMAS) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (Util.isTrue(1, 100)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropTatVoGiangSinh(50)) {
                    list.add(new ItemMap(zone, 649, 1, x, yEnd, player.id));
                }
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
//            if (Util.isTrue(1, 300)) {
//                if (pl.itemEvent != null && pl.itemEvent.canDropTatVoGiangSinh(50)) {
//                    list.add(new ItemMap(zone, 649, 1, x, yEnd, player.id));
//                }
//            }
        }
        if (mapid == 5 || mapid == 13) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (Util.isTrue(1, 10)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropBinhNuoc(100)) {
                    list.add(new ItemMap(zone, 456, 1, x, yEnd, pl.id));
                }
            }
        }
        if (EventManager.INTERNATIONAL_WOMANS_DAY) {
            Player pl = player;
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (pl.isPet) {
                pl = ((Pet) pl).master;
            }
            if (Util.isTrue(1, 50000000)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropHoaHong(100)) {
                    list.add(new ItemMap(zone, 610, 1, x, yEnd, player.id));
                }
            }
        }

        if (player.itemTime.isUseMayDo && (Util.isTrue(15, 1000) || (player.isActive() && Util.isTrue(1, 200))) && this.tempId > 57 && this.tempId < 66) {
            list.add(new ItemMap(zone, 380, 1, x, yEnd, player.id));
        }
        if (player.itemTime.isUseMayDo2 && Util.isTrue(15, 100) && this.tempId > 80 && this.tempId < 81) {
            list.add(new ItemMap(zone, 1110, 1, x, yEnd, player.id));
        }

        if (player.isPl() && TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
            if (player.gender == 0 && this.tempId == 11 || player.gender == 1 && this.tempId == 12 || player.gender == 2 && this.tempId == 10) {
                list.add(new ItemMap(zone, 20, 1, x, yEnd, player.id));
                TaskService.gI().checkDoneTaskFind7Stars(player);
            }
        }

        if (MapService.gI().isMapUpPorata(mapid)) {
            if (Util.isTrue(1, 50)) {
                ItemMap it = new ItemMap(zone, 934, Util.nextInt(1, 2), x, yEnd, player.id);
                it.options.add(new Item.ItemOption(86, 0));
                list.add(it);
            } else if (Util.isTrue(1, 50)) {
                ItemMap it = new ItemMap(zone, 935, Util.nextInt(1, 2), x, yEnd, player.id);
                it.options.add(new Item.ItemOption(86, 0));
                list.add(it);
            } else if (Util.isTrue(1, 50)) {
                ItemMap it = new ItemMap(zone, 933, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(31, Util.nextInt(5, 50)));
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            }
        }

        // Vang roi
        if (Util.isTrue(1, 100) || Manager.TEST && Util.isTrue(1, 100)) {
            int vang = MapService.gI().isMapNappa(mapid) ? Util.nextInt(5000, 10000)
                    : MapService.gI().isMapTuongLai(mapid) ? Util.nextInt(10000, 20000)
                    : MapService.gI().isMapCold(mapid) ? Util.nextInt(20000, 30000)
                    : mapid >= 155 && mapid <= 159 ? Util.nextInt(10000, 30000)
                            : Util.nextInt(1000, 5000);
            if (player.nPoint.tlGold > 0) {
                vang += vang * player.nPoint.tlGold / 100;
            }
            if (vang < 20000) {
                list.add(new ItemMap(zone, 189, vang, x, yEnd, player.id));
            } else if (vang < 30000) {
                list.add(new ItemMap(zone, 188, vang, x, yEnd, player.id));
            } else {
                list.add(new ItemMap(zone, 190, vang, x, yEnd, player.id));
            }
        }
        if (player.isPl() && Util.isTrue(1, 2000) && MapService.gI().isMapUpSKH(mapid)) {
            short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
            ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
            if (!ops.isEmpty()) {
                it.options = ops;
            }
            it.options.add(new Item.ItemOption(107, Util.nextInt(0, 1)));
            it.options.add(new Item.ItemOption(73, 0));
            list.add(it);
        }
        if (player.getSession() != null && player.getSession().player != null && player.getSession().player.nPoint != null && player.getSession().player.nPoint.power >= 80000000000L) {
            if (Util.isTrue(1, 1000)) {
                ItemMap it = new ItemMap(zone, 1191, 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(30, 0));
                list.add(it);
            }
        }
        if (mapid == 5 && Util.isTrue(1, 100)) {
            ItemMap it = new ItemMap(zone, 456, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(93, 1));
            it.options.add(new Item.ItemOption(30, 1));
            list.add(it);
        }
        //Set kich hoat
        if (((Util.isTrue(1, 400000)) || (Manager.TEST && Util.isTrue(5, 1000000)) || Util.isTrue(1, 10000000)) && MapService.gI().isMapUpSKH(mapid)) {
            short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
            ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
            if (!ops.isEmpty()) {
                it.options = ops;
            }

            int[] opsrand = ItemService.gI().randOptionItemKichHoat(player.gender);
            it.options.add(new Item.ItemOption(opsrand[0], 0));
            it.options.add(new Item.ItemOption(opsrand[1], 0));
            it.options.add(new Item.ItemOption(30, 0));
            list.add(it);
        }
        if ((Util.isTrue(1, 5800) && MapService.gI().isMapUpDoSao(mapid))) {
            short itTemp1 = (short) ItemService.gI().randTempItemDoSao();
            ItemMap it = new ItemMap(zone, itTemp1, 1, x, yEnd, player.id);
            List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp1);
            // Kiểm tra xem có không có ops không
            if (!ops.isEmpty()) {
                it.options = ops;
            }
            it.options.add(new Item.ItemOption(107, Util.nextInt(0, 3)));
            if (Util.isTrue(20, 100)) {
                int randomOption = Util.nextInt(34, 35);
                it.options.add(new Item.ItemOption(randomOption, 0));
            }
            // Thêm các option mặc định
            it.options.add(new Item.ItemOption(208, 0));
            // Thêm item vào danh sách
            list.add(it);
        }
        if (player.inventory.haveOption(player.inventory.itemsBody, 5, 106) && Util.isTrue(1, 2000)) {
            ItemMap it = new ItemMap(zone, 1207, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(30, 0));
            list.add(it);
        }

        //Sao pha le
        if (Util.isTrue(1, 3000) || (player.nPoint.isDoSPL && Util.isTrue(5, 200)) || (player.isActive() && Util.isTrue(1, 250))) {
            int rand = Util.nextInt(0, 6);
            ItemMap it = new ItemMap(zone, 441 + rand, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
            list.add(it);
        }

        //Da nang cap
        if (Util.isTrue(1, 10000) || (Util.isTrue(5, 1000) && MapService.gI().isMapTuongLai(mapid)) || (player.isActive() && Util.isTrue(5, 500))) {
            int rand = Util.nextInt(0, 4);
            ItemMap it = new ItemMap(zone, 220 + rand, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(71 - rand, 0));
            list.add(it);
        }
        if (Util.isTrue(1, 1000) && MapService.gI().isMapCold(mapid)) {
            ItemMap it = new ItemMap(zone, 930, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(87, 0));
            list.add(it);
        }
        if (Util.isTrue(1, 1000) && MapService.gI().isMapPhoBan(mapid)) {
            ItemMap it = new ItemMap(zone, 928, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(87, 0));
            list.add(it);
        }
        if (Util.isTrue(5, 1000) && MapService.gI().isMapPhoBan(mapid)) {
            ItemMap it = new ItemMap(zone, 1208, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(93, 25));
            list.add(it);
        }
        //rồng băng
        if (Util.isTrue(1, 1000)) {
            ItemMap it = new ItemMap(zone, 931, 1, x, yEnd, player.id);
            it.options.add(new Item.ItemOption(87, 0));
            list.add(it);
        }
        if (player.getSession() != null
                && player.getSession().player != null
                && player.getSession().player.nPoint != null
                && player.getSession().player.nPoint.power >= 20000000000L
                && MapService.gI().isMapCold(mapid) && Util.isTrue(1, 200)) {

            ItemMap it = new ItemMap(zone, 1215, 1, x, yEnd, player.id);
            //   it.options.add(new Item.ItemOption(30, 0));
            list.add(it);
        }
        if (player.getSession() != null
                && player.getSession().player != null
                && player.getSession().player.nPoint != null
                && player.getSession().player.nPoint.power >= 20000000000L
                && MapService.gI().isMapHanhTinhThucVat(mapid) && Util.isTrue(1, 200)) {

            ItemMap it = new ItemMap(zone, 1216, 1, x, yEnd, player.id);
            //   it.options.add(new Item.ItemOption(30, 0));
            list.add(it);
        }
        if (Util.isTrue(5, 1000) && MapService.gI().isMapPhoBan(mapid)) {
            ItemMap it = new ItemMap(zone, 1217, 1, x, yEnd, player.id);
            //    it.options.add(new Item.ItemOption(93, 25));
            list.add(it);
        }
        if (MapService.gI().isMapCold(mapid)) {
            if (player.isPet) {
                player = ((Pet) player).master;
            }
            if (player.isPet) {
                player = ((Pet) player).master;
            }
            if (Util.isTrue(1, 5000000) || (player.isActive() && Util.isTrue(1, 450000)) || (player.isAdmin() && Util.isTrue(10, 50))) {
                ItemMap it = ItemService.gI().randDoTL(this.zone, 1, x, yEnd, player.id);
                list.add(it);
                ServerNotify.gI().notify(player.name + " vừa nhặt được " + it.itemTemplate.name + " tại " + this.zone.map.mapName + " khu " + this.zone.zoneId);
            }
            if ((Util.isTrue(1, 5000) || (player.isActive() && Util.isTrue(1, 500))) && InventoryService.gI().fullSetThan(player)) {
                ItemMap it = new ItemMap(zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
                it.options.add(new Item.ItemOption(73, 0));
                list.add(it);
            }
        }
        // Kiểm tra xem bản đồ có phải là "Map Tương Lai"
        if (MapService.gI().isMapTuongLai(mapid) && (Util.isTrue(1, 20000) || Util.isTrue(1, 12550))) {
            // Nhặt item Lưỡng Long ngẫu nhiên
            ItemMap it = ItemService.gI().randDoLuongLong(this.zone, 1, x, yEnd, player.id);
            list.add(it);
            // Thông báo cho server
            ServerNotify.gI().notify(player.name + " vừa nhặt được " + it.itemTemplate.name + " tại " + this.zone.map.mapName + " khu " + this.zone.zoneId);
        }

        // Kiểm tra xem bản đồ có phải là "Map Nappa"
        if (MapService.gI().isMapNappa(mapid) && (Util.isTrue(1, 20000) || Util.isTrue(1, 12550))) {
            // Nhặt item Kaio ngẫu nhiên
            ItemMap it = ItemService.gI().randDoKaio(this.zone, 1, x, yEnd, player.id);
            list.add(it);
            // Thông báo cho server
            ServerNotify.gI().notify(player.name + " vừa nhặt được " + it.itemTemplate.name + " tại " + this.zone.map.mapName + " khu " + this.zone.zoneId);
        }

//        if (MapService.gI().isMapTuongLai(mapid) && ((Util.isTrue(1, 1000) || (player.isActive() && Util.isTrue(1, 1500)))) && InventoryService.gI().fullSetThan(player)) {
//            ItemMap it = new ItemMap(zone, Util.nextInt(663, 667), 1, x, yEnd, player.id);
//            it.options.add(new Item.ItemOption(73, 0));
//            list.add(it);
//        }
        //Item nro , rác
        if (Util.isTrue(1, 100) || (player.isActive() && Util.isTrue(1, 100))) {
            list.add(new ItemMap(zone, 225, 1, x, yEnd, player.id));
//        }
//        if (Util.isTrue(1, 100) || (player.isActive() && Util.isTrue(1, 200))) {
//            list.add(new ItemMap(zone, 533, 1, x, yEnd, player.id));
        }
        if (Util.isTrue(1, 10000) || (player.isActive() && Util.isTrue(1, 50000))) {
            list.add(new ItemMap(zone, 18, 1, x, yEnd, player.id));
        }
        if (Util.isTrue(1, 10000) || (player.isActive() && Util.isTrue(1, 1000))) {
            list.add(new ItemMap(zone, 19, 1, x, yEnd, player.id));
        }
        if (Util.isTrue(1, 10000) || (player.isActive() && Util.isTrue(1, 1000))) {
            list.add(new ItemMap(zone, 20, 1, x, yEnd, player.id));
        }
        //Úp quái rơi ngọc
        if (Util.isTrue(2, 10000) || (player.isActive() && Util.isTrue(2, 1000))) {
            int rand_ngoc = Util.nextInt(1, 3);
            list.add(new ItemMap(zone, 77, rand_ngoc, x, yEnd, player.id));
        }
        //1188: trang giay cũ
        if (Util.isTrue(2, 5000) || (player.isActive() && Util.isTrue(10, 1000))) {
            list.add(new ItemMap(zone, 1188, 1, x, yEnd, player.id));
        }
        //1191: bí kiếp
        if (Util.isTrue(2, 5000) || (player.isActive() && Util.isTrue(10, 2000))) {
            list.add(new ItemMap(zone, 1191, 1, x, yEnd, player.id));
        }
//        if (Util.isTrue(1, 10000) || (player.isActive() && Util.isTrue(1, 2000))) {
//            list.add(new ItemMap(zone, 861, 1, x, yEnd, player.id));
//        }
        //Úp quái bí ngô 1 - 7 sao
//        if (Util.isTrue(1, 100000) || (player.isActive() && Util.isTrue(1, 15000))) {
//            list.add(new ItemMap(zone, 702, 1, x, yEnd, player.id));
//        }
//        if (Util.isTrue(1, 100000) || (player.isActive() && Util.isTrue(1, 13000))) {
//            list.add(new ItemMap(zone, 703, 1, x, yEnd, player.id));
//        }
//        if (Util.isTrue(1, 100000) || (player.isActive() && Util.isTrue(1, 13000))) {
//            list.add(new ItemMap(zone, 704, 1, x, yEnd, player.id));
//        }
//        if (Util.isTrue(1, 100000) || (player.isActive() && Util.isTrue(1, 13000))) {
//            list.add(new ItemMap(zone, 705, 1, x, yEnd, player.id));
//        }
//        if (Util.isTrue(1, 100000) || (player.isActive() && Util.isTrue(1, 12000))) {
//            list.add(new ItemMap(zone, 706, 1, x, yEnd, player.id));
//        }
//        if (Util.isTrue(1, 100000) || (player.isActive() && Util.isTrue(1, 11000))) {
//            list.add(new ItemMap(zone, 707, 1, x, yEnd, player.id));
//        }
//        if (Util.isTrue(1, 100000) || (player.isActive() && Util.isTrue(1, 11000))) {
//            list.add(new ItemMap(zone, 708, 1, x, yEnd, player.id));
//        }
        // Manh thien su
        if ((Util.isTrue(1, 10000) || (player.isActive() && Util.isTrue(10, 3000))) && MapService.gI().isMapHanhTinhThucVat(mapid) && InventoryService.gI().findItemNTK(player)) {
            list.add(new ItemMap(zone, Util.nextInt(1066, 1070), 1, x, yEnd, player.id));
        }
//        if (Util.isTrue(1, 5000) || (player.isActive() && Util.isTrue(1, 1000))) {
//            list.add(new ItemMap(zone, 861, 1, x, yEnd, player.id));
//        }
        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (this.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(this.zone, 73, 1, this.location.x, this.location.y, player.id);
                }
                break;
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(int dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(this.point.gethp());
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinhMobPhoBan() {
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob); //level mob
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinhMobTayKarin() {
        this.point.hp = this.point.maxHp;
        this.maxTiemNang = 1;
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob); //level mob
            msg.writer().writeInt(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendSieuQuai(int type) {
        Message msg;
        try {
            msg = new Message(-75);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(type);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendDisable(boolean bool) {
        Message msg;
        try {
            msg = new Message(81);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendDoneMove(boolean bool) {
        Message msg;
        try {
            msg = new Message(82);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendFire(boolean bool) {
        Message msg;
        try {
            msg = new Message(85);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendIce(boolean bool) {
        Message msg;
        try {
            msg = new Message(86);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendWind(boolean bool) {
        Message msg;
        try {
            msg = new Message(87);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void sendMobMaxHp(int maxHp) {
        Message msg;
        try {
            msg = new Message(87);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(maxHp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    private void phanSatThuong(Player plTarget, long dame) {
        if (plTarget.nPoint == null) {
            return;
        }
        int percentPST = plTarget.nPoint.tlPST;
        if (percentPST != 0) {
            int damePST = (int) (long) (dame * percentPST / 100L);
            Message msg;
            try {
                msg = new Message(-9);
                msg.writer().writeByte(this.id);
                if (damePST >= this.point.hp) {
                    damePST = this.point.hp - 1;
                }
                int hpMob = this.point.hp;
                injured(null, damePST, true);
                damePST = hpMob - this.point.hp;
                msg.writer().writeInt(this.point.hp);
                msg.writer().writeInt(damePST);
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(36);
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                msg.cleanup();
            } catch (IOException e) {
            }
        }
    }

    public void startDie() {
        Message msg;
        try {
            setDie();
            this.point.hp = -1;
            this.status = 0;
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }
}
