package event.ievent;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


public interface IEvent {

    void init();

    void npc();

    void createNpc(int mapId, int npcId, int x, int y);

    void boss();

    void createBoss(int bossId, int... total);

    void itemMap();

    void itemBoss();
}
