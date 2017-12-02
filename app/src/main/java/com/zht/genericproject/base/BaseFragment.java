package com.zht.genericproject.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.zht.genericproject.util.PermissionCheck;


/**
 * Description: Fragment基类
 * 基本功能需要子类来实现
 */
public class BaseFragment extends Fragment {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionCheck.getInstance().onRequestPermissionsResult(getActivity(), requestCode, permissions, grantResults);
    }
}
