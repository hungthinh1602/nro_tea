/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.npc.npc_list;

import consts.ConstNpc;
import models.npc.Npc;
import models.player.Player;
import services.func.UseItem;

/**
 *
 * @author Administrator
 */
public class ToriBot extends Npc {

    public ToriBot(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Trong thời gian mùa 1 diễn ra\nNếu mua VIP sẽ được nhận\nnhiều ưu đãi hơn nữa.\nLưu ý: nâng cấp VIP chỉ được nâng 1 lần mỗi mùa",
                    "VIP 1", "VIP 2", "VIP 3", "Đóng");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                if (player.idMark.isBaseMenu()) {
                    switch (select) {
                        case 0 ->
                            createOtherMenu(player, 2,
                                    "|7|Nâng cấp VIP 1 bạn sẽ nhận được"
                                    + "\n|2|- Nhận ngay 500Tr vàng, X1 phiếu giảm giá 80%"
                                    + "\n- X3 kinh nghiệm toàn mùa, Avatar đẹp trai nhất vũ trụ"
                                    + "\n- Tặng 1 đệ tử",
                                    "1500 Ngọc\nXanh", "Đóng");
                        case 1 ->
                            createOtherMenu(player, 4,
                                    "|7|Nâng cấp VIP 2 bạn sẽ nhận được"
                                    + "\n|2|- Nhận ngay 1 Tỉ vàng, X3 phiếu giảm giá 80%"
                                    + "\n- X3 kinh nghiệm toàn mùa, Avatar đẹp trai nhất vũ trụ"
                                    + "\n- Cải trang quy lão kame"
                                    + "\n- Tặng 1 đệ tử, x30 thẻ đội trưởng vàng",
                                    "3500 Ngọc\nXanh", "Đóng");
                        case 2 ->
                            createOtherMenu(player, 6,
                                    "|7|Nâng cấp VIP 3 bạn sẽ nhận được"
                                    + "\n|2|- Nhận ngay 1 Tỉ 5 vàng, X5 phiếu giảm giá 80%"
                                    + "\n- X3 kinh nghiệm toàn mùa, Avatar đẹp trai nhất vũ trụ"
                                    + "\n- Cải trang quy lão kame"
                                    + "\n- Tặng 1 đệ tử, x50 thẻ đội trưởng vàng",
                                    "5500 Ngọc\nXanh", "Đóng");
                    }
                } else if (player.idMark.getIndexMenu() == 2) {
                    switch (select) {
                        case 0 -> {
                            if (player.pet != null) {
                                createOtherMenu(player, 8,
                                        "|7|Nâng cấp VIP 1"
                                        + "\n|2|- Bạn có muốn thay thế đệ tử hiện có thành đệ tử mới không ? đệ tử cũ sẽ biến mất lưu ý hãy tháo đồ nếu đổi đệ tử mới",
                                        "Có", "Không");
                            } else {
                                UseItem.gI().ComfirmNhanVIP(player, true);
                            }
                        }
                    }
                } else if (player.idMark.getIndexMenu() == 4) {
                    switch (select) {
                        case 0 -> {
                            if (player.pet != null) {
                                createOtherMenu(player, 10,
                                        "|7|Nâng cấp VIP 2"
                                        + "\n|2|- Bạn có muốn thay thế đệ tử hiện có thành đệ tử mới không ? đệ tử cũ sẽ biến mất lưu ý hãy tháo đồ nếu đổi đệ tử mới",
                                        "Có", "Không");
                            } else {
                                UseItem.gI().ComfirmNhanVIP2(player, true);
                            }
                        }
                    }
                } else if (player.idMark.getIndexMenu() == 6) {
                    switch (select) {
                        case 0 -> {
                            if (player.pet != null) {
                                createOtherMenu(player, 12,
                                        "|7|Nâng cấp VIP 3"
                                        + "\n|2|- Bạn có muốn thay thế đệ tử hiện có thành đệ tử mới không ? đệ tử cũ sẽ biến mất lưu ý hãy tháo đồ nếu đổi đệ tử mới",
                                        "Có", "Không");
                            } else {
                                UseItem.gI().ComfirmNhanVIP3(player, true);
                            }
                        }
                    }
                } else if (player.idMark.getIndexMenu() == 8) {
                    switch (select) {
                        case 0, 1 -> {
                            UseItem.gI().ComfirmNhanVIP(player, select == 0);
                        }
                    }
                } else if (player.idMark.getIndexMenu() == 10) {
                    switch (select) {
                        case 0, 1 -> {
                            UseItem.gI().ComfirmNhanVIP2(player, select == 0);
                        }
                    }
                } else if (player.idMark.getIndexMenu() == 12) {
                    switch (select) {
                        case 0, 1 -> {
                            UseItem.gI().ComfirmNhanVIP3(player, select == 0);
                        }
                    }
                }
            }
        }
    }
}
