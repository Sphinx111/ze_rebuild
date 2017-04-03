import org.jbox2d.common.Vec2;

/**
 * Created by Celeron on 3/30/2017.
 */
public class Actor extends GameEntity {

    //Actor fields
    enums.Team myTeam;
    enums.ActorType myActorType;
    float health;
    Weapon myWeapon;
    float maxSpeed;
    float FOV;
    float ACCEL = 100;

    //"helper" fields
    int timeBitten;
    int biteDelay;
    GameEntity target;

    public Actor (ze_rebuild parentApp) {
        pApp = parentApp;
        setup();
    }
    public void setActorProperties (enums.Team newTeam, enums.ActorType newActorType) {
        myTeam = newTeam;
        myActorType = newActorType;
        float[] statsToUse = null;
        if (newActorType == enums.ActorType.SOLDIER) {
            statsToUse = newActorType.getSoldierStats();
        } else if (newActorType == enums.ActorType.BASIC_ZOMBIE) {
            statsToUse = newActorType.getZombieStats();
        } else if (newActorType == enums.ActorType.BIG_ZOMBIE) {
            statsToUse = newActorType.getBigZombieStats();
        } else {
            statsToUse = newActorType.getCivilianStats();
        }
        health = statsToUse[0];
        maxSpeed = statsToUse[1];
        FOV = statsToUse[2];
        super.worldWidth = statsToUse[3];
        super.worldHeight = statsToUse[3];
    }
    public void addWeapon(enums.WeaponType newWeapon) {
        myWeapon = new Weapon(pApp);
        myWeapon.setup(this,newWeapon);
    }
    public void wasHit(Vec2 forceToApply,float damage) {
        health -= damage;
        applyForce(forceToApply);
    }
    public void move(Vec2 direction) {
        Vec2 newForce = direction.mul(ACCEL);
        applyForce(newForce);
        speedCheck();
    }
    public void setFacing(float angle) {
        myBody.setTransform(myBody.getWorldCenter(),angle);
    }
    public void shoot() {
        myWeapon.shoot(myBody.getAngle());
    }

    @Override
    public void update() {
        speedCheck();
        myWeapon.update();
    }
    public void speedCheck() {
        //check whether I'm going above my maxspeed, if so, scale down to my maxspeed
        Vec2 myTravel = myBody.getLinearVelocity();
        if (myTravel.length() > maxSpeed) {
            myBody.setLinearVelocity(new Vec2(myTravel.mul(maxSpeed/myTravel.length())));
        }
        //because this function is usually called before weapon hits are applied, weapon hits can push a player beyond their maxspeed, yay!
    }
    public void setup() {
        addWeapon(enums.WeaponType.RIFLE);
    }

}
