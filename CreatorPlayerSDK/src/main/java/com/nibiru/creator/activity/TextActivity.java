package com.nibiru.creator.activity;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nibiru.creator.R;
import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.data.LabelData;
import com.nibiru.creator.utils.DataUtils;

public class TextActivity extends BaseHotActivity {

    private TextView textTv;
    private ScrollView contentSv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        contentSv = findViewById(R.id.sv);
        textTv = findViewById(R.id.text);
        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (hotPot != null) {
            showText(hotPot);
        }
    }

    public void showText(HotPot hotPot) {
        LabelData labelData = DataUtils.getLabelData(hotPot);
        if (labelData != null) {
            contentSv.setBackgroundColor(labelData.getBgColor());
            textTv.setText(labelData.getTitle());
            contentSv.requestFocus();
            textTv.setTextColor(labelData.getTextColor()); // 设置字体颜色
//            textTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelData.getFontSize() * 2); // 设置字体大小
            // 设置字体样式：加粗、倾斜、下划线
            TextPaint paint = textTv.getPaint();
            paint.setFakeBoldText(labelData.isBold());
            paint.setUnderlineText(labelData.isUnderline());
            if (labelData.isItalic()) {
                paint.setTextSkewX(-0.25f);
            }
        }
    }
}
