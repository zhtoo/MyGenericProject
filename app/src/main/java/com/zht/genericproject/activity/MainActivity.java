package com.zht.genericproject.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;
import com.zht.genericproject.fragment.AllFragment;
import com.zht.genericproject.fragment.ExploreFragment;
import com.zht.genericproject.fragment.MeFragment;
import com.zht.genericproject.fragment.TweetFragment;

public class MainActivity extends BaseActivity {

    private FragmentTabHost tabhost;
    private FrameLayout fl;
    private ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleBar(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);


        fl = (FrameLayout) findViewById(R.id.fl);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        //1.绑定tabhost和切换Fragment的布局
        tabhost.setup(this, getSupportFragmentManager(), R.id.fl);
        //去掉线
        tabhost.getTabWidget().setDividerDrawable(null);

        //2.创建Tab按钮
        //创建综合界面的按钮
        TabHost.TabSpec allTab = tabhost.newTabSpec("all");
        allTab.setIndicator(getIndicateView(R.drawable.tab_icon_new, "综合"));

        //创建动弹界面的按钮
        TabHost.TabSpec tweetTab = tabhost.newTabSpec("tweet");
        tweetTab.setIndicator(getIndicateView(R.drawable.tab_icon_tweet, "动弹"));

        //创建加号界面的按钮
        TabHost.TabSpec addTab = tabhost.newTabSpec("addTab");
        View addView = getIndicateView(0, "");
        addView.setEnabled(false);//禁用点击事件
        addTab.setIndicator(addView);

        //创建动弹界面的按钮
        TabHost.TabSpec exploreTab = tabhost.newTabSpec("exploreTab");
        exploreTab.setIndicator(getIndicateView(R.drawable.tab_icon_explore, "发现"));

        //创建动弹界面的按钮
        TabHost.TabSpec meTab = tabhost.newTabSpec("meTab");
        meTab.setIndicator(getIndicateView(R.drawable.tab_icon_me, "我"));

        //3.添加Tab按钮以及对应的Fragment
        tabhost.addTab(allTab, AllFragment.class, null);
        tabhost.addTab(tweetTab, TweetFragment.class, null);
        tabhost.addTab(addTab, null, null);
        tabhost.addTab(exploreTab, ExploreFragment.class, null);
        tabhost.addTab(meTab, MeFragment.class, null);


        //给加号添加点击事件
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "弹对话框！", Toast.LENGTH_SHORT).show();
            }
        });



    }


    //为每个tab初始化布局
    private View getIndicateView(int iconResId, String title) {
        View view = View.inflate(this, R.layout.tab_main, null);
        ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);

        iv_image.setBackgroundResource(iconResId);
        tv_title.setText(title);

        return view;
    }


}
