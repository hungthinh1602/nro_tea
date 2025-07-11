package server;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import Bot.BotManager;
import utils.FileRunner;
import managers.boss.BrolyManager;
import database.daos.HistoryTransactionDAO;
import managers.boss.BossManager;
import managers.boss.OtherBossManager;
import managers.boss.TreasureUnderSeaManager;
import managers.boss.SnakeWayManager;
import managers.boss.RedRibbonHQManager;
import managers.boss.GasDestroyManager;
import managers.boss.YardartManager;
import managers.boss.ChristmasEventManager;
import managers.boss.FinalBossManager;
import managers.boss.HalloweenEventManager;
import managers.boss.HungVuongEventManager;
import managers.boss.LunarNewYearEventManager;
import managers.boss.SkillSummonedManager;
import managers.boss.TrungThuEventManager;
import java.io.IOException;
import interfaces.ISession;
import network.Network;
import network.io.MyKeyHandler;
import network.session.MySession;
import services.player.ClanService;
import services.dungeon.NgocRongNamecService;
import utils.Logger;
import utils.TimeUtil;

import java.util.*;
import managers.tournament.The23rdMartialArtCongressManager;
import managers.tournament.DeathOrAliveArenaManager;
import event.EventManager;
import database.daos.EventDAO;
import database.daos.PlayerDAO;
import managers.tournament.WorldMartialArtsTournamentManager;
import network.io.MessageSendCollect;
import managers.ShenronEventManager;
import interfaces.ISessionAcceptHandler;
import managers.ConsignShopManager;
import managers.SuperRankManager;
import managers.boss.TeaEventManager;
import models.npc.TaiXiu;
import models.player.Player;
import services.Service;
import services.TopService;

public class ServerManager {

    public static String timeStart;

    public static final Map CLIENTS = new HashMap();

    public static String NAME = "Local";
    public static String IP = "127.0.0.1";
    public static int PORT = 14445;

    private static ServerManager instance;

    public static boolean isRunning;

    public void init() {
        Manager.gI();
        HistoryTransactionDAO.deleteHistory();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) {
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
        ServerManager.gI().run();
    }

    public void run() {
        isRunning = true;
        activeServerSocket();
        activeCommandLine();
        new Thread(NgocRongNamecService.gI(), "Update NRNM").start();
        new Thread(SuperRankManager.gI(), "Update Super Rank").start();
        new Thread(The23rdMartialArtCongressManager.gI(), "Update DHVT23").start();
        new Thread(DeathOrAliveArenaManager.gI(), "Update Võ Đài Sinh Tử").start();
        new Thread(WorldMartialArtsTournamentManager.gI(), "Update WMAT").start();
        new Thread(AutoMaintenance.gI(), "Update Bảo Trì Tự Động").start();
        new Thread(ShenronEventManager.gI(), "Update Shenron").start();
        BossManager.gI().loadBoss();
        Manager.MAPS.forEach(models.map.Map::initBoss);
        EventManager.gI().init();
        new Thread(BossManager.gI(), "Update boss").start();
        new Thread(YardartManager.gI(), "Update yardart boss").start();
        new Thread(FinalBossManager.gI(), "Update final boss").start();
        new Thread(SkillSummonedManager.gI(), "Update Skill-summoned boss").start();
        new Thread(BrolyManager.gI(), "Update broly boss").start();
        new Thread(OtherBossManager.gI(), "Update other boss").start();
        new Thread(RedRibbonHQManager.gI(), "Update reb ribbon hq boss").start();
        new Thread(TreasureUnderSeaManager.gI(), "Update treasure under sea boss").start();
        new Thread(SnakeWayManager.gI(), "Update snake way boss").start();
        new Thread(GasDestroyManager.gI(), "Update gas destroy boss").start();
        new Thread(TrungThuEventManager.gI(), "Update trung thu event boss").start();
        new Thread(HalloweenEventManager.gI(), "Update halloween event boss").start();
        new Thread(ChristmasEventManager.gI(), "Update christmas event boss").start();
        new Thread(HungVuongEventManager.gI(), "Update Hung Vuong event boss").start();
        new Thread(LunarNewYearEventManager.gI(), "Update lunar new year event boss").start();
        new Thread(TeaEventManager.gI(), "Update teamobi event boss").start();
        new Thread(BotManager.gI(), "Thread Bot Game").start();
        new Thread(TopService.gI(), "Thread TOP").start();
        new Thread(TaiXiu.gI(), "Thread TaiXiu").start();
        TaiXiu taixiu = new TaiXiu();
        Thread thread = new Thread(taixiu);
        thread.setName("Thread TaiXiu");
        thread.start();
    }

    private void activeServerSocket() {
        try {
            Network.gI().init().setAcceptHandler(new ISessionAcceptHandler() {
                @Override
                public void sessionInit(ISession is) {
                    if (!canConnectWithIp(is.getIP())) {
                        is.disconnect();
                        return;
                    }
                    is.setMessageHandler(Controller.gI())
                            .setSendCollect(new MessageSendCollect())
                            .setKeyHandler(new MyKeyHandler())
                            .startCollect().startQueueHandler();
                }

                @Override
                public void sessionDisconnect(ISession session) {
                    Client.gI().kickSession((MySession) session);
                }
            }).setTypeSessionClone(MySession.class)
                    .setDoSomeThingWhenClose(() -> {
                        Logger.error("SERVER CLOSE\n");
                        System.exit(0);
                    })
                    .start(PORT);
        } catch (Exception e) {
        }
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void disconnect(MySession session) {
        Object o = CLIENTS.get(session.ipAddress);
        if (o != null) {
            int n = Integer.parseInt(String.valueOf(o));
            n--;
            if (n < 0) {
                n = 0;
            }
            CLIENTS.put(session.ipAddress, n);
        }
    }

    public void close() {
        isRunning = false;
        try {
            ClanService.gI().close();
        } catch (Exception e) {
            Logger.error("Lỗi save clan!\n");
        }
        Client.gI().close();
        ConsignShopManager.gI().save();
        EventDAO.save();
        Logger.success("SUCCESSFULLY MAINTENANCE!\n");

        if (AutoMaintenance.isRunning) {
            AutoMaintenance.isRunning = false;
            try {
                String batchFilePath = "run.bat";
                FileRunner.runBatchFile(batchFilePath);
            } catch (IOException e) {
            }
        }
        System.exit(0);
    }

    private void activeCommandLine() {

        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String line = sc.nextLine();
                if (line.equals("baotri")) {
                    Maintenance.gI().start(10);
                } else if (line.equals("athread")) {
                    ServerNotify.gI().notify("TIEN DUNG DEP TRAI DEBUG SERVER: " + Thread.activeCount());
                } else if (line.equals("nplayer")) {
                    Logger.error("Player in game: " + Client.gI().getPlayers().size() + "\n");
                } else if (line.equals("admin")) {
                    new Thread(() -> {
                        Client.gI().close();
                    }).start();
                } else if (line.startsWith("kick")) {
                    new Thread(() -> {
                        try {

                            Client.gI().cloneMySessionNotConnect();
                            try {
                                Logger.error("Đang tiến hành lưu data người dùng");
                                for (Player pl : Client.gI().getPlayers()) {
                                    try {
                                        if (pl != null && pl.isPl()) {
                                            PlayerDAO.updatePlayer(pl);
                                            Thread.sleep(50);
                                        }
                                    } catch (Exception exx) {
                                        exx.printStackTrace();
                                    }
                                }
                                Thread.sleep(1000);
                                Logger.success("Lưu dữ liệu người dùng thành công");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Logger.error("Lỗi lưu dữ liệu người dùng");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.error("Thông báo: Lỗi khi kick ss.\n");
                        }
                    }).start();
                } else if (line.startsWith("bang")) {
                    new Thread(() -> {
                        try {
                            ClanService.gI().close();
                            Logger.error("Save " + Manager.CLANS.size() + " bang");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.error("Thông báo: lỗi lưu dữ liệu bang hội.\n");
                        }
                    }).start();
                } else if (line.startsWith("a")) {
                    String a = line.replace("a ", "");
                    Service.gI().sendThongBaoAllPlayer(a);
                }
            }
        }, "Active line").start();
    }
}
