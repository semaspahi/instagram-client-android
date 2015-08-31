package instagram.instagram;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InstagramActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "45ce8faf821f43d6a39f6b61bf677820";
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

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
    }

}
