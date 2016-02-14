package com.projectzulu.randomize;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by gianmarco on 02/02/16.
 */
public class Utility {

    public static int getScreenHeightPixels(Context context) {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
