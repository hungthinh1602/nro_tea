package managers.boss;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


public class TeaEventManager extends BossManager {

    private static TeaEventManager instance;

    public static TeaEventManager gI() {
        if (instance == null) {
            instance = new TeaEventManager();
        }
        return instance;
    }
}
