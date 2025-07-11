package services.map;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import models.map.ItemMap;
import models.player.Player;
import network.io.Message;
import services.Service;
import utils.Logger;
import utils.Util;

public class ItemMapService {

    private static ItemMapService instance;

    public static ItemMapService gI() {
        if (instance == null) {
            instance = new ItemMapService();
        }
        return instance;
    }

   public void pickItem(Player player, int itemMapId, boolean isThuHut) {
        if (player.idMark != null && isThuHut || Util.canDoWithTime(player.idMark.getLastTimePickItem(), 1000)) {
            player.zone.pickItem(player, itemMapId);
            if (player.idMark != null) {
                player.idMark.setLastTimePickItem(System.currentTimeMillis());
            }
        }
    }


    //xóa item map và gửi item map biến mất
    public void removeItemMapAndSendClient(ItemMap itemMap) {
        sendItemMapDisappear(itemMap);
        removeItemMap(itemMap);
    }

    public void sendItemMapDisappear(ItemMap itemMap) {
        Message msg;
        try {
            msg = new Message(-21);
            msg.writer().writeShort(itemMap.itemMapId);
            Service.gI().sendMessAllPlayerInMap(itemMap.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(ItemMapService.class, e);
        }
    }

    public void removeItemMap(ItemMap itemMap) {
        itemMap.zone.removeItemMap(itemMap);
        itemMap.dispose();
    }

    public boolean findItemMapByPlayer(Player player, int tempId) {
        for (ItemMap it : player.zone.items) {
            if (it.playerId == player.id && it.itemTemplate.id == tempId) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlackBall(int tempId) {
        return tempId >= 372 && tempId <= 378;
    }

    public boolean isNamecBall(int tempId) {
        return tempId >= 353 && tempId <= 360;
    }

    public boolean isNamecBallStone(int tempId) {
        return tempId == 362;
    }
}
