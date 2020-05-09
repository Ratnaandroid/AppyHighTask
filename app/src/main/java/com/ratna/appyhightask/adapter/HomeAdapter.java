package com.ratna.appyhightask.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.ratna.appyhightask.activities.NewsDetailsActivity;
import com.ratna.appyhightask.model.NewsDetails;
import com.ratna.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ratna on 06-05-2020.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Object> newsDetailsArrayList;
    private List<UnifiedNativeAd> mNativeAds;

    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The unified native ad view type.
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;
    public HomeAdapter(Context mContext, ArrayList<Object> newsDetailsArrayList, List<UnifiedNativeAd> mNativeAds) {
        this.mContext = mContext;
        this.newsDetailsArrayList = newsDetailsArrayList;
        this.mNativeAds = mNativeAds;
    }


    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     /*   View view = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);*/


        /*View unifiedNativeLayoutView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.ad_unified,
                parent, false);
        return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);*/
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.ad_unified,
                        parent, false);
                return new ViewHolder(unifiedNativeLayoutView,UNIFIED_NATIVE_AD_VIEW_TYPE);//new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
                return new ViewHolder(view,MENU_ITEM_VIEW_TYPE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAdapter.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) newsDetailsArrayList.get(position);
                populateNativeAdView(nativeAd, holder.getAdView());//(holder).getAdView()
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
              //  ViewHolder menuItemHolder = (ViewHolder) holder;
                final NewsDetails newsDetails = (NewsDetails) newsDetailsArrayList.get(position);
                holder.mTxtTitle.setText(newsDetails.getTitle());
                if (!newsDetails.getAuthor().equals("null")){
                    holder.mTxtAuthor.setVisibility(View.VISIBLE);
                    holder.mTxtAuthor.setText("Author:"+newsDetails.getAuthor());
                }else {
                    holder.mTxtAuthor.setVisibility(View.GONE);
                }

                holder.mTxtDescription.setText(newsDetails.getDescription());
                holder.mTxtPublished.setText(newsDetails.getPublishedat());


                Glide.with(mContext)
                        .load(newsDetails.getUrltoimage())
                        .apply(new RequestOptions().placeholder(R.drawable.appyhighlogo).error(R.drawable.appyhighlogo))
                        .into(holder.mImgItem);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity activity = (Activity) mContext;
                        Intent product = new Intent(mContext, NewsDetailsActivity.class);
                        product.putExtra("newsurl", newsDetails.getUrl());
                        activity.startActivity(product);
                        activity.overridePendingTransition(0, 0);
                    }
                });

        }





    }


    @Override
    public int getItemCount() {
        return newsDetailsArrayList.size();
    }
    @Override
    public int getItemViewType(int position) {

        Object recyclerViewItem = newsDetailsArrayList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTxtTitle, mTxtAuthor, mTxtDescription, mTxtPublished;
        ImageView mImgItem;
        UnifiedNativeAdView adView;

        public UnifiedNativeAdView getAdView() {
            return adView;
        }

        public ViewHolder(View itemView, int menuItemViewType) {
            super(itemView);
            switch (menuItemViewType){
                case MENU_ITEM_VIEW_TYPE:{
                    mTxtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                    mTxtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
                    mTxtDescription = (TextView) itemView.findViewById(R.id.txtdescription);
                    mTxtPublished = (TextView) itemView.findViewById(R.id.txtPublished);
                    mImgItem = (ImageView) itemView.findViewById(R.id.imgItem);
                    break;
                }
                case UNIFIED_NATIVE_AD_VIEW_TYPE:{
                    adView = (UnifiedNativeAdView) itemView.findViewById(R.id.ad_view);

                    // The MediaView will display a video asset if one is present in the ad, and the
                    // first image asset otherwise.
                    adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

                    // Register the view used for each individual asset.
                    adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
                    adView.setBodyView(adView.findViewById(R.id.ad_body));
                    adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
                    adView.setIconView(adView.findViewById(R.id.ad_icon));
                    adView.setPriceView(adView.findViewById(R.id.ad_price));
                    adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
                    adView.setStoreView(adView.findViewById(R.id.ad_store));
                    adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
                    break;
                }
            }
        }
    }
    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }




}
