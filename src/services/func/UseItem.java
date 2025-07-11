package services.func;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import consts.ConstItem;
import services.CombineService;
import services.ShenronEventService;
import models.radar.Card;
import services.RadarService;
import models.radar.RadarCard;
import consts.ConstMap;
import models.item.Item;
import consts.ConstNpc;
import consts.ConstPlayer;
import database.daos.PlayerDAO;
import java.util.Date;
import models.boss.Boss;
import models.item.Item.ItemOption;
import models.map.ItemMap;
import models.map.Zone;
import models.map.vetinh.Satellite;
import models.map.vetinh.SatelliteDef;
import models.map.vetinh.SatelliteExp;
import models.map.vetinh.SatelliteHp;
import models.map.vetinh.SatelliteMp;
import models.npc.specialnpc.MabuEgg;
import models.player.Inventory;
import services.map.NpcService;
import models.player.Player;
import models.skill.Skill;
import network.io.Message;
import services.map.ChangeMapService;
import utils.SkillUtil;
import services.Service;
import utils.Util;
import network.session.MySession;
import services.ItemService;
import services.ItemTimeService;
import services.PetService;
import services.RewardService;
import services.player.PlayerService;
import services.TaskService;
import services.player.InventoryService;
import services.map.MapService;
import services.dungeon.NgocRongNamecService;
import utils.Logger;
import utils.TimeUtil;

public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static UseItem instance;

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(MySession session, Message msg) {
        Player player = session.player;
        if (player == null) {
            return;
        }
        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            if (index == -1) {
                return;
            }
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryService.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryService.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryService.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryService.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryService.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryService.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryService.gI().itemPetBodyToBag(player, index);
                    break;
            }
            if (player.setClothes != null) {
                player.setClothes.setup();
            }
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }
            player.setClanMember();
            Service.gI().sendFlagBag(player);
            Service.gI().point(player);
            Service.gI().sendSpeedPlayer(player, -1);
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);

        }
    }

    public Item finditem(Player player, int iditem) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == iditem) {
                return item;
            }
        }
        return null;
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg = null;
        byte type;
        try {
            type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            if (index < 0) {
                                return;
                            }
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học " + player.inventory.itemsBag.get(index).template.name + "?");
                                    player.sendMessage(msg);
                                } else if (item.template.id == 570) {
                                    if (!Util.isAfterMidnight(player.lastTimeRewardWoodChest)) {
                                        Service.gI().sendThongBao(player, "Hãy chờ đến ngày mai");
                                        return;
                                    }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn mở\n" + player.inventory.itemsBag.get(index).template.name + " ?");
                                    player.sendMessage(msg);
                                } else if (item.template.type == 22) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn dùng\n" + player.inventory.itemsBag.get(index).template.name + " ?");
                                    player.sendMessage(msg);
                                } else {
                                    UseItem.gI().useItem(player, item, index);
                                }
                            }
                        } else {
                            int iditem = _msg.reader().readShort();
                            Item item = finditem(player, iditem);
                            UseItem.gI().useItem(player, item, index);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        Item item = null;
                        if (index < 0) {
                            return;
                        }
                        if (where == 0) {
                            item = player.inventory.itemsBody.get(index);
                        } else {
                            item = player.inventory.itemsBag.get(index);
                        }

                        if (item.isNotNullItem() && item.template.id == 673) {
                            Service.gI().sendThongBao(player, "Không thể bỏ ra vật phẩm này . Cay chưa ?");
                            return;
                        }
                        if (item.isNotNullItem() && item.template.id == 570) {
                            Service.gI().sendThongBao(player, "Không thể bỏ vật phẩm này.");
                            return;
                        }
                        if (!item.isNotNullItem()) {
                            return;
                        }
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                        player.sendMessage(msg);
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    InventoryService.gI().throwItem(player, where, index);
                    Service.gI().point(player);
                    InventoryService.gI().sendItemBags(player);
                    break;
                case ACCEPT_USE_ITEM:
                    if (index >= 0 && index < player.inventory.itemsBag.size()) {
                        Item item = player.inventory.itemsBag.get(index);
                        if (item.isNotNullItem()) {
                            useItem(player, item, index);
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void useSatellite(Player player, Item item) {
        Satellite satellite = null;
        if (player.zone != null) {
            if (player.zone.getSatellites().size() < 5) {
                switch (item.template.id) {
                    case 342 ->
                        satellite = new SatelliteMp(player.zone, 342, player.location.x, player.zone.map.yPhysicInTop(player.location.x, player.location.y), player);
                    case 343 ->
                        satellite = new SatelliteExp(player.zone, 343, player.location.x, player.zone.map.yPhysicInTop(player.location.x, player.location.y), player);
                    case 344 ->
                        satellite = new SatelliteDef(player.zone, 344, player.location.x, player.zone.map.yPhysicInTop(player.location.x, player.location.y), player);
                    case 345 ->
                        satellite = new SatelliteHp(player.zone, 345, player.location.x, player.zone.map.yPhysicInTop(player.location.x, player.location.y), player);
                }
                if (satellite != null) {
                    InventoryService.gI().subQuantityItemsBag(player, item, 1);
                    satellite.sendVeTinh();
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Đã đạt tối đa số lượng vệ tinh có thể đặt trong khu!");
            }
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 570) {
                if (!Util.isAfterMidnight(pl.lastTimeRewardWoodChest)) {
                    Service.gI().sendThongBao(pl, "Hãy chờ đến ngày mai");
                } else {
                    openWoodChest(pl, item);
                }
                return;
            }
            if (item.template.strRequire <= pl.nPoint.power) {
                switch (item.template.type) {
                    case 33: //card
                        UseCard(pl, item);
                        break;

                    case 6: //đậu thần
                        this.eatPea(pl);
                        break;
                    case 7:
                        learnSkill(pl, item);
                        break;
                    case 12: //ngọc rồng các loại
                        controllerCallRongThan(pl, item);
                        break;
                    case 22:
                        useSatellite(pl, item);
                        break;
                    case 23: //thú cưỡi mới
                    case 24: //thú cưỡi cũ
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        break;
                    case 11: //item bag
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendFlagBag(pl);
                        break;
                    case 72:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendPetFollow(pl, (short) (item.template.iconID - 1));
                        break;
                    case 36:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendEffPlayer(pl);
                        break;
                    case 35:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        break;
                    case 98:
                        InventoryService.gI().itemBagToBody(pl, indexBag);
                        Service.gI().sendEffPlayer(pl);
                        break;
                    default:
                        switch (item.template.id) {
                            case 456:
                                Boss boss = (Boss) pl.zone.getBosses().stream().filter(b -> b.name.equals("Xinbatô")).findAny().orElse(null);
                                if (item.quantity >= 99 && boss != null) {
                                    int[] itemne = {441, 442, 443, 444, 445, 446, 447, 459};
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 99);
                                    InventoryService.gI().addItemBag(pl, Util.saoPhaLe(Util.isTrue(70, 100) ? itemne[Util.nextInt(itemne.length - 1)] : itemne[itemne.length - 1], 1));
                                    InventoryService.gI().sendItemBags(pl);
                                    Service.gI().chat(boss, "Ngước ngon quá, bắn tùm lum");
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                    }
                                    Zone newZone = Util.randomAllMap();
                                    ChangeMapService.gI().changeMapYardrat(boss, newZone, newZone.map.mapWidth + Util.nextInt(-50, 50), newZone.map.yPhysicInTop(newZone.map.mapWidth, newZone.map.mapHeight));
                                } else {
                                    Service.gI().sendThongBao(pl, "Hãy sử dụng khi gặp Xinbato.");
                                }
                                break;
                            case 460:
                                if (pl.zone.getBosses().stream().filter(b -> b.name.equals("Sói hẹc quyn")).findAny().orElse(null) != null) {
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                    InventoryService.gI().sendItemBags(pl);
                                    Service.gI().dropItemMap(pl.zone, new ItemMap(pl.zone, 460, 1, pl.location.x, pl.location.y, pl.id));
                                } else {
                                    Service.gI().sendThongBao(pl, "Không thể vứt cục xương, hãy đến nơi có Sói hẹc quyn.");
                                }
                                break;
                            case 568:
                                if (pl.mabuEgg == null) {
                                    MabuEgg.createMabuEgg(pl);
                                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                    InventoryService.gI().sendItemBags(pl);
                                } else {
                                    Service.gI().sendThongBao(pl, "Con đang có trứng Mabu chưa mở ở nhà rồi.");
                                }
                                break;
                            case 992: // Nhan thoi khong
                                pl.type = 2;
                                pl.maxTime = 5;
                                Service.gI().Transport(pl);
                                break;
                            case 361:
                                pl.idGo = (short) Util.nextInt(0, 6);
                                NgocRongNamecService.gI().menuCheckTeleNamekBall(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                                InventoryService.gI().sendItemBags(pl);
                                break;
                            case 1128:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1404, 1405, 1406);
                                Service.gI().point(pl);
                                break;
                            case 1129:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1407, 1408, 1409);
                                Service.gI().point(pl);
                                break;
                            case 1130:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1410, 1411, 1412);
                                Service.gI().point(pl);
                                break;
                            case 1131:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1413, 1414, 1415);
                                Service.gI().point(pl);
                                break;
                            case 1132:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1416, 1417, 1418);
                                Service.gI().point(pl);
                                break;
                            case 1133:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1419, 1420, 1421);
                                Service.gI().point(pl);
                                break;
                            case 1146:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1435, 1436, 1437);
                                Service.gI().point(pl);
                                break;
                            case 1145:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1438, 1439, 1440);
                                Service.gI().point(pl);
                                break;
                            case 1144:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1441, 1442, 1443);
                                Service.gI().point(pl);
                                break;
                            case 1148:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1444, 1445, 1446);
                                Service.gI().point(pl);
                                break;
                            case 1222:
                                InventoryService.gI().itemBagToBody(pl, indexBag);
                                PetService.Pet2(pl, 1485, 1486, 1487);
                                Service.gI().point(pl);
                                break;
                            case 211: //nho tím
                            case 212: //nho xanh
                                eatGrapes(pl, item);
                                break;
                            case 380: //cskb
                                openCSKB(pl, item);
                                break;
                            case 381: //cuồng nộ
                            case 382: //bổ huyết
                            case 383: //bổ khí
                            case 384: //giáp xên
                            case 385: //ẩn danh
                            case 379: //máy dò capsule
                            case 638: //commeson
                            case 2075: //rocket
                            case 2160: //Nồi cơm điện
                            case 579:
                            case 1045: //đuôi khỉ
                            case 663: //bánh pudding
                            case 664: //xúc xíc
                            case 665: //kem dâu
                            case 666: //mì ly
                            case 667: //sushi
                            case 1099:
                            case 1100:
                            case 1101:
                            case 1102:
                            case 1103:
                            case 764:
                            case 1136:
                                useItemTime(pl, item);
                                break;
                            case 880:
                            case 881:
                            case 882:
                                if (pl.itemTime.isEatMeal2) {
                                    Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
                                    break;
                                }
                                useItemTime(pl, item);
                                break;
                            case 570:
                                openWoodChest(pl, item);
                                break;
                            case 521: //tdlt
                                useTDLT(pl, item);
                                break;
                            case 454: //bông tai
                                UseItem.gI().usePorata(pl);
                                break;
                            //   case 1149:
                            //         UseItem.gI().ItemSKH(pl, item);
                            //         break;
                            //   case 1150:
                            //        UseItem.gI().ItemSKH(pl, item);
                            //       break;
                            //    case 1151:
                            //       UseItem.gI().ItemSKH(pl, item);
                            //       break;
                            case 921: //bông tai
                                UseItem.gI().usePorata2(pl);
                                break;
                            case 193: //gói 10 viên capsule
                                openCapsuleUI(pl);
                                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            case 194: //capsule đặc biệt
                                openCapsuleUI(pl);
                                break;
                            case 401: //đổi đệ tử
                                changePet(pl, item);
                                break;
                            case 402: //sách nâng chiêu 1 đệ tử
                            case 403: //sách nâng chiêu 2 đệ tử
                            case 404: //sách nâng chiêu 3 đệ tử
                            case 759: //sách nâng chiêu 4 đệ tử
                                upSkillPet(pl, item);
                                break;
                            case 726:
                                UseItem.gI().ItemManhGiay(pl, item);
                                break;
                            case 727:
                            case 728:
                                UseItem.gI().ItemSieuThanThuy(pl, item);
                                break;
                            case 648:
                                ItemService.gI().OpenItem648(pl, item);
                                break;
                            case 1143:
                                ItemService.gI().OpenItem1143(pl, item);
                                break;
                            case 1198:
                                ItemService.gI().OpenItem1198(pl, item);
                                break;
                            case 1199:
                                ItemService.gI().OpenItem1199(pl, item);
                                break;
                            case 736:
                                ItemService.gI().OpenItem736(pl, item);
                                break;
                            case 987:
                                Service.gI().sendThongBao(pl, "Bảo vệ trang bị không bị rớt cấp"); //đá bảo vệ
                                break;
                            case 2006:
                                Input.gI().createFormChangeNameByItem(pl);
                                break;
                            case 1156, 1157:
                                ItemService.gI().OpenCapsuleCaiTrang(pl, item);
                                break;
                            case 1190:
                                Input.gI().createFormTangRuby(pl);
                                break;
                            case 1189:
                                Input.gI().createFormTangGem(pl);
                                break;
                        }
                        break;
                }
                TaskService.gI().checkDoneTaskUseItem(pl, item);
                InventoryService.gI().sendItemBags(pl);
            } else {
                Service.gI().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
            }
        }
    }

    private int randClothes(int level) {
        return ConstItem.LIST_ITEM_CLOTHES[Util.nextInt(0, 2)][Util.nextInt(0, 4)][level - 1];
    }

    private void openWoodChest(Player pl, Item item) {
        int time = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
        if (time != 0) {
            Item itemReward;
            int param = item.itemOptions.get(0).param;
            int gold;
            int[] listItem = {441, 442, 443, 444, 445, 446, 447, 457, 457, 457, 223, 224, 225};
            int[] listClothesReward;
            int[] listItemReward;
            String text = "Bạn nhận được\n";
            if (param < 8) {
                gold = 100000000 * param;
                listClothesReward = new int[]{randClothes(param)};
                listItemReward = Util.pickNRandInArr(listItem, 3);
            } else if (param < 10) {
                gold = 120000000 * param;
                listClothesReward = new int[]{randClothes(param), randClothes(param)};
                listItemReward = Util.pickNRandInArr(listItem, 4);
            } else {
                gold = 150000000 * param;
                listClothesReward = new int[]{randClothes(param), randClothes(param), randClothes(param)};
                listItemReward = Util.pickNRandInArr(listItem, 5);
                int ruby = Util.nextInt(1, 5);
                pl.inventory.ruby += ruby;
                pl.textRuongGo.add(text + "|1| " + ruby + " Hồng Ngọc");
            }
            for (var i : listClothesReward) {
                itemReward = ItemService.gI().createNewItem((short) i);
                RewardService.gI().initBaseOptionClothes(itemReward.template.id, itemReward.template.type, itemReward.itemOptions);
                RewardService.gI().initStarOption(itemReward, new RewardService.RatioStar[]{new RewardService.RatioStar((byte) 1, 1, 2), new RewardService.RatioStar((byte) 2, 1, 3), new RewardService.RatioStar((byte) 3, 1, 4), new RewardService.RatioStar((byte) 4, 1, 5),});
                InventoryService.gI().addItemBag(pl, itemReward);
                pl.textRuongGo.add(text + itemReward.getInfoItem());
            }
            for (var i : listItemReward) {
                itemReward = ItemService.gI().createNewItem((short) i);
                RewardService.gI().initBaseOptionSaoPhaLe(itemReward);
                itemReward.quantity = Util.nextInt(1, 5);
                InventoryService.gI().addItemBag(pl, itemReward);
                pl.textRuongGo.add(text + itemReward.getInfoItem());
            }
            if (param == 11) {
                itemReward = ItemService.gI().createNewItem((short) ConstItem.MANH_NHAN);
                itemReward.quantity = Util.nextInt(1, 3);
                InventoryService.gI().addItemBag(pl, itemReward);
                pl.textRuongGo.add(text + itemReward.getInfoItem());
            }
            NpcService.gI().createMenuConMeo(pl, ConstNpc.RUONG_GO, -1, "Bạn nhận được\n|1|+" + Util.numberToMoney(gold) + " vàng", "OK [" + pl.textRuongGo.size() + "]");
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            pl.inventory.addGold(gold);
            InventoryService.gI().sendItemBags(pl);
            PlayerService.gI().sendInfoHpMpMoney(pl);
        } else {
            Service.gI().sendThongBao(pl, "Vì bạn quên không lấy chìa nên cần đợi 24h để bẻ khóa");
        }
    }

    public void openRuongGo(Player player) {
        Item ruongGo = InventoryService.gI().findItemBag(player, 570);
        if (ruongGo != null) {
            int level = InventoryService.gI().getParam(player, 72, 570);
            if (InventoryService.gI().getCountEmptyBag(player) < level) {
                Service.gI().sendThongBao(player, "Cần ít nhất " + (level - InventoryService.gI().getCountEmptyBag(player)) + " ô trống trong hành trang");
            } else {
                player.itemsWoodChest.clear();
                if (level == 0) {
                    InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
                    InventoryService.gI().sendItemBags(player);
                    //   Item item = ItemService.gI().createNewItem((short) 673);
                    //   InventoryService.gI().addItemBag(player, item);
                    InventoryService.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn vừa nhận được cái nịt nè :)) Đừng cay");
                    return;
                }
                int rand = Util.nextInt(0, 6);
                Item level1 = ItemService.gI().createNewItem(((short) (441 + rand)));
                level1.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
                level1.quantity = Util.nextInt(1, level * 2);
                player.itemsWoodChest.add(level1);
                if (level > 1) {
                    rand = Util.nextInt(0, 4);
                    Item level2 = ItemService.gI().createNewItem(((short) (220 + rand)));
                    level2.itemOptions.add(new Item.ItemOption(71 - rand, 0));
                    level2.quantity = Util.nextInt(1, level * 3);
                    player.itemsWoodChest.add(level2);
                }
//                if (level > 2) {
//                    Item level3 = ItemService.gI().createNewItem((short) 20); // 7 sao
//                    level3.quantity = Util.nextInt(1, level);
//                    player.itemsWoodChest.add(level3);
//                }
//                if (level > 3) {
//                    Item level4 = ItemService.gI().createNewItem((short) 19); // 6 sao
//                    level4.quantity = Util.nextInt(1, level);
//                    player.itemsWoodChest.add(level4);
//                }
//                if (level > 4) {
//                    Item level5 = ItemService.gI().createNewItem((short) 18); // 5 sao
//                    level5.quantity = Util.nextInt(1, level);
//                    player.itemsWoodChest.add(level5);
//                }
//                if (level > 5) {
//                    Item level6 = ItemService.gI().createNewItem((short) 18); // 5 sao
//                    level6.quantity = Util.nextInt(1, level);
//                    player.itemsWoodChest.add(level6);
//                }
//                if (level > 6) {
//                    Item level7 = ItemService.gI().createNewItem((short) 17); // 4 sao
//                    level7.quantity = Util.nextInt(1, level);
//                    player.itemsWoodChest.add(level7);
//                }
//                if (level > 11) {
//                    Item level8 = ItemService.gI().createNewItem((short) 17); // 4SAO
//                    level8.quantity = Util.nextInt(1, level);
//                    player.itemsWoodChest.add(level8);
//                }
//                if (level > 8) {
////                    Item level9 = ItemService.gI().createNewItem((short) 2055); // bi kiep tuyet ky
//              //      level9.quantity = Util.nextInt(15, level + 15);
//            //        player.itemsWoodChest.add(level9);
//                }
//                if (level > 9) {
//                    int[] itemId = {2025, 2026, 2036, 2037, 2038, 2039, 2040, 2019, 2020, 2021, 2022, 2023, 2024};
//                    byte[] option = {77, 80, 81, 103, 50, 94, 5};
//                    byte[] option_v2 = {14, 16, 17, 19, 27, 28, 5, 47, 87}; //77 %hp // 80 //81 //103 //50 //94 //5 % sdcm
//                    byte optionid;
//                    byte optionid_v2;
//                    byte param;
//                    Item level10 = ItemService.gI().createNewItem((short) itemId[Util.nextInt(0, 12)]);
//                    level10.itemOptions.clear();
//                    optionid = option[Util.nextInt(0, 6)];
//                    param = (byte) Util.nextInt(5, 10);
//                    level10.itemOptions.add(new Item.ItemOption(optionid, param));
//                    if (Util.isTrue(20, 100)) {
//                        optionid_v2 = option_v2[Util.nextInt(option_v2.length)];
//                        level10.itemOptions.add(new Item.ItemOption(optionid_v2, param));
//                    }
//                    level10.itemOptions.add(new Item.ItemOption(30, 0));
//                    level10.itemOptions.add(new Item.ItemOption(93, Util.nextInt(level, 30)));
//                    level10.quantity = 1;
//                    player.itemsWoodChest.add(level10);
//                }
//                if (level > 10) {
//                    byte[] option = {77, 80, 81, 103, 50, 94, 5};
//                    byte[] option_v2 = {14, 16, 17, 19, 27, 28, 5, 47, 87}; //77 %hp // 80 //81 //103 //50 //94 //5 % sdcm
//                    byte optionid;
//                    byte optionid_v2;
//                    byte param;
//                    Item level11 = ItemService.gI().createNewItem((short) Util.nextInt(2112, 2118));
//                    level11.itemOptions.clear();
//                    optionid = option[Util.nextInt(0, 6)];
//                    param = (byte) Util.nextInt(5, 10);
//                    level11.itemOptions.add(new Item.ItemOption(optionid, param));
//                    if (Util.isTrue(20, 100)) {
//                        optionid_v2 = option_v2[Util.nextInt(option_v2.length)];
//                        level11.itemOptions.add(new Item.ItemOption(optionid_v2, param));
//                    }
//                    level11.itemOptions.add(new Item.ItemOption(30, 0));
//                    if (Util.isTrue(90, 100)) {
//                        level11.itemOptions.add(new Item.ItemOption(93, Util.nextInt(level, 30)));
//                    }
//                    level11.quantity = 1;
//                    player.itemsWoodChest.add(level11);
//                }
//                if (level > 11) {
//                    byte[] option = {77, 80, 81, 103, 50, 94, 5};
//                    byte[] option_v2 = {14, 16, 17, 19, 27, 28, 5, 47, 87}; //77 %hp // 80 //81 //103 //50 //94 //5 % sdcm
//                    byte optionid;
//                    byte optionid_v2;
//                    byte param;
//                    Item level12 = ItemService.gI().createNewItem((short) Util.nextInt(2108, 2122));
//                    level12.itemOptions.clear();
//                    optionid = option[Util.nextInt(0, 6)];
//                    param = (byte) Util.nextInt(5, 10);
//                    level12.itemOptions.add(new Item.ItemOption(optionid, param));
//                    if (Util.isTrue(20, 100)) {
//                        optionid_v2 = option_v2[Util.nextInt(option_v2.length)];
//                        level12.itemOptions.add(new Item.ItemOption(optionid_v2, param));
//                    }
//                    level12.itemOptions.add(new Item.ItemOption(30, 0));
//                    if (Util.isTrue(50, 100)) {
//                        level12.itemOptions.add(new Item.ItemOption(93, Util.nextInt(level, 30)));
//                    }
//                    level12.quantity = 1;
//                    player.itemsWoodChest.add(level12);
//                }
//                InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
//                InventoryService.gI().sendItemBags(player);
//                for (Item it : player.itemsWoodChest) {
//                    InventoryService.gI().addItemBag(player, it);
//                }
//                InventoryService.gI().sendItemBags(player);
//                player.indexWoodChest = player.itemsWoodChest.size() - 1;
//                int i = player.indexWoodChest;
//                if (i < 0) {
//                    return;
//                }
//                Item itemWoodChest = player.itemsWoodChest.get(i);
//                player.indexWoodChest--;
//                String info = "|1|" + itemWoodChest.template.name;
//                String info2 = "\n|2|";
//                if (!itemWoodChest.itemOptions.isEmpty()) {
//                    for (Item.ItemOption io : itemWoodChest.itemOptions) {
//                        if (io.optionTemplate.id != 102 && io.optionTemplate.id != 73) {
//                            info2 += io.getOptionString() + "\n";
//                        }
//                    }
//                }
//                info = (info2.length() > "\n|2|".length() ? (info + info2).trim() : info.trim()) + "\n|0|" + itemWoodChest.template.description;
//                NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1, "Bạn nhận được\n"
//                        + info.trim(), "OK" + (i > 0 ? " [" + i + "]" : ""));
            }
        }
    }

    private void changePet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender + 1;
            if (gender > 2) {
                gender = 0;
            }
            PetService.gI().changeNormalPet(player, gender);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.gI().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

    private void openCSKB(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {76, 188, 189, 190, 381, 382, 383, 384, 385};
            int[][] gold = {{5000, 20000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryService.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
            case 382: //bổ huyết
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                break;
            case 383: //bổ khí
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                break;
            case 384: //giáp xên
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                break;
            case 381: //cuồng nộ
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                Service.gI().point(pl);
                break;
            case 385: //ẩn danh
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 379: //máy dò capsule
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 1099:// cn
                pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo2 = true;
                Service.gI().point(pl);

                break;
            case 1100:// bo huyet
                pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet2 = true;
                break;
            case 764:
                pl.itemTime.lastTimeKhauTrang = System.currentTimeMillis();
                pl.itemTime.isKhauTrang = true;
                break;
            case 1136:
                pl.itemTime.lastTimeTnDeTu = System.currentTimeMillis();
                pl.itemTime.isTnDeTu = true;
                break;
            case 1101://bo khi
                pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi2 = true;
                break;
            case 1102://gx
                pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen2 = true;
                break;
            case 1103://an danh
                pl.itemTime.lastTimeAnDanh2 = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh2 = true;
                break;
            case 638: //Commeson
                pl.itemTime.lastTimeUseCMS = System.currentTimeMillis();
                pl.itemTime.isUseCMS = true;
                break;
            case 2160: //Nồi cơm điện
                pl.itemTime.lastTimeUseNCD = System.currentTimeMillis();
                pl.itemTime.isUseNCD = true;
                break;
            case 579:
            case 1045: // Đuôi khỉ
                pl.itemTime.lastTimeUseDK = System.currentTimeMillis();
                pl.itemTime.isUseDK = true;
                break;
            case 663: //bánh pudding
            case 664: //xúc xíc
            case 665: //kem dâu
            case 666: //mì ly
            case 667: //sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
            case 880:
            case 881:
            case 882:
                pl.itemTime.lastTimeEatMeal2 = System.currentTimeMillis();
                pl.itemTime.isEatMeal2 = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal2);
                pl.itemTime.iconMeal2 = item.template.iconID;
                break;
            case 1109: //máy dò đồ
                pl.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis();
                pl.itemTime.isUseMayDo2 = true;
                break;
        }
        Service.gI().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        } else if (tempId >= ShenronEventService.NGOC_RONG_1_SAO && tempId <= ShenronEventService.NGOC_RONG_7_SAO) {
            ShenronEventService.gI().openMenuSummonShenron(pl, 0);
        }
    }

    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.gI().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Skill skillNeed = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.gI().sendThongBao(pl, "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            //System.out.println(curSkill.template.name + " - " + curSkill.point);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp " + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryService.gI().sendItemBags(pl);
                }
            } else {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");

            }
        } catch (Exception e) {
            Logger.logException(UseItem.class,
                    e);
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    private void usePorata2(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion2(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void usePorata(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.idMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    public void choseMapCapsule(Player pl, int index) {

        if (pl.idNRNM != -1) {
            Service.gI().sendThongBao(pl, "Không thể mang ngọc rồng này lên Phi thuyền");
            Service.gI().hideWaitDialog(pl);
            return;
        }

        int zoneId = -1;
        if (index > pl.mapCapsule.size() - 1 || index < 0) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            Service.gI().hideWaitDialog(pl);
            return;
        }
        Zone zoneChose = pl.mapCapsule.get(index);
        //Kiểm tra số lượng người trong khu
        zoneChose = ChangeMapService.gI().checkMapCanJoin(pl, zoneChose);
        if (zoneChose == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            Service.gI().hideWaitDialog(pl);
            return;
        }
        if (zoneChose.getNumOfPlayers() > 25
                || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                || MapService.gI().isMapMaBu(zoneChose.map.mapId)
                || MapService.gI().isMapHuyDiet(zoneChose.map.mapId)) {
            Service.gI().sendThongBao(pl, "Hiện tại không thể vào được khu!");
            return;
        }
        if (index != 0 || zoneChose.map.mapId == 21
                || zoneChose.map.mapId == 22
                || zoneChose.map.mapId == 23) {
            pl.mapBeforeCapsule = pl.zone;
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        pl.changeMapVIP = true;
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }

    public void eatPea(Player player) {
        if (!Util.canDoWithTime(player.lastTimeEatPea, 1000)) {
            return;
        }
        player.lastTimeEatPea = System.currentTimeMillis();
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            int hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (Item.ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 1000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp(player.nPoint.hp + hpKiHoiPhuc);
            player.nPoint.setMp(player.nPoint.mp + hpKiHoiPhuc);
            PlayerService.gI().sendInfoHpMp(player);
            Service.gI().sendInfoPlayerEatPea(player);
            if (player.pet != null && player.zone.equals(player.pet.zone) && !player.pet.isDie()) {
                int statima = 100 * lvPea;
                player.pet.nPoint.stamina += statima;
                if (player.pet.nPoint.stamina > player.pet.nPoint.maxStamina) {
                    player.pet.nPoint.stamina = player.pet.nPoint.maxStamina;
                }
                player.pet.nPoint.setHp(player.pet.nPoint.hp + hpKiHoiPhuc);
                player.pet.nPoint.setMp(player.pet.nPoint.mp + hpKiHoiPhuc);
                Service.gI().sendInfoPlayerEatPea(player.pet);
                Service.gI().chatJustForMe(player, player.pet, "Cám ơn sư phụ");
            }

            InventoryService.gI().subQuantityItemsBag(player, pea, 1);
            InventoryService.gI().sendItemBags(player);
        }
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402: //skill 1
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403: //skill 2
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404: //skill 3
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759: //skill 4
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cám ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;

            }

        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    private void ItemManhGiay(Player pl, Item item) {
        if (pl.winSTT && !Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            Service.gI().sendThongBao(pl, "Hãy gặp thần mèo Karin để sử dụng");
            return;
        } else if (pl.winSTT && Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            pl.winSTT = false;
            pl.callBossPocolo = false;
            pl.zoneSieuThanhThuy = null;
        }
        NpcService.gI().createMenuConMeo(pl, item.template.id, 564, "Đây chính là dấu hiệu riêng của...\nĐại Ma Vương Pôcôlô\nĐó là một tên quỷ dữ đội lốt người, một kẻ đại gian ác\ncó sức mạnh vô địch và lòng tham không đáy...\nĐối phó với hắn không phải dễ\nCon có chắc chắn muốn tìm hắn không?", "Đồng ý", "Từ chối");
    }

    private void ItemSieuThanThuy(Player pl, Item item) {
        long tnsm = 5_000_000;
        int n = 0;
        switch (item.template.id) {
            case 727:
                n = 2;
                break;
            case 728:
                n = 10;
                break;
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        if (Util.isTrue(50, 100)) {
            Service.gI().sendThongBao(pl, "Bạn đã bị chết vì độc của thuốc tăng lực siêu thần thủy.");
            pl.setDie();
        } else {
            for (int i = 0; i < n; i++) {
                Service.gI().addSMTN(pl, (byte) 2, tnsm, true);
            }
        }
    }

    public void UseCard(Player pl, Item item) {
        RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream().filter(c -> c.Id == item.template.id).findFirst().orElse(null);
        if (radarTemplate == null) {
            return;
        }
        if (radarTemplate.Require != -1) {
            RadarCard radarRequireTemplate = RadarService.gI().RADAR_TEMPLATE.stream().filter(r -> r.Id == radarTemplate.Require).findFirst().orElse(null);
            if (radarRequireTemplate == null) {
                return;
            }
            Card cardRequire = pl.Cards.stream().filter(r -> r.Id == radarRequireTemplate.Id).findFirst().orElse(null);
            if (cardRequire == null || cardRequire.Level < radarTemplate.RequireLevel) {
                Service.gI().sendThongBao(pl, "Bạn cần sưu tầm " + radarRequireTemplate.Name + " ở cấp độ " + radarTemplate.RequireLevel + " mới có thể sử dụng thẻ này");
                return;
            }
        }
        Card card = pl.Cards.stream().filter(r -> r.Id == item.template.id).findFirst().orElse(null);
        if (card == null) {
            Card newCard = new Card(item.template.id, (byte) 1, radarTemplate.Max, (byte) -1, radarTemplate.Options);
            pl.Cards.add(newCard);
            RadarService.gI().RadarSetAmount(pl, newCard.Id, newCard.Amount, newCard.MaxAmount);
            RadarService.gI().RadarSetLevel(pl, newCard.Id, newCard.Level);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
        } else {
            if (card.Level >= 2) {
                Service.gI().sendThongBao(pl, "Thẻ này đã đạt cấp tối đa");
                return;
            }
            card.Amount++;
            if (card.Amount >= card.MaxAmount) {
                card.Amount = 0;
                if (card.Level == -1) {
                    card.Level = 1;
                } else {
                    card.Level++;
                }
                Service.gI().point(pl);
            }
            RadarService.gI().RadarSetAmount(pl, card.Id, card.Amount, card.MaxAmount);
            RadarService.gI().RadarSetLevel(pl, card.Id, card.Level);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);
        }
    }

    public static final int[][][] LIST_ITEM_CLOTHES = {
        // áo , quần , găng ,giày,rada
        //td -> nm -> xd
        {{0, 33, 3, 34, 136, 137, 138, 139, 230, 231, 232, 233, 555}, {6, 35, 9, 36, 140, 141, 142, 143, 242, 243, 244, 245, 556}, {21, 24, 37, 38, 144, 145, 146, 147, 254, 255, 256, 257, 562}, {27, 30, 39, 40, 148, 149, 150, 151, 266, 267, 268, 269, 563}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
        {{1, 41, 4, 42, 152, 153, 154, 155, 234, 235, 236, 237, 557}, {7, 43, 10, 44, 156, 157, 158, 159, 246, 247, 248, 249, 558}, {22, 46, 25, 45, 160, 161, 162, 163, 258, 259, 260, 261, 564}, {28, 47, 31, 48, 164, 165, 166, 167, 270, 271, 272, 273, 565}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
        {{2, 49, 5, 50, 168, 169, 170, 171, 238, 239, 240, 241, 559}, {8, 51, 11, 52, 172, 173, 174, 175, 250, 251, 252, 253, 560}, {23, 53, 26, 54, 176, 177, 178, 179, 262, 263, 264, 265, 566}, {29, 55, 32, 56, 180, 181, 182, 183, 274, 275, 276, 277, 567}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}}
    };

    private void ItemSKH(Player pl, Item item) {
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada", "Từ Chối");
    }

    public void ComfirmNhanVIP(Player player, boolean receivedPet) {
        if (InventoryService.gI().getCountEmptyBag(player) > 5) {
            Item itemqua;
            Item itemqua1;
            Item itemqua2;
            Item itemqua3;
            Item itemqua4;
            Item itemqua5;
            Item itemqua6;
            try {
                int time = 5;

                // Kiểm tra nếu người chơi đã nhận quà VIP 1 hoặc VIP 2 rồi
                if (player.getSession().hasReceivedVIP) {
                    Service.gI().sendThongBao(player, "Bạn Đã Sở Hữu VIP Rồi!");
                    return;
                }

                // Nếu đủ 150 gem, tiến hành VIP 1
                if (player.inventory.gem >= 1500) {
                    Service.gI().sendThongBao(player, "Tiến Hành Nhận\nVIP 1 \nSau " + time + " Giây!");
                    while (time > 0) {
                        time--;
                        Thread.sleep(1000);
                        Service.gI().sendThongBao(player, "|7|" + time);
                    }

                    // Đánh dấu đã nhận VIP 1
                    PlayerDAO.subVIP(player);
                    player.inventory.gem -= 1500;
                    player.inventory.gold += 500_000_000;
                    player.getSession().vip = 1;
                    Service.gI().sendMoney(player);
                    itemqua = ItemService.gI().createNewItem((short) 459, 1);
                    itemqua.itemOptions.add(new ItemOption(112, 80));
                    itemqua.itemOptions.add(new ItemOption(93, 90));
                    itemqua.itemOptions.add(new ItemOption(30, 0));
                    if (receivedPet) {
                        PetService.gI().createNormalPet(player);
                    }

                    switch (player.gender) {
                        case 0:
                            itemqua1 = ItemService.gI().createNewItem((short) 227);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        case 1:
                            itemqua1 = ItemService.gI().createNewItem((short) 228);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        case 2:
                            itemqua1 = ItemService.gI().createNewItem((short) 229);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        default:
                            Service.gI().sendThongBao(player, "Giới tính không hợp lệ, không thể nhận quà!");
                            return;
                    }

                    Service.gI().sendThongBao(player, "Bạn vừa nhận được 500Tr vàng");
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua.template.name);
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua1.template.name);
                    Service.gI().sendThongBao(player, "Bạn vừa nhận được đệ tử");

                    InventoryService.gI().addItemBag(player, itemqua);
                    InventoryService.gI().addItemBag(player, itemqua1);
                    InventoryService.gI().sendItemBags(player);
                } else {
                    Service.gI().sendThongBao(player, "Bạn Chưa Đủ Ngọc Để Nhận Vip Nạp Này!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 6 ô trống hành trang");
        }
    }

    public void ComfirmNhanVIP2(Player player, boolean receivedPet) {
        if (InventoryService.gI().getCountEmptyBag(player) > 5) {
            Item itemqua;
            Item itemqua1;
            Item itemqua2;
            Item itemqua3;
            Item itemqua4;
            Item itemqua5;
            Item itemqua6;
            try {
                int time = 5;
                if (player.getSession().hasReceivedVIP) {
                    Service.gI().sendThongBao(player, "Bạn Đã Sở Hữu VIP Rồi!");
                    return;
                }
                if (player.inventory.gem >= 3500) { // Nếu đủ 350 gem, tiến hành VIP 2
                    Service.gI().sendThongBao(player, "Tiến Hành Nhận\nVIP 2 \nSau " + time + " Giây!");
                    while (time > 0) {
                        time--;
                        Thread.sleep(1000);
                        Service.gI().sendThongBao(player, "|7|" + time);
                    }

                    // Đánh dấu đã nhận VIP 2
                    PlayerDAO.subVIP(player);
                    player.inventory.gem -= 3500;
                    player.inventory.gold += 1_000_000_000;
                    player.getSession().vip = 2;
                    Service.gI().sendMoney(player);
                    itemqua = ItemService.gI().createNewItem((short) 459, 3);
                    itemqua.itemOptions.add(new ItemOption(112, 80));
                    itemqua.itemOptions.add(new ItemOption(93, 90));
                    itemqua.itemOptions.add(new ItemOption(30, 0));
                    if (receivedPet) {
                        PetService.gI().createNormalPet(player);
                    }
                    itemqua2 = ItemService.gI().createNewItem((short) 956, 30);
                    itemqua3 = ItemService.gI().createNewItem((short) 710);
                    itemqua3.itemOptions.add(new ItemOption(50, 22));
                    itemqua3.itemOptions.add(new ItemOption(77, 20));
                    itemqua3.itemOptions.add(new ItemOption(103, 20));
                    itemqua3.itemOptions.add(new ItemOption(159, 3));
                    itemqua3.itemOptions.add(new ItemOption(160, 35));
                    itemqua3.itemOptions.add(new ItemOption(30, 0));

                    switch (player.gender) {
                        case 0:
                            itemqua1 = ItemService.gI().createNewItem((short) 227);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        case 1:
                            itemqua1 = ItemService.gI().createNewItem((short) 228);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        case 2:
                            itemqua1 = ItemService.gI().createNewItem((short) 229);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        default:
                            Service.gI().sendThongBao(player, "Giới tính không hợp lệ, không thể nhận quà!");
                            return;
                    }

                    Service.gI().sendThongBao(player, "Bạn vừa nhận được 1 Tỉ vàng");
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua.template.name);
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua1.template.name);
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua2.template.name);
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua3.template.name);
                    Service.gI().sendThongBao(player, "Bạn vừa nhận được đệ tử");

                    InventoryService.gI().addItemBag(player, itemqua);
                    InventoryService.gI().addItemBag(player, itemqua1);
                    InventoryService.gI().addItemBag(player, itemqua2);
                    InventoryService.gI().addItemBag(player, itemqua3);
                    InventoryService.gI().sendItemBags(player);
                } else {
                    Service.gI().sendThongBao(player, "Bạn Chưa Đủ Hồng Ngọc Để Nhận Vip Này!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 6 ô trống hành trang");
        }
    }

    public void ComfirmNhanVIP3(Player player, boolean receivedPet) {
        if (InventoryService.gI().getCountEmptyBag(player) > 5) {
            Item itemqua;
            Item itemqua1;
            Item itemqua2;
            Item itemqua3;
            Item itemqua4;
            Item itemqua5;
            Item itemqua6;
            try {
                int time = 5;
                if (player.getSession().hasReceivedVIP) {
                    Service.gI().sendThongBao(player, "Bạn Đã Sở Hữu VIP Rồi!");
                    return;
                }
                if (player.inventory.gem >= 3500) { // Nếu đủ 350 gem, tiến hành VIP 2
                    Service.gI().sendThongBao(player, "Tiến Hành Nhận\nVIP 3 \nSau " + time + " Giây!");
                    while (time > 0) {
                        time--;
                        Thread.sleep(1000);
                        Service.gI().sendThongBao(player, "|7|" + time);
                    }

                    // Đánh dấu đã nhận VIP 2
                    PlayerDAO.subVIP(player);
                    player.inventory.gem -= 5500;
                    player.inventory.gold += 1_500_000_000;
                    player.getSession().vip = 3;
                    player.effect.addPointBiMocSachTui();
                    Service.gI().sendMoney(player);
                    itemqua = ItemService.gI().createNewItem((short) 459, 5);
                    itemqua.itemOptions.add(new ItemOption(112, 80));
                    itemqua.itemOptions.add(new ItemOption(93, 90));
                    itemqua.itemOptions.add(new ItemOption(30, 0));
                    if (receivedPet) {
                        PetService.gI().createNormalPet(player);
                    }
                    itemqua2 = ItemService.gI().createNewItem((short) 956, 50);
                    itemqua3 = ItemService.gI().createNewItem((short) 710);
                    itemqua3.itemOptions.add(new ItemOption(50, 22));
                    itemqua3.itemOptions.add(new ItemOption(77, 20));
                    itemqua3.itemOptions.add(new ItemOption(103, 20));
                    itemqua3.itemOptions.add(new ItemOption(159, 3));
                    itemqua3.itemOptions.add(new ItemOption(160, 35));
                    itemqua3.itemOptions.add(new ItemOption(30, 0));

                    switch (player.gender) {
                        case 0:
                            itemqua1 = ItemService.gI().createNewItem((short) 227);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        case 1:
                            itemqua1 = ItemService.gI().createNewItem((short) 228);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        case 2:
                            itemqua1 = ItemService.gI().createNewItem((short) 229);
                            itemqua1.itemOptions.add(new ItemOption(50, Util.nextInt(10, 25)));
                            itemqua1.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(97, Util.nextInt(5, 15)));
                            itemqua1.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
                            break;
                        default:
                            Service.gI().sendThongBao(player, "Giới tính không hợp lệ, không thể nhận quà!");
                            return;
                    }

                    Service.gI().sendThongBao(player, "Bạn vừa nhận được 1 Tỉ 5 vàng");
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua.template.name);
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua1.template.name);
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua2.template.name);
                    Service.gI().sendThongBao(player, "Bạn Đã Nhận Được " + itemqua3.template.name);
                    Service.gI().sendThongBao(player, "Bạn vừa nhận được đệ tử");

                    InventoryService.gI().addItemBag(player, itemqua);
                    InventoryService.gI().addItemBag(player, itemqua1);
                    InventoryService.gI().addItemBag(player, itemqua2);
                    InventoryService.gI().addItemBag(player, itemqua3);
                    InventoryService.gI().sendItemBags(player);
                } else {
                    Service.gI().sendThongBao(player, "Bạn Chưa Đủ Hồng Ngọc Để Nhận Vip Này!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Service.gI().sendThongBao(player, "Bạn phải có ít nhất 6 ô trống hành trang");
        }
    }
}
