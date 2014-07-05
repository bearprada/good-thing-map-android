package goodthingmap.android.prada.lab.goodthingmap.component;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import goodthingmap.android.prada.lab.goodthingmap.R;

/**
 * Created by prada on 2014/7/5.
 */
public class ListDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICKER_SELECT = 2;
    private Uri mCameraOutputFile;

    public static ListDialogFragment newInstance() {
        return new ListDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_choose_image, container, false);
        v.findViewById(R.id.bn_from_camera).setOnClickListener(this);
        v.findViewById(R.id.bn_from_gallery).setOnClickListener(this);
        v.findViewById(R.id.bn_cancel).setOnClickListener(this);

        getDialog().setTitle("上傳照片");
        return v;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            mCameraOutputFile = Uri.fromFile(createImageFile());
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mCameraOutputFile);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.bn_cancel:
                dismiss();
                break;
            case R.id.bn_from_camera:
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bn_from_gallery:
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, IMAGE_PICKER_SELECT);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch(requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                sendEmail(mCameraOutputFile);
                dismiss();
                break;
            case IMAGE_PICKER_SELECT:
                sendEmail(data.getData());
                dismiss();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void sendEmail(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.fromParts("mailto", "goodmaps2013@gmail.com", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "提供好事圖片");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        Intent mailer = Intent.createChooser(intent, null);
        startActivity(mailer);
    }

}
