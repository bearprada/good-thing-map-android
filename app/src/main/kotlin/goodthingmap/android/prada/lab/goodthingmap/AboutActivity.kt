package goodthingmap.android.prada.lab.goodthingmap

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.flurry.android.FlurryAgent

class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        findViewById<View>(R.id.btn_about_link).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/GoodthingMap")))
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.about, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        FlurryAgent.logEvent("PageAbout", true)
    }

    override fun onStop() {
        FlurryAgent.endTimedEvent("PageAbout")
        super.onStop()
    }
}
