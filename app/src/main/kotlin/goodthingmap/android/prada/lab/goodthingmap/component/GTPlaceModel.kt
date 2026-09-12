package goodthingmap.android.prada.lab.goodthingmap.component

import android.prada.lab.goodthingmap.model.GoodThing
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.squareup.picasso.Picasso
import goodthingmap.android.prada.lab.goodthingmap.R
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash

@EpoxyModelClass(layout = R.layout.item_good_thing)
abstract class GTPlaceModel : EpoxyModelWithHolder<GTPlaceModel.GTViewHolder>() {
    @EpoxyAttribute
    var title: String = ""

    @EpoxyAttribute
    var address: String = ""

    @EpoxyAttribute
    var distance: String = ""

    @EpoxyAttribute
    var imageUrl: String = ""

    @EpoxyAttribute(DoNotHash)
    var clickListener: GTClickListener? = null

    override fun bind(holder: GTViewHolder) {
        holder.addressView.text = address
        holder.titleView.text = title
        holder.distanceView.text = distance
        Picasso.with(holder.imageView.context).load(imageUrl).into(holder.imageView)
        holder.rootView.setOnClickListener { view -> clickListener?.onPlaceClick(view, id()) }
    }

    class GTViewHolder : EpoxyHolder() {
        lateinit var imageView: ImageView
        lateinit var addressView: TextView
        lateinit var titleView: TextView
        lateinit var distanceView: TextView
        lateinit var rootView: View

        override fun bindView(view: View) {
            rootView = view
            addressView = view.findViewById(R.id.list_address)
            titleView = view.findViewById(R.id.list_title)
            distanceView = view.findViewById(R.id.list_distance)
            imageView = view.findViewById(R.id.list_image_view)
        }
    }

    interface GTClickListener {
        fun onPlaceClick(view: View, placeId: Long)
        fun onFavorClick(view: View, goodthing: GoodThing)
    }
}
