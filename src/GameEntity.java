
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
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
    public GameEntity () {/* Stub constructor to satisfy java's compile requirements.*/}

    //Physics fields (does not apply to decoration).
    Body myBody;
    Fixture myFix;

    //Drawing fields
    PImage myImage;
    int[] myColor;
    float pixWidth; //doubles as radius for circular objects
    float pixHeight;

    public void setCoreProperties (Vec2 pos, float width, float height, float angle, enums.EntityType type, int id) {
        myID = id;
        myType = type;
        this.angle = angle;
        worldPos = pos;
        worldWidth = width;
        worldHeight = height;
        myType = type;
    }

    public void setPImage (PImage toUse) {
        myImage = toUse;
    }
    public void setColor(int red, int green, int blue) {
        myColor[0] = red;
        myColor[1] = green;
        myColor[2] = blue;
     }

    void makeBody() {
        // Define a polygon (this is what we use for a rectangle)
        PolygonShape sd = new PolygonShape();
        float box2dw = pApp.box2d.scalarPixelsToWorld(worldWidth/2);
        float box2dh = pApp.box2d.scalarPixelsToWorld(worldHeight /2);
        sd.setAsBox(box2dw,box2dh);

        // Define a fixture
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        // Parameters that affect physics
        fd.density = 1;
        fd.friction = 0;
        fd.restitution = 0.05f;

        // Define the body and make it from the shape
        BodyDef bd = new BodyDef();
        if (myType == enums.EntityType.ACTOR || myType == enums.EntityType.SENSOR || myType == enums.EntityType.GAME_LOGIC || myType == enums.EntityType.MAP_ITEM) {
            bd.type = BodyType.DYNAMIC;
        } else if (myType == enums.EntityType.DOOR) {
            bd.type = BodyType.KINEMATIC;
        } else if (myType == enums.EntityType.FIXED) {
            bd.type = BodyType.STATIC;
        } else {
            bd.type = BodyType.DYNAMIC;
        }
        bd.position.set(worldPos);

        myBody = pApp.box2d.createBody(bd);
        myBody.setAngularDamping(0.5f);
        myBody.setLinearDamping(8);
        myFix = myBody.createFixture(fd);
        myBody.setUserData(this);
        myFix.setUserData(this);
    }

}
