package com.helloandroid.proj2;

import android.graphics.Bitmap;

public class ImageItem {

    //int resId;
    Bitmap bt;


    public ImageItem(Bitmap bt){
        this.bt = bt;
        //this.resId = resId;
    }
     public Bitmap getBt(){
        return bt;
     }
     public void setBt(Bitmap bt){
        this.bt = bt;
     }

    /*
    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
    */

}
