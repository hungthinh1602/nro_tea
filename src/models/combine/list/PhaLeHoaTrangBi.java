package models.combine.list;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import consts.ConstNpc;
import models.item.Item;
import services.CombineService;
import static services.CombineService.MAX_STAR_ITEM;
import utils.CombineUtil;
import models.player.Player;
import models.player.PlayerEffect;
import server.ServerNotify;
import services.player.InventoryService;
import services.Service;
import services.TaskService;
import utils.Util;

public class PhaLeHoaTrangBi {

    public static void showInfoCombine(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item item = player.combineNew.itemsCombine.get(0);
            if (CombineUtil.isTrangBiPhaLeHoa(item)) {
                int star = 0;
                for (Item.ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 107) {
                        star = io.param;
                        break;
                    }
                }
                if (star < CombineService.MAX_STAR_ITEM) {
                    player.combineNew.goldCombine = CombineUtil.getGoldPhaLeHoa(star);
                    player.combineNew.gemCombine = CombineUtil.getGemPhaLeHoa(star);
                    player.combineNew.ratioCombine = CombineUtil.getRatioPhaLeHoa(star);

                    String npcSay = item.template.name + "\n|2|";
                    for (Item.ItemOption io : item.itemOptions) {
                        if (io.optionTemplate.id != 102) {
                            npcSay += io.getOptionString() + "\n";
                        }
                    }
                    npcSay += "|7|Tỉ lệ thành công: " + player.combineNew.ratioCombine + "%" + "\n";
                    if (player.combineNew.goldCombine <= player.inventory.gold) {
                        npcSay += "|1|Cần " + Util.numberToMoney(player.combineNew.goldCombine) + " vàng";
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                "Nâng cấp\ncần " + player.combineNew.gemCombine + " ngọc", "Nâng cấp 10 lần", "Nâng cấp 50 lần", "Nâng cấp 100 lần");
                    } else {
                        npcSay += "Còn thiếu " + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold) + " vàng";
                        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "Đóng");
                    }

                } else {
                    CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm đã đạt tối đa sao pha lê", "Đóng");
                }
            } else {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vật phẩm này không thể đục lỗ", "Đóng");
            }
        } else {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hãy hãy chọn 1 vật phẩm để pha lê hóa", "Đóng");
        }
    }

    public static void phaLeHoa(Player player, int... numm) {
        if (player.idMark != null && !Util.canDoWithTime(player.idMark.getLastTimeCombine(), 500)) {
            return;
        }
        player.idMark.setLastTimeCombine(System.currentTimeMillis());
        int n = 1;
        if (numm.length > 0) {
            n = numm[0];
        }
        if (!player.combineNew.itemsCombine.isEmpty()) {
            int gold = player.combineNew.goldCombine;
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gold < gold) {
                Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
                return;
            } else if (player.inventory.gem < gem) {
                Service.gI().sendThongBao(player, "Không đủ ngọc để thực hiện");
                return;
            }
            int barGoldSell = 0;
            int num = 0;
            int star = 0;
            boolean success = false;
            int fail = 0;
            Item item = null;
            Item.ItemOption optionStar = null;
            for (int i = 0; i < n; i++) {
                num = i;
                gold = player.combineNew.goldCombine;
                gem = player.combineNew.gemCombine;
                if (player.inventory.gem < gem) {
                    if (n > 1) {
                        Service.gI().sendThongBao(player, "Xịt liên tục " + i + " lần và đã hết ngọc!");
                        Service.gI().sendMoney(player);
                    }
                    break;
                }
                if (n > 1 && player.inventory.gold < gold && InventoryService.gI().findItemBag(player, 457) != null && InventoryService.gI().findItemBag(player, 457).quantity >= 1) {
                    InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 457), 1);
                    player.inventory.gold += 37000000;
                    barGoldSell++;
                }
                if (player.inventory.gold < gold) {
                    break;
                }
                item = player.combineNew.itemsCombine.get(0);
                if (CombineUtil.isTrangBiPhaLeHoa(item)) {
                    star = 0;
                    optionStar = null;
                    for (Item.ItemOption io : item.itemOptions) {
                        if (io.optionTemplate.id == 107) {
                            star = io.param;
                            optionStar = io;
                            break;
                        }
                    }
                    if (star < MAX_STAR_ITEM) {
                        player.combineNew.goldCombine = CombineUtil.getGoldPhaLeHoa(star);
                        player.combineNew.gemCombine = CombineUtil.getGemPhaLeHoa(star);
                        player.combineNew.ratioCombine = CombineUtil.getRatioPhaLeHoa(star);
                        player.inventory.gold -= gold;
                        player.inventory.gem -= gem;
                        int ratio = 1;
                        boolean succ = true;
                        if (optionStar != null) {
                            switch (optionStar.param) {
                                case 6 ->
                                    ratio *= 50;
                                case 7 ->
                                    ratio *= 100;
                                case 8 ->
                                    ratio *= 200;
                                case 9 ->
                                    ratio *= 400;
                                case 10 ->
                                    ratio *= 800;
                                case 11 ->
                                    ratio *= 1600000;
                                case 12 ->
                                    ratio *= 20000000;
                            }
                            if (optionStar.param > 10) {
                                succ = false;
                                if (Util.isTrue(1, 100000000)) {
                                    succ = true;
                                }
                            }
                        }
                        if (Util.isTrue(player.combineNew.ratioCombine, 100 * ratio) && succ) {
                            success = true;
                            break;
                        } else {
                            fail++;
                        }
                    }
                } else {
                    break;
                }
                player.effect.addPointThanhDapDo();
            }
            if (InventoryService.gI().findItemBag(player, 457) != null && InventoryService.gI().findItemBag(player, 457).quantity >= 1) {
                if (player.inventory.gold < 37_000_000) {
                    InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 457), 1);
                    InventoryService.gI().sendItemBags(player);
                    player.inventory.addGold(37000000);
                    Service.gI().sendMoney(player);
                    barGoldSell++;
                }
            }
            num++;
            if (item != null && fail > 0) {
                Item.ItemOption optionXit = null;
                for (Item.ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 213) {
                        fail += io.param;
                        optionXit = io;
                        break;
                    }
                }
                if (optionXit == null) {
                    item.itemOptions.add(new Item.ItemOption(213, fail));
                } else {
                    optionXit.param = fail;
                }
            }
            if (success) {
                star++;
                if (item != null) {
                    if (optionStar == null) {
                        item.itemOptions.add(new Item.ItemOption(107, star));
                    } else {
                        optionStar.param = star;
                    }
                    if (optionStar != null && optionStar.param >= 7) {
                        if (optionStar.param == 11) {
                            TaskService.gI().checkDoneTaskPhaLeHoa11S(player);
                        }
                        ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa pha lê hóa "
                                + "thành công " + item.template.name + " lên " + star + " sao pha lê");
                    }
                }
                if (n > 1) {
                    Service.gI().sendThongBao(player, "Pha lê hóa trang bị lên " + star + " sao thành công, sau " + num + " lần nâng cấp!");
                }
                CombineService.gI().sendEffectSuccessCombine(player);
            } else {
                if (n > 1 && num < 100) {
                    Service.gI().sendThongBao(player, "Xịt liên tục " + num + " lần " + (barGoldSell > 0 ? ", đã bán " + barGoldSell + " thỏi vàng" : "") + " và đã hết vàng!");
                }
                CombineService.gI().sendEffectFailCombine(player);
            }
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendMoney(player);
            CombineService.gI().reOpenItemCombine(player);
        }
    }

}
