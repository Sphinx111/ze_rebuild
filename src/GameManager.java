import shiffman.box2d.Box2DProcessing;

/**
 * Created by Eddy on 30/03/2017.
 */
public class GameManager {

    public ClientMapHandler mapHandler;
    ze_rebuild pApp;
    public Box2DProcessing box2d;

    public GameManager(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    void setup() {
        //setup box2d interface
        box2d = new Box2DProcessing(pApp);
        mapHandler = new ClientMapHandler(pApp);
    }

    void update() {

    }

}
