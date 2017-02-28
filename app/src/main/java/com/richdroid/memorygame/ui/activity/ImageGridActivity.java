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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.richdroid.memorygame.utils.PabloPicasso;
import com.richdroid.memorygame.utils.ProgressBarUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Response;

public class ImageGridActivity extends AppCompatActivity implements View.OnClickListener, PhotoGridAdapter.OnGuessedCorrect {

    private static final String TAG = ImageGridActivity.class.getSimpleName();
    private static final int UPDATE_PROGRESS_BAR = 1001;
    private static final int MAX_PROGRESS = 500;
    private RecyclerView mRecyclerView;
    private PhotoGridAdapter mPhotoAdapter;
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
    private ImageView mGuessImageView;
    private List<Integer> mGuessedPicIds;
    private TextView mGuessTv;

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
        mGuessTv = (TextView) findViewById(R.id.tv_guess_label);
        Button retryButton = (Button) findViewById(R.id.button_retry);
        retryButton.setOnClickListener(this);

        mLinearProgressBar = (ProgressBar) findViewById(R.id.progress_bar_linear);
        mLinearProgressBar.setMax(MAX_PROGRESS);
        mTimerHandler = new TimerHandler();

        mGuessImageView = (ImageView) findViewById(R.id.iv_guess_pic);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a grid layout manager with three columns
        mLayoutManager = new GridLayoutManager(this, 3);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mPhotoObjectsList = new ArrayList<Photo>();
        mPhotoUrlList = new ArrayList<String>();
        mGuessedPicIds = new ArrayList<>();

        // specify an adapter
        mPhotoAdapter = new PhotoGridAdapter(this, this, mPhotoUrlList);
        mRecyclerView.setAdapter(mPhotoAdapter);
        //Fetch the photos from flickr api
        fetchPhotosIfOnline();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_retry:
                fetchPhotosIfOnline();
                break;
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
                mPhotoAdapter.notifyDataSetChanged();
            }

            if (mPhotoUrlList.size() == 9) {
                //when all 9 images url has been added, start the timer
                int timeDuration = 15;
                Message msg = mTimerHandler.obtainMessage(UPDATE_PROGRESS_BAR, timeDuration, 0);
                mTimerHandler.sendMessage(msg);
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
                    mObjectAnimator.setDuration(timeDuration * 1000);
                    if (mInterpolator == null) mInterpolator = new LinearInterpolator();
                    mObjectAnimator.setInterpolator(mInterpolator);
                    mObjectAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Toast.makeText(ImageGridActivity.this, "Your timer has finished", Toast.LENGTH_SHORT).show();
                            mPhotoAdapter.onTimerHasFinished();
                            mPhotoAdapter.notifyDataSetChanged();
                            showNextGuessPic();
                            mGuessTv.setText(getString(R.string.guessing_started));
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

    public void showNextGuessPic() {
        //Also set some random image to guess pic imageview

        if (mGuessedPicIds.size() < mPhotoUrlList.size()) {
            Random randomNo = new Random();
            int picIdToUseInGuessPic;

            do {
                picIdToUseInGuessPic = randomNo.nextInt(9); //Values generate will be between 0 to 8
            }
            while (mGuessedPicIds.contains(picIdToUseInGuessPic));


            String pictureUrlPath = mPhotoUrlList.get(picIdToUseInGuessPic);
            PabloPicasso.with(ImageGridActivity.this).load(pictureUrlPath).placeholder(R.drawable.bg_grey_placeholder)
                    .into(mGuessImageView);
            mGuessedPicIds.add(picIdToUseInGuessPic);
            mPhotoAdapter.setPicIdUsedInGuessPic(picIdToUseInGuessPic);
            mPhotoAdapter.notifyDataSetChanged();
        } else {
            mGuessTv.setText(getString(R.string.guessing_completed));
        }
    }
}
