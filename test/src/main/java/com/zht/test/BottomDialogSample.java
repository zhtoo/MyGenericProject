package com.zht.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zht.bottomdialog.SelectBottomDialog;

public class BottomDialogSample extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_dialog);


    }


    public void onSelectViewClick(View v) {
        final TextView textView = (TextView) v;
        SelectBottomDialog dialog = new SelectBottomDialog();
        dialog.setItemStrings(this, new String[]{"未婚", "已婚", "离异"});
        dialog.show(this.getSupportFragmentManager());
        dialog.setOnClickListener(new SelectBottomDialog.onItemClickListener() {
            @Override
            public void onClick(String text) {

                textView.setText(text);
            }
        });
    }


}
