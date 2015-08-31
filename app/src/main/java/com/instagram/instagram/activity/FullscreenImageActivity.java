package com.instagram.instagram.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import com.instagram.instagram.R;

import butterknife.InjectView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FullscreenImageActivity extends AppCompatActivity {

    @InjectView(R.id.photo)
    ImageView fullscreenImage;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fullscreenImage = (ImageView) findViewById(R.id.photo);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        fullscreenImage.setImageBitmap(bmp);
        mAttacher = new PhotoViewAttacher(fullscreenImage);
    }
}