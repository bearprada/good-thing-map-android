package android.prada.lab.goodthingmap.model;

import android.location.Location;
import android.prada.lab.goodthingmap.network.GoodThingService;

import io.reactivex.Observable;

/**
 * Created by prada on 16/08/2017.
 */

public class GoodThingRepository {
    private final GoodThingService service;

    public GoodThingRepository(GoodThingService service) {
        this.service = service;
    }

    public Observable<GoodThingData> topStory() {
        return service.getTopStory();
    }

    public Observable<GoodThingsData> listPlaces(GoodThingType type, Location location) {
        if (location == null && type == GoodThingType.NEAR) {
            return service.listStory();
        }
        if (location == null) {
            return service.listStory(type.getTypeId());
        }
        if (type == GoodThingType.NEAR) {
            return service.listStory(location.getLatitude(), location.getLongitude());
        }
        return service.listStory(type.getTypeId(), location.getLatitude(), location.getLongitude());
    }
}
