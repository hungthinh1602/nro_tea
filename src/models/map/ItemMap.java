package models.map;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import consts.ConstItem;
import consts.ConstMap;
import java.util.ArrayList;
import java.util.List;
import models.Template.ItemTemplate;
import models.clan.Clan;
import models.item.Item.ItemOption;
import models.player.Pet;
import models.player.Player;
import utils.Util;
import services.map.ItemMapService;
import services.ItemService;
import services.map.MapService;
import services.player.PlayerService;
import services.Service;

public class ItemMap {

    public Zone zone;
    public int itemMapId;
    public ItemTemplate itemTemplate;
    public int quantity;

    public int x;
    public int y;
    public long playerId;
    public List<ItemOption> options;

    public long createTime;

    public int clanId = -1;

    public boolean isBlackBall;
    public boolean isNamecBall;
    public Clan clan;

    public ItemMap(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        this.zone = zone;
        this.itemMapId = zone.countItemAppeaerd++;
        if (zone.countItemAppeaerd >= 2000000000) {
            zone.countItemAppeaerd = 0;
        }
        this.itemTemplate = ItemService.gI().getTemplate((short) tempId);
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId != -1 ? Math.abs(playerId) : playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
        this.isBlackBall = ItemMapService.gI().isBlackBall(this.itemTemplate.id);
        this.isNamecBall = ItemMapService.gI().isNamecBall(this.itemTemplate.id);
        this.lastTimeMoveToPlayer = System.currentTimeMillis();
        this.zone.addItem(this);
    }

    public ItemMap(Zone zone, ItemTemplate temp, int quantity, int x, int y, long playerId, Clan clan) {
        this.zone = zone;
        this.itemMapId = zone.countItemAppeaerd++;
        if (zone.countItemAppeaerd >= 2000000000) {
            zone.countItemAppeaerd = 0;
        }
        this.itemTemplate = temp;
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId != -1 ? Math.abs(playerId) : playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
        this.isBlackBall = ItemMapService.gI().isBlackBall(this.itemTemplate.id);
        this.isNamecBall = ItemMapService.gI().isNamecBall(this.itemTemplate.id);
        this.lastTimeMoveToPlayer = System.currentTimeMillis();
        this.clan = clan;
        this.zone.addItem(this);

    }

    public ItemMap(Zone zone, ItemTemplate temp, int quantity, int x, int y, long playerId) {
        this.zone = zone;
        this.itemMapId = zone.countItemAppeaerd++;
        if (zone.countItemAppeaerd >= 2000000000) {
            zone.countItemAppeaerd = 0;
        }
        this.itemTemplate = temp;
        this.quantity = quantity;
        this.x = x;
        this.y = y;
        this.playerId = playerId != -1 ? Math.abs(playerId) : playerId;
        this.createTime = System.currentTimeMillis();
        this.options = new ArrayList<>();
        this.isBlackBall = ItemMapService.gI().isBlackBall(this.itemTemplate.id);
        this.isNamecBall = ItemMapService.gI().isNamecBall(this.itemTemplate.id);
        this.lastTimeMoveToPlayer = System.currentTimeMillis();
        this.zone.addItem(this);
    }

    public ItemMap(ItemMap itemMap) {
        this.zone = itemMap.zone;
        this.itemMapId = itemMap.itemMapId;
        this.itemTemplate = itemMap.itemTemplate;
        this.quantity = itemMap.quantity;
        this.x = itemMap.x;
        this.y = itemMap.y;
        this.playerId = itemMap.playerId;
        this.options = itemMap.options;
        this.isBlackBall = itemMap.isBlackBall;
        this.isNamecBall = itemMap.isNamecBall;
        this.lastTimeMoveToPlayer = itemMap.lastTimeMoveToPlayer;
        this.createTime = System.currentTimeMillis();
        this.zone.addItem(this);
    }

    public void update() {
        if (isNotNullItem()) {
            if (this.isBlackBall) {
                if (Util.canDoWithTime(lastTimeMoveToPlayer, timeMoveToPlayer)) {
                    if (this.zone != null && !this.zone.getPlayers().isEmpty()) {
                        Player player = this.zone.getPlayers().get(0);
                        if (player.zone != null && player.zone.equals(this.zone)) {
                            this.x = player.location.x;
                            this.y = this.zone.map.yPhysicInTop(this.x, player.location.y - 24);
                            reAppearItem();
                            this.lastTimeMoveToPlayer = System.currentTimeMillis();
                        }
                    }
                }
                return;
            }

            //========================SATELLITE========================
//            if (this.itemTemplate.type == 22) {
//                satelliteUpdate();
//            }
            if (Util.canDoWithTime(createTime, 45000)) {
                if (this.itemTemplate.type != 22 && this.itemTemplate.id != 726 && this.itemTemplate.id != 992) {
                    this.playerId = -1;
                }
            }
            if ((Util.canDoWithTime(createTime, 50000) && isNotNullItem() && itemTemplate.type != 22 || Util.canDoWithTime(createTime, 180000)) && !this.isNamecBall) {
                if (this.zone != null && this.zone.map.mapId != 21 && this.zone.map.mapId != 22
                        && this.zone.map.mapId != 23 && this.itemTemplate.id != 78
                        && this.itemTemplate.id != 726 && !(MapService.gI().isMapDoanhTrai(this.zone.map.mapId) && this.itemTemplate.id >= 14 && this.itemTemplate.id <= 20)) {
                    ItemMapService.gI().removeItemMapAndSendClient(this);
                }
            }
            //========================DHVT ITEM 726========================
            if (this.zone != null && this.zone.map.mapId == 52 && isNotNullItem() && this.itemTemplate.id == 726) {
                if (!findPlayerByID(this.playerId)) {
                    ItemMapService.gI().removeItemMapAndSendClient(this);
                }
            }
            if (this.zone != null && isNotNullItem() && this.itemTemplate.id == 460 && this.playerId == 123456789 && Util.canDoWithTime(createTime, 5000)) {
                ItemMapService.gI().removeItemMapAndSendClient(this);
            }
        }
    }

    private boolean findPlayerByID(long id) {
        for (Player pl : this.zone.getPlayers()) {
            if (pl.id == id) {
                return true;
            }
        }
        return false;
    }

    private void satelliteUpdate() {
        for (Player pl : this.zone.getPlayers()) {
            if (!pl.isDie() && Util.getDistance(pl.location.x, pl.location.y, x, y) < 200 && pl.satellite != null && (pl.id == this.playerId || this.clanId != -1 && pl.clan != null && pl.clan.id == this.clanId)) {
                switch (this.itemTemplate.id) {
                    case 342 -> {
                        if (!pl.satellite.isMP) {
                            pl.satellite.isMP = true;
                            pl.satellite.lastMPTime = System.currentTimeMillis();
                            if (pl.nPoint.mp < pl.nPoint.mpMax) {
                                pl.nPoint.addMp(pl.nPoint.mpMax / 10);
                                PlayerService.gI().sendInfoMp(pl);
                            }
                        }
                    }
                    case 343 -> {
                        if (!pl.satellite.isIntelligent) {
                            pl.satellite.isIntelligent = true;
                            pl.satellite.lastIntelligentTime = System.currentTimeMillis();
                        }
                    }
                    case 344 -> {
                        if (!pl.satellite.isDefend) {
                            pl.satellite.isDefend = true;
                            pl.satellite.lastDefendTime = System.currentTimeMillis();
                        }
                    }
                    case 345 -> {
                        if (!pl.satellite.isHP) {
                            pl.satellite.isHP = true;
                            pl.satellite.lastHPTime = System.currentTimeMillis();
                            if (pl.nPoint.hp < pl.nPoint.hpMax) {
                                pl.nPoint.addHp(pl.nPoint.hpMax / 10);
                                PlayerService.gI().sendInfoHp(pl);
                                Service.gI().Send_Info_NV(pl);
                            }
                        }
                    }
                }
            }
        }
    }

    private final int timeMoveToPlayer = 10000;
    private long lastTimeMoveToPlayer;

    private void reAppearItem() {
        ItemMapService.gI().sendItemMapDisappear(this);
        Service.gI().dropItemMap(this.zone, this);
    }

    public boolean isNotNullItem() {
        return itemTemplate != null;
    }

    public void dispose() {
        this.zone = null;
        this.itemTemplate = null;
        this.options = null;
    }

    public boolean isBelongToMe(Player player) {
        try {
            if (player == null) {
                return false;
            }
            if (this.playerId == Math.abs(player.id)) { // kiểm tra là sư phụ hoặc đệ tử của nó
                return true;
            }
            if (this.clan != null) {
                if (player.clan != null && this.clan.id == player.clan.id) {
                    return true;
                }
                if (player.isPet) {
                    if (((Pet) player).master.clan != null) {
                        if (((Pet) player).master.clan.id == this.clan.id) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isInVeTinhRange(Player pl, int code, int locationX, int locationY) {
        ItemMap itemMap = null;
        if (pl == null) {
            return false;
        }
        switch (code) {
            case ConstItem.VE_TINH_TRI_LUC:
                itemMap = pl.veTinhTriLuc;
                break;
            case ConstItem.VE_TINH_TRI_TUE:
                itemMap = pl.veTinhTriTue;
                break;
            case ConstItem.VE_TINH_PHONG_THU:
                itemMap = pl.veTinhPhongThu;
                break;
            case ConstItem.VE_TINH_SINH_LUC:
                itemMap = pl.veTinhSinhLuc;
                break;
        }

        if (itemMap == null || itemMap.itemTemplate == null) {
            return false;
        }

        boolean isInRange = Util.myGetDistcance(itemMap.x, itemMap.y, locationX, locationY) <= ConstMap.RANGE_VE_TINH;
        return !pl.isDie() && isInRange;
    }
}
