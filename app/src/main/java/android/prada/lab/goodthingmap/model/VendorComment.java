package android.prada.lab.goodthingmap.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by 123 on 9/2/2014.
 */

@ParseClassName("VendorComment")
public class VendorComment extends ParseObject {
    public static final String ID = "objectId";
    public static final String VENDOR_ID = "vendorId";
    public static final String USER_ID = "userId";
    public static final String COMMENT = "comment";

    public String getId() {
        return getObjectId();
    }

    public String getUserId() {
        return getString(USER_ID);
    }

    public String getComment() {
        return getString(COMMENT);
    }

    public Date getPostTime() {
        return this.getUpdatedAt();
    }
}
