package goodthingmap.android.prada.lab.goodthingmap.component;

import android.content.Context;
import android.location.Location;
import android.prada.lab.goodthingmap.model.GoodThing;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import goodthingmap.android.prada.lab.goodthingmap.R;

/**
 * Created by prada on 2014/7/4.
 */
public class GoodThingAdapter extends RecyclerView.Adapter<GoodThingViewHolder> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private Location mCurrentLocation;
    private final List<GoodThing> mGoodThings = new ArrayList<GoodThing>();
    private final GoodThingItemListener mListener;

    public GoodThingAdapter(Context context, GoodThingItemListener listener) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mListener = listener;
    }

    public void setLocation(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public GoodThingViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_good_thing, viewGroup, false);
        GoodThingViewHolder vh = new GoodThingViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(GoodThingViewHolder vh, int i) {
        final GoodThing thing = getItem(i);
        vh.addressView.setText(thing.getAddress());
        vh.titleView.setText(thing.getTitle());
        vh.distanceView.setText(Utility.calDistance(mCurrentLocation, thing));

        Picasso.with(mContext).load(thing.getListImageUrl()).into(vh.imageView, new Callback.EmptyCallback() {
            @Override public void onSuccess() {}
        });

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view, thing);
                }
            }
        });
    }

    private GoodThing getItem(int position) {
        return mGoodThings.get(position);
    }

    @Override
    public int getItemCount() {
        return mGoodThings.size();
    }

    public void add(GoodThing thing) {
        mGoodThings.add(thing);
    }

    public void addAll(List<GoodThing> things) {
        mGoodThings.addAll(things);
    }

    public interface GoodThingItemListener {
        void onItemClick(View view, GoodThing goodthing);
        void onFavorClick(View view, GoodThing goodthing);
    }
}
