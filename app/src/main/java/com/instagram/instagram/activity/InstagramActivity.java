package com.instagram.instagram.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.instagram.instagram.R;
import com.instagram.instagram.adapter.FeedAdapter;
import com.instagram.instagram.service.Instagram;
import com.instagram.instagram.service.model.Media;
import com.instagram.instagram.service.model.Popular;
import com.instagram.instagram.utils.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InstagramActivity extends AppCompatActivity implements FeedAdapter.OnFeedItemClickListener{

    private static final String CLIENT_ID = "45ce8faf821f43d6a39f6b61bf677820";
    Instagram instagram;
    private FeedAdapter feedAdapter;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    List<Media> mediaList;
    public static SparseArray<Bitmap> sPhotoCache = new SparseArray<Bitmap>(8);

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

        setTypeface();
        setupFeed();
        startIntroAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setBackgroundColor(getResources().getColor(R.color.style_color_primary));
        updateStatusBarColor();
    }

    void setTypeface() {
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Billabong.ttf");
        ivLogo.setTypeface(type);
    }

    private void setupFeed() {
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvFeed.setLayoutManager(layoutManager);

        instagram = new Instagram();
        instagram.getMediaEndpoint().mediaService.getPopularPublic(CLIENT_ID, new Callback<Popular>() {
            @Override
            public void success(Popular popular, Response response) {
                mediaList = popular.getMediaList();
                feedAdapter = new FeedAdapter(getApplicationContext(), mediaList);
                feedAdapter.setOnFeedItemClickListener(InstagramActivity.this);
                rvFeed.setAdapter(feedAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), error.getResponse().getReason(), Toast.LENGTH_LONG).show();
            }
        });
    }

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

    @Override
    public void onItemClick(View v, int position) {

        ImageView photo = (ImageView) ((View) v.getParent()).findViewById(R.id.photo);
        if ((photo.getDrawable())==null){
            Toast.makeText(getApplicationContext(),"Image is still loading.",Toast.LENGTH_LONG).show();
        }else {
            Intent intent = new Intent();
            intent.setClass(this, DetailActivity.class);
            if (mediaList.get(position).getCaption() != null) {
                intent.putExtra("text", mediaList.get(position).getCaption().getText());
            }
            intent.putExtra("photo", R.id.photo);
            intent.putExtra("activity", "main");
            sPhotoCache.put(intent.getIntExtra("photo", -1), ((BitmapDrawable) photo.getDrawable()).getBitmap());

            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ViewGroup) photo.getParent()).setTransitionGroup(false);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, photo, "photo");
                startActivity(intent, options.toBundle());
            } else {
                // Implement this feature without material design
                startActivity(intent);
            }
        }
    }
}
