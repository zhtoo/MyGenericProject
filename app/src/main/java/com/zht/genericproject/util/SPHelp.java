package com.zht.genericproject.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zht.genericproject.MyApplication;


/**
 * SharePreferences不需要申请权限
 * 添加和修改的方法一样，只是Key的不同
 * 存储所需的功能
 * 1、创建存储的“文件”
 * 2、存储数据
 * 3、读取数据
 * 4、修改数据
 * 5、删除数据
 * 关键代码：
 * //1、获取到SharedPreferences的对象（创建）
 * sp = getSharedPreferences("config", Context.MODE_PRIVATE);
 * //2、获取编辑器
 * edit = sp.edit();
 * //存储数据
 * edit.putString("name", "bbbbbb");
 * //删除数据
 * //edit.remove("name");
 * edit.commit();//带edit必须提交
 * //查询数据
 * sp.getString("name", "")
 * <p>
 * Created by zhtoo on 2017/6/24.
 */

public class SPHelp {

    private static SharedPreferences.Editor edit;

    /**
     * 设置数据
     * @param key
     * @param value
     */
    public static void setData(String key, String value) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        edit = sp.edit();
        edit.putString(key, value);
        edit.commit();//提交
    }

    /**
     * 获取数据
     * @param key
     * @return
     */
    public static String getData(String key) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }



}

