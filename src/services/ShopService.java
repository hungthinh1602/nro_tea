package services;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import consts.ConstAchievement;
import models.item.Item;
import models.player.Inventory;
import models.player.Player;
import models.shop.ItemShop;
import models.shop.Shop;
import models.shop.TabShop;
import network.io.Message;
import models.item.Item.ItemOption;
import java.util.ArrayList;
import server.Manager;
import services.player.InventoryService;
import utils.Logger;
import utils.Util;

import java.util.List;
import models.player.PlayerEffect;

import services.func.Input;
import services.func.BuyBackService;

public class ShopService {

    private static final byte COST_GOLD = 0;
    private static final byte COST_GEM = 1;
    private static final byte COST_RUBY = 3;
    private static final byte COST_COUPON = 4;

    private static final byte NORMAL_SHOP = 0;
    private static final byte SPEC_SHOP = 3;

    private static ShopService I;

    public static ShopService gI() {
        if (ShopService.I == null) {
            ShopService.I = new ShopService();
        }
        return ShopService.I;
    }

    public void opendShop(Player player, String tagName, boolean allGender) {
        if (tagName.equals("ITEMS_LUCKY_ROUND")) {
            openShopType4(player, tagName, player.inventory.itemsBoxCrackBall);
            return;
        } else if (tagName.equals("ITEMS_DABAN")) {
            openShopType8(player, tagName, player.inventory.itemsDaBan);
            return;
        }
        try {
            Shop shop = this.getShop(tagName);
            shop = this.resolveShop(player, shop, allGender);
            switch (shop.typeShop) {
                case NORMAL_SHOP:
                    openShopType0(player, shop);
                    break;
                case SPEC_SHOP:
                    openShopType3(player, shop);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Service.gI().sendThongBao(player, ex.getMessage());
        }
    }

    private Shop getShop(String tagName) throws Exception {
        for (Shop s : Manager.SHOPS) {
            if (s.tagName != null && s.tagName.equals(tagName)) {
                return s;
            }
        }
        throw new Exception("Shop " + tagName + " không tồn tại!");
    }

    private Shop resolveShop(Player player, Shop shop, boolean allGender) {
        if (shop.tagName != null && (shop.tagName.equals("BUA_1H")
                || shop.tagName.equals("BUA_8H") || shop.tagName.equals("BUA_1M"))) {
            return this.resolveShopBua(player, new Shop(shop));
        }
        return allGender ? new Shop(shop) : new Shop(shop, player.gender);
    }

    private Shop resolveShopBua(Player player, Shop s) {
        for (TabShop tabShop : s.tabShops) {
            for (ItemShop item : tabShop.itemShops) {
                long min = 0;
                switch (item.temp.id) {
                    case 213:
                        long timeTriTue = player.charms.tdTriTue;
                        long current = System.currentTimeMillis();
                        min = (timeTriTue - current) / 60000;

                        break;
                    case 214:
                        min = (player.charms.tdManhMe - System.currentTimeMillis()) / 60000;
                        break;
                    case 215:
                        min = (player.charms.tdDaTrau - System.currentTimeMillis()) / 60000;
                        break;
                    case 216:
                        min = (player.charms.tdOaiHung - System.currentTimeMillis()) / 60000;
                        break;
                    case 217:
                        min = (player.charms.tdBatTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 218:
                        min = (player.charms.tdDeoDai - System.currentTimeMillis()) / 60000;
                        break;
                    case 219:
                        min = (player.charms.tdThuHut - System.currentTimeMillis()) / 60000;
                        break;
                    case 522:
                        min = (player.charms.tdDeTu - System.currentTimeMillis()) / 60000;
                        break;
                    case 671:
                        min = (player.charms.tdTriTue3 - System.currentTimeMillis()) / 60000;
                        break;
                    case 672:
                        min = (player.charms.tdTriTue4 - System.currentTimeMillis()) / 60000;
                        break;
                }
                if (min > 0) {
                    item.options.clear();
                    if (min >= 1440) {
                        item.options.add(new Item.ItemOption(63, (int) min / 1440));
                    } else if (min >= 60) {
                        item.options.add(new Item.ItemOption(64, (int) min / 60));
                    } else {
                        item.options.add(new Item.ItemOption(65, (int) min));
                    }
                }
            }
        }
        return s;
    }

    private void openShopType0(Player player, Shop shop) {
        if (shop != null) {
            player.idMark.setShopOpen(shop);
            player.idMark.setTagNameShop(shop.tagName);
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(NORMAL_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        if (itemShop.typeSell == COST_GOLD) {
                            msg.writer().writeInt(itemShop.cost);
                            msg.writer().writeInt(0);
                        } else if (itemShop.typeSell == COST_GEM) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(itemShop.cost);
                        } else if (itemShop.typeSell == COST_RUBY) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(itemShop.cost);
                        } else if (itemShop.typeSell == COST_COUPON) {
                            msg.writer().writeInt(0);
                            msg.writer().writeInt(itemShop.cost);
                        }
                        msg.writer().writeByte(itemShop.options.size());
                        for (Item.ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        if (itemShop.temp.type == 5) {
                            msg.writer().writeByte(1);
                            msg.writer().writeShort(itemShop.temp.head);
                            msg.writer().writeShort(itemShop.temp.body);
                            msg.writer().writeShort(itemShop.temp.leg);
                            msg.writer().writeShort(-1);
                        } else {
                            msg.writer().writeByte(0);
                        }
                    }
                }
                player.sendMessage(msg);
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    private void openShopType3(Player player, Shop shop) {
        player.idMark.setShopOpen(shop);
        player.idMark.setTagNameShop(shop.tagName);
        if (shop != null) {
            Message msg = null;
            try {
                msg = new Message(-44);
                msg.writer().writeByte(SPEC_SHOP);
                msg.writer().writeByte(shop.tabShops.size());
                for (TabShop tab : shop.tabShops) {
                    msg.writer().writeUTF(tab.name);
                    msg.writer().writeByte(tab.itemShops.size());
                    for (ItemShop itemShop : tab.itemShops) {
                        msg.writer().writeShort(itemShop.temp.id);
                        msg.writer().writeShort(itemShop.iconSpec);
                        msg.writer().writeInt(itemShop.cost);
                        msg.writer().writeByte(itemShop.options.size());
                        for (Item.ItemOption option : itemShop.options) {
                            msg.writer().writeByte(option.optionTemplate.id);
                            msg.writer().writeShort(option.param);
                        }
                        msg.writer().writeByte(itemShop.isNew ? 1 : 0);
                        if (itemShop.temp.type == 5) {
                            msg.writer().writeByte(1);
                            msg.writer().writeShort(itemShop.temp.head);
                            msg.writer().writeShort(itemShop.temp.body);
                            msg.writer().writeShort(itemShop.temp.leg);
                            msg.writer().writeShort(-1);
                        } else {
                            msg.writer().writeByte(0);
                        }
                    }
                }
                player.sendMessage(msg);
            } catch (Exception e) {
                Logger.logException(ShopService.class, e);
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    private void openShopType4(Player player, String tagName, List<Item> items) {
        if (items == null) {
            return;
        }
        player.idMark.setTagNameShop(tagName);
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(4);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Phần\nthưởng");
            msg.writer().writeByte(items.size());
            for (Item item : items) {
                msg.writer().writeShort(item.template.id);
                msg.writer().writeUTF("Cậu Bé Rồng");
                msg.writer().writeByte(item.itemOptions.size() + 1);
                for (Item.ItemOption io : item.itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeShort(io.param);
                }
                //số lượng
                msg.writer().writeByte(31);
                msg.writer().writeShort(item.quantity);
                //
                msg.writer().writeByte(1);
                if (item.template.type == 5) {
                    msg.writer().writeByte(1);
                    msg.writer().writeShort(item.template.head);
                    msg.writer().writeShort(item.template.body);
                    msg.writer().writeShort(item.template.leg);
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeByte(0);
                }
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void openShopType8(Player player, String tagName, List<Item> items) {
        if (items == null) {
            return;
        }
        player.idMark.setTagNameShop(tagName);
        Message msg = null;
        try {
            msg = new Message(-44);
            msg.writer().writeByte(8);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Mua lại");
            msg.writer().writeByte(items.size());
            for (Item item : items) {
                int giamualaingoc = item.template.gem / 2;
                int giamualaivang = giamualaingoc == 0 ? (int) item.template.gold / 2 > 0 ? (int) item.template.gold / 2 : item.quantity * 100 : 0;
                msg.writer().writeShort(item.template.id);
                msg.writer().writeInt(giamualaivang);
                msg.writer().writeInt(giamualaingoc);
                msg.writer().writeInt(item.quantity);
                msg.writer().writeByte(item.itemOptions.size());
                for (Item.ItemOption io : item.itemOptions) {
                    msg.writer().writeByte(io.optionTemplate.id);
                    msg.writer().writeShort(io.param);
                }
                msg.writer().writeByte(0);
                if (item.template.type == 5) {
                    msg.writer().writeByte(1);
                    msg.writer().writeShort(item.template.head);
                    msg.writer().writeShort(item.template.body);
                    msg.writer().writeShort(item.template.leg);
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeByte(0);
                }
            }
            player.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void takeItem(Player player, byte type, int tempId) {
        String tagName = player.idMark.getTagNameShop();
        if (tagName == null || tagName.length() <= 0) {
            return;
        }

        // Xử lý khi người chơi mua items từ các tag khác nhau
        if (tagName.equals("ITEMS_LUCKY_ROUND")) {
            getItemSideBoxLuckyRound(player, player.inventory.itemsBoxCrackBall, type, tempId);
            return;
        } else if (tagName.equals("ITEMS_REWARD")) {
            return;

        } else if (tagName.equals("ITEMS_DABAN")) {
            buyItemDaBan(player, player.inventory.itemsDaBan, type, tempId);
            return;
        } else if (tagName.equals("BILL")) {
            // Thêm điều kiện kiểm tra trước khi thực hiện mua item
            if (isEligibleForBill(player, tempId)) {
                buyItemHD(player, tempId); // Tiến hành mua item nếu thỏa mãn điều kiện
            } else {
                Service.gI().sendThongBao(player, "Không đủ điều kiện để mua item này.");
            }
            return;
        }

        // Kiểm tra nếu không có shop mở
        if (player.idMark.getShopOpen() == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }

        // Các loại tag khác
        if (tagName.equals("BUA_1H") || tagName.equals("BUA_8H") || tagName.equals("BUA_1M")) {
            buyItemBua(player, tempId);
        } else if (tagName.equals("SANTA_HEAD")) {
            Item itS = ItemService.gI().createNewItem((short) tempId);
            player.head = (short) itS.template.head;
        } else {
            buyItem(player, tempId);
        }

        // Cập nhật lại số tiền của người chơi sau giao dịch
        Service.gI().sendMoney(player);
    }

// Phương thức kiểm tra điều kiện trước khi mua item với tag "BILL"
    private boolean isEligibleForBill(Player player, int tempId) {
        // Kiểm tra xem người chơi có đủ tiền và các điều kiện khác trước khi cho phép mua item
        Shop shop = player.idMark.getShopOpen();
        ItemShop itemShop = shop.getItemShop(tempId);
        if (itemShop == null) {
            return false; // Nếu không có item trong shop
        }

// Kiểm tra nếu không có đủ set thần (level 13)
        boolean hasSetThan = player.inventory.itemsBody.stream()
                .anyMatch(it -> it != null && it.template != null && it.template.level == 13);

        if (!hasSetThan) {
            Service.gI().sendThongBao(player, "Không có đủ set thần.");
            return false; // Nếu không có đủ set thần
        }
        Item item = ItemService.gI().createItemFromItemShop(itemShop);

// Kiểm tra nếu item yêu cầu thức ăn (level 14)
        if (item.template.level == 14) {
            // Kiểm tra nếu người chơi có đủ ít nhất 99 thức ăn với các id tương ứng
            boolean hasEnoughFood = player.inventory.itemsBag.stream()
                    .filter(it -> it != null && it.template != null
                    && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665 || it.template.id == 666 || it.template.id == 667))
                    .anyMatch(it -> it.quantity >= 99);

            if (!hasEnoughFood) {
                Service.gI().sendThongBao(player, "Không có đủ thức ăn.");
                return false; // Nếu không có đủ thức ăn
            }
        }

        // Kiểm tra không gian hành trang
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBao(player, "Hành trang đầy, không thể chứa thêm.");
            return false; // Nếu không đủ không gian hành trang
        }

        return true; // Nếu tất cả các điều kiện đã thỏa mãn
    }

    private boolean subMoneyByItemShop(Player player, ItemShop is) {
        int gold = 0;
        int gem = 0;
        int ruby = 0;
        int coupon = 0;
        switch (is.typeSell) {
            case COST_GOLD ->
                gold = is.cost;
            case COST_GEM ->
                gem = is.cost;
            case COST_RUBY ->
                ruby = is.cost;
            case COST_COUPON ->
                coupon = is.cost;

        }
        if (player.inventory.gold < gold) {
            Service.gI().sendThongBao(player, "Bạn không có đủ vàng");
            return false;
        } else if (player.inventory.gem < gem) {
            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc");
            return false;
        } else if (player.inventory.ruby < ruby) {
            Service.gI().sendThongBao(player, "Bạn không có đủ hồng ngọc");
            return false;
        } else if (player.inventory.coupon < coupon) {
            Service.gI().sendThongBao(player, "Bạn không có đủ điểm");
            return false;
        }
        player.inventory.gold -= gold;
        player.inventory.gem -= gem;
        player.inventory.ruby -= ruby;
        player.inventory.coupon -= coupon;
        return true;
    }

    private boolean subMoneyByItemShopV2(Player player, ItemShop is) {
        int gold = 0;
        int gem = 0;
        int ruby = 0;
        int coupon = 0;
        switch (is.typeSell) {
            case COST_GOLD ->
                gold = is.cost;
            case COST_GEM ->
                gem = is.cost;
            case COST_RUBY ->
                ruby = is.cost;
            case COST_COUPON ->
                coupon = is.cost;

        }
        if (player.inventory.gold < gold) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ vàng, còn thiếu " + Util.numberToMoney(player.inventory.gold - gold));
            return false;
        } else if (player.inventory.gem < gem) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ ngọc, còn thiếu " + Util.numberToMoney(player.inventory.gem - gem));
            return false;
        } else if (player.inventory.ruby < ruby) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ hồng ngọc, còn thiếu " + Util.numberToMoney(player.inventory.ruby - ruby));
            return false;
        } else if (player.inventory.coupon < coupon) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ điểm, còn thiếu " + Util.numberToMoney(player.inventory.coupon - coupon));
            return false;
        }
        player.inventory.gold -= gold;
        player.inventory.gem -= gem;
        player.inventory.ruby -= ruby;
        player.inventory.coupon -= coupon;
        Service.gI().sendMoney(player);
        return true;
    }

    /**
     * Mua bùa
     *
     * @param player người chơi
     * @param itemTempId id template vật phẩm
     */
    private void buyItemBua(Player player, int itemTempId) {
        Shop shop = player.idMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (!subMoneyByItemShop(player, is)) {
            return;
        }
        InventoryService.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
        InventoryService.gI().sendItemBags(player);
        opendShop(player, shop.tagName, true);
    }

    /**
     * Mua vật phẩm trong cửa hàng
     *
     * @param player người chơi
     * @param itemTempId id template vật phẩm
     */
    public void buyItem(Player player, int itemTempId) {
        Shop shop = player.idMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);

        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }

        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy");
            return;
        }

        if (itemTempId == 711 && !InventoryService.gI().findItemSkinQuyLaoKame(player)) {
            Service.gI().sendThongBao(player, "Bạn phải có cải trang thành Quy Lão Kame mới có thể đổi.");
            return;
        }

        if (shop.typeShop == ShopService.NORMAL_SHOP) {
            if (!subMoneyByItemShop(player, is)) {
                return;
            }
        } else if (shop.typeShop == ShopService.SPEC_SHOP) {
            if (!this.subIemByItemShop(player, is)) {
                return;
            }
        }
        if (shop.tagName.equals("SANTA_PGG")) {
            Item pGG = InventoryService.gI().findItem(player.inventory.itemsBag, 459);
            if (pGG != null) {
                //  Item item = ItemService.gI().createItemFromItemShop(is);
                InventoryService.gI().subQuantityItemsBag(player, pGG, 1);
                //   InventoryService.gI().addItemBag(player, item);
                InventoryService.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Đổi thành công ");
            } else {
                Service.gI().sendThongBao(player, "Bạn không có phiếu giảm giá!");
            }
        }
        Item itemShopDanhHieu = ItemService.gI().createItemFromItemShop(is);
        int pointUocRong = PlayerEffect.baseTrumUocRong - player.effect.getPointTrumUocRong();
        int pointSanBoss = PlayerEffect.baseTrumSanBoss - player.effect.getPointTrumSanBoss();
        int pointDapDo = PlayerEffect.baseThanhDapDo - player.effect.getPointThanhDapDo();
        int pointChamChi = PlayerEffect.baseNongDanChamChi - player.effect.getPointNongDanChamChi();
        int pointVeChai = PlayerEffect.baseOngThanVeChai - player.effect.getPointOngThanVeChai();
        int pointCung = PlayerEffect.basePhanCung - player.effect.getPointPhanCung();
        if (is.temp.id == 1160) {
            if (InventoryService.gI().findItemBody(player, 1160) != null || InventoryService.gI().findItemBag(player, 1160) != null || InventoryService.gI().findItemBox(player, 1160) != null) {
                Service.gI().sendThongBao(player, "Bạn nhận được con cặc");
                return;
            }
            int pointDanap = 3000000 - player.getSession().danap;  // Yêu cầu 1 triệu Danap mới được mua
            if (player.getSession().danap >= 3000000) {  // Kiểm tra nếu người chơi có ít nhất 1 triệu Danap
                player.getSession().danap = 3000000;  // Cập nhật Danap của người chơi, có thể thay đổi nếu cần
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Còn thiếu " + pointDanap + " / 3000000 tổng nạp để nhận vật phẩm");
            }
        } else if (is.temp.id == 1162) {
            if (InventoryService.gI().findItemBody(player, 1162) != null || InventoryService.gI().findItemBag(player, 1162) != null || InventoryService.gI().findItemBox(player, 1162) != null) {
                Service.gI().sendThongBao(player, "Bạn nhận được con cặc");
                return;
            }
            int pointDanap = 1000000 - player.getSession().danap;  // Yêu cầu 1 triệu Danap mới được mua
            if (player.getSession().danap >= 1000000) {  // Kiểm tra nếu người chơi có ít nhất 1 triệu Danap
                player.getSession().danap = 1000000;  // Cập nhật Danap của người chơi, có thể thay đổi nếu cần
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Còn thiếu " + pointDanap + " / 1000000 tổng nạp để nhận vật phẩm");
            }
        } else if (is.temp.id == 1163) {
            if (player.effect.isEff(player.effect.getPointTrumUocRong(), PlayerEffect.baseTrumUocRong)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectUocRong(PlayerEffect.baseTrumUocRong);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Còn thiếu " + pointUocRong + " lần ước rồng 1 sao");
            }
        } else if (is.temp.id == 1164) {
            if (player.effect.isEff(player.effect.getPointTrumSanBoss(), PlayerEffect.baseTrumSanBoss)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSanBoss(PlayerEffect.baseTrumSanBoss);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Còn thiếu " + pointSanBoss + " Boss");
            }
        } else if (is.temp.id == 1165) {
            if (player.effect.isEff(player.effect.getPointThanhDapDo(), PlayerEffect.baseThanhDapDo)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectDapDo(PlayerEffect.baseThanhDapDo);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Còn thiếu " + pointDapDo + " lần đập đồ");
            }
        } else if (is.temp.id == 1166) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Sở hữu VIP 3 tại ToriBot để nhận miễn phí!");
            }
        } else if (is.temp.id == 1167) {
            if (player.effect.isEff(player.effect.getPointNongDanChamChi(), PlayerEffect.baseNongDanChamChi)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectChamChi(PlayerEffect.baseNongDanChamChi);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Còn thiếu " + pointChamChi + " nhiệm vụ bò mọng");
            }
        } else if (is.temp.id == 1168) {
            if (player.effect.isEff(player.effect.getPointOngThanVeChai(), PlayerEffect.baseOngThanVeChai)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectVeChai(PlayerEffect.baseOngThanVeChai);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Còn thiếu " + pointVeChai + " điểm");
            }
        } else if (is.temp.id == 1170) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1171) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1172) {
            long sucmanh = 60_000_000_000L - player.nPoint.power;  // Yêu cầu 1 triệu Danap mới được mua
            if (player.nPoint.power >= 60_000_000_000L) {  // Kiểm tra nếu người chơi có ít nhất 1 triệu Danap
                player.nPoint.power = 60_000_000_000L;  // Cập nhật Danap của người chơi, có thể thay đổi nếu cần
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Còn thiếu " + sucmanh + " / 60 tỉ sức mạnh để nhận vật phẩm");
            }
        } else if (is.temp.id == 1173) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1174) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1175) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1176) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1177) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1178) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1179) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else if (is.temp.id == 1169) {
            if (player.effect.isEff(player.effect.getPointBiMocSachTui(), PlayerEffect.baseBiMocSachTui)) {
                InventoryService.gI().addItemBag(player, itemShopDanhHieu);
                InventoryService.gI().sendItemBags(player);
                player.effect.subPointEffectSachTui(PlayerEffect.baseBiMocSachTui);
                Service.gI().sendThongBao(player, "Bạn nhận được " + is.temp.name);
            } else {
                Service.gI().sendThongBao(player, "Đang cập nhật thêm, vui lòng chờ!");
            }
        } else {
            Item item = ItemService.gI().createItemFromItemShop(is);
            InventoryService.gI().addItemBag(player, item);
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);
        }
    }

    private boolean subIemByItemShop(Player pl, ItemShop itemShop) {
        boolean isBuy = false;
        short itSpec = ItemService.gI().getItemIdByIcon((short) itemShop.iconSpec);
        int buySpec = itemShop.cost;
        Item itS = ItemService.gI().createNewItem(itSpec);
        switch (itS.template.id) {
            case 76:
            case 188:
            case 189:
            case 190:
                if (pl.inventory.gold >= buySpec) {
                    pl.inventory.gold -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn Không Đủ Vàng Để Mua Vật Phẩm");
                    isBuy = false;
                }
                break;
            case 861:
                if (pl.inventory.ruby >= buySpec) {
                    pl.inventory.ruby -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn Không Đủ Hồng Ngọc Để Mua Vật Phẩm");
                    isBuy = false;
                }
                break;
            case 77:
                if (pl.inventory.gem >= buySpec) {
                    pl.inventory.gem -= buySpec;
                    isBuy = true;
                } else {
                    Service.gI().sendThongBao(pl, "Bạn Không Đủ Ngọc Xanh Để Mua Vật Phẩm");
                    isBuy = false;
                }
                break;
            default:
                if (InventoryService.gI().findItemBag(pl, itSpec) == null || !InventoryService.gI().findItemBag(pl, itSpec).isNotNullItem()) {
                    Service.gI().sendThongBao(pl, "Không tìm thấy " + itS.template.name);
                    isBuy = false;
                } else if (InventoryService.gI().findItemBag(pl, itSpec).quantity < buySpec) {
                    Service.gI().sendThongBao(pl, "Bạn không có đủ " + buySpec + " " + itS.template.name);
                    isBuy = false;
                } else {
                    InventoryService.gI().subQuantityItemsBag(pl, InventoryService.gI().findItemBag(pl, itSpec), buySpec);
                    isBuy = true;
                }
                break;
        }
        return isBuy;
    }

    public void showConfirmSellItem(Player pl, int where, int index) {
        Item item = null;
        if (where == 0) {
            if (index < 0) {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
                return;
            }
            item = pl.inventory.itemsBody.get(index);
        } else {
            if (pl.getSession().version < 220) {
                index -= (pl.inventory.itemsBody.size() - 7);
            }
            item = pl.inventory.itemsBag.get(index);
        }
        if (item != null && item.isNotNullItem()) {
            if (item.template.id == 570) {
                Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm này");
                return;
            }
            int quantity = item.quantity;
            int cost = item.template.gold;
            if (item.template.id == 457) {
                if (quantity > 1) {
                    Input.gI().createFormBanSLL(pl);
                    return;
                }
                quantity = 1;
            } else {
                cost /= 4;
            }
            if (cost == 0) {
                cost = 1;
            }
            cost *= quantity;

            String text = "Bạn có muốn bán\nx" + quantity
                    + " " + item.template.name + "\nvới giá là " + Util.numberToMoney(cost) + " vàng?";
            Message msg = null;
            try {
                msg = new Message(7);
                msg.writer().writeByte(where);
                msg.writer().writeShort(index);
                msg.writer().writeUTF(text);
                pl.sendMessage(msg);
            } catch (Exception e) {
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }

    public void sellItem(Player pl, int where, int index) {
        if (pl.idMark.getShopOpen() == null || pl.idMark.getTagNameShop() == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (index < 0) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        Item item = null;
        if (where == 0) {
            item = pl.inventory.itemsBody.get(index);
        } else {
            item = pl.inventory.itemsBag.get(index);
        }
        if (item != null) {
            if (item.template.id == 570) {
                Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm này");
                return;
            }
            if (InventoryService.gI().getParam(pl, 93, item.template.id) > 0) {
                Service.gI().sendThongBao(pl, "Bạn không thể bán vật phẩm có hạn sử dụng");
                return;
            }
            int quantity = item.quantity;
            int cost = item.template.gold;
            if (item.template.id == 457) {
                quantity = 1;
            } else {
                cost /= 4;
            }
            if (cost == 0) {
                cost = 1;
            }
            cost *= quantity;

            if (pl.inventory.gold + cost > Inventory.LIMIT_GOLD) {
                Service.gI().sendThongBao(pl, "Vàng sau khi bán vượt quá giới hạn");
                return;
            }
            pl.inventory.gold += cost;
            Service.gI().sendMoney(pl);
            Service.gI().sendThongBao(pl, "Đã bán " + item.template.name
                    + " thu được " + Util.numberToMoney(cost) + " vàng");

            //Add vật phẩm đã bán
            if (item.template.id != 457) {
                BuyBackService.gI().addItem(pl, item);
            }
            if (where == 0) {
                InventoryService.gI().subQuantityItemsBody(pl, item, quantity);
                InventoryService.gI().sendItemBody(pl);
                Service.gI().Send_Caitrang(pl);
            } else {
                InventoryService.gI().subQuantityItemsBag(pl, item, quantity);
                InventoryService.gI().sendItemBags(pl);
            }
            if ("BUNMA".equals(pl.idMark.getTagNameShop())
                    || "DENDE".equals(pl.idMark.getTagNameShop())
                    || "APPULE".equals(pl.idMark.getTagNameShop())) {
                AchievementService.gI().checkDoneTask(pl, ConstAchievement.TRUM_NHAT_VE_CHAI);
            }
        } else {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    private void getItemSideBoxLuckyRound(Player player, List<Item> items, byte type, int index) {
        if (items == null) {
            return;
        }
        if (index < 0 || index >= items.size()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = items.get(index);
        switch (type) {
            case 0: //nhận
                if (item.isNotNullItem()) {
                    if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                        InventoryService.gI().addItemBag(player, item);
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        InventoryService.gI().sendItemBags(player);
                        items.remove(index);
                    } else {
                        Service.gI().sendThongBao(player, "Hành trang đã đầy");
                    }
                } else {
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                }
                break;
            case 1: //xóa
                items.remove(index);
                Service.gI().sendThongBao(player, "Xóa vật phẩm thành công");
                break;
            case 2: //nhận hết
                for (int i = items.size() - 1; i >= 0; i--) {
                    item = items.get(i);
                    if (InventoryService.gI().addItemBag(player, item)) {
                        Service.gI().sendThongBao(player,
                                "Bạn nhận được " + (item.template.id == 189
                                        ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                        items.remove(i);
                    }
                }
                InventoryService.gI().sendItemBags(player);
                break;
        }
        openShopType4(player, player.idMark.getTagNameShop(), items);
    }

    private void buyItemDaBan(Player player, List<Item> items, byte type, int index) {
        if (items == null) {
            return;
        }
        if (index >= items.size()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        Item item = items.get(index);
        int giamualaingoc = item.template.gem / 2;
        int giamualaivang = giamualaingoc == 0 ? (int) item.template.gold / 2 > 0 ? (int) item.template.gold / 2 : item.quantity * 100 : 0;
        if (giamualaivang > 0 && player.inventory.gold < giamualaivang) {
            Service.gI().sendThongBao(player, "Bạn không có đủ vàng!");
            return;
        }
        if (giamualaingoc > 0 && player.inventory.gem < giamualaingoc) {
            Service.gI().sendThongBao(player, "Bạn không có đủ ngọc xanh!");
            return;
        }
        player.inventory.gem -= giamualaingoc;
        player.inventory.gold -= giamualaivang;
        Service.gI().sendMoney(player);
        if (item.isNotNullItem()) {
            if (InventoryService.gI().getCountEmptyBag(player) != 0) {
                InventoryService.gI().addItemBag(player, item);
                Service.gI().sendThongBao(player,
                        "Bạn nhận được " + (item.template.id == 189
                                ? Util.numberToMoney(item.quantity) + " vàng" : item.template.name));
                InventoryService.gI().sendItemBags(player);
                items.remove(index);
            } else {
                Service.gI().sendThongBao(player, "Hành trang đã đầy");
            }
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
        openShopType8(player, player.idMark.getTagNameShop(), items);
    }

    private boolean isFullSetThan(Player player) {
        boolean haveAo = false;
        boolean haveQuan = false;
        boolean haveGang = false;
        boolean HaveGiay = false;
        boolean HaveNhan = false;

        for (Item item : player.inventory.itemsBody) {
            if (item != null && item.template != null && item.template.level == 13) {
                switch (item.template.type) {
                    case 0:
                        haveAo = true;
                        break;
                    case 1:
                        haveQuan = true;
                        break;
                    case 2:
                        haveGang = true;
                        break;
                    case 3:
                        HaveGiay = true;
                        break;
                    case 4:
                        HaveNhan = true;
                        break;
                }
            }
        }

        return haveAo && haveQuan && haveGang && HaveGiay && HaveNhan;
    }

    private void buyItemHD(Player player, int itemTempId) {
        Shop shop = player.idMark.getShopOpen();
        ItemShop is = shop.getItemShop(itemTempId);
        if (is == null) {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
            return;
        }
        if (!isFullSetThan(player)) {
            Service.gI().sendThongBao(player, "Không có đủ set thần");
            return;
        }
        Item item = ItemService.gI().createItemFromItemShop(is);
        if (InventoryService.gI().getCountEmptyBag(player) < 1) {
            Service.gI().sendThongBao(player, "Hành trang đã đầy, không thể chứa thêm.");
            return;
        }
        if (!subMoneyByItemShopV2(player, is)) {
            return;
        }
        if (item.template.level == 14) {
            Item doAn = player.inventory.itemsBag.stream().filter(it -> it != null && it.template != null
                    && (it.template.id == 663 || it.template.id == 664 || it.template.id == 665 || it.template.id == 666 || it.template.id == 667)
                    && it.quantity >= 99).findFirst().orElse(null);

            if (doAn != null) {
                InventoryService.gI().subQuantityItemsBag(player, doAn, 99); // Subtract 99 food
            } else {
                Service.gI().sendThongBao(player, "Không có đủ thức ăn");
                return; // Stop if not enough food
            }
        }

        Item itemremove = null;
        int bodyindex = -1;

        for (int i = 0; i < player.inventory.itemsBody.size(); i++) {
            Item bodyItem = player.inventory.itemsBody.get(i);
            if (bodyItem != null && bodyItem.template != null && bodyItem.template.level == 13 && bodyItem.template.type == item.template.type) {
                itemremove = bodyItem;
                bodyindex = i;
                break;
            }
        }

        if (itemremove == null || bodyindex == -1) {
            Service.gI().sendThongBao(player, "Không có " + item.template.name + " để nâng cấp");
        }

        player.inventory.itemsBody.set(bodyindex, ItemService.gI().createItemNull());
        InventoryService.gI().sendItemBody(player);

        // Handle random parameter adjustment for level 14 items
        int param = 0;
        if (item.template.level == 14) {
            if (Util.isTrue(25, 100)) {
                param = Util.nextInt(11, 15);
            } else if (Util.isTrue(25, 75)) {
                param = Util.nextInt(5, 10);
            } else {
                param = Util.nextInt(0, 4);
            }
        }

        // Apply item options with possible upgrades if item is level 14
        List<ItemOption> itemoptions = new ArrayList<>();
        if (!item.itemOptions.isEmpty()) {
            for (ItemOption ios : item.itemOptions) {
                if (item.template.level == 14 && InventoryService.gI().optionCanUpgrade(ios.optionTemplate.id) && param > 0) {
                    int id = ios.optionTemplate.id;
                    int param1 = ios.param + (ios.param * param) / 100;
                    itemoptions.add(new ItemOption(id, param1));
                } else if (ios.optionTemplate.id != 164) {
                    itemoptions.add(new ItemOption(ios.optionTemplate.id, ios.param));
                }
            }
        } else {
            itemoptions.add(new ItemOption(73, (short) 0));
        }

        item.itemOptions.clear();
        item.itemOptions.addAll(itemoptions);
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBags(player);

        Service.gI().sendThongBao(player, "Mua thành công " + is.temp.name);
    }
}
