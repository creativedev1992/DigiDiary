package com.digi.diary.cutomeView;

import android.content.Context;
import android.widget.TextView;

import com.digi.diary.R;


/**
 * Created by Anupama on 11/13/2016.
 */
public class CustomTextView extends TextView {

    public CustomTextView(Context context, int styleAttribute) {
        super(context, null, styleAttribute);
    }

    // You could also just apply your default style if none is given
    public CustomTextView(Context context) {
        super(context, null, R.style.MyTextViewStyle1);
    }
}
