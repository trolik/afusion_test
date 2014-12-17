package com.trolik77.afusion.afusiontest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.trolik77.afusion.afusiontest.dto.ImageData;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "CustomAdapter";

    private List<ImageData> mDataSet;
    private Point size = new Point();
    private int margin;

    public CustomAdapter(Activity context, List<ImageData> dataSet) {
        mDataSet = dataSet;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        display.getSize(size);

        margin = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
    }

    public void addItems(ImageData item) {
        mDataSet.add(item);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        ImageData imageData = mDataSet.get(position);

        viewHolder.getTextView().setText(imageData.name + "  [" + imageData.width + "x" + imageData.height + "]");

        imageData.ratio = (float)imageData.width / ((float)size.x - margin * 2);

        ViewGroup.LayoutParams params = viewHolder.getRootView().getLayoutParams();
        params.height = (int)(imageData.height / imageData.ratio);

        if (!imageData.path.equals(viewHolder.getImageView().getTag())) {
            Log.d(TAG, "Element " + position + " set. Name: " + imageData.name);

            viewHolder.getImageView().setTag(imageData.path);
            viewHolder.getImageView().setImageBitmap(null);

            BitmapDownloaderTask task = new BitmapDownloaderTask(viewHolder.getImageView(), imageData);
            task.execute(imageData.path);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
