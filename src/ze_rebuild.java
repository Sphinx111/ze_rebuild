/**
 * Created by Eddy on 30/03/2017.
 */
import processing.core.PApplet;
import shiffman.box2d.Box2DProcessing;

public class ze_rebuild extends PApplet {

    public ze_rebuild pApp;
    public Box2DProcessing box2d;
    StateManager stateManager;
    InputHandler inputHandler;
    public boolean isServer = false;

    public void settings() {
        //Set up with fixed initial size, set to allow resizing.
        size(1300,700);
        //surface.setResizable(true);
    }

    public void setup() {
        pApp = this;

        //surface.setResizable(true);

        //initialise box2d-to-processing interface
        box2d = new Box2DProcessing(pApp);

        //initialise inputHandler as early as possible
        inputHandler = new InputHandler(pApp);

        //initiate state manager to pass setup/draw calls to different elements depending on state.
        stateManager = new StateManager(pApp);
        stateManager.setup(pApp);
    }

    public void draw() {
        //clear the screen each frame;
        background(51);
        //pass the draw() call to the stateManager for redirection
        stateManager.passDraw();
    }

    public static void main (String[] args) {
        PApplet.main("ze_rebuild");
    }

    public void keyPressed() {
        inputHandler.keyInCheck();
    }
    public void keyReleased() {
        inputHandler.keyOutCheck();
    }
    public void mousePressed() {
        inputHandler.mouseInCheck();
    }
    public void mouseReleased() {
        inputHandler.mouseOutCheck();
    }

}
