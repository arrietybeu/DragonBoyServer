package nro.server.service.model.entity;

import nro.server.system.LogServer;

public final class SafeCallback {

    /**
     * Chạy callback có kiểm tra an toàn entity
     *
     * @param entity   Entity thực hiện (Player, Boss, ...)
     * @param runnable Logic cần chạy nếu entity còn sống
     */
    public static void run(Entity entity, Runnable runnable) {
        try {
            if (entity == null || entity.getPoints() == null
                    || entity.getPoints().isDead() || entity.getArea() == null
                    || entity.getPoints() == null || entity.getSkills() == null
                    || entity.getFashion() == null || entity.getFusion() == null) {
                return;
            }
            runnable.run();
        } catch (Exception e) {
            LogServer.LogException("SafeCallback error for entity " + (entity != null ? entity.getName() : "null"), e);
        }
    }
}