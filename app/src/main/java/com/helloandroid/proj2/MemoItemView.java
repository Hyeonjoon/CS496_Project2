package com.helloandroid.proj2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemoItemView extends LinearLayout {

    CheckBox checkBox;
    String check;

    public MemoItemView(Context context) {
        super(context);

        init(context);
    }

    public MemoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memo_item, this, true);

        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }

    public void setMemo(String str){
        checkBox.setText(str);
    }

    public String chCheck () {
        if (checkBox.isChecked()) {
            check = "1";
        } else {
            check = "0";
        }
        return check;
    }

}
