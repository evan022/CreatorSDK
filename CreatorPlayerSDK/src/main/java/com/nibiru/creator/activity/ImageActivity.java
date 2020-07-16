package com.nibiru.creator.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.nibiru.creator.R;
import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.data.ImageData;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.utils.Constants;
import com.nibiru.creator.utils.DataUtils;

public class ImageActivity extends BaseHotActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.image);
        imageView.requestFocus();
        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (hotPot != null) {
            showImage(hotPot);
        }
    }

    public void showImage(HotPot hotPot) {
        final ImageData imageData = DataUtils.getImageData(hotPot);
        if (imageData != null) {
            resetSize(imageView, imageData.getImageWidth(), imageData.getImageHeight());
            if (Constants.value_isCMCC) {
                retrofitHelper.getResUrl(imageData.getImageName(), new RetrofitHelper.OnGetResUrlListener() {
                    @Override
                    public void onSuccess(String playUrl) {
                        Glide.with(ImageActivity.this).load(playUrl).into(imageView);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            } else {
                Glide.with(this).load(imageData.getImageName()).into(imageView);
            }
        }
    }
}
