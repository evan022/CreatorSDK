package com.nibiru.creator.fragment;

import android.animation.ObjectAnimator;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.collection.LongSparseArray;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
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
import com.nibiru.creator.R;
import com.nibiru.creator.activity.CreatorPlayerActivity;
import com.nibiru.creator.activity.SelectorActivity;
import com.nibiru.creator.data.HotPotData;
import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.data.HotPotData.HotPotAppearance;
import com.nibiru.creator.data.HotPotData.HotPotAttribute;
import com.nibiru.creator.data.HotPotData.SkyBoxSceneAttribute;
import com.nibiru.creator.data.HotPotData.Vec2;
import com.nibiru.creator.data.HotPotData.AudioHotPot;
import com.nibiru.creator.data.HotPotData.Event;
import com.nibiru.creator.data.HotPotData.SceneRenderMode;
import com.nibiru.creator.data.NpvHotPotData;
import com.nibiru.creator.data.SceneData;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.utils.Constants;
import com.nibiru.creator.utils.JumpUtils;
import com.nibiru.creator.utils.Logger;
import com.nibiru.creator.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VideoFragment extends BaseFragment implements TextureView.SurfaceTextureListener {

    private SceneData sceneData;
    private int sceneId;
    private int renderMode = SceneRenderMode.SceneType_VR_VALUE;
    private boolean needDividedBy1000;
    private List<SceneData> sceneList = new ArrayList<SceneData>();
    private List<View> hotPotActors = new ArrayList<View>();
    private List<NpvHotPotData> hotPotDataList = new ArrayList<NpvHotPotData>();
    private List<Long> triggerTimeList = new ArrayList<Long>();
    private LongSparseArray<List<SceneData>> eventArray = new LongSparseArray<List<SceneData>>();
    private int videoWidth;
    private int videoHeight;
    private long eventTriggerTime;
    private long startTime;
    private long endTime;

    private ConstraintLayout rootView;
    private TextureView textureView;
    private MediaPlayer mMediaPlayer;
    private Surface mediaSurface;
    private String dataSource;
    private boolean isPrepared;

    private Map<String, Boolean> audioStateMap = new HashMap<String, Boolean>();
    private String currentAudioSource;
    private int type;
    private Timer timer;
    private TimerTask timerTask;
    private float scale = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            sceneData = arguments.getParcelable("data");
            if (sceneData != null) {
                dataSource = sceneData.getFileUrl();
                renderMode = sceneData.getRenderMode();
                sceneId = sceneData.getSceneId();
                if (Constants.value_isCMCC) {
                    getResUrl();
                }
            }
        }
        sceneList = ((CreatorPlayerActivity) context).getSceneDataList();
        needDividedBy1000 = hotPotDataManager.needDividedBy1000();
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        rootView = view.findViewById(R.id.root_view);
        textureView = view.findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(this);
        startTimer();
        return view;
    }

    private void getResUrl() {
        retrofitHelper.getResUrl(dataSource, new RetrofitHelper.OnGetResUrlListener() {
            @Override
            public void onSuccess(String playUrl) {
                dataSource = playUrl;
                if (textureView != null && textureView.isAvailable()) {
                    startPlay();
                }
            }

            @Override
            public void onFailure() {
                ((CreatorPlayerActivity) context).loadFailed();
            }
        });
    }

    private void startPlay() {
        if (TextUtils.isEmpty(dataSource)) {
            return;
        }
        release();
        try {
            if (Constants.firstSceneId == sceneId && Constants.isFirstOpened) {
                ((CreatorPlayerActivity) context).showLoading();
                Constants.isFirstOpened = false;
            } else {
                ((CreatorPlayerActivity) context).showTip();
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(mediaSurface);
            mMediaPlayer.setDataSource(dataSource);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPrepared = true;
                    ((CreatorPlayerActivity) context).hideLoading();
                    setFocusedView(null);
                    play();
                    loadHotPots();
                    loadEvents(sceneId);
//                    f();
                    Logger.e("onPrepared");
                }
            });
            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        ((CreatorPlayerActivity) context).showLoading(getString(R.string.buffering));
                        return true;
                    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        ((CreatorPlayerActivity) context).hideLoading();
                        return true;
                    }
                    return false;
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    ((CreatorPlayerActivity) context).loadFailed();
                    return true;
                }
            });

            mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    int screenWidth = Constants.screen_width;
                    int screenHeight = Constants.screen_height;
                    resetSize(screenWidth, screenHeight, width, height);
                    Logger.e("onVideoSizeChanged: " + width + ", " + height);
                    if (width > Constants.screen_width) {
                        scale = width * 1f / Constants.screen_width;
                    } else {
                        scale = 1;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
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

                final com.nibiru.creator.data.Vec2 coordinate = Utils.getScreenCoord(position, videoWidth, videoHeight); // 热点中心点
                final ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
                layoutParams.topToTop = ConstraintSet.PARENT_ID;
                final float[] size = {hotPotWidth, hotPotHeight};
//                layout.setScaleX(scale);
//                layout.setScaleY(scale);
                rootView.addView(layout, layoutParams);
                layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                layout.setVisibility(View.GONE);
                layout.post(new Runnable() {
                    @Override
                    public void run() {
                        int layoutWidth = layout.getMeasuredWidth();
                        int layoutHeight = layout.getMeasuredHeight();
                        if (renderMode == SceneRenderMode.SceneType_VR_VALUE) {
                            double[] result = Utils.millierConvertion(videoWidth, videoHeight, position.getMY(), position.getMX());
                            layoutParams.leftMargin = (int) (result[0] - videoWidth / 2f - (layoutWidth > size[0] ? layoutWidth / 2f : size[0] / 2));
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

                // 热点开始时间
                long startTime = 0;
                if (hotPot.hasMStartTime()) {
                    startTime = hotPot.getMStartTime();
                    Logger.e("startTime: " + startTime);
                }
                // 热点结束时间
                long endTime = 0;
                if (hotPot.hasMEndTime()) {
                    endTime = hotPot.getMEndTime();
                    Logger.e("endTime: " + endTime);
                }
                NpvHotPotData npvHotPotData = new NpvHotPotData();
                npvHotPotData.setStartTime(needDividedBy1000 ? startTime : startTime * 1000);
                if (endTime < 0) {
                    npvHotPotData.setEndTime(getDuration());
                } else {
                    npvHotPotData.setEndTime(needDividedBy1000 ? endTime : endTime * 1000);
                }
                npvHotPotData.setHotpotActor(layout);
                hotPotDataList.add(npvHotPotData);

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
                                    if (!((CreatorPlayerActivity) context).isFinishing()) {
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
        /*AnimatorSet animatorSet = new AnimatorSet();
        v.setPivotX(v.getWidth() / 2f);
        v.setPivotY(v.getHeight() / 2f);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(v, "scaleX", 1, 1.3f).setDuration(200),
                ObjectAnimator.ofFloat(v, "scaleY", 1, 1.3f).setDuration(200));
        animatorSet.start();*/

        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation animation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animationSet.addAnimation(animation);
        animationSet.setFillAfter(true);
        v.clearAnimation();
        v.startAnimation(animationSet);
    }

    private void scaleDown(View v) {
        /*AnimatorSet animatorSet = new AnimatorSet();
        v.setPivotX(v.getWidth() / 2f);
        v.setPivotY(v.getHeight() / 2f);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(v, "scaleX", 1.3f, 1).setDuration(200),
                ObjectAnimator.ofFloat(v, "scaleY", 1.3f, 1).setDuration(200));
        animatorSet.start();*/
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation animation = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(200);
        animation.setFillAfter(true);
        animationSet.addAnimation(animation);
        animationSet.setFillAfter(true);
        v.clearAnimation();
        v.startAnimation(animationSet);
    }

    private void loadEvents(int sceneId) {
        List<Event> events = hotPotDataManager.getEvents(sceneId);
        if (events != null && events.size() > 0) {
            triggerTimeList.clear();
            for (Event event : events) {
                long startTime = needDividedBy1000 ? event.getMTriggerTime() / 1000 : event.getMTriggerTime();
                triggerTimeList.add(startTime);
                List<Integer> nextSceneIdsList = event.getMNextSceneIdsList();
                List<SceneData> tempSceneList = new ArrayList<SceneData>();
                if (nextSceneIdsList != null && nextSceneIdsList.size() > 0) {
                    for (Integer nextSceneId : nextSceneIdsList) {
                        if (sceneList != null && sceneList.size() > 0) {
                            for (SceneData sceneData : sceneList) {
                                if (nextSceneId == sceneData.getSceneId()) {

                                    if (event.hasStartTime()) {
                                        sceneData.setStartTime(event.getStartTime());
                                    }

                                    tempSceneList.add(sceneData);
                                    if (sceneData.getSceneType() == 2) {
//                                        loadEventHotpot(event);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (tempSceneList.size() > 0) {
                    eventArray.put(startTime, tempSceneList);
                }
            }
        }
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    ((CreatorPlayerActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateHotPotState();
                        }
                    });
                }
            };
        }
        timer.schedule(timerTask, 0, 100);
    }

    private void updateHotPotState() {
        if (isPrepared && isPlaying()) {
            int currentPos = getCurrentPosition();
            int duration = getDuration();
            if (hotPotDataList != null && hotPotDataList.size() > 0) {
                for (NpvHotPotData hotPotData : hotPotDataList) {
                    final View actor = hotPotData.getHotpotActor();

                    long startTime = hotPotData.getStartTime();
                    long endTime = hotPotData.getEndTime();
                    if (currentPos < 0) {
                        currentPos = 0;
                    }
                    if (currentPos > duration) {
                        currentPos = duration;
                    }
                    if (currentPos >= startTime && currentPos <= endTime) {
                        if (actor.getVisibility() != View.VISIBLE) {
                            ((CreatorPlayerActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    actor.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    } else {
                        if (actor.getVisibility() == View.VISIBLE) {
                            ((CreatorPlayerActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    actor.setAnimation(null);
                                    actor.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            }
        }
        updateEventState();
    }

    private void updateEventState() {
        if (isPrepared && isPlaying()) {
            long currentTime = System.currentTimeMillis();
            int currentPos = getCurrentPosition();
            int size = eventArray.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    long key = eventArray.keyAt(i);
                    Logger.e("updateEventState key = " + key + "  currentPos = " + currentPos + ",startTime = " + startTime + ",eventTriggerTime = " + eventTriggerTime);
                    if (key == startTime + currentPos / 1000 && eventTriggerTime != key) {
                        List<SceneData> list = eventArray.get(key);
                        eventTriggerTime = key;
                        pause();
                        if (list != null) {
                            int listSize = list.size();
                            if (listSize == 0) {

                            } else if (listSize == 1) {
                                SceneData sceneData = list.get(0);
                                Logger.e("sceneData sceneDataDre = " + sceneData.getFileName());
                                if (sceneData.getSceneType() == 2) {
                                    SelectorActivity.jumpToSelectorForResult(getActivity(), sceneData.getSceneId());
                                } else {
                                    switchScene(sceneData);
                                }
                            }
                        }
                    }
                }
            }
        }
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
                            switchScene(sceneData);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void switchScene(final SceneData sceneData) {
        if (sceneData.getSceneType() == 0) {
            reset();
            ((CreatorPlayerActivity) context).changeFrag(PhotoFragment.class, sceneData);
        } else {
            reset();
            dataSource = sceneData.getFileUrl();
            renderMode = sceneData.getRenderMode();
            sceneId = sceneData.getSceneId();
            if (Constants.value_isCMCC) {
                getResUrl();
            } else {
                startPlay();
            }
        }
    }

    @Override
    public void updateUI(SceneData sceneData) {
        if (sceneData != null) {
            reset();
            dataSource = sceneData.getFileUrl();
            renderMode = sceneData.getRenderMode();
            sceneId = sceneData.getSceneId();
            if (Constants.value_isCMCC) {
                getResUrl();
            } else {
                startPlay();
            }
        }
    }

    private void reset() {
        release();
        renderMode = SceneRenderMode.SceneType_VR_VALUE;
        isPrepared = false;
        if (hotPotActors != null && hotPotActors.size() > 0) {
            for (View v : hotPotActors) {
                rootView.removeView(v);
            }
            hotPotActors.clear();
        }
        if (hotPotDataList != null) {
            hotPotDataList.clear();
        }
        if (eventArray != null) {
            eventArray.clear();
        }
        eventTriggerTime = 0;
        if (audioStateMap != null) {
            audioStateMap.clear();
        }
        currentAudioSource = null;
        type = 0;
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

    private void resetSize(int screenWidth, int screenHeight, int width, int height) {
        if (textureView == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
        int videoHeight = screenHeight;
        int videoWidth = (int) (screenHeight * (width * 1.0f / height));

        if (videoWidth > screenWidth) {
            videoWidth = screenWidth;
            videoHeight = (int) (videoWidth / (width * 1.0f / height));
        }
        layoutParams.height = videoHeight;
        layoutParams.width = videoWidth;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        textureView.setLayoutParams(layoutParams);

        layoutParams = rootView.getLayoutParams();
        layoutParams.width = videoWidth;
        layoutParams.height = videoHeight;
        rootView.setLayoutParams(layoutParams);
    }

    private void play() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void pause() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.pause();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPlaying() {
        if (mMediaPlayer != null) {
            try {
                return mMediaPlayer.isPlaying();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void seekTo(int msec) {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.seekTo(msec);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer == null) {
            return 0;
        }
        return mMediaPlayer.getCurrentPosition();
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mediaSurface = new Surface(surface);
        if (dataSource != null && (dataSource.startsWith("http") || dataSource.startsWith("https"))) {
            startPlay();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mediaSurface = null;
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (type == TYPE_AUDIO) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.pause();
            }
        }

        if (isPlaying()) {
            pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (type == TYPE_AUDIO) {
            if (!audioPlayer.isPlaying()) {
                audioPlayer.start();
            }
        }

        if (!isPlaying()) {
            play();
        }
    }
}
