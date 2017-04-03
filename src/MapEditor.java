import org.jbox2d.common.Vec2;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RADIUS;

/**
 * Created by Celeron on 3/31/2017.
 */
public class MapEditor {
    //this class should (when active) send build instructions to the MapHandler, in order to build a playable map.

    ze_rebuild pApp;
    ClientMapHandler currentMap;

    //helper variables:
    Vec2 worldMousePos;
    Camera camera;
    boolean settingDoorPos = false;

    //is there an object being created, and if so what type.
    boolean creatingNew = false;
    enums.EntityType currentEditorMode = enums.EntityType.FIXED;
    float width = 1; //the "y" radius value for rectangles created here, world scale.
    float length = 1;  // the "x" radius value for rectangles created here, world scale.

    //start and end positions for creation of world geometry
    Vec2 startPos;
    Vec2 endPos;
    Vec2 centerPoint;
    float angle;
    Vec2 helperPos; //third position, usually used for setting width in fixed/doors/sensors.


    public MapEditor (ze_rebuild parentApp) {
        pApp = parentApp;
    }
    public void setup(ClientMapHandler currentMap) {
        this.currentMap = currentMap;
    }

    public void receiveMouseClick() {
        camera = pApp.stateManager.gameManager.mainCamera;
        worldMousePos = pApp.box2d.coordPixelsToWorld(pApp.mouseX - camera.xOff, pApp.mouseY - camera.yOff);
        if (!creatingNew) {
            creatingNew = true;
            firstClick();
        } else if (startPos != null && endPos == null){
            secondClick();
        } else if (currentEditorMode == enums.EntityType.DOOR && settingDoorPos) {
            fourthClick();
        } else {
            thirdClick();
        }
    }
    public void update() {
        camera = pApp.stateManager.gameManager.mainCamera;
        worldMousePos = pApp.box2d.coordPixelsToWorld(pApp.mouseX - camera.xOff, pApp.mouseY - camera.yOff);
        show();
    }

    private void firstClick() {
        startPos = worldMousePos;
        if (currentEditorMode == enums.EntityType.ACTOR) {
            currentMap.setActorSettings(enums.Team.HUMAN, enums.ActorType.SOLDIER);
            currentMap.createEntityByMouse(startPos,angle,length,width,currentEditorMode);

            //reset helper variables used so far
            cleanup();
        } else if (currentEditorMode == enums.EntityType.GAME_LOGIC) {
            currentMap.createEntityByMouse(startPos,angle,length,width,currentEditorMode);

            //reset helper variables used so far.
            cleanup();
        } else {
            //anything special to do for creation of fixed objects?
        }
    }
    private void secondClick() {
        endPos = worldMousePos;
        //find centerpoint for rectangle.
        centerPoint = (endPos.add(startPos)).mul(0.5f);
        //find angle of rotation for rectangle being drawn.
        Vec2 startToEnd = endPos.add(startPos.negate());
        angle = (float)Math.atan2(startToEnd.y,startToEnd.x);
        length = startToEnd.length();
    }
    private void thirdClick() {
        helperPos = worldMousePos;
        width =(helperPos.add(endPos.negate()).length());

        if (currentEditorMode != enums.EntityType.DOOR) {
            currentMap.createEntityByMouse(centerPoint, angle, length, width, currentEditorMode);

            //reset helper variables used so far
            cleanup();
        } else {
            settingDoorPos = true;
        }
    }
    private void fourthClick() {
        currentMap.setDoorSettings(worldMousePos,5,5);
        currentMap.createEntityByMouse(centerPoint,angle,length,2 * width,currentEditorMode);

        cleanup();
    }
    private void cleanup() {
        startPos = null;
        endPos = null;
        helperPos = null;
        centerPoint = null;
        length = 1;
        width = 1;
        settingDoorPos = false;
        creatingNew = false;
    }
    public void show() {
        if (creatingNew) {
            //if no endPos has been defined yet, and creatingNew is true, then the player is drawing a
            //rectangle shape from firstClick to wherever they click next, display the predicted block line.
            if (endPos == null && startPos != null) {
                Vec2 lineStartPos = pApp.box2d.coordWorldToPixels(startPos);
                lineStartPos = new Vec2(lineStartPos.x, lineStartPos.y);
                pApp.stroke(255);
                pApp.strokeWeight(2);
                pApp.line(lineStartPos.x, lineStartPos.y, pApp.mouseX - camera.xOff, pApp.mouseY - camera.yOff);
            //else if there is an endPos, and creatingNew is true, player is setting width of a rectangle,
            //show predicted shape of rectangle:
            } else if (endPos != null && startPos != null) {
                pApp.rectMode(RADIUS);
                //work out pixel coordinates for draw locations.
                Vec2 rectDrawCenter = pApp.box2d.coordWorldToPixels(centerPoint);
                //not sure if I still need to adjust for camera offsets in this drawing step???
                rectDrawCenter = new Vec2(rectDrawCenter.x, rectDrawCenter.y);
                width = worldMousePos.add(endPos.negate()).length();
                float rectDrawLength = pApp.box2d.scalarWorldToPixels(length) / 2;
                float rectDrawWidth = pApp.box2d.scalarWorldToPixels(width) / 2;

                //if not setting a door pos, predict a normal rectangle.
                if (!settingDoorPos) {
                    pApp.noFill();
                    pApp.stroke(255);
                    pApp.strokeWeight(1);
                    pApp.pushMatrix();
                    pApp.translate(rectDrawCenter.x, rectDrawCenter.y);
                    pApp.rotate(-angle);
                    pApp.rect(0, 0, rectDrawLength, rectDrawWidth);
                    pApp.popMatrix();
                } else {
                    //if we are setting a door pos, draw the normal door position, as well as an outline of it's open Pos.
                    pApp.fill(20);
                    pApp.stroke(255);
                    pApp.strokeWeight(1);
                    pApp.pushMatrix();
                    pApp.translate(rectDrawCenter.x, rectDrawCenter.y);
                    pApp.rotate(-angle);
                    pApp.rect(0, 0, rectDrawLength, pApp.box2d.scalarWorldToPixels(width)/2);
                    pApp.popMatrix();

                    pApp.noFill();
                    pApp.pushMatrix();
                    pApp.translate(pApp.mouseX,pApp.mouseY);
                    pApp.rotate(-angle);
                    pApp.rect(0,0,length/2,width/2);
                    pApp.popMatrix();
                }
            }
        }
    }

}
