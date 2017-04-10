package Server;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

/**
 * Created by Celeron on 3/30/2017.
 */
public class WeaponCastCallback implements RayCastCallback{

    Vec2 closestPointHit;
    float closestFraction = 1.01f;
    Object closestObjectHit;

    enums.Team filterOutTeam = enums.Team.NONE;

    public float reportFixture(Fixture hitFix, Vec2 pointHit, Vec2 normalHit, float fraction) {
        //if this report is closer to firer than any other report, process it's data, otherwise ignore it.
        if (fraction < closestFraction) {
            Object hitObj = hitFix.getUserData();
            //if the object hit is an actor, check if their team is being filtered out.
            if (hitObj instanceof Actor) {
                if (((Actor)hitObj).myTeam != filterOutTeam) {
                    closestFraction = fraction;
                    closestPointHit = pointHit;
                    closestObjectHit = hitObj;
                    return fraction;
                } else {
                    return 1;
                }
            } else if (hitObj instanceof Sensor || hitObj instanceof GameLogic || hitObj instanceof MapItem) {
                //if raycast hits sensor, MapItem or gamelogic, ignore the collision.
                return 1;
            } else {
                //if it hits a fixed object or door, report the collision
                closestFraction = fraction;
                closestPointHit = pointHit;
                closestObjectHit = hitObj;
                return fraction;
            }
        }

        return 1;
    }
    public void cleanup() {
        closestFraction = 1.01f;
        closestPointHit = null;
        closestObjectHit = null;
    }
    public void setTeamFilter(enums.Team filterOutHitsOnThisTeam) {
        filterOutTeam = filterOutHitsOnThisTeam;
    }
}
