package nro.service.model.template;

public record NpcTemplate(int id, String name, int head, int body, int leg, short avatarId, String chat) {

    public record NpcInfo(int npcId, int x, int y, int status, int avatar) {
    }

}
