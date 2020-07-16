package com.nibiru.creator.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nibiru.creator.data.SceneData;
import com.nibiru.creator.data.manager.HotPotDataManager;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.player.AudioPlayer;

public class BaseFragment extends Fragment {
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 2;
    protected Context context;
    protected HotPotDataManager hotPotDataManager;
    protected AudioPlayer audioPlayer;
    private View focusedView;
    protected RetrofitHelper retrofitHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hotPotDataManager = HotPotDataManager.getInstance();
        audioPlayer = new AudioPlayer();
        retrofitHelper = RetrofitHelper.getInstance();
    }

    public View getFocusedView() {
        return focusedView;
    }

    public void setFocusedView(View focusedView) {
        this.focusedView = focusedView;
    }

    public void updateUI(SceneData sceneData) {

    }
}
