package com.googolmo.utils.graphics;

import android.content.ContentResolver;

import android.graphics.Bitmap.Config;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.net.Uri;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.lang.Class;
import java.lang.Integer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BitmapUtils {


    /**
     * 旋转图片
     * 
     * @param bmp 原始图片
     * @param angle 旋转的角度
     * @return
     */
    public static Bitmap rotate(Bitmap bmp, float angle) {
        Matrix matrixRotateLeft = new Matrix();
        matrixRotateLeft.setRotate(angle);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrixRotateLeft, true);
    }


    /**
     * 按原比例缩放图片
     * 
     * @param contentResolver
     * @param uri 图片的URI地址
     * @param maxWidth 缩放后的宽度
     * @param maxHeight 缩放后的高度
     * @return
     */
    public static Bitmap scale(ContentResolver contentResolver, Uri uri, int maxWidth, int maxHeight) {
        String tag = "SCALE";
        Log.d(tag, "uri=" + uri.toString());
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = contentResolver.openInputStream(uri);
            BitmapFactory.decodeStream(input, null, options);

            int sourceWidth = options.outWidth;
            int sourceHeight = options.outHeight;

            Log.d(tag, "sourceWidth=" + sourceWidth + ", sourceHeight=" + sourceHeight);
            Log.d(tag, "maxWidth=" + maxWidth + ", maxHeight=" + maxHeight);

            input.close();

            float rate = Math.max(sourceWidth / (float) maxWidth, sourceHeight / (float) maxHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) rate;
            Log.d(tag, "rate=" + rate + ", inSampleSize=" + options.inSampleSize);

            input = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

            int w0 = bitmap.getWidth();
            int h0 = bitmap.getHeight();

            Log.d(tag, "w0=" + w0 + ", h0=" + h0);


            float scaleWidth = maxWidth / (float) w0;
            float scaleHeight = maxHeight / (float) h0;
            float maxScale = Math.min(scaleWidth, scaleHeight);
            Log.d(tag, "scaleWidth=" + scaleWidth + ", scaleHeight=" + scaleHeight);

            Matrix matrix = new Matrix();
            matrix.reset();
            if (maxScale < 1)
                matrix.postScale(maxScale, maxScale);

            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w0, h0, matrix, true);


            input.close();
            // bitmap.recycle();

            return resizedBitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Drawable转换为Bitmap
     * 
     * @param drawable
     * @return
     */
    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable == null)
            return null;

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 　为指定图片增加阴影
     * 
     * @param map　图片
     * @param radius　阴影的半径
     * @return
     */
    public static Bitmap drawShadow(Bitmap map, int radius) {
        if (map == null)
            return null;

        BlurMaskFilter blurFilter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);

        int[] offsetXY = new int[2];
        Bitmap shadowImage = map.extractAlpha(shadowPaint, offsetXY);
        shadowImage = shadowImage.copy(Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage);
        c.drawBitmap(map, -offsetXY[0], -offsetXY[1], null);
        return shadowImage;
    }

    /**
     * 获得圆角的bitmap
     * 
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        try{
            Class cls = Class.forName("android.media.ExifInterface");
            Class[] types = new Class[]{ String.class };
            Constructor cons = cls.getConstructor(types);

            types = new Class[] { String.class, Integer.TYPE };
            Method method = cls.getMethod("getAttributeInt", types);

            Object[] args = new Object[] { filepath };
            Object exif = cons.newInstance(args);

            if (exif != null) {
                args = new Object[] {"Orientation", -1};
                int orientation = (Integer) method.invoke(exif, args);
                if (orientation != -1) {
                    switch(orientation) {
                        //case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                        case 6:
                            degree = 90;
                            break;
                            //case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                        case 3:
                            degree = 180;
                            break;
                            //case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                        case 8:
                            degree = 270;
                            break;
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return degree;
    }

    public static byte[] generateBitstream(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        return os.toByteArray();
    }


}
