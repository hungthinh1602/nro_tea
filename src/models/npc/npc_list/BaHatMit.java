package models.npc.npc_list;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import consts.ConstNpc;
import lombok.extern.java.Log;
import models.item.Item;
import models.tournament.DeathOrAliveArena;
import managers.tournament.DeathOrAliveArenaManager;
import models.item.Item.ItemOption;
import services.tournament.DeathOrAliveArenaService;
import models.npc.Npc;
import models.npc.NpcFactory;
import models.player.Player;
import network.io.Message;
import services.player.InventoryService;
import services.ItemService;
import services.Service;
import services.map.ChangeMapService;
import services.CombineService;
import services.RewardService;
import services.ShopService;
import utils.Logger;
import utils.Util;

public class BaHatMit extends Npc {

    public BaHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi tìm ta có việc gì?",
                            "Sự kiện\nNoel 2024", "Chức năng\nPha lê", "Võ đài\nSinh Tử", "Đập sét\nkích hoạt");
                case 112 -> {
                    if (Util.isAfterMidnight(player.lastTimePKVoDaiSinhTu)) {
                        player.haveRewardVDST = false;
                        player.thoiVangVoDaiSinhTu = 0;
                    }
                    if (player.haveRewardVDST) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Đây là phần thưởng cho con.",
                                "1 ngọc bí\nbất kì", "1 bí ngô");
                        return;
                    }
                    if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Ngươi muốn hủy đăng ký thi đấu võ đài?",
                                    "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                            return;
                        }
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó",
                                "Top 100", "Bình chọn", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                        return;
                    }
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi muốn đăng ký thi đấu võ đài?\nnhiều phần thưởng giá trị đang đợi ngươi đó",
                            "Top 100", "Đồng ý\n" + player.thoiVangVoDaiSinhTu + " thỏi vàng", "Từ chối", "Về\nđảo rùa");
                }
                case 174 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi tìm ta có việc gì?",
                            "Quay về", "Từ chối");
                case 181 ->
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi tìm ta có việc gì?",
                            "Quay về", "Từ chối");
                default -> {
                    Item nguoituyet = InventoryService.gI().findItemBag(player, 1210);
                    Item nguoituyetbanggia = InventoryService.gI().findItemBag(player, 1211);
                    String nangcapbt = InventoryService.gI().findItemBongTaiCap2(player) ? "Mở chỉ số\nBông tai\nPorata cấp\n2" : "Nâng cấp\nBông tai\nPorata";

                    if (player.luotNhanBuaMienPhi == 1) {
                        if (nguoituyet != null && nguoituyetbanggia != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                    "Thưởng\nBùa 1h\nngẫu nhiên", "Sách\nTuyệt kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                    nangcapbt, "Làm phép\nNhập đá", "Nhập\nNgọc Rồng", "Giao\nNgười Tuyết", "Giao\nNgười Tuyết\nBăng Giá");
                        } else if (nguoituyet != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                    "Thưởng\nBùa 1h\nngẫu nhiên", "Sách\nTuyệt kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                    nangcapbt, "Làm phép\nNhập đá", "Nhập\nNgọc Rồng", "Giao\nNgười Tuyết");
                        } else if (nguoituyetbanggia != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                    "Thưởng\nBùa 1h\nngẫu nhiên", "Sách\nTuyệt kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                    nangcapbt, "Làm phép\nNhập đá", "Nhập\nNgọc Rồng", "Giao\nNgười Tuyết\nBăng Giá");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                    "Thưởng\nBùa 1h\nngẫu nhiên", "Sách\nTuyệt kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                    nangcapbt, "Làm phép\nNhập đá", "Nhập\nNgọc Rồng");
                        }
                    } else {
                        if (nguoituyet != null && nguoituyetbanggia != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                    "Sách\nTuyệt kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm", nangcapbt, "Làm phép\nNhập đá", "Nhập\nNgọc Rồng", "Giao\nNgười Tuyết", "Giao\nNgười Tuyết\nBăng Giá");
                        } else if (nguoituyet != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                    "Sách\nTuyệt kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm", nangcapbt, "Làm phép\nNhập đá", "Nhập\nNgọc Rồng", "Giao\nNgười Tuyết");
                        } else if (nguoituyetbanggia != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                    "Sách\nTuyệt kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm", nangcapbt, "Làm phép\nNhập đá", "Nhập\nNgọc Rồng", "Giao\nNgười Tuyết\nBăng Giá");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi tìm ta có việc gì?",
                                    "Sách\nTuyệt kỹ", "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm", nangcapbt, "Làm phép\nNhập đá", "Nhập\nNgọc Rồng");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 5 -> {
                    if (player.idMark.isBaseMenu()) {
                        switch (select) {
                            case 0 ->
                                createOtherMenu(player, ConstNpc.CHUC_NANG_SU_KIEN_NOEL, "Ta có thể giúp gì cho ngươi?",
                                        "Đắp\nngười tuyết", "Đắp\nngười tuyết\nbăng giá", "Giao\nngười tuyết", "Giao\nngười tuyết\nbăng giá",
                                        "Đóng");
                            case 1 ->
                                createOtherMenu(player, ConstNpc.CHUC_NANG_SAO_PHA_LE, "Ta có thể giúp gì cho ngươi?", "Ép sao\ntrang bị", "Pha lê\nhóa\ntrang bị");
                            case 2 ->
                                ChangeMapService.gI().changeMapNonSpaceship(player, 112, 200 + Util.nextInt(-100, 100), 408);
                            case 3 ->
                                CombineService.gI().openTabCombine(player, CombineService.NANG_HUY_DIET_LEN_SKH);
                            case 4 ->
                                ChangeMapService.gI().changeMapNonSpaceship(player, 181, 1500 + Util.nextInt(0, 100), 264);
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.CHUC_NANG_SU_KIEN_NOEL) {
                        switch (select) {
                            case 0: {
                                Item tangtuyet = InventoryService.gI().findItemBagByTemp(player, 1207);
                                Item muicarot = InventoryService.gI().findItemBagByTemp(player, 1206);
                                Item taygo = InventoryService.gI().findItemBagByTemp(player, 1208);
                                Item khanchoang = InventoryService.gI().findItemBagByTemp(player, 1209);

                                if ((tangtuyet != null && tangtuyet.quantity >= 10)
                                        && (muicarot != null && muicarot.quantity >= 1)
                                        && (taygo != null && taygo.quantity >= 2)
                                        && (khanchoang != null && khanchoang.quantity >= 5)) {
                                    createOtherMenu(player, ConstNpc.MENU_DOI_VPSK,
                                            "|2|Để làm ra 1 người tuyết cần\n"
                                            + "|1|Tảng tuyết " + tangtuyet.quantity + "/10\n"
                                            + "Mũi cà rốt " + muicarot.quantity + "/1\n"
                                            + "Tay gỗ " + taygo.quantity + "/2\n"
                                            + "Khăn choàng " + khanchoang.quantity + "/5\n"
                                            + "|4%", "Đồng ý", "Từ chối");
                                    break;
                                } else {
                                    String NpcSay = "|2|Để làm ra 1 người tuyết cần\n";
                                    if (tangtuyet == null) {
                                        NpcSay += "|7|Tảng tuyết " + "0/10\n";
                                    } else {
                                        NpcSay += "|1|Tảng tuyết " + tangtuyet.quantity + "/10\n";
                                    }
                                    if (muicarot == null) {
                                        NpcSay += "|7|Mũi cà rốt " + "0/1\n";
                                    } else {
                                        NpcSay += "|1|Mũi cà rốt" + muicarot.quantity + "/1\n";
                                    }
                                    if (taygo == null) {
                                        NpcSay += "|7|Tay gỗ " + "0/2\n";
                                    } else {
                                        NpcSay += "|1|Tay gỗ " + taygo.quantity + "/2\n";
                                    }
                                    if (khanchoang == null) {
                                        NpcSay += "|7|Khăn choàng " + "0/5\n";
                                    } else {
                                        NpcSay += "|1|Khăn choàng " + khanchoang.quantity + "/5\n";
                                    }
                                    NpcSay += "|7|%\n";
                                    createOtherMenu(player, ConstNpc.MENU_DOI_VPSK_2,
                                            NpcSay, "Từ chối");
                                }
                            }
                            break;
                            case 1: {
                                Item tangtuyet = InventoryService.gI().findItemBagByTemp(player, 1207);
                                Item muicarot = InventoryService.gI().findItemBagByTemp(player, 1206);
                                Item taygo = InventoryService.gI().findItemBagByTemp(player, 1208);
                                Item khanchoang = InventoryService.gI().findItemBagByTemp(player, 1209);
                                Item nonnoel = InventoryService.gI().findItemBagByTemp(player, 1212);

                                if ((tangtuyet != null && tangtuyet.quantity >= 10)
                                        && (muicarot != null && muicarot.quantity >= 1)
                                        && (taygo != null && taygo.quantity >= 2)
                                        && (khanchoang != null && khanchoang.quantity >= 5)
                                        && (nonnoel != null && nonnoel.quantity >= 1)) {
                                    createOtherMenu(player, ConstNpc.MENU_DOI_VPSK_1,
                                            "|2|Để làm ra 1 người tuyết băng giá cần\n"
                                            + "|1|Tảng tuyết " + tangtuyet.quantity + "/10\n"
                                            + "Mũi cà rốt " + muicarot.quantity + "/1\n"
                                            + "Tay gỗ " + taygo.quantity + "/2\n"
                                            + "Khăn choàng " + khanchoang.quantity + "/10\n"
                                            + "Nón Noel " + nonnoel.quantity + "/1\n"
                                            + "|4%", "Đồng ý", "Từ chối");
                                    break;
                                } else {
                                    String NpcSay = "|2|Để làm ra 1 người tuyết băng giá cần\n";
                                    if (tangtuyet == null) {
                                        NpcSay += "|7|Tảng tuyết " + "0/10\n";
                                    } else {
                                        NpcSay += "|1|Tảng tuyết " + tangtuyet.quantity + "/10\n";
                                    }
                                    if (muicarot == null) {
                                        NpcSay += "|7|Mũi cà rốt " + "0/1\n";
                                    } else {
                                        NpcSay += "|1|Mũi cà rốt" + muicarot.quantity + "/1\n";
                                    }
                                    if (taygo == null) {
                                        NpcSay += "|7|Tay gỗ " + "0/2\n";
                                    } else {
                                        NpcSay += "|1|Tay gỗ " + taygo.quantity + "/2\n";
                                    }
                                    if (khanchoang == null) {
                                        NpcSay += "|7|Khăn choàng " + "0/10\n";
                                    } else {
                                        NpcSay += "|1|Khăn choàng " + khanchoang.quantity + "/10\n";
                                    }
                                    if (nonnoel == null) {
                                        NpcSay += "|7|Nón Noel " + "0/1\n";
                                    } else {
                                        NpcSay += "|1|Nón Noel " + nonnoel.quantity + "/1\n";
                                    }
                                    NpcSay += "|7|%\n";
                                    createOtherMenu(player, ConstNpc.MENU_DOI_VPSK_2,
                                            NpcSay, "Từ chối");
                                }
                            }
                            break;
                            case 2:
                                createOtherMenu(player, ConstNpc.MENI_XEM_DIEM,
                                        "|7|SỰ KIỆN GIAO NGƯỜI TUYẾT NOEL\n"
                                        + "|7|Hãy đem người tuyết đến ta ở vách núi sau làng",
                                        "Đóng");
                                break;
                            case 3:
                                createOtherMenu(player, ConstNpc.MENI_XEM_DIEM,
                                        "|7|SỰ KIỆN GIAO NGƯỜI TUYẾT NOEL\n"
                                        + "|7|Hãy đem người tuyết đến ta ở vách núi sau làng",
                                        "Đóng");
                                break;
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_DOI_VPSK_1) {
                        switch (select) {
                            case 0: {
                                Item tangtuyet = InventoryService.gI().findItemBagByTemp(player, 1207);
                                Item muicarot = InventoryService.gI().findItemBagByTemp(player, 1206);
                                Item taygo = InventoryService.gI().findItemBagByTemp(player, 1208);
                                Item khanchoang = InventoryService.gI().findItemBagByTemp(player, 1209);
                                Item nonnoel = InventoryService.gI().findItemBagByTemp(player, 1212);

                                short daykimtuyen = 1211;
                                Item sachTuyetKy = ItemService.gI().createNewItem((short) daykimtuyen);
                                sachTuyetKy.itemOptions.add(new Item.ItemOption(30, 0));
                                sachTuyetKy.itemOptions.add(new Item.ItemOption(87, 0));
                                sachTuyetKy.itemOptions.add(new Item.ItemOption(93, 25));
                                if (Util.isTrue(100, 100)) {
                                    try { // send effect susscess
                                        Message msg = new Message(-81);
                                        msg.writer().writeByte(0);
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeShort(tempId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(1);
                                        msg.writer().writeByte(2);
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, tangtuyet));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, muicarot));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, taygo));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, khanchoang));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, nonnoel));
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(7);
                                        msg.writer().writeShort(sachTuyetKy.template.iconID);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    } catch (Exception e) {
                                        System.out.println("lỗi 4");
                                    }
                                    InventoryService.gI().addItemList(player.inventory.itemsBag, sachTuyetKy);
                                    InventoryService.gI().subQuantityItemsBag(player, tangtuyet, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, muicarot, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, taygo, 2);
                                    InventoryService.gI().subQuantityItemsBag(player, khanchoang, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, nonnoel, 1);
                                    InventoryService.gI().sendItemBags(player);
                                    return;
                                } else {
                                    try { // send effect faile
                                        Message msg = new Message(-81);
                                        msg.writer().writeByte(0);
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeShort(tempId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(1);
                                        msg.writer().writeByte(2);
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, tangtuyet));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, muicarot));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, taygo));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, khanchoang));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, nonnoel));
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(8);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    } catch (Exception e) {
                                        Logger.logException(NpcFactory.class, e, "Lỗi VPSK");
                                    }
                                    InventoryService.gI().addItemList(player.inventory.itemsBag, sachTuyetKy);
                                    InventoryService.gI().subQuantityItemsBag(player, tangtuyet, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, muicarot, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, taygo, 2);
                                    InventoryService.gI().subQuantityItemsBag(player, khanchoang, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, nonnoel, 1);
                                    InventoryService.gI().sendItemBags(player);
                                }
                            }
                            break;
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_DOI_VPSK) {
                        switch (select) {
                            case 0: {
                                Item tangtuyet = InventoryService.gI().findItemBagByTemp(player, 1207);
                                Item muicarot = InventoryService.gI().findItemBagByTemp(player, 1206);
                                Item taygo = InventoryService.gI().findItemBagByTemp(player, 1208);
                                Item khanchoang = InventoryService.gI().findItemBagByTemp(player, 1209);

                                short daykimtuyen = 1210;
                                Item sachTuyetKy = ItemService.gI().createNewItem((short) daykimtuyen);
                                sachTuyetKy.itemOptions.add(new Item.ItemOption(30, 0));
                                sachTuyetKy.itemOptions.add(new Item.ItemOption(86, 0));
                                sachTuyetKy.itemOptions.add(new Item.ItemOption(93, 25));
                                if (Util.isTrue(100, 100)) {
                                    try { // send effect susscess
                                        Message msg = new Message(-81);
                                        msg.writer().writeByte(0);
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeShort(tempId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(1);
                                        msg.writer().writeByte(2);
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, tangtuyet));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, muicarot));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, taygo));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, khanchoang));
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(7);
                                        msg.writer().writeShort(sachTuyetKy.template.iconID);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    } catch (Exception e) {
                                        System.out.println("lỗi 4");
                                    }
                                    InventoryService.gI().addItemList(player.inventory.itemsBag, sachTuyetKy);
                                    InventoryService.gI().subQuantityItemsBag(player, tangtuyet, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, muicarot, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, taygo, 2);
                                    InventoryService.gI().subQuantityItemsBag(player, khanchoang, 5);
                                    InventoryService.gI().sendItemBags(player);
                                    return;
                                } else {
                                    try { // send effect faile
                                        Message msg = new Message(-81);
                                        msg.writer().writeByte(0);
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeShort(tempId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(1);
                                        msg.writer().writeByte(2);
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, tangtuyet));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, muicarot));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, taygo));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, khanchoang));
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(8);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    } catch (Exception e) {
                                        Logger.logException(NpcFactory.class, e, "Lỗi VPSK");
                                    }
                                    InventoryService.gI().addItemList(player.inventory.itemsBag, sachTuyetKy);
                                    InventoryService.gI().subQuantityItemsBag(player, tangtuyet, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, muicarot, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, taygo, 2);
                                    InventoryService.gI().subQuantityItemsBag(player, khanchoang, 5);
                                    InventoryService.gI().sendItemBags(player);
                                }
                            }
                            break;
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.CHUC_NANG_SAO_PHA_LE) {
                        switch (select) {
                            case 0 ->
                                CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                            case 1 ->
                                CombineService.gI().openTabCombine(player, CombineService.PHA_LE_HOA_TRANG_BI);
                        }
//                    } else if (player.idMark.getIndexMenu() == ConstNpc.CHUC_NANG_CHUYEN_HOA) {
//                        switch (select) {
//                            case 0:
//                                CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_BANG_VANG);
//                            case 1:
//                                CombineService.gI().openTabCombine(player, CombineService.CHUYEN_HOA_BANG_NGOC);
//                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combineNew.typeCombine) {
                            case CombineService.EP_SAO_TRANG_BI, CombineService.PHA_LE_HOA_TRANG_BI, CombineService.NANG_HUY_DIET_LEN_SKH -> {
                                switch (select) {
                                    case 0 ->
                                        CombineService.gI().startCombine(player);
                                    case 1 ->
                                        CombineService.gI().startCombineVip(player, 10);
                                    case 2 ->
                                        CombineService.gI().startCombineVip(player, 50);
                                    case 3 ->
                                        CombineService.gI().startCombineVip(player, 100);
                                    default -> {
                                    }
                                }
                            }

                        }
                    }
                }
                case 112 -> {
                    if (player.idMark.isBaseMenu()) {
                        if (player.haveRewardVDST) {
                            switch (select) {
                                case 0 -> {
                                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item item = ItemService.gI().createNewItem((short) (Util.nextInt(19, 20)));
                                        item.itemOptions.add(new Item.ItemOption(93, 1));
                                        InventoryService.gI().addItemBag(player, item);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                        player.haveRewardVDST = false;
                                    } else {
                                        Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống, không thể nhặt thêm");
                                    }
                                }
                                case 1 -> {
                                    if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                                        Item item = ItemService.gI().createNewItem((short) 226);
                                        item.itemOptions.add(new Item.ItemOption(93, 1));
                                        InventoryService.gI().addItemBag(player, item);
                                        InventoryService.gI().sendItemBags(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
                                        player.haveRewardVDST = false;
                                    } else {
                                        Service.gI().sendThongBao(player, "Hành trang không còn chỗ trống, không thể nhặt thêm");
                                    }
                                }
                            }
                            return;
                        }
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            if (DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().equals(player)) {
                                switch (select) {
                                    case 0 -> {
                                    }
                                    case 1 ->
                                        this.npcChat("Không thể thực hiện");
                                    case 2 -> {
                                    }
                                    case 3 ->
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                }
                                return;
                            }
                            switch (select) {
                                case 0 -> {
                                }
                                case 1 ->
                                    this.createOtherMenu(player, ConstNpc.DAT_CUOC_HAT_MIT,
                                            "Phí bình chọn là 1 triệu vàng\nkhi trận đấu kết thúc\n90% tổng tiền bình chọn sẽ chia đều cho phe bình chọn chính xác",
                                            "Bình chọn cho " + DeathOrAliveArenaManager.gI().getVDST(player.zone).getPlayer().name + " (" + DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocPlayer() + ")",
                                            "Bình chọn cho hạt mít (" + DeathOrAliveArenaManager.gI().getVDST(player.zone).getCuocBaHatMit() + ")");
                                case 2 ->
                                    DeathOrAliveArenaService.gI().startChallenge(player);
                                case 3 -> {
                                }
                                case 4 ->
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                            }
                            return;
                        }
                        switch (select) {
                            case 0 -> {
                            }
                            case 1 ->
                                DeathOrAliveArenaService.gI().startChallenge(player);
                            case 2 -> {
                            }
                            case 3 ->
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.DAT_CUOC_HAT_MIT) {
                        if (DeathOrAliveArenaManager.gI().getVDST(player.zone) != null) {
                            switch (select) {
                                case 0 -> {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocPlayer(vdst.getCuocPlayer() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonPlayer++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(1_000_000 - player.inventory.gold) + " vàng nữa");
                                    }
                                }
                                case 1 -> {
                                    if (player.inventory.gold >= 1_000_000) {
                                        DeathOrAliveArena vdst = DeathOrAliveArenaManager.gI().getVDST(player.zone);
                                        vdst.setCuocBaHatMit(vdst.getCuocBaHatMit() + 1);
                                        vdst.addBinhChon(player);
                                        player.binhChonHatMit++;
                                        player.zoneBinhChon = player.zone;
                                        player.inventory.gold -= 1_000_000;
                                        Service.gI().sendMoney(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(1_000_000 - player.inventory.gold) + " vàng nữa");
                                    }
                                }
                            }
                        }
                    }
                }
                case 174 -> {
                    if (player.idMark.isBaseMenu()) {
                        switch (select) {
                            case 0 ->
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    }
                }
                case 181 -> {
                    if (player.idMark.isBaseMenu()) {
                        switch (select) {
                            case 0 ->
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                        }
                    }
                }
                case 42, 43, 44, 84 -> {
                    if (player.idMark.isBaseMenu()) {
                        if (player.luotNhanBuaMienPhi == 1) {
                            switch (select) {
                                case 0:
                                    if (player.luotNhanBuaMienPhi == 1) {
                                        int idItem = Util.nextInt(213, 219);
                                        player.charms.addTimeCharms(idItem, 60);
                                        Item bua = ItemService.gI().createNewItem((short) idItem);
                                        player.luotNhanBuaMienPhi = 0;
                                        Service.gI().sendThongBao(player, "Bạn vừa nhận thưởng " + bua.getName());
                                    } else {
                                        Service.gI().sendThongBao(player, "Hôm nay bạn đã nhận bùa miễn phí rồi!!!");
                                    }
                                    break;
                                case 1:
                                    createOtherMenu(player, ConstNpc.SACH_TUYET_KY, "Ta có thể giúp gì cho ngươi ?",
                                            "Đóng thành\nSách cũ",
                                            "Đổi Sách\nTuyệt kỹ",
                                            "Giám định\nSách",
                                            "Tẩy\nSách",
                                            "Nâng cấp\nSách\nTuyệt kỹ",
                                            "Hồi phục\nSách",
                                            "Phân rã\nSách");

                                    break;
                                case 2: // shop bùa
                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                            + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                            "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng",
                                            "Bùa\n  Đệ tử Mabư\n 1 giờ", "Đóng");
                                    break;
                                case 3:
                                    CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_VAT_PHAM);
                                    break;
                                case 4:
                                    if (InventoryService.gI().findItemBongTaiCap2(player)) {
                                        CombineService.gI().openTabCombine(player, CombineService.NANG_CHI_SO_BONG_TAI);
                                    } else {
                                        CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_BONG_TAI);
                                    }
                                    break;
                                case 5: // nâng cấp bông tai
                                    CombineService.gI().openTabCombine(player, CombineService.LAM_PHEP_NHAP_DA);
                                    break;
                                case 6: // làm phép nhập đá
                                    CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                                    break;
                                case 7:
                                    if (InventoryService.gI().findItemNguoituyet(player)) {
                                        Item item = InventoryService.gI().findItemBag(player, 1210);
                                        if (item != null && item.quantity >= 1) {
                                            RewardService.gI().rewardNguoiTuyet(player);
                                            InventoryService.gI().subQuantityItemsBag(player, item, 1);
                                        }
                                    } else if (InventoryService.gI().findItemNguoituyetbanggia(player)) {
                                        Item item = InventoryService.gI().findItemBag(player, 1211);
                                        if (item != null && item.quantity >= 1) {
                                            RewardService.gI().rewardNguoiTuyetBangGia(player);
                                            InventoryService.gI().subQuantityItemsBag(player, item, 1);
                                        }
                                    }
                                    break;
                                case 8:
                                    if (InventoryService.gI().findItemNguoituyetbanggia(player)) {
                                        Item item = InventoryService.gI().findItemBag(player, 1211);
                                        if (item != null && item.quantity >= 1) {
                                            RewardService.gI().rewardNguoiTuyetBangGia(player);
                                            InventoryService.gI().sendItemBags(player);
                                            InventoryService.gI().subQuantityItemsBag(player, item, 1);
                                        }
                                    }
                                    break;

                            }
                        } else {
                            switch (select) {
                                case 0:
                                    createOtherMenu(player, ConstNpc.SACH_TUYET_KY, "Ta có thể giúp gì cho ngươi ?",
                                            "Đóng thành\nSách cũ",
                                            "Đổi Sách\nTuyệt kỹ",
                                            "Giám định\nSách",
                                            "Tẩy\nSách",
                                            "Nâng cấp\nSách\nTuyệt kỹ",
                                            "Hồi phục\nSách",
                                            "Phân rã\nSách");

                                    break;
                                case 1: // shop bùa
                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                            + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                            "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng",
                                            "Bùa\n  Đệ tử Mabư\n 1 giờ", "Đóng");
                                    break;
                                case 2:
                                    CombineService.gI().openTabCombine(player,
                                            CombineService.NANG_CAP_VAT_PHAM);
                                    break;
                                case 3:
                                    if (InventoryService.gI().findItemBongTaiCap2(player)) {
                                        CombineService.gI().openTabCombine(player, CombineService.NANG_CHI_SO_BONG_TAI);
                                    } else {
                                        CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_BONG_TAI);
                                    }
                                    break;
                                case 4: // nâng cấp bông tai
                                    CombineService.gI().openTabCombine(player, CombineService.LAM_PHEP_NHAP_DA);
                                    break;
                                case 5: // làm phép nhập đá
                                    CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                                    break;
                                case 6:
                                    if (InventoryService.gI().findItemNguoituyet(player)) {
                                        Item item = InventoryService.gI().findItemBag(player, 1210);
                                        if (item != null && item.quantity >= 1) {
                                            RewardService.gI().rewardNguoiTuyet(player);
                                            InventoryService.gI().subQuantityItemsBag(player, item, 1);
                                        }
                                    } else if (InventoryService.gI().findItemNguoituyetbanggia(player)) {
                                        Item item = InventoryService.gI().findItemBag(player, 1211);
                                        if (item != null && item.quantity >= 1) {
                                            RewardService.gI().rewardNguoiTuyetBangGia(player);
                                            InventoryService.gI().subQuantityItemsBag(player, item, 1);
                                        }
                                    }
                                    break;
                                case 7:
                                    if (InventoryService.gI().findItemNguoituyetbanggia(player)) {
                                        Item item = InventoryService.gI().findItemBag(player, 1211);
                                        if (item != null && item.quantity >= 1) {
                                            RewardService.gI().rewardNguoiTuyetBangGia(player);
                                            InventoryService.gI().sendItemBags(player);
                                            InventoryService.gI().subQuantityItemsBag(player, item, 1);
                                        }
                                    }
                                    break;

                            }
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.SACH_TUYET_KY) {
                        switch (select) {
                            case 0:
                                Item trangSachCu = InventoryService.gI().findItemBagByTemp(player, 1188);

                                Item biaSach = InventoryService.gI().findItemBagByTemp(player, 1154);
                                if ((trangSachCu != null && trangSachCu.quantity >= 9999) && (biaSach != null && biaSach.quantity >= 1)) {
                                    createOtherMenu(player, ConstNpc.DONG_THANH_SACH_CU,
                                            "|2|Chế tạo Cuốn sách cũ\n"
                                            + "|1|Trang sách cũ " + trangSachCu.quantity + "/9999\n"
                                            + "Bìa sách " + biaSach.quantity + "/1\n"
                                            + "|4|Tỉ lệ thành công: 20%\n"
                                            + "Thất bại mất 99 trang sách và 1 bìa sách", "Đồng ý", "Từ chối");
                                    break;
                                } else {
                                    String NpcSay = "|2|Chế tạo Cuốn sách cũ\n";
                                    if (trangSachCu == null) {
                                        NpcSay += "|7|Trang sách cũ " + "0/9999\n";
                                    } else {
                                        NpcSay += "|1|Trang sách cũ " + trangSachCu.quantity + "/9999\n";
                                    }
                                    if (biaSach == null) {
                                        NpcSay += "|7|Bìa sách " + "0/1\n";
                                    } else {
                                        NpcSay += "|1|Bìa sách " + biaSach.quantity + "/1\n";
                                    }

                                    NpcSay += "|7|Tỉ lệ thành công: 20%\n";
                                    NpcSay += "|7|Thất bại mất 99 trang sách và 1 bìa sách";
                                    createOtherMenu(player, ConstNpc.DONG_THANH_SACH_CU_2,
                                            NpcSay, "Từ chối");
                                    break;
                                }
                            case 1:
                                Item cuonSachCu = InventoryService.gI().findItemBagByTemp(player, 1187);
                                Item kimBam = InventoryService.gI().findItemBagByTemp(player, 1153);

                                if ((cuonSachCu != null && cuonSachCu.quantity >= 10) && (kimBam != null && kimBam.quantity >= 1)) {
                                    createOtherMenu(player, ConstNpc.DOI_SACH_TUYET_KY,
                                            "|2|Đổi sách tuyệt kỹ 1\n"
                                            + "|1|Cuốn sách cũ " + cuonSachCu.quantity + "/10\n"
                                            + "Kìm bấm giấy " + kimBam.quantity + "/1\n"
                                            + "|4|Tỉ lệ thành công: 20%\n", "Đồng ý", "Từ chối");
                                    break;
                                } else {
                                    String NpcSay = "|2|Đổi sách Tuyệt kỹ 1\n";
                                    if (cuonSachCu == null) {
                                        NpcSay += "|7|Cuốn sách cũ " + "0/10\n";
                                    } else {
                                        NpcSay += "|1|Cuốn sách cũ " + cuonSachCu.quantity + "/10\n";
                                    }
                                    if (kimBam == null) {
                                        NpcSay += "|7|Kìm bấm giấy " + "0/1\n";
                                    } else {
                                        NpcSay += "|1|Kìm bấm giấy " + kimBam.quantity + "/1\n";
                                    }
                                    NpcSay += "|7|Tỉ lệ thành công: 20%\n";
                                    createOtherMenu(player, ConstNpc.DOI_SACH_TUYET_KY_2,
                                            NpcSay, "Từ chối");
                                }
                                break;
                            case 2:// giám định sách
                                CombineService.gI().openTabCombine(player,
                                        CombineService.GIAM_DINH_SACH);
                                break;
                            case 3:// tẩy sách
                                CombineService.gI().openTabCombine(player,
                                        CombineService.TAY_SACH);
                                break;
                            case 4:// nâng cấp sách
                                CombineService.gI().openTabCombine(player,
                                        CombineService.NANG_CAP_SACH_TUYET_KY);
                                break;
                            case 5:// phục hồi sách
                                CombineService.gI().openTabCombine(player,
                                        CombineService.PHUC_HOI_SACH);
                                break;
                            case 6:// phân rã sách
                                CombineService.gI().openTabCombine(player,
                                        CombineService.PHAN_RA_SACH);
                                break;
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.DONG_THANH_SACH_CU) {
                        switch (select) {
                            case 0:

                                Item trangSachCu = InventoryService.gI().findItemBagByTemp(player, 1188);
                                Item biaSach = InventoryService.gI().findItemBagByTemp(player, 1154);
                                Item cuonSachCu = ItemService.gI().createNewItem((short) 1187);
                                if (Util.isTrue(20, 100)) {
                                    cuonSachCu.itemOptions.add(new ItemOption(30, 0));
                                    try { // send effect susscess
                                        Message msg = new Message(-81);
                                        msg.writer().writeByte(0);
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeShort(tempId);
                                        player.sendMessage(msg);
                                        msg.cleanup();

                                        msg = new Message(-81);
                                        msg.writer().writeByte(1);
                                        msg.writer().writeByte(2);
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, trangSachCu));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, biaSach));
                                        player.sendMessage(msg);
                                        msg.cleanup();

                                        msg = new Message(-81);
                                        msg.writer().writeByte(7);
                                        msg.writer().writeShort(cuonSachCu.template.iconID);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        player.sendMessage(msg);
                                        msg.cleanup();

                                    } catch (Exception e) {
                                        System.out.println("lỗi 1");
                                    }

                                    InventoryService.gI().addItemList(player.inventory.itemsBag, cuonSachCu);
                                    InventoryService.gI().subQuantityItemsBag(player, trangSachCu, 9999);
                                    InventoryService.gI().subQuantityItemsBag(player, biaSach, 1);
                                    InventoryService.gI().sendItemBags(player);
                                    return;
                                } else {
                                    try { // send effect faile
                                        Message msg = new Message(-81);
                                        msg.writer().writeByte(0);
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeShort(tempId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(1);
                                        msg.writer().writeByte(2);
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, biaSach));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, trangSachCu));
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(8);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    } catch (Exception e) {
                                        System.out.println("lỗi 2");
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, trangSachCu, 99);
                                    InventoryService.gI().subQuantityItemsBag(player, biaSach, 1);
                                    InventoryService.gI().sendItemBags(player);
                                }
                                return;
                            case 1:
                                break;
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.DOI_SACH_TUYET_KY) {
                        switch (select) {
                            case 0:
                                Item cuonSachCu = InventoryService.gI().findItemBagByTemp(player, 1187);
                                Item kimBam = InventoryService.gI().findItemBagByTemp(player, 1153);

                                short baseValue = 1183;
                                short genderModifier = (player.gender == 0) ? -2 : ((player.gender == 2) ? 2 : (short) 0);

                                Item sachTuyetKy = ItemService.gI().createNewItem((short) (baseValue + genderModifier));

                                if (Util.isTrue(20, 100)) {

                                    sachTuyetKy.itemOptions.add(new ItemOption(224, 0));
                                    sachTuyetKy.itemOptions.add(new ItemOption(21, 40));
                                    sachTuyetKy.itemOptions.add(new ItemOption(30, 0));
                                    sachTuyetKy.itemOptions.add(new ItemOption(87, 1));
                                    sachTuyetKy.itemOptions.add(new ItemOption(225, 5));
                                    sachTuyetKy.itemOptions.add(new ItemOption(226, 1000));
                                    try { // send effect susscess
                                        Message msg = new Message(-81);
                                        msg.writer().writeByte(0);
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeShort(tempId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(1);
                                        msg.writer().writeByte(2);
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, kimBam));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, cuonSachCu));
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(7);
                                        msg.writer().writeShort(sachTuyetKy.template.iconID);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    } catch (Exception e) {
                                        System.out.println("lỗi 4");
                                    }
                                    InventoryService.gI().addItemList(player.inventory.itemsBag, sachTuyetKy);
                                    InventoryService.gI().subQuantityItemsBag(player, cuonSachCu, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, kimBam, 1);
                                    InventoryService.gI().sendItemBags(player);
                                    return;
                                } else {
                                    try { // send effect faile
                                        Message msg = new Message(-81);
                                        msg.writer().writeByte(0);
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeUTF("test");
                                        msg.writer().writeShort(tempId);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(1);
                                        msg.writer().writeByte(2);
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, kimBam));
                                        msg.writer().writeByte(InventoryService.gI().getIndexBag(player, cuonSachCu));
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        msg = new Message(-81);
                                        msg.writer().writeByte(8);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        msg.writer().writeShort(-1);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                    } catch (Exception e) {
                                        Logger.logException(NpcFactory.class, e, "Lỗi sách");
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, cuonSachCu, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, kimBam, 1);
                                    InventoryService.gI().sendItemBags(player);
                                }
                                return;
                            case 1:
                                break;
                        }
                        //làm phép nhập đá
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                        switch (select) {
                            case 0 ->
                                ShopService.gI().opendShop(player, "BUA_1H", true);
                            case 1 ->
                                ShopService.gI().opendShop(player, "BUA_8H", true);
                            case 2 ->
                                ShopService.gI().opendShop(player, "BUA_1M", true);
                        }
                    } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combineNew.typeCombine) {
                            case CombineService.NANG_CAP_VAT_PHAM, CombineService.NANG_CAP_BONG_TAI, CombineService.NANG_CHI_SO_BONG_TAI, CombineService.LAM_PHEP_NHAP_DA, CombineService.NHAP_NGOC_RONG, CombineService.GIAM_DINH_SACH, CombineService.TAY_SACH, CombineService.NANG_CAP_SACH_TUYET_KY, CombineService.PHUC_HOI_SACH, CombineService.PHAN_RA_SACH -> {
                                switch (select) {
                                    case 0 ->
                                        CombineService.gI().startCombine(player);
                                    case 1 ->
                                        CombineService.gI().startCombineVip(player, 10);
                                    case 2 ->
                                        CombineService.gI().startCombineVip(player, 100);
                                    default -> {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
