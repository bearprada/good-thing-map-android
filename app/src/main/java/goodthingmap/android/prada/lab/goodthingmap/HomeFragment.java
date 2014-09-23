package goodthingmap.android.prada.lab.goodthingmap;


import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.prada.lab.goodthingmap.model.UserAddedVendor;
import android.prada.lab.goodthingmap.model.UserFavorite;
import android.prada.lab.goodthingmap.model.VendorData;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;
import goodthingmap.android.prada.lab.goodthingmap.component.VendorAdapter;

/**
 * Created by 123 on 8/31/2014.
 */
public class HomeFragment extends BaseServiceFragment implements LocationListener, View.OnClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    public static final String EXTRA_LOCATION = "extra_location";
    public static final String EXTRA_VENDOR_ID = "extra_vendor_id";

    public static final String EXTRA_FRAGMENT_TYPE = "extra_fragment_type";
    public static final int FRAGMENT_DEFAULT = 0;
    public static final int FRAGMENT_USER_FAVORITE = 1;
    public static final int FRAGMENT_USER_ADDED_VENDOR = 2;

    private int mFragmentType;


    private VendorAdapter mAdapter;
    private VendorAdapter.ItemFilter mFilter;
    private Location mCurrentLocation;
    private LocationManager mLocationManager;
    private String mProvider;
    private List<VendorData> mVendors;

    private Set<VendorData.Type> mTypeFilterSet;
    private Set<Integer> mBtnEnableTypeSet;

    private boolean mPerformSearch = false;



    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setHasOptionsMenu(true);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, false);
        mCurrentLocation = mLocationManager.getLastKnownLocation(mProvider);

        Bundle bundle = getArguments();
        if(bundle != null) {
            mFragmentType = bundle.getInt(EXTRA_FRAGMENT_TYPE);
        } else {
            mFragmentType = FRAGMENT_DEFAULT;
        }

        setTypeFilter();
    }

    private void setTypeFilter() {
        mTypeFilterSet = new HashSet<VendorData.Type>();
        mBtnEnableTypeSet = new HashSet<Integer>();

        mTypeFilterSet.add(VendorData.Type.MAIN);
        mBtnEnableTypeSet.add(R.id.btn_home_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(mProvider, 10000, 0, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mAdapter = new VendorAdapter(getActivity());
        mAdapter.setLocation(mCurrentLocation);
        mFilter = (VendorAdapter.ItemFilter)mAdapter.getFilter();
        ListView lv = (ListView)rootView.findViewById(R.id.list_home);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(this);
        lv.setTextFilterEnabled(true);

        Button btnMain = (Button)rootView.findViewById(R.id.btn_home_main);
        Button btnSnack = (Button)rootView.findViewById(R.id.btn_home_snack);
        Button btnFruit = (Button)rootView.findViewById(R.id.btn_home_fruit);
        Button btnTbi = (Button)rootView.findViewById(R.id.btn_home_tbi);
        Button btnOther = (Button)rootView.findViewById(R.id.btn_home_other);

        btnMain.setTag(VendorData.Type.MAIN);
        btnSnack.setTag(VendorData.Type.SNACK);
        btnFruit.setTag(VendorData.Type.FRUIT);
        btnTbi.setTag(VendorData.Type.TBI);
        btnOther.setTag(VendorData.Type.OTHER);

        btnMain.setOnClickListener(this);
        btnSnack.setOnClickListener(this);
        btnFruit.setOnClickListener(this);
        btnTbi.setOnClickListener(this);
        btnOther.setOnClickListener(this);

        switch(mFragmentType) {
            case FRAGMENT_USER_FAVORITE:
                updateUserFavorite();
                break;
            case FRAGMENT_USER_ADDED_VENDOR:
                updateUserAddedVendors();
                break;
            default:
                updateVendors();
        }


        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }


    protected void updateUserAddedVendors() {
        ParseQuery<UserAddedVendor> query = ParseQuery.getQuery(UserAddedVendor.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo(UserAddedVendor.USER_ID, mUser.getObjectId());
        query.whereEqualTo("cancel", false);
        query.findInBackground(new FindCallback<UserAddedVendor>() {
            @Override
            public void done(List<UserAddedVendor> userAddedVendors, ParseException e) {
                if(e == null) {
                    List<ParseQuery<VendorData>> queries = new ArrayList<ParseQuery<VendorData>>();

                    for(UserAddedVendor userAddedVendor : userAddedVendors) {
                        ParseQuery<VendorData> query = ParseQuery.getQuery(VendorData.class);
                        query.whereEqualTo(VendorData.ID, userAddedVendor.getVendorId());
                        query.whereEqualTo("public", true);
                        queries.add(query);
                    }

                    updateVendors(queries);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void updateUserFavorite() {
        ParseQuery<UserFavorite> query = ParseQuery.getQuery(UserFavorite.class);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));
        query.whereEqualTo(UserFavorite.USER_ID, mUser.getObjectId());
        query.whereEqualTo("cancel", false);
        query.findInBackground(new FindCallback<UserFavorite>() {
            @Override
            public void done(List<UserFavorite> userFavorites, ParseException e) {
                if(e == null) {
                    List<ParseQuery<VendorData>> queries = new ArrayList<ParseQuery<VendorData>>();
                    
                    for(UserFavorite userFavorite : userFavorites) {
                        ParseQuery<VendorData> query = ParseQuery.getQuery(VendorData.class);
                        query.whereEqualTo(VendorData.ID, userFavorite.getVendorId());
                        query.whereEqualTo("public", true);
                        queries.add(query);
                    }

                    updateVendors(queries);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    protected void updateVendors(List<ParseQuery<VendorData>> queries) {
        if(queries == null || queries.isEmpty()) {
            return;
        }

        ParseQuery<VendorData> mainQuery = ParseQuery.or(queries);
        mainQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        mainQuery.setMaxCacheAge(TimeUnit.HOURS.toMillis(1));
        mainQuery.findInBackground(updateVendorCallback);
    }

    protected void updateVendors() {
        ParseQuery<VendorData> query = ParseQuery.getQuery(VendorData.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.HOURS.toMillis(1));
        query.whereEqualTo("public", true);
        query.findInBackground(updateVendorCallback);
    }

    protected void updateListView(List<VendorData> list) {
        mPerformSearch = false;

        if(mCurrentLocation != null) {
            Collections.sort(list, new Comparator<VendorData>() {
                @Override
                public int compare(VendorData vendorData, VendorData vendorData2) {
                    float dist = mCurrentLocation.distanceTo(vendorData.getLocation());
                    float dist2 = mCurrentLocation.distanceTo(vendorData2.getLocation());
                    return (int) (dist - dist2);
                }
            });
        }

        for (VendorData vendorData : list) {
            mAdapter.add(vendorData);
        }

        mAdapter.notifyDataSetChanged();

        mPerformSearch = true;
    }


    private void updateTypeFilter(View view) {
        int id = view.getId();
        if(mBtnEnableTypeSet.contains(id)) {
            view.setBackgroundColor(Color.TRANSPARENT);
            mBtnEnableTypeSet.remove(id);
            mTypeFilterSet.remove(view.getTag());
        } else {
            view.setBackgroundColor(Color.RED);
            mBtnEnableTypeSet.add(id);
            mTypeFilterSet.add((VendorData.Type)view.getTag());
        }

        System.out.println("mTypeFilterSet: " + mTypeFilterSet);
        mFilter.filter(mTypeFilterSet);
    }

    @Override
    public void onClick(View view) {
        
        switch(view.getId()) {
            case R.id.btn_home_main:
                updateTypeFilter(view);
                break;
            case R.id.btn_home_snack:
                updateTypeFilter(view);
                break;
            case R.id.btn_home_fruit:
                updateTypeFilter(view);
                break;
            case R.id.btn_home_tbi:
                updateTypeFilter(view);
                break;
            case R.id.btn_home_other:
                updateTypeFilter(view);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String vendorId = mAdapter.getItem(i).getId();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_VENDOR_ID, vendorId);
        bundle.putParcelable(EXTRA_LOCATION, mCurrentLocation);
        BaseServiceFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);

        this.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, detailFragment)
                .addToBackStack(detailFragment.getTag())
                .commit();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private boolean canPerformSearch() {
        return mPerformSearch;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        if (!canPerformSearch()) {
            return false;
        }

        System.out.println("newText: " + newText + ", mVendors.size(): " + mVendors.size());
        mFilter.filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!canPerformSearch()) {
            return false;
        }

        System.out.println("query: " + query);
        mFilter.filter(query);
        return true;
    }
    
    private FindCallback<VendorData> updateVendorCallback = new FindCallback<VendorData>() {

        @Override
        public void done(List<VendorData> vendors, ParseException e) {
            if(e == null) {
                mVendors = vendors;
                updateListView(mVendors);
            } else {
                e.printStackTrace();
            }
        }
    };
}
