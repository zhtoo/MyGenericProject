package com.zht.test.sample;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.zht.expandablerecycler.ExpandableRecyclerAdapter;
import com.zht.expandablerecycler.vertical.Inside;
import com.zht.expandablerecycler.vertical.Outside;
import com.zht.expandablerecycler.vertical.OutsideAdapter;
import com.zht.test.R;

import java.util.Arrays;
import java.util.List;

/**
 * 作者：zhanghaitao on 2018/1/3 14:38
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class ExpandableRcyclerViewSample extends AppCompatActivity {


    private RecyclerView recycler;
    private OutsideAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_rcycler);

        recycler = (RecyclerView) findViewById(R.id.expandable_recycler);
        initRecycler();

    }

    private void initRecycler() {
        Inside beef = new Inside("beef", false);
        Inside cheese = new Inside("cheese", true);
        Inside salsa = new Inside("salsa", true);
        Inside tortilla = new Inside("tortilla", true);
        Inside ketchup = new Inside("ketchup", true);
        Inside bun = new Inside("bun", true);

        Outside taco = new Outside("taco", Arrays.asList(beef, cheese, salsa, tortilla));
        Outside quesadilla = new Outside("quesadilla", Arrays.asList(cheese, tortilla));
        Outside burger = new Outside("burger", Arrays.asList(beef, cheese, ketchup, bun));
        final List<Outside> recipes = Arrays.asList(taco, quesadilla, burger);


        mAdapter = new OutsideAdapter(this, recipes);
        mAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @UiThread
            @Override
            public void onParentExpanded(int parentPosition) {
                Outside expandedRecipe = recipes.get(parentPosition);

                String toastMsg = "expanded: %s";
                Toast.makeText(ExpandableRcyclerViewSample.this,
                        toastMsg,
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @UiThread
            @Override
            public void onParentCollapsed(int parentPosition) {
                Outside collapsedRecipe = recipes.get(parentPosition);

                String toastMsg = "collapsed: %s";
                Toast.makeText(ExpandableRcyclerViewSample.this,
                        toastMsg,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        recycler.setAdapter(mAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

    }


}
