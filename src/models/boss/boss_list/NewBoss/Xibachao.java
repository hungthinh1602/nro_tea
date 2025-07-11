/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.boss.boss_list.NewBoss;

import models.boss.Boss;
import models.boss.BossesData;
import models.item.Item;
import models.map.ItemMap;
import models.player.Player;
import services.Service;
import utils.Util;

/**
 *
 * @author Administrator
 */
public class Xibachao extends Boss {

    public Xibachao() throws Exception {
        super(-Util.nextInt(1000, 1000000), true, true, BossesData.XINBATO_NEW);
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
}
