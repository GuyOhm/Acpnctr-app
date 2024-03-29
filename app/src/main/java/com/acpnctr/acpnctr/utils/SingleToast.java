package com.acpnctr.acpnctr.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Allow to have only one Toast message at a time.
 */

public class SingleToast {

    private static Toast mToast;

    public static void show(Context context, String text, int duration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }
}
