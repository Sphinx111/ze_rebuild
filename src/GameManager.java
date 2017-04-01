
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
        mapHandler.setup();
        //initialize our fastRandomNumber class
        FastMath.initRand(pApp);
        System.out.println(getClass().getName() + " - Started Successfully");
    }

    void update() {
        System.out.println(getClass().getName() + ">>> Received draw loop from stateManager! It's my turn!");
        mapHandler.update();
        pApp.box2d.step();
        mapHandler.show();
    }
    public void receiveInput(Vec2 direction, boolean[] mouseButtons, float angleToFace, int ID) {
        //As long as the client has been given a player ID to use, send inputs for that player.
        mapHandler.applyInputToActor(direction, mouseButtons, angleToFace, ID);
    }


}
