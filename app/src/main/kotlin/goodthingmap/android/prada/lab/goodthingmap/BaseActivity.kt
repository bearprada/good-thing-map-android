package goodthingmap.android.prada.lab.goodthingmap

import android.content.Intent
import android.os.Bundle
import android.prada.lab.goodthingmap.network.GoodThingService
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import goodthingmap.android.prada.lab.goodthingmap.util.LogEventUtils

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val AUTHORITY = "https://goodthing.tw:8080/"
    }

    protected lateinit var mService: GoodThingService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogEventUtils.init(this)
        mService = Retrofit.Builder()
            .baseUrl(AUTHORITY)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GoodThingService::class.java)
    }

    override fun onStart() {
        super.onStart()
        LogEventUtils.startSession(this)
    }

    override fun onStop() {
        LogEventUtils.stopSession(this)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        R.id.action_about -> {
            startActivity(Intent(this, AboutActivity::class.java))
            true
        }
        R.id.action_my_fravor -> {
            Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_LONG).show()
            LogEventUtils.sendEvent("ClickFavor")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
