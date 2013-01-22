package com.googolmo.utils.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * User: googolmo
 * Date: 12-9-23
 * Time: 下午12:39
 */
public class IntentUtils {

    private static final String TAG = IntentUtils.class.getName();


    public static Intent getPhotoCropIntent(File f, int width) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(f), "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", width);
        intent.putExtra("return-data", true);
        return intent;
    }

    public static Intent getPhotoPickIntent() {
        Uri uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        return new Intent(Intent.ACTION_PICK, uri);
    }

    public static Intent getPhotoPickIntent(File f, int width) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", width);
        // intent.putExtra("crop", "false");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        // intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name());
        // intent.putExtra("return-data", false);
        intent.putExtra("return-data", true);
        intent.putExtra("crop", "true");
        return intent;
    }

    public static Intent getPhotoPickIntent(int width) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", width);
        intent.putExtra("return-data", true);
        // intent.putExtra("output", Uri.fromFile(f));
        // intent.putExtra("outputFormat", "JEPG");
        return intent;
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }
}
