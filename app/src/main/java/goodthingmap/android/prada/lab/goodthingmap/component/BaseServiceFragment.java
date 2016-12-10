package goodthingmap.android.prada.lab.goodthingmap.component;

import android.prada.lab.goodthingmap.network.GoodThingService;
import android.support.v4.app.Fragment;

import goodthingmap.android.prada.lab.goodthingmap.Consts;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by prada on 2014/7/4.
 */
public abstract class BaseServiceFragment extends Fragment {

    protected final GoodThingService mService;

    public BaseServiceFragment() {
        mService = new Retrofit.Builder()
                .baseUrl(Consts.AUTHORITY)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoodThingService.class);
    }
}
