package com.example.newsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.newsapp.R;
import com.example.newsapp.utils.NetControl;

public class DetailActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_detail);

        webView = (WebView) this.findViewById(R.id.webView);

        int oid = getIntent().getIntExtra("oid",-1);
        webView.loadUrl(NetControl.DETAIL_ADDRESS+oid);
    }
}
