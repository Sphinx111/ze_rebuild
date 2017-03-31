import org.jbox2d.common.Vec2;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Celeron on 3/30/2017.
 */
public class Door extends GameEntity {

    //Door fields
    HashMap<Integer,GameEntity> doorLinks; //
    Vec2 doorClosedPos;
    Vec2 doorOpenPos;
    Vec2 hinge;
    int doorOpenTime;
    int doorCloseTime;
    boolean isHinged; //Not in use in early versions!
    boolean fullOpen;
    boolean fullClosed;
    boolean sendSignalOnOpen;
    boolean sendSignalOnClose;

    public Door(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    public void setHinge(Vec2 hingePoint) {
        //Not in use yet
    }
    public void setDoorOpenPos(Vec2 doorOpenPos) {
        this.doorOpenPos = doorOpenPos;
    }
    public void setDoorClosedPos(Vec2 doorClosedPos) {
        this.doorClosedPos = doorClosedPos;
    }
    public void setSignalOutConditions(boolean openSig, boolean closeSig) {
        sendSignalOnOpen = openSig;
        sendSignalOnClose = closeSig;
    }
    public void setDoorSpeed(int open,int closed) {
        doorOpenTime = open;
        doorCloseTime = closed;
    }
    public void openDoor() {
        if (fullClosed) {
            fullClosed = false;
            Vec2 direction = doorOpenPos.add(doorClosedPos.negate());
            Vec2 magnitude = direction.mul(1/doorOpenTime);
            myBody.setLinearVelocity(magnitude);
        }
    }
    public void closeDoor() {
        if (fullOpen) {
            fullOpen = false;
            Vec2 direction = doorClosedPos.add(doorOpenPos.negate());
            Vec2 magnitude = direction.mul(1/doorCloseTime);
            myBody.setLinearVelocity(magnitude);
        }
    }
    public void checkDoorMovement() {
        if (!fullClosed && !fullOpen) {
            Vec2 myPos = myBody.getWorldCenter();
            float distance = myPos.add(doorOpenPos.negate()).length();
            if (distance <= 0.05) {
                myBody.setLinearVelocity(new Vec2(0,0));
                if (sendSignalOnOpen) {
                    sendSignal();
                }
            }
            distance = myPos.add(doorClosedPos.negate()).length();
            if (distance <= 0.05) {
                myBody.setLinearVelocity(new Vec2(0,0));
                if (sendSignalOnClose) {
                    sendSignal();
                }
            }
        }
    }
    public void addDoorLink(GameEntity link) {
        if (!doorLinks.containsKey(link.myID)) {
            doorLinks.put(link.myID,link);
        }
    }
    public void removeDoorLink(GameEntity link) {
        if (doorLinks.containsKey(link.myID)) {
            doorLinks.remove(link.myID);
        }
    }
    public Collection<GameEntity> getDoorLinks() {
        return doorLinks.values();
    }
    public void receiveSignal() {
        if (fullClosed) {
            openDoor();
        } else if (fullOpen) {
            closeDoor();
        }
    }
    public void sendSignal() {
        for (GameEntity g : doorLinks.values()) {
            if (g instanceof Sensor) {
                Sensor s = (Sensor)g;
                s.receiveSignal();
            } else if (g instanceof Door) {
                Door d = (Door)g;
                d.receiveSignal();
            } else if (g instanceof GameLogic) {
                GameLogic l = (GameLogic)g;
                l.receiveSignal();
            }
        }
    }
}
