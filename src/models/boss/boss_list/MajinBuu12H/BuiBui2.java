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
import consts.ConstPlayer;
import java.util.Random;
import models.map.ItemMap;
import models.player.Player;
import services.Service;
import utils.Util;

import server.ServerNotify;
import services.EffectSkillService;
import services.SkillService;
import services.TaskService;
import models.skill.Skill;
import server.Manager;
import utils.SkillUtil;

public class BuiBui2 extends Boss {

    private int indexChat;
    private long lastTimeSlow;

    private long lastTimeAfk;

    private long lastTimeChatAfk;

    private int timeChat;

    public BuiBui2() throws Exception {
        super(FINAL, BossID.BUI_BUI_2, BossesData.BUI_BUI_2);
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
        if (Util.isTrue(50, 100)) {
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

    private void slowPlayerInMap() {
        for (Player pl : this.zone.getNotBosses()) {
            if (Util.isTrue(5, 10)) {
                EffectSkillService.gI().setIsLamCham(pl, 5000);
            }
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            if (Util.canDoWithTime(lastTimeSlow, 10000)) {
                slowPlayerInMap();
                this.lastTimeSlow = System.currentTimeMillis();
            }
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
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
    public void chatM() {
        if (this.typePk == ConstPlayer.NON_PK) {
            return;
        }
        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }
        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }

        if (Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            String textChat = this.data[this.currentLevel].getTextM()[indexChat];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            this.chat(prefix, textChat);
            this.indexChat++;
            if (indexChat == this.data[this.currentLevel].getTextM().length) {
                this.indexChat = 0;
                this.lastTimeChatM = System.currentTimeMillis();
                this.timeChatM = 10000;
            } else {
                this.lastTimeChatM = System.currentTimeMillis();
                this.timeChatM = 3000;
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
//            this.changeToTypePK();
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
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(200, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

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

}
