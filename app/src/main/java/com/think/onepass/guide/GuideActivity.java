package com.think.onepass.guide;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.think.onepass.R;
import com.think.onepass.util.SharePreferenceUtils;

public class GuideActivity extends AppCompatActivity {
    private static final String TAG = "GuideActivity";
    private ViewPager mViewPager;
    private TextView firstDot,secondDot,thirdDot;
    private static final int[] Image_id = new int[]{R.mipmap.guide_page_one_image
            ,R.mipmap.guide_page_second_image,R.mipmap.guide_page_third_image};
    private static final int[] TextTop_id = new int[]{R.string.main_guide_text_top_first
            ,R.string.main_guide_text_top_second,R.string.main_guide_text_top_third};
    private static final int[] TextBottom_id = new int[]{R.string.main_guide_text_bottom_first
            ,R.string.main_guide_text_bottom_second,R.string.main_guide_text_bottom_third};
    private static final int PAGE_COUNT = Image_id.length;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_guide);
        mViewPager = findViewById(R.id.main_guide_vp);
        firstDot = findViewById(R.id.guide_dot_1);
        secondDot = findViewById(R.id.guide_dot_2);
        thirdDot = findViewById(R.id.guide_dot_3);
        firstDot.setBackground(getDrawable(R.drawable.dot_green));
        secondDot.setBackground(getDrawable(R.drawable.dot_white));
        thirdDot.setBackground(getDrawable(R.drawable.dot_white));
        PagerAdapter pagerAdapter = new GuidePagerAdapter();
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case 0:
                        firstDot.setBackground(getDrawable(R.drawable.dot_green));
                        secondDot.setBackground(getDrawable(R.drawable.dot_white));
                        thirdDot.setBackground(getDrawable(R.drawable.dot_white));
                        break;
                    case 1:
                        firstDot.setBackground(getDrawable(R.drawable.dot_white));
                        secondDot.setBackground(getDrawable(R.drawable.dot_green));
                        thirdDot.setBackground(getDrawable(R.drawable.dot_white));
                        break;
                    case 2:
                        firstDot.setBackground(getDrawable(R.drawable.dot_white));
                        secondDot.setBackground(getDrawable(R.drawable.dot_white));
                        thirdDot.setBackground(getDrawable(R.drawable.dot_green));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private class GuidePagerAdapter extends PagerAdapter{
        public GuidePagerAdapter() {
            super();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.d(TAG, "instantiateItem: ");
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.main_guide_page,null);
            container.addView(view);
            ImageView imageView = view.findViewById(R.id.main_guide_page_iv);
            TextView topTv = view.findViewById(R.id.main_guide_page_tv_top);
            TextView bottomTv = view.findViewById(R.id.main_guide_page_tv_bottom);
            final Button button = view.findViewById(R.id.main_guide_page_bt);
            imageView.setImageResource(Image_id[position]);
            topTv.setText(TextTop_id[position]);
            bottomTv.setText(TextBottom_id[position]);
            if(position == getCount()-1){
                button.setVisibility(View.VISIBLE);
            }else {
                button.setVisibility(View.INVISIBLE);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharePreferenceUtils.setGuideMain(true);
                    finish();
                }
            });
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            int currentItem = mViewPager.getCurrentItem();
            if (currentItem > 0) {
                mViewPager.setCurrentItem(currentItem - 1);
            }else {
                moveTaskToBack(true);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
