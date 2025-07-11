package models.player;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */
import database.daos.SuperRankDAO;
import java.util.ArrayList;
import java.util.List;
import services.tournament.SuperRankService;
import services.map.NpcService;

public class SuperRank {

    private Player player;
    public boolean nhanGiai;
    public int rank;
    public int win;
    public int lose;
    public List<String> history;
    public List<Long> lastTime;
    public long lastPKTime;
    public long lastRewardTime;
    public int ticket = 3;

    public SuperRank(Player player) {
        this.player = player;
        this.history = new ArrayList<>();
        this.lastTime = new ArrayList<>();
    }

    public void history(String text, long lastTime) {
        if (this.history.size() > 4) {
//            this.history.removeFirst();
//            this.lastTime.removeFirst();
        }
        this.history.add(text);
        this.lastTime.add(lastTime);
    }

    public void reward() {
        int rw = SuperRankService.gI().reward(rank);
        if (rw != -1 && nhanGiai == false) {
            NpcService.gI().createTutorial(player, -1, "Bạn đang ở TOP " + rank + " võ đài Siêu Hạng và nhận được " + rw + " ngọc");
            player.inventory.gem += rw;
            nhanGiai = true;
            lastRewardTime = System.currentTimeMillis();
            SuperRankDAO.updatePlayer(player);
        }
    }

    public void dispose() {
        history.clear();
        lastTime.clear();
        win = -1;
        lose = -1;
        lastPKTime = -1;
        player = null;
    }
}
