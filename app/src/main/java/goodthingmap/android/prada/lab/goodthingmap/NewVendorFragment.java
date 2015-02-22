package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;

/**
 * Created by 123 on 9/7/2014.
 */
public class NewVendorFragment extends BaseServiceFragment {
    public static final String TAG = "NewVendorFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_vendor, container, false);


        return rootView;
    }
}
