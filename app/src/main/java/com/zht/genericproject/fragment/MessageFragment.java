package com.zht.genericproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zht.genericproject.base.BaseFragment;
import com.zht.genericproject.util.StatusBarUtils;

/**
 * @author： Tom on 2017/12/18 23:41
 * @email： 820159571@qq.com
 *  
 */
public class MessageFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  View view = inflater.inflate(setLayout(), container, false);
        TextView view =new TextView(getContext());
        view.setText(getClass().getSimpleName());
        measureTitleBarHeight(view);
        return view;
    }

    private void measureTitleBarHeight(View view) {
        if (view != null) {
            view.setPadding(view.getLeft(),
                    StatusBarUtils.getStatusBarHeight(getContext()),
                    view.getRight(), view.getBottom());
        }
    }
}
