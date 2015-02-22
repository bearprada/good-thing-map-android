package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import goodthingmap.android.prada.lab.goodthingmap.R;
import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;

/**
 * Created by 123 on 9/5/2014.
 */
public class AboutFragment extends BaseServiceFragment {
    public static final String TAG = "AboutFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        rootView.findViewById(R.id.btn_about_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.facebook.com/GoodthingMap";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return rootView;
    }
}
