package consts;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


public class ConstPlayer {

    public static final int[] HEADMONKEY = {192, 195, 196, 199, 197, 200, 198};
    
     public static final byte[][] AURABIENHINH = {
        {0},   //td
        {0},  //nm
        {0}   //xd
    };
    public static final short[][] HEADBIENHINH = {
        {1665, 1667, 1669, 1672, 1675, 1675, 1675}, // TD
        {1700, 1701, 1702, 1703, 1704, 1704, 1704}, // NM
        {1678, 1680, 1683, 1685, 1687, 1687, 1687} // XD
    };
    public static final short[] BIENHINH = {1445, 1308, 1303}; // TD NM XD

    public static final short[] BODYBIENHINH = {1661, 1698, 1663}; // TD NM XD
    public static final short[] LEGBIENHINH = {1662, 1699, 1664}; // TD NM XD

    public static final byte TRAI_DAT = 0;
    public static final byte NAMEC = 1;
    public static final byte XAYDA = 2;

    //type pk
    public static final byte NON_PK = 0;
    public static final byte PK_PVP = 3;
    public static final byte PK_PVP_2 = 4;
    public static final byte PK_ALL = 5;

    //type fushion
    public static final byte NON_FUSION = 0;
    public static final byte LUONG_LONG_NHAT_THE = 4;
    public static final byte HOP_THE_PORATA = 6;
    public static byte HOP_THE_PORATA2 = 8;
}
