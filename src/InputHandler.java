import org.jbox2d.common.Vec2;

/**
 * Created by Celeron on 3/31/2017.
 */
public class InputHandler {
    //this class receives and handles mouse/keyboard input before passing to statemanager for redirection.

    ze_rebuild pApp;
    boolean[] keys = new boolean[9];
    // 0 - W Forwards
    // 1 - A Left
    // 2 - S Backwards
    // 3 - D Right
    // 4 - Esc - openMenu/closeGame
    // 5 - Shift/Space - Sprint!
    // 6 - left click
    // 7 - right click
    // 8 - M - mapEditor toggle (debugOnly);

    public InputHandler(ze_rebuild parentApp) {
        pApp = parentApp;
    }
    public void setup() {
        System.out.println(getClass().getName() + " - Started Successfully");
        //setup variables/control schemes here.
    }
    public void update() {
        enums.GAMESTATE currentState = pApp.stateManager.getGameState();

        int localID = pApp.stateManager.client.myPlayerID;
        //if there's a game running, and the client has been assigned a player ID, send the current inputs
        if (currentState == enums.GAMESTATE.GAMEPLAY && localID != -1) {
            System.out.println(getClass().getName() + ">>> game is running and player exists, getting input");
            float vertical1 = 0;
            float vertical2 = 0;
            if (keys[0]) {vertical1 = 1;}
            if (keys[2]) {vertical2 = -1;}
            float verticalSum = vertical1 + vertical2;
            float horizontal1 = 0;
            float horizontal2 = 0;
            if (keys[1]) {horizontal1 = -1;}
            if (keys[3]) {horizontal2 = 1;}
            float horizontalSum = horizontal1 + horizontal2;
            Vec2 directionOfInput = new Vec2(horizontalSum,verticalSum);
            boolean[] mouseButtons = {keys[6],keys[7]};

            float newAngle = getAngleInstructions(localID);
            pApp.stateManager.gameManager.receiveInput(directionOfInput,mouseButtons, newAngle, localID);
        }
    }

    //returns angle from player to the current mouse location
    public float getAngleInstructions(int playerID) {
        Camera mainCamera = pApp.stateManager.gameManager.mainCamera;
        Vec2 mouseWorldPos = pApp.box2d.coordPixelsToWorld(pApp.mouseX - mainCamera.xOff,pApp.mouseY - mainCamera.yOff);
        ClientMapHandler activeMapHandler = pApp.stateManager.gameManager.mapHandler;
        if (activeMapHandler.allObjects.containsKey(playerID)) {
            Vec2 playerWorldPos = activeMapHandler.allObjects.get(playerID).myBody.getWorldCenter();
            Vec2 vectorToMouse = mouseWorldPos.add(playerWorldPos.negate());
            float newAngle = (float)Math.atan2(vectorToMouse.y,vectorToMouse.x);
            return newAngle;
        } else {
            System.out.println(getClass().getName() + ">>> Did not find player with ID: " + playerID + " to calculate angle for");
            return 0;
        }
    }

    public void keyInCheck() {

        //check movement keys
        if (pApp.key == 'W' || pApp.key == 'w') {
            keys[0] = true;
        } else if (pApp.key == 'A' || pApp.key == 'a') {
            keys[1] = true;
        } else if (pApp.key == 'S' || pApp.key == 's') {
            keys[2] = true;
        } else if (pApp.key == 'D' || pApp.key == 'd') {
            keys[3] = true;
        } else if (pApp.keyCode == pApp.ESC) {
            //Esc key toggles on press
            if (!keys[4]) {
                keys[4] = true;
            } else {
                keys[4] = false;
            }
        } else if (pApp.keyCode == pApp.SHIFT) {
            keys[5] = true;
        } else if (pApp.key == 'M' || pApp.key == 'm') {
            if (!keys[8]) {
                keys[8] = true;
            } else {
                keys[8] = false;
            }
        }


    }

    public void keyOutCheck() {
        if (pApp.key == 'W' || pApp.key == 'w') {
            keys[0] = false;
        } else if (pApp.key == 'A' || pApp.key == 'a') {
            keys[1] = false;
        } else if (pApp.key == 'S' || pApp.key == 's') {
            keys[2] = false;
        } else if (pApp.key == 'D' || pApp.key == 'd') {
            keys[3] = false;
        } else if (pApp.keyCode == pApp.SHIFT) {
            keys[5] = false;
        }
    }

    public void mouseInCheck() {
        //what do we do if the mouse is pressed
        if (pApp.mouseButton == pApp.LEFT) {
            keys[6] = true;
        } else if (pApp.mouseButton == pApp.RIGHT) {
            keys[7] = true;
        }

        enums.GAMESTATE currentState = pApp.stateManager.getGameState();

        if (currentState == enums.GAMESTATE.MENU) {
            pApp.stateManager.mainMenu.receiveMouseClick();
        }
    }
    public void mouseOutCheck() {
        //what do we do if the mouse is released
        if (pApp.mouseButton == pApp.LEFT) {
            keys[6] = false;
        } else if (pApp.mouseButton == pApp.RIGHT) {
            keys[7] = false;
        }
    }



}
