package com.nibiru.creator.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.FileInputStream;

public class BitmapUtil {
    /**
     * 给二维码中心添加logo
     *
     * @param qrBitmap   二维码图片
     * @param logoBitmap LOGO图片
     * @return
     */
    public static Bitmap addLogo(Bitmap qrBitmap, Bitmap logoBitmap) {
        int qrBitmapWidth = qrBitmap.getWidth();
        int qrBitmapHeight = qrBitmap.getHeight();
        int logoBitmapWidth = logoBitmap.getWidth();
        int logoBitmapHeight = logoBitmap.getHeight();
        Bitmap blankBitmap = Bitmap.createBitmap(qrBitmapWidth, qrBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blankBitmap);
        canvas.drawBitmap(qrBitmap, 0, 0, null);
        canvas.save(/*Canvas.ALL_SAVE_FLAG*/);
        float scaleSize = 1.0f;
        while ((logoBitmapWidth / scaleSize) > (qrBitmapWidth / 5) || (logoBitmapHeight / scaleSize) > (qrBitmapHeight / 5)) {
            scaleSize *= 2;
        }
        float sx = 1.0f / scaleSize;
        canvas.scale(sx, sx, qrBitmapWidth / 2, qrBitmapHeight / 2);
        canvas.drawBitmap(logoBitmap, (qrBitmapWidth - logoBitmapWidth) / 2, (qrBitmapHeight - logoBitmapHeight) / 2, null);
        canvas.restore();
        return blankBitmap;
    }

    /**
     * url生成图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 毛玻璃
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {

            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

        }

        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                .getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;
    }

    public static Bitmap getBitmapWithEdge(Bitmap paramBitmap) {
        Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth() + 4,
                paramBitmap.getHeight() + 4, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        Paint localPaint = new Paint();
        if (paramBitmap != null) {
            localCanvas.drawBitmap(paramBitmap, null, new RectF(2, 2,
                            paramBitmap.getWidth() + 2, paramBitmap.getHeight() + 2),
                    localPaint);
        }
        return localBitmap;
    }

    public static Bitmap generateWLT(String str, float textSize, int color,
                                     int width, int height, Layout.Alignment align, boolean center) {
        Bitmap bmTemp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(bmTemp);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        StaticLayout layout = new StaticLayout(str, textPaint, width, align,
                1.0F, 0.0F, false);
        if (center) {
            canvasTemp.translate(0, (float) (height - layout.getHeight()) / 2f);
        }
        canvasTemp.save();
        layout.draw(canvasTemp);
        canvasTemp.restore();
        return bmTemp;
    }

    public static Bitmap generateWLT(String str, float textSize, int width,
                                     int height) {
        Bitmap bmTemp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(bmTemp);
        // canvasTemp.drawARGB(255, 0, 0, 0);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        StaticLayout layout = new StaticLayout(str, textPaint, width,
                Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
        canvasTemp.save();
        layout.draw(canvasTemp);
        canvasTemp.restore();
        return bmTemp;
    }

    /**
     * @param str
     * @param textSize
     * @param textColor
     * @return
     */
    public static Bitmap generateWLT(String str, float textSize,
                                     String textColor) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(textColor));
        paint.setTextSize(textSize);
        paint.setTypeface(null);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        float measureTextWidth = paint.measureText(str);
        int finalWidth = (int) measureTextWidth + 6;
        int finalHeight = (int) textSize + 6;

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        float baseY = 1.0f / 2 * finalHeight - 1.0f / 2
                * (fontMetrics.ascent + fontMetrics.descent);
        Bitmap bmTemp = Bitmap.createBitmap(finalWidth, finalHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(bmTemp);
        // canvasTemp.drawARGB(255, 0, 0, 0);
        canvasTemp.drawText(str, 0, baseY, paint);
        return bmTemp;
    }

    public static Bitmap generateWLT(String str, String colorStr,
                                     float textSize, int width, int height) {
        Bitmap bmTemp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(bmTemp);
        canvasTemp.drawARGB(160, 0, 0, 0);
        TextPaint textPaint = new TextPaint();
        int color = 0;
        try {
            color = Color.parseColor(colorStr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            color = Color.parseColor("white");
        }
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        StaticLayout layout = new StaticLayout("  " + subStringCN(str, 20),
                textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        canvasTemp.save();
        layout.draw(canvasTemp);
        canvasTemp.restore();
        return bmTemp;
    }

    @SuppressLint("NewApi")
    public static Bitmap generateWLT(String str, float textSize, int color,
                                     int width, int height, Layout.Alignment align) {
        Bitmap bmTemp = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvasTemp = new Canvas(bmTemp);
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        StaticLayout layout = new StaticLayout(str, textPaint, width, align,
                1.0F, 0.0F, false);
        canvasTemp.translate(0, (float) (height - layout.getHeight()) / 2f);
        canvasTemp.save();
        layout.draw(canvasTemp);
        canvasTemp.restore();
        return bmTemp;
    }

    public static String subStringCN(final String str, final int maxLength) {
        if (str == null) {
            return str;
        }
        String suffix = "...";
        int suffixLen = suffix.length();
        final StringBuffer sbuffer = new StringBuffer();
        final char[] chr = str.trim().toCharArray();
        int len = 0;
        for (int i = 0; i < chr.length; i++) {
            if (chr[i] >= 0xa1) {
                len += 2;
            } else {
                len++;
            }
        }
        if (len <= maxLength) {
            return str;
        }
        len = 0;
        for (int i = 0; i < chr.length; i++) {
            if (chr[i] >= 0xa1) {
                len += 2;
                if (len + suffixLen > maxLength) {
                    break;
                } else {
                    sbuffer.append(chr[i]);
                }
            } else {
                len++;
                if (len + suffixLen > maxLength) {
                    break;
                } else {
                    sbuffer.append(chr[i]);
                }
            }
        }
        sbuffer.append(suffix);
        return sbuffer.toString();
    }

}