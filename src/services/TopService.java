package services;

import database.AlyraManager;
import java.sql.Connection;
import server.Manager;
import utils.Logger;

public class TopService implements Runnable {

    private static TopService i;

    public static TopService gI() {
        if (i == null) {
            i = new TopService();
        }
        return i;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (Manager.timeRealTop + 1000 < System.currentTimeMillis()) {
                    Manager.timeRealTop = System.currentTimeMillis();
                    try (Connection con = AlyraManager.getConnection()) {
                        Manager.topSM = Manager.realTop(Manager.queryTopSM, con);
                        Manager.topNV = Manager.realTop(Manager.queryTopNV, con);
                        Manager.topNap = Manager.realTop(Manager.queryTopNap, con);
                        Manager.topsk = Manager.realTop(Manager.queryTopsk, con);
                    } catch (Exception ignored) {
                        Logger.error("Lỗi đọc top");
                    }
                }
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
        }
    }

}
