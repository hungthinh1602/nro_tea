package services.func;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import database.daos.HistoryTransactionDAO;
import models.item.Item;
import models.player.Inventory;
import models.player.Player;
import network.io.Message;
import services.ItemService;
import services.player.PlayerService;
import services.Service;
import services.player.InventoryService;
import utils.Logger;
import utils.Util;

import java.util.ArrayList;
import java.util.List;
import services.TaskService;

public class Trade {

    public static final int TIME_TRADE = 180000;
    public static final int QUANLITY_MAX = 2_000_000_000;

    private Player player1;
    private Player player2;

    private long gold1Before;
    private long gold2Before;
    private List<Item> bag1Before;
    private List<Item> bag2Before;

    private List<Item> itemsBag1;
    private List<Item> itemsBag2;

    private List<Item> itemsTrade1;
    private List<Item> itemsTrade2;
    private int goldTrade1;
    private int goldTrade2;

    public byte accept;

    private long lastTimeStart;
    private boolean start;

    public Trade(Player pl1, Player pl2) {
        this.player1 = pl1;
        this.player2 = pl2;
        this.gold1Before = pl1.inventory.gold;
        this.gold2Before = pl2.inventory.gold;
        this.bag1Before = InventoryService.gI().copyItemsBag(player1);
        this.bag2Before = InventoryService.gI().copyItemsBag(player2);
        this.itemsBag1 = InventoryService.gI().copyItemsBag(player1);
        this.itemsBag2 = InventoryService.gI().copyItemsBag(player2);
        this.itemsTrade1 = new ArrayList<>();
        this.itemsTrade2 = new ArrayList<>();
        TransactionService.PLAYER_TRADE.put(pl1, this);
        TransactionService.PLAYER_TRADE.put(pl2, this);
    }

    public void openTabTrade() {
        player1.idMark.setAcpTrade(true);
        player2.idMark.setAcpTrade(true);
        this.lastTimeStart = System.currentTimeMillis();
        this.start = true;
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) player1.id);
            player2.sendMessage(msg);
            msg.cleanup();
            msg = new Message(-86);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) player2.id);
            player1.sendMessage(msg);
        } catch (Exception ignored) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void addItemBot(Item it) {
        itemsTrade2.add(it);
    }

    public void addItemTrade(Player pl, byte index, int quantity) {
        if ((this.player1.nPoint.power >= 500_000_000 && this.player2.nPoint.power >= 500_000_000)) {
            if (true) {
                if (index == -1) {
                    if (pl.equals(this.player1)) {
                        goldTrade1 = quantity;
                    } else {
                        goldTrade2 = quantity;
                    }
                } else {
                    Item item;
                    if (pl.equals(this.player1)) {
                        item = itemsBag1.get(index);
                    } else {
                        item = itemsBag2.get(index);
                    }
                    if (quantity > item.quantity || quantity < 0) {
                        return;
                    }
                    if (isItemCannotTran(item)) {
                        removeItemTrade(pl, index);
                    } else {
                        if (quantity > 99999) {
                            int n = quantity / 99999;
                            int left = quantity % 99999;
                            for (int i = 0; i < n; i++) {
                                Item itemTrade = ItemService.gI().copyItem(item);
                                itemTrade.quantity = 99999;
                                if (pl.equals(this.player1)) {
                                    InventoryService.gI().subQuantityItem(itemsBag1, item, itemTrade.quantity);
                                    itemsTrade1.add(itemTrade);
                                } else {
                                    InventoryService.gI().subQuantityItem(itemsBag2, item, itemTrade.quantity);
                                    itemsTrade2.add(itemTrade);
                                }
                            }
                            if (left > 0) {
                                Item itemTrade = ItemService.gI().copyItem(item);
                                itemTrade.quantity = left;
                                if (pl.equals(this.player1)) {
                                    InventoryService.gI().subQuantityItem(itemsBag1, item, itemTrade.quantity);
                                    itemsTrade1.add(itemTrade);
                                } else {
                                    InventoryService.gI().subQuantityItem(itemsBag2, item, itemTrade.quantity);
                                    itemsTrade2.add(itemTrade);
                                }
                            }
                        } else {
                            Item itemTrade = ItemService.gI().copyItem(item);
                            itemTrade.quantity = quantity != 0 ? quantity : 1;
                            if (pl.equals(this.player1)) {
                                InventoryService.gI().subQuantityItem(itemsBag1, item, itemTrade.quantity);
                                itemsTrade1.add(itemTrade);
                            } else {
                                InventoryService.gI().subQuantityItem(itemsBag2, item, itemTrade.quantity);
                                itemsTrade2.add(itemTrade);
                            }
                        }
                    }
                }
            }
        } else {
            Service.gI().sendThongBaoFromAdmin(pl,
                    "|7|Bạn chưa đủ 500Tr sm " + "\n|7|để giao dịch");
            removeItemTrade(pl, index);

        }
    }

    private void removeItemTrade(Player pl, byte index) {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(2);
            msg.writer().write(index);
            pl.sendMessage(msg);
            Service.gI().sendThongBao(pl, "Không thể giao dịch vật phẩm này");
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void removeItemTrade2(Player pl, byte index) {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(2);
            msg.writer().write(index);
            pl.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private boolean isItemCannotTran(Item item) {
        for (Item.ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 30) {
                return true;
            }
        }
        switch (item.template.id) {
            case 454:
            case 579:
            case 921:
                return true;
        }
        switch (item.template.type) {
            case 27: //
                return item.template.id == 590;
            case 5: //cải trang
            case 6: //đậu thần
            case 7: //sách skill
            case 8: //vật phẩm nhiệm vụ
            case 11: //flag bag
            case 13: //bùa
            case 22: //vệ tinh
            case 23: //ván bay
            case 24: //ván bay vip
            case 28: //cờ
            case 31: //bánh trung thu, bánh tết
            case 32: //giáp tập luyện
                return true;
            default:
                return false;
        }
    }

    public void cancelTrade() {
        String notifiText = "Giao dịch bị hủy bỏ";
        Service.gI().sendThongBao(player1, notifiText);
        Service.gI().sendThongBao(player2, notifiText);
        closeTab();
        dispose();
    }

    private void closeTab() {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(7);
            player1.sendMessage(msg);
            player2.sendMessage(msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void dispose() {
        player1.idMark.setPlayerTradeId(-1);
        player2.idMark.setPlayerTradeId(-1);
        TransactionService.PLAYER_TRADE.remove(player1);
        TransactionService.PLAYER_TRADE.remove(player2);
        this.player1 = null;
        this.player2 = null;
        this.itemsBag1 = null;
        this.itemsBag2 = null;
        this.itemsTrade1 = null;
        this.itemsTrade2 = null;
    }

    public void lockTran(Player pl) {
        Message msg = null;
        try {
            msg = new Message(-86);
            msg.writer().writeByte(6);
            if (pl.equals(player1)) {
                msg.writer().writeInt(goldTrade1);
                msg.writer().writeByte(itemsTrade1.size());
                for (Item item : itemsTrade1) {
                    msg.writer().writeShort(item.template.id);
                    if (player1.getSession().version < 222) {
                        msg.writer().writeByte(item.quantity > Byte.MAX_VALUE ? Byte.MAX_VALUE : item.quantity);
                    } else {
                        msg.writer().writeInt(item.quantity);
                    }
                    msg.writer().writeByte(item.itemOptions.size());
                    for (Item.ItemOption io : item.itemOptions) {
                        msg.writer().writeByte(io.optionTemplate.id);
                        msg.writer().writeShort(io.param);
                    }
                }
                player2.sendMessage(msg);
            } else {
                msg.writer().writeInt(goldTrade2);
                msg.writer().writeByte(itemsTrade2.size());
                for (Item item : itemsTrade2) {
                    msg.writer().writeShort(item.template.id);
                    if (player2.getSession().version < 222) {
                        msg.writer().writeByte(item.quantity > Byte.MAX_VALUE ? Byte.MAX_VALUE : item.quantity);
                    } else {
                        msg.writer().writeInt(item.quantity);
                    }
                    msg.writer().writeByte(item.itemOptions.size());
                    for (Item.ItemOption io : item.itemOptions) {
                        msg.writer().writeByte(io.optionTemplate.id);
                        msg.writer().writeShort(io.param);
                    }
                }
                player1.sendMessage(msg);
            }
        } catch (Exception e) {
            Logger.logException(Trade.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void acceptTrade() {
        this.accept++;
        if (this.accept == 2) {
            this.startTrade();
        }
    }

    private void startTrade() {
        byte tradeStatus = SUCCESS;
        if (player1.inventory.gold + goldTrade2 > Inventory.LIMIT_GOLD) {
            tradeStatus = FAIL_MAX_GOLD_PLAYER1;
        } else if (player2.inventory.gold + goldTrade1 > Inventory.LIMIT_GOLD) {
            tradeStatus = FAIL_MAX_GOLD_PLAYER2;
        }
        if (tradeStatus != SUCCESS) {
            sendNotifyTrade(tradeStatus);
        } else {
            for (Item item : itemsTrade1) {
                if (!InventoryService.gI().addItemList(itemsBag2, item)) {
                    tradeStatus = FAIL_NOT_ENOUGH_BAG_P1;
                    break;
                }
            }
            if (tradeStatus != SUCCESS) {
                sendNotifyTrade(tradeStatus);
            } else {
                for (Item item : itemsTrade2) {
                    if (!InventoryService.gI().addItemList(itemsBag1, item)) {
                        tradeStatus = FAIL_NOT_ENOUGH_BAG_P2;
                        break;
                    }
                }
                if (tradeStatus == SUCCESS) {
                    player1.inventory.gold += goldTrade2;
                    player2.inventory.gold += goldTrade1;
                    player1.inventory.gold -= goldTrade1;
                    player2.inventory.gold -= goldTrade2;
                    player1.inventory.itemsBag = itemsBag1;
                    player2.inventory.itemsBag = itemsBag2;

                    InventoryService.gI().sendItemBags(player1);
                    InventoryService.gI().sendItemBags(player2);
                    PlayerService.gI().sendInfoHpMpMoney(player1);
                    PlayerService.gI().sendInfoHpMpMoney(player2);

                    HistoryTransactionDAO.insert(player1, player2, goldTrade1, goldTrade2, itemsTrade1, itemsTrade2,
                            bag1Before, bag2Before, this.player1.inventory.itemsBag, this.player2.inventory.itemsBag,
                            gold1Before, gold2Before, this.player1.inventory.gold, this.player2.inventory.gold);
                }
                sendNotifyTrade(tradeStatus);
            }
        }

    }

    private static final byte SUCCESS = 0;
    private static final byte FAIL_MAX_GOLD_PLAYER1 = 1;
    private static final byte FAIL_MAX_GOLD_PLAYER2 = 2;
    private static final byte FAIL_NOT_ENOUGH_BAG_P1 = 3;
    private static final byte FAIL_NOT_ENOUGH_BAG_P2 = 4;

    private void sendNotifyTrade(byte status) {
        player1.idMark.setLastTimeTrade(System.currentTimeMillis());
        player2.idMark.setLastTimeTrade(System.currentTimeMillis());
        switch (status) {
            case SUCCESS:
                Service.gI().sendThongBao(player1, "Giao dịch thành công");
                Service.gI().sendThongBao(player2, "Giao dịch thành công");
                break;
            case FAIL_MAX_GOLD_PLAYER1:
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do số lượng vàng sau giao dịch vượt tối đa");
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do số lượng vàng " + player1.name + " sau giao dịch vượt tối đa");
                break;
            case FAIL_MAX_GOLD_PLAYER2:
                Service.gI().sendThongBao(player2, "Giao dịch thất bại do số lượng vàng sau giao dịch vượt tối đa");
                Service.gI().sendThongBao(player1, "Giao dịch thất bại do số lượng vàng " + player2.name + " sau giao dịch vượt tối đa");
                break;
            case FAIL_NOT_ENOUGH_BAG_P1:
                Service.gI().sendThongBao(player1, "Giao dịch thất bại vì " + player1.name + " không đủ chỗ chứa");
                Service.gI().sendThongBao(player2, "Giao dịch thất bại vì " + player1.name + " không đủ chỗ chứa");
                break;
            case FAIL_NOT_ENOUGH_BAG_P2:
                Service.gI().sendThongBao(player1, "Giao dịch thất bại vì " + player2.name + " không đủ chỗ chứa");
                Service.gI().sendThongBao(player2, "Giao dịch thất bại vì " + player2.name + " không đủ chỗ chứa");
                break;
        }
    }

    public void update() {
        if (this.start && Util.canDoWithTime(lastTimeStart, TIME_TRADE)) {
            this.cancelTrade();
        }
    }
}
