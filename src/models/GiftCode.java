package models;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import models.item.Item.ItemOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import models.player.Player;

public class GiftCode {

    public String code;
    public int countLeft;
    public int id;
    public boolean allGender;
    public HashMap<Integer, Integer> detail = new HashMap<>();
    public HashMap<Integer, ArrayList<ItemOption>> option = new HashMap<>();
    public Timestamp datecreate;
    public Timestamp dateexpired;

    public boolean isUsedGiftCode(Player player) {
        return player.giftCode.isUsedGiftCode(code);
    }

    public boolean timeCode() {
        return this.datecreate.getTime() > this.dateexpired.getTime();
    }
}
