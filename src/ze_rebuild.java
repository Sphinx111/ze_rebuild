/**
 * Created by Eddy on 30/03/2017.
 */
import processing.core.PApplet;
import shiffman.box2d.Box2DProcessing;

import java.io.Serializable;

public class ze_rebuild extends PApplet {

    public ze_rebuild pApp;
    public Box2DProcessing box2d;
    StateManager stateManager;
    public boolean isServer = false;

    public void setup() {
        //Set up with fixed initial size, set to allow resizing.
        size(1300,700);
        frame.setResizable(true);
        pApp = this;

        //initialise box2d-to-processing interface
        box2d = new Box2DProcessing(pApp);

        //initiate state manager to pass setup/draw calls to different elements depending on state.
        stateManager = new StateManager(this);
        stateManager.setup(this);
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


}