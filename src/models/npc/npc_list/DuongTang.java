//package models.npc.npc_list;
//
///*
// * @Author: DienCoLamCoi
// * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
// * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
// */
//
//
//import consts.ConstNpc;
//import models.npc.Npc;
//import models.player.Player;
//import services.map.ChangeMapService;
//import utils.Util;
//
//public class DuongTang extends Npc {
//
//    public DuongTang(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        super(mapId, status, cx, cy, tempId, avartar);
//    }
//
//    @Override
//    public void openBaseMenu(Player player) {
//        if (canOpenNpc(player)) {
//            switch (mapId) {
//                case 0 -> {
//                    createOtherMenu(player, ConstNpc.BASE_MENU, "A mi phò phò, thí chủ hãy giúp giải cứu đồ đệ của bần tăng đang bị phong ấn tại ngũ hành sơn",
//                            "Đồng ý", "Nhiệm vụ\nhộ tống", "Từ chối", "Nhận\nthưởng");
//                }
//                case 123 -> {
//                    createOtherMenu(player, ConstNpc.BASE_MENU, "Ra khỏi ngôi làng này sẽ gặp ngọn núi ngũ hành sơn",
//                            "Về\nLàng Aru", "Đóng");
//                }
//                default ->
//                    super.openBaseMenu(player);
//            }
//        }
//    }
//
//    @Override
//    public void confirmMenu(Player player, int select) {
//        if (canOpenNpc(player)) {
//            if (player.idMark.isBaseMenu()) {
//                switch (mapId) {
//                    case 0 -> {
//                        if (select == 0) {
//                            ChangeMapService.gI().changeMapNonSpaceship(player, 123, 50, 384);
//                        }
//                    }
//                    case 123 -> {
//                        if (select == 0) {
//                            ChangeMapService.gI().changeMapNonSpaceship(player, 0, Util.nextInt(700, 800), 432);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//}
