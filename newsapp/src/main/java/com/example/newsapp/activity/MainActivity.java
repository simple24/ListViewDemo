package com.example.newsapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.newsapp.R;
import com.example.newsapp.adapter.NewsListAdapter;
import com.example.newsapp.bean.NetResult;
import com.example.newsapp.bean.NewsData;
import com.example.newsapp.utils.NetControl;

public class MainActivity extends AppCompatActivity implements NetControl.OnNewsListDataListener{

    private static final int PAGE_SIZE = 10;

    private ListView listView_news;
    private View footer_view;

    private NewsListAdapter adapter;
    private NetControl control;

    private int pageIndex = 1;
    private boolean isBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView_news = (ListView) this.findViewById(R.id.listview_main);
        footer_view = getLayoutInflater().inflate(R.layout.listview_footer,null);
        listView_news.addFooterView(footer_view);
        footer_view.setVisibility(View.GONE);

        control = new NetControl(this);
        adapter = new NewsListAdapter(control);
        listView_news.setAdapter(adapter);

        //加载第一页数据
        control.getNewsListData(NetControl.TOP_LINE,pageIndex,PAGE_SIZE,this);

        //分页滚动事件
        listView_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && isBottom){
                    //开启视图
                    footer_view.setVisibility(View.VISIBLE);
                    //加载下一页数据
                    control.getNewsListData(NetControl.TOP_LINE,++pageIndex,PAGE_SIZE,MainActivity.this);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isBottom = ((firstVisibleItem+visibleItemCount) == totalItemCount);
            }
        });

        //item的点击事件，跳转到详情页
        listView_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到当前item的数据
                NewsData newsData = (NewsData) adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("oid",newsData.oid);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onNewsListData(NetResult result) {
        if (result==null){
            Toast.makeText(this,"网络异常!!!",Toast.LENGTH_SHORT).show();
        }else {
           if (result.resultCode != NetControl.RESULT_OK){
                Toast.makeText(this,"网络异常",Toast.LENGTH_SHORT).show();
                return;
            }
            footer_view.setVisibility(View.GONE);
            //更新数据
            adapter.addDatas(result.newsData);
        }

    }
}
