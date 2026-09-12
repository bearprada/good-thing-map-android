package goodthingmap.android.prada.lab.goodthingmap;

import android.content.Intent;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Bundle;
import android.prada.lab.goodthingmap.network.GoodThingService;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import goodthingmap.android.prada.lab.goodthingmap.util.LogEventUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by prada on 2014/7/5.
 */
public abstract class BaseActivity extends AppCompatActivity implements LifecycleOwner {
    public static final String AUTHORITY = "https://goodthing.tw:8080/";
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    protected GoodThingService mService;

    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        LogEventUtils.init(this);

        mService = new Retrofit.Builder()
            .baseUrl(AUTHORITY)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GoodThingService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        LogEventUtils.startSession(this);
    }

    @Override
    protected void onStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        super.onStop();
        LogEventUtils.stopSession(this);
    }

    @Override
    protected void onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_my_fravor:
                Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_LONG).show();
                LogEventUtils.sendEvent("ClickFavor");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
