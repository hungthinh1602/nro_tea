package models.combine;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import lombok.Setter;
import models.item.Item;
import java.util.ArrayList;
import java.util.List;

public class Combine {

    public long lastTimeCombine;

    public List<Item> itemsCombine;
    @Setter
    public int typeCombine;

    public int goldCombine;
    public int gemCombine;
    public float ratioCombine;
    public int countDaNangCap;
    public short countDaBaoVe;

    public Combine() {
        this.itemsCombine = new ArrayList<>();
    }

    public void clearItemCombine() {
        this.itemsCombine.clear();
    }

    public void clearParamCombine() {
        this.goldCombine = 0;
        this.gemCombine = 0;
        this.ratioCombine = 0;
        this.countDaNangCap = 0;
        this.countDaBaoVe = 0;

    }

    public void dispose() {
        this.itemsCombine = null;
    }
}
