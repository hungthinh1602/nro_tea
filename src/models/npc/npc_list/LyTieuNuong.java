package models.npc.npc_list;

import consts.ConstNpc;
import consts.ConstTask;
import models.npc.Npc;
import models.npc.TaiXiu;
import models.player.Player;
import services.Service;
import services.TaskService;
import services.func.Input;

public class LyTieuNuong extends Npc {

    public LyTieuNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào , Ta là Lý Tiểu Nương - Quản lí casino TX tại Đảo Kame", 
                        "Thể lệ", "Tham gia", "Đóng");
            }
        }
    }

    @Override
    public void confirmMenu(Player pl, int select) {
        if (canOpenNpc(pl)) {
            switch (pl.idMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU:
                    switch (select) {
                        case 0 -> createOtherMenu(pl, ConstNpc.IGNORE_MENU, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_HD), "Ok");
                        case 1 -> {
                            if (TaiXiu.baotri) {
                                createOtherMenu(pl, ConstNpc.MINIGAME_BAOTRI, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU), "Cập nhập", "Đóng");
                                return;
                            }
                            if (pl.goldTai == 0 && pl.goldXiu == 0) {
                                createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TUYCHON, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_EMPTY),
                                        "Cập nhập", "Đặt Tài", "Đặt Xỉu", "Đóng");
                            } else if (pl.goldTai > 0) {
                                if (pl.isAdmin()) {
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TAI, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_TAI),
                                            "Auto Tài", "Auto Xỉu", "Tam Hoa", "Cập nhập", "Thêm Tài", "Thêm Xỉu", "Đóng");
                                } else {
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TAI, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_TAI),
                                            "Cập nhập", "Thêm Tài", "Thêm Xỉu", "Đóng");
                                }
                            } else if (pl.goldXiu > 0) {
                                if (pl.isAdmin()) {
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_XIU, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_XIU),
                                            "Auto Tài", "Auto Xỉu", "Tam Hoa", "Cập nhập", "Thêm Xỉu", "Đóng");
                                } else {
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_XIU, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_XIU),
                                            "Cập nhập", "Thêm Xỉu", "Đóng");
                                }
                            }
                        }
                    }
                    break;

                case ConstNpc.MINIGAME_TAIXIU_TUYCHON:
                    if (pl.goldTai == 0 && pl.goldXiu == 0 && !TaiXiu.baotri) {
                        switch (select) {
                            case 0 -> 
                                createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TUYCHON, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_EMPTY),
                                        "Cập nhập", "Đặt Tài", "Đặt Xỉu", "Đóng");
                            case 1 -> {
                                if (pl.isAdmin()) {
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TAI, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_TAI),
                                            "Auto Tài", "Auto Xỉu", "Tam Hoa", "Cập nhập", "Thêm Tài", "Thêm Xỉu", "Đóng");
                                } else {
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TAI, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_TAI),
                                            "Cập nhập", "Thêm Tài", "Đóng");
                                }
                            }
                            case 2 -> {
                                if (pl.isAdmin()) {
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_XIU, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_XIU),
                                            "Auto Tài", "Auto Xỉu", "Tam Hoa", "Cập nhập", "Thêm Xỉu", "Đóng");
                                } else {
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_XIU, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_XIU),
                                            "Cập nhập", "Thêm Xỉu", "Đóng");
                                }
                            }
                        }
                    }
                    break;

                case ConstNpc.MINIGAME_TAIXIU_TAI:
                    if (pl.goldTai >= 0 && !TaiXiu.baotri) {
                        if (pl.isAdmin()) {
                            switch (select) {
                                case 0, 1, 2 -> {
                                    TaiXiu.gI().chinhCau((byte) select);
                                    Service.gI().sendThongBao(pl, "|7|Bạn đã can thiệp kết quả 100% ra: " + (select == 0 ? "TÀI" : select == 1 ? "XỈU" : "TAM HOA"));
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TAI, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_TAI),
                                            "Auto Tài", "Auto Xỉu", "Tam Hoa", "Cập nhập", "Thêm Tài", "Thêm Xỉu", "Đóng");
                                }
                                case 3 -> 
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TAI, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_TAI),
                                            "Auto Tài", "Auto Xỉu", "Tam Hoa", "Cập nhập", "Thêm Tài", "Thêm Xỉu", "Đóng");
                                case 4 -> {
                                    if (TaskService.gI().getIdTask(pl) < ConstTask.TASK_20_0) {
                                        Service.gI().sendThongBao(pl, "Bạn phải làm tới nhiệm vụ TĐST mới có thể tham gia");
                                        return;
                                    }
                                    Input.gI().taixiu_Tai(pl);
                                }
                            }
                        } else {
                            switch (select) {
                                case 0 -> 
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_TAI, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_TAI),
                                            "Cập nhập", "Thêm Tài", "Đóng");
                                case 1 -> {
                                    if (TaskService.gI().getIdTask(pl) < ConstTask.TASK_20_0) {
                                        Service.gI().sendThongBao(pl, "Bạn phải làm tới nhiệm vụ TĐST mới có thể tham gia");
                                        return;
                                    }
                                    Input.gI().taixiu_Tai(pl);
                                }
                            }
                        }
                    }
                    break;

                case ConstNpc.MINIGAME_TAIXIU_XIU:
                    if (pl.goldXiu >= 0 && !TaiXiu.baotri) {
                        if (pl.isAdmin()) {
                            switch (select) {
                                case 0, 1, 2 -> {
                                    TaiXiu.gI().chinhCau((byte) select);
                                    Service.gI().sendThongBao(pl, "|7|Bạn đã can thiệp kết quả 100% ra: " + (select == 0 ? "TÀI" : select == 1 ? "XỈU" : "TAM HOA"));
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_XIU, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_XIU),
                                            "Auto Tài", "Auto Xỉu", "Tam Hoa", "Cập nhập", "Thêm Xỉu", "Đóng");
                                }
                                case 3 -> 
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_XIU, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_XIU),
                                            "Auto Tài", "Auto Xỉu", "Tam Hoa", "Cập nhập", "Thêm Xỉu", "Đóng");
                                case 4 -> {
                                    if (TaskService.gI().getIdTask(pl) < ConstTask.TASK_20_0) {
                                        Service.gI().sendThongBao(pl, "Bạn phải làm tới nhiệm vụ TĐST mới có thể tham gia");
                                        return;
                                    }
                                    Input.gI().taixiu_Xiu(pl);
                                }
                            }
                        } else {
                            switch (select) {
                                case 0 -> 
                                    createOtherMenu(pl, ConstNpc.MINIGAME_TAIXIU_XIU, ConstNpc.TextNpc(pl, ConstNpc.TAI_XIU_XIU),
                                            "Cập nhập", "Thêm Xỉu", "Đóng");
                                case 1 -> {
                                    if (TaskService.gI().getIdTask(pl) < ConstTask.TASK_20_0) {
                                        Service.gI().sendThongBao(pl, "Bạn phải làm tới nhiệm vụ TĐST mới có thể tham gia");
                                        return;
                                    }
                                    Input.gI().taixiu_Xiu(pl);
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }
}
