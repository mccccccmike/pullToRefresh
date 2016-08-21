package com.sudichina.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RefreshListView.OnRefreshListener {
    private RefreshListView mListView;
    private List<Bean> mData;
    private BaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (RefreshListView) findViewById(R.id.listView);
        mListView.setOnRefreshListener(this);
        mData = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mData.add(new Bean("默认数据" + i, "用户ID" + i, R.mipmap.ic_launcher));
        }
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mData.size();
            }

            @Override
            public Object getItem(int i) {
                return mData.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, null);
                }

                TextView tv1 = (TextView) view.findViewById(R.id.tv1);
                TextView tv2 = (TextView) view.findViewById(R.id.tv2);
                ImageView iv = (ImageView) view.findViewById(R.id.iv);

                tv1.setText(mData.get(i).getTv1());
                tv2.setText(mData.get(i).getTv2());
                iv.setImageResource(mData.get(i).getIv());

                return view;
            }
        };

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取最新数据
                for (int i = 0; i < 2; i++) {
                    mData.add(0, new Bean("最新数据" + i, "最新用户ID" + i, R.mipmap.a));
                }
                //通知listview显示最新数据
                mAdapter.notifyDataSetChanged();
                //通知数据刷新完毕
                mListView.refreshComplete();
            }
        },3000);

    }
}
