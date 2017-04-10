package Server;

/**
 * Created by Celeron on 3/31/2017.
 */
public class MapItem extends GameEntity {

    enums.ItemType myItemType;

    String myShape;
    String myTexture;

    boolean exhaustible;
    int qtyAvailable;

    public MapItem (ze_rebuild parentApp) {
        pApp = parentApp;
    }
    public void setItemType (enums.ItemType newType) {
        myItemType = newType;
    }
    public void setExhaustibleAndQty (boolean isExhaustible, int qty) {
        exhaustible = isExhaustible;
        qtyAvailable = qty;
    }

}
