package com.helloandroid.proj2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageItemView extends LinearLayout {
    ImageView imageView;

    public ImageItemView(Context context) {
        super(context);

        init(context);
    }

    public ImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init (Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_item, this, true);

        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setImage(int resId) {
        imageView.setImageResource(resId);
    }

    public void setbt(Bitmap bt) {
        imageView.setImageBitmap(bt);
    }
}

