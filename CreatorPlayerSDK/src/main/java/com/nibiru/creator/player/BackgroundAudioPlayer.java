package com.nibiru.creator.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.IOException;

public class BackgroundAudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {
    // 用于播放应用于某个场景的背景音乐
    private MediaPlayer backgroundMediaPlayer;
    // 用于播放应用于全部场景的背景音乐
    private MediaPlayer allBackgroundMediaPlayer;

    private boolean isAllBackgroundAudioPlayed;
    private boolean isAllBackgroundAudioCompletion;
    private boolean isBackgroundAudioCompletion;

    public BackgroundAudioPlayer() {

    }

    public void play(String path, int playType) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        reset();
        isBackgroundAudioCompletion = false;
        try {
            backgroundMediaPlayer.setDataSource(path);
            backgroundMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            backgroundMediaPlayer.setLooping(playType == -1);
            backgroundMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playAll(String path, int playType) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (!isAllBackgroundAudioPlayed) {
            isAllBackgroundAudioCompletion = false;
            resetAll();
            try {
                allBackgroundMediaPlayer.setDataSource(path);
                allBackgroundMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                allBackgroundMediaPlayer.setLooping(playType == -1);
                allBackgroundMediaPlayer.prepareAsync();
                isAllBackgroundAudioPlayed = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (!isAllBackgroundAudioCompletion) {
                startAll();
            }
        }
    }

    public void pause() {
        if (backgroundMediaPlayer != null) {
            try {
                if (backgroundMediaPlayer.isPlaying()) {
                    backgroundMediaPlayer.pause();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void pauseAll() {
        if (allBackgroundMediaPlayer != null) {
            try {
                if (allBackgroundMediaPlayer.isPlaying()) {
                    allBackgroundMediaPlayer.pause();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (backgroundMediaPlayer != null) {
            try {
                if (!backgroundMediaPlayer.isPlaying() && !isBackgroundAudioCompletion) {
                    backgroundMediaPlayer.start();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (backgroundMediaPlayer != null) {
            try {
                backgroundMediaPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void startAll() {
        if (allBackgroundMediaPlayer != null) {
            try {
                if (!allBackgroundMediaPlayer.isPlaying() && !isAllBackgroundAudioCompletion) {
                    allBackgroundMediaPlayer.start();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopAll() {
        if (allBackgroundMediaPlayer != null) {
            try {
                allBackgroundMediaPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPlaying() {
        if (backgroundMediaPlayer != null) {
            try {
                return backgroundMediaPlayer.isPlaying();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isAllPlaying() {
        if (allBackgroundMediaPlayer != null) {
            try {
                return allBackgroundMediaPlayer.isPlaying();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void reset() {
        if (backgroundMediaPlayer != null) {
            backgroundMediaPlayer.stop();
            backgroundMediaPlayer.release();
            backgroundMediaPlayer = null;
        }
        backgroundMediaPlayer = new MediaPlayer();
        backgroundMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                start();
            }
        });
        backgroundMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isBackgroundAudioCompletion = true;
            }
        });
    }

    private void resetAll() {
        if (allBackgroundMediaPlayer != null) {
            allBackgroundMediaPlayer.stop();
            allBackgroundMediaPlayer.release();
            allBackgroundMediaPlayer = null;
        }
        allBackgroundMediaPlayer = new MediaPlayer();
        allBackgroundMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startAll();
            }
        });
        allBackgroundMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isAllBackgroundAudioCompletion = true;
            }
        });
    }

    public void release() {
        if (backgroundMediaPlayer != null) {
            backgroundMediaPlayer.stop();
            backgroundMediaPlayer.release();
            backgroundMediaPlayer = null;
        }
    }

    public void releaseAll() {
        if (allBackgroundMediaPlayer != null) {
            allBackgroundMediaPlayer.stop();
            allBackgroundMediaPlayer.release();
            allBackgroundMediaPlayer = null;
        }
    }

    public void setAllBackgroundAudioPlayed(boolean allBackgroundAudioPlayed) {
        isAllBackgroundAudioPlayed = allBackgroundAudioPlayed;
    }

    public boolean isAllBackgroundAudioPlayed() {
        return isAllBackgroundAudioPlayed;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        isAllBackgroundAudioCompletion = true;
    }


    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
