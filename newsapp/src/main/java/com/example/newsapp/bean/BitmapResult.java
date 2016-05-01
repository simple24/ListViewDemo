package com.example.newsapp.bean;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class BitmapResult {

    /**
     * 跟请求的图片绑定的那个ImageView
     */
    public ImageView netImageView;

    /**
     * 图片cover地址，相对的地址，不包含主机名
     */
    public String cover;

    /**
     * 图片对象
     */
    public Bitmap bitmap;

    /**
     * 网络请求结果码
     */
    public int resultCode;
}
