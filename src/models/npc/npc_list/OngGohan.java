package models.npc.npc_list;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import consts.ConstNpc;
import database.daos.PlayerDAO;
import models.npc.Npc;
import models.player.Player;
import services.player.InventoryService;
import services.ItemService;
import services.Service;
import services.TaskService;
import services.func.Input;

public class OngGohan extends Npc {

    public OngGohan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Chào con , Đến với thế giới Cậu Bé Rồng Online \nTa tặng cho con 3 giftcode : tanthu , cauberong và caubevuive\n Server miễn phí , chịu khó cày cuốc nhé , Cơ chế gốc 100%",
                        "Nhập\nGiftcode", "Quên Mã\nBảo Vệ", "Mua Ngọc Xanh","Đổi Mật\nKhẩu");

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.idMark.isBaseMenu()) {
                switch (select) {
                    case 3 ->
                        Input.gI().createFormChangePassword(player);
//                    case 1 -> {
//                        if (player.inventory.gem >= 20_000_000) {
//                            this.npcChat(player, "20Tr Ngọc Xanh Là Đủ Rồi , Nhận Cái Lồn Gì Lắm!");
//                            break;
//                        }
//                        player.inventory.gem += 2000000;
//                        Service.gI().sendMoney(player);
//                        Service.gI().sendThongBao(player, "Bạn vừa nhận được 2M ngọc xanh!");
//                    }
//                    case 2 -> {
//                        if (Maintenance.isRunning) {
//                            break;
//                        }
//                        if (!Util.isAfterMidnight(player.lastRewardGoldBarTime) && !player.isAdmin()) {
//                            Service.gI().sendThongBaoOK(player, "Hãy chờ đến ngày mai");
//                            this.npcChat(player, "Chỉ có làm mới có ăn, không làm mà đòi có ăn chỉ có ăn đb, ăn c*t thôi con nhé!");
//                            return;
//                        }
//                        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
//                            int quantity = player.danhanthoivang ? (player.getSession().vip > 0 ? 10000 : 3000) : 10000;
//                            if (player.isAdmin()) {
//                                quantity = 2000;
//                            }
//                            Item goldBar = ItemService.gI().createNewItem((short) 457, quantity);
//                            InventoryService.gI().addItemBag(player, goldBar);
//                            InventoryService.gI().sendItemBags(player);
//                            this.npcChat(player, "Ta đã gửi " + quantity + " thỏi vàng vào hành trang của con\n con hãy kiểm tra ");
//                            player.danhanthoivang = true;
//                            player.lastRewardGoldBarTime = System.currentTimeMillis();
//                        } else {
//                            this.npcChat(player, "Hãy chừa cho ta 1 ô trống");
//                        }
//                    }
                    case 0 -> {
                        Input.gI().createFormGiftCode(player);
                    }
                    case 1 -> {
                        Input.gI().createFormMBV(player);
                    }
                    case 2 -> {
                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_VND,
                                "Con muốn đổi vnd thành gì ?\n"
                                + "|7|Số tiền của bạn còn : " + player.getSession().sotien + " VND",
                                "Mua Ngọc Xanh", "Đóng");
                    }
                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_OPEN_VND) {
                switch (select) {
//                    case 0 ->
//                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_THOI_VANG, "|7|Số tiền của bạn còn : " + player.getSession().sotien + "\n"
//                                + "Muốn quy đổi không", "Quy Đổi\n10.000\n 20 Thỏi Vàng", "Quy Đổi\n20.000\n 40 Thỏi Vàng", "Quy Đổi\n30.000\n 60 Thỏi Vàng", "Quy Đổi\n50.000\n 100 Thỏi Vàng", "Quy Đổi\n100.000 \n200 Thỏi Vàng", "Đóng");
                    case 0 ->
                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_HONG_NGOC, "|7|Số tiền của bạn còn : " + player.getSession().sotien + "\n"
                                + "Muốn quy đổi không", "Quy Đổi\n10.000\n 100 Ngọc Xanh", "Quy Đổi\n20.000\n 200 Ngọc Xanh", "Quy Đổi\n50.000\n 510 Ngọc Xanh", "Quy Đổi\n100.000\n 1100 Ngọc Xanh", "Quy Đổi\n500.000 \n5500 Ngọc Xanh", "Quy Đổi\n1.000.000 \n12000 Ngọc Xanh", "Đóng");
                }
//            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_OPEN_THOI_VANG) {
//                switch (select) {
//                    case 0 -> {
//                        if (player.getSession().sotien < 20000) {
//                            Service.gI().sendThongBaoOK(player, "Bạn không đủ 20k VND");
//                            return;
//                        }
//
//                        if (PlayerDAO.subvnd(player, 20000)) {
//                            Item i = ItemService.gI().createNewItem((short) 457, (short) 70);
//                            InventoryService.gI().addItemBag(player, i);
//                            InventoryService.gI().sendItemBags(player);
//                            Service.gI().sendThongBaoOK(player, "Bạn đã nhận được 70 thỏi vàng");
//                        }
//                    }
//                    case 1 -> {
//                        if (player.getSession().sotien < 20000) {
//                            Service.gI().sendThongBaoOK(player, "Bạn không đủ 20k VND");
//                            return;
//                        }
//
//                        if (PlayerDAO.subvnd(player, 20000)) {
//                            Item i = ItemService.gI().createNewItem((short) 457, (short) 70);
//                            InventoryService.gI().addItemBag(player, i);
//                            InventoryService.gI().sendItemBags(player);
//                            Service.gI().sendThongBaoOK(player, "Bạn đã nhận được 70 thỏi vàng");
//                        }
//                    }
//                }
            } else if (player.idMark.getIndexMenu() == ConstNpc.MENU_OPEN_HONG_NGOC) {
                if (InventoryService.gI().getCountEmptyBag(player) < 2) {
                    Service.gI().sendThongBaoOK(player, "Cần ít nhất 2 ô trống hành trang!");
                    return;
                }
                switch (select) {
                    case 0 -> {
                        if (player.getSession().sotien < 10000) {
                            Service.gI().sendThongBaoOK(player, "Bạn không đủ 10K VND");
                            return;
                        }
                        if (PlayerDAO.subvnd(player, 10000)) {
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1189, 10));
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1190, 10));
                            player.inventory.gem += 100;
                            Service.gI().sendMoney(player);
                            InventoryService.gI().sendItemBags(player);
                            Service.gI().sendThongBaoOK(player, "Bạn đã nhận được 100 Ngọc xanh");
                        }
                    }
                    case 1 -> {
                        if (player.getSession().sotien < 20000) {
                            Service.gI().sendThongBaoOK(player, "Bạn không đủ 20K VND");
                            return;
                        }
                        if (PlayerDAO.subvnd(player, 20000)) {
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1189, 20));
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1190, 20));
                            player.inventory.gem += 200;
                            Service.gI().sendMoney(player);
                            InventoryService.gI().sendItemBags(player);
                            Service.gI().sendThongBaoOK(player, "Bạn đã nhận được 200 Ngọc xanh");
                        }
                    }
                    case 2 -> {
                        if (player.getSession().sotien < 50000) {
                            Service.gI().sendThongBaoOK(player, "Bạn không đủ 50K VND");
                            return;
                        }
                        if (PlayerDAO.subvnd(player, 50000)) {
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1189, 50));
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1190, 50));
                            player.inventory.gem += 510;
                            Service.gI().sendMoney(player);
                            InventoryService.gI().sendItemBags(player);
                            Service.gI().sendThongBaoOK(player, "Bạn đã nhận được 510 Ngọc xanh");
                        }
                    }
                    case 3 -> {
                        if (player.getSession().sotien < 100000) {
                            Service.gI().sendThongBaoOK(player, "Bạn không đủ 100K VND");
                            return;
                        }
                        if (PlayerDAO.subvnd(player, 100000)) {
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1189, 100));
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1190, 100));
                            player.inventory.gem += 1100;
                            Service.gI().sendMoney(player);
                            InventoryService.gI().sendItemBags(player);
                            Service.gI().sendThongBaoOK(player, "Bạn đã nhận được 1100 Ngọc xanh");
                        }
                    }
                    case 4 -> {
                        if (player.getSession().sotien < 500000) {
                            Service.gI().sendThongBaoOK(player, "Bạn không đủ 500K VND");
                            return;
                        }
                        if (PlayerDAO.subvnd(player, 500000)) {
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1189, 500));
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1190, 500));
                            player.inventory.gem += 5500;
                            Service.gI().sendMoney(player);
                            InventoryService.gI().sendItemBags(player);
                            Service.gI().sendThongBaoOK(player, "Bạn đã nhận được 5500 Ngọc xanh");
                        }
                    }
                    case 5 -> {
                        if (player.getSession().sotien < 1000000) {
                            Service.gI().sendThongBaoOK(player, "Bạn không đủ 1000K VND");
                            return;
                        }
                        if (PlayerDAO.subvnd(player, 1000000)) {
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1189, 1000));
//                            InventoryService.gI().addItemBag(player, ItemService.gI().createNewItemPhieuTangNgoc(1190, 1000));
                            player.inventory.gem += 12000;
                            Service.gI().sendMoney(player);
                            InventoryService.gI().sendItemBags(player);
                            Service.gI().sendThongBaoOK(player, "Bạn đã nhận được 12000 Ngọc xanh");
                        }
                    }
                }
            }
        }
    }
}
