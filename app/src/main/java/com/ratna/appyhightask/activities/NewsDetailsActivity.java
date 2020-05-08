package com.ratna.appyhightask.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ratna.myapplication.R;

public class NewsDetailsActivity extends AppCompatActivity {
    WebView mwebview;
    String url;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        initcomponents();

    }

    private void initcomponents() {
        setReferences();
        getData();
        showWebView();
    }

    private void getData() {
        Intent intent=getIntent();
        url=intent.getStringExtra("newsurl");

    }

    private void setReferences() {
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        mwebview = (WebView) findViewById(R.id.webview);
    }


    private void showWebView() {
        if (url != null && !url.equals("") && !url.equals("null") && url.length() > 0) {
//             Utility.showProgressDialog(this, "Loading...", false);
            mwebview.setBackgroundColor(0x00000000);
            WebSettings webSettings = mwebview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setDomStorageEnabled(true);
            mwebview.setVerticalScrollBarEnabled(true);
            mwebview.setHorizontalScrollBarEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setSupportMultipleWindows(true);
            mwebview.setWebChromeClient(new WebChromeClient());
            mwebview.setVerticalScrollBarEnabled(true);
            mwebview.setHorizontalScrollBarEnabled(true);
            mwebview.getSettings().setBuiltInZoomControls(true);
            mwebview.getSettings().setDisplayZoomControls(false);

            mwebview.setWebViewClient(new WebViewClient() {


                @Override
                public boolean shouldOverrideUrlLoading(WebView view,
                                                        String url) {

                    if (url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx")) {
                        String googleDocs = "https://docs.google.com/viewer?url=";
                        view.loadUrl(googleDocs + url);
                    } else {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        view.loadUrl(url);

                    }
                    return true;
                }


                @Override
                public void onPageStarted(WebView view, String urlValue, Bitmap favicon) {
                    super.onPageStarted(view, urlValue, favicon);
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onPageFinished(WebView view, final String url) {
                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                    super.onPageFinished(view, url);


                }
            });

            if (url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx")) {
                String googleDocs = "https://docs.google.com/viewer?url=";
                mwebview.loadUrl(googleDocs + url);
            } else
                mwebview.loadUrl(url);

            mwebview.setWebChromeClient(new WebChromeClient());


        }
    }
}
