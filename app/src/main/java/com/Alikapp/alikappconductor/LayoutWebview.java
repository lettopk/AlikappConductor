package com.Alikapp.alikappconductor;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class LayoutWebview extends AppCompatActivity {

    private Button btnCancelarpago;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview);

        Intent intent = getIntent();
        String url = intent.getStringExtra("pack");

        webView = (WebView) findViewById(R.id.webView);

        final WebSettings ajustesWebView = webView.getSettings();
        ajustesWebView.setJavaScriptEnabled(true);
        webView.loadUrl(url);

    }
}