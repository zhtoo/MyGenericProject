package com.zht.genericproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseFragment;
import com.zht.genericproject.util.StatusBarUtils;
import com.zht.genericproject.view.CircleImageView;
import com.zht.genericproject.view.SolarSystemView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author： Tom on 2017/12/18 23:42
 * @email： 820159571@qq.com
 * @describe:个人中心的详情界面
 */
public class MeFragment extends BaseFragment {

    @BindView(R.id.me_view_solar_system)
    SolarSystemView meViewSolarSystem;
    @BindView(R.id.me_setting)
    ImageView mSetting;
    @BindView(R.id.me_zxing)
    ImageView mZxing;
    @BindView(R.id.me_info_head_container)
    FrameLayout mHeadContainer;
    @BindView(R.id.me_portrait)
    CircleImageView mPortrait;
    @BindView(R.id.me_gender)
    ImageView mGender;
    @BindView(R.id.me_info_icon_container)
    FrameLayout mIconContainer;
    @BindView(R.id.me_nick)
    TextView mNick;
    @BindView(R.id.me_avail_score)
    TextView mAvailScore;
    @BindView(R.id.me_active_score)
    TextView mActiveScore;
    @BindView(R.id.me_show_my_info)
    LinearLayout mShowInfo;
    Unbinder unbinder;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, view);
        measureTitleBarHeight();
        initSolar();
        return view;
    }

    /**
     * 测量状态栏的高度，并未状态栏设置空余高度
     */
    private void measureTitleBarHeight() {
        if (mShowInfo != null) {
            mShowInfo.setPadding(mShowInfo.getLeft(),
                    StatusBarUtils.getStatusBarHeight(getContext()),
                    mShowInfo.getRight(), mShowInfo.getBottom());
        }
    }

    private int mMaxRadius;
    private int mR;
    private float mPx;
    private float mPy;


    /**
     * 初始化solarSystemView
     */
    private void initSolar() {
        View root = view;
        if (root != null) {
            root.post(new Runnable() {
                @Override
                public void run() {
                    if (mShowInfo == null) return;

                    int width = mShowInfo.getWidth();
                    float rlShowInfoX = mShowInfo.getX();

                    int height = mIconContainer.getHeight();
                    float y1 = mIconContainer.getY();

                    float x = mPortrait.getX();
                    float y = mPortrait.getY();
                    int portraitWidth = mPortrait.getWidth();

                    mPx = x + rlShowInfoX + (width >> 1);
                    mPy = y1 + y +(height - y) / 2;
                    mMaxRadius = (int) (meViewSolarSystem.getHeight() - mPy + 250);
                    mR = (portraitWidth >> 1);

                    updateSolar(mPx, mPy);
                }
            });
        }
    }

    /**
     * 更新绘制solarSystemView
     *
     * @param px float
     * @param py float
     */
    private void updateSolar(float px, float py) {

        SolarSystemView solarSystemView = meViewSolarSystem;
        Random random = new Random(System.currentTimeMillis());
        int maxRadius = mMaxRadius;
        int r = mR;
        solarSystemView.clear();
        for (int i = 40, radius = r + i; radius <= maxRadius; i = (int) (i * 1.4), radius += i) {
            SolarSystemView.Planet planet = new SolarSystemView.Planet();
            planet.setClockwise(random.nextInt(10) % 2 == 0);
            planet.setAngleRate((random.nextInt(35) + 1) / 1000.f);
            planet.setRadius(radius);
            solarSystemView.addPlanets(planet);

        }
        solarSystemView.setPivotPoint(px, py);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.me_view_solar_system, R.id.me_setting, R.id.me_zxing, R.id.me_info_head_container, R.id.me_portrait, R.id.me_gender, R.id.me_info_icon_container, R.id.me_nick, R.id.me_avail_score, R.id.me_active_score, R.id.me_show_my_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.me_view_solar_system:
                break;
            case R.id.me_setting:
                break;
            case R.id.me_zxing:
                break;
            case R.id.me_info_head_container:
                break;
            case R.id.me_portrait:
                break;
            case R.id.me_gender:
                break;
            case R.id.me_info_icon_container:
                break;
            case R.id.me_nick:
                break;
            case R.id.me_avail_score:
                break;
            case R.id.me_active_score:
                break;
            case R.id.me_show_my_info:
                break;
        }
    }
}
