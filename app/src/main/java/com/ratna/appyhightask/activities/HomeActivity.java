package com.ratna.appyhightask.activities;

import android.content.Intent;
import android.provider.Settings;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.onesignal.OneSignal;
import com.ratna.appyhightask.adapter.HomeAdapter;
import com.ratna.appyhightask.interfaces.IParseListener;
import com.ratna.appyhightask.model.NewsDetails;
import com.ratna.appyhightask.utils.Utility;
import com.ratna.appyhightask.webutils.ServerResponse;
import com.ratna.appyhightask.webutils.WsUtils;
import com.ratna.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements IParseListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    GridLayoutManager mGridLayoutManager;
    String url;

    public static ArrayList<NewsDetails> newsModelArrayList = new ArrayList<>();
    HomeAdapter homeAdapter;
    NativeExpressAdView nativeAd;
    ImageView imglocation;
    String code;
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       ID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        initComponents();

    }


    private void initComponents() {
        getData();
        setReferences();
        setClickListeners();

        if (Utility.isNetworkAvailable(this)) {
            callServiceforNews();
        } else {
            Toast.makeText(this, "Please check your internet connection and try again later", Toast.LENGTH_SHORT).show();

        }
        admob();
        pushNotifications();
    }

    private void setClickListeners() {
        imglocation.setOnClickListener(this);
    }

    private void getData() {
        Intent mintent = getIntent();
        code = mintent.getStringExtra("code");
    }


    private void pushNotifications() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }


    private void setReferences() {
        imglocation = (ImageView) findViewById(R.id.locations);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);


        mGridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setHasFixedSize(true);

      /*  int widthInDP = getResources().getConfiguration().screenWidthDp;
        widthInDP -= getResources().getDimension(R.dimen.activity_horizontal_margin);
        nativeAd.setAdSize(new AdSize(widthInDP, 250));*/


    }

    private void callServiceforNews() {
        if (code == null || code.equals("")) {
            code = "in";
        }
        url = "http://newsapi.org/v2/top-headlines?country=" + code + "&apiKey=a64547cb2b7b41eca39d63ea0687419b";
        Log.e("hit", "" + url);
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.serviceRequestGet(this, url, null, this, WsUtils.WS_CODE_NEWS);
    }

    private void admob() {
        MobileAds.initialize(this, "ca-app-pub-9190916303459690/6565482855");
      /* AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-9190916303459690/6565482855")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // Show the ad.
                       *//* if (adLoader.isLoading()) {
                            // The AdLoader is still loading ads.
                            // Expect more adLoaded or onAdFailedToLoad callbacks.
                        } else {
                            // The AdLoader has finished loading ads.
                        }*//*
                        nativeAd =(NativeExpressAdView)findViewById(R.id.nativeadd);
                        nativeAd.setNativead(unifiedNativeAd);
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAds(new AdRequest.Builder().build(), 3);*/


        NativeExpressAdView mAdView = (NativeExpressAdView)findViewById(R.id.nativeadd);
        AdRequest adRequest = new AdRequest.Builder()

                .addTestDevice(ID)
                .build();

        if (mAdView != null) {

            mAdView.loadAd(adRequest);
        }
    }


    @Override
    public void ErrorResponse(VolleyError error, int requestCode) {

    }

    @Override
    public void SuccessResponse(String response, int requestCode) {
        switch (requestCode) {
            case WsUtils.WS_CODE_NEWS:
                responseForNews(response);
                break;

            default:
                break;
        }
    }

    private void responseForNews(String response) {
        if (response != null) {
            try {
                JSONObject mjsonObject = new JSONObject(response);
                String status = mjsonObject.optString("status");
                String message = mjsonObject.optString("message");
                if (status.equals("ok")) {
                    newsModelArrayList.clear();
                    JSONArray jsonArray = mjsonObject.getJSONArray("articles");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        newsModelArrayList.add(new NewsDetails
                                (jsonObject.optString("author"),
                                        jsonObject.optString("title"),
                                        jsonObject.optString("description"),
                                        jsonObject.optString("url"),
                                        jsonObject.optString("urlToImage"),
                                        jsonObject.optString("publishedAt"),
                                        jsonObject.optString("content")));

                    }

                    setAdapter();
                } else {
                    Toast.makeText(this, "" + message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setAdapter() {
        homeAdapter = new HomeAdapter(this, newsModelArrayList);
        recyclerView.setAdapter(homeAdapter);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locations:
                PopupMenu popup = new PopupMenu(this, v);
                popup.setOnMenuItemClickListener(this);
                popup.inflate(R.menu.my_options_menu);
                popup.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.india:
                code = "in";
                if (Utility.isNetworkAvailable(this)) {
                    callServiceforNews();
                } else {
                    Toast.makeText(this, "Please check your internet connection and try again later", Toast.LENGTH_SHORT).show();

                }
                return true;
            case R.id.usa:
                code = "us";
                if (Utility.isNetworkAvailable(this)) {
                    callServiceforNews();
                } else {
                    Toast.makeText(this, "Please check your internet connection and try again later", Toast.LENGTH_SHORT).show();

                }
                return true;
            default:
                return false;
        }
    }
}
