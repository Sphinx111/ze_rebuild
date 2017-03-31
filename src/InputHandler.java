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

        if (currentState == enums.GAMESTATE.GAMEPLAY) {
            pApp.stateManager.gameManager.receiveInput(keys);
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
