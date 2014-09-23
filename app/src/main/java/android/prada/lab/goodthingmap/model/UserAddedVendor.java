package android.prada.lab.goodthingmap.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by 123 on 9/5/2014.
 */

@ParseClassName("UserAddedVendor")
public class UserAddedVendor extends ParseObject {
    public static final String ID = "objectId";
    public static final String VENDOR_ID = "vendorId";
    public static final String USER_ID = "userId";

    public String getId() {
        return getObjectId();
    }

    public String getVendorId() {
        return getString(VENDOR_ID);
    }

    public String getUserId() {
        return getString(USER_ID);
    }
}
