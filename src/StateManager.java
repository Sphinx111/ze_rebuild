import org.jbox2d.common.Vec2;

/**
 * Created by Eddy on 30/03/2017.
 */

public class StateManager {
    //This class will handle save/loadgames, and initialises/ends gameplay elements.

    private ze_rebuild pApp;
    protected enums.GAMESTATE currentState;
    MainMenu mainMenu;
    GameManager gameManager;
    PauseMenu pauseMenu;
    LoadManager loadManager;

    public int loadPercent = 0;

    //Split these elements up when separating client/server builds
    ClientModule client;


    public StateManager(ze_rebuild parent) {
        pApp = parent;
    }

    public void setup(ze_rebuild parentApp) {
        currentState = enums.GAMESTATE.STARTUP;

        //Load up all modules/assets on a separate thread so that the app does not freeze.
        LoadThread loadThread = new LoadThread();
        loadThread.start();
    }


    private void update() {
        while (loadPercent < 100) {
            pApp.noFill();
            pApp.rect(pApp.width/2 - 100, pApp.height/2 - 20, 200, 40);
            pApp.fill(0,100,100);
            pApp.rect(pApp.width/2 - 100, pApp.height/2 - 20, 200 * loadPercent / 100, 40);
            pApp.fill(255);
            pApp.textSize(10);
            pApp.text("Loading", pApp.width/2 - 20, pApp.height/2 - 5);
        }

        currentState = enums.GAMESTATE.GAMEPLAY;
    }

    void passDraw() {
        switch(currentState) {
            case STARTUP:
                this.update();
                break;
            case MENU:
                mainMenu.update();
                break;
            case GAMEPLAY:
                gameManager.mainCamera.applyOffset();
                gameManager.update();
                pApp.inputHandler.update();
                gameManager.mainCamera.removeOffset();
                break;
            case PAUSE:
                pauseMenu.update();
                break;
            case LOADING:
                loadManager.update();
                break;
        }

        //always pass ticks to server/client modules,regardless of gamestate:
        client.update();
        //server.update();

    }

    public void setGameState (enums.GAMESTATE newState) {
        currentState = newState;
    }

    public enums.GAMESTATE getGameState () {
        return currentState;
    }

    private class LoadThread extends Thread {

        @Override
        public void run() {
            //Load main menu and run initial setup.
            mainMenu = new MainMenu(pApp);
            mainMenu.setup();
            loadPercent = 10;

            //initialise physics world
            pApp.box2d.createWorld(new Vec2(0,0));
            pApp.box2d.setScaleFactor(20);

            //load gameManager, don't need initial setup yet (server must connect first)
            gameManager = new GameManager(pApp);
            loadPercent = 50;

            //load pauseMenu, don't need initial setup yet (player must start game first)
            pauseMenu = new PauseMenu(pApp);
            loadPercent = 60;

            //load loadManager (weird naming), don't need initial setup yet (server must connect first)
            loadManager = new LoadManager(pApp);
            loadPercent = 75;

            //load clientModule, run initial setup so app can find any servers running.
            client = new ClientModule(pApp);
            client.setup();
            loadPercent = 80;

            //run gameManager setup (TODO: Remove for production, testing only at this location)
            gameManager.setup();

            loadPercent = 100;

            System.out.println("LoadThread finished processing");

            return;
        }

    }

}
