/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.boss.boss_list.NewBoss;

import java.util.List;
import models.boss.Boss;
import models.boss.BossesData;
import models.item.Item;
import models.map.ItemMap;
import models.player.Player;
import services.Service;
import services.player.PlayerService;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class BaDo extends Boss {

    public BaDo() throws Exception {
        super(-Util.nextInt(1000, 1000000), true, true, BossesData.O_DO_NEW);
    }

    private static final String[] textOdo = new String[]{
        "Hôi quá, tránh xa ta ra", "Biến đi", "Trời ơi đồ ở dơ",
        "Thúi quá", "Mùi gì hôi quá"
    };
    private long lastTimeOdo;

    public void subHpWithOdo() {
        try {
            if (this.nPoint != null) {
                if (Util.canDoWithTime(lastTimeOdo, 15000)) {
                    for (int i = this.zone.getNotBosses().size() - 1; i >= 0; i--) {
                        Player pl = this.zone.getNotBosses().get(i);
                        if (pl != null && pl.nPoint != null && !pl.isDie()) {
                            int subHp = (int) ((long) pl.nPoint.hpMax * 20 / 100);
                            if (subHp >= pl.nPoint.hp) {
                                subHp = pl.nPoint.hp - 1;
                            }
                            Service.gI().chat(pl, textOdo[Util.nextInt(0, textOdo.length - 1)]);
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().Send_Info_NV(pl);
                            pl.injured(null, subHp, true, false);
                        }
                    }
                    this.lastTimeOdo = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void update() {
        super.update();
        subHpWithOdo();
    }

    @Override
    public void reward(Player plKill) {
        int[] itemne = {441, 442, 443, 444, 445, 446, 447, 459};
        Service.gI().dropItemMap(this.zone, Util.saoPhaLe(zone, Util.isTrue(95, 100) ? itemne[Util.nextInt(itemne.length - 1)] : itemne[itemne.length - 1], 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y), -1));
        if (Util.isTrue(1, 5)) {
        ItemMap item = new ItemMap(zone, 927, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), -1);
        item.options.add(new Item.ItemOption(87, 0));
        }
        if (Util.isTrue(1, 10)) {
        ItemMap item2 = new ItemMap(zone, 926, 1, this.location.x + Util.nextInt(50), this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), -1);
        item2.options.add(new Item.ItemOption(87, 0));
        }
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            damage = Util.nextInt(300, 500);
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
