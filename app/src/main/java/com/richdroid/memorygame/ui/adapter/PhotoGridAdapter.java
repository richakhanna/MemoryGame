package com.richdroid.memorygame.ui.adapter;

import android.content.Context;
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

import java.util.List;

/**
 * Created by richa.khanna on 3/18/16.
 */
public class PhotoGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context mContext;
    private static List<String> mPhotoUrlList;
    // Allows to remember the last item shown on screen
    private int lastAnimatedItemPosition = -1;
    //public static final String ARG_MOVIE_DETAIL = "MOVIE_DETAIL";


    // Provide a suitable constructor (depends on the kind of dataset)
    public PhotoGridAdapter(Context context, List<String> photoUrlList) {
        mContext = context;
        mPhotoUrlList = photoUrlList;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class PhotoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        //private TextView mTVTitle;
        private CardView mCardView;
        private ImageView mIVThumbNail;
        //private TextView mTVRating;


        public PhotoViewHolder(View view) {
            super(view);
            //this.mTVTitle = (TextView) view.findViewById(R.id.tv_title);
            this.mCardView = (CardView) view.findViewById(R.id.card_view);
            this.mIVThumbNail = (ImageView) view.findViewById(R.id.iv_game_pic);
            //this.mTVRating = (TextView) view.findViewById(R.id.tv_rating);
            this.mIVThumbNail.setOnClickListener(this);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();

            switch (view.getId()) {
//                case R.id.iv_game_pic:
//
//
//                    break;

                default:
                    Toast.makeText(mContext, "You clicked at position " + itemPosition +
                            " on game pic." , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int itemPosition = getAdapterPosition();
            return true;
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
        //cusHolder.mTVTitle.setText(mDatasetList.get(position).getTitle());
        //cusHolder.mTVRating.setText(String.valueOf(mDatasetList.get(position).getVoteAverage()));
        String completePosterPath = mPhotoUrlList.get(position);
        PabloPicasso.with(mContext).load(completePosterPath).placeholder(R.drawable.bg_grey_placeholder)
                .into(cusHolder.mIVThumbNail);
        cusHolder.mIVThumbNail.setVisibility(View.VISIBLE);
        setEnterAnimation(cusHolder.mCardView, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPhotoUrlList.size();
    }

    private void setEnterAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it will be animated
        if (position > lastAnimatedItemPosition) {
            //Animation using xml
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_up);
            viewToAnimate.startAnimation(animation);
            lastAnimatedItemPosition = position;
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
}
