import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

/**
 * Created by Celeron on 3/30/2017.
 */
public class WeaponCastCallback implements RayCastCallback{

    public float reportFixture(Fixture hitFix, Vec2 pointHit, Vec2 normalHit, float fraction) {


        return fraction;
    }
}
