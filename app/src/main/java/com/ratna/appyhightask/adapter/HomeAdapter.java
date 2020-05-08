package com.ratna.appyhightask.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ratna.appyhightask.activities.NewsDetailsActivity;
import com.ratna.appyhightask.model.NewsDetails;
import com.ratna.myapplication.R;

import java.util.ArrayList;

/**
 * Created by ratna on 06-05-2020.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    Context mContext;
    ArrayList<NewsDetails> dealsModelArrayList;


    public HomeAdapter(Context mContext, ArrayList<NewsDetails> dealsModelArrayList) {
        this.mContext = mContext;
        this.dealsModelArrayList = dealsModelArrayList;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAdapter.ViewHolder holder, int position) {
        final NewsDetails newsDetails = dealsModelArrayList.get(position);
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


    @Override
    public int getItemCount() {
        return dealsModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTxtTitle, mTxtAuthor, mTxtDescription, mTxtPublished;
        ImageView mImgItem;



        public ViewHolder(View itemView) {
            super(itemView);
            mTxtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mTxtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
            mTxtDescription = (TextView) itemView.findViewById(R.id.txtdescription);
            mTxtPublished = (TextView) itemView.findViewById(R.id.txtPublished);


            mImgItem = (ImageView) itemView.findViewById(R.id.imgItem);

        }
    }


}
