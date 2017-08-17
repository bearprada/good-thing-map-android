package goodthingmap.android.prada.lab.goodthingmap.component;

import android.location.Location;
import android.prada.lab.goodthingmap.model.GoodThing;

import com.airbnb.epoxy.TypedEpoxyController;

import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.util.LocationUtil;

/**
 * Created by prada on 16/08/2017.
 */

public class GTController extends TypedEpoxyController<List<GoodThing>> {
    private final GTPlaceModel.GTClickListener mItemClickListener;
    private Location mCurrentLocation;

    public GTController(GTPlaceModel.GTClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    protected void buildModels(List<GoodThing> things) {
        for (GoodThing thing : things) {
            new GTPlaceModel_()
                .id(thing.getId())
                .address(thing.getAddress())
                .distance(LocationUtil.calDistance(mCurrentLocation, thing))
                .imageUrl(thing.getListImageUrl())
                .clickListener(mItemClickListener)
                .addTo(this);
        }
    }

    public void setLocation(Location location) {
        mCurrentLocation = location;
        requestModelBuild();
    }
}
