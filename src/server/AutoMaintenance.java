package server;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import java.time.LocalTime;
import utils.Logger;

public class AutoMaintenance extends Thread {

    public static boolean AutoMaintenance = false; // Bật/tắt bảo trì tự động
    public static final int hours = 14; // Giờ bảo trì
    public static final int mins = 37; // Phút bảo trì
    private static AutoMaintenance instance;
    public static boolean isRunning;

    public static AutoMaintenance gI() {
        if (instance == null) {
            instance = new AutoMaintenance();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning && !isRunning) {
            try {
                if (AutoMaintenance) {
                    LocalTime currentTime = LocalTime.now();
                    if (currentTime.getHour() == hours && currentTime.getMinute() == mins) {
                        Logger.log(Logger.PURPLE, "Đang tiến hành quá trình bảo trì tự động\n");
                        Maintenance.gI().start(60);
                        isRunning = true;
                        AutoMaintenance = false;
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

}
