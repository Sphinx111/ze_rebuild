import org.jbox2d.common.Vec2;
import processing.core.PImage;

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
        float SOLDIER_MAXSPEED = 4;
        float SOLDIER_ACCEL = 150;
        float SOLDIER_FOV = PI / 2;
        float SOLDIER_MAXSIGHTRANGE = 1000;
        float SOLDIER_RADIUS = 1;
        float[] getSoldierStats () {
            float[] soldierStats = {SOLDIER_HEALTH, SOLDIER_MAXSPEED, SOLDIER_FOV, SOLDIER_RADIUS};
            return soldierStats;
        }

        float ZOMBIE_HEALTH = 2500;
        float ZOMBIE_MAXSPEED_MULTIPLIER = 1.2f;
        float ZOMBIE_ACCEL_MULTIPLIER = 1;
        float ZOMBIE_FOV = 3 * PI / 4;
        float ZOMBIE_MAXSIGHTRANGE = 1000;
        float ZOMBIE_RADIUS = 1;
        float[] getZombieStats() {
            float[] zombieStats = {ZOMBIE_HEALTH, ZOMBIE_MAXSPEED_MULTIPLIER * SOLDIER_MAXSPEED, ZOMBIE_FOV, ZOMBIE_RADIUS};
            return zombieStats;
        }

        float BIGZOMBIE_HEALTH = 25000;
        float BIGZOMBIE_MAXSPEED_MULTIPLIER = 0.95f;
        float BIGZOMBIE_ACCEL_MULTIPLIER = 8;
        float BIGZOMBIE_FOV = 3 * PI / 4;
        float BIGZOMBIE_MAXSIGHTRANGE = 800;
        float BIGZOMBIE_RADIUS = 2;
        float BIGZOMBIE_DENSITY = 4;
        float[] getBigZombieStats() {
            float[] bigZombieStats = {BIGZOMBIE_HEALTH, BIGZOMBIE_MAXSPEED_MULTIPLIER * SOLDIER_MAXSPEED, BIGZOMBIE_FOV, BIGZOMBIE_RADIUS};
            return bigZombieStats;
        }

        float CIVILIAN_HEALTH = 80;
        float CIVILIAN_MAXSPEED = 4;
        float CIVILIAN_ACCEL = 300;
        float CIVILIAN_FOV = 3 * PI / 4;
        float CIVILIAN_MAXSIGHTRANGE = 1000;
        float CIVILIAN_RADIUS = 1;
        float[] getCivilianStats() {
            float[] civilianStats = {CIVILIAN_HEALTH,CIVILIAN_MAXSPEED, CIVILIAN_FOV, CIVILIAN_RADIUS};
            return civilianStats;
        }
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
        SMG,
        ZOMBIE_TEETH;

        String[] getGraphics(WeaponType testWeapon) {
            if (testWeapon == RIFLE) {
                String[] fileNames = {"PLACEHOLDER.SVG", "PLACEHOLDER.IMG"};
                return fileNames;
            } else if (testWeapon == HANDGUN) {
                String[] fileNames = {"PLACEHOLDER.SVG", "PLACEHOLDER.IMG"};
                return fileNames;
            } else if (testWeapon == SMG) {
                String[] fileNames = {"PLACEHOLDER.SVG", "PLACEHOLDER.IMG"};
                return fileNames;
            } else if (testWeapon == ZOMBIE_TEETH) {
                String[] fileNames = {"PLACEHOLDER.SVG", "PLACEHOLDER.IMG"};
                return fileNames;
            } else {
                return null;
            }
        }

        Vec2 getOffset(WeaponType testType) {
           return new Vec2(0,0);
        }

        float[] getWeaponStats(WeaponType testWeapon) {
            if (testWeapon == RIFLE) {
                float[] returnStats = {RIFLE_DAMAGE, RIFLE_PUSHBACK, RIFLE_RANGE, RIFLE_FIREDELAY, RIFLE_RELOADTIME, RIFLE_MAGAZINESIZE, RIFLE_MAXSPREAD};
                return returnStats;
            } else if (testWeapon == HANDGUN) {
                float[] returnStats = {HANDGUN_DAMAGE, HANDGUN_PUSHBACK, HANDGUN_RANGE, HANDGUN_FIREDELAY, HANDGUN_RELOADTIME, HANDGUN_MAGAZINESIZE, HANDGUN_MAXSPREAD};
                return returnStats;
            } else if (testWeapon == SMG) {
                float[] returnStats = {SMG_DAMAGE, SMG_PUSHBACK, SMG_RANGE, SMG_FIREDELAY, SMG_RELOADTIME, SMG_MAGAZINESIZE, SMG_MAXSPREAD};
                return returnStats;
            } else if (testWeapon == ZOMBIE_TEETH) {
                float[] returnStats = {ZOMBIE_DAMAGE, ZOMBIE_PUSHBACK, ZOMBIE_RANGE, ZOMBIE_FIREDELAY, ZOMBIE_RELOADTIME, ZOMBIE_MAGAZINESIZE, 0};
                return returnStats;
            } else {
                return null;
            }
        }

        String[] getWeaponSounds(WeaponType testWeapon) {
            if (testWeapon == RIFLE) {
                String[] returnStrings = {RIFLE_SOUND, RIFLE_SOUND, RIFLE_SOUND};
                return returnStrings;
            } else if (testWeapon == HANDGUN) {
                String[] returnStrings = {HANDGUN_SOUND, HANDGUN_SOUND, HANDGUN_SOUND, HANDGUN_SOUND};
                return returnStrings;
            } else if (testWeapon == SMG) {
                String[] returnStrings = {SMG_SOUND, SMG_SOUND, SMG_SOUND, SMG_SOUND};
                return returnStrings;
            } else if (testWeapon == ZOMBIE_TEETH) {
                String[] returnStrings = {ZOMBIE_SOUND, ZOMBIE_SOUND, ZOMBIE_SOUND, ZOMBIE_SOUND};
                return returnStrings;
            } else {
                return null;
            }
        }

        float RIFLE_DAMAGE = 15;
        float RIFLE_PUSHBACK = 120; //remember, this value is improved by the fire rate.
        float RIFLE_RANGE = 1500;
        float RIFLE_MAXSPREAD = 3 * (2 * PI / 360); //degrees + radians conversion.
        int RIFLE_FIREDELAY = 4;
        int RIFLE_MAGAZINESIZE = 30;
        int RIFLE_RELOADTIME = 120;
        String RIFLE_SOUND = "gunshot.mp3";

        float SMG_DAMAGE = 15;
        float SMG_PUSHBACK = 120; //remember, this value is improved by the fire rate.
        float SMG_RANGE = 1500;
        float SMG_MAXSPREAD = 3 * (2 * PI / 360); //degrees + radians conversion.
        int SMG_FIREDELAY = 4;
        int SMG_MAGAZINESIZE = 30;
        int SMG_RELOADTIME = 120;
        String SMG_SOUND = "gunshot.mp3";

        float HANDGUN_DAMAGE = 5;
        float HANDGUN_PUSHBACK = 80;
        float HANDGUN_RANGE = 600;
        float HANDGUN_MAXSPREAD = 4.5f * (2 * PI / 360);
        int HANDGUN_FIREDELAY = 6;
        int HANDGUN_MAGAZINESIZE = 12;
        int HANDGUN_RELOADTIME = 30;
        String HANDGUN_SOUND = "gunshot.mp3";

        float ZOMBIE_DAMAGE = 15;
        float ZOMBIE_PUSHBACK = 120; //remember, this value is improved by the fire rate.
        float ZOMBIE_RANGE = 1500;
        float ZOMBIE_MAXSPREAD = 3 * (2 * PI / 360); //degrees + radians conversion.
        int ZOMBIE_FIREDELAY = 4;
        int ZOMBIE_MAGAZINESIZE = 30;
        int ZOMBIE_RELOADTIME = 120;
        String ZOMBIE_SOUND = "zombie.mp3";
    }

    public enum LogicType {
        TIMER,
        TWO_STAGE_TIMER,
        GAME_END,
        SLOW,
        APPLY_DAMAGE,
        MOVE_ENTITIES;
    }
}