package com.instagram.instagram.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.instagram.instagram.R;
import com.instagram.instagram.adapter.FeedAdapter;
import com.instagram.instagram.service.Instagram;
import com.instagram.instagram.service.endpoints.MediaEndpoint;
import com.instagram.instagram.service.model.Media;
import com.instagram.instagram.service.model.Popular;
import com.instagram.instagram.utils.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class InstagramActivity extends AppCompatActivity implements FeedAdapter.OnFeedItemClickListener {

    MediaEndpoint mediaEndpoint;
    private FeedAdapter feedAdapter;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    List<Media> mediaList;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.ivLogo)
    TextView ivLogo;
    @InjectView(R.id.fabSearch)
    FloatingActionButton floatingActionButton;
    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        ButterKnife.inject(this);

        setupFeed();
        startIntroAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ivLogo.setTypeface(Utils.getTypeface(getApplicationContext()));
        toolbar.setBackgroundColor(getResources().getColor(R.color.style_color_primary));
        updateStatusBarColor();
    }

    private void setupFeed() {
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvFeed.setLayoutManager(layoutManager);
        feedAdapter = new FeedAdapter(getApplicationContext());
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);

        mediaEndpoint = new Instagram().getMediaEndpoint();
        Call<Popular> popular = mediaEndpoint.getPopular();
        popular.enqueue(mediaCallback);
    }

    Callback<Popular> mediaCallback = new Callback<Popular>() {

        @Override
        public void onResponse(Response<Popular> response, Retrofit retrofit) {

            if (response.body() != null) {
                Popular popular = response.body();
                mediaList = popular.getMediaList();
                feedAdapter.addData(mediaList);
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Snackbar.make(floatingActionButton, "Internet connection problem :(", Snackbar.LENGTH_SHORT).show();
        }
    };

    private void startIntroAnimation() {
        floatingActionButton.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        floatingActionButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.style_color_primary_dark));
        }
    }

    @OnClick(R.id.fabSearch)
    public void onSearchClick() {
        int[] startingLocation = new int[2];
        floatingActionButton.getLocationOnScreen(startingLocation);
        startingLocation[0] += floatingActionButton.getWidth() / 2;
        SearchActivity.startSearchFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onItemClick(View v, int position) {

        ImageView photo = (ImageView) ((View) v.getParent()).findViewById(R.id.photo);
        String text = null;
        if (mediaList.get(position).getCaption() != null) {
            text = mediaList.get(position).getCaption().getText();
        }
        DetailActivity.launch(this, photo, text, ((BitmapDrawable) photo.getDrawable()).getBitmap());
    }

}
