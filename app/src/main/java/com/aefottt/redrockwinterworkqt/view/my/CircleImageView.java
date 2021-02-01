package com.aefottt.redrockwinterworkqt.view.my;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 通过BitmapShader着色器来绘制圆形图片
 */
public class CircleImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Paint mPaint; //画笔
    private Matrix mMatrix; //矩阵
    private int mRadius; //半径
    private Bitmap mBitmap; //Bitmap图片
    private BitmapShader mBitmapShader; //着色器

    public CircleImageView(@NonNull Context context) {
        super(context);
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化操作
     */
    private void init() {
        // 判断是否有图片资源
        if (getDrawable() == null){
            return;
        }
        mPaint = new Paint();
        mMatrix = new Matrix();
        mBitmap = drawableToBitmap(getDrawable());
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
    }

    /**
     * 将Drawbable转化为Bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        // 是否为BitmapDrawable的子类
        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }
        // 通过画布将drawable画到bitmap上
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        //创建一个宽高与图片一样长的Bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //创建以bitmap为背景的画布
        Canvas canvas = new Canvas(bitmap);
        //将drawable画到canvas上
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取最短的边设为半径
        mRadius = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 2;

        //重新设置图片大小
        setMeasuredDimension(mRadius * 2, mRadius * 2);
    }

    /**
     * 进行绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas); TODO 这里一定要删掉才能绘制成功！
        // 计算缩放比例
        float mScale =(mRadius * 2.0f) / Math.min(mBitmap.getWidth(), mBitmap.getHeight());
        // 设置矩阵的偏移度
        mMatrix.setScale(mScale, mScale);
        // 配备矩阵给着色器
        mBitmapShader.setLocalMatrix(mMatrix);
        // 配备着色器给画笔
        mPaint.setShader(mBitmapShader);
        // 在画布上画圆
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
    }
}
