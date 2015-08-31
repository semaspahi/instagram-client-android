package com.instagram.instagram.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.instagram.instagram.R;
import com.instagram.instagram.adapter.EndlessRecyclerOnScrollListener;
import com.instagram.instagram.adapter.FeedAdapter;
import com.instagram.instagram.service.Instagram;
import com.instagram.instagram.service.model.Media;
import com.instagram.instagram.service.model.RecentByTag;
import com.instagram.instagram.view.RevealBackgroundView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener, FeedAdapter.OnFeedItemClickListener {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final String CLIENT_ID = "45ce8faf821f43d6a39f6b61bf677820";
    Instagram instagram;
    private FeedAdapter feedAdapter;
    List<Media> mediaList;
    public static SparseArray<Bitmap> sPhotoCache = new SparseArray<Bitmap>(4);

    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    String tag = "";
    String max_id;
    String min_id;

    SearchView mSearchView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        setupRevealBackground(savedInstanceState);
        setupFeed();

        String query = getIntent().getStringExtra(SearchManager.QUERY);
        query = query == null ? "" : query;
        tag = query;

        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
        }
    }

    public static void startSearchFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, SearchActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            feedAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvFeed.setVisibility(View.VISIBLE);
        } else {
            rvFeed.setVisibility(View.INVISIBLE);
        }
    }

    private void setupFeed() {

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvFeed.setLayoutManager(layoutManager);
        rvFeed.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (max_id != null) {
                    instagram.getTagsEndpoint().tagsService.getRecentMaxMinPublic(tag, CLIENT_ID, min_id, max_id, new Callback<RecentByTag>() {
                        @Override
                        public void success(RecentByTag recentByTag, Response response) {
                            max_id = recentByTag.getPagination().getNextMaxId();
//                            min_id = recentByTag.getPagination().getNextMinId();
                            mediaList.addAll(recentByTag.getMediaList());
                            feedAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getApplicationContext(), error.getResponse().getReason(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void getFeed(String tag) {
        this.tag = tag;
        instagram = new Instagram();
        instagram.getTagsEndpoint().tagsService.getRecentPublic(tag, CLIENT_ID, new Callback<RecentByTag>() {
            @Override
            public void success(RecentByTag popular, Response response) {
                max_id = popular.getPagination().getNextMaxId();
//                min_id = popular.getPagination().getNextMinId();
                mediaList = popular.getMediaList();
                if (mediaList.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No result found.", Toast.LENGTH_LONG).show();
                } else {
                    feedAdapter = new FeedAdapter(getApplicationContext(), mediaList);
                    feedAdapter.setOnFeedItemClickListener(SearchActivity.this);
                    rvFeed.setAdapter(feedAdapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), error.getResponse().getReason(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        String query = intent.getStringExtra(SearchManager.QUERY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (mediaList != null) {
                    setupFeed();
                    mediaList.clear();
                    feedAdapter.notifyDataSetChanged();
                }
                getFeed(s);
                searchView.clearFocus();
                searchView.setQuery(s, false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                return false;
            }
        });

        if (!TextUtils.isEmpty(tag)) {
            searchView.setQuery(tag, false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View v, int position) {
        ImageView photo = (ImageView) ((View) v.getParent()).findViewById(R.id.photo);
        if ((photo.getDrawable()) == null) {
            Toast.makeText(getApplicationContext(), "Image is still loading.", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent();
            intent.setClass(this, DetailActivity.class);
            if (mediaList.get(position).getCaption() != null) {
                intent.putExtra("text", mediaList.get(position).getCaption().getText());
            }
            intent.putExtra("photo", R.id.photo);
            intent.putExtra("activity", "search");
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
