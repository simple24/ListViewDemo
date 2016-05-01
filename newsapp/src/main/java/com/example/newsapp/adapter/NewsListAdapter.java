package com.example.newsapp.adapter;


import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsapp.R;
import com.example.newsapp.bean.BitmapResult;
import com.example.newsapp.bean.NewsData;
import com.example.newsapp.utils.NetControl;

import java.util.ArrayList;
import java.util.List;

public class NewsListAdapter extends BaseAdapter{

    private List<NewsData> list = new ArrayList<NewsData>();
    //初始化网络操作
    private NetControl netControl;
    //内存缓存（图片）
    private LruCache<String,Bitmap> mCache;
    //构造方法设置缓存大小
    public NewsListAdapter(NetControl netControl) {
        this.netControl = netControl;
        //获取最大内存
        long maxMemory = Runtime.getRuntime().maxMemory();
        //分配1/16，，指定单位数
        mCache = new LruCache<String, Bitmap>((int) (maxMemory/16)){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight(); //存储单元
            }
        };
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vHolder = null;
        if (convertView == null){
            vHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item,null);

            vHolder.newsImgView = (ImageView) convertView.findViewById(R.id.news_list_img);
            vHolder.subjectView = (TextView) convertView.findViewById(R.id.news_list_subject_tv);
            vHolder.summaryView = (TextView) convertView.findViewById(R.id.news_list_summary_tv);
            vHolder.timeView = (TextView) convertView.findViewById(R.id.news_list_time_tv);

            convertView.setTag(vHolder);
        }else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        //设置UI
        setDataToUI(vHolder,position);
        return convertView;
    }

    private void setDataToUI(final ViewHolder vHolder, int position) {
        //得到item数据
        NewsData newsData = this.list.get(position);
        //先设置文字
        vHolder.subjectView.setText(newsData.subject);
        vHolder.summaryView.setText(newsData.summary);
        vHolder.timeView.setText(newsData.changed);

        //设置图片，需要判断图片是否存在
        Bitmap bitmap = mCache.get(newsData.cover);
        if (bitmap == null){
            //图片未加载成功的时候
            vHolder.newsImgView.setImageResource(R.drawable.default01);
            //避免图片与地址不一致
            vHolder.newsImgView.setTag(newsData.cover);
            //连网加载
            netControl.getBitmapImage(newsData.cover, vHolder.newsImgView, new NetControl.OnBitmapDataListener() {
                @Override
                public void onBitmapData(BitmapResult result) {
                    if (result.resultCode == NetControl.RESULT_OK){
                        Log.i("MainActivity","cover:"+result.cover);
                        Log.i("MainActivity","bimtap:"+result.bitmap.toString());
                        mCache.put(result.cover,result.bitmap);
                        String cover = (String) vHolder.newsImgView.getTag();
                        if (cover.equals(result.netImageView.getTag())){
                            vHolder.newsImgView.setImageBitmap(result.bitmap);
                        }
                    }
                }
            });
        }else {
            vHolder.newsImgView.setImageBitmap(bitmap);
        }
    }

    class ViewHolder{
        ImageView newsImgView;
        TextView subjectView,summaryView,timeView;
    }

    public void addDatas(ArrayList<NewsData> data){
        this.list.addAll(data);
        notifyDataSetChanged();
    }
}
