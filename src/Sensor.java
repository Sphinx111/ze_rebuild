import org.jbox2d.dynamics.contacts.ContactEdge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Celeron on 3/30/2017.
 */
public class Sensor extends GameEntity {

    //Sensor fields
    HashMap<Integer,GameEntity> sensorLinks;
    int tickButtonPressed;
    boolean wasPressed;
    boolean resettable;
    enums.Team teamFilter;
    HashMap<Integer,GameEntity> touchingObjects;

    public Sensor(ze_rebuild parentApp) {
        super.pApp = parentApp;
    }
    public void setSensorResettable (boolean reset) {
        resettable = reset;
    }
    public void addLink(GameEntity toAdd) {
        if (!sensorLinks.containsKey(toAdd.myID)) {
            sensorLinks.put(toAdd.myID, toAdd);
        }
    }
    public void removeLink(GameEntity toRemove) {
        if (sensorLinks.containsKey(toRemove.myID)) {
            sensorLinks.remove(toRemove.myID);
        }
    }
    public ArrayList<Actor> getTouchingActors(enums.Team teamFilter) {
        ArrayList<Actor> touchingActors = new ArrayList<Actor>(0);
        if (teamFilter == null) {
            for (GameEntity g : touchingObjects.values()) {
                if (g instanceof Actor) {
                    touchingActors.add((Actor)g);
                }
            }
            return touchingActors;
        } else {
            for (GameEntity g : touchingObjects.values()) {
                if (g instanceof Actor) {
                    if (((Actor)g).myTeam == teamFilter) {
                        touchingActors.add((Actor) g);
                    }
                }
            }
            return touchingActors;
        }
    }
    public boolean checkCollisions() {
        boolean returnMe = false;
        for (ContactEdge ce = myBody.getContactList(); ce != null; ce = ce.next) {
            Object other = ce.other.getUserData();
            if (other instanceof Actor) {
                Actor stranger = (Actor)other;
                if (stranger.myTeam == teamFilter || teamFilter == null) {
                    returnMe = true;
                    if (!touchingObjects.containsKey(stranger.myID)) {
                        touchingObjects.put(stranger.myID, stranger);
                    }
                }
            }
        }
        return returnMe;
    }
    public void setTeamFilter(enums.Team teamToAccept) {
        teamFilter = teamToAccept;
    }
    public Collection<GameEntity> getSensorLinks() {
        return sensorLinks.values();
    }
    public void sendSignal() {
        for (GameEntity g : sensorLinks.values()) {
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
    public void receiveSignal() {
        if (!wasPressed) {
            wasPressed = true;
            sendSignal();
        } else if (resettable) {
            wasPressed = false;
        }
    }

}
