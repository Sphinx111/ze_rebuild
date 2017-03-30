
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
    }

    void update() {

    }

    public enum Team {
        HUMAN,
        ZOMBIE,
        NONE;
    }

    public enum ActorType {
        SOLDIER,
        ZOMBIE,
        BIG_ZOMBIE,
        CIVILIAN;
    }

}
