package com.instagram.instagram.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.instagram.instagram.R;
import com.instagram.instagram.utils.Utils;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_BITMAP = "DetailActivity:bitmap";
    public static final String EXTRA_TEXT = "DetailActivity:text";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.photo)
    ImageView photo;
    @InjectView(R.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.inject(this);

        updateStatusBarColor();
        toolbar.setNavigationIcon(R.drawable.ic_up);

        photo.setImageDrawable(byteToBitmap(getIntent().getByteArrayExtra(EXTRA_BITMAP)));
        ViewCompat.setTransitionName(photo, EXTRA_BITMAP);
        colorize();
        setupText();
    }

    private void colorize() {
        final Bitmap bitmapPhoto = ((BitmapDrawable) photo.getDrawable()).getBitmap();
        Palette palette = Palette.generate(bitmapPhoto);
        applyPalette(palette);
    }

    private void setupText() {
        if (getIntent().getStringExtra(EXTRA_TEXT) != null) {
            title.setText(getIntent().getStringExtra(EXTRA_TEXT));
        }
    }

    private void applyPalette(Palette palette) {
        getWindow().setBackgroundDrawable(new ColorDrawable(palette.getDarkMutedColor(0x000000)));
        title.setTextColor(palette.getVibrantColor(0x000000));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void launch(AppCompatActivity activity, View transitionView, String text, Bitmap bitmap) {
        ((ViewGroup) transitionView.getParent()).setTransitionGroup(false);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_BITMAP);
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(EXTRA_BITMAP, bitmapToByte(bitmap));
        intent.putExtra(EXTRA_TEXT, text);
        ActivityCompat.startActivity(activity, intent, options.toBundle());

    }

    static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    Drawable byteToBitmap(byte[] byteArray){
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
         return new BitmapDrawable(getResources(), bmp);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

}