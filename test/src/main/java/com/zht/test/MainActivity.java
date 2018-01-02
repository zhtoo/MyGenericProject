package com.zht.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：zhanghaitao on 2018/1/2 11:06
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Class> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.main_list);

        list = new ArrayList<>();
        //在此处添加Activity即可
        list.add(ExpandableViewSample.class);
        list.add(BottomDialogSample.class);


        initRecycler();
    }

    private void initRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            TextView textView = new TextView(MainActivity.this);

            textView.setPadding(50, 50, 0, 50);
            //布局参数
            LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,//mText的宽度
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            textView.setLayoutParams(textlp);
            return new MyViewHolder(textView);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.setData(position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public MyViewHolder(TextView itemView) {
                super(itemView);
                textView = itemView;
            }

            public void setData(final int position) {
                String simpleName = list.get(position).getSimpleName();
                textView.setText(simpleName);
                if (position % 2 == 1) {
                    textView.setBackgroundColor(Color.parseColor("#eeeeee"));
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, list.get(position)));
                    }
                });
            }

        }


    }
}
