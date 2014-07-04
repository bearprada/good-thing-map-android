package goodthingmap.android.prada.lab.goodthingmap.component;

import android.content.Context;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import goodthingmap.android.prada.lab.goodthingmap.R;

/**
 * Created by prada on 2014/7/4.
 */
public class GoodThingAdapter extends ArrayAdapter<GoodThing> {

    private final LayoutInflater mInflater;

    public GoodThingAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.item_good_thing, null);
        } else {
            view = convertView;
        }
        GoodThing thing = getItem(i);
        ((TextView)view.findViewById(R.id.list_address)).setText(thing.getAddress());
        ((TextView)view.findViewById(R.id.list_title)).setText(thing.getTitle());
        // ((TextView)view.findViewById(R.id.list_distance)).setText(thing.getStory()); // FIXME
        // ((ImageView)view.findViewById(R.id.list_flavor)); // FIXME

        ImageView iv = ((ImageView) view.findViewById(R.id.list_image_view));
        Picasso.with(getContext()).load(thing.getListImageUrl()).into(iv, new Callback.EmptyCallback() {
            @Override public void onSuccess() {}
        });
        return view;
    }
}
