package models.player;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import lombok.Getter;
import lombok.Setter;

public class Traning {

    @Setter
    @Getter
    private int top;

    @Setter
    @Getter
    private int topWhis;

    @Setter
    @Getter
    private int time;

    @Setter
    @Getter
    private long lastTime;

    @Setter
    @Getter
    private int lastTop;

    @Setter
    @Getter
    private long lastRewardTime;

}
