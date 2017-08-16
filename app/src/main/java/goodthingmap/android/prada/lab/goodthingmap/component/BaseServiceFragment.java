package goodthingmap.android.prada.lab.goodthingmap.component;

import android.prada.lab.goodthingmap.network.GoodThingService;
import android.support.v4.app.Fragment;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by prada on 2014/7/4.
 */
public abstract class BaseServiceFragment extends Fragment {
    public static final String AUTHORITY = "http://goodthing.tw:8080/";

    protected final GoodThingService mService;

    public BaseServiceFragment() {
        mService = new Retrofit.Builder()
                .baseUrl(AUTHORITY)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoodThingService.class);
    }
}
