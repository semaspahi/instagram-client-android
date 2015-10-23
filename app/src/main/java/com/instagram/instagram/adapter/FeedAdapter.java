package com.instagram.instagram.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.instagram.instagram.R;
import com.instagram.instagram.service.model.Media;
import com.instagram.instagram.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int MAX_PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    private final Context context;
    private final int cellSize;
    private final List<Media> mediaList;

    private boolean lockedAnimations = false;
    private int lastAnimatedItem = 0;
    private int visibleItemCount = 6;

    private OnFeedListener onFeedListener;
    private OnFeedItemClickListener onFeedItemClickListener;

    public FeedAdapter(Context context) {
        this.context = context;
        this.cellSize = Utils.getScreenWidth(context) / 2;
        this.mediaList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindPhoto((PhotoViewHolder) holder, position);
    }

    private void bindPhoto(final PhotoViewHolder holder, final int position) {
        Picasso.with(context)
                .load(mediaList.get(position).getImages().getStandardResolution().getUrl())
                .resize(cellSize, cellSize)
                .centerCrop()
                .into(holder.ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder, position);
                    }

                    @Override
                    public void onError() {

                    }
                });

        if(position == getItemCount() - visibleItemCount) {
            if(onFeedListener != null) {
                onFeedListener.onLoadMore();
            }
        }

        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void animatePhoto(final PhotoViewHolder viewHolder, final int position) {

        viewHolder.ivPhoto.setTag(position);
        viewHolder.flRoot.setTag(position);
        viewHolder.ivText.setTag(position);
        viewHolder.ivPhoto.setOnClickListener(this);
        if (mediaList.get(position).getCaption() != null) {
            viewHolder.ivText.setText(mediaList.get(position).getCaption().getText());
        }

        if (!lockedAnimations) {
            if (lastAnimatedItem == viewHolder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay =   MAX_PHOTO_ANIMATION_DELAY - System.currentTimeMillis();
             if (animationDelay < 0) {
                animationDelay = viewHolder.getPosition() * 30;
            } else {
                animationDelay += viewHolder.getPosition() * 30;
            }

            viewHolder.flRoot.setScaleY(0);
            viewHolder.flRoot.setScaleX(0);
            viewHolder.flRoot.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    @Override
    public void onClick(View view) {
        if (onFeedItemClickListener != null) {
            onFeedItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
        if (onFeedListener != null) {
            onFeedListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public void addData(List<Media> mediaList) {
        this.mediaList.addAll(mediaList);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mediaList.clear();
        notifyDataSetChanged();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.flRoot)
        FrameLayout flRoot;
        @InjectView(R.id.photo)
        ImageView ivPhoto;
        @InjectView(R.id.ivText)
        TextView ivText;

        public PhotoViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    public interface OnFeedListener {
        void onItemClick(View v, int position);
        void onLoadMore();
    }

    public void setOnFeedListener(OnFeedListener onFeedListener) {
        this.onFeedListener = onFeedListener;
    }

    public interface OnFeedItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }
}