package models.npc;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import services.player.ClanService;
import services.Service;
import services.ItemService;
import services.dungeon.NgocRongNamecService;
import services.player.IntrinsicService;
import services.player.InventoryService;
import services.map.NpcService;
import services.PetService;
import services.player.PlayerService;
import services.player.FriendAndEnemyService;
import consts.ConstNpc;
import managers.boss.BossManager;
import models.clan.Clan;

import java.util.HashMap;

import services.map.ChangeMapService;
import services.func.SummonDragon;
import static services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static services.func.SummonDragon.SHENRON_SAY;

import models.player.Player;
import models.item.Item;
import models.matches.PVPService;
import server.Client;
import server.Maintenance;
import server.Manager;
import services.func.Input;
import utils.Logger;
import utils.Util;
import services.dungeon.SuperDivineWaterService;
import services.SubMenuService;
import services.ShenronEventService;
import models.npc.npc_list.*;
import services.func.SummonDragonNamek;

public class NpcFactory {

    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<>();

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Manager.NPC_TEMPLATES.get(tempId).avatar;
        try {
            return switch (tempId) {
                case ConstNpc.CAY_THONG ->
                    new CayThong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TORI_BOT ->
                    new ToriBot(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GHI_DANH ->
                    new GhiDanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CHI_CHI ->
                    new ChiChi(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRONG_TAI ->
                    new TrongTai(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.POTAGE ->
                    new Potage(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MR_POPO ->
                    new MrPoPo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUY_LAO_KAME ->
                    new QuyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU ->
                    new TruongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA ->
                    new VuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUA_HANG_KY_GUI ->
                    new KyGui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN ->
                    new OngGohan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_MOORI ->
                    new OngMoori(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_PARAGUS ->
                    new OngParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA ->
                    new Bulma(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE ->
                    new Dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE ->
                    new Appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF ->
                    new DrDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO ->
                    new Cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI ->
                    new Cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA ->
                    new Santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON ->
                    new Uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT ->
                    new BaHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO ->
                    new RuongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN ->
                    new DauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK ->
                    new Calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO ->
                    new Jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE ->
                    new ThuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VADOS ->
                    new Vados(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_VU_TRU ->
                    new ThanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT ->
                    new Kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN ->
                    new Osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BABIDAY ->
                    new Babiday(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LY_TIEU_NUONG ->
                    new LyTieuNuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LINH_CANH ->
                    new LinhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG ->
                    new QuaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG ->
                    new QuocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL ->
                    new BulmaTuongLai(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_OMEGA ->
                    new RongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S ->
                    new Rong1Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_2S ->
                    new Rong2Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_3S ->
                    new Rong3Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_4S ->
                    new Rong4Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_5S ->
                    new Rong5Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_6S ->
                    new Rong6Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_7S ->
                    new Rong7Sao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAI_THIEN_SU ->
                    new DaiThienSu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.WHIS ->
                    new Whis(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL ->
                    new Bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG ->
                    new BoMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_MEO_KARIN ->
                    new Karin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ ->
                    new GokuSSJ(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_2 ->
                    new GokuSSJ2(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TAPION ->
                    new Tapion(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DOC_NHAN ->
                    new DocNhan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GIUMA_DAU_BO ->
                    new GiuMaDauBo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TO_SU_KAIO ->
                    new ToSuKaio(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BARDOCK ->
                    new Bardock(mapId, status, cx, cy, tempId, avatar);
//                case ConstNpc.DUONG_TANG ->
//                    new DuongTang(mapId, status, cx, cy, tempId, avatar);

                default ->
                    new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
                            }
                        }
                    };
            };
        } catch (Exception e) {
            Logger.logException(NpcFactory.class,
                    e, "Lỗi load npc");
            return null;
        }
    }

    public static void createNpcRongThieng() {
        new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.idMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:
                        break;
                    case ConstNpc.SHOW_SHENRON_NAMEK_CONFIRM:
                        SummonDragonNamek.gI().showConfirmShenron(player, player.idMark.getIndexMenu(), (byte) select);
                        break;
                    case ConstNpc.SHENRON_NAMEK_CONFIRM:
                        if (select == 0) {
                            SummonDragonNamek.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragonNamek.gI().sendWhishesNamec(player);
                        }
                        break;
                    case ConstNpc.SHOW_SHENRON_EVENT_CONFIRM:
                        if (player.shenronEvent != null) {
                            player.shenronEvent.showConfirmShenron((byte) select);
                        }
                        break;
                    case ConstNpc.SHENRON_EVENT_CONFIRM:
                        if (player.shenronEvent != null) {
                            if (select == 0) {
                                player.shenronEvent.confirmWish();
                            } else if (select == 1) {
                                player.shenronEvent.sendWhishesShenron();
                            }
                        }
                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.idMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.idMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.idMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.idMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU -> {
                    }
                    case ConstNpc.SUMMON_SHENRON_EVENT -> {
                        if (select == 0) {
                            ShenronEventService.gI().summonShenron(player);
                        }
                    }
                    case ConstNpc.MAKE_MATCH_PVP -> {
                        if (Maintenance.isRunning) {
                        }
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                    }
                    case ConstNpc.MAKE_FRIEND -> {
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                try {
                                    FriendAndEnemyService.gI().acceptMakeFriend(player,
                                            Integer.parseInt(String.valueOf(playerId)));
                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                    }
//                    case ConstNpc.REVENGE -> {
//                        if (select == 0) {
//                            PVPService.gI().acceptRevenge(player);
//                        }
//                    }
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON -> {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                    }
                    case ConstNpc.SUMMON_SHENRON -> {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                    }
                    case ConstNpc.MENU_OPTION_USE_ITEM726 -> {
                        if (select == 0) {
                            SuperDivineWaterService.gI().joinMapThanhThuy(player);
                        }
                    }
                    case ConstNpc.MENU_SIEU_THAN_THUY -> {
                        if (select == 0) {
                            ChangeMapService.gI().changeMap(player, 46, -1, Util.nextInt(300, 400), 408);
                        }
                    }
                    case ConstNpc.TAP_TU_DONG_CONFIRM -> {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.lastMapOffline, player.lastZoneOffline, player.lastXOffline);
                        }
                    }
                    case ConstNpc.INTRINSIC -> {
                        switch (select) {
                            case 0 ->
                                IntrinsicService.gI().showAllIntrinsic(player);
                            case 1 ->
                                IntrinsicService.gI().showConfirmOpen(player);
                            case 2 ->
                                IntrinsicService.gI().showConfirmOpenVip(player);
                            default -> {
                            }
                        }
                    }
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC -> {
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                    }
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP -> {
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                    }
                    case ConstNpc.CONFIRM_LEAVE_CLAN -> {
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                    }
                    case ConstNpc.CONFIRM_NHUONG_PC -> {
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                    }

                    case ConstNpc.BAN_PLAYER -> {
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.gI().sendThongBao(player, "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                    }
                    case ConstNpc.BUFF_PET -> {
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.gI().sendThongBao(player, "Phát đệ tử cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                    }
                    case ConstNpc.OTT -> {
                        if (select < 3) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            player.idMark.setOtt(select);
                            String[] selects = new String[]{"Kéo", "Búa", "Bao", "Hủy"};
                            NpcService.gI().createMenuConMeo(pl, ConstNpc.OTT_ACCEPT, -1,
                                    player.name + " muốn chơi oẳn tù tì với bạn mức cược 5tr.", selects, player);
                        }
                    }
                    case ConstNpc.OTT_ACCEPT -> {
                        if (select < 3) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            int slp1 = pl.idMark.getOtt();
                            int slp2 = select;
                            if (slp1 == -1 || slp2 == -1) {
                                return;
                            }
                            pl.idMark.setOtt(-1);
                            String[] selects = new String[]{"Kéo", "Búa", "Bao"};
                            Service.gI().chat(pl, selects[slp1]);
                            Service.gI().chat(player, selects[slp2]);
                            Service.gI().sendEffAllPlayer(pl, 1000 + slp1, 1, 2, 1);
                            Service.gI().sendEffAllPlayer(player, 1000 + slp2, 1, 2, 1);
                            if (slp1 == slp2) {
                                Service.gI().sendThongBao(pl, "Hòa!");
                                Service.gI().sendThongBao(player, "Hòa!");
                            } else if (slp1 == 0 && slp2 == 2 || slp1 == 1 && slp2 == 0 || slp1 == 2 && slp2 == 1) {
                                Service.gI().sendThongBao(pl, "Thắng!");
                                Service.gI().sendThongBao(player, "Thua!");
                                pl.inventory.gold += 4800000;
                                player.inventory.gold -= 5000000;
                                Service.gI().sendMoney(pl);
                                Service.gI().sendMoney(player);
                            } else {
                                Service.gI().sendThongBao(pl, "Thua!");
                                Service.gI().sendThongBao(player, "Thắng!");
                                pl.inventory.gold -= 5000000;
                                player.inventory.gold += 4800000;
                                Service.gI().sendMoney(pl);
                                Service.gI().sendMoney(player);
                            }
                        }
                    }
                    case ConstNpc.SUB_MENU -> {
                        Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                        switch (select) {
                            case 0 ->
                                SubMenuService.gI().controller(player, (int) pl.id, SubMenuService.OTT);
                            case 1 ->
                                SubMenuService.gI().controller(player, (int) pl.id, SubMenuService.CUU_SAT);
                            case 2 -> {
//                                if (item != null) {
//                                    SubMenuService.gI().controller(player, (int) pl.id, SubMenuService.BUY_BACK);
//                                } else {
//                                    Service.gI().sendThongBao(pl, pl.name + " chưa bật bluetooth!");
//                                }
                            }
                            case 3 -> {
//                                if (item != null) {
//                                    Service.gI().sendThongBao(pl, pl.name + " chưa bật bluetooth!");
//                                }
                            }
                        }
                    }

                    case ConstNpc.BUY_BACK -> {
//                        if (select == 0) {
//                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
//                            BuyBackService.gI().buyItem(player, pl);
//                        }
                    }
                    case ConstNpc.MENU_BOT -> {
                        switch (select) {
                            case 0 -> {
                                Input.gI().createFormBotQuai(player);
                            }
                            case 1 -> {
                                Input.gI().createFormBotQuaiNappa(player);
                            }
                            case 2 -> {
                                Input.gI().createFormBotQuaiTuonglai(player);
                            }
                            case 3 -> {
                                Input.gI().createFormBotQuaiCold(player);
                            }
                            case 4 -> {
                                Input.gI().createFormBotBoss(player);
                            }
                        }
                    }
                    case ConstNpc.MENU_OPTION_USE_ITEM1149 -> {
                        try {
                            ItemService.gI().OpenSKH(player, player.idMark.getIndexMenu(), select);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.error("Lỗi mở hộp quà");
                        }
                    }
                    case ConstNpc.MENU_OPTION_USE_ITEM1150 -> {
                        try {
                            ItemService.gI().OpenSKH(player, player.idMark.getIndexMenu(), select);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.error("Lỗi mở hộp quà");
                        }
                    }
                    case ConstNpc.MENU_OPTION_USE_ITEM1151 -> {
                        try {
                            ItemService.gI().OpenSKH(player, player.idMark.getIndexMenu(), select);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.error("Lỗi mở hộp quà");
                        }
                    }
                    case ConstNpc.MENU_ADMIN -> {
                        switch (select) {
                            case 0 -> {
                                for (int i = 925; i <= 931; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryService.gI().addItemBag(player, item);
                                }
                                InventoryService.gI().sendItemBags(player);
                            }
                            case 1 -> {
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                }
                            }
                            case 2 -> {
                                if (player.isAdmin()) {
                                    System.out.println(player.name + " Đang bảo trì game!");
                                    Maintenance.gI().start(60);
                                }
                            }
                            case 3 ->
                                Input.gI().createFormFindPlayer(player);
                            case 4 ->
                                BossManager.gI().showListBoss(player);
                            case 5 ->
                                Input.gI().createFormBuffVND(player);
                        }
                    }
                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN -> {
                        switch (select) {
                            case 0 -> {
                                Clan clan = player.clan;
                                clan.deleteDB(clan.id);
                                Manager.CLANS.remove(clan);
                                player.clan = null;
                                player.clanMember = null;
                                ClanService.gI().sendMyClan(player);
                                ClanService.gI().sendClanId(player);
                                Service.gI().sendThongBao(player, "Đã giải tán bang hội.");
                            }
                        }
                    }

                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND -> {
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsBoxCrackBall.clear();
                            Service.gI().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                    }

                    case ConstNpc.MENU_FIND_PLAYER -> {
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0 -> {
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);
                                    }
                                }
                                case 1 -> {
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x, player.location.y);
                                    }
                                }
                                case 2 ->
                                    Input.gI().createFormChangeName(player, p);
                                case 3 -> {
                                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                            "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                }
                                case 4 -> {
                                    Service.gI().sendThongBao(player, "Kik người chơi " + p.name + " thành công");
                                    Client.gI().getPlayers().remove(p);
                                    Client.gI().kickSession(p.getSession());
                                }
                            }
                        }
                    }
                    case ConstNpc.CONFIRM_TELE_NAMEC -> {
                        if (select == 0) {
                            NgocRongNamecService.gI().teleportToNrNamec(player);
                            player.inventory.subGemAndRuby(50);
                            Service.gI().sendMoney(player);
                        }
                    }
                    case ConstNpc.MA_BAO_VE -> {
                        if (select == 0) {
                            if (player.mbv == 0) {
                                if (player.inventory.gold >= 30000) {
                                    player.inventory.gold -= 30000;
                                    Service.gI().sendMoney(player);
                                    player.mbv = player.idMark.getMbv();
                                    player.baovetaikhoan = true;
                                    Service.gI().sendThongBao(player, "Kích hoạt thành công, tài khoản đang được bảo vệ");
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ tiền để kích hoạt bảo vệ tài khoản");
                                }
                            } else {
                                if (player.baovetaikhoan) {
                                    player.baovetaikhoan = false;
                                    Service.gI().sendThongBao(player, "Chức năng bảo vệ tài khoản đang tắt");
                                } else {
                                    player.baovetaikhoan = true;
                                    Service.gI().sendThongBao(player, "Tài khoản đang được bảo vệ");
                                }
                            }
                        }
                    }
                    case ConstNpc.RUONG_GO -> {
                        int size = player.textRuongGo.size();
                        if (size > 0) {
                            String menuselect = "OK [" + (size - 1) + "]";
                            if (size == 1) {
                                menuselect = "OK";
                            }
                            NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1, player.textRuongGo.get(size - 1), menuselect);
                            player.textRuongGo.remove(size - 1);
                        }
                    }
                    case ConstNpc.MENU_XUONG_TANG_DUOI -> {
                        if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX && player.zone.map.mapId != 120) {
                            ChangeMapService.gI().changeMap(player, player.zone.map.mapIdNextMabu((short) player.zone.map.mapId), -1, -1, 100);
                        }
                    }
                }
            }
        };
    }

}
