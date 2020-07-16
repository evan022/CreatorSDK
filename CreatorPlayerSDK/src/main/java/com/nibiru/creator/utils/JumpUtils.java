package com.nibiru.creator.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.nibiru.creator.R;
import com.nibiru.creator.activity.ImageActivity;
import com.nibiru.creator.activity.ImageListActivity;
import com.nibiru.creator.activity.ImageTextActivity;
import com.nibiru.creator.activity.TextActivity;
import com.nibiru.creator.activity.VideoActivity;
import com.nibiru.creator.data.HotPotData.HotPot;

public class JumpUtils {
    public static void jumpToHotPot(Activity context, HotPot hotPot) {
        if (hotPot.hasMImageHotPot()) {
            if (hotPot.getMImageHotPot() == null || TextUtils.isEmpty(hotPot.getMImageHotPot().getMImageName())) {
                ToastUtil.showToast(context, context.getString(R.string.tip_no_resource));
            } else {
                ImageActivity.start(context, ImageActivity.class, hotPot);
            }
        } else if (hotPot.hasMImageListHotPot()) {
            if (hotPot.getMImageListHotPot() == null
                    || hotPot.getMImageListHotPot().getMImagePathsList() == null
                    || hotPot.getMImageListHotPot().getMImagePathsList().size() == 0) {
                ToastUtil.showToast(context, context.getString(R.string.tip_no_resource));
            } else {
                ImageListActivity.start(context, ImageListActivity.class, hotPot);
            }
        } else if (hotPot.hasMImageTxtHotPot()) {
            if (hotPot.getMImageTxtHotPot() == null
                    || (TextUtils.isEmpty(hotPot.getMImageTxtHotPot().getMImagePath()))) {
                ToastUtil.showToast(context, context.getString(R.string.tip_no_resource));
            } else {
//                Picture2DActivity.loadImageHotPot(context, hotPot, baseScene != null);
                ImageTextActivity.start(context, ImageTextActivity.class, hotPot);
            }
        } else if (hotPot.hasMVideoImageHotPot()) {
            if (hotPot.getMVideoImageHotPot() == null || TextUtils.isEmpty(hotPot.getMVideoImageHotPot().getMFileUrl())) {
                ToastUtil.showToast(context, context.getString(R.string.tip_no_resource));
            } else {
                VideoActivity.start(context, VideoActivity.class, hotPot);
            }
        } else if (hotPot.hasMLabelHotPot()) {
            if (hotPot.getMLabelHotPot() == null || TextUtils.isEmpty(hotPot.getMLabelHotPot().getMTitle())) {
                ToastUtil.showToast(context, context.getString(R.string.tip_no_resource));
            } else {
                TextActivity.start(context, TextActivity.class, hotPot);
            }
        } else if (hotPot.hasMModelHotPot()) {
            if (hotPot.getMModelHotPot() == null || TextUtils.isEmpty(hotPot.getMModelHotPot().getMModelPath())) {
                ToastUtil.showToast(context, context.getString(R.string.tip_no_resource));
            } else {

            }
        }
    }
}
