package Server;

import org.jbox2d.common.Vec2;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Celeron on 3/30/2017.
 */
public class GameLogic extends GameEntity {

    //Gamelogic fields
    HashMap<Integer, GameEntity> logicLinks = new HashMap<Integer, GameEntity>();
    HashMap<Integer, GameEntity> altLogicLinks = new HashMap<Integer, GameEntity>();
    String label;
    enums.LogicType myLogicType = enums.LogicType.TWO_STAGE_TIMER; //Timer, 2stepTimer, GameEnd, damage, move(teleport)
    int timerDelay1 = 120;
    int timerDelay2 = 480;
    float strengthOfEffect; //amount of damage to apply per tick / amount to slow
    Vec2 teleportPos;

    //helper variables
    boolean resettable = true;
    int timeActivated = -1;
    boolean damaging = false;
    boolean slowing = false;
    int timeActivated2 = -1;

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

    public void setLabel(String newLabel) {
        label = newLabel;
    }

    public void setLogicType(enums.LogicType newType) {
        myLogicType = newType;
    }

    public void receiveSignal() {
        timeActivated = pApp.frameCount;
        System.out.println(getClass().getName() + ">>>SIGNAL RECEIVED");
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

    public Collection<GameEntity> getLogicLinks() {
        return logicLinks.values();
    }

    public void sendSignal() {
        for (GameEntity g : logicLinks.values()) {
            System.out.println(getClass().getName() + ">>> SIGNAL SENT");
            if (g instanceof Sensor) {
                ((Sensor) g).receiveSignal();
            } else if (g instanceof Door) {
                ((Door) g).receiveSignal();
            } else if (g instanceof GameLogic) {
                ((GameLogic) g).receiveSignal();
            }
        }
    }

    public void sendSignal2() {

    }

    public void runLogic() {
        if (myLogicType == enums.LogicType.TIMER) {
            simpleTimerLogic();
        } else if (myLogicType == enums.LogicType.TWO_STAGE_TIMER) {
            twoStepTimerLogic();
        } else {
            simpleTimerLogic();
        }
    }

    @Override
    public void update() {
        runLogic();
    }

    private void simpleTimerLogic() {
        //As long as the timer has been activated at least once
        if (timeActivated != -1) {
            //check whether enough time has passed to trigger the logic
            if (pApp.frameCount > timeActivated + timerDelay1) {
                sendSignal();
                timeActivated = -1;
            }
        }
    }

    private void twoStepTimerLogic() {
        //As long as the timer has been activated at least once
        if (timeActivated != -1) {
            if (timeActivated2 == -1 && pApp.frameCount > timeActivated + timerDelay1) {
                timeActivated2 = pApp.frameCount;
                sendSignal();
            } else if (timeActivated2 != -1 && pApp.frameCount > timeActivated2 + timerDelay2) {
                sendSignal();
                timeActivated = -1;
                timeActivated2 = -1;
            }
        }

    }
}
