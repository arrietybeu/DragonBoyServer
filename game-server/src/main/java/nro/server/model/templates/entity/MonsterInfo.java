package nro.server.model.templates.entity;

/**
 * @param id
 * @param templateID
 * @param status
 * @param sys
 * @param level
 * @param levelBoss
 * @param isBoss
 * @param x
 * @param y
 * @param hp
 * @param maxHp
 * @author Arriety
 */
public record MonsterInfo(int templateID, byte level, long maxHp,
                          short x, short y) {
}
