package com.trolik77.afusion.afusiontest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.trolik77.afusion.afusiontest.dto.ImageData;
import java.lang.ref.WeakReference;

public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private String url;
    private ImageData imageData;
    private final WeakReference<ImageView> imageViewReference;

    public BitmapDownloaderTask(ImageView imageView, ImageData imageData) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.imageData = imageData;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        url = params[0];
        Log.d(BitmapDownloaderTask.class.getSimpleName(), "url: " + url);

        int w, h;
        if (imageData.ratio > 1) {
            w = (int)(imageData.width / imageData.ratio);
            h = (int)(imageData.height / imageData.ratio);
        } else {
            w = imageData.width;
            h = imageData.height;
        }

        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(params[0]), w, h);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }
        if (bitmap == null) {
            return;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                if (!url.equals(imageView.getTag())) {
                    Log.d(BitmapDownloaderTask.class.getName(), "imageview has gone");
                    return;
                }
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
