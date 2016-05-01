package com.example.newsapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.newsapp.bean.BitmapResult;
import com.example.newsapp.bean.NetResult;
import com.example.newsapp.bean.NewsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NetControl {

    /**
     * 头条类别新闻
     */
    public static final int TOP_LINE = 0;
    /**
     * 社会类别新闻
     */
    public static final int SOCIETY = 1;
    /**
     * 真相类别新闻
     */
    public static final int TRUTH = 2;
    /**
     * 国内类别新闻
     */
    public static final int INLAND = 3;
    /**
     * 国际类别新闻
     */
    public static final int INTERNATIONAL = 4;
    /**
     * 军事类别新闻
     */
    public static final int MILITARY = 5;
    /**
     * 体育类别新闻
     */
    public static final int SPORTS = 6;
    /**
     * 娱乐类别新闻
     */
    public static final int RECREATION = 7;

    /**
     * 网络结果返回码
     */
    public static final int RESULT_OK = 2000;

    /**
     * 服务器域名
     */
    public static final String HOST_NAME = "http://litchiapi.jstv.com";

    /**
     * 请求新闻列表数据的接口地址
     */
    public static final String address = HOST_NAME
            + "/api/GetFeeds?column=%&PageSize=@&pageIndex=$&val=100511D3BE5301280E0992C73A9DEC41";

    public static final String DETAIL_ADDRESS = "http://litchi.jstv.com/Wap/Article/";


    /**
     * 新闻数据列表回调接口
     * @author Administrator
     *
     */
    public interface OnNewsListDataListener{
        void onNewsListData(NetResult result);
    }

    /**
     * 图片数据回调接口
     * @author Administrator
     *
     */
    public interface OnBitmapDataListener{
        void onBitmapData(BitmapResult result);
    }

    private Context context;

    public NetControl(Context context){
        this.context = context;
    }

    /**
     * 请求网络图片  异步请求
     * cover 图片地址
     * listener 回调对象
     *
     */
    public void getBitmapImage(String cover, ImageView imageView,OnBitmapDataListener listener){
        new MyAsyncTask(listener,imageView).execute(cover);
    }

    class MyAsyncTask extends AsyncTask<String,Void,BitmapResult>{

        private OnBitmapDataListener listener;
        private String cover;
        private ImageView imageView;

        public MyAsyncTask(OnBitmapDataListener listener, ImageView imageView) {
            this.listener = listener;
            this.imageView = imageView;
        }

        @Override
        protected BitmapResult doInBackground(String... params) {
            BitmapResult bitmapResult = new BitmapResult();
            cover = params[0];
            bitmapResult.cover = cover;
            bitmapResult.netImageView = imageView;
            //获取文件名
            String filename = cover.substring(cover.lastIndexOf("/")+1);
            Log.i("MainActivity","文件名"+ filename);
            //获取缓存目录
            File dir = context.getExternalCacheDir();
            Log.i("MainActivity","缓存目录"+ dir.getAbsolutePath());

            File file = new File(dir,filename);
            if (file.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bitmapResult.bitmap = bitmap;
                bitmapResult.resultCode = RESULT_OK;
            }else {
                getImageFromNet(cover,file,bitmapResult);
            }

            return bitmapResult;
        }

        @Override
        protected void onPostExecute(BitmapResult result) {
            super.onPostExecute(result);
            listener.onBitmapData(result);
        }

        private void getImageFromNet(String cover, File file, BitmapResult bitmapResult) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(HOST_NAME+cover).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);

                if (conn.getResponseCode() == 200){
                    FileOutputStream outputstream = new FileOutputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                    if (bitmap != null){
                        bitmapResult.bitmap = bitmap;
                        bitmapResult.resultCode = RESULT_OK;

                        //把图片保存到SD卡
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputstream);

                    }
                    outputstream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 网络请求列表数据  异步请求
     * type 栏目
     * pageNo 页码
     * pageCount 每页记录数
     * listener 回调接口
     */

    public void getNewsListData(int type,int pageNo,int pageCount,OnNewsListDataListener listener){
        //处理address内部参数
        String path = address.replaceAll("%",String.valueOf(type));
        path = path.replaceAll("@",String.valueOf(pageCount));
        path = path.replaceAll("\\$",String.valueOf(pageNo));
        Log.i("MainActivity","url:"+path);

        new NewsAsyncTask(listener).execute(path);
    }

    //异步请求
    class NewsAsyncTask extends AsyncTask<String,Void,NetResult>{

        private OnNewsListDataListener listener;

        public NewsAsyncTask(OnNewsListDataListener listener) {
            this.listener = listener;
        }

        @Override
        protected NetResult doInBackground(String... params) {
            NetResult  result = null;
            InputStream is = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(params[0]).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);

                int code = conn.getResponseCode();
                if (code ==200){
                    is = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len=is.read(bytes))!=-1){
                        bos.write(bytes,0,len);
                        bos.flush();
                    }
                    byte[] data = bos.toByteArray();
                    String json = new String(data);
                    ArrayList<NewsData> list = parseNewsListJson(json);
                    result = new NetResult(RESULT_OK,list);
                }else {
                    result = new NetResult(code,null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(NetResult result) {
            super.onPostExecute(result);
            listener.onNewsListData(result);
        }
    }

    //解析 JSON 数据
    private ArrayList<NewsData> parseNewsListJson(String json) throws JSONException {
        ArrayList<NewsData> newsDatas = new ArrayList<NewsData>();
        // 把字符串转换成json对象
        JSONObject jsonObject = new JSONObject(json);
        String status = jsonObject.getString("status");
        if (!status.equals("ok")) {
            return newsDatas;
        }
        JSONObject paramz = jsonObject.getJSONObject("paramz");
        JSONArray feeds = paramz.getJSONArray("feeds");
        for (int i = 0; i < feeds.length(); i++) {
            JSONObject newsJson = feeds.getJSONObject(i);

            int id = newsJson.getInt("id");
            int oid = newsJson.getInt("oid");
            JSONObject data = newsJson.getJSONObject("data");
            String subject = data.getString("subject");
            String summary = data.getString
                    ("summary");
            String cover = data.getString("cover");
            String changed = data.getString("changed");

            NewsData newsData = new NewsData(id, oid, subject, summary, cover, changed);
            Log.i("MainActivity", newsData.toString());
            newsDatas.add(newsData);
        }
        return newsDatas;
    }
}
