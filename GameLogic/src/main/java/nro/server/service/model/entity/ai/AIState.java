package nro.server.service.model.entity.ai;

public enum AIState {

    // đứng yên, không làm gì
    IDLE,

    // đuổi theo mục tiêu đang được target
    CHASING,

    // tấn công mục tiêu đang được target
    ATTACKING,

    // bị choáng hoặc không thể di chuyển
    STUNNED,

    // đang rặn kỹ năng đặc biệt
    CASTING,

    // boss sủi, bỏ chạy dành cho con ăn trộm
    ESCAPING,

    // boss vào trạng thái không thể bị tấn công (bất tử)
    INVULNERABLE,

    // boss khích đểu người chơi cà khẹ
    TAUNTING,

    // chết mất tiu
    DEAD

}
