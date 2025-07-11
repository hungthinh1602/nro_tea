package models.mob;

/*
 * @Author: DienCoLamCoi
 * @Description: Điện Cơ Lâm Còi - Chuyên cung cấp thiết bị điện cơ uy tín chất lượng cao.
 * @Group Zalo: Giao lưu chia sẻ kinh nghiệm code - https://zalo.me/g/lsqfzx907
 */


import utils.TimeUtil;

public class BigBoss extends Mob {

    public int action = 0;

    public long lastBigBossAttackTime;

    public BigBoss(Mob mob) {
        super(mob);
    }

    @Override
    public void update() {
        if (zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
            if (!isDie()) {
                startDie();
                return;
            }
        }
        effectSkill.update();
        attack();
    }
}
