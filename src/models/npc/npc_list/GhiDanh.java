package models.npc.npc_list;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import consts.ConstNpc;
import models.item.Item;
import services.tournament.The23rdMartialArtCongressService;
import services.tournament.WorldMartialArtsTournamentService;
import models.npc.Npc;
import models.player.Player;
import services.player.InventoryService;
import services.ItemService;
import services.map.NpcService;
import services.player.PlayerService;
import services.Service;
import services.map.ChangeMapService;
import utils.Util;

public class GhiDanh extends Npc {

    String[] menuselect = new String[]{};

    public GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player pl) {
        if (canOpenNpc(pl)) {
            switch (this.mapId) {
                case 52 ->
                    WorldMartialArtsTournamentService.menu(this, pl);
                case 129 -> {
                    if (Util.isAfterMidnight(pl.lastTimePKDHVT23)) {
                        pl.goldChallenge = 10_000_000;
                        pl.rubyChallenge = 10;
                        pl.levelWoodChest = 0;
                    }
                    long goldchallenge = pl.goldChallenge;
                    long gemchallenge = pl.rubyChallenge;
                    // Hướng dẫn thêm - Hủy\nđăng kí - về dhvt
                    if (pl.levelWoodChest == 0) {
                        menuselect = new String[]{"Hướng\ndẫn\nthêm", "Thi đấu\n" + Util.numberToMoney(gemchallenge) + " Ngọc xanh", "Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng", "Về\nĐại Hội\nVõ Thuật"};
                    } else {
                        menuselect = new String[]{"Hướng\ndẫn\nthêm", "Thi đấu\n" + Util.numberToMoney(gemchallenge) + " Ngọc xanh", "Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng", "Nhận\nthưởng\nRương Cấp\n" + pl.levelWoodChest, "Về\nĐại Hội\nVõ Thuật"};
                    }
                    this.createOtherMenu(pl, ConstNpc.BASE_MENU, "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm, ngày nghỉ, ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào", menuselect, "Từ chối");
                }
                default ->
                    super.openBaseMenu(pl);
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 52) {
                WorldMartialArtsTournamentService.confirm(this, player, select);
            } else if (this.mapId == 129) {
                switch (player.idMark.getIndexMenu()) {
                    case ConstNpc.BASE_MENU -> {
                        long goldchallenge = player.goldChallenge;
                        long gemchallenge = player.rubyChallenge;
                        if (player.levelWoodChest == 0) {
                            switch (select) {
                                case 0 ->
                                    NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.NPC_DHVT23);
                                case 1, 2 -> {
                                    if (player.levelWoodChest != 12) {
                                        if (InventoryService.gI().finditemWoodChest(player)) {
                                            if (select == 1) {
                                                if (player.inventory.gem >= gemchallenge) {
                                                    The23rdMartialArtCongressService.gI().startChallenge(player);
                                                    player.inventory.gem -= (gemchallenge);
                                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                                    player.goldChallenge += 10000000;
                                                    player.rubyChallenge += 10;
                                                } else {
                                                    Service.gI().sendThongBao(player, "Bạn không đủ Ngọc xanh, còn thiếu " + Util.numberToMoney(gemchallenge - player.inventory.gem) + " Ngọc xanh nữa");
                                                }
                                            } else {
                                                if (player.inventory.gold >= goldchallenge) {
                                                    The23rdMartialArtCongressService.gI().startChallenge(player);
                                                    player.inventory.gold -= (goldchallenge);
                                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                                    player.goldChallenge += 10000000;
                                                    player.rubyChallenge += 10;
                                                } else {
                                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(goldchallenge - player.inventory.gold) + " vàng nữa");
                                                }
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Hãy mở rương báu vật trước");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn đã vô địch giải. Vui lòng chờ đến ngày mai");
                                    }
                                }
                                case 3 ->
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                            }
                        } else {
                            switch (select) {
                                case 0 ->
                                    NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.NPC_DHVT23);
                                case 1, 2 -> {
                                    if (player.levelWoodChest != 12) {
                                        if (InventoryService.gI().finditemWoodChest(player)) {
                                            if (select == 1) {
                                                if (player.inventory.gem >= gemchallenge) {
                                                    The23rdMartialArtCongressService.gI().startChallenge(player);
                                                    player.inventory.gem -= (gemchallenge);
                                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                                    player.goldChallenge += 10000000;
                                                    player.rubyChallenge += 10;
                                                } else {
                                                    Service.gI().sendThongBao(player, "Bạn không đủ Ngọc xanh, còn thiếu " + Util.numberToMoney(gemchallenge - player.inventory.gem) + " Ngọc xanh nữa");
                                                }
                                            } else {
                                                if (player.inventory.gold >= goldchallenge) {
                                                    The23rdMartialArtCongressService.gI().startChallenge(player);
                                                    player.inventory.gold -= (goldchallenge);
                                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                                    player.goldChallenge += 10000000;
                                                    player.rubyChallenge += 10;
                                                } else {
                                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(goldchallenge - player.inventory.gold) + " vàng nữa");
                                                }
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Hãy mở rương báu vật trước");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn đã vô địch giải. Vui lòng chờ đến ngày mai");
                                    }
                                }
                                case 3 ->
                                    this.createOtherMenu(player, 1, "Phần thưởng của bạn đang ở cấp " + player.levelWoodChest + " / 12\n"
                                            + "Mỗi ngày chỉ được nhận được nhận thưởng 1 lần\n"
                                            + "bạn có chắc sẽ nhận phần thưởng ngay bây giờ?", "OK", "Từ chối");
                                case 4 ->
                                    ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                            }
                        }
                    }
                    case 1 -> {
                        if (select == 0) {
                            if (InventoryService.gI().finditemWoodChest(player)) {
                                if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                    Item it = ItemService.gI().createNewItem((short) 571);
                                    it.itemOptions.add(new Item.ItemOption(72, player.levelWoodChest));
                                    it.itemOptions.add(new Item.ItemOption(30, 0));
                                    it.createTime = System.currentTimeMillis();
                                    InventoryService.gI().addItemBag(player, it);
                                    InventoryService.gI().sendItemBags(player);
                                    player.levelWoodChest = 0;
                                    player.lastTimeRewardWoodChest = System.currentTimeMillis();
                                    NpcService.gI().createMenuConMeo(player, -1, -1, "Bạn nhận được\n|1|Rương Bạc\n|2|Giấu bên trong nhiều vật phẩm quý giá", "OK");
                                } else {
                                    this.npcChat(player, "Hành trang đã đầy, cần một ô trống trong hành trang để nhận vật phẩm");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Hãy mở rương báu vật trước");
                            }
                        }
                    }
                }
            }
        }
    }
}
