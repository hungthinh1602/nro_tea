package models.task;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


public class SubTaskMain {

    public short count;

    public String name;

    public short maxCount;

    public String notify;

    public byte npcId;

    public short mapId;

    public SubTaskMain() {
    }

    public SubTaskMain(SubTaskMain stm) {
        this.count = 0;
        this.name = stm.name;
        this.maxCount = stm.maxCount;
        this.npcId = stm.npcId;
        this.mapId = stm.mapId;
        this.notify = stm.notify;
    }

}
