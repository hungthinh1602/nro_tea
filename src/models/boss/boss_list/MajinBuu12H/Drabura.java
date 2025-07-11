package models.boss.boss_list.MajinBuu12H;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import models.boss.Boss;
import consts.BossID;
import consts.BossStatus;
import models.boss.BossesData;
import static consts.BossType.FINAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import models.map.ItemMap;

import models.player.Player;
import services.EffectSkillService;
import services.Service;
import utils.Util;

import server.ServerNotify;
import services.SkillService;
import services.TaskService;
import services.map.ChangeMapService;
import models.skill.Skill;
import server.Manager;
import utils.SkillUtil;

public class Drabura extends Boss {

    private long lastTimePetrify;

    private long lastTimeMove;

    private int timeMove;

    private long lastTimeAfk;

    private long lastTimeChatAfk;

    private int timeChat;

    public Drabura() throws Exception {
        super(FINAL, BossID.DRABURA, BossesData.DRABURA);
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            this.zone = zoneFinal;
        }
        ChangeMapService.gI().changeMap(this, this.zone, -1, -1);
        this.changeStatus(BossStatus.ACTIVE);
        Service.gI().changeFlag(this, 10);
    }

    @Override
    public void active() {
        this.attack();
    }

    @Override
    public Player getPlayerAttack() {
        List<Player> plNotVoHinh = new ArrayList();
        for (Player pl : this.zone.getNotBosses()) {
            if ((pl.effectSkin == null || !pl.effectSkin.isVoHinh) && pl.cFlag != this.cFlag) {
                plNotVoHinh.add(pl);
            }
        }
        if (!plNotVoHinh.isEmpty()) {
            return plNotVoHinh.get(Util.nextInt(0, plNotVoHinh.size() - 1));
        }

        return null;
    }

    private void petrifyPlayersInTheMap() {
        for (Player pl : this.zone.getNotBosses()) {
            if (Util.isTrue(1, 10)) {
                this.chat("phẹt");
                EffectSkillService.gI().setIsStone(pl, 22000);
            }
        }
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(lastTimeChatAfk, timeChat)) {
            this.chat("Đừng vội mừng, ta sẽ hồi sinh và thịt hết bọn mi");
            this.lastTimeChatAfk = System.currentTimeMillis();
            this.timeChat = Util.nextInt(10000, 15000);
        }
        if (Util.canDoWithTime(lastTimeAfk, 60000)) {
            Service.gI().hsChar(this, this.nPoint.hpMax, this.nPoint.mpMax);
            this.changeStatus(BossStatus.CHAT_S);
        }
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
            ServerNotify.gI().notify(plKill.name + ": Đã tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ.");
        }
        this.lastTimeAfk = System.currentTimeMillis();
        this.changeStatus(BossStatus.AFK);
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            if (Util.canDoWithTime(lastTimePetrify, 10000)) {
                petrifyPlayersInTheMap();
                this.lastTimePetrify = System.currentTimeMillis();
            }
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    if (Util.canDoWithTime(lastTimeMove, timeMove)) {
                        Player plRand = super.getPlayerAttack();
                        if (plRand != null) {
                            this.moveToPlayer(plRand);
                            this.lastTimeMove = System.currentTimeMillis();
                            this.timeMove = Util.nextInt(1000, 5000);
                        }
                    }
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)), pl.location.y);
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)), pl.location.y);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void reward(Player plKill) {
        plKill.fightMabu.changePoint((byte) 10);
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        int[] itemDos = new int[]{19, 20, 17, 18};
        int randomnro = new Random().nextInt(itemDos.length);
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_Kaio_AWJ.length - 1);
        byte randomDo1 = (byte) new Random().nextInt(Manager.itemIds_Kaio_GN.length - 1);
        byte randomDo2 = (byte) new Random().nextInt(Manager.itemIds_LuongLong_AWJ.length - 1);
        byte randomDo3 = (byte) new Random().nextInt(Manager.itemIds_LuongLong_GN.length - 1);
        if (Util.isTrue(60, 100)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, itemDos[randomnro], 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
        if (Util.isTrue(2, 100)) {
            Service.gI().dropItemMap(this.zone, Util.ratiItemkaio(zone, Manager.itemIds_Kaio_AWJ[randomDo], 1, this.location.x, this.location.y, plKill.id));
        }
        if (Util.isTrue(1, 100)) {
            Service.gI().dropItemMap(this.zone, Util.ratiItemkaio(zone, Manager.itemIds_Kaio_GN[randomDo1], 1, this.location.x, this.location.y, plKill.id));
        }
        if (Util.isTrue(2, 100)) {
            Service.gI().dropItemMap(this.zone, Util.ratiItemluonglong(zone, Manager.itemIds_LuongLong_AWJ[randomDo], 1, this.location.x, this.location.y, plKill.id));
        }
        if (Util.isTrue(1, 100)) {
            Service.gI().dropItemMap(this.zone, Util.ratiItemluonglong(zone, Manager.itemIds_LuongLong_GN[randomDo1], 1, this.location.x, this.location.y, plKill.id));
        }
    }
    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(10, 100)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.TU_SAT:
                        return 0;
                }
            }

            if (plAtt.isPl() && Util.isTrue(1, 5)) {
                plAtt.fightMabu.changePercentPoint((byte) 1);
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }

            if (damage >= 1000000) {
                damage = 1000000;
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                this.lastTimeAfk = System.currentTimeMillis();
                die(plAtt);
            }

            return (int) damage;
        } else {
            return 0;
        }
    }

}
