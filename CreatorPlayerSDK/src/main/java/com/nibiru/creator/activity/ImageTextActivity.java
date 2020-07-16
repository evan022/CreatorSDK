package com.nibiru.creator.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.nibiru.creator.R;
import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.data.ImageTextData;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.utils.Constants;
import com.nibiru.creator.utils.DataUtils;

public class ImageTextActivity extends BaseHotActivity {
    private ConstraintLayout imageTextLayout;
    private ScrollView imageTextSv;
    private ImageView imageTextIv;
    private TextView imageTextTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_text);

        initViews();

        if (hotPot != null) {
            showImageText(hotPot);
        }
    }

    private void initViews() {
        imageTextLayout = findViewById(R.id.image_text_layout);
        imageTextSv = findViewById(R.id.image_text_sv);
        imageTextIv = findViewById(R.id.image_text_iv);
        imageTextTv = findViewById(R.id.image_text_tv);
        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showImageText(HotPot hotPot) {
        final ImageTextData imageTextData = DataUtils.getImageTextData(hotPot);
        if (imageTextData != null) {
            resetSize(imageTextLayout, imageTextData.getWidth(), imageTextData.getHeight());
            if (Constants.value_isCMCC) {
                retrofitHelper.getResUrl(imageTextData.getImagePath(), new RetrofitHelper.OnGetResUrlListener() {
                    @Override
                    public void onSuccess(String playUrl) {
                        Glide.with(ImageTextActivity.this).load(playUrl).into(imageTextIv);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            } else {
                Glide.with(this).load(imageTextData.getImagePath()).into(imageTextIv);
            }
            imageTextSv.setBackgroundColor(Color.argb(140, 255, 255, 255));
            imageTextSv.requestFocus();
            imageTextTv.setText(imageTextData.getText());
            imageTextTv.setTextColor(imageTextData.getTextColor()); // 设置字体颜色
//            textTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelData.getFontSize() * 2); // 设置字体大小
            // 设置字体样式：加粗、倾斜、下划线
            TextPaint paint = imageTextTv.getPaint();
            paint.setFakeBoldText(imageTextData.isBold());
            paint.setUnderlineText(imageTextData.isUnderline());
            if (imageTextData.isItalic()) {
                paint.setTextSkewX(-0.25f);
            }
        }
    }
}
