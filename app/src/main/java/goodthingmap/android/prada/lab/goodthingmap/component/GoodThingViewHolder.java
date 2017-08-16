package goodthingmap.android.prada.lab.goodthingmap.component;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import goodthingmap.android.prada.lab.goodthingmap.R;

/**
 * Created by prada on 2014/11/15.
 */
public class GoodThingViewHolder extends RecyclerView.ViewHolder {

    public final ImageView imageView;
    public final TextView addressView;
    public final TextView titleView;
    public final TextView distanceView;

    public GoodThingViewHolder(View view) {
        super(view);
        addressView = view.findViewById(R.id.list_address);
        titleView = view.findViewById(R.id.list_title);
        distanceView = view.findViewById(R.id.list_distance);
        // ((ImageView)view.findViewById(R.id.list_flavor)); // FIXME

        imageView = view.findViewById(R.id.list_image_view);
    }
}