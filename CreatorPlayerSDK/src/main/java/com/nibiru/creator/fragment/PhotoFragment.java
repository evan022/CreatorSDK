package com.nibiru.creator.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.nibiru.creator.R;
import com.nibiru.creator.activity.CreatorPlayerActivity;
import com.nibiru.creator.activity.SelectorActivity;
import com.nibiru.creator.activity.TextActivity;
import com.nibiru.creator.data.HotPotData.DisappearMode;
import com.nibiru.creator.data.HotPotData.DisplayMode;
import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.data.HotPotData.HotPotAppearance;
import com.nibiru.creator.data.HotPotData.HotPotAttribute;
import com.nibiru.creator.data.HotPotData.SkyBoxSceneAttribute;
import com.nibiru.creator.data.HotPotData.Vec2;
import com.nibiru.creator.data.HotPotData.AudioHotPot;
import com.nibiru.creator.data.HotPotData.SceneRenderMode;
import com.nibiru.creator.data.SceneData;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.utils.Constants;
import com.nibiru.creator.utils.JumpUtils;
import com.nibiru.creator.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoFragment extends BaseFragment {
    private SceneData sceneData;
    private int sceneId;
    private int renderMode = SceneRenderMode.SceneType_VR_VALUE; // 渲染模式
    private ConstraintLayout rootView;
    private ImageView imageView;
    private List<View> hotPotActors = new ArrayList<View>();
    private int imageWidth;
    private int imageHeight;
    private Map<String, Boolean> audioStateMap = new HashMap<String, Boolean>();
    private String currentAudioSource;
    private int type;
    private boolean isPrepared;
    private float scale = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            sceneData = arguments.getParcelable("data");
        }
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        rootView = view.findViewById(R.id.root_view);
        imageView = view.findViewById(R.id.image);
        updateUI();
        return view;
    }

    private void updateUI() {
        if (sceneData != null) {
            sceneId = sceneData.getSceneId();
            renderMode = sceneData.getRenderMode();
            String fileUrl = sceneData.getFileUrl();
            if (Constants.firstSceneId == sceneId && Constants.isFirstOpened) {
                ((CreatorPlayerActivity) context).showLoading();
                Constants.isFirstOpened = false;
            } else {
                ((CreatorPlayerActivity) context).showTip();
            }
            if (Constants.value_isCMCC) {
                retrofitHelper.getResUrl(fileUrl, new RetrofitHelper.OnGetResUrlListener() {
                    @Override
                    public void onSuccess(String playUrl) {
                        loadSkyBox(playUrl);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            } else {
                loadSkyBox(fileUrl);
            }
        }
    }

    private void loadSkyBox(String fileUrl) {
        if (TextUtils.isEmpty(fileUrl)) {
            return;
        }
        Glide.with(context).load(fileUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                float bitmapWidth = resource.getWidth();
                float bitmapHeight = resource.getHeight();
                if (bitmapWidth > Constants.screen_width) {
                    scale = bitmapWidth * 1f / Constants.screen_width;
                } else {
                    scale = 1;
                }
                imageHeight = Constants.screen_height;
                imageWidth = (int) (bitmapWidth / bitmapHeight * imageHeight);

                ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
                layoutParams.width = imageWidth;
                layoutParams.height = imageHeight;
                rootView.setLayoutParams(layoutParams);

                imageView.setImageBitmap(resource);
                if (imageView.getVisibility() != View.VISIBLE) {
                    imageView.setVisibility(View.VISIBLE);
                }
                isPrepared = true;
                ((CreatorPlayerActivity) context).hideLoading();
                setFocusedView(null);
                loadHotPots();
            }
        });
    }

    private void loadHotPots() {
        List<HotPot> hotPots = hotPotDataManager.getHotPots(sceneId);
        if (hotPots != null && hotPots.size() > 0) {
            for (final HotPot hotPot : hotPots) {
                int id = hotPot.getMHotPotID(); // 热点ID
                HotPotAttribute hotPotAttribute = hotPot.getMAttribute();
                final Vec2 position = hotPot.getMPosition(); // 热点在场景中的位置
                String name = hotPot.getMName(); // 热点名称
                boolean isShowName = (hotPot.getMShowName() == 1); // 是否显示热点名称

                // 热点显示类型，默认是点击出现、点击消失
                int hotPotShowType = DisplayMode.DisplayMode_ClickShow_VALUE;
                int hotPotDisappearType = DisappearMode.DisappearMode_ClickHidden_VALUE;
                if (hotPot.hasMDisplayMode()) {
                    hotPotShowType = hotPot.getMDisplayMode().getNumber();
                }
                if (hotPot.hasMDisappearMode()) {
                    hotPotDisappearType = hotPot.getMDisappearMode().getNumber();
                }

                HotPotAppearance appearance = hotPotAttribute.getMAppearance();
                final String[] hotPotImageName = {appearance.getMFileUrl()};

                String[] focusedImageNames = new String[1];
                if (hotPot.hasHoverImage()) {
                    focusedImageNames[0] = hotPot.getHoverImage();
                    Log.e("Test", "has hover image");
                }
                Log.e("Test", "hover image: " + focusedImageNames[0]);
//                final float scale = Constants.densityDpi == 640 ? 1 : Constants.densityDpi / 640f * 1.6f;
                float hotPotWidth = 1f;
                float hotPotHeight = 1f;
                if (hotPot.hasMLabelSize()) {
                    Vec2 size = hotPot.getMLabelSize();
                    hotPotWidth = size.getMX() / scale;
                    hotPotHeight = size.getMY() / scale;
                }

                LayoutInflater inflater = LayoutInflater.from(context);
                final View layout = inflater.inflate(R.layout.layout_hotpot, null);
                layout.setFocusable(true);
                final ImageView iconIv = layout.findViewById(R.id.icon);
                final TextView nameTv = layout.findViewById(R.id.name);
                nameTv.setText(name);

                if (!TextUtils.isEmpty(name) && isShowName) {
                    nameTv.setVisibility(View.VISIBLE);
                } else {
                    nameTv.setVisibility(View.GONE);
                }

                ViewGroup.LayoutParams iconLayoutParams = iconIv.getLayoutParams();
                iconLayoutParams.width = (int) hotPotWidth;
                iconLayoutParams.height = (int) hotPotHeight;
                iconIv.setLayoutParams(iconLayoutParams);

                final com.nibiru.creator.data.Vec2 coordinate = Utils.getScreenCoord(position, imageWidth, imageHeight); // 热点中心点
                final ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                layoutParams.topToTop = ConstraintSet.PARENT_ID;
                final float[] size = {hotPotWidth, hotPotHeight};
//                layoutParams.leftMargin = (int) ((coordinate.getX() - hotPotWidth / 2));
//                layoutParams.topMargin = (int) ((coordinate.getY() - hotPotHeight / 2 - 74));
//                layout.setScaleX(scale);
//                layout.setScaleY(scale);
                rootView.addView(layout, layoutParams);
                layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                layout.post(new Runnable() {
                    @Override
                    public void run() {
                        int layoutWidth = layout.getMeasuredWidth();
                        int layoutHeight = layout.getMeasuredHeight();
                        if (renderMode == SceneRenderMode.SceneType_VR_VALUE) {
                            double[] result = Utils.millierConvertion(imageWidth, imageHeight, position.getMY(), position.getMX());
                            layoutParams.leftMargin = (int) (result[0] - imageWidth / 2f - (layoutWidth > size[0] ? layoutWidth / 2f : size[0] / 2));
                            layoutParams.topMargin = (int) (result[1] - size[1] / 2 - (!TextUtils.isEmpty(name) && isShowName ? layoutHeight - size[1] : 0));
                        } else {
                            layoutParams.leftMargin = (int) ((coordinate.getX() - (layoutWidth > size[0] ? layoutWidth / 2 : size[0] / 2)));
                            layoutParams.topMargin = (int) ((coordinate.getY() - size[1] / 2 - (!TextUtils.isEmpty(name) && isShowName ? layoutHeight - size[1] : 0)));
                        }
                        layout.setLayoutParams(layoutParams);
                    }
                });
                hotPotActors.add(layout);
                if (Constants.value_isCMCC) {
                    retrofitHelper.getResUrl(hotPotImageName[0], new RetrofitHelper.OnGetResUrlListener() {
                        @Override
                        public void onSuccess(String playUrl) {
                            hotPotImageName[0] = playUrl;
                            Glide.with(context).load(playUrl).dontAnimate().into(iconIv);
                        }

                        @Override
                        public void onFailure() {

                        }
                    });
                    if (!TextUtils.isEmpty(focusedImageNames[0])) {
                        retrofitHelper.getResUrl(focusedImageNames[0], new RetrofitHelper.OnGetResUrlListener() {
                            @Override
                            public void onSuccess(String playUrl) {
                                focusedImageNames[0] = playUrl;
                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                    }
                } else {
                    Glide.with(context).load(hotPotImageName[0]).dontAnimate().into(iconIv);
                }

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isAudio = false;
                        boolean isVideo = false;
                        if (hotPot.hasMAudioHotPot()) {
                            isAudio = true;
                        } else if (hotPot.hasMVideoImageHotPot()) {
                            isVideo = true;
                        }
                        if (type == TYPE_AUDIO) {
                            if (!isAudio) {
                                stopAudio();
                            }
                        }
                        if (hotPot.hasMSceneSwitchHotPot()) {
                            switchScene(hotPot);
                        } else if (hotPot.hasMAudioHotPot()) {
//                            TextActivity.start(getActivity(), TextActivity.class, hotPot);
                            AudioHotPot audioHotPot = hotPot.getMAudioHotPot();
                            if (audioStateMap.get(hotPot.getMHotPotID() + "_" + audioHotPot.getMFileUrl()) != null) {
                                stopAudio();
                            } else {
                                playAudio(hotPot);
                            }
                        } else {
                            JumpUtils.jumpToHotPot(getActivity(), hotPot);
                        }
                    }
                });

                layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (isPrepared) {
                            if (hasFocus) {
                                if (!TextUtils.isEmpty(focusedImageNames[0])) {
                                    Glide.with(context).load(focusedImageNames[0]).dontAnimate().into(iconIv);
                                } else {
                                    scaleUp(v);
                                }
                            } else {
                                if (!TextUtils.isEmpty(focusedImageNames[0])) {
                                    if (!((CreatorPlayerActivity)context).isFinishing()) {
                                        Glide.with(context).load(hotPotImageName[0]).dontAnimate().into(iconIv);
                                    }
                                } else {
                                    scaleDown(v);
                                }
                            }
                        }
                        setFocusedView(layout);
                    }
                });
            }
        }
    }

    private void scaleUp(View v) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation animation = new ScaleAnimation(1.0f,1.2f,1.0f,1.2f,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animationSet.addAnimation(animation);
        animationSet.setFillAfter(true);
        v.clearAnimation();
        v.startAnimation(animationSet);
    }

    private void scaleDown(View v) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation animation = new ScaleAnimation(1.2f,1.0f,1.2f,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animationSet.addAnimation(animation);
        animationSet.setFillAfter(true);
        v.clearAnimation();
        v.startAnimation(animationSet);
    }

    private void switchScene(HotPot hotPot) {
        if (hotPot.hasMSceneSwitchHotPot()) {
            sceneId = hotPot.getMSceneSwitchHotPot().getMNextScene();
            List<SceneData> sceneDataList = ((CreatorPlayerActivity) context).getSceneDataList();
            if (sceneDataList != null && sceneDataList.size() > 0) {
                for (SceneData sceneData : sceneDataList) {
                    if (sceneId == sceneData.getSceneId()) {
                        if (sceneData.getSceneType() == 2) {
                            SelectorActivity.jumpToSelectorForResult(getActivity(), hotPot.getMSceneSwitchHotPot().getMNextScene());
                        } else {
                            if (sceneData.getSceneType() == 0) {
                                renderMode = sceneData.getRenderMode();
                                switchScene();
                            } else {
                                ((CreatorPlayerActivity) context).changeFrag(VideoFragment.class, sceneData);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void updateUI(SceneData sceneData) {
        sceneId = sceneData.getSceneId();
        switchScene();
    }

    private void switchScene() {
        isPrepared = false;
        if (hotPotActors != null && hotPotActors.size() > 0) {
            for (View v : hotPotActors) {
                rootView.removeView(v);
            }
            hotPotActors.clear();
        }
        renderMode = SceneRenderMode.SceneType_VR_VALUE;
        if (audioStateMap != null) {
            audioStateMap.clear();
        }
        currentAudioSource = null;
        type = 0;
        imageView.setVisibility(View.GONE);
        ((CreatorPlayerActivity) context).showTip();
        SkyBoxSceneAttribute sceneAttribute = hotPotDataManager.getSkyBoxSceneAttribute(sceneId);
        if (sceneAttribute != null) {
            String fileUrl = sceneAttribute.getMFileUrl();
            if (sceneAttribute.hasMRenderMode()) {
                renderMode = sceneAttribute.getMRenderMode().getNumber();
            }
            if (Constants.value_isCMCC) {
                retrofitHelper.getResUrl(fileUrl, new RetrofitHelper.OnGetResUrlListener() {
                    @Override
                    public void onSuccess(String playUrl) {
                        loadSkyBox(playUrl);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            } else {
                loadSkyBox(fileUrl);
            }
        }
    }

    private void playAudio(HotPot hotPot) {
        if (type == TYPE_AUDIO) {
            stopAudio();
        }
        AudioHotPot audioHotPot = hotPot.getMAudioHotPot();
        int id = hotPot.getMHotPotID();
        String dataSource = audioHotPot.getMFileUrl();
        int playType = audioHotPot.getMLoopTime();
        /*if (hasBackgroundAudio(sceneId)) {
            backgroundAudioPlayer.pause();
        } else if (hasAllBackgroundAudio()) {
            backgroundAudioPlayer.pauseAll();
        }*/
        String actorKey = id + "_" + dataSource;
        audioPlayer.play(dataSource, playType);
        currentAudioSource = actorKey;
        audioStateMap.put(actorKey, true);
        type = TYPE_AUDIO;
    }

    private void stopAudio() {
        /*if (hasBackgroundAudio(sceneId)) {
            backgroundAudioPlayer.start();
        } else if (hasAllBackgroundAudio()) {
            backgroundAudioPlayer.startAll();
        }*/
        audioPlayer.stop();
        if (currentAudioSource != null) {
            audioStateMap.remove(currentAudioSource);
            currentAudioSource = null;
        }
        type = 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SelectorActivity.REQUEST_CODE_SELECT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                sceneId = data.getIntExtra(SelectorActivity.INTENT_SCENE_ID, -1);
                switchScene();
            }
        }
    }
}
