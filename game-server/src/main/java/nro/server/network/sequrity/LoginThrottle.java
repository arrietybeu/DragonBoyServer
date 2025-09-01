package nro.server.network.sequrity;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static nro.server.utils.Utils.humanize;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class LoginThrottle {

    private static final Logger log = LoggerFactory.getLogger(LoginThrottle.class);

    private static final Map<String, Entry> byIp = new ConcurrentHashMap<>();

    private static final long[] BACKOFF_STEPS_MS = {
            5_000L,        // 1)  5s
            10_000L,       // 2) 10s
            30_000L,       // 3) 30s
            60_000L,       // 4) 1m
            1_800_000L,    // 5) 30m
            3_600_000L,    // 6) 1h
            86_400_000L    // 7) 1d
    };

    private static final int MAX_FAILS_CAP = 10_000;

    public static long getRemainingMs(String ip) {
        Entry e = byIp.get(ip);
        if (e == null) return 0L;
        long now = System.currentTimeMillis();
        long remain = e.blockedUntilMs - now;
        return remain > 0 ? remain : 0L;
    }

    public static boolean isThrottled(String ip) {
        return getRemainingMs(ip) > 0;
    }

    public static void onLoginFail(String ip) {
        long now = System.currentTimeMillis();
        Entry e = byIp.computeIfAbsent(ip, k -> new Entry());

        synchronized (e) {
            int before = e.failCount;
            if (e.failCount < MAX_FAILS_CAP) e.failCount++;
            int after = e.failCount;

            int blocksBefore = before / 5;  // số “nấc” trước khi tăng
            int blocksAfter = after / 5;  // số “nấc” sau khi tăng
            boolean newBlock = blocksAfter > blocksBefore; // chỉ khi đạt bội số 5 mới lên nấc

            e.lastFailAtMs = now;

            if (newBlock) {
                int idx = Math.min(blocksAfter - 1, BACKOFF_STEPS_MS.length - 1);
                long dur = BACKOFF_STEPS_MS[idx];

                // NẾU vẫn đang bị block thì cộng dồn từ mốc cũ, còn không thì tính từ hiện tại
                long base = Math.max(now, e.blockedUntilMs);
                e.blockedUntilMs = base + dur;

                long remain = e.blockedUntilMs - now;
                log.info("[Throttle] ip={} fail={} blocks={} idx={} dur={}ms remain={}ms (~{})",
                        ip, after, blocksAfter, idx, dur, remain, humanize(remain));
            } else {
                long remain = Math.max(0, e.blockedUntilMs - now);
                log.debug("[Throttle] ip={} fail={} (no new block). remain={}ms (~{})",
                        ip, after, remain, humanize(remain));
            }
        }
    }

    /**
     * Gọi khi đăng nhập thành công: reset đếm & bỏ chờ
     */
    public static void onLoginSuccess(String ip) {
        Entry e = byIp.remove(ip);
        if (e != null) {
            synchronized (e) {
                e.failCount = 0;
                e.blockedUntilMs = 0L;
            }
        }
    }

    private static final class Entry {
        // tổng số lần sai (tính dồn)
        int failCount;

        // đang bị chờ đến mốc thời gian này
        long blockedUntilMs;

        // thời điểm lần sai gần nhất
        long lastFailAtMs;
    }
}
