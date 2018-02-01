package com.zht.bottomdialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zht on 17/11/29.
 * <p>
 * BaseBottomDialog的实现类
 * 使用方式：
 * <p>
 * SelectBottomDialog dialog = new SelectBottomDialog();
 * dialog.show(getSupportFragmentManager());
 * <p>
 * 示例：
 * SelectBottomDialog dialog = new SelectBottomDialog();
 * dialog.setItemStrings(getContext(), new String[]{"未婚", "已婚", "离异"});
 * dialog.show(activity.getSupportFragmentManager());
 * dialog.setOnClickListener(new SelectBottomDialog.onItemClickListener() {
 *
 * @Override public void onClick(String text) {
 * String oldText = mMaritalStatusText.getText().toString().trim();
 * if(oldText.equals("请选择")&&!TextUtils.isEmpty(text)){
 * activity.changeProgress(activity.mBasicProgress, 1);
 * }
 * mMaritalStatusText.setText(text);
 * }
 * });
 */

public class SelectBottomDialog extends BaseBottomDialog implements View.OnClickListener {

    private LinearLayout container;
    private TextView cancel;

    private Context context;
    private String[] itemStrings;
    private int[] viewID;//条目的id
    private int lineHeight = 0;//分割线的高度
    private int dialogTextColor = 0;//弹出文字的颜色
    private float dialogTextSize = 0;//弹出文字的颜色
    private int lineColor = 0;//线条颜色
    private float cancelTextSize = 0;//“取消”字体大小
    private int cancelTextColor = 0;//“取消”字体颜色


    @Override
    public int getLayoutRes() {
        return R.layout.dialog_bottom_select;
    }

    @Override
    public void addItemView(View v) {
        //内容
        container = (LinearLayout) v.findViewById(R.id.dialog_bottom_container);
        //取消按钮
        cancel = (TextView) v.findViewById(R.id.dialog_bottom_cancel);
        if (cancelTextSize > 0) {
            cancel.setTextSize(cancelTextSize);
        }
        if (cancelTextColor > 0) {
            cancel.setTextColor(context.getResources().getColor(cancelTextColor));
        }
        container.removeAllViews();
        //循环创建text
        for (int i = 0; i < itemStrings.length; i++) {
            TextView mText = new TextView(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mText.setId(View.generateViewId());
                viewID[i] = mText.getId();
            }
            //布局参数
            LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,//mText的宽度
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //设置属性
            mText.setLayoutParams(textlp);
            mText.setGravity(Gravity.CENTER);
            mText.setPadding(30, 30, 30, 30);
            mText.setTextSize(dialogTextSize > 0 ? dialogTextSize : 18);
            mText.setTextColor(dialogTextColor > 0 ? context.getResources().getColor(dialogTextColor) :
                    Color.parseColor("#222222"));
            mText.setText(this.itemStrings[i]);
            container.addView(mText, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            //设置监听
            mText.setOnClickListener(this);
            if (i != itemStrings.length - 1) {
                View line = new View(context);
                //布局参数
                LinearLayout.LayoutParams linelp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        lineHeight > 0 ? lineHeight : 2);
                //设置属性
                line.setLayoutParams(linelp);
                line.setBackgroundColor(lineColor > 0 ? getResources().getColor(lineColor) :
                        Color.parseColor("#EEEEEE"));
                container.addView(line);
            }

        }


    }

    @Override
    public void bindView(View v) {
        //TODO:绑定视图
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String selecetText;
        if (id != R.id.dialog_bottom_cancel) {
            for (int i = 0; i < itemStrings.length; i++) {
                if (id == viewID[i]) {
                    selecetText = itemStrings[i];
                    if (listener != null) {
                        listener.onClick(selecetText);
                    }
                }
            }
        }
        this.dismiss();
    }

    public void setItemStrings(Context context, String[] itemStrings) {
        this.context = context;
        this.itemStrings = itemStrings;
        viewID = new int[itemStrings.length];
    }

    /**
     * 选择对话框回调
     *
     * @param listener
     */
    public void setOnClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    private onItemClickListener listener;

    /**
     * 选择对话框回调接口，调用者实现
     */
    public interface onItemClickListener {
        void onClick(String text);
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getDialogTextColor() {
        return dialogTextColor;
    }

    public void setDialogTextColor(int dialogTextColor) {
        this.dialogTextColor = dialogTextColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public float getCancelTextSize() {
        return cancelTextSize;
    }

    public void setCancelTextSize(float cancelTextSize) {
        this.cancelTextSize = cancelTextSize;
    }

    public int getCancelTextColor() {
        return cancelTextColor;
    }

    public void setCancelTextColor(int cancelTextColor) {
        this.cancelTextColor = cancelTextColor;
    }

    public float getDialogTextSize() {
        return dialogTextSize;
    }

    public void setDialogTextSize(float dialogTextSize) {
        this.dialogTextSize = dialogTextSize;
    }
}
