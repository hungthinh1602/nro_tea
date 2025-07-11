package models.task;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


public class ClanTaskTemplate {

    public int id;
    public String name;
    public int[][] count;

    public ClanTaskTemplate() {
        this.count = new int[5][2];
    }

}
