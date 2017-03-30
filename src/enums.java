import static processing.core.PConstants.PI;

public class enums {

    public enum EntityType {
        FIXED,
        DOOR,
        SENSOR,
        MAP_ITEM,
        GAME_LOGIC,
        ACTOR,
        DECORATION;

        public static String getStringFromType(EntityType testType) {
            if (testType == EntityType.FIXED) {
                return "FIXED";
            } else if (testType == EntityType.SENSOR) {
                return "SENSOR";
            } else if (testType == EntityType.DOOR) {
                return "DOOR";
            } else if (testType == EntityType.ACTOR) {
                return "ACTOR";
            } else if (testType == EntityType.MAP_ITEM) {
                return "MAP_ITEM";
            } else if (testType == EntityType.GAME_LOGIC) {
                return "GAME_LOGIC";
            } else if (testType == EntityType.DECORATION) {
                return "DECORATION";
            } else {
                return "FIXED";
            }
        }

        public static EntityType getTypeFromString(String testString) {
            if (testString.equals("FIXED")) {
                return EntityType.FIXED;
            } else if (testString.equals("SENSOR")) {
                return EntityType.SENSOR;
            } else if (testString.equals("DOOR")) {
                return EntityType.DOOR;
            } else if (testString.equals("ACTOR")) {
                return EntityType.ACTOR;
            } else if (testString.equals("MAP_ITEM")) {
                return EntityType.MAP_ITEM;
            } else if (testString.equals("GAME_LOGIC")) {
                return EntityType.GAME_LOGIC;
            } else if (testString.equals("DECORATION")) {
                return EntityType.DECORATION;
            } else {
                return EntityType.FIXED;
            }
        }
    }

    public enum ItemType {
        ITEM_RIFLE,
        ITEM_HANDGUN,
        NO_ITEM;

        float RIFLE_LENGTH = 20;
        float RIFLE_WIDTH = 5;

        public static String getStringFromItem(ItemType testType) {
            if (testType == ItemType.ITEM_RIFLE) {
                return "ITEM_RIFLE";
            } else if (testType == ItemType.ITEM_HANDGUN) {
                return "ITEM_HANDGUN";
            } else {
                return "NO_ITEM";
            }
        }

        public static ItemType getTypeFromString(String testString) {
            if (testString.equals("ITEM_RIFLE")) {
                return ItemType.ITEM_RIFLE;
            } else if (testString.equals("ITEM_HANDGUN")) {
                return ItemType.ITEM_HANDGUN;
            } else {
                return ItemType.NO_ITEM;
            }
        }

    }

    public enum Team {
        HUMAN, ZOMBIE, NEUTRAL, NONE;

        int[] HUMAN_COLOR = {100, 100, 200};
        int[] ZOMBIE_COLOR = {100, 200, 100};
        int[] NEUTRAL_COLOR = {150, 150, 150};

    }

    public enum ActorType {
        CIVILIAN,
        SOLDIER,
        BASIC_ZOMBIE,
        BIG_ZOMBIE;

        float SOLDIER_HEALTH = 100;
        float SOLDIER_MAXSPEED = 8;
        float SOLDIER_ACCEL = 150;
        float SOLDIER_FOV = PI / 2;
        float SOLDIER_MAXSIGHTRANGE = 1000;
        float SOLDIER_RADIUS = 30;

        float ZOMBIE_HEALTH = 2500;
        float ZOMBIE_MAXSPEED_MULTIPLIER = 1.2f;
        float ZOMBIE_ACCEL_MULTIPLIER = 1;
        float ZOMBIE_FOV = 3 * PI / 4;
        float ZOMBIE_MAXSIGHTRANGE = 1000;
        float ZOMBIE_RADIUS = 30;

        float BIGZOMBIE_HEALTH = 25000;
        float BIGZOMBIE_MAXSPEED_MULTIPLIER = 0.95f;
        float BIGZOMBIE_ACCEL_MULTIPLIER = 8;
        float BIGZOMBIE_FOV = 3 * PI / 4;
        float BIGZOMBIE_MAXSIGHTRANGE = 800;
        float BIGZOMBIE_RADIUS = 45;
        float BIGZOMBIE_DENSITY = 4;

        float CIVILIAN_HEALTH = 80;
        float CIVILIAN_MAXSPEED = 7;
        float CIVILIAN_ACCEL = 300;
        float CIVILIAN_FOV = 3 * PI / 4;
        float CIVILIAN_MAXSIGHTRANGE = 1000;
        float CIVILIAN_RADIUS = 30;
    }

    public enum PacketType {
        CLIENT_REGISTER,
        CLIENT_INPUT,
        SERVER_JOINDATA,
        SERVER_UPDATE,
        NO_TYPE;
    }

    public enum WeaponType {
        RIFLE,
        HANDGUN,
        SMG;

        int RIFLE_DAMAGE = 15;
        float RIFLE_PUSHBACK = 120; //remember, this value is improved by the fire rate.
        float RIFLE_RANGE = 1500;
        float RIFLE_MAXSPREAD = 3 * (2 * PI / 360); //degrees + radians conversion.
        int RIFLE_FIREDELAY = 4;
        int RIFLE_MAGAZINESIZE = 30;
        int RIFLE_RELOADTIME = 120;
        String RIFLE_SOUND = "gunshot.mp3";

        int HANDGUN_DAMAGE = 5;
        float HANDGUN_PUSHBACK = 80;
        float HANDGUN_RANGE = 600;
        float HANDGUN_MAXSPREAD = 4.5f * (2 * PI / 360);
        int HANDGUN_FIREDELAY = 6;
        int HANDGUN_MAGAZINESIZE = 12;
        int HANDGUN_RELOADTIME = 30;
        String HANDGUN_SOUND = "gunshot.mp3";


    }
}