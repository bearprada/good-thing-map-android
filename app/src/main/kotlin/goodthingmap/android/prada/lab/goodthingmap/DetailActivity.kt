package goodthingmap.android.prada.lab.goodthingmap

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.prada.lab.goodthingmap.model.CheckinResult
import android.prada.lab.goodthingmap.model.GoodThing
import android.prada.lab.goodthingmap.model.LikeResult
import android.prada.lab.goodthingmap.model.UserMessage
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.AlertDialogWrapper
import com.afollestad.materialdialogs.MaterialDialog
import com.amplitude.api.Amplitude
import com.facebook.FacebookSdk
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.flurry.android.FlurryAgent
import com.squareup.picasso.Picasso
import goodthingmap.android.prada.lab.goodthingmap.component.AlertDialogFragment
import goodthingmap.android.prada.lab.goodthingmap.component.ListDialogFragment
import goodthingmap.android.prada.lab.goodthingmap.util.LocationUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : BaseActivity(), View.OnClickListener {
    companion object {
        const val MAX_STORY_TEXT_LINES = 6
    }

    private lateinit var goodThing: GoodThing
    private lateinit var storyText: TextView
    private lateinit var likeButton: View
    private lateinit var commentList: LinearLayout
    private lateinit var likeButtonText: Button
    private lateinit var shareButtonText: Button
    private var location: Location? = null
    private var shareDialog: ShareDialog? = null
    private var storyLines = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.fragment_detail)
        goodThing = intent.getParcelableExtra(GoodThing.EXTRA_GOODTHING)
            ?: error("DetailActivity requires a GoodThing")
        location = intent.getParcelableExtra(GoodListActivity.EXTRA_LOCATION)

        FacebookSdk.sdkInitialize(this)
        shareDialog = ShareDialog(this)
        findViewById<TextView>(R.id.detail_distance).text = LocationUtil.calDistance(location, goodThing)
        findViewById<TextView>(R.id.detail_title).text = goodThing.title
        findViewById<TextView>(R.id.detail_memo).text = goodThing.memo
        storyText = findViewById(R.id.detail_story)
        storyText.text = goodThing.story
        storyText.setOnClickListener(this)

        setupImages(findViewById(R.id.detail_images), goodThing.images)
        Picasso.get().load(goodThing.detailImageUrl).into(findViewById<ImageView>(R.id.detail_cover_image))
        commentList = findViewById(R.id.detail_list_comments)
        refreshCommentList()
        likeButton = findViewById(R.id.btn_detail_like)
        likeButton.setOnClickListener(this)
        likeButtonText = findViewById(R.id.btn_detail_like_text)
        shareButtonText = findViewById(R.id.btn_detail_share_text)
        listOf(
            R.id.btn_detail_comment,
            R.id.btn_detail_map,
            R.id.btn_detail_new_image,
            R.id.btn_detail_report,
            R.id.btn_detail_share
        ).forEach { findViewById<View>(it).setOnClickListener(this) }
        setupLikeNum()
        setupCheckinNum()
    }

    override fun onStart() {
        super.onStart()
        FlurryAgent.logEvent("PageDetail", true)
    }

    override fun onStop() {
        FlurryAgent.endTimedEvent("PageDetail")
        super.onStop()
    }

    private fun getCommentView(index: Int, parent: ViewGroup, comment: UserMessage): View =
        LayoutInflater.from(this).inflate(R.layout.item_comment, parent, false).also { view ->
            view.findViewById<TextView>(R.id.list_seq_id).text = "#$index"
            view.findViewById<TextView>(R.id.list_comment).text = comment.message
            view.findViewById<TextView>(R.id.list_time).text = comment.time.toString()
        }

    private fun setupLikeNum() {
        lifecycleScope.launch {
            runCatching { withContext(Dispatchers.IO) { mService.requestLikeNum(goodThing.id) } }
                .onSuccess { result -> likeButtonText.text = getString(R.string.like) + "(${result.result})" }
        }
    }

    private fun setupCheckinNum() {
        lifecycleScope.launch {
            runCatching { withContext(Dispatchers.IO) { mService.requestCheckinNum(goodThing.id) } }
                .onSuccess { result -> shareButtonText.text = getString(R.string.share) + "(${result.result})" }
        }
    }

    private fun setupImages(container: ViewGroup, images: List<String>) {
        for (url in images) {
            val imageView = LayoutInflater.from(this)
                .inflate(R.layout.item_image, container, false) as ImageView
            Picasso.get().load(url)
                .placeholder(R.drawable.btn_new_image)
                .error(R.drawable.btn_new_image)
                .into(imageView)
            container.addView(imageView)
        }
        repeat((images.size until 5).count()) {
            ImageView(this).apply {
                setImageResource(R.drawable.btn_new_image)
                setOnClickListener { ListDialogFragment.newInstance().show(supportFragmentManager, "") }
                container.addView(this)
            }
        }
    }

    private fun getActivityInfo(packageName: String): ActivityInfo? {
        val sendIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
        return packageManager.queryIntentActivities(sendIntent, 0)
            .map(ResolveInfo::activityInfo)
            .firstOrNull { it.packageName.equals(packageName, ignoreCase = true) }
    }

    private fun shareToTarget(info: ActivityInfo, uri: String) {
        startActivity(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_to_target_share))
            putExtra(Intent.EXTRA_TEXT, uri)
            setClassName(info.packageName, info.name)
        })
    }

    private fun shareToAppNotFound(packageName: String) {
        val alert = AlertDialogFragment.newInstance(
            null,
            getString(R.string.share_to_target_error_message),
            getString(android.R.string.yes),
            DialogInterface.OnClickListener { _, _ ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            },
            getString(android.R.string.no),
            null
        )
        runCatching { alert.show(supportFragmentManager, "download_warning") }
    }

    private fun shareToFacebook() {
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val content = ShareLinkContent.Builder()
                .setContentTitle(goodThing.title)
                .setContentUrl(Uri.parse(googleMapUri()))
                .setImageUrl(Uri.parse(goodThing.imageUrl))
                .setContentDescription("${goodThing.memo.orEmpty()}   ${goodThing.story.orEmpty()}")
                .build()
            shareDialog?.show(content)
        } else {
            shareToAppNotFound("com.facebook.katana")
        }
    }

    private fun googleMapUri(): String =
        "https://maps.google.com/maps?q=${goodThing.latitude},${goodThing.longtitude}"

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_detail_report -> {
                FlurryAgent.logEvent("Event_Click_Detail_Report", false)
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.fromParts("mailto", "goodmaps2013@gmail.com", null)
                    putExtra(Intent.EXTRA_SUBJECT, R.string.subject_report)
                }
                startActivity(Intent.createChooser(intent, null))
            }
            R.id.btn_detail_new_image -> {
                FlurryAgent.logEvent("Event_Click_Detail_New_Image", false)
                ListDialogFragment.newInstance().show(supportFragmentManager, "")
            }
            R.id.btn_detail_comment -> showCommentDialog()
            R.id.btn_detail_like -> likeGoodThing()
            R.id.btn_detail_map -> showMapDialog()
            R.id.btn_detail_share -> {
                FlurryAgent.logEvent("Event_Click_Detail_Share", false)
                shareToFacebook()
            }
            R.id.detail_story -> expandStory()
        }
    }

    private fun showCommentDialog() {
        FlurryAgent.logEvent("Event_Click_Detail_Comment", false)
        MaterialDialog.Builder(this)
            .title(R.string.enter_comment)
            .positiveText(R.string.confirm)
            .negativeText(R.string.cancel)
            .input(null, null, false) { _, input ->
                val comment = input.toString()
                lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            mService.postComment(Amplitude.getDeviceId(), goodThing.id, comment)
                        }
                        Toast.makeText(this@DetailActivity, R.string.post_comment_successful, Toast.LENGTH_SHORT).show()
                        if (goodThing.message == null) goodThing.message = mutableListOf()
                        goodThing.message?.add(0, UserMessage.newInstance(comment))
                        refreshCommentList()
                    } catch (error: CancellationException) {
                        throw error
                    } catch (error: Throwable) {
                        Toast.makeText(this@DetailActivity, R.string.post_comment_fail, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .build()
            .show()
    }

    private fun likeGoodThing() {
        FlurryAgent.logEvent("Event_Click_Detail_Like", false)
        likeButton.isSelected = true
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    mService.likeGoodThing(Amplitude.getDeviceId(), goodThing.id)
                }
                Toast.makeText(this@DetailActivity, R.string.msg_like_successful, Toast.LENGTH_SHORT).show()
                likeButtonText.text = getString(R.string.like) + "(${result.result})"
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                Toast.makeText(this@DetailActivity, "${getString(R.string.add_like_fail)}:${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMapDialog() {
        FlurryAgent.logEvent("Event_Click_Detail_Map", false)
        val message = if (goodThing.isBigIssue) R.string.warning_tbi_navigation else R.string.warning_navigation
        val dialog: Dialog = AlertDialogWrapper.Builder(this)
            .setTitle(R.string.warning_navigation_title)
            .setMessage(message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.confirm) { _, _ ->
                val url = location?.let {
                    "http://maps.google.com/maps?saddr=${it.latitude},${it.longitude}&daddr=${goodThing.latitude},${goodThing.longtitude}"
                } ?: googleMapUri()
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            .create()
        dialog.show()
    }

    private fun expandStory() {
        FlurryAgent.logEvent("Event_Click_Detail_Story", false)
        val currentLines = storyText.lineCount
        storyLines = maxOf(storyLines, currentLines)
        if (storyLines <= MAX_STORY_TEXT_LINES) return
        val params = storyText.layoutParams as LinearLayout.LayoutParams
        val shownLines = if (currentLines > MAX_STORY_TEXT_LINES) MAX_STORY_TEXT_LINES else storyLines + 1
        storyText.maxLines = shownLines
        params.height = storyText.lineHeight * (shownLines + 1)
        params.setMargins(params.leftMargin, params.topMargin, 0, 0)
        storyText.layoutParams = params
    }

    private fun refreshCommentList() {
        commentList.removeAllViews()
        goodThing.message.orEmpty().forEachIndexed { index, message ->
            commentList.addView(getCommentView(index + 1, commentList, message))
        }
    }
}
