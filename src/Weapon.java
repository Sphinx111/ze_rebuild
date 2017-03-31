import org.jbox2d.common.Vec2;
import processing.core.PImage;
import processing.core.PShape;

/**
 * Created by Eddy on 30/03/2017.
 */
public class Weapon {

    ze_rebuild pApp;
    enums.WeaponType myWeaponType;
    Actor holdingActor;

    WeaponCastCallback weaponCallback;

    //variables decided by weapon type
    float damage;
    float pushback;
    float range;
    float fireDelay; //(in simulation ticks)
    float reloadDelay; //(in simulation ticks)
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
    float reloadCountdown;

    //draw fields
    String myShape;
    String myTexture;
    Vec2 playerOffset;

    public Weapon(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    public void setup(Actor holdingPlayer, enums.WeaponType newType) {
        //give the weapon a reference to whoever is holding it.
        holdingActor = holdingPlayer;

        //initialise the weapon's own raycast listener.
        weaponCallback = new WeaponCastCallback();
        weaponCallback.setTeamFilter(holdingActor.myTeam);
        //set core weapon stats;
        setType(newType);

        //initialize helper variables.
        timeLastFired = pApp.frameCount;
        roundsInMagazine = magazineSize;
        reloadCountdown = -1;
    }

    public void update() {
        checkReloadState();
    }
    public void checkReloadState() {
        if (reloadCountdown == -1) {
            //do nothing if the weapon is not currently reloading, counter stays at -1
            return;
        } else {
            //if the weapon has finished reloading, fill the magazine back up.
            if (reloadCountdown == 0) {
                roundsInMagazine = magazineSize;
            }
            //whether finished or not, decrement the reload timer
            reloadCountdown--;
            return;
        }
    }
    public void reload() {
        if (roundsInMagazine > 0) {
            reloadCountdown = reloadDelay;
            roundsInMagazine = 0;
        }
    }
    public boolean canShoot() {
        //if there are rounds in the magazine, and firedelay has passed...
        if (roundsInMagazine > 0 && pApp.frameCount > timeLastFired + fireDelay) {
            return true;
        } else {
            return false;
        }
    }
    public void shoot(float angle) {
        //update helper variables.
        timeLastFired = pApp.frameCount;
        roundsInMagazine -= 1;

        Actor firer = holdingActor;
        //define a line for the raycast check.
        //apply random bullet spread to firing angle, use FastMath with frameCount seed to get same offset on server and client for any given tick
        float randomOffset = FastMath.rand256()/256 * spread;
        Vec2 origin = firer.myBody.getWorldCenter();
        Vec2 endPoint = new Vec2(origin.x + (range * (float)Math.cos(angle)), origin.y + (range * (float)Math.sin(angle)));
        //send out a raycast which will be caught by the raycast callback.
        pApp.box2d.world.raycast(weaponCallback,origin,endPoint);

        //tell mainCamera to apply screenshake
        int screenShakeAmt = getScreenShake(firer);
        //TODO: send screenShake instructions to camera
        //pApp.mainCamera.addScreenShake(screenShakeAmt);

        //TODO: add sound here

        //TODO: draw better firing line
        //draw line to the point hit
        pApp.strokeWeight(2);
        pApp.stroke(250,30,30);
        if (weaponCallback.closestPointHit != null) {
            Vec2 bulletEnd = pApp.box2d.coordWorldToPixels(weaponCallback.closestPointHit);
            Vec2 drawOrigin = pApp.box2d.coordWorldToPixels(origin);
            pApp.line(drawOrigin.x,drawOrigin.y,bulletEnd.x,bulletEnd.y);
            pApp.strokeWeight(15);
            pApp.stroke(250,50,0);
            pApp.point(bulletEnd.x,bulletEnd.y);
        } else {
            Vec2 drawOrigin = pApp.box2d.coordWorldToPixels(origin);
            Vec2 endPointPixels = pApp.box2d.coordWorldToPixels(endPoint);
            pApp.line(drawOrigin.x,drawOrigin.y,endPointPixels.x,endPointPixels.y);
        }

        //Get object collision info from weaponCallback.
        //if the raycast's nearest hit object is an actor (necessarily not null), apply force and damage.
        if (weaponCallback.closestObjectHit instanceof Actor) {
            Actor actorHit = (Actor)weaponCallback.closestObjectHit;
            Vec2 vecToTarget = actorHit.myBody.getWorldCenter().add(origin.negate());
            //get direction of shot without magniture.
            vecToTarget.normalize();
            //multiply direction by the pushback value to get force.
            Vec2 forceToApply = vecToTarget.mul(pushback);

            actorHit.wasHit(forceToApply,damage);
        }
        //firing cycle has finished, cleanup the weaponCallback results.
        weaponCallback.cleanup();
    }
    public int getScreenShake(Actor firer) {
        int newAmount = 0;
        if (firer.myID == pApp.stateManager.client.myPlayerID) {
            //if the player firing IS the player viewing this client
            newAmount = (int)Math.floor(damage * 2);
        }
        return newAmount;
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
