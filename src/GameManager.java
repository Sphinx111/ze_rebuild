//import org.jbox2d.*;
import org.jbox2d.common.Vec2;

/**
 * Created by Eddy on 30/03/2017.
 */
public class GameManager {

    public ClientMapHandler mapHandler;
    ze_rebuild pApp;
    Camera mainCamera;

    public GameManager(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    void setup() {
        mapHandler = new ClientMapHandler(pApp);
        mainCamera = new Camera(pApp);
        mainCamera.setup();
        mapHandler.setup();
        //initialize our fastRandomNumber class
        FastMath.initRand(pApp);
        System.out.println(getClass().getName() + " - Started Successfully");
    }

    void update() {
        mapHandler.update();
        pApp.box2d.step();
        mapHandler.show();
    }
    public void receiveInput(Vec2 direction, boolean[] mouseButtons, float angleToFace, int ID) {
        //if editor mode is active, change mouseInput going to player to {false,false}.
        //if left mouse is pressed, send click to editor
        if (mapHandler.editorMode) {
            boolean[] emptyMouse = {false,false};
            mouseButtons = emptyMouse;
        }
        //else if editor mode is not active, use normal mouse input.
        //As long as the client has been given a player ID to use, send inputs for that player.
        mapHandler.applyInputToActor(direction, mouseButtons, angleToFace, ID);

    }


}
