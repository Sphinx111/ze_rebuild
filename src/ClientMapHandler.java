/**
 * Created by Eddy on 30/03/2017.
 */

import org.jbox2d.common.Vec2;

import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientMapHandler {

    ze_rebuild pApp;

    public ClientMapHandler(ze_rebuild parentApp) {
        pApp = parentApp;

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

    GameEntity createMapObjectByMouse(Vec2 origin, Vec2 end, Vec2 objWidthSet, EntityType type) {
        float midX = (origin.x + end.x) / 2;
        float midY = -(origin.y + end.y) / 2;
        Vec2 midPoint = new Vec2(midX, midY);
        Vec2 vecStartEnd = end.add(origin.mul(-1));
        float angle = (float) Math.atan2(vecStartEnd.y, vecStartEnd.x);
        float lengthOfLine = (float) Math.sqrt((vecStartEnd.x * vecStartEnd.x) + (vecStartEnd.y * vecStartEnd.y));
        Vec2 objWidthVec = objWidthSet.add(end.mul(-1));
        float widthOfLine = (float) Math.sqrt((objWidthVec.x * objWidthVec.x) + (objWidthVec.y * objWidthVec.y));
        GameEntity newEntity = entityFactory(midPoint, lengthOfLine,widthOfLine,-angle,type,uniqueIDCounter);
        return newEntity;
    }

    GameEntity entityFactory(Vec2 center, float width, float height, float angle, EntityType type, int newID) {



        uniqueIDCounter+=1;
    }

    MapObject createItemByMouse(Vec2 origin, ItemType item) {
        float myWidth = box2d.scalarPixelsToWorld(item.RIFLE_LENGTH);
        float myHeight = box2d.scalarPixelsToWorld(item.RIFLE_WIDTH);
        MapObject newItem = new MapObject(origin, myWidth, myHeight, 0, item, uniqueIDCounter);
        allObjects.add(newItem);
        uniqueIDCounter += 1;
        return newItem;
    }

    void objectUpdated(MapObject iChanged) {
        objectsUpdated.add((Object) iChanged);
    }

    void objectUpdated(Actor iChanged) {
        objectsUpdated.add((Object) iChanged);
    }

    MapObject createItemFromLoad(Vec2 origin, float wide, float tall, float angle, ItemType type, int newID) {
        MapObject newItem = new MapObject(origin, wide, tall, angle, type, newID);
        allObjects.add(newItem);
        return newItem;
    }

    void createActorByMouse(Vec2 origin, Team team, Type type) {

    }

    void createActorFromLoad(Vec2 origin, Team team, Type type) {

    }

    void removeObject(MapObject toRemove) {
        if (!objectsToRemove.contains(toRemove)) {
            objectsToRemove.add(toRemove);
        }
    }

    void cleanup() {
        for (MapObject m : objectsToRemove) {
            box2d.world.destroyBody(m.body);
            allObjects.remove(m);
        }
        objectsToRemove.clear();
    }

    MapObject createMapObjectFromLoad(Vec2 origin, float wide, float tall, float angle, BlockType type, int newID) {
        Vec2 alteredPos = new Vec2(origin.x, origin.y);
        MapObject newObject = new MapObject(alteredPos, wide, tall, angle, type, newID);
        allObjects.add(newObject);
        return newObject;
    }

    MapObject createMapObjectFromCopy(Vec2 origin, float wide, float tall, float angle, BlockType type, int newID) {
        MapObject newObject = new MapObject(origin, wide, tall, angle, type, newID);
        allObjects.add(newObject);
        return newObject;
    }

    void update() {
        for (MapObject m : allObjects) {
            m.update();
        }
    }

    void show() {
        for (MapObject m : allObjects) {
            m.show();
        }
    }

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
            for (MapObject m : allObjects) {
                //close all doors
                if (m.myType == BlockType.DOOR) {
                    m.body.setTransform(m.doorClosedPos, m.body.getAngle());
                }
                //prepare stringbuilder for saving a line of data.
                StringBuilder newline = new StringBuilder();
                //get string of blockType for first piece of data
                String typeString;
                if (m.myType != BlockType.ITEM) {
                    typeString = BlockType.getStringFromType(m.myType) + ",";
                } else {
                    typeString = ItemType.getStringFromItem(m.myItemType) + ",";
                }
                newline.append(typeString);

                int saveID = m.myID;
                newline.append(saveID + ",");
                Vec2 savePos = m.body.getPosition();
                newline.append(savePos.x + "," + savePos.y);
                float saveAngle = m.body.getAngle();
                newline.append("," + saveAngle);
                float xVal = box2d.scalarPixelsToWorld(m.pixWidth);
                newline.append("," + xVal);
                float yVal = box2d.scalarPixelsToWorld(m.pixHeight);
                newline.append("," + yVal);
                int linkedID = m.linkedDoorID;
                newline.append("," + linkedID);
                if (m.doorOpenPos != null) {
                    Vec2 doorOpenPos = m.doorOpenPos;
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
            fill(255);
            rect(width / 2 - 100, 50, 200, 50);
            fill(0);
            text("Loading Map", width / 2 - 75, 40);
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
                    BlockType loadType = null;
                    ItemType loadItem = null;
                    int loadMode = 0;
                    if (dataPieces[0].contains("ITEM")) {
                        loadItem = ItemType.getTypeFromString(dataPieces[0]);
                        loadMode = 1;
                    } else {
                        loadType = BlockType.getTypeFromString(dataPieces[0]);
                        loadMode = 2;
                    }
                    int newID = Integer.parseInt(dataPieces[1]);
                    float x = Float.parseFloat(dataPieces[2]);
                    float y = Float.parseFloat(dataPieces[3]);
                    float angle = Float.parseFloat(dataPieces[4]);
                    Vec2 newPos = new Vec2(x, y);
                    float wide = Float.parseFloat(dataPieces[5]);
                    float tall = Float.parseFloat(dataPieces[6]);
                    int linkedID = Integer.parseInt(dataPieces[7]);
                    MapObject newMapObject = null;
                    if (loadMode == 1) {
                        newMapObject = createItemFromLoad(newPos, wide, tall, angle, loadItem, newID);
                    } else {
                        newMapObject = createMapObjectFromLoad(newPos, wide, tall, angle, loadType, newID);
                    }
                    if (newID > highestLoadID) {
                        highestLoadID = newID;
                    }
                    if (loadType == BlockType.SENSOR && linkedID != -1) {
                        newMapObject.linkedDoorID = linkedID;
                        sensorsNeedingLinks.add(newMapObject);
                    }
                    if (dataPieces.length >= 10) {
                        float xOpen = Float.parseFloat(dataPieces[8]);
                        float yOpen = Float.parseFloat(dataPieces[9]);
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

}
