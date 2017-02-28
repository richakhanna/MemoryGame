package com.richdroid.memorygame.ui.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.richdroid.memorygame.R;
import com.richdroid.memorygame.utils.PabloPicasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by richa.khanna on 3/18/16.
 */
public class PhotoGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context mContext;
    private static List<String> mPhotoUrlList;
    // Allows to remember the last item shown on screen
    private int mLastAnimatedItemPosition = -1;
    private boolean mTimerFinished = false;
    private int mPicIdUsedInGuessPic = -1;
    //To check whether all images has been guessed
    // Initially fill all of them to false to show none of them has been guessed.
    private HashMap<Integer, Boolean> mMap;
    private OnGuessedCorrect mOnGuessedCorrect;
    private MediaPlayer mediaPlayer;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PhotoGridAdapter(Context context, OnGuessedCorrect onGuessedCorrect, List<String> photoUrlList) {
        mContext = context;
        mPhotoUrlList = photoUrlList;
        mOnGuessedCorrect = onGuessedCorrect;
        mMap = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            mMap.put(i, false);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class PhotoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private CardView mCardView;
        private ImageView mGamePicIv;

        public PhotoViewHolder(View view) {
            super(view);
            this.mCardView = (CardView) view.findViewById(R.id.card_view);
            this.mGamePicIv = (ImageView) view.findViewById(R.id.iv_game_pic);
            this.mGamePicIv.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();

            switch (view.getId()) {
                case R.id.iv_game_pic:

                    if (mTimerFinished && itemPosition == mPicIdUsedInGuessPic) {
                        Toast.makeText(mContext, "You have guessed correctly ", Toast.LENGTH_SHORT).show();
                        //And flip the image back.
                        mMap.put(itemPosition, true);
                        notifyItemChanged(itemPosition);
                        mOnGuessedCorrect.showNextGuessPic();

                        //Play the correct answer sound
                        mediaPlayer = MediaPlayer.create(mContext, R.raw.correct_answer);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.reset();
                                mp.release();
                            }

                        });
                        mediaPlayer.start();
                    }

                    break;
            }
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_view, parent, false);
        return new PhotoViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        PhotoViewHolder cusHolder = (PhotoViewHolder) holder;
        if (!mTimerFinished) {
            String pictureUrlPath = mPhotoUrlList.get(position);
            PabloPicasso.with(mContext).load(pictureUrlPath).placeholder(R.drawable.bg_grey_placeholder)
                    .into(cusHolder.mGamePicIv);
        } else {
            //if timer has finished, then set the view with respect to map boolean value,
            // like if they have been guessed, then show the image otherwise not
            if (mMap.get(position)) {
                String pictureUrlPath = mPhotoUrlList.get(position);
                PabloPicasso.with(mContext).load(pictureUrlPath).placeholder(R.drawable.bg_grey_placeholder)
                        .into(cusHolder.mGamePicIv);
            } else {
                cusHolder.mGamePicIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_grey_placeholder));
            }

        }

        cusHolder.mGamePicIv.setVisibility(View.VISIBLE);
        setEnterAnimation(cusHolder.mCardView, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPhotoUrlList.size();
    }

    public void onTimerHasFinished() {
        mTimerFinished = true;
    }

    public void setPicIdUsedInGuessPic(int picId) {
        mPicIdUsedInGuessPic = picId;
    }

    private void setEnterAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it will be animated
        if (position > mLastAnimatedItemPosition) {
            //Animation using xml
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_up);
            viewToAnimate.startAnimation(animation);
            mLastAnimatedItemPosition = position;
        }
    }

    /**
     * The view could be reused while the animation is been happening.
     * In order to avoid that is recommendable to clear the animation when is detached.
     */
    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        ((PhotoViewHolder) holder).mCardView.clearAnimation();
    }

    public interface OnGuessedCorrect {
        void showNextGuessPic();
    }
}
