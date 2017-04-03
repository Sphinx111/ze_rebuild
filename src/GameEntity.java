import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import processing.core.PImage;

import static processing.core.PConstants.CENTER;


/**
 * Created by Eddy on 30/03/2017.
 */


public class GameEntity {
    //parent class for Actors, Objects, Items etc with a presence in the gameworld.

    transient ze_rebuild pApp;

    //Universal Fields
    enums.EntityType myType;
    int myID = -1;
    Vec2 worldPos;
    float worldWidth;
    float worldHeight;
    float angle;

    public GameEntity(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    public GameEntity() {/* Stub constructor to satisfy java's compile requirements.*/}

    //Physics fields (does not apply to decoration).
    transient Body myBody;
    transient Fixture myFix;

    //Drawing fields
    transient PImage myImage;
    transient int[] myColor = new int[3];
    transient float pixWidth; //doubles as radius for circular objects
    transient float pixHeight;

    public void setCoreProperties(Vec2 pos, float width, float height, float angle, enums.EntityType type, int id) {
        myID = id;
        myType = type;
        this.angle = angle;
        worldPos = pos;
        worldWidth = width;
        worldHeight = height;
        myType = type;
        pixWidth = pApp.box2d.scalarPixelsToWorld(worldWidth);
        pixHeight = pApp.box2d.scalarPixelsToWorld(worldHeight);
    }

    public void applyForce(Vec2 forceToApply) {
        myBody.applyForceToCenter(forceToApply);
    }

    public void setPImage(PImage toUse) {
        myImage = toUse;
    }

    public void setColor(int red, int green, int blue) {
        myColor[0] = red;
        myColor[1] = green;
        myColor[2] = blue;
    }

    public void update() {
        //DO NOTHING for basic static objects. Other types will override.
    }

    void makeBody() {
        // Define a polygon (this is what we use for a rectangle)
        PolygonShape sd = new PolygonShape();
        float box2dw = worldWidth / 2;
        float box2dh = worldHeight / 2;
        sd.setAsBox(box2dw, box2dh);

        // Define a fixture
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        // Parameters that affect physics
        fd.density = 1;
        fd.friction = 0;
        fd.restitution = 0.05f;
        if (myType == enums.EntityType.SENSOR || myType == enums.EntityType.GAME_LOGIC || myType == enums.EntityType.MAP_ITEM) {
            fd.isSensor = true;
        }

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
        myBody.setTransform(myBody.getWorldCenter(),angle);
    }

    void show() {
        //subclasses shouldn't need to override this method if done properly.
        Vec2 pixPos = pApp.box2d.getBodyPixelCoord(myBody);
        float pixWidth = pApp.box2d.scalarWorldToPixels(worldWidth);
        float pixHeight = pApp.box2d.scalarWorldToPixels(worldHeight);
        float angle = myBody.getAngle();

        pApp.rectMode(CENTER);
        pApp.pushMatrix();
        pApp.translate(pixPos.x, pixPos.y);
        pApp.rotate(-angle);
        pApp.fill(myColor[0], myColor[1], myColor[2]);
        pApp.stroke(0);
        pApp.strokeWeight(1);
        pApp.rect(0, 0, pixWidth, pixHeight);
        pApp.popMatrix();

    }

}
