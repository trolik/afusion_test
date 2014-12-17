package com.trolik77.afusion.afusiontest;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {

    private final View rootView;
    private final TextView textView;
    private final ImageView imageView;

    public ViewHolder(View v) {
        super(v);

        rootView = v;
        textView = (TextView) v.findViewById(R.id.textView);
        imageView = (ImageView) v.findViewById(R.id.imageView);
    }

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public View getRootView() {
        return rootView;
    }
}
