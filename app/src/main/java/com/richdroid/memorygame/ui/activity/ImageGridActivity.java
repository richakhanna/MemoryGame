package com.richdroid.memorygame.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.richdroid.memorygame.R;
import com.richdroid.memorygame.app.AppController;
import com.richdroid.memorygame.model.FlickrGetPhotoSizesResponse;
import com.richdroid.memorygame.model.FlickrGetPhotosResponse;
import com.richdroid.memorygame.model.Photo;
import com.richdroid.memorygame.model.Size;
import com.richdroid.memorygame.rest.ApiManager;
import com.richdroid.memorygame.rest.ApiRequester;
import com.richdroid.memorygame.ui.adapter.PhotoGridAdapter;
import com.richdroid.memorygame.utils.NetworkUtils;
import com.richdroid.memorygame.utils.ProgressBarUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ImageGridActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ImageGridActivity.class.getSimpleName();
    private static final int UPDATE_PROGRESS_BAR = 1001;
    private static final int MAX_PROGRESS = 500;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Photo> mPhotoObjectsList;
    private List<String> mPhotoUrlList;
    private ApiManager mApiMan;
    private ProgressBarUtil mProgressBarUtil;
    private LinearLayout mNoNetworkRetryLayout;
    private SharedPreferences pref;

    private Handler mTimerHandler;
    private ObjectAnimator mObjectAnimator;
    private ProgressBar mLinearProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        AppController app = ((AppController) getApplication());
        mApiMan = app.getApiManager();


        mProgressBarUtil = new ProgressBarUtil(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.photo_recycler_view);
        mNoNetworkRetryLayout = (LinearLayout) findViewById(R.id.network_retry_full_linearlayout);
        Button retryButton = (Button) findViewById(R.id.button_retry);
        retryButton.setOnClickListener(this);

        mLinearProgressBar = (ProgressBar) findViewById(R.id.progress_bar_linear);
        mLinearProgressBar.setMax(MAX_PROGRESS);
        mTimerHandler = new TimerHandler();

        Button startTimeButton = (Button) findViewById(R.id.button_start_timer);
        startTimeButton.setOnClickListener(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a grid layout manager with three columns
        mLayoutManager = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mPhotoObjectsList = new ArrayList<Photo>();
        mPhotoUrlList = new ArrayList<String>();

        // specify an adapter
        mAdapter = new PhotoGridAdapter(this, mPhotoUrlList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_retry:
                fetchPhotosIfOnline();
                break;
            case R.id.button_start_timer:
                int timeFromServer = 60;
                Message msg = mTimerHandler.obtainMessage(UPDATE_PROGRESS_BAR, timeFromServer, 0);
                mTimerHandler.sendMessage(msg);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPhotoObjectsList.isEmpty()) {
            fetchPhotosIfOnline();
        }
    }


    private void fetchPhotosIfOnline() {
        if (NetworkUtils.isOnline(this)) {
            mNoNetworkRetryLayout.setVisibility(View.GONE);
            mProgressBarUtil.show();
            mApiMan.getPhotos(new WeakReference<ApiRequester>(mPhotosRequester));
        } else {
            if (mPhotoObjectsList.isEmpty()) {
                mNoNetworkRetryLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private ApiRequester mPhotosRequester = new ApiRequester() {

        @Override
        public void onFailure(Throwable error) {
            if (isFinishing()) {
                return;
            }

            mProgressBarUtil.hide();
            // Log error here since request failed
            Log.v(TAG, "Failure : getPhotos onFailure : " + error.toString());

            mPhotoObjectsList.clear();
            mPhotoUrlList.clear();
            mNoNetworkRetryLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(Response response) {
            if (isFinishing()) {
                return;
            }

            mPhotoObjectsList.clear();
            mPhotoUrlList.clear();
            Log.v(TAG, "Success : getPhotos response : " + new Gson().toJson(response).toString());
            FlickrGetPhotosResponse flickrGetPhotosResponse = (FlickrGetPhotosResponse) response.body();

            if (flickrGetPhotosResponse != null && flickrGetPhotosResponse.getPhotoSet() != null && flickrGetPhotosResponse.getPhotoSet().getPhotos() != null && flickrGetPhotosResponse.getPhotoSet().getPhotos().size() > 0) {
                List<Photo> photosList = flickrGetPhotosResponse.getPhotoSet().getPhotos();
                Log.v(TAG, "Success : photosList size : " + photosList.size());
                for (Photo photo : photosList) {
                    mPhotoObjectsList.add(photo);
                    mApiMan.getPhotoSizesDescription(new WeakReference<ApiRequester>(mPhotoSizesRequester), photo.getId());
                }
                Log.v(TAG, "Success : mPhotoObjectsList size : " + mPhotoObjectsList.size());
            }
        }
    };

    private ApiRequester mPhotoSizesRequester = new ApiRequester() {

        @Override
        public void onFailure(Throwable error) {
            if (isFinishing()) {
                return;
            }

            mProgressBarUtil.hide();
            // Log error here since request failed
            Log.v(TAG, "Failure : getPhotoSizesDescription onFailure : " + error.toString());
            mNoNetworkRetryLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(Response response) {
            if (isFinishing()) {
                return;
            }

            mProgressBarUtil.hide();
            Log.v(TAG, "Success : getPhotoSizesDescription response : " + new Gson().toJson(response).toString());
            FlickrGetPhotoSizesResponse flickrGetPhotoSizesResponse = (FlickrGetPhotoSizesResponse) response.body();

            if (flickrGetPhotoSizesResponse != null && flickrGetPhotoSizesResponse.getAllSize() != null && flickrGetPhotoSizesResponse.getAllSize().getSizes() != null && flickrGetPhotoSizesResponse.getAllSize().getSizes().size() > 0) {
                List<Size> photoSizesList = flickrGetPhotoSizesResponse.getAllSize().getSizes();
                Log.v(TAG, "Success : photoSizesList size : " + photoSizesList.size());
                Size size = photoSizesList.get(1);
                mPhotoUrlList.add(size.getSource());
                Log.v(TAG, "Success : mPhotoUrlList size : " + mPhotoUrlList.size());
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private class TimerHandler extends Handler {

        private LinearInterpolator mInterpolator;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PROGRESS_BAR:
                    int timeDuration = msg.arg1;
                    // for smooth progress bar update.
                    mObjectAnimator =
                            ObjectAnimator.ofInt(mLinearProgressBar, "progress", 0, MAX_PROGRESS);
                    mObjectAnimator.setDuration(timeDuration*1000);
                    if (mInterpolator == null) mInterpolator = new LinearInterpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            Log.v(TAG, "getInterpolation() " + String.format("%.4f", input));
                            return input;
                        }
                    };
                    mObjectAnimator.setInterpolator(mInterpolator);
                    mObjectAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            Log.d(TAG , "Start time " + System.currentTimeMillis());
                            Log.d(TAG , "Get Duration  " + mObjectAnimator.getDuration());
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Log.d(TAG , "End time " + System.currentTimeMillis());
                            Log.d(TAG , "Get Duration  " + mObjectAnimator.getDuration());
                            Toast.makeText(ImageGridActivity.this, "Your timer has finished", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    mObjectAnimator.start();
                    break;
            }
        }
    }
}
