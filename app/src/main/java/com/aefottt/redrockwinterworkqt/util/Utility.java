package com.aefottt.redrockwinterworkqt.util;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.aefottt.redrockwinterworkqt.view.my.MyApplication;

public class Utility {
    /**
     * 用户信息相关的文件名
     */
    public static final String FILE_NAME_USER_INFO = "UserInfo";
    // 是否登录过的Key值
    public static final String KEY_IS_LOGIN = "isLogin";
    // 用户名信息
    public static final String KEY_USERNAME = "userName";
    // JSESSIONID
    public static final String KEY_COOKIE = "Cookie";
    // Cookie数量
    public static final String KEY_COOKIE_NUM = "CookieNum";

    /**
     * dp转换为px
     */
    public static int dpToPx(int dpValue) {
        final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Bitmap blurBitmap(Bitmap bitmap, float radius){
        // 以源Bitmap为模板创建一个新的目标Bitmap
        Bitmap blurBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // 创建RenderScript，它是一个Android平台提供的高性能计算框架
        RenderScript rs = RenderScript.create(MyApplication.getContext());
        // 在rs的基础上创建高斯模糊对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        // 创建Allocations，将数据输入输出RenderScript
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, blurBitmap);
        // 设定模糊度，范围0-25，超过范围会崩溃
        blurScript.setRadius(radius);
        // 运行RenderScript，输入数据，并将输出数据赋给allOut
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        // 将Render运行后的输出结果（allOut）拷贝给目标Bitmap
        allOut.copyTo(blurBitmap);
        // 销毁Render
        rs.destroy();
        return blurBitmap;
    }
}
