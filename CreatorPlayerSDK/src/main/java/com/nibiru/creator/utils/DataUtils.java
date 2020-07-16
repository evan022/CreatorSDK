package com.nibiru.creator.utils;

import android.graphics.Color;
import android.text.TextUtils;

import com.nibiru.creator.data.AudioData;
import com.nibiru.creator.data.HotPotData;
import com.nibiru.creator.data.ImageData;
import com.nibiru.creator.data.ImageListData;
import com.nibiru.creator.data.ImageTextData;
import com.nibiru.creator.data.LabelData;
import com.nibiru.creator.data.VideoData;

import java.util.List;

/**
 * 获得各类热点数据的工具类
 */
public class DataUtils {

    public static LabelData getLabelData(HotPotData.HotPot hotPot) {
        if (hotPot == null || hotPot.getMLabelHotPot() == null) {
            return null;
        }
        LabelData labelData = new LabelData();
        int hotPotID = hotPot.getMHotPotID(); // 热点id
        HotPotData.LabelHotPot labelHotPot = hotPot.getMLabelHotPot();
        // 文字背景宽高
        float bgW = labelHotPot.getMShowRect().getMX();
        float bgH = labelHotPot.getMShowRect().getMY();

        // 文字颜色
        int textRed = (int) labelHotPot.getMFontColor().getMX();
        int textGreen = (int) labelHotPot.getMFontColor().getMY();
        int textBlue = (int) labelHotPot.getMFontColor().getMZ();
        int textAlpha = 255;
        int textColor = Color.argb(textAlpha, textRed, textGreen, textBlue);

        // 文字背景色
        int bgRed = (int) (labelHotPot.getMBackgroundColor().getMX());
        int bgGreen = (int) (labelHotPot.getMBackgroundColor().getMY());
        int bgBlue = (int) (labelHotPot.getMBackgroundColor().getMZ());
        float backgroundOpacity = labelHotPot.getMBackgroundOpacity();
        int bgAlpha = (int) (backgroundOpacity * 255 / 100f);
        int bgColor = Color.argb(bgAlpha, bgRed, bgGreen, bgBlue);

        // 字体大小
        float charSize = labelHotPot.getMCharSize() * 1.39f;
        // 要显示的文字
        String title = labelHotPot.getMTitle();

        int bold = labelHotPot.getMBolid();
        int italic = labelHotPot.getMItalic();
        int underline = labelHotPot.getMUnderline();
        float spacingMultiplier = 1; // 文字行高
        if (labelHotPot.hasMLineDistance()) {
            spacingMultiplier = (charSize + labelHotPot.getMLineDistance()) / charSize;
        }

        int alignment = 1; // 文本对齐方式，默认左对齐
        if (labelHotPot.hasMTextAlignment()) {
            alignment = labelHotPot.getMTextAlignment();
        }

        String textFont = "宋体";
        if (labelHotPot.hasMTextFont()) {
            textFont = labelHotPot.getMTextFont();
        }

        labelData.setId(hotPotID);
        labelData.setBgWidth(bgW);
        labelData.setBgHeight(bgH);
        labelData.setTextColor(textColor);
        labelData.setBgColor(bgColor);
        labelData.setFontSize(charSize);
        labelData.setTitle(title);
        labelData.setBold(bold == 1);
        labelData.setItalic(italic == 1);
        labelData.setUnderline(underline == 1);
        labelData.setSpacingMultiplier(spacingMultiplier);
        labelData.setTextFont(textFont);
        labelData.setAlignment(alignment);
        return labelData;
    }

    public static ImageTextData getImageTextData(HotPotData.HotPot hotPot) {
        if (hotPot == null || hotPot.getMImageTxtHotPot() == null) {
            return null;
        }
        ImageTextData imageTextData = new ImageTextData();
        int id = hotPot.getMHotPotID();
        HotPotData.ImageTxtHotPot imageTxtHotPot = hotPot.getMImageTxtHotPot();
        String imagePath = imageTxtHotPot.getMImagePath();
        imagePath = imageTxtHotPot.getMFileUrl();
        // 文本
        String text = imageTxtHotPot.getMTxt();
        // 图片类型
        int imageType = imageTxtHotPot.getMImageType().getNumber();
        // 图文显示方式
        int textMode = imageTxtHotPot.getMImageTxtMode().getNumber();
        float width = imageTxtHotPot.getMShowRect().getMX();
        float height = imageTxtHotPot.getMShowRect().getMY();

        int textRed = 0;
        int textGreen = 0;
        int textBlue = 0;
        int textAlpha = 255;
        int textColor;
        float charSize = 10 * 1.39f;
        if (!TextUtils.isEmpty(text)) {
            if (imageTxtHotPot.hasMFontColor()) {
                textRed = (int) imageTxtHotPot.getMFontColor().getMX();
                textGreen = (int) imageTxtHotPot.getMFontColor().getMY();
                textBlue = (int) imageTxtHotPot.getMFontColor().getMZ();
            }
            if (imageTxtHotPot.hasMCharSize()) {
                charSize = imageTxtHotPot.getMCharSize() * 1.5f;
            }
        }
        // 文本颜色
        textColor = Color.argb(textAlpha, textRed, textGreen, textBlue);

        int bold = 0;
        int italic = 0;
        int underline = 0;
        if (imageTxtHotPot.hasMBolid()) {
            bold = imageTxtHotPot.getMBolid();
        }
        if (imageTxtHotPot.hasMItalic()) {
            italic = imageTxtHotPot.getMItalic();
        }
        if (imageTxtHotPot.hasMUnderline()) {
            underline = imageTxtHotPot.getMUnderline();
        }

        float spacingMultiplier = 1;
        if (imageTxtHotPot.hasMLineDistance()) {
            spacingMultiplier = (charSize + imageTxtHotPot.getMLineDistance()) / charSize;
        }

        String textFont = "宋体";
        if (imageTxtHotPot.hasMTextFont()) {
            textFont = imageTxtHotPot.getMTextFont();
        }

        imageTextData.setId(id);
        imageTextData.setImagePath(imagePath);
        imageTextData.setText(text);
        imageTextData.setImageType(imageType);
        imageTextData.setTextMode(textMode);
        imageTextData.setWidth(width);
        imageTextData.setHeight(height);
        imageTextData.setTextColor(textColor);
        imageTextData.setFontSize(charSize);
        imageTextData.setBold(bold == 1);
        imageTextData.setItalic(italic == 1);
        imageTextData.setUnderline(underline == 1);
        imageTextData.setSpacingMultiplier(spacingMultiplier);
        imageTextData.setTextFont(textFont);
        return imageTextData;
    }

    public static ImageData getImageData(HotPotData.HotPot hotPot) {
        if (hotPot == null || hotPot.getMImageHotPot() == null) {
            return null;
        }

        ImageData imageData = new ImageData();
        int id = hotPot.getMHotPotID();
        HotPotData.ImageHotPot imageHotPot = hotPot.getMImageHotPot();
        String imageName = imageHotPot.getMImageName();
        imageName = imageHotPot.getMFileUrl();
        // 图片类型
        int type = imageHotPot.getMImageType().getNumber();
        float imageWidth = imageHotPot.getMShowSize().getMX() / 50f;
        float imageHeight = imageHotPot.getMShowSize().getMY() / 50f;

        imageData.setId(id);
        imageData.setImageName(imageName);
        imageData.setType(type);
        imageData.setImageWidth(imageWidth);
        imageData.setImageHeight(imageHeight);
        return imageData;
    }

    public static AudioData getAudioData(HotPotData.HotPot hotPot) {
        if (hotPot == null || hotPot.getMAudioHotPot() == null) {
            return null;
        }
        AudioData audioData = new AudioData();
        int id = hotPot.getMHotPotID();
        HotPotData.AudioHotPot audioHotPot = hotPot.getMAudioHotPot();
        String audioName = audioHotPot.getMAudioName();
        audioName = audioHotPot.getMFileUrl();
        int loopTime = audioHotPot.getMLoopTime();

        audioData.setId(id);
        audioData.setAudioName(audioName);
        audioData.setLoopTime(loopTime);
        return audioData;
    }

    public static VideoData getVideoData(HotPotData.HotPot hotPot) {
        if (hotPot == null || hotPot.getMVideoImageHotPot() == null) {
            return null;
        }
        VideoData videoData = new VideoData();
        HotPotData.VideoImageHotPot videoHotPot = hotPot.getMVideoImageHotPot();
        int id = hotPot.getMHotPotID();
        String dataSource = videoHotPot.getMVideoName();
        dataSource = videoHotPot.getMFileUrl();
        HotPotData.Vec2 showRect = videoHotPot.getMShowRect();
        float width = showRect.getMX() / 50f;
        float height = showRect.getMY() / 50f;
        int videoType = videoHotPot.getMVideoType().getNumber();
        int sourceMode = videoHotPot.getMSourceMode().getNumber();
        String snapPath = videoHotPot.getMSnappath();

        videoData.setId(id);
        videoData.setDataSource(dataSource);
        videoData.setVideoType(videoType);
        videoData.setVideoWidth(width);
        videoData.setVideoHeight(height);
        videoData.setSourceMode(sourceMode);
        videoData.setSnapPath(snapPath);
        return videoData;
    }

    public static ImageListData getImageListData(HotPotData.HotPot hotPot) {
        if (hotPot == null || hotPot.getMImageListHotPot() == null) {
            return null;
        }

        ImageListData imageListData = new ImageListData();
        int id = hotPot.getMHotPotID();
        HotPotData.ImageListHotPot imageListHotPot = hotPot.getMImageListHotPot();
        List<String> pathList = imageListHotPot.getMImagePathsList();
        pathList = imageListHotPot.getMFileUrlList();
        int type = imageListHotPot.getMImageType().getNumber();
        int imageSum = imageListHotPot.getMImageSum();
        int autoPlay = imageListHotPot.getMIsAutoPlay();
        HotPotData.Vec2 showSize = imageListHotPot.getMShowSize();
        if (showSize != null) {
            float imageWidth = showSize.getMX() / 50f;
            float imageHeight = showSize.getMY() / 50f;
            imageListData.setImageWidth(imageWidth);
            imageListData.setImageHeight(imageHeight);
        }

        imageListData.setId(id);
        imageListData.setPathList(pathList);
        imageListData.setType(type);
        imageListData.setImageSum(imageSum);
        imageListData.setAutoPlay(autoPlay);
        return imageListData;
    }
}
