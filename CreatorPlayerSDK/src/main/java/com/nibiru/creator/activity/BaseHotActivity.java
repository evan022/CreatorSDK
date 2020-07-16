package com.nibiru.creator.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.utils.Constants;

public class BaseHotActivity extends BaseActivity {

    public static void start(Activity context, Class<?> cls, HotPot hotPot) {
        Intent intent = new Intent();
        intent.putExtra("data", hotPot);
        intent.setClass(context, cls);
        context.startActivity(intent);
        context.overridePendingTransition(0, 0);
    }

    protected HotPot hotPot;
    protected RetrofitHelper retrofitHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retrofitHelper = RetrofitHelper.getInstance();
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            if (intent.getExtras().containsKey("data")) {
                hotPot = (HotPot) intent.getExtras().getSerializable("data");
            }
        }
        resetWindow();
    }

    private void resetWindow() {
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = Constants.screen_width;
        lp.height = Constants.screen_height;
        window.setAttributes(lp);
    }

    public void resetSize(View view, float width, float height) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        float viewHeight = Constants.screen_height * 0.8f;
        float viewWidth = viewHeight * (width / height);
        layoutParams.width = (int) viewWidth;
        layoutParams.height = (int) viewHeight;
        view.setLayoutParams(layoutParams);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
