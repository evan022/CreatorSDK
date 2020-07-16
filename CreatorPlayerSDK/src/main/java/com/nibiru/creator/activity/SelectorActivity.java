package com.nibiru.creator.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.nibiru.creator.R;
import com.nibiru.creator.data.HotPotData.Component;
import com.nibiru.creator.data.HotPotData.Font;
import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.data.HotPotData.SkinSet;
import com.nibiru.creator.data.HotPotData.SkyBoxSceneAttribute;
import com.nibiru.creator.data.HotPotData.Vec2;
import com.nibiru.creator.data.HotPotData.Vec4;
import com.nibiru.creator.data.manager.HotPotDataManager;
import com.nibiru.creator.utils.BitmapUtil;
import com.nibiru.creator.utils.DensityUtils;
import com.nibiru.creator.utils.JumpUtils;
import com.nibiru.creator.utils.Logger;
import com.nibiru.creator.utils.Utils;

import java.util.List;

public class SelectorActivity extends BaseHotActivity {
    public static final String INTENT_SELECTOR = "selector";
    public static final String INTENT_SCENE_ID = "scene_id";
    public static final String INTENT_HOTPOT = "hotpot";
    private FrameLayout selectorPanel;
    private int sceneId;
    public static int REQUEST_CODE_SELECT = 1001;

    public static void jumpToSelectorForResult(Activity context, int sceneId) {
        Intent intent = new Intent(context, SelectorActivity.class);
        intent.putExtra(INTENT_SELECTOR, sceneId);
        context.startActivityForResult(intent, REQUEST_CODE_SELECT);
        context.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);

        initViews();
        if (getIntent() != null) {
            sceneId = getIntent().getIntExtra(INTENT_SELECTOR, -1);
            if (sceneId != -1) {
                loadComponent(sceneId);
            }
        }
    }

    private void initViews() {
        selectorPanel = findViewById(R.id.selector_panel);
    }

    private void loadComponent(int sceneId) {
        SkyBoxSceneAttribute sceneAttribute = HotPotDataManager.getInstance().getSkyBoxSceneAttribute(sceneId);
        if (sceneAttribute != null) {
            int skinSetCount = sceneAttribute.getMSkinSetCount();
            if (skinSetCount > 0) {
                SkinSet skinSet = sceneAttribute.getMSkinSet(0);
                float scale = 1;
                float scaleX = 1;
                float scaleY = 1;
                int width = DensityUtils.dp2px(this, skinSet.getMWidth());
                int height = DensityUtils.dp2px(this, skinSet.getMHeight());
                int frameWidth = DensityUtils.getScreenWidth(this);
                int frameHeight = DensityUtils.getScreenHeight(this);
                if (width > frameWidth && height < frameHeight) {
                    scaleX = (float) frameWidth / width;
                    scaleY = scaleX;
                } else if (height > frameHeight && width < frameWidth) {
                    scaleY = (float) frameHeight / height;
                    scaleX = scaleY;
                } else if (width > frameWidth && height > frameHeight) {
                    scaleX = (float) frameWidth / width;
                    scaleY = (float) frameHeight / height;
                }
                if (scaleX <= scaleY) {
                    scale = scaleX;
                } else {
                    scale = scaleY;
                }

                final float bgOpacity = skinSet.getMOpacity();
                Vec4 color = skinSet.getMColor();
                int bgRed = (int) color.getMX();
                int bgGreen = (int) color.getMY();
                int bgBlue = (int) color.getMZ();
                int bgAlpha = (int) (bgOpacity * 255 / 100f);
                int bgColor = Color.argb(bgAlpha, bgRed, bgGreen, bgBlue);

                ViewGroup.LayoutParams layoutParams = selectorPanel.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.width = (int) (width * scale);
                    layoutParams.height = (int) (height * scale);
                    selectorPanel.setLayoutParams(layoutParams);
                }
                // 如果选择器存在背景图的话优先加载背景图
                boolean hasBg = false; // 是否存在自定义背景图
                Bitmap bgBitmap = null;
                if (skinSet.hasMUserDefineFilePath()) {
                    String bgPath = skinSet.getMFileUrl();
                    if (!TextUtils.isEmpty(bgPath)) {
                        hasBg = true;
                        Glide.with(this).load(bgPath).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                selectorPanel.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapUtil.getRoundedCornerBitmap(BitmapUtil.getTransparentBitmap(resource, (int) bgOpacity),
                                        DensityUtils.dp2px(SelectorActivity.this, 6))));
                            }
                        });
                    } else {
                        bgBitmap = Utils.generateRoundBitmap(width, height, bgColor, DensityUtils.dp2px(this, 6));
                    }
                } else {
                    bgBitmap = Utils.generateRoundBitmap(width, height, bgColor, DensityUtils.dp2px(this, 6));
                }
                if (!hasBg) {
                    selectorPanel.setBackgroundDrawable(new BitmapDrawable(getResources(), bgBitmap));
                }

                List<Component> componentList = sceneAttribute.getMComponentsList();
                if (componentList != null && componentList.size() > 0) {
                    for (Component component : componentList) {
                        int id = component.getMId();
                        int type = component.getMType();
                        Vec2 position = component.getMPosition();
                        Vec2 size = component.getMSize();
                        float opacity = component.getMOpacity();
                        float rotateAngle = component.getMRotateAngle();
                        int jumpSceneId = 0;
                        HotPot hotPot = null;
                        if (component.hasMJumpSceneId()) {
                            jumpSceneId = component.getMJumpSceneId();
                        }
                        if (component.hasMHotspot()) {
                            hotPot = component.getMHotspot();
                        }
                        if (type != 4) {
                            int btnWidth = DensityUtils.dp2px(this, size.getMX() * scale);
                            int btnHeight = DensityUtils.dp2px(this, size.getMY() * scale);
                            Vec4 fillColor = component.getMFillColor();
                            float radiusX = 0;
                            float radiusY = 0;
                            if (component.hasMRadius()) {
                                radiusX = DensityUtils.dp2px(this, component.getMRadius() * scale);
                                radiusY = DensityUtils.dp2px(this, component.getMRadius() * scale);
                            }
                            if (type == 6) {
                                radiusX = DensityUtils.dp2px(this, size.getMX() / 2 * scale);
                                radiusY = DensityUtils.dp2px(this, size.getMY() / 2 * scale);
                            }
                            int btnRed = (int) fillColor.getMX();
                            int btnGreen = (int) fillColor.getMY();
                            int btnBlue = (int) fillColor.getMZ();
                            int btnColor = Color.argb(255, btnRed, btnGreen, btnBlue);

                            TextView textView = null;
                            if (type != 5 && type != 6) {
                                btnColor = Color.TRANSPARENT;
                            } else {
                                Bitmap btnBitmap;
                                if (type == 6) {
                                    btnBitmap = Utils.generateCircleOvalBitmap(btnWidth, btnHeight, btnColor);
                                } else {
                                    btnBitmap = Utils.generateRoundRectBitmap(btnWidth, btnHeight, btnColor, (int) radiusX, (int) radiusY);
                                }
                                textView = new TextView(this);
                                int textPadding = DensityUtils.dp2px(this, 3);
                                int textPaddingTop = DensityUtils.dp2px(this, 1);
                                textView.setPadding(textPadding, textPaddingTop, textPadding, textPadding);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(btnWidth, btnHeight);
                                params.gravity = Gravity.LEFT | Gravity.TOP;
                                params.leftMargin = DensityUtils.dp2px(this, (int) position.getMX() * scale);
                                params.topMargin = DensityUtils.dp2px(this, (int) position.getMY() * scale);
                                textView.setLayoutParams(params);
                                textView.setAlpha(opacity);
                                if (rotateAngle > 0) {
                                    textView.setRotation(rotateAngle);
                                }
                                textView.setBackgroundDrawable(new BitmapDrawable(btnBitmap));
                                textView.setOnClickListener(new ItemClickListener(this, jumpSceneId, hotPot));
                            }

                            if (component.hasMContent()) {
                                String content = component.getMContent();
                                if (component.hasMFont()) {
                                    Font font = component.getMFont();
                                    String textFont = font.getMFontFamily();
                                    int fontSize = DensityUtils.dp2px(this, font.getMFontSize() * scale);
                                    Vec4 fontColor = font.getMFontColor();
                                    int bold = font.getMBold();
                                    int italic = font.getMItalic();
                                    int underline = font.getMUnderline();
                                    int alignment = font.getMAlignment();
                                    int titleColor = Color.argb(255, (int) fontColor.getMX(), (int) fontColor.getMY(), (int) fontColor.getMZ());
                                    float spacingMultiplier = (fontSize * 1.0f + DensityUtils.dp2px(this, font.getMLineHeight() * scale)) / fontSize;
                                    if (type == 5 || type == 6) {
                                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
                                        textView.setTextColor(titleColor);
                                        if (alignment == 1) {
                                            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                                        } else if (alignment == 2) {
                                            textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                                        } else {
                                            textView.setGravity(Gravity.CENTER);
                                        }
                                        if (bold == 1) {
                                            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                                        }
                                        if (italic == 1) {
                                            textView.setTypeface(textView.getTypeface(), Typeface.ITALIC);
                                        }
                                        if (underline == 1) {
                                            if (!TextUtils.isEmpty(content)) {
                                                SpannableString spannableString = new SpannableString(content);
                                                spannableString.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                                                textView.setText(spannableString);
                                            }
                                        }
                                        if (underline != 1) {
                                            textView.setText(content);
                                        }
                                        if (!TextUtils.isEmpty(textFont)) {
//                                            textView.setTypeface(FontCache.get(this, textFont));
                                        }
                                        textView.setFocusable(true);
                                        selectorPanel.addView(textView);
                                        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (hasFocus) {
                                                    scaleUp(v);
                                                } else {
                                                    scaleDown(v);
                                                }
                                            }
                                        });
                                    } else {
                                        Bitmap textBitmap = Utils.getRoundTextBitmap(this, btnWidth, btnHeight, (int) radiusX, (int) radiusY, textFont, content, btnColor, titleColor, fontSize, alignment, spacingMultiplier, bold == 1, italic == 1, underline == 1);
                                        ImageView imageView = new ImageView(this);
                                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(btnWidth, btnHeight);
                                        params.gravity = Gravity.LEFT | Gravity.TOP;
                                        params.leftMargin = DensityUtils.dp2px(this, (int) position.getMX() * scale);
                                        params.topMargin = DensityUtils.dp2px(this, (int) position.getMY() * scale);
                                        imageView.setLayoutParams(params);
                                        imageView.setAlpha(opacity);
                                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                        if (rotateAngle > 0) {
                                            imageView.setRotation(rotateAngle);
                                        }
                                        imageView.setImageBitmap(textBitmap);
                                        imageView.setOnClickListener(new ItemClickListener(this, jumpSceneId, hotPot));
                                        imageView.setFocusable(true);
                                        selectorPanel.addView(imageView);
                                        imageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (hasFocus) {
                                                    scaleUp(v);
                                                } else {
                                                    scaleDown(v);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        } else { // 图片
                            if (component.hasImagePath()) {
//                                String imagePath = component.getImagePath();
                                String imagePath = component.getMFileUrl();
                                int imageWidth = DensityUtils.dp2px(this, size.getMX() * scale);
                                int imageHeight = DensityUtils.dp2px(this, size.getMY() * scale);

                                final ImageView imageView = new ImageView(this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(imageWidth, imageHeight);
                                params.gravity = Gravity.LEFT | Gravity.TOP;
                                params.leftMargin = DensityUtils.dp2px(this, (int) position.getMX() * scale);
                                params.topMargin = DensityUtils.dp2px(this, (int) position.getMY() * scale);
                                imageView.setLayoutParams(params);
                                imageView.setAlpha(opacity);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                if (rotateAngle > 0) {
                                    imageView.setRotation(rotateAngle);
                                }
                                imageView.setOnClickListener(new ItemClickListener(this, jumpSceneId, hotPot));
                                imageView.setFocusable(true);
                                selectorPanel.addView(imageView);

                                Glide.with(this).load(imagePath).asBitmap().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        imageView.setImageBitmap(resource);
                                    }
                                });

                                imageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        if (hasFocus) {
                                            scaleUp(v);
                                        } else {
                                            scaleDown(v);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

    }

    private void scaleUp(View v) {
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

    private static class ItemClickListener implements View.OnClickListener {
        private Activity context;
        //跳转场景的id
        private int sceneId;
        //元件关联的热点
        private HotPot hotPot;

        public ItemClickListener(Activity context, int sceneId, HotPot hotPot) {
            this.context = context;
            this.sceneId = sceneId;
            this.hotPot = hotPot;
        }

        @Override
        public void onClick(View v) {
            if (sceneId != 0) {
                Intent intent = new Intent();
                intent.putExtra(INTENT_SCENE_ID, sceneId);
                context.setResult(RESULT_OK, intent);
                context.finish();
            } else {
                if (hotPot != null) {
                    if (hotPot.hasMModelHotPot()) {
                        /*Intent intent = new Intent();
                        intent.putExtra(INTENT_HOTPOT, hotPot);
                        context.setResult(RESULT_OK, intent);
                        context.finish();*/
                    } else {
                        JumpUtils.jumpToHotPot(context, hotPot);
                    }
                }
            }
        }
    }
}
