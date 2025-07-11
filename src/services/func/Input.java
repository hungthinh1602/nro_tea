package services.func;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import Bot.NewBot;
import models.clan.Clan;
import models.clan.ClanMember;
import database.AlyraManager;
import consts.ConstNpc;
import database.daos.PlayerDAO;
import models.item.Item;
import models.map.Zone;
import models.npc.Npc;
import services.map.NpcManager;
import models.player.Player;
import network.io.Message;
import interfaces.ISession;
import server.Client;
import services.Service;
import services.GiftCodeService;
import services.player.InventoryService;
import services.ItemService;
import services.map.NpcService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.item.Item.ItemOption;
import models.npc.TaiXiu;
import models.player.Inventory;
import server.Manager;
import services.player.ClanService;
import services.map.ChangeMapService;
import services.player.PlayerService;
import utils.Util;

public class Input {

    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<>();
    public static final int TAIXIU_TAI = 5172;
    public static final int TAIXIU_XIU = 5173;
    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int NAP_THE = 505;
    public static final int CHANGE_NAME_BY_ITEM = 506;
    public static final int GIVE_IT = 507;
    public static final int GET_IT = 508;
    public static final int DANGKY = 509;
    public static final int CHOOSE_LEVEL_KGHD = 510;
    public static final int CHOOSE_LEVEL_CDRD = 511;
    public static final int DISSOLUTION_CLAN = 513;

    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;
    public static final byte MBV = 23;
    public static final byte BANSLL = 24;
    public static final byte BANGHOI = 25;
    public static final int CON_SO_MAY_MAN_VANG = 50098;
    public static final int CON_SO_MAY_MAN_NGOC = 50900;

    public static final int BOTQUAI = 206783;
    public static final int BOTITEM = 206762;
    public static final int BOTBOSS = 2067683;
    public static final int BOTQUAI_NAPPA = 206784;
    public static final int BOTQUAI_TUONGLAI = 206785;
    public static final int BOTQUAI_COLD = 206786;
    public static final int BUFFVND = 586537;

    public static final int TANG_NGOC_XANH = 5356326;
    public static final int TANG_NGOC_HONG = 4365478;

    private static Input intance;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            Player pl = null;
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.idMark.getTypeInput()) {
                case TANG_NGOC_HONG:
                    pl = Client.gI().getPlayer(text[0]);
                    int numRuby = Integer.parseInt((text[1]));
                    if (pl != null) {
                        if (numRuby >= 10 && numRuby <= 1000 && player.inventory.ruby >= numRuby) {
                            Item item = InventoryService.gI().findVeTangNgochong(player);
                            ItemOption option = item.itemOptions.stream().filter(io -> io.optionTemplate.id == 31 && io.param > 0).findAny().orElse(null);
                            int maxNumOption = numRuby / 10;
                            if (option == null || option.param < maxNumOption) {
                                Service.gI().sendThongBao(player, "Không đủ số lượt để tặng.");
                                return;
                            }
                            player.inventory.subRuby(numRuby);
                            PlayerService.gI().sendInfoHpMpMoney(player);
                            pl.inventory.ruby += numRuby * 90 / 100;
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().sendThongBao(player, "Tặng Hồng ngọc thành công");
                            Service.gI().sendThongBao(pl, "Bạn được " + player.name + " tặng " + (numRuby * 90 / 100) + " Hồng ngọc");
                            option.param -= maxNumOption;
                            InventoryService.gI().sendItemBags(player);
                        } else {
                            Service.gI().sendThongBao(player, "Không đủ hoặc quá giới hạn Hồng ngọc cho phép tặng (10-1000 ngọc)");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case TANG_NGOC_XANH:
                    pl = Client.gI().getPlayer(text[0]);
                    int numGem = Integer.parseInt((text[1]));
                    if (pl != null) {
                        if (numGem >= 10 && numGem <= 1000 && player.inventory.gem >= numGem) {
                            Item item = InventoryService.gI().findVeTangNgocxanh(player);
                            ItemOption option = item.itemOptions.stream().filter(io -> io.optionTemplate.id == 31 && io.param > 0).findAny().orElse(null);
                            int maxNumOption = numGem / 10;
                            if (option == null || option.param < maxNumOption) {
                                Service.gI().sendThongBao(player, "Không đủ số lượt để tặng.");
                                return;
                            }
                            player.inventory.subGem(numGem);
                            PlayerService.gI().sendInfoHpMpMoney(player);
                            pl.inventory.gem += numGem * 90 / 100;
                            PlayerService.gI().sendInfoHpMpMoney(pl);
                            Service.gI().sendThongBao(player, "Tặng ngọc xanh thành công");
                            Service.gI().sendThongBao(pl, "Bạn được " + player.name + " tặng " + (numGem * 90 / 100) + " Ngọc xanh");
                            option.param -= maxNumOption;
                            InventoryService.gI().sendItemBags(player);
                        } else {
                            Service.gI().sendThongBao(player, "Không đủ hoặc quá giới hạn Ngọc xanh cho phép tặng (10-1000 ngọc)");
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case TAIXIU_TAI:
                    try {
                        long taixiu_Tai = Long.parseLong(text[0]);
                        if (taixiu_Tai >= 1_000_000L && taixiu_Tai <= 200_000_000_000L) {
                            if (player.inventory.gold + (player.goldTai * 80 / 100) + taixiu_Tai > 200_000_000_000L) {
                                Service.gI().sendThongBaoOK(player, "Số vàng nhận sau khi cược vượt quá giới hạn vàng.");
                                return;
                            }
                            if (player.inventory.gold < taixiu_Tai) {
                                Service.gI().sendThongBao(player, "Bạn không đủ tiền cược.");
                                return;
                            }
                            if (Util.canDoWithTime(TaiXiu.gI().lastTimeEnd, TaiXiu.TIME_END)) {
                                Service.gI().sendThongBaoOK(player, "Đã qua lượt mới, vui lòng đặt cược lại.");
                                return;
                            }
                            Service.gI().sendThongBao(player, "Bạn đã đặt " + Util.numberToMoney(taixiu_Tai) + " vàng vào cửa Tài");
                            TaiXiu.gI().addTai(player, taixiu_Tai);
                        } else {
                            Service.gI().sendThongBao(player, "Cược ít nhất 1Tr - nhiều nhất 200Tỉ");
                        }
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Số tiền nhập không hợp lệ.");
                    }
                    break;

                case TAIXIU_XIU:
                    try {
                        long taixiu_Xiu = Long.parseLong(text[0]);
                        if (taixiu_Xiu >= 1_000_000L && taixiu_Xiu <= 200_000_000_000L) {
                            if (player.inventory.gold + (player.goldXiu * 80 / 100) + taixiu_Xiu > 200_000_000_000L) {
                                Service.gI().sendThongBaoOK(player, "Số vàng nhận sau khi cược vượt quá giới hạn vàng.");
                                return;
                            }
                            if (player.inventory.gold < taixiu_Xiu) {
                                Service.gI().sendThongBaoOK(player, "Bạn không đủ tiền cược.");
                                return;
                            }
                            if (Util.canDoWithTime(TaiXiu.gI().lastTimeEnd, TaiXiu.TIME_END)) {
                                Service.gI().sendThongBaoOK(player, "Đã qua lượt mới, vui lòng đặt cược lại.");
                                return;
                            }
                            Service.gI().sendThongBao(player, "Bạn đã đặt " + Util.numberToMoney(taixiu_Xiu) + " vàng vào cửa Xỉu");
                            TaiXiu.gI().addXiu(player, taixiu_Xiu);
                        } else {
                            Service.gI().sendThongBao(player, "Cược ít nhất 1Tr - nhiều nhất 200Tỉ");
                        }
                    } catch (NumberFormatException e) {
                        Service.gI().sendThongBao(player, "Số tiền nhập không hợp lệ.");
                    }
                    break;
                case BUFFVND:
                    try {
                        String username = text[0].trim();
                        int addvnd = Integer.parseInt(text[1].trim());
                        int addtotalvnd = Integer.parseInt(text[2].trim());
                        if (PlayerDAO.Addvnd(username, addvnd)
                                && PlayerDAO.Addtotalvnd(username, addtotalvnd)) {

                            Service.gI().sendThongBao(player, "Bạn đã buff cho " + username + " " + addvnd + "Tổng nạp\n và" + addtotalvnd + "VNĐ");

                            if (Client.gI().getPlayerByUserName(username) != null) {
                                Client.gI().getPlayerByUserName(username).getSession().sotien += addvnd;
                                Client.gI().getPlayerByUserName(username).getSession().danap += addtotalvnd;
                                Service.gI().sendThongBao(Client.gI().getPlayerByUserName(username), "Bạn vừa được cộng " + addvnd + "Tổng nạp bởi " + player.name);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Service.gI().sendThongBao(player, "Đã đéo có gì xảy ra");
                    }
                    break;
                case BOTITEM:
                    int slot = Integer.parseInt(text[0]);
                    int idBan = Integer.parseInt(text[1]);
                    int idTraoDoi = Integer.parseInt(text[2]);
                    int slot_TraoDoi = Integer.parseInt(text[3]);
                    // ShopBot bs = new ShopBot(idBan , idTraoDoi , slot_TraoDoi);
                    new Thread(() -> {
                        //  NewBot.gI().runBot(1 , bs , slot);
                    }).start();
                    break;
                case BOTQUAI_NAPPA:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        NewBot.gI().runBot(1, slot);
                    }).start();
                    break;
                case BOTQUAI_TUONGLAI:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        NewBot.gI().runBot(2, slot);
                    }).start();
                    break;
                case BOTQUAI_COLD:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        NewBot.gI().runBot(3, slot);
                    }).start();
                    break;
                case BOTBOSS:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        NewBot.gI().runBot(2, slot);
                    }).start();
                    break;
                case BOTQUAI:
                    slot = Integer.parseInt(text[0]);
                    new Thread(() -> {
                        NewBot.gI().runBot(0, slot);
                    }).start();
                    break;
                case CON_SO_MAY_MAN_VANG:
                    int CSMM2 = Integer.parseInt(text[0]);
                    if (CSMM2 >= MiniGame.gI().MiniGame_S1.min && CSMM2 <= MiniGame.gI().MiniGame_S1.max && MiniGame.gI().MiniGame_S1.second > 10) {
                        MiniGame.gI().MiniGame_S1.newData(player, CSMM2, 1);
                    }
                    break;
                case GIVE_IT:
                    String name = text[0];
                    int id = Integer.parseInt(text[1]);
                    int op = Integer.parseInt(text[2]);
                    int pr = Integer.parseInt(text[3]);
                    int q = Integer.parseInt(text[4]);

                    if (Client.gI().getPlayer(name) != null) {
                        Item item = ItemService.gI().createNewItem(((short) id));
                        List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop((short) id);
                        if (!ops.isEmpty()) {
                            item.itemOptions = ops;
                        }
                        item.quantity = q;
                        item.itemOptions.add(new Item.ItemOption(op, pr));
                        InventoryService.gI().addItemBag(Client.gI().getPlayer(name), item);
                        InventoryService.gI().sendItemBags(Client.gI().getPlayer(name));
                        Service.gI().sendThongBao(Client.gI().getPlayer(name), "Nhận " + item.template.name + " từ " + player.name);

                    } else {
                        Service.gI().sendThongBao(player, "Không online");
                    }
                    break;
                case GET_IT:
                    id = Integer.parseInt(text[0]);
                    op = Integer.parseInt(text[1]);
                    pr = Integer.parseInt(text[2]);
                    q = Integer.parseInt(text[3]);

                    if (player.isAdmin()) {
                        Item item = ItemService.gI().createNewItem(((short) id));
                        List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop((short) id);
                        if (!ops.isEmpty()) {
                            item.itemOptions = ops;
                        }
                        item.quantity = q;
                        item.itemOptions.add(new Item.ItemOption(op, pr));
                        InventoryService.gI().addItemBag(player, item);
                        InventoryService.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "Nhận " + item.template.name + " !");

                    } else {
                        Service.gI().sendThongBao(player, "Không đủ quyền hạn!");
                    }
                    break;
                case CHANGE_PASSWORD:
                    Service.gI().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE:
                    GiftCodeService.gI().giftCode(player, text[0]);
                    break;
                case FIND_PLAYER:
                    pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngài muốn..?",
                                new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên", "Ban"},
                                pl);
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case CHANGE_NAME: {
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (AlyraManager.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            AlyraManager.executeUpdate("update player set name = ? where id = ?", plChanged.name, plChanged.id);
                            Service.gI().player(plChanged);
                            Service.gI().Send_Caitrang(plChanged);
                            Service.gI().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.gI().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.gI().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                }
                break;
                case CHANGE_NAME_BY_ITEM: {
                    if (player != null) {
                        if (AlyraManager.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                            createFormChangeNameByItem(player);
                        } else if (Util.haveSpecialCharacter(text[0])) {
                            Service.gI().sendThongBaoOK(player, "Tên nhân vật không được chứa ký tự đặc biệt");
                        } else if (text[0].length() < 5) {
                            Service.gI().sendThongBaoOK(player, "Tên nhân vật quá ngắn");
                        } else if (text[0].length() > 10) {
                            Service.gI().sendThongBaoOK(player, "Tên nhân vật chỉ đồng ý các ký tự a-z, 0-9 và chiều dài từ 5 đến 10 ký tự");
                        } else {
                            Item theDoiTen = InventoryService.gI().findItem(player.inventory.itemsBag, 2006);
                            if (theDoiTen == null) {
                                Service.gI().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                            } else {
                                InventoryService.gI().subQuantityItemsBag(player, theDoiTen, 1);
                                player.name = text[0].toLowerCase();
                                AlyraManager.executeUpdate("update player set name = ? where id = ?", player.name, player.id);
                                Service.gI().player(player);
                                Service.gI().Send_Caitrang(player);
                                Service.gI().sendFlagBag(player);
                                Zone zone = player.zone;
                                ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                                Service.gI().sendThongBao(player, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            }
                        }
                    }
                }
                break;
                case CHOOSE_LEVEL_BDKB:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc muốn đến\nhang kho báu cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }

                    break;
                case CHOOSE_LEVEL_KGHD:
                    level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.MR_POPO, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, 2,
                                    "Cậu có chắc muốn đến\nDestron Gas cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    }
                    break;
                case CHOOSE_LEVEL_CDRD:
                    level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.THAN_VU_TRU, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, 3,
                                    "Con có chắc muốn đến\ncon đường rắn độc cấp độ " + level + " ?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    }
                    break;
                case MBV:
                    int mbv = Integer.parseInt(text[0]);
                    int nmbv = Integer.parseInt(text[1]);
                    int rembv = Integer.parseInt(text[2]);
                    if ((mbv + "").length() != 6 || (nmbv + "").length() != 6 || (rembv + "").length() != 6) {
                        Service.gI().sendThongBao(player, "Trêu bố mày à?");
                    } else if (player.mbv == 0) {
                        Service.gI().sendThongBao(player, "Bạn chưa cài mã bảo vệ!");
                    } else if (player.mbv != mbv) {
                        Service.gI().sendThongBao(player, "Mã bảo vệ không đúng");
                    } else if (nmbv != rembv) {
                        Service.gI().sendThongBao(player, "Mã bảo vệ không trùng khớp");
                    } else {
                        player.mbv = nmbv;
                        Service.gI().sendThongBao(player, "Đổi mã bảo vệ thành công!");
                    }
                    break;
                case BANSLL:
                    // Check if the input contains only numbers
                    String input = text[0];
                    if (!input.matches("^[0-9\\\\s,\\\\.]+$")) {
                        Service.gI().sendThongBao(player, "Vui lòng nhập số hợp lệ (không có ký tự đặc biệt).");
                        break;
                    }

                    // Proceed with the original logic if the input is valid
                    int sltv = Math.abs(Integer.parseInt(input));
                    long cost = (long) sltv * 37000000;

                    Item ThoiVang = InventoryService.gI().findItemBag(player, 457);
                    if (ThoiVang != null) {
                        if (ThoiVang.quantity < sltv) {
                            Service.gI().sendThongBao(player, "Bạn chỉ có " + ThoiVang.quantity + " Thỏi vàng");
                        } else {
                            if (player.inventory.gold + cost > Inventory.LIMIT_GOLD) {
                                int slban = (int) ((Inventory.LIMIT_GOLD - player.inventory.gold) / 37000000);
                                if (slban < 1) {
                                    Service.gI().sendThongBao(player, "Vàng sau khi bán vượt quá giới hạn");
                                } else if (slban < 2) {
                                    Service.gI().sendThongBao(player, "Bạn chỉ có thể bán 1 Thỏi vàng");
                                } else {
                                    Service.gI().sendThongBao(player, "Số lượng trong khoảng 1 tới " + slban);
                                }
                            } else {
                                InventoryService.gI().subQuantityItemsBag(player, ThoiVang, sltv);
                                InventoryService.gI().sendItemBags(player);
                                player.inventory.gold += cost;
                                Service.gI().sendMoney(player);
                                Service.gI().sendThongBao(player, "Đã bán " + sltv + " Thỏi vàng thu được " + Util.numberToMoney(cost) + " vàng");
                            }
                        }
                    }
                    break;

                case BANGHOI:
                    Clan clan = player.clan;
                    if (clan != null) {
                        ClanMember cm = clan.getClanMember((int) player.id);
                        if (clan.isLeader(player)) {
                            if (clan.canUpdateClan(player)) {
                                String tenvt = text[0];
                                if (!Util.haveSpecialCharacter(tenvt) && tenvt.length() > 1 && tenvt.length() < 5) {
                                    clan.name2 = tenvt;
                                    clan.update();
                                    Service.gI().sendThongBao(player, "[" + tenvt + "] OK");
                                } else {
                                    Service.gI().sendThongBaoOK(player, "Chỉ chấp nhận các ký tự a-z, 0-9 và chiều dài từ 2 đến 4 ký tự");
                                }
                            }
                        }
                    }
                    break;
                case DISSOLUTION_CLAN:
                    String xacNhan = text[0];
                    if (xacNhan.equalsIgnoreCase("OK")) {
                        clan = player.clan;
                        if (clan.isLeader(player)) {
                            clan.deleteDB(clan.id);
                            Manager.CLANS.remove(clan);
                            player.clan = null;
                            player.clanMember = null;
                            ClanService.gI().sendMyClan(player);
                            ClanService.gI().sendClanId(player);
                            player.lastTimeRemoveClan = System.currentTimeMillis();
                            Service.gI().sendThongBao(player, "Bang hội đã giải tán thành công.");
                        }
                    }
                    break;
            }
        } catch (Exception e) {
        }
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.idMark.setTypeInput(typeInput);
        Message msg = null;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg = null;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void createFormTangRuby(Player pl) {
        createForm(pl, TANG_NGOC_HONG, "Tặng ngọc", new SubInput("Tên nhân vật", ANY),
                new SubInput("Số Hồng Ngọc Muốn Tặng", NUMERIC));
    }

    public void createFormTangGem(Player pl) {
        createForm(pl, TANG_NGOC_XANH, "Tặng ngọc", new SubInput("Tên nhân vật", ANY),
                new SubInput("Số Ngọc xanh Muốn Tặng", NUMERIC));
    }

    public void taixiu_Tai(Player pl) {
        createForm(pl, TAIXIU_TAI, "Đặt cược Tài Xỉu (Cửa Tài)", new SubInput("Số vàng cược (từ 1Tr -> 200Tỉ)", NUMERIC));
    }

    public void taixiu_Xiu(Player pl) {
        createForm(pl, TAIXIU_XIU, "Đặt cược Tài Xỉu (Cửa Xỉu)", new SubInput("Số vàng cược (từ 1Tr -> 200Tỉ)", NUMERIC));
    }

    public void createFormBuffVND(Player player) {
        createForm(player, BUFFVND, "Buff VNĐ và Tổng Nạp",
                new SubInput("TK game người chơi", ANY),
                new SubInput("VNĐ CẦN BUFF", ANY),
                new SubInput("TỔNG NẠP CẦN BUFF", ANY));
    }

    public void createFormBotQuaiNappa(Player pl) {
        createForm(pl, BOTQUAI_NAPPA, "Buff Bot Quái",
                new SubInput("số lượng bot", NUMERIC));
    }

    public void createFormBotQuaiTuonglai(Player pl) {
        createForm(pl, BOTQUAI_TUONGLAI, "Buff Bot Quái",
                new SubInput("số lượng bot", NUMERIC));
    }

    public void createFormBotQuaiCold(Player pl) {
        createForm(pl, BOTQUAI_COLD, "Buff Bot Quái",
                new SubInput("số lượng bot", NUMERIC));
    }

    public void createFormBotQuai(Player pl) {
        createForm(pl, BOTQUAI, "Buff Bot Quái",
                new SubInput("số lượng bot", NUMERIC));
    }

    public void createFormBotBoss(Player pl) {
        createForm(pl, BOTBOSS, "Buff Bot Boss",
                new SubInput("số lượng bot", NUMERIC));
    }

    public void createFormBotItem(Player pl) {
        createForm(pl, BOTITEM, "Buff Bot Item",
                new SubInput("số lượng bot", NUMERIC),
                new SubInput("id item cần bán", NUMERIC),
                new SubInput("id item trao đổi", NUMERIC),
                new SubInput("số lượng yêu cầu trao đổi", NUMERIC));
    }

    public void createFormConSoMayMan_Gem(Player pl) {
        createForm(pl, CON_SO_MAY_MAN_NGOC, "Hãy chọn 1 số từ 0 đến 99 giá 5 ngọc", new SubInput("Số bạn chọn", NUMERIC));
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Đổi mật khẩu", new SubInput("Mật khẩu cũ", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }

    public void createFormGiveItem(Player pl) {
        createForm(pl, GIVE_IT, "Tặng vật phẩm", new SubInput("Tên", ANY), new SubInput("Id Item", ANY), new SubInput("ID OPTION", ANY), new SubInput("PARAM", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormGetItem(Player pl) {
        createForm(pl, GET_IT, "Get vật phẩm", new SubInput("Id Item", ANY), new SubInput("ID OPTION", ANY), new SubInput("PARAM", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "Giftcode Cậu Bé Rồng Online", new SubInput("Gift-code", ANY));
    }

    public void createFormMBV(Player pl) {
        createForm(pl, MBV, "Đồ ngu! Đồ ăn hại! Cút mẹ mày đi!", new SubInput("Nhập Mã Bảo Vệ Đã Quên", NUMERIC), new SubInput("Nhập Mã Bảo Vệ Mới", NUMERIC), new SubInput("Nhập Lại Mã Bảo Vệ Mới", NUMERIC));
    }

    public void createFormBangHoi(Player pl) {
        createForm(pl, BANGHOI, "Nhập tên viết tắt bang hội", new SubInput("Tên viết tắt từ 2 đến 4 kí tự", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void createFormNapThe(Player pl, byte loaiThe) {
        pl.idMark.setLoaiThe(loaiThe);
        createForm(pl, NAP_THE, "Nạp thẻ", new SubInput("Mã thẻ", ANY), new SubInput("Seri", ANY));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormConSoMayMan_Gold(Player pl) {
        createForm(pl, CON_SO_MAY_MAN_VANG, "Hãy chọn 1 số từ 0 đến 99 giá 1.000.000 vàng", new SubInput("Số bạn chọn", NUMERIC));
    }

    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Hãy chọn cấp độ hang kho báu từ 1-110", new SubInput("Cấp độ", NUMERIC));
    }

    public void createFormChooseLevelCDRD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_CDRD, "Hãy chọn cấp độ từ 1-110", new SubInput("Cấp độ", NUMERIC));
    }

    public void createFormChooseLevelKGHD(Player pl) {
        createForm(pl, CHOOSE_LEVEL_KGHD, "Hãy chọn cấp độ từ 1-110", new SubInput("Cấp độ", NUMERIC));
    }

    public void createFormBanSLL(Player pl) {
        createForm(pl, BANSLL, "Bạn muốn bán bao nhiêu [Thỏi vàng] ?", new SubInput("Số lượng", NUMERIC));
    }

    public void createFormGiaiTanBangHoi(Player pl) {
        createForm(pl, DISSOLUTION_CLAN, "Nhập OK để xác nhận giải tán bang hội.", new SubInput("", ANY));
    }

    public static class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

}
