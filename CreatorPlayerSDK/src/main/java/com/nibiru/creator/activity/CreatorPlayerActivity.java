package com.nibiru.creator.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nibiru.creator.R;
import com.nibiru.creator.data.SceneData;
import com.nibiru.creator.data.manager.HotPotDataManager;
import com.nibiru.creator.fragment.BaseFragment;
import com.nibiru.creator.fragment.PhotoFragment;
import com.nibiru.creator.fragment.VideoFragment;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.utils.Constants;

import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatorPlayerActivity extends BaseActivity {

    private LinearLayout loadingLayout;
    private ImageView loadingIv;
    private TextView loadingTv;
    private TextView enterTv;
    private TextView switchTv;

    private static final Class<?>[] FRAGMENTS = {PhotoFragment.class, VideoFragment.class};
    private FragmentManager mFManager;
    private Fragment mCurrentFragment;

    private RetrofitHelper retrofitHelper;
    private HotPotDataManager hotPotDataManager;
    private List<SceneData> sceneDataList;
    private ObjectAnimator objectAnimator;

    public static void start(Context context, String npkPath) {
        Intent intent = new Intent(context, CreatorPlayerActivity.class);
        intent.putExtra("npkPath", npkPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
//        setCustomDensity();

        retrofitHelper = RetrofitHelper.getInstance();
        hotPotDataManager = HotPotDataManager.getInstance();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Constants.screen_width = displayMetrics.widthPixels;
        Constants.screen_height = displayMetrics.heightPixels;
        Constants.densityDpi = displayMetrics.densityDpi;
//        Logger.e("onCreate: " + Constants.screen_width + ", " + Constants.screen_height + ", " + displayMetrics.densityDpi);

        mFManager = getSupportFragmentManager();

        initViews();

        String npkPath = null;
        Intent intent = getIntent();
        if (intent != null) {
            npkPath = intent.getStringExtra("npkPath");
        }
        if (TextUtils.isEmpty(npkPath)) {
            npkPath = "https://test116.1919game.net:8443/NibiruCustomUpload/files/material/799/cdn1592201126239/npt_1592201126239/0605.npk";
        }
        getNptContent(npkPath);
//        getNptContent("https://test116.1919game.net:8443/NibiruCustomUpload/files/material/799/cdn1592201126239/npt_1592201126239/0605.npk");
    }

    private void setCustomDensity() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float targetDensity = displayMetrics.widthPixels / 640f / 1.4f;
        int targetDensityDpi = (int) (160 * targetDensity);
        displayMetrics.density = displayMetrics.scaledDensity = targetDensity;
        displayMetrics.densityDpi = targetDensityDpi;

        displayMetrics = getApplication().getResources().getDisplayMetrics();
        displayMetrics.density = displayMetrics.scaledDensity = targetDensity;
        displayMetrics.densityDpi = targetDensityDpi;
    }

    private void initViews() {
        loadingLayout = findViewById(R.id.loading_layout);
        loadingIv = findViewById(R.id.loading_iv);
        loadingTv = findViewById(R.id.loading_tv);
        enterTv = findViewById(R.id.enter_tv);
        switchTv = findViewById(R.id.switch_tv);
    }

    public void showLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*loadingLayout.setVisibility(View.VISIBLE);
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
                if (loadingTv.getText() != getString(R.string.loading)) {
                    loadingTv.setText(R.string.loading);
                }*/
                loadingLayout.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(Constants.projectName)) {
                    enterTv.setText("正在打开 " + Constants.projectName);
                } else {
                    enterTv.setText(R.string.tip);
                }
                enterTv.setVisibility(View.VISIBLE);
                switchTv.setVisibility(View.GONE);
            }
        });
    }

    public void showLoading(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*loadingLayout.setVisibility(View.VISIBLE);
                if (loadingIv.getVisibility() != View.VISIBLE) {
                    loadingIv.setVisibility(View.VISIBLE);
                }
                if (objectAnimator == null) {
                    objectAnimator = ObjectAnimator.ofFloat(loadingIv, "rotation", 0, 360);
                    objectAnimator.setDuration(900);
                    objectAnimator.setInterpolator(new LinearInterpolator());
                    objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
                }
                objectAnimator.start();*/
                loadingLayout.setVisibility(View.GONE);
                enterTv.setVisibility(View.GONE);
                switchTv.setVisibility(View.VISIBLE);
                switchTv.setText(text);
            }
        });
    }

    public void showTip() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingLayout.setVisibility(View.GONE);
                enterTv.setVisibility(View.GONE);
                switchTv.setText(R.string.switch_tip);
                switchTv.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingLayout.setVisibility(View.GONE);
                if (objectAnimator != null) {
                    objectAnimator.cancel();
                }
                enterTv.setVisibility(View.GONE);
                switchTv.setVisibility(View.GONE);
            }
        });
    }

    public void loadFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingIv.setVisibility(View.GONE);
                loadingTv.setText(R.string.load_failed);
            }
        });
    }

    private void getNptContent(String urlStr) {
        if (TextUtils.isEmpty(urlStr)) {
            return;
        }
        try {
            URL url = new URL(urlStr);
            String path = url.getPath();
            int index = path.lastIndexOf("/");
            if (index != -1) {
                String name = path.substring(index + 1);
                index = name.lastIndexOf(".");
                if (index != -1) {
                    name = name.substring(0, index);
                    name = URLDecoder.decode(name, "UTF-8");
                    Constants.projectName = name;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showLoading();
        retrofitHelper.getNptContent(urlStr, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    hotPotDataManager.setDataStream(response.body().byteStream());
                    sceneDataList = hotPotDataManager.getSceneDataList();
                    if (sceneDataList != null && sceneDataList.size() > 0) {
                        for (SceneData sceneData : sceneDataList) {
                            if (sceneData.isPrimaryScene()) {
                                Constants.firstSceneId = sceneData.getSceneId();
                                Constants.isFirstOpened = true;
                                if (sceneData.getSceneType() == 0) {
                                    changeFrag(PhotoFragment.class, sceneData);
                                } else {
                                    changeFrag(VideoFragment.class, sceneData);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadFailed();
            }
        });
    }

    public void changeFrag(Class<?> fragmentClazz, SceneData sceneData) {
        FragmentTransaction transaction = mFManager.beginTransaction();
        for (Class<?> fc : FRAGMENTS) {
            if (fc == fragmentClazz) {
                continue;
            }
            Fragment fragment = mFManager.findFragmentByTag(fc.getCanonicalName());
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }

        Fragment lastFragment = mFManager.findFragmentByTag(fragmentClazz.getCanonicalName());
        if (lastFragment == null) {
            try {
                lastFragment = (Fragment) fragmentClazz.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", sceneData);
                lastFragment.setArguments(bundle);
                transaction.add(R.id.container, lastFragment, fragmentClazz.getCanonicalName());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            ((BaseFragment) lastFragment).updateUI(sceneData);
            transaction.show(lastFragment);
        }
        transaction.commit();
        mCurrentFragment = lastFragment;
    }

    public List<SceneData> getSceneDataList() {
        return sceneDataList;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (mCurrentFragment instanceof BaseFragment) {
                View focusedView = ((BaseFragment) mCurrentFragment).getFocusedView();
                if (focusedView != null) {
                    focusedView.performClick();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
