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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
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
import java.util.List;

public class HomeActivity extends AppCompatActivity implements IParseListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    // The number of native ads to load and display.
    public static final int NUMBER_OF_ADS = 5;

    // The AdLoader used to load ads.
    private AdLoader adLoader;

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    RecyclerView recyclerView;
    GridLayoutManager mGridLayoutManager;
    String url;

    public static ArrayList<Object> newsModelArrayList = new ArrayList<>();
    HomeAdapter homeAdapter;

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
        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this,
                getString(R.string.ad_unit_id));
     /*   admob();
        addNativeExpressAds();
        setUpAndLoadNativeExpressAds();*/

        pushNotifications();


        loadNativeAds();
        // loadMenu();
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
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
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
                        /*newsModelArrayList.add(new NewsDetails
                                ());*/
                        NewsDetails newsDetails = new NewsDetails();
                        newsDetails.setAuthor(jsonObject.optString("author"));
                        newsDetails.setTitle(jsonObject.optString("title"));
                        newsDetails.setDescription(jsonObject.optString("description"));
                        newsDetails.setUrl(jsonObject.optString("url"));
                        newsDetails.setUrltoimage(jsonObject.optString("urlToImage"));
                        newsDetails.setPublishedat(jsonObject.optString("publishedAt"));
                        newsDetails.setContent(jsonObject.optString("content"));
                        newsModelArrayList.add(newsDetails);
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
        Log.e("Adapter", "" + mNativeAds.size());
        if (mNativeAds.size() >= 1) {
            Log.e("Adapter", "" + mNativeAds.get(0).getPrice());
        }
        homeAdapter = new HomeAdapter(this, newsModelArrayList, mNativeAds);
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
                loadNativeAds();
                code = "in";
                if (Utility.isNetworkAvailable(this)) {
                    callServiceforNews();
                } else {
                    Toast.makeText(this, "Please check your internet connection and try again later", Toast.LENGTH_SHORT).show();

                }
                return true;
            case R.id.usa:
                loadNativeAds();
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


    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (newsModelArrayList.size() / mNativeAds.size()) + 1;
        int index = 0;
        for (UnifiedNativeAd ad : mNativeAds) {
            newsModelArrayList.add(index, ad);
            index = index + offset;
        }
    }

    private void loadNativeAds() {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.ad_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        Toast.makeText(HomeActivity.this, "native ad success", Toast.LENGTH_SHORT).show();

                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                            // loadMenu();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        Toast.makeText(HomeActivity.this, "native ad failed to load", Toast.LENGTH_SHORT).show();
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                            // loadMenu();
                        }
                    }
                }).build();

        // Load the Native Express ad.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }
    class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String notificationID = notification.payload.notificationID;
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String smallIconAccentColor = notification.payload.smallIconAccentColor;
            String sound = notification.payload.sound;
            String ledColor = notification.payload.ledColor;
            int lockScreenVisibility = notification.payload.lockScreenVisibility;
            String groupKey = notification.payload.groupKey;
            String groupMessage = notification.payload.groupMessage;
            String fromProjectNumber = notification.payload.fromProjectNumber;
            String rawPayload = notification.payload.rawPayload;

            String customKey;

            Log.i("OneSignalExample", "NotificationID received: " + notificationID);

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }



    class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl

            String customKey;
            String openURL = launchUrl;
           // Object activityToLaunch = HomeActivity.class;
            if (openURL != null) {

                Intent intent = new Intent(getApplicationContext(), NewsDetailsActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("newsurl", openURL);
                Log.i("OneSignalExample", "openURL = " + openURL);
                // startActivity(intent);
                startActivity(intent);
            }else {
                Intent home=new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(home);
            }

            if (data != null) {
                customKey = data.optString("customkey", null);
                openURL = data.optString("openURL", null);

                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

          /*  if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

                if (result.action.actionID.equals("id1")) {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                    activityToLaunch = Splashscreen.class;
                } else
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
            }
            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
            // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("openURL", openURL);
            Log.i("OneSignalExample", "openURL = " + openURL);
            // startActivity(intent);
            startActivity(intent);
*/
            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.
        /*
           <application ...>
             <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
           </application>
        */
        }
    }
}
