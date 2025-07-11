package managers.boss;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


public class TrungThuEventManager extends BossManager {

    private static TrungThuEventManager instance;

    public static TrungThuEventManager gI() {
        if (instance == null) {
            instance = new TrungThuEventManager();
        }
        return instance;
    }

}
