
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import processing.core.PImage;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;


/**
 * Created by Eddy on 30/03/2017.
 */


public class GameEntity {
    //parent class for Actors, Objects, Items etc with a presence in the gameworld.

    ze_rebuild pApp;

    //Universal Fields
    enums.EntityType myType;
    int myID = -1;
    Vec2 worldPos;
    float worldWidth;
    float worldHeight;
    float angle;

    public GameEntity (ze_rebuild parentApp) {
        pApp = parentApp;
    }

    //Physics fields (does not apply to decoration).
    Body myBody;
    Fixture myFix;

    //Drawing fields
    PImage myImage;
    int[] myColor;
    float pixWidth; //doubles as radius for circular objects
    float pixHeight;

    //Actor fields
    GameManager.Team myTeam;
    GameManager.ActorType myActorType;
    float health;
    Weapon myWeapon;
    float maxSpeed;
    float timeBitten;
    float biteDelay;
    float FOV;
    GameEntity target;

    //Sensor fields
    HashMap<Integer,GameEntity> sensorLinks;
    int tickButtonPressed;
    boolean wasPressed;
    GameManager.Team teamFilter;
    HashMap<Integer,GameEntity> touchingObjects;

    //Door fields
    HashMap<Integer,GameEntity> doorLinks;
    Vec2 doorClosedPos;
    Vec2 doorOpenPos;
    Vec2 hinge;
    boolean isHinged;
    boolean fullOpen;
    boolean fullClosed;

    //Gamelogic fields
    HashMap<Integer,GameEntity> logicLinks;
    String label;
    LogicType myLogicType;
    GameManager.Team teamAffected;
    int timerDelay1;
    int timerDelay2;
    int strengthOfEffect; //amount of damage to apply per timerDelay2 or speed to MoveLinkedObjects.

    public GameEntity (Vec2 pos, float width, float height, float angle, ClientMapHandler.EntityType type, int id) {
        myID = id;
        myType = type;
        this.angle = angle;
        worldPos = pos;
        worldWidth = width;
        worldHeight = height;
        myType = type;
    }

    void makeBody() {
        // Define a polygon (this is what we use for a rectangle)
        PolygonShape sd = new PolygonShape();
        float box2dr = pApp.box2d.scalarPixelsToWorld(worldWidth/2);
        sd.setAsBox(box2dr,box2dr);

        // Define a fixture
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        // Parameters that affect physics
        fd.density = 1;
        if (myType == enums.EntityType.ACTOR) {
            fd.density = myType.BIGZOMBIE_DENSITY;
        }
        fd.friction = 0;
        if (!isPlayer) {
            fd.friction = -0.2;
        }
        fd.restitution = 0.05;

        // Define the body and make it from the shape
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(box2d.coordPixelsToWorld(pos));

        body = box2d.createBody(bd);
        body.setAngularDamping(0.5);
        body.setLinearDamping(8);
        Fixture fix = body.createFixture(fd);
        body.setUserData(this);
        fix.setUserData(this);
    }

    }


    private enum LogicType {
        TIMER,
        TWO_STAGE_TIMER,
        GAME_END,
        APPLY_DAMAGE,
        MOVE_LINKED_OBJECTS,
        DOOR_OPEN,
        DOOR_CLOSE,
    }
}
