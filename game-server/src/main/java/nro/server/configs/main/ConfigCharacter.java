package nro.server.configs.main;

import nro.commons.configuration.Property;

/**
 * @author Arriety
 */
public class ConfigCharacter {

    @Property(key = "character.inventory.body.size", defaultValue = "1")
    public static int INVENTORY_BODY_SIZE;

    @Property(key = "character.inventory.bag.size", defaultValue = "1")
    public static int INVENTORY_BAG_SIZE;

    @Property(key = "character.inventory.box.size", defaultValue = "1")
    public static int INVENTORY_BOX_SIZE;

    @Property(key = "character.creation.currencies.gold", defaultValue = "0")
    public static int CREATION_GOLD;

    @Property(key = "character.creation.currencies.gem", defaultValue = "0")
    public static int CREATION_GEM;

    @Property(key = "character.creation.currencies.ruby", defaultValue = "0")
    public static int CREATION_RUBY;

    @Property(key = "character.creation.points.hp", defaultValue = "0")
    public static int CREATION_HP;

    @Property(key = "character.creation.points.mp", defaultValue = "0")
    public static int CREATION_MP;

    @Property(key = "character.creation.points.damage", defaultValue = "0")
    public static int CREATION_DAMAGE;

    @Property(key = "character.creation.points.power", defaultValue = "0")
    public static int CREATION_POWER;

    @Property(key = "character.creation.points.potential", defaultValue = "0")
    public static int CREATION_POTENTIAL;

    @Property(key = "character.creation.points.stamina", defaultValue = "0")
    public static int CREATION_STAMINA;

    @Property(key = "character.creation.points.defense", defaultValue = "0")
    public static int CREATION_DEFENSE;

    @Property(key = "character.creation.points.crit_chance", defaultValue = "0")
    public static int CREATION_CRIT_CHANCE;

    @Property(key = "character.creation.task.start_id", defaultValue = "0")
    public static int CREATION_TASK_START_ID;

    @Property(key = "character.creation.task.start_index", defaultValue = "0")
    public static int CREATION_TASK_START_INDEX;

    @Property(key = "character.creation.magic_tree.level", defaultValue = "1")
    public static int MAGIC_TREE_LEVEL;

    @Property(key = "character.creation.magic_tree.peas", defaultValue = "0")
    public static int MAGIC_TREE_PEAS;

}
