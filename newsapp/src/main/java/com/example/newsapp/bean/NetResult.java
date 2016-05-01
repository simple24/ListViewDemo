package com.example.newsapp.bean;

import java.util.ArrayList;

public class NetResult {

    public NetResult() {

    }
    // 网络连接的结果码
    public int resultCode;

    public ArrayList<NewsData> newsData;

    public NetResult(int resultCode, ArrayList<NewsData> newsData) {
        this.resultCode = resultCode;
        this.newsData = newsData;
    }
}
