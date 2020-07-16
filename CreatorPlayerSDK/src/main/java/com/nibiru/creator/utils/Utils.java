package com.nibiru.creator.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.nibiru.creator.data.HotPotData;
import com.nibiru.creator.data.Vec2;

public class Utils {

    public static int getRatioSize(int imageWidth, int imageHeight, int bitWidth, int bitHeight) {
        // 图片最大分辨率
        /*int imageWidth = 4000;
        int imageHeight = 2000;*/
        // 缩放比
        /*int ratio = 1;
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth;
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight;
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1;*/
        return calculateInSampleSize(imageWidth, imageHeight, bitWidth, bitHeight);
    }

    private static int calculateInSampleSize(int reqWidth, int reqHeight, int bitWidth, int bitHeight) {
        // Raw height and width of image
        int inSampleSize = 1;
        if (bitHeight > reqHeight || bitWidth > reqWidth) {
            final int halfHeight = bitHeight / 2;
            final int halfWidth = bitWidth / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
            long totalPixels = bitWidth / inSampleSize * bitHeight / inSampleSize;
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }

    public static Vec2 getScreenCoord(HotPotData.Vec2 p) {
        return new Vec2((p.getMX() + 1) / 2.0f * Constants.screen_width,
                Constants.screen_height - (p.getMY() + 1) / 2.0f * Constants.screen_height);
    }

    public static Vec2 getScreenCoord(HotPotData.Vec2 p, float imageWidth, float imageHeight) {
        return new Vec2((p.getMX() + 1) / 2.0f * imageWidth,
                imageHeight - (p.getMY() + 1) / 2.0f * imageHeight);
    }

    public static Bitmap generateRoundBitmap(int width, int height, int color, int radius) {
        Bitmap localBitmap = Bitmap.createBitmap(width + 4, height + 4,
                Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setColor(color);
        localCanvas.drawRoundRect(new RectF(2, 2, width + 2, height + 2), radius, radius, localPaint);
        return localBitmap;
    }

    public static Bitmap generateRoundRectBitmap(int width, int height, int color, int radiusX, int radiusY) {
        Bitmap localBitmap = Bitmap.createBitmap(width + 4, height + 4,
                Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setColor(color);
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int xRadius = radiusX > halfWidth ? halfWidth : radiusX;
        int yRadius = radiusY > halfHeight ? halfHeight : radiusY;
        localCanvas.drawRoundRect(new RectF(2, 2, width + 2, height + 2), xRadius, yRadius, localPaint);
        return localBitmap;
    }

    public static Bitmap generateCircleOvalBitmap(int width, int height, int color) {
        Bitmap localBitmap = Bitmap.createBitmap(width + 4, height + 4,
                Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setColor(color);
        localCanvas.drawOval(new RectF(2, 2, width + 2, height + 2), localPaint);
        return localBitmap;
    }

    public static Bitmap getRoundTextBitmap(Context context, int width, int height, int radiusX, int radiusY, String textFont, String text, int bgColor, int textColor, float textSize, int alignment, float spacingMult, boolean isBold, boolean isItalic, boolean isUnderline) {
        Bitmap localBitmap = Bitmap.createBitmap(width + 4, height + 4,
                Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        localPaint.setAntiAlias(true);
        localPaint.setColor(bgColor);
        localCanvas.drawRoundRect(new RectF(2, 2, width + 2, height + 2), radiusX, radiusY, localPaint);
        if (!TextUtils.isEmpty(text)) {
            int paddingLeft = DensityUtils.dp2px(context, 3);
            int paddingTop = DensityUtils.dp2px(context, 5);
            int textWidth = width - 2 * paddingLeft;
            int textHeight = height - 2 * paddingTop;
            Bitmap textBitmap = generateWLT(context, textFont, text, textSize, alignment, spacingMult, isBold, isItalic, isUnderline, textColor, textWidth, textHeight);
            float left = 2 + paddingLeft;
            float top = 2 + paddingTop;
            //防止出现文字透明
            localPaint.setColor(Color.WHITE);
            localCanvas.drawBitmap(textBitmap, null, new RectF(left, top, left + textWidth, top + textHeight), localPaint);

            if (!textBitmap.isRecycled()) {
                textBitmap.recycle();
                textBitmap = null;
            }
        }
        return localBitmap;
    }

    public static Bitmap generateWLT(Context context, String textFont, String str, float textSize, float spacingMultiplier, boolean isBold, boolean isItalic, boolean isUnderline, int color,
                                     int width, int height) {
        Bitmap bmTemp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(bmTemp);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        if (isBold) {
            textPaint.setFakeBoldText(true);
        }
        if (isUnderline) {
            textPaint.setUnderlineText(true);
        }
        if (isItalic) {
            textPaint.setTextSkewX(-0.25f);
        }
        StaticLayout layout = new StaticLayout(str, textPaint, width, Layout.Alignment.ALIGN_NORMAL,
                spacingMultiplier, 0.0f, false);
        layout.draw(canvasTemp);
        return bmTemp;
    }

    public static Bitmap generateWLT(Context context, String textFont, String str, float textSize, int alignment, float spacingMultiplier, boolean isBold, boolean isItalic, boolean isUnderline, int color,
                                     int width, int height) {
        Bitmap bmTemp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(bmTemp);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        if (isBold) {
            textPaint.setFakeBoldText(true);
        }
        if (isUnderline) {
            textPaint.setUnderlineText(true);
        }
        if (isItalic) {
            textPaint.setTextSkewX(-0.25f);
        }
        StaticLayout layout = new StaticLayout(str, textPaint, width, alignment == 1 ? Layout.Alignment.ALIGN_NORMAL :
                alignment == 2 ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER,
                spacingMultiplier, 0.0f, false);
        layout.draw(canvasTemp);
        return bmTemp;
    }

    public static double[] millierConvertion(int imageWidth, int imageHeight, float lat, float lon) {
        double W = imageWidth;
        double H = imageHeight;
        double mill = 2.3; // 米勒投影中的一个常数，范围大约在正负2.3之间
        double x = lon * Math.PI / 180; // 将经度从度数转换为弧度
        double y = lat * Math.PI / 180; // 将纬度从度数转换为弧度
        y = 1.25 * Math.log(Math.tan(0.25 * Math.PI + 0.4 * y)); // 米勒投影的转换
        // 弧度转为实际距离
        x = (W / 2) + (W / (2 * Math.PI)) * x;
        y = (H / 2) - (H / (2 * mill)) * y;
        double[] result = new double[2];
        result[0] = x;
        result[1] = y;
        Logger.e("x: " + x + ", y: " + y);
        return result;
    }
}
