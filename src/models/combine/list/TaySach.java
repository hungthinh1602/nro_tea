package models.combine.list;

import consts.ConstNpc;
import models.item.Item;
import models.item.Item.ItemOption;
import models.player.Player;
import services.CombineService;
import services.ItemService;
import services.Service;
import services.player.InventoryService;

/**
 * @author Modified
 */
public class TaySach {

    public static void showInfoCombine(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item sachTuyetKy = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (CombineService.gI().issachTuyetKy(item)) {
                    sachTuyetKy = item;
                }
            }
            if (sachTuyetKy != null) {
                String npcSay = "|3|Tẩy Sách Tuyệt Kỹ\n";
                npcSay += "|5|Tẩy sách sẽ xóa chỉ số chính và giữ các option hệ thống để giám định lại.";
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                        "Đồng ý", "Từ chối");
            } else {
                Service.gI().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ để tẩy");
            }
        } else {
            Service.gI().sendThongBaoOK(player, "Cần Sách Tuyệt Kỹ để tẩy");
        }
    }

    public static void Taysach(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item sachTuyetKy = null;
            int do_ben = 1000;
            for (Item item : player.combineNew.itemsCombine) {
                if (CombineService.gI().issachTuyetKy(item)) {
                    sachTuyetKy = item;
                }
            }
            if (sachTuyetKy != null) {
                int luotTay = 0;
                for (ItemOption io : sachTuyetKy.itemOptions) {
                    if (io.optionTemplate.id == 225) {
                        luotTay = io.param;
                        break;
                    }
                    if (io.optionTemplate.id == 226) {
                        do_ben = io.param;
                        break;
                    }
                }
                if (luotTay <= 0) {
                    Service.gI().sendThongBao(player, "Yêu cầu lượt tẩy lớn hơn 0");
                    return;
                }

                if (CombineService.gI().checkHaveOption(sachTuyetKy, 0, 224)) {
                    Service.gI().sendThongBao(player, "Chưa được giám định!!");
                    return;
                }

                Item sachTuyetKy_2 = ItemService.gI().createNewItem((short) sachTuyetKy.template.id);

                // Đánh dấu sách đã tẩy


                // Giảm số lượt tẩy đi 1 nếu còn > 0 và cập nhật lại item option
                for (ItemOption io : sachTuyetKy.itemOptions) {
                    if (io.optionTemplate.id == 225) {
                        int newParam = io.param - 1;
                        if (newParam > 0) {
                            sachTuyetKy_2.itemOptions.add(new ItemOption(224, 0));
                            sachTuyetKy_2.itemOptions.add(new ItemOption(225, newParam));
                            sachTuyetKy_2.itemOptions.add(new ItemOption(21, 40));
                            sachTuyetKy_2.itemOptions.add(new ItemOption(30, 0));
                            sachTuyetKy_2.itemOptions.add(new ItemOption(87, 0));
                            sachTuyetKy_2.itemOptions.add(new ItemOption(226, do_ben));
                        }
                    }}


                CombineService.gI().sendEffectSuccessCombine(player);
                InventoryService.gI().subQuantityItemsBag(player, sachTuyetKy, 1);
                InventoryService.gI().addItemBag(player, sachTuyetKy_2);
                InventoryService.gI().sendItemBags(player);
                CombineService.gI().reOpenItemCombine(player);
            }
        }
    }
}
