package nro.server.world;

import lombok.Getter;

/**
 * @author Arriety
 */
public record InstanceHandler(@Getter WorldMapInstance instance) {

    public void onUpdate(long now) {
        // TODO: Gọi logic ECS như SystemManager.update(instance, now);
    }

    public void onEnter(Object entity) {
        // TODO: Gán component position, trigger system...
    }

    public void onLeave(Object entity) {
        // TODO: Xóa entity, clear component ECS...
    }

    public void onTimeout() {
        // TODO: Huỷ instance, phát thưởng nếu cần
    }

}
