import org.jbox2d.common.Vec2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Celeron on 3/30/2017.
 */
public class GameLogic extends GameEntity {

    //Gamelogic fields
    HashMap<Integer,GameEntity> logicLinks;
    HashMap<Integer,GameEntity> altLogicLinks;
    String label;
    enums.LogicType myLogicType; //Timer, 2stepTimer, GameEnd, damage, move(teleport)
    int timerDelay1 = -1;
    int timerDelay2 = -1;
    float strengthOfEffect; //amount of damage to apply per tick / amount to slow
    Vec2 teleportPos;

    //helper variables
    boolean resettable;
    int timeActivated = -1;
    boolean damaging = false;
    boolean slowing = false;

    public GameLogic(ze_rebuild parentApp) {
        super.pApp = parentApp;
    }

    public void setResettable(boolean isItResettable) {
        resettable = isItResettable;
    }
    public void setTimerDelays(int timer1, int timer2) {
        timerDelay1 = timer1;
        timerDelay2 = timer2;
    }
    public void setLabel (String newLabel) {
        label = newLabel;
    }
    public void setLogicType(enums.LogicType newType) {
        myLogicType = newType;
    }
    public void receiveSignal() {
        timeActivated = pApp.frameCount;
    }
    public void setTeleportPos(Vec2 newPos) {
        teleportPos = newPos;
    }
    public void setStrengthOfEffect(float effectiveness) {
        strengthOfEffect = effectiveness;
    }
    public void addLogicLink(GameEntity toAdd) {
        if (!logicLinks.containsKey(toAdd.myID)) {
            logicLinks.put(toAdd.myID, toAdd);
        }
    }
    public void removeLogicLink(GameEntity toRemove) {
        if (logicLinks.containsKey(toRemove.myID)) {
            logicLinks.remove(toRemove.myID);
        }
    }
    public Collection<GameEntity> getLogicLinks () {
        return logicLinks.values();
    }
    public void sendSignal() {
        for (GameEntity g : logicLinks.values()) {
            if (g instanceof Sensor) {
                ((Sensor)g).receiveSignal();
            } else if (g instanceof Door) {
                ((Door)g).receiveSignal();
            } else if (g instanceof GameLogic) {
                ((GameLogic)g).receiveSignal();
            }
        }
    }
    public void sendSignal2() {

    }
    public void runLogic() {
        //If logic is a 2step timer or a game end timer, check whether 2nd step should be triggered first.
        if (myLogicType != enums.LogicType.TIMER) {
            if (pApp.frameCount > timeActivated + timerDelay2 && timerDelay2 != -1 && timeActivated != -1) {
                //if 2nd timer delay is spent, break early, send secondary signals out.
                timeActivated = -1;
                slowing = false;
                damaging = false;
                sendSignal();
                return;
            } else if (pApp.frameCount > timeActivated + timerDelay1 && timerDelay1 != -1 &&timeActivated != -1) {
                if (myLogicType == enums.LogicType.APPLY_DAMAGE) {
                    damaging = true;
                } else if (myLogicType == enums.LogicType.SLOW) {
                    slowing = true;
                }
                sendSignal();
                return;
            }
        //else if the logic is Damage, Slow or simple timer, check if timer1 has elapsed.
        } else {
            if (pApp.frameCount > timeActivated + timerDelay1 && timerDelay1 != -1 &&timeActivated != -1) {
                sendSignal();
                timeActivated = -1;
                return;
            }
        }
    }

}
