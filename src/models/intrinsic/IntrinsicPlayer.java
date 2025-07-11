package models.intrinsic;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import services.player.IntrinsicService;

public class IntrinsicPlayer {

    public byte countOpen;

    public Intrinsic intrinsic;

    public IntrinsicPlayer() {
        this.intrinsic = IntrinsicService.gI().getIntrinsicById(0);
    }

    public void dispose() {
        this.intrinsic = null;
    }
}
