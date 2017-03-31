
import org.jbox2d.*;

/**
 * Created by Eddy on 30/03/2017.
 */
public class GameManager {

    public ClientMapHandler mapHandler;
    ze_rebuild pApp;

    public GameManager(ze_rebuild parentApp) {
        pApp = parentApp;
    }

    void setup() {
        mapHandler = new ClientMapHandler(pApp);
        System.out.println(getClass().getName() + " - Started Successfully");
    }

    void update() {
        mapHandler.update();
        mapHandler.show();
    }
    public void receiveInput(boolean[] keysAndMouse) {

    }


}
