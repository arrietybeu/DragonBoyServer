package nro.server.service.core.effect;

@SuppressWarnings("ALL")
public enum EffectType {

    STUN(false),                   // Choáng (VD: Thái Dương Hạ San)
    ROOT(false),                   // Trói không di chuyển
    SILENCE(false),                // Không thể dùng skill
    SLEEP(false),                  // Thôi miên
    POLYMORPH(false),              // Biến thành Socola, Cà rốt...
    SHIELD(false),                 // Khiên năng lượng
    HEAL(false),                   // Hồi máu
    BUFF_DAMAGE(false),            // Tăng sát thương
    BUFF_DEFENSE(false),           // Tăng giáp
    BUFF_SPEED(false),             // Tăng tốc độ di chuyển
    INVISIBILITY(false),           // Tàng hình
    TRANSFORM(false),              // Biến hình (khỉ, hợp thể, v.v.)
    TELEPORT_ATTACK(false),        // Dịch chuyển tức thời + Gây dame
    AOE_DAMAGE(false),             // Gây sát thương diện rộng
    CHAT_ON_EFFECT(false),         // Hiện câu thoại khi mặc skin
    AURA_BUFF_ALLY(false),         // Cải trang buff đồng minh xung quanh
    AURA_DEBUFF_ENEMY(false),      // Cải trang debuff địch xung quanh
    IMMUNE(false);                 // Miễn nhiễm hiệu ứng

    private final boolean isDebuff;

    EffectType(boolean isDebuff) {
        this.isDebuff = isDebuff;
    }

}