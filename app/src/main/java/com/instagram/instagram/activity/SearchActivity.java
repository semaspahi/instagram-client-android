package com.instagram.instagram.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.instagram.instagram.R;
import com.instagram.instagram.adapter.FeedAdapter;
import com.instagram.instagram.service.Instagram;
import com.instagram.instagram.service.endpoints.TagsEndpoint;
import com.instagram.instagram.service.model.Media;
import com.instagram.instagram.service.model.RecentByTag;
import com.instagram.instagram.view.RevealBackgroundView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SearchActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener, FeedAdapter.OnFeedListener {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    TagsEndpoint tagsEndpoint;
    private FeedAdapter feedAdapter;
    List<Media> mediaList;

    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    String tag = "";
    String max_id;
    String min_id;

    GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        tagsEndpoint = new Instagram().getTagsEndpoint();

        setupRevealBackground(savedInstanceState);
        setupFeed();
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
        layoutManager = new GridLayoutManager(this, 2);
        rvFeed.setLayoutManager(layoutManager);
        feedAdapter = new FeedAdapter(getApplicationContext());
        feedAdapter.setOnFeedListener(SearchActivity.this);
        rvFeed.setAdapter(feedAdapter);
        mediaList = new ArrayList<>();
    }

    private void getFeed(String tag) {
        this.tag = tag;
        Call<RecentByTag> popular = tagsEndpoint.getPopular(tag);
        popular.enqueue(recentCallback);
    }

    Callback<RecentByTag> recentCallback = new Callback<RecentByTag>() {

        @Override
        public void onResponse(Response<RecentByTag> response, Retrofit retrofit) {

            if (response.body() != null) {
                RecentByTag recentByTag = response.body();
                max_id = recentByTag.getPagination().getNextMaxId();
                mediaList.addAll(recentByTag.getMediaList());
                if (mediaList.size() != 0) {
                    feedAdapter.addData(recentByTag.getMediaList());
                } else {
                    Snackbar.make(rvFeed, "No result found.", Snackbar.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Snackbar.make(rvFeed, "Internet connection problem :(", Snackbar.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onNewIntent(getIntent());
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
                feedAdapter.clearData();
                mediaList.clear();
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
                onBackPressed();
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
        if (photo.getDrawable() != null) {
            String text = null;
            if (mediaList.get(position).getCaption() != null) {
                text = mediaList.get(position).getCaption().getText();
            }
            DetailActivity.launch(this, photo, text, ((BitmapDrawable) photo.getDrawable()).getBitmap());
        }
    }

    @Override
    public void onLoadMore() {
        if (max_id != null) {
            Call<RecentByTag> popular = tagsEndpoint.getPopular(tag, max_id);
            popular.enqueue(recentCallback);
        }
    }
}
