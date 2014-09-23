package android.prada.lab.goodthingmap.model;

import android.location.Location;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

/**
 * Created by 123 on 9/1/2014.
 */

@ParseClassName("Vendor")
public class VendorData extends ParseObject {
    public static final String ID = "objectId";
    public static final String VENDOR_NAME = "vendorName";
    public static final String DESCRIPTION = "description";
    public static final String BRIEF_DESCRIPTION = "briefDescription";
    public static final String TYPE = "type";
    public static final String ICON_URL = "iconUrl";
    public static final String BUSINESS_HOUR = "businessHour";
    public static final String LOCATION = "location";
    public static final String ADDRESS = "address";
    public static final String NUM_LIKE = "numLike";
    public static final String NUM_SHARE = "numShare";

    public enum Type {
        MAIN, SNACK, FRUIT, TBI, OTHER
    }

    public String getId() {
        return getObjectId();
    }

    public String getTitle() {
        return getString(VENDOR_NAME);
    }

    public String getBriefDescription() {
        return getString(BRIEF_DESCRIPTION);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public String getBusinessHour() {
        return getString(BUSINESS_HOUR);
    }

    public String getIconUrl() {
        return getString(ICON_URL);
    }

    public Location getLocation() {
        ParseGeoPoint geoPoint =  getParseGeoPoint(LOCATION);
        Location location = new Location(getTitle());

        if(geoPoint != null) {
            location.setLatitude(geoPoint.getLatitude());
            location.setLongitude(geoPoint.getLongitude());
        }

        return location;
    }

    public String getAddress() {
        return getString(ADDRESS);
    }

    public int getNumLike() {
        return getInt(NUM_LIKE);
    }

    public int getNumShare() {
        return getInt(NUM_SHARE);
    }

    public Type getType() {
        switch(getInt(TYPE)) {
            case 0:
                return Type.MAIN;
            case 1:
                return Type.SNACK;
            case 2:
                return Type.FRUIT;
            case 3:
                return Type.TBI;
            default:
                return Type.OTHER;
        }
    }
}
