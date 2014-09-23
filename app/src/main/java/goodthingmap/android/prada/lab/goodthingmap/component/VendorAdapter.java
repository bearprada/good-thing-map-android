package goodthingmap.android.prada.lab.goodthingmap.component;

import android.content.Context;
import android.location.Location;
import android.prada.lab.goodthingmap.model.VendorData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import goodthingmap.android.prada.lab.goodthingmap.R;

/**
 * Created by 123 on 9/1/2014.
 */
public class VendorAdapter extends ArrayAdapter<VendorData> {
    private final LayoutInflater mInflater;
    private Location mCurrentLocation;
    private List<VendorData> mOriginalValues;
    private List<VendorData> mFilterValues;
    private ItemFilter mFilter;

    public VendorAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
        mOriginalValues = new ArrayList<VendorData>();
        mFilterValues = mOriginalValues;
    }

    @Override
    public void add(VendorData item) {
        System.out.println("add!!!!!!!!!!");
        super.add(item);
        mOriginalValues.add(item);
    }

    @Override
    public void addAll(Collection<? extends VendorData> collection) {
        super.addAll(collection);
        mOriginalValues.addAll(collection);

    }

    @Override
    public void addAll(VendorData... items) {
        super.addAll(items);
        mOriginalValues.addAll(Arrays.asList(items));
    }

    @Override
    public void remove(VendorData item) {
        super.remove(item);
        mOriginalValues.remove(item);
    }

    @Override
    public void clear() {
        System.out.println("clear!!!!!!!!!!");
        super.clear();
        mOriginalValues.clear();
        mFilter.clear();
    }

    @Override
    public void insert(VendorData item, int index) {
        super.insert(item, index);
        mOriginalValues.add(index, item);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }

        return mFilter;
    }

    protected void setFilteringItems(List<VendorData> list) {
        super.clear();
        super.addAll(list);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.item_vendor, null);
        } else {
            view = convertView;
        }

        VendorData vendorData = getItem(i);
        ((TextView) ViewHolder.get(view,R.id.item_vendor_title)).setText(vendorData.getTitle());
        ((TextView) ViewHolder.get(view,R.id.item_vendor_address)).setText(vendorData.getAddress());
        ((TextView) ViewHolder.get(view,R.id.item_vendor_business_hour)).setText(vendorData.getBusinessHour());
        ((TextView) ViewHolder.get(view,R.id.item_vendor_brief_description)).setText(vendorData.getBriefDescription());
        ((TextView) ViewHolder.get(view,R.id.item_vendor_distance)).setText(Utility.calDistance(mCurrentLocation, vendorData.getLocation()));

        ImageView iv = ((ImageView) view.findViewById(R.id.item_vendor_icon));
        Picasso.with(getContext()).load(vendorData.getIconUrl()).into(iv, new Callback.EmptyCallback() {
            @Override public void onSuccess() {}
        });
        return view;
    }


    public void setLocation(Location location) {
        mCurrentLocation = location;
    }


    public class ItemFilter extends Filter {
        private Map<VendorData, Map<Integer, String>> valueMaps;
        private Set<VendorData.Type> typeFilterSet;

        public ItemFilter() {

            valueMaps = new HashMap<VendorData, Map<Integer, String>>();
        }

        public void clear() {
            valueMaps.clear();
        }

        public void filter(Set<VendorData.Type> typeSet) {
            this.typeFilterSet = typeSet;
            publishResults(mFilterValues);
        }

        private void publishResults(List<VendorData> list) {
            if (typeFilterSet == null || typeFilterSet.isEmpty()) {
                setFilteringItems(new ArrayList<VendorData>());
            } else {
                ArrayList<VendorData> results = new ArrayList<VendorData>();
                for (VendorData vendorData : list) {
                    if (typeFilterSet.contains(vendorData.getType())) {
                        results.add(vendorData);
                    }
                }
                setFilteringItems(results);
            }

            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                System.out.println("mOriginalValues.size(): " + mOriginalValues.size());
                ArrayList<VendorData> list = new ArrayList<VendorData>(mOriginalValues);
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                final ArrayList<VendorData> values = new ArrayList<VendorData>(mOriginalValues);
                final int count = values.size();

                final ArrayList<VendorData> newValues = new ArrayList<VendorData>(count);

                for (int i = 0; i < count; i++) {
                    final VendorData vendorData = values.get(i);
                    Map<Integer, String> valueMap = valueMaps.get(vendorData);

                    if (valueMap == null) {
                        valueMap = new HashMap<Integer, String>();
                        valueMap.put(R.id.item_vendor_title, vendorData.getTitle());
                        valueMap.put(R.id.item_vendor_title, vendorData.getTitle());
                        valueMap.put(R.id.item_vendor_address, vendorData.getAddress());
                        valueMap.put(R.id.item_vendor_business_hour, vendorData.getBusinessHour());
                        valueMap.put(R.id.item_vendor_brief_description, vendorData.getBriefDescription());
                    }

                    for (Map.Entry<Integer, String> entry : valueMap.entrySet()) {
                        String valueText = entry.getValue().toLowerCase();

                        if (valueText.contains(prefixString)) {
                            newValues.add(vendorData);
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println("publishResults: " + results.count);
            mFilterValues = (List<VendorData>)results.values;
            publishResults(mFilterValues);
        }
    }


}
