/**
 * Created by Eddy on 30/03/2017.
 */

import org.jbox2d.common.Vec2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static org.jbox2d.common.MathUtils.PI;

public class ClientMapHandler {

    ze_rebuild pApp;

    public ClientMapHandler(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    public void setup() {
        //DEBUG METHOD CALL
        testSetup();
        editor = new MapEditor(pApp);
        editor.setup(this);
    }

    //Gives entities a Unique ID number for serialization and save/load processes.
    public int uniqueIDCounter = 0;

    //Hashmap of all Entities currently loaded allows quick access by the entity's ID number.
    HashMap<Integer,GameEntity> allObjects = new HashMap<Integer, GameEntity>();
    ArrayList<Integer> objectsToRemove = new ArrayList<Integer>();
    HashMap<Integer,GameEntity> objectsUpdated = new HashMap<Integer,GameEntity>(); //any objects moved this turn are listed for subsequent serialization.
    PrintWriter printToSave;
    BufferedReader readToLoad;
    String name = "testMap";

    //Map editor module:
    MapEditor editor;

    //helper variables - Editor
    public boolean editorMode = false;
    enums.Team editorTeam = enums.Team.NONE;
    enums.ActorType editorActorType;
    enums.Team sensorFilterTeam = enums.Team.NONE;
    enums.ItemType editorItemType;
    Vec2 editorDoorOpenPos;
    int editorDoorOpenSpeed = -1;
    int editorDoorCloseSpeed = -1;

    public void setActorSettings(enums.Team currentTeam, enums.ActorType currentType) {
        editorTeam = currentTeam;
        editorActorType = currentType;
    }
    public void setDoorSettings (Vec2 newDoorOpenPos,int openSpeed, int closeSpeed) {
        editorDoorOpenPos = newDoorOpenPos;
        editorDoorOpenSpeed = openSpeed;
        editorDoorCloseSpeed = closeSpeed;
    }
    public void setSensorSettings (enums.Team teamFilter) {
        sensorFilterTeam = teamFilter;
    }

    public void applyInputToActor(Vec2 direction, boolean[] mouseButtons, float angleToFace, int ID) {
        //check the desired Actor exists.
        if (allObjects.containsKey(ID) && allObjects.get(ID) instanceof Actor) {
            //System.out.println(getClass().getName() + ">>> Desired Actor exists, applying input now...");
            Actor actor = (Actor)allObjects.get(ID);
            actor.move(direction);
            actor.setFacing(angleToFace);
            if (mouseButtons[0]) {
                actor.shoot();
            }
        }
    }

    //simple test of entityFactory and entity show() methods.
    public void testSetup() {
        System.out.println(getClass().getName() + ">>> Started testSetup method");
        for (int i = 0; i < 5; i++) {
            Vec2 newOrigin = new Vec2((float)Math.random() * pApp.width, (float)Math.random() * pApp.height);
            newOrigin = pApp.box2d.coordPixelsToWorld(newOrigin);
            float newAngle = 2 * PI * (float)Math.random();
            float newWidth = (float)Math.random() * 4;
            float newHeight = (float)Math.random() * 2;
            enums.EntityType myType = null;
            if (i < 4) {
                newWidth = 1;
                newHeight = 1;
                myType = enums.EntityType.ACTOR;
                editorTeam = enums.Team.ZOMBIE;
                editorActorType = enums.ActorType.BASIC_ZOMBIE;
            } else {
                newWidth = 1;
                newHeight = 1;
                myType = enums.EntityType.ACTOR;
                pApp.stateManager.client.myPlayerID = uniqueIDCounter;
                editorTeam = enums.Team.HUMAN;
                editorActorType = enums.ActorType.SOLDIER;
            }

            entityFactory(newOrigin,newWidth,newHeight,newAngle,myType,uniqueIDCounter);
        }
        System.out.println(getClass().getName() + ">>> Finished testSetup method");
    }

    public void toggleMapEditor() {
        if (editorMode) {
            editorMode = false;
        } else {
            editorMode = true;
        }
    }

    GameEntity createEntityByMouse(Vec2 centerPoint, float angle, float length, float width, enums.EntityType type) {
        GameEntity newEntity = entityFactory(centerPoint, length,width,angle,type,uniqueIDCounter);
        return newEntity;
    }

    GameEntity entityFactory(Vec2 center, float width, float height, float angle, enums.EntityType type, int newID) {
        GameEntity newEntity = null;
        int[] colors = {0,0,0};
        //if the current type is basic fixed block, just create standard GameEntity
        if (type == enums.EntityType.FIXED) {
            newEntity = new GameEntity(pApp);
            colors = enums.EntityType.getColorForType(type);
        //if the current type is Actor, create an Actor as GameEntity
        } else if (type == enums.EntityType.ACTOR) {
            if (editorActorType != null && editorTeam != enums.Team.NONE) {
                newEntity = new Actor(pApp);
                ((Actor)newEntity).setActorProperties(editorTeam,editorActorType);
                colors = enums.Team.getColorForTeam(editorTeam);
            }
        } else if (type == enums.EntityType.SENSOR) {
            if (sensorFilterTeam != null) {
                newEntity = new Sensor(pApp);
                ((Sensor)newEntity).setTeamFilter(sensorFilterTeam);
                colors = enums.EntityType.getColorForType(type);
            }
        } else if (type == enums.EntityType.DOOR) {
            if (editorDoorOpenPos != null && editorDoorOpenSpeed > 0 && editorDoorCloseSpeed > 0) {
                newEntity = new Door(pApp);
                ((Door)newEntity).setDoorClosedPos(center);
                ((Door)newEntity).setDoorOpenPos(editorDoorOpenPos);
                ((Door)newEntity).setDoorSpeed(editorDoorOpenSpeed,editorDoorCloseSpeed);
                colors = enums.EntityType.getColorForType(type);
            }
        } else if (type == enums.EntityType.GAME_LOGIC) {
            newEntity =  new GameLogic(pApp);
            colors = enums.EntityType.getColorForType(type);
            //TODO: gamelogic constructors;
        } else if (type == enums.EntityType.MAP_ITEM) {
            if (editorItemType != null) {
                newEntity = new MapItem(pApp);
                ((MapItem) newEntity).setItemType(editorItemType);
                colors = enums.EntityType.getColorForType(type);
            }
        }

        //if object is actor controlled by client, override color.
        if (newEntity instanceof Actor && newEntity.myID == pApp.stateManager.client.myPlayerID) {
            colors = enums.Team.getColorForTeam(enums.Team.PLAYER);
        }

        //if no object has been created for any reason, terminate early
        if (newEntity == null) {
            return null;
        }
        //set universal properties for each object created.
        newEntity.setCoreProperties(center,width,height,angle,type,newID);
        newEntity.setColor(colors[0],colors[1],colors[2]);
        //build it's physics body and add to the physics world
        newEntity.makeBody();
        //add object to ID-keyed list of all game entities.
        allObjects.put(newID,newEntity);
        //increment uniqueIDcounter (useful if building a level up from scratch)
        uniqueIDCounter++;
        //return the new Entity in case the Factory method is being used for anything later.
        return newEntity;
    }

    void removeObject(int toRemove) {
        if (!objectsToRemove.contains(toRemove)) {
            objectsToRemove.add(toRemove);
        }
    }

    void cleanup() {
        for (int keyV : objectsToRemove) {
            GameEntity g = allObjects.get(keyV);
            pApp.box2d.world.destroyBody(g.myBody);
            allObjects.remove(keyV);
        }
        objectsToRemove.clear();
    }

    GameEntity createEntityFromLoad(Vec2 origin, float wide, float tall, float angle, enums.EntityType type, int newID) {
        GameEntity newEntity = entityFactory(origin,wide,tall,angle,type,newID);
        return newEntity;
    }

    void update() {
        for (GameEntity g : allObjects.values()) {
            g.update();
        }
        if (editorMode) {
            editor.update();
        }
    }

    void show() {
        for (GameEntity g : allObjects.values()) {
            g.show();
        }
    }

    /*
       //THIS METHOD WILL BE REPLACED WITH SERIALIZATION OF OBJECTS. maybe?
    void saveMap() {
        boolean saveReady = false;
        try {
            printToSave = new PrintWriter(name + ".txt");
            saveReady = true;
        } catch (Exception e) {
            e.printStackTrace();
            saveReady = false;
        }
        if (saveReady) {
            for (GameEntity g : allObjects.values()) {
                //close all doors
                if (g.myType == enums.EntityType.DOOR) {
                    g.myBody.setTransform(((Door)g).doorClosedPos, g.myBody.getAngle());
                }
                //prepare stringbuilder for saving a line of data.
                StringBuilder newline = new StringBuilder();
                //get string of blockType for first piece of data
                String typeString;
                if (g.myType != enums.EntityType.MAP_ITEM) {
                    typeString = enums.EntityType.getStringFromType(g.myType) + ",";
                } else {
                    typeString = enums.ItemType.getStringFromItem(((MapItem)g).myItemType) + ",";
                }
                newline.append(typeString);

                int saveID = g.myID;
                newline.append(saveID + ",");
                Vec2 savePos = g.myBody.getPosition();
                newline.append(savePos.x + "," + savePos.y);
                float saveAngle = g.myBody.getAngle();
                newline.append("," + saveAngle);
                float xVal = g.worldWidth;
                newline.append("," + xVal);
                float yVal = g.worldHeight;
                newline.append("," + yVal);
                if (((Door)g).doorOpenPos != null) {
                    Vec2 doorOpenPos = ((Door)g).doorOpenPos;
                    newline.append("," + doorOpenPos.x + "," + doorOpenPos.y);
                }

                printToSave.println(newline);
            }
            printToSave.close();
        }
    }

    MapObject getObjectByID(int idCheck) {
        for (MapObject m : allObjects) {
            if (m.myID == idCheck) {
                return m;
            }
        }
        return null;
    }
    */

    /*
        FROM HERE, METHOD WILL BE REPLACED WITH SERIALIZATION
    void loadMap(String mapName) {
        String inputData = "";
        String[] inputLines;
        boolean loadReady = false;
        try {
            readToLoad = new BufferedReader(new FileReader(mapName + ".txt"));
            loadReady = true;
        } catch (Exception e) {
            e.printStackTrace();
            loadReady = false;
        }

        if (loadReady) {
            int highestLoadID = 0; //var tracks highest ID loaded in, so mapHandler can continue adding ID's from new highest value.
            allObjects.clear(); //delete all existing map objects when loading new map.
            ArrayList<MapObject> sensorsNeedingLinks = new ArrayList<MapObject>();
            pApp.fill(255);
            pApp.rect(pApp.width / 2 - 100, 50, 200, 50);
            pApp.fill(0);
            pApp.text("Loading Map", pApp.width / 2 - 75, 40);
            try {
                //get data from file and store in memory quickly.
                StringBuilder sb = new StringBuilder();
                String line = readToLoad.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = readToLoad.readLine();
                }
                inputData = sb.toString();
                readToLoad.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            inputLines = inputData.split(System.getProperty("line.separator"));
            //build MapObjects from saved data
            try {
                for (int i = 0; i < inputLines.length; i++) {
                    String[] dataPieces = inputLines[i].split(",");
                    enums.EntityType loadType = null;
                    enums.ItemType loadItem = null;
                    int loadMode = 0;
                    if (dataPieces[0].contains("ITEM")) {
                        loadItem = enums.ItemType.getTypeFromString(dataPieces[0]);
                        loadMode = 1;
                    } else {
                        loadType = enums.EntityType.getTypeFromString(dataPieces[0]);
                        loadMode = 2;
                    }
                    int newID = Integer.parseInt(dataPieces[1]);
                    float x = Float.parseFloat(dataPieces[2]);
                    float y = Float.parseFloat(dataPieces[3]);
                    float angle = Float.parseFloat(dataPieces[4]);
                    Vec2 newPos = new Vec2(x, y);
                    float wide = Float.parseFloat(dataPieces[5]);
                    float tall = Float.parseFloat(dataPieces[6]);
                    MapObject newMapObject = null;
                    if (loadMode == 1) {
                        newMapObject = createEntityFromLoad(newPos, wide, tall, angle, loadItem, newID);
                    } else {
                        newMapObject = createMapObjectFromLoad(newPos, wide, tall, angle, loadType, newID);
                    }
                    if (newID > highestLoadID) {
                        highestLoadID = newID;
                    }
                    if (dataPieces.length >= 10) {
                        float xOpen = Float.parseFloat(dataPieces[7]);
                        float yOpen = Float.parseFloat(dataPieces[8]);
                        Vec2 openPos = new Vec2(xOpen, yOpen);
                        openPos = new Vec2(openPos.x, openPos.y);
                        newMapObject.doorOpenPos = openPos;
                    }
                }
                //connect back up all linked sensors.
                for (MapObject m : sensorsNeedingLinks) {
                    m.linkToDoor(getObjectByID(m.linkedDoorID));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            uniqueIDCounter = highestLoadID + 1; // lets map handler continue adding map objects from loaded ID system
            sensorsNeedingLinks.clear();
        }
    }
    */

}
