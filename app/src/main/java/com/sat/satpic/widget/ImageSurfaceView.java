package com.sat.satpic.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sat.satpic.utils.LogUtils;

import java.io.FileInputStream;

/**
 * Created by Tianluhua on 2018/3/14.
 */

public class ImageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "ImageSurfaceView";

    private Bitmap bitmap;
    private static Matrix matrix = new Matrix();
    //获取画布
    private SurfaceHolder mSurfaceHolder = null;
    private Paint paint = new Paint();


    public ImageSurfaceView(Context context) {
        super(context, null);
        LogUtils.e("ImageSurfaceView---1");

    }

    public ImageSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
        LogUtils.e("ImageSurfaceView---2");
    }

    public ImageSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogUtils.e("ImageSurfaceView---3");
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        this.mSurfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        drawImg();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    //画图方法
    private void drawImg() {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas == null || mSurfaceHolder == null) {
            return;
        }

        try {
            if (bitmap != null) {
                //画布宽和高
                int height = getHeight();
                int width = getWidth();
                //生成合适的图像
                bitmap = getReduceBitmap(bitmap, width, height);
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL);
                //清屏
                paint.setColor(Color.BLACK);
                canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), paint);
                //画图
                canvas.drawBitmap(bitmap, matrix, paint);
            }
            //解锁显示
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        } catch (Exception ex) {
            LogUtils.e("ImageSurfaceView", ex.getMessage());
            return;
        } finally {
            //资源回收
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    //缩放图片
    private Bitmap getReduceBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int hight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float wScake = ((float) w / width);
        float hScake = ((float) h / hight);
        matrix.postScale(wScake, hScake);
        return Bitmap.createBitmap(bitmap, 0, 0, width, hight, matrix, true);
    }

}
