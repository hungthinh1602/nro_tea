package models.player;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import models.item.Item;
import models.map.ItemMap;
import models.map.Zone;
import services.player.InventoryService;
import services.map.ItemMapService;
import services.Service;
import utils.Util;

public class DropItem {

    private Player player;

    public DropItem(Player player) {
        this.player = player;
    }

    public void update() {
        Zone zone = player.zone;
        if (player.isPl() && zone != null && zone.map.mapId == 52
                && InventoryService.gI().getCountEmptyBag(player) > 0
                && !InventoryService.gI().isExistItemBag(player, 726)
                && !ItemMapService.gI().findItemMapByPlayer(player, 726)) {
            int x = Util.nextInt(100, zone.map.mapWidth - 100);
            int y = zone.map.yPhysicInTop(x, 100);
            ItemMap it = new ItemMap(zone, 726, 1, x, y, player.id);
            it.options.add(new Item.ItemOption(30, 0));
            it.options.add(new Item.ItemOption(93, 1));
            Service.gI().dropItemMapForMe(player, it);
        }
    }

    public void dispose() {
        player = null;
    }
}
