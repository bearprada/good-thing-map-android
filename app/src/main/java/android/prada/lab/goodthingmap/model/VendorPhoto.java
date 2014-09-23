package android.prada.lab.goodthingmap.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by 123 on 9/2/2014.
 */

@ParseClassName("VendorPhoto")
public class VendorPhoto extends ParseObject {
    public static final String ID = "objectId";
    public static final String VENDOR_ID = "vendorId";
    public static final String URL = "photoUrl";

    public String getId() {
        return getObjectId();
    }

    public String getVendorId() {
        return getString(VENDOR_ID);
    }

    public String getUrl() {
        return getString(URL);
    }
}
