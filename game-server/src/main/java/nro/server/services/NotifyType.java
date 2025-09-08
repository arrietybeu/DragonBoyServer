package nro.server.services;

public enum NotifyType {
    /**
     * Thông báo mở ra 1 form UI popup
     */
    UI_FORM,

    /**
     * Thông báo có con mèo bay xuống (kiểu animation đặc biệt)
     */
    FLYING_CAT,

    /**
     * Thông báo ở thanh bên dưới màn hình (bottom bar)
     */
    BOTTOM_BAR,

    /**
     * Thông báo qua chat thế giới
     */
    WORLD_CHAT
}
