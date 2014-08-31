package goodthingmap.android.prada.lab.goodthingmap.component;

import android.prada.lab.goodthingmap.network.GoodThingService;
import android.support.v4.app.Fragment;

import goodthingmap.android.prada.lab.goodthingmap.Consts;
import retrofit.RestAdapter;

/**
 * Created by prada on 2014/7/4.
 */
public abstract class BaseServiceFragment extends Fragment {

    protected final GoodThingService mService;

    public BaseServiceFragment() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Consts.AUTHORITY)
                .build();
        mService = restAdapter.create(GoodThingService.class);
    }
}
