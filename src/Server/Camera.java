package Server;

import org.jbox2d.common.Vec2;

/**
 * Created by Celeron on 4/1/2017.
 */
public class Camera {

    ze_rebuild pApp;

    float xOff = 0;
    float yOff = 0;
    float oldxOff = 0;
    float oldYOff = 0;
    float screenShakeValue = 0;
    float fractionalAdjust = 1;
    float defFractionalAdjust = 0.1f;

    float playerBodyOffsetX;
    float getPlayerBodyOffsetY;

    float screenCenterX;
    float screenCenterY;

    public Camera(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    public void setup() {
        screenCenterX = pApp.width/2;
        screenCenterY = pApp.height/2;
    }

    public void applyOffset() {
        //save current offset values (to allow slower adjust to new position);
        float oldXOff = xOff;
        float oldYOff = yOff;
        //prepare variables for not-null checks.
        float playerPosX = 0;
        float playerPosY = 0;
        int myPlayerID = -1;
        //if the client has started, get the current playerID.
        if (pApp.stateManager.client != null) {
            myPlayerID = pApp.stateManager.client.myPlayerID;
        }
        //Check the mapHandler has started, and that a player ID has been assigned (not -1);
        if (pApp.stateManager.gameManager.mapHandler != null && myPlayerID != -1) {
            //check whether the mapHandler has a reference for our player object yet
            if (pApp.stateManager.gameManager.mapHandler.allObjects.containsKey(myPlayerID)) {
                //get the player's pixel position to use for offsetting/translating with the camera.
                Actor myPlayer = (Actor) pApp.stateManager.gameManager.mapHandler.allObjects.get(myPlayerID);
                Vec2 playerPixPos = pApp.box2d.getBodyPixelCoord(myPlayer.myBody);
                playerPosX = playerPixPos.x;
                playerPosY = playerPixPos.y;
            }
        }
        //Desired translation is set like this...
        xOff = screenCenterX - playerPosX;
        yOff = screenCenterY - playerPosY;
        //then we ease off the translation a little bit, a fraction between the last frame and current one.
        //we also add screenshake after the fractional adjust.
        xOff = (oldXOff + ((xOff - oldXOff) * fractionalAdjust)) + applyScreenshake();
        yOff = (oldYOff + ((yOff - oldYOff) * fractionalAdjust)) + applyScreenshake();
        pApp.pushMatrix();
        pApp.translate(xOff,yOff);
        //let the screenshake ease off each frame, if it goes below zero, set to zero for steady screen.
        screenShakeValue -= 10;
        if (screenShakeValue < 0){
            screenShakeValue = 0;
        }
        fractionalAdjust = defFractionalAdjust;
    }
    public void removeOffset() {
        pApp.popMatrix();
    }
    protected float applyScreenshake() {
        //randomise amount of screenshake between -Value and +value, then return it.
        float returnMe = ((float)Math.random() * screenShakeValue * 2) - screenShakeValue;
        return returnMe;
    }
    public void addScreenshake(float toAdd) {
        //add screenshake amount to current
        screenShakeValue += toAdd;
    }
}
