package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import goodthingmap.android.prada.lab.goodthingmap.component.BaseServiceFragment;


public class HomeActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new HomeFragment())
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        BaseServiceFragment fragment = null;
        Bundle bundle = null;

        System.out.println("Tab: " + position);

        switch(position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new NewVendorFragment();
                break;
            case 2:
                fragment = new HomeFragment();
                bundle = new Bundle();
                bundle.putInt(HomeFragment.EXTRA_FRAGMENT_TYPE,
                        HomeFragment.FRAGMENT_USER_FAVORITE);
                fragment.setArguments(bundle);
                break;
            case 3:
                fragment = new HomeFragment();
                bundle = new Bundle();
                bundle.putInt(HomeFragment.EXTRA_FRAGMENT_TYPE,
                        HomeFragment.FRAGMENT_USER_ADDED_VENDOR);
                fragment.setArguments(bundle);
                break;
            case 4:
                fragment = new AboutFragment();
                break;
            case 5:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.fromParts("mailto", "goodmaps2013@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.subject_report);
                Intent mailer = Intent.createChooser(intent, null);
                startActivity(mailer);
                return;
            default:
                return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.getTag())
                .commit();
    }
}
