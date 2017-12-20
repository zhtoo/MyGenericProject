package com.zht.genericproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;
import com.zht.genericproject.util.ActivityUtils;
import com.zht.genericproject.view.IndicatorView;

/**
 * 作者：zhanghaitao on 2017/12/18 14:58
 * 邮箱：820159571@qq.com
 *
 * @describe:向导页
 */

public class GuideActivity extends BaseActivity {


    private ViewPager mViewPager;
    private IndicatorView mIndicator;

    private int[] mGuidePicture = {
            R.drawable.ic_guide1,
            R.drawable.ic_guide2,
            R.drawable.ic_guide3,
            R.drawable.ic_guide4,
            R.drawable.ic_guide5,
    };
    private Button mAccess;
    private Button mSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        hideTitleBar();
        mViewPager = (ViewPager) findViewById(R.id.guide_view_pager);
        mIndicator = (IndicatorView) findViewById(R.id.guide_pager_indicator);
        mAccess = (Button) findViewById(R.id.guide_access);
        mSkip = (Button) findViewById(R.id.guide_skip);

        //FZXKJW.TTF  方正行楷简体
        Typeface typeFace =Typeface.createFromAsset(getAssets(),"fonts/FZXKJW.TTF");
        //使用字体
        mAccess.setTypeface(typeFace);
        mSkip.setTypeface(typeFace);
        mAccess.setOnClickListener(btClickListener);
        mSkip.setOnClickListener(btClickListener);

        //设置ViewPager适配器
        mViewPager.setAdapter(new GuidePagerAdapter());
        mIndicator.setCount(mGuidePicture.length);

        if(mGuidePicture.length == 1){
            mIndicator.setVisibility(View.GONE);
            mAccess.setVisibility(View.VISIBLE);
        }

        mViewPager.setCurrentItem(0);
         //设置ViewPager的监听器
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mIndicator.setoffset(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            if(position == (mGuidePicture.length-1)){
                mAccess.setVisibility(View.VISIBLE);
            }else {
                mAccess.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
        });

    }


    private  View.OnClickListener btClickListener  = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Activity activity = ActivityUtils.peek();
            Intent intent = new Intent();
            intent.setClass(activity, MainActivity.class);
            ActivityUtils.push(MainActivity.class, intent);
            ActivityUtils.pop(activity);
        }
    };



    private class  GuidePagerAdapter extends PagerAdapter{

        private Context context;
        private LayoutInflater mLayoutInflater;

        public GuidePagerAdapter() {
            this.context = GuideActivity.this;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mGuidePicture.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
          //  View view = mLayoutInflater.inflate(R.layout.item_guide_viewpager, container, false);
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(mGuidePicture[position]);
            //不是用的Glide的原因是Glide在加载图片会有延迟。这样会出现白屏现象。
            /*Glide.with(context)
                    .load(mGuidePicture[position])
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                    //.thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                   // .error(R.drawable.ic_error_pictrue)//加载错误的图片
                    .into(new GlideDrawableImageViewTarget(imageView) {
                        //加载开始
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                        }

                        //加载失败
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                        }

                        //加载成功
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                        }
                    });*/
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }


}
