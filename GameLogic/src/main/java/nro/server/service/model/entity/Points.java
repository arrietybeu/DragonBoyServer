package nro.server.service.model.entity;

import lombok.Getter;
import lombok.Setter;
import nro.server.service.model.template.item.ItemOption;
import nro.server.service.model.entity.monster.Monster;

@Getter
@Setter
@SuppressWarnings("ALL")
public abstract class Points {

    // chỉ số cơ bản, chỉ số gốc
    protected int baseHP, baseMP;
    protected int baseDamage;
    protected int baseDefense;
    protected byte baseCriticalChance;
    protected byte movementSpeed;
    protected short stamina;

    // chi so hien tai
    protected long currentHP, currentMP, currentDamage;

    // chi so
    protected long maxHP, maxMP;
    protected short maxStamina;

    // chi so tong hop cam lon
    protected long totalDefense;
    protected byte totalCriticalChance;

    protected short expPerStatIncrease;
    protected byte hpPer1000Potential;
    protected byte mpPer1000Potential;
    protected byte damagePer1000Potential;

    protected short eff5BuffHp, eff5BuffMp;

    // power , tiem nang
    protected long power, potentialPoints;

    protected short tlHutHp, tlHutMp, tlHutHpMob;

    protected int percentExpPotentia;

    protected boolean isHaveMount;

    public boolean isDead() {
        return this.currentHP <= 0;
    }

    public void setCurrentHp(long hp) {
        if (hp < 0) {
            this.currentHP = 0;
        } else this.currentHP = Math.min(hp, this.maxHP);
    }

    public void setCurrentMp(long mp) {
        if (mp < 0) {
            this.currentMP = 0;
        } else this.currentMP = Math.min(mp, this.maxMP);
    }

    public void subCurrentHp(long hp) {
        this.currentHP -= hp;
        if (this.currentHP < 0) {
            this.currentHP = 0;
        }
    }

    /// tính toán sát thương cuối cùng khi tấn công (đã áp dụng kỹ năng nếu có)
    public abstract long getDameAttack();

    /// call resetBaseStats + applyItemBonuses để cập nhật lại toàn bộ chỉ số hiện tại
    public abstract void calculateStats();

    /// thiết lập lại các chỉ số về gốc (baseHP, baseMP...), chưa tính bonus từ item
    public abstract void resetBaseStats();

    /// trừ MP khi đang bay, có thể bỏ qua nếu đang cưỡi thú (mount)
    public abstract void reduceMPWhenFlying();

    /// Áp dụng chỉ số cộng thêm từ item trang bị (HP, MP, Damage, Defense...)
    public abstract void applyItemBonuses();

    /// tính giá trị cộng từ ItemOption dựa trên kiểu Option (phần trăm, cộng cố định, ...)
    public abstract long getParamOption(long currentPoint, ItemOption option);

    /// tính sát thương kỹ năng
    public abstract long getDameSkill();

    /// add EXP (sức mạnh hoặc tiềm năng), theo type: 0 = Power, 1 = Potential, 2 = cả 2
    public abstract void addExp(int type, long exp);

    /// Trừ EXP (Power hoặc Potential), theo type
    public abstract void subExp(int type, long exp);

    /// khi chết, hồi sinh và đưa về map làng tương ứng theo giới tính
    public abstract void returnTownFromDead();

    /// đánh dấu chết, lock di chuyển, hủy skill, thông báo cho khu vực
    public abstract void setDie();

    /// hồi sinh hoàn toàn, mở khóa di chuyển, gửi trạng thái về client
    public abstract void setLive();

    /// nang cấp chỉ số dựa theo loại nâng cấp (HP, MP, Damage...), tốn tiềm năng
    public abstract void upPotentialPoint(int type, int point);

    /// thực hiện kiểm tra và nâng cấp nếu đủ điều kiện, return true nếu thành công
    public abstract boolean isUpgradePotential(int type, long potentiaUse, final long currentPoint, int point);

    /// hồiii đầy máu và năng lượng
    public abstract void healPlayer();

    /// tính điểm tiềm năng kiếm được sau khi tấn công quái, có xét chênh lệch cấp độ
    public abstract long getPotentialPointsAttack(Monster monster, long damage);

}
