package com.instagram.instagram.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;

import com.instagram.instagram.R;
import com.instagram.instagram.adapter.TransitionAdapter;
import com.instagram.instagram.utils.Utils;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.photo)
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.inject(this);

        updateStatusBarColor();
        toolbar.getBackground().setAlpha(0);
        toolbar.setNavigationIcon(R.drawable.ic_up);

        final Bitmap bitmapPhoto = setupPhoto(getIntent().getIntExtra("photo", R.id.photo));
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, FullscreenImageActivity.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });

        colorize(bitmapPhoto);
        setupText();
        applySystemWindowsBottomInset(R.id.container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getEnterTransition().addListener(new TransitionAdapter() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTransitionEnd(Transition transition) {
                    Drawable mDrawable = new BitmapDrawable(getResources(), bitmapPhoto);
                    ObjectAnimator color = ObjectAnimator.ofArgb(mDrawable, "tint",
                            getResources().getColor(R.color.photo_tint), 0);
                    color.start();

                    getWindow().getEnterTransition().removeListener(this);
                }
            });
        } else {
            // Implement this feature without material design
            ImageView imageView = (ImageView) findViewById(R.id.photo);
            imageView.setImageBitmap(bitmapPhoto);

        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ImageView imageView = (ImageView) findViewById(R.id.photo);
            ObjectAnimator color = ObjectAnimator.ofArgb(imageView.getDrawable(), "tint",
                    0, getResources().getColor(R.color.photo_tint));
            color.addListener(new AnimatorListenerAdapter() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onAnimationEnd(Animator animation) {
                    finishAfterTransition();
                }
            });
            color.start();
        }else {
            finish();
        }
    }

    private void setupText() {
        TextView titleView = (TextView) findViewById(R.id.title);
        if (getIntent().getStringExtra("text")!=null)
            titleView.setText(getIntent().getStringExtra("text"));

//        TextView descriptionView = (TextView) findViewById(R.id.description);
//        if (getIntent().getStringExtra("comments")!=null)
//            descriptionView.setText(getIntent().getStringExtra("comments"));
    }

    private void applySystemWindowsBottomInset(int container) {
        View containerView = findViewById(container);
        containerView.setFitsSystemWindows(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    if (metrics.widthPixels < metrics.heightPixels) {
                        view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
                    } else {
                        view.setPadding(0, 0, windowInsets.getSystemWindowInsetRight(), 0);
                    }
                    return windowInsets.consumeSystemWindowInsets();
                }
            });
        }
    }

    private void colorize(Bitmap photo) {
        Palette palette = Palette.generate(photo);
        applyPalette(palette);
    }

    private void applyPalette(Palette palette) {
        getWindow().setBackgroundDrawable(new ColorDrawable(palette.getDarkMutedColor(0x000000)));
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setTextColor(palette.getVibrantColor(0x000000));

        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setTextColor(palette.getLightVibrantColor(0x000000));

    }

    private Bitmap setupPhoto(int resource) {

        Bitmap bitmap = null;
        if (getIntent().getStringExtra("activity").equals("main")){
            bitmap = InstagramActivity.sPhotoCache.get(resource);
        }
        ((ImageView) findViewById(R.id.photo)).setImageBitmap(bitmap);
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

}