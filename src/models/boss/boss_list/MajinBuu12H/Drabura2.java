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
import consts.AppearType;
import static consts.BossType.FINAL;
import java.util.Random;
import models.map.ItemMap;

import models.player.Player;
import services.EffectSkillService;
import utils.Util;

import services.TaskService;
import services.map.ChangeMapService;
import models.skill.Skill;
import server.Manager;
import services.Service;

public class Drabura2 extends Boss {

    private boolean callBoss = true;

    private long lastTimeJoin;

    public Drabura2() throws Exception {
        super(FINAL, BossID.DRABURA_2, BossesData.DRABURA_2);
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            this.zone = zoneFinal;
        }
        this.lastTimeJoin = System.currentTimeMillis();
        this.callBoss = false;
        ChangeMapService.gI().changeMap(this, this.zone, Util.nextInt(300, 400), 336);
        this.changeStatus(BossStatus.CHAT_S);
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

            if (plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.LIEN_HOAN:
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

            if (damage >= this.nPoint.hp) {
                this.changeStatus(BossStatus.AFK);
                damage = 0;
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return (int) damage;
        } else {
            return 0;
        }
    }

    @Override
    public void rest() {
        int nextLevel = this.currentLevel + 1;
        if (nextLevel >= this.data.length) {
            nextLevel = 0;
        }
        if (this.data[nextLevel].getTypeAppear() == AppearType.DEFAULT_APPEAR
                && Util.canDoWithTime(lastTimeRest, secondsRest * 1000)) {
            this.changeStatus(BossStatus.RESPAWN);
        }

        if (Util.canDoWithTime(lastTimeRest, 5000)) {
            if (!this.callBoss) {
                for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                    boss.changeStatus(BossStatus.RESPAWN);
                }
                this.callBoss = true;
            }
        }

    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(this.lastTimeJoin, 250000)) {
            this.leaveMap();
        }
    }

    @Override
    public void afk() {
        this.changeToTypeNonPK();
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }

}
