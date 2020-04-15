package com.example.hw9.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.hw9.R;

public class NewsAdapter extends RecyclerView.Adapter {
    private NewsData newsData;
    NewsAdapter(NewsData readyData){
        newsData = readyData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_news_card, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return newsData.getLength();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mItemTitle;
        private TextView mItemTime;
        private NetworkImageView mItemImage;

        public ListViewHolder(View itemView){
            super(itemView);
            mItemTitle = (TextView) itemView.findViewById(R.id.card_title);
            mItemImage = (NetworkImageView) itemView.findViewById(R.id.card_image);
            mItemTime = (TextView) itemView.findViewById(R.id.card_time);
            itemView.setOnClickListener(this);
        }

        public void bindView(int position){
            mItemTitle.setText(newsData.titles.get(position));
            mItemTime.setText(newsData.times.get(position) + " | " + newsData.sections.get(position));
            mItemImage.setImageUrl(newsData.imgPaths.get(position), newsData.getImageLoader());
        }

        public void onClick(View view){

        }
    }
}
