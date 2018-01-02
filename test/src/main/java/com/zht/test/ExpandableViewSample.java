package com.zht.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zht.expandableview.view.ExpandableView;

public class ExpandableViewSample extends AppCompatActivity {

    private ExpandableView mExpandableView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_view);


        mExpandableView = (ExpandableView) findViewById(R.id.expandable_view);

        createExpandableView();

    }


    private void createExpandableView() {
        String[] androidVersionNameList = {
                "Ice Cream Sandwich",
                "Jelly Bean"};

        mExpandableView.fillData(0, "改个控件都这么难", false);
        addContentView(mExpandableView, androidVersionNameList, true);

    }


    public void addContentView(ExpandableView view, String[] stringList, boolean showCheckbox) {

        for (int i = 0; i < stringList.length; i++) {

            View itemView = View.inflate(this, R.layout.item_expandable_view, null);

//            ExpandedListItemView itemView = new ExpandedListItemView(this);
//            itemView.setText(stringList[i], showCheckbox);
            view.addContentView(itemView);
        }
    }


}
