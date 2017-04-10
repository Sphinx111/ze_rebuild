package Server;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import java.util.ArrayList;

import static processing.core.PConstants.RADIUS;

/**
 * Created by Celeron on 3/31/2017.
 */
public class MapEditor {
    //this class should (when active) send build instructions to the MapHandler, in order to build a playable map.

    ze_rebuild pApp;
    ClientMapHandler currentMap;
    EditorQuery editorQuery;

    //helper variables:
    Vec2 worldMousePos;
    Camera camera;
    boolean settingDoorPos = false;

    //is there an object being created, and if so what type.
    boolean creatingNew = false;
    GameEntity currentEntity = null;
    enums.EntityType currentEditorMode = enums.EntityType.FIXED;
    float width = 1; //the "y" radius value for rectangles created here, world scale.
    float length = 1;  // the "x" radius value for rectangles created here, world scale.

    //start and end positions for creation of world geometry
    Vec2 startPos;
    Vec2 endPos;
    Vec2 centerPoint;
    float angle;
    Vec2 helperPos; //third position, usually used for setting width in fixed/doors/sensors.
    float currentLength;
    float currentWidth;


    public MapEditor (ze_rebuild parentApp) {
        pApp = parentApp;
    }
    public void setup(ClientMapHandler currentMap) {
        editorQuery = new EditorQuery();
        this.currentMap = currentMap;
    }

    public void receiveMouseClick() {
        camera = pApp.stateManager.gameManager.mainCamera;
        worldMousePos = pApp.box2d.coordPixelsToWorld(pApp.mouseX - camera.xOff, pApp.mouseY - camera.yOff);
        //if the click was right click and no object is being created, check what they clicked on, otherwise, continue normal creation process
        if (!creatingNew) {
            if (pApp.mouseButton == pApp.RIGHT && currentEntity == null) {
                //check whether we're clicking on an existing object
                currentEntity = getMapObjectUnderMouse(enums.EntityType.DOOR, enums.EntityType.GAME_LOGIC, enums.EntityType.SENSOR);
                editEntityProperties(currentEntity);
                return;
            } else if (pApp.mouseButton == pApp.RIGHT && currentEntity != null) {
                GameEntity clickedEntity = getMapObjectUnderMouse(enums.EntityType.DOOR, enums.EntityType.GAME_LOGIC, enums.EntityType.SENSOR);
                if (clickedEntity != null) {
                    addLinkFromAToB(currentEntity,clickedEntity);
                    return;
                }
            } else {
                creatingNew = true;
                firstClick();
                return;
            }
        } else if (startPos != null && endPos == null){
            if (pApp.mouseButton == pApp.RIGHT) {
                cleanup();
            } else {
                secondClick();
                return;
            }
        } else if (currentEditorMode == enums.EntityType.DOOR && settingDoorPos) {
            if (pApp.mouseButton == pApp.RIGHT) {
                cleanup();
            } else {
                fourthClick();
                return;
            }
        } else if (!settingDoorPos){
            if (pApp.mouseButton == pApp.RIGHT) {
                cleanup();
            } else {
                thirdClick();
                return;
            }
        }
    }
    public void setCurrentEditorMode (enums.EntityType newMode) {
        currentEditorMode = newMode;
    }
    GameEntity getMapObjectUnderMouse(enums.EntityType desiredType, enums.EntityType alternateType, enums.EntityType thirdType) {
        Vec2 clickPos = new Vec2(worldMousePos.x - 0.5f, worldMousePos.y - 0.5f);
        Vec2 boundingBox = new Vec2(1,1);
        GameEntity result = null;
        AABB checkArea = new AABB(clickPos,clickPos.add(boundingBox));
        pApp.box2d.world.queryAABB(editorQuery,checkArea);
        for (Fixture f : editorQuery.allFixtures) {
            if (f.testPoint(clickPos)) {
                Object other = f.getUserData();
                if (other instanceof GameEntity) {
                    GameEntity stranger = (GameEntity)other;
                    if (stranger.myType == desiredType || stranger.myType == alternateType || stranger.myType == thirdType) {
                        result = stranger;
                    }
                }
            }
        }
        editorQuery.allFixtures.clear();
        return result;
    }
    public void update() {
        camera = pApp.stateManager.gameManager.mainCamera;
        worldMousePos = pApp.box2d.coordPixelsToWorld(pApp.mouseX - camera.xOff, pApp.mouseY - camera.yOff);
        show();
    }
    private void addLinkFromAToB (GameEntity source, GameEntity dest) {
        System.out.println(getClass().getName() + ">>> Adding link from " + source.getClass().getName() + " to " + dest.getClass().getName());
        if (source instanceof Sensor) {
            Sensor sourceSensor = (Sensor)source;
            sourceSensor.addLink(dest);
        } else if (source instanceof GameLogic) {
            GameLogic sourceLogic = (GameLogic)source;
            sourceLogic.addLogicLink(dest);
        } else if (source instanceof Door) {
            Door sourceDoor = (Door)source;
            sourceDoor.addDoorLink(dest);
        }
        currentEntity = null;
    }
    private void editEntityProperties( GameEntity clickedEntity) {
        if (clickedEntity instanceof Door) {
            Door clickedDoor = (Door)clickedEntity;
            //TODO: SOMETHING TO EDIT DOOR DEFAULT SETTINGS
        } else if (clickedEntity instanceof Sensor) {
            Sensor clickedSensor = (Sensor)clickedEntity;
            //DO SOMETHING TO EDIT SENSOR DEFAULT SETTINGS
        } else if (clickedEntity instanceof GameLogic) {
            GameLogic clickedLogic = (GameLogic)clickedEntity;
            //DO SOMETHING TO EDIT SENSOR DEFAULT SETTINGS;
        }
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
            currentLength = length;
            currentWidth = width;
        }
    }
    private void fourthClick() {
        currentMap.setDoorSettings(worldMousePos,5,5);
        currentMap.createEntityByMouse(centerPoint,angle,currentLength,currentWidth,currentEditorMode);

        cleanup();
    }
    private void cleanup() {
        startPos = null;
        endPos = null;
        helperPos = null;
        centerPoint = null;
        length = 1;
        width = 1;
        angle = 0;
        settingDoorPos = false;
        creatingNew = false;
    }
    public void show() {
        if (currentEntity != null) {
            Vec2 lineStartPos = pApp.box2d.coordWorldToPixels(currentEntity.myBody.getWorldCenter());
            lineStartPos = new Vec2(lineStartPos.x, lineStartPos.y);
            pApp.stroke(30,30,240);
            pApp.strokeWeight(1);
            pApp.line(lineStartPos.x, lineStartPos.y, pApp.mouseX - camera.xOff, pApp.mouseY - camera.yOff);
        }
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
                    pApp.rect(0, 0, pApp.box2d.scalarWorldToPixels(currentLength/2), pApp.box2d.scalarWorldToPixels(currentWidth/2));
                    pApp.popMatrix();

                    pApp.noFill();
                    pApp.pushMatrix();
                    pApp.translate(pApp.mouseX - camera.xOff,pApp.mouseY - camera.yOff);
                    pApp.rotate(-angle);
                    pApp.rect(0,0,pApp.box2d.scalarWorldToPixels(currentLength/2),pApp.box2d.scalarWorldToPixels(currentWidth/2));
                    pApp.popMatrix();
                }
            }
        }
    }

    class EditorQuery implements QueryCallback{

        ArrayList<Fixture> allFixtures = new ArrayList<Fixture>();

        public boolean reportFixture(Fixture fix) {
            if (!allFixtures.contains(fix)) {
                allFixtures.add(fix);
            }
            return true;
        }


    }

}

