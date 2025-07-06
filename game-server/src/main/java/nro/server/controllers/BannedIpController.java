package nro.server.controllers;

import nro.commons.utils.NetworkUtils;
import nro.server.dao.BannedIpDAO;
import nro.server.model.session.BannedIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Arriety
 */
public class BannedIpController {

    private static final Logger log = LoggerFactory.getLogger(BannedIpController.class);

    private static Set<BannedIP> banList;

    public static void start() {
        clean();
        load();
    }

    private static void clean() {
        BannedIpDAO.cleanExpiredBans();
    }

    public static void load() {
        reload();
    }

    public static void reload() {
        banList = BannedIpDAO.getAllBans();
        log.info("BannedIpController loaded {} IP bans.", banList.size());
    }

    public static boolean isBanned(String ip) {
        for (BannedIP ipBan : banList) {
            if (ipBan.isActive() && NetworkUtils.checkIPMatching(ipBan.getMask(), ip))
                return true;
        }
        return false;
    }

    public static boolean banIp(String ip) {
        return banIp(ip, null);
    }

    public static boolean banIp(String ip, Timestamp expireTime) {
        BannedIP ipBan = new BannedIP();
        ipBan.setMask(ip);
        ipBan.setTimeEnd(expireTime);
        return banList.add(ipBan) && BannedIpDAO.insert(ipBan);
    }

    public static boolean addOrUpdateBan(BannedIP ipBan) {
        if (ipBan.getId() == null) {
            if (BannedIpDAO.insert(ipBan)) {
                banList.add(ipBan);
                return true;
            }
            return false;
        }
        return BannedIpDAO.update(ipBan);
    }

    public static boolean unbanIp(String ip) {
        Iterator<BannedIP> it = banList.iterator();
        while (it.hasNext()) {
            BannedIP ipBan = it.next();
            if (ipBan.getMask().equals(ip)) {
                if (BannedIpDAO.remove(ipBan)) {
                    it.remove();
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
