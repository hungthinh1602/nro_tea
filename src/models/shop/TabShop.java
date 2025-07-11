package models.shop;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import java.util.ArrayList;
import java.util.List;

public class TabShop {

    public Shop shop;

    public int id;

    public String name;

    public List<ItemShop> itemShops;

    public TabShop() {
        this.itemShops = new ArrayList<>();
    }

    public TabShop(TabShop tabShop, int gender) {
        this.itemShops = new ArrayList<>();
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name;

        for (ItemShop itemShop : tabShop.itemShops) {
            if (itemShop.temp.gender == gender || itemShop.temp.gender > 2) {
                this.itemShops.add(new ItemShop(itemShop));
            }
        }
    }

    public TabShop(TabShop tabShop) {
        this.itemShops = new ArrayList<>();
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name;
        for (ItemShop itemShop : tabShop.itemShops) {
            this.itemShops.add(new ItemShop(itemShop));
        }
    }

    public void dispose() {
        this.shop = null;
        this.name = null;
        if (this.itemShops != null) {
            for (ItemShop is : this.itemShops) {
                is.dispose();
            }
            this.itemShops.clear();
        }
        this.itemShops = null;
    }

}
