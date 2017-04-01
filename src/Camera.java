/**
 * Created by Celeron on 4/1/2017.
 */
public class Camera {

    ze_rebuild pApp;

    float xOff = 0;
    float yOff = 0;
    float oldxOff = 0;
    float oldYOff = 0;
    float screenShakeValue = 0;
    float fractionalAdjust = 1;

    float screenCenterX;
    float screenCenterY;

    public Camera(ze_rebuild parentApp) {
        pApp = parentApp;
    }
    public void setup() {
        screenCenterX = pApp.width/2;
        screenCenterY = pApp.height/2;
    }
}
