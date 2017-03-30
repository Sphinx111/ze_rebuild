import org.jbox2d.common.Vec2;
import processing.core.PImage;
import processing.core.PShape;

/**
 * Created by Eddy on 30/03/2017.
 */
public class Weapon {

    ze_rebuild pApp;
    enums.WeaponType myWeaponType;

    WeaponCastCallback weaponCallback;

    //variables decided by weapon type
    float damage;
    float pushback;
    float range;
    float fireDelay;
    float reloadDelay;
    float magazineSize;
    float spread;

    //audio fields
    String fireSound;
    String reloadSound;
    String emptySound;
    String hitSound;

    //fucntional variables (track bullets fired etc)
    float timeLastFired;
    float roundsInMagazine;

    //draw fields
    String myShape;
    String myTexture;
    Vec2 playerOffset;

    public Weapon(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    public void setType(enums.WeaponType newType) {
        myWeaponType = newType;
        float[] stats = newType.getWeaponStats(newType);
        damage = stats[0];
        pushback = stats[1];
        range = stats[2];
        fireDelay = stats[3];
        reloadDelay = stats[4];
        magazineSize = stats[5];
        spread = stats[6];
        String[] sounds = newType.getWeaponSounds(newType);
        fireSound = sounds[0];
        reloadSound = sounds[1];
        emptySound = sounds[2];
        hitSound = sounds[4];
        String[] graphics = newType.getGraphics(newType);
        myShape = graphics[0];
        myTexture = graphics[1];
    }

}
