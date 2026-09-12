package goodthingmap.android.prada.lab.goodthingmap.component

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import goodthingmap.android.prada.lab.goodthingmap.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListDialogFragment : DialogFragment(), View.OnClickListener {
    companion object {
        @JvmStatic
        fun newInstance(): ListDialogFragment = ListDialogFragment()
    }

    private var cameraOutputUri: Uri? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            cameraOutputUri?.let(::sendEmail)
            dismiss()
        }
    }

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let(::sendEmail)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_choose_image, container, false).also { view ->
        view.findViewById<View>(R.id.bn_from_camera).setOnClickListener(this)
        view.findViewById<View>(R.id.bn_from_gallery).setOnClickListener(this)
        view.findViewById<View>(R.id.bn_cancel).setOnClickListener(this)
        dialog?.setTitle("上傳照片")
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val pictures = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw IOException("Pictures directory is unavailable")
        val storageDir = File(pictures, "captures").apply {
            if (!exists() && !mkdirs()) throw IOException("Unable to create pictures directory")
        }
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) == null) return
        val file = createImageFile()
        cameraOutputUri = FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.fileprovider", file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraOutputUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cameraLauncher.launch(intent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.bn_cancel -> dismiss()
            R.id.bn_from_camera -> runCatching { dispatchTakePictureIntent() }
            R.id.bn_from_gallery -> imagePickerLauncher.launch(ImagePickerIntentFactory.create())
        }
    }

    private fun sendEmail(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("goodmaps2013@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "提供好事圖片")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, null))
    }
}
