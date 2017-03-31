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

    //"helper" fields
    int timeBitten;
    int biteDelay;
    GameEntity target;

    public Actor (ze_rebuild parentApp) {
        super.pApp = parentApp;
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
        myWeapon.setType(enums.WeaponType.RIFLE);
    }
    public void wasHit(Vec2 forceToApply,float damage) {
        health -= damage;
        applyForce(forceToApply);
    }

}
