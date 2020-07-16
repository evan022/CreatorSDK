package com.nibiru.creator.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.nibiru.creator.R;
import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.data.ImageListData;
import com.nibiru.creator.network.RetrofitHelper;
import com.nibiru.creator.utils.Constants;
import com.nibiru.creator.utils.DataUtils;
import com.nibiru.creator.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ImageListActivity extends BaseHotActivity implements View.OnClickListener {
    private List<ImageView> imageViews;
    private ViewPager imageViewpager;
    private ImagePagerAdapter pagerAdapter;
    private ImageButton leftBtn;
    private ImageButton rightBtn;
    private Button closeBtn;
    private int currentImageIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        initViews();

        if (hotPot != null) {
            showImageList(hotPot);
        }
    }

    private void initViews() {
        imageViewpager = findViewById(R.id.viewpager);
        closeBtn = findViewById(R.id.close_btn);
        leftBtn = findViewById(R.id.left_btn);
        rightBtn = findViewById(R.id.right_btn);
        closeBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
    }

    public void showImageList(HotPot hotPot) {
        ImageListData imageListData = DataUtils.getImageListData(hotPot);
        if (imageListData != null) {
            if (imageViews == null) {
                imageViews = new ArrayList<>();
            }
            imageViews.clear();
            List<String> pathList = imageListData.getPathList();
            if (pathList != null && pathList.size() > 0) {
                int size = pathList.size();
                if (size == 1) {
                    leftBtn.setVisibility(View.GONE);
                    rightBtn.setVisibility(View.GONE);
                } else {
                    leftBtn.setVisibility(View.VISIBLE);
                    rightBtn.setVisibility(View.VISIBLE);
                    leftBtn.setFocusable(false);
                    rightBtn.setFocusable(false);
                }
                imageViewpager.setFocusable(false);
                resetSize(imageViewpager, imageListData.getImageWidth(), imageListData.getImageHeight());
                for (final String path : pathList) {
                    final ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageViews.add(imageView);
                    if (Constants.value_isCMCC) {
                        retrofitHelper.getResUrl(path, new RetrofitHelper.OnGetResUrlListener() {
                            @Override
                            public void onSuccess(String playUrl) {
                                Glide.with(ImageListActivity.this).load(playUrl).into(imageView);
                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                    } else {
                        Glide.with(this).load(path).into(imageView);
                    }
                }
            }
            pagerAdapter = new ImagePagerAdapter(this, imageViews);
            imageViewpager.setAdapter(pagerAdapter);
            imageViewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    currentImageIndex = position;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.close_btn) {
            finish();
        } else if (id == R.id.left_btn) {
            pageLeft();
        } else if (id == R.id.right_btn) {
            pageRight();
        }
    }

    private void pageRight() {
        if (currentImageIndex < imageViews.size() - 1) {
            imageViewpager.setCurrentItem(currentImageIndex + 1);
        } else {
            ToastUtil.showToast(this, getString(R.string.last_page));
        }
    }

    private void pageLeft() {
        if (currentImageIndex > 0) {
            imageViewpager.setCurrentItem(currentImageIndex - 1);
        } else {
            ToastUtil.showToast(this, getString(R.string.first_page));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            pageRight();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            pageLeft();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private Context context;
        private List<ImageView> imageList;

        public ImagePagerAdapter(Context context, List<ImageView> imageList) {
            this.context = context;
            this.imageList = imageList;
        }

        @Override
        public int getCount() {
            return imageList == null ? 0 : imageList.size();
        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = imageList.get(position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
