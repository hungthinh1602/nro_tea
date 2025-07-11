package models.radar;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


public class OptionCard {

    public int id;
    public int param;
    public byte active;

    public OptionCard(int i, int p, byte a) {
        id = i;
        param = p;
        active = a;
    }

    @Override
    public String toString() {
        final String n = "\"";
        return "{"
                + n + "id" + n + ":" + n + id + n + ","
                + n + "active" + n + ":" + n + active + n + ","
                + n + "param" + n + ":" + n + param + n
                + "}";
    }
}
