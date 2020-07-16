package com.nibiru.creator.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.nibiru.creator.R;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.utils.Constants;
import com.nibiru.creator.utils.Logger;

import java.io.IOException;

public class VideoActivity extends BaseHotActivity implements TextureView.SurfaceTextureListener {

    private LinearLayout loadingLayout;
    private ImageView loadingIv;
    private TextView loadingTv;
    private Button closeBtn;

    private TextureView textureView;
    private SimpleExoPlayer mMediaPlayer;
    private Surface mediaSurface;
    private String dataSource;
    private ObjectAnimator objectAnimator;
    private boolean isPrepared;
    private boolean isVideoPaused;
    private boolean isBuffering;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        if (hotPot != null) {
            dataSource = hotPot.getMVideoImageHotPot().getMFileUrl();
        }
        initViews();
        if (Constants.value_isCMCC) {
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

                }
            });
        }
    }

    private void initViews() {
        loadingLayout = findViewById(R.id.loading_layout);
        loadingIv = findViewById(R.id.loading_iv);
        loadingTv = findViewById(R.id.loading_tv);
        closeBtn = findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textureView = findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(this);
    }

    public void showLoading(String text) {
        loadingLayout.setVisibility(View.VISIBLE);
        if (loadingIv.getVisibility() != View.VISIBLE) {
            loadingIv.setVisibility(View.VISIBLE);
        }
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(loadingIv, "rotation", 0, 360);
            objectAnimator.setDuration(900);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        objectAnimator.start();
        loadingTv.setText(text);
    }

    public void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void loadFailed() {
        loadingIv.setVisibility(View.GONE);
        loadingTv.setText(R.string.load_failed);
    }

    private void startPlay() {
        if (TextUtils.isEmpty(dataSource)) {
            return;
        }
        release();
        showLoading(getString(R.string.loading));
        // 得到默认合适的带宽
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
        // 创建跟踪的工厂
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        // 创建跟踪器
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        // 创建player
        mMediaPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        mMediaPlayer.setVideoSurface(mediaSurface);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayer"), null);
        // 创建要播放的媒体的MediaSource
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(dataSource));
        // 准备播放器的MediaSource
        mMediaPlayer.prepare(mediaSource);

        mMediaPlayer.addListener(new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                if (Player.TIMELINE_CHANGE_REASON_PREPARED == reason) {
                    isPrepared = true;
                    play();
                    hideLoading();
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    isBuffering = true;
                    showLoading(getString(R.string.buffering));
                } else if (playbackState == Player.STATE_READY && playWhenReady) {
                    isBuffering = false;
                    hideLoading();
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                loadFailed();
            }
        });

        mMediaPlayer.addVideoListener(new VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unAppliedRotationDegrees, float pixelWidthHeightRatio) {
                Logger.e("onVideoSizeChanged: " + width + ", " + height);
            }
        });
            /*mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPrepared = true;
                    play();
                }
            });
            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        isBuffering = true;
                        showLoading(getString(R.string.buffering));
                        return true;
                    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        isBuffering = false;
                        hideLoading();
                        return true;
                    }
                    return false;
                }
            });

            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    loadFailed();
                    return true;
                }
            });*/
    }

    private void play() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.setPlayWhenReady(true);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void pause() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.setPlayWhenReady(false);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPlaying() {
        if (mMediaPlayer != null) {
            try {
                return mMediaPlayer.getPlayWhenReady();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void seekTo(long msec) {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.seekTo(msec);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return -1;
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
        release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (closeBtn.hasFocus()) {
                finish();
            } else {
                if (isPrepared && !isBuffering) {
                    if (isPlaying()) {
                        isVideoPaused = true;
                        pause();
                    } else {
                        isVideoPaused = false;
                        play();
                    }
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (isPrepared) {
                long position = mMediaPlayer.getCurrentPosition();
                position -= 5 * 1000;
                if (position < 0) {
                    position = 0;
                }
                seekTo(position);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (isPrepared) {
                long position = mMediaPlayer.getCurrentPosition();
                position += 5 * 1000;
                long duration = getDuration();
                if (position > duration) {
                    position = duration;
                }
                seekTo(position);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying()) {
            pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isVideoPaused) {
            play();
        }
    }
}
