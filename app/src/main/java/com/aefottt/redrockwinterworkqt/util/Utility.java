package com.aefottt.redrockwinterworkqt.util;

import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

public class Utility {
    /**
     * dp转换为px
     */
    public static int dpToPx(int dpValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
