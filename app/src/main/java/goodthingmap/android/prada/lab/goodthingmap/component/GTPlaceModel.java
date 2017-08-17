package goodthingmap.android.prada.lab.goodthingmap.component;

import android.prada.lab.goodthingmap.model.GoodThing;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.squareup.picasso.Picasso;

import goodthingmap.android.prada.lab.goodthingmap.R;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * Created by prada on 16/08/2017.
 */

@EpoxyModelClass(layout = R.layout.item_good_thing)
public abstract class GTPlaceModel extends EpoxyModelWithHolder<GTPlaceModel.GTViewHolder> {
    @EpoxyAttribute String title;
    @EpoxyAttribute String address;
    @EpoxyAttribute String distance;
    @EpoxyAttribute String imageUrl;
    @EpoxyAttribute(DoNotHash)
    GTClickListener clickListener;

    @Override
    public void bind(GTViewHolder holder) {
        holder.addressView.setText(address);
        holder.titleView.setText(title);
        holder.distanceView.setText(distance);
        Picasso.with(holder.imageView.getContext()).load(imageUrl).into(holder.imageView);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onPlaceClick(view, id());
            }
        });
    }

    public static class GTViewHolder extends EpoxyHolder {

        ImageView imageView;
        TextView addressView;
        TextView titleView;
        TextView distanceView;
        View rootView;

        @Override
        protected void bindView(View view) {
            rootView = view;
            addressView = view.findViewById(R.id.list_address);
            titleView = view.findViewById(R.id.list_title);
            distanceView = view.findViewById(R.id.list_distance);
            imageView = view.findViewById(R.id.list_image_view);
        }
    }

    public interface GTClickListener {
        void onPlaceClick(View view, long placeId);
        void onFavorClick(View view, GoodThing goodthing);
    }
}
