package nro.consts;

@SuppressWarnings("ALL")
public class ConstError {

    // ======= Lỗi liên quan đến Database =======
    public static final int ERROR_LOADING_DATABASE_FOR_PLAYER = 403; // Lỗi tải dữ liệu người chơi từ Database
    public static final int ERROR_SAVING_PLAYER_DATA = 404;            // Lỗi lưu dữ liệu người chơi vào Database

    // ======= Lỗi xác thực & tài khoản =======
    public static final int ERROR_INVALID_CREDENTIALS = 401;           // Thông tin đăng nhập không hợp lệ
    public static final int ERROR_ACCOUNT_LOCKED = 402;                // Tài khoản bị khóa

    // ======= Lỗi tương tác game (giao dịch, vật phẩm, kỹ năng) =======
    public static final int ERROR_INVALID_ITEM = 501;                  // Vật phẩm không hợp lệ
    public static final int ERROR_INSUFFICIENT_GOLD = 502;               // Không đủ vàng để giao dịch
    public static final int ERROR_INVALID_SKILL = 503;                 // Kỹ năng không hợp lệ hoặc chưa học

    // ======= Lỗi giao tiếp, chat =======
    public static final int ERROR_CHAT_MESSAGE_TOO_LONG = 601;         // Tin nhắn chat quá dài
    public static final int ERROR_PLAYER_NOT_FOUND = 602;              // Không tìm thấy người chơi

    // ======= Lỗi tương tác trong chế độ chiến đấu =======
    public static final int ERROR_CANNOT_ATTACK_FRIEND = 701;          // Không thể tấn công bạn bè
    public static final int ERROR_INVALID_TARGET = 702;                // Mục tiêu tấn công không hợp lệ


    // ======= Các lỗi khác =======
    public static final int ERROR_UNKNOWN = 999;

}
