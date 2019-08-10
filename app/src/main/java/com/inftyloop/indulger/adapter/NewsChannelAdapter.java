package com.inftyloop.indulger.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.listener.OnNewsTypeDragListener;
import com.inftyloop.indulger.model.entity.NewsChannel;

import java.util.List;

public class NewsChannelAdapter extends BaseMultiItemQuickAdapter<NewsChannel, BaseViewHolder> {
    private static final long DURATION = 100;
    private static final int ANIM_DURATION = 300;
    private BaseViewHolder mHolder;
    private boolean mIsEdit;
    private long mStartTime;
    private RecyclerView mRecyclerView;
    private OnNewsTypeDragListener mDragListener;
    private Context ctx;

    public void setOnNewsTypeDragListener(OnNewsTypeDragListener l) {
        mDragListener = l;
    }

    public NewsChannelAdapter(List<NewsChannel> data, Context c) {
        super(data);
        mIsEdit = false;
        ctx = c;
        addItemType(NewsChannel.TYPE_MY, R.layout.item_channel_title);
        addItemType(NewsChannel.TYPE_MY_LIST, R.layout.item_channel);
        addItemType(NewsChannel.TYPE_RECOMMENDED, R.layout.item_channel_title);
        addItemType(NewsChannel.TYPE_RECOMMENDED_LIST, R.layout.item_channel);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mRecyclerView = (RecyclerView) parent;
        return super.onCreateViewHolder(parent, viewType);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, NewsChannel channel) {
        switch (helper.getItemViewType()) {
            case NewsChannel.TYPE_MY:
                mHolder = helper;
                helper.setText(R.id.tvTitle, channel.title)
                        .setOnClickListener(R.id.tvEdit, v -> {
                            startEditMode(!mIsEdit); // flip state
                            helper.setText(R.id.tvEdit,
                                    ctx.getResources().getString(mIsEdit ?
                                            R.string.home_news_type_channel_edit_complete:
                                            R.string.home_news_type_channel_edit));
                        });
                break;
            case NewsChannel.TYPE_RECOMMENDED:
                helper.setText(R.id.tvTitle, channel.title).setGone(R.id.tvEdit, false);
                break;
            case NewsChannel.TYPE_MY_LIST:
                helper.setVisible(R.id.ivDelete, mIsEdit && !(channel.title.equals(ctx.getString(R.string.home_news_type_channel_recommend))))
                        .setOnLongClickListener(R.id.rlItemView, v -> {
                            if (!mIsEdit) {
                                startEditMode(true);
                                mHolder.setText(R.id.tvEdit, ctx.getString(R.string.home_news_type_channel_edit_complete));
                            }
                            if (mDragListener != null)
                                mDragListener.onStartDrag(helper);
                            return true;
                        })
                        .setOnTouchListener(R.id.tvChannel, new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent evt) {
                                if (!mIsEdit) return false;
                                switch (evt.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        mStartTime = System.currentTimeMillis();
                                        break;
                                    case MotionEvent.ACTION_MOVE:
                                        if (System.currentTimeMillis() - mStartTime > DURATION && mDragListener != null)
                                            mDragListener.onStartDrag(helper);
                                        break;
                                    case MotionEvent.ACTION_CANCEL:
                                    case MotionEvent.ACTION_UP:
                                        mStartTime = 0;
                                        break;
                                }
                                return false;
                            }
                        }).getView(R.id.ivDelete).setTag(true);
                helper.setText(R.id.tvChannel, channel.title).setOnClickListener(R.id.ivDelete, v -> {
                    if (mIsEdit) {
                        int recommendFirstPos = getRecommendFirstPos();
                        int curPos = helper.getAdapterPosition() - getHeaderLayoutCount();
                        View tV = mRecyclerView.getLayoutManager().findViewByPosition(recommendFirstPos);
                        View cV = mRecyclerView.getLayoutManager().findViewByPosition(curPos);
                        if (mRecyclerView.indexOfChild(tV) >= 0 && recommendFirstPos != -1) {
                            RecyclerView.LayoutManager man = mRecyclerView.getLayoutManager();
                            int spanCount = ((GridLayoutManager) man).getSpanCount();
                            int tX = tV.getLeft();
                            int tY = tV.getTop();
                            int mChanSize = getMyChannelSize();
                            if (mChanSize % spanCount == 1)
                                tY -= tV.getHeight();
                            channel.setItemType(NewsChannel.TYPE_RECOMMENDED_LIST);
                            if (mDragListener != null)
                                mDragListener.onMoveToRecommendedChannel(curPos, recommendFirstPos - 1);
                            startAnimation(cV, tX, tY);
                        } else {
                            channel.setItemType(NewsChannel.TYPE_RECOMMENDED_LIST);
                            if (recommendFirstPos == -1) recommendFirstPos = mData.size();
                            if (mDragListener != null)
                                mDragListener.onMoveToRecommendedChannel(curPos, recommendFirstPos - 1);
                        }
                    }
                });
                break;
            case NewsChannel.TYPE_RECOMMENDED_LIST:
                helper.setText(R.id.tvChannel, channel.title).setVisible(R.id.ivDelete, false)
                        .setOnClickListener(R.id.tvChannel, v -> {
                            int myLastPos = getMyLastPos();
                            int curPos = helper.getAdapterPosition() - getHeaderLayoutCount();
                            View tV = mRecyclerView.getLayoutManager().findViewByPosition(myLastPos);
                            View cV = mRecyclerView.getLayoutManager().findViewByPosition(curPos);
                            if(mRecyclerView.indexOfChild(tV) >= 0 && myLastPos != -1) {
                                RecyclerView.LayoutManager man = mRecyclerView.getLayoutManager();
                                int spanCnt = ((GridLayoutManager)man).getSpanCount();
                                int tX = tV.getLeft() + tV.getWidth();
                                int tY = tV.getTop();
                                int myChaSize = getMyChannelSize();
                                if(myChaSize % spanCnt == 0) {
                                    View lastFourth = mRecyclerView.getLayoutManager().findViewByPosition(myLastPos - 3);
                                    tX = lastFourth.getLeft();
                                    tY = lastFourth.getTop() + lastFourth.getHeight();
                                }
                                channel.setItemType(NewsChannel.TYPE_MY_LIST);
                                if(mDragListener != null)
                                    mDragListener.onMoveToMyChannel(curPos, myLastPos + 1);
                                startAnimation(cV, tX, tY);
                            } else {
                                channel.setItemType(NewsChannel.TYPE_MY_LIST);
                                if(myLastPos == -1) myLastPos = 0;
                                if(mDragListener != null)
                                    mDragListener.onMoveToMyChannel(curPos, myLastPos + 1);
                            }
                        });
        }
    }

    public int getMyChannelSize() {
        int sz = 0;
        for (NewsChannel x : mData) {
            if (x.getItemType() == NewsChannel.TYPE_MY_LIST)
                ++sz;
        }
        return sz;
    }

    private void startAnimation(final View curView, int tX, int tY) {
        final ViewGroup parent = (ViewGroup) mRecyclerView.getParent();
        final ImageView mirrorView = addMirrorView(parent, curView);
        TranslateAnimation anim = getTranslateAnimation(tX - curView.getLeft(), tY - curView.getTop());
        curView.setVisibility(View.INVISIBLE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                parent.removeView(mirrorView);
                if (curView.getVisibility() == View.INVISIBLE)
                    curView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mirrorView.startAnimation(anim);
    }

    @SuppressWarnings("deprecation")
    private ImageView addMirrorView(ViewGroup parent, View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        ImageView mv = new ImageView(view.getContext());
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        mv.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(false);
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        int[] parent_loc = new int[2];
        mRecyclerView.getLocationOnScreen(parent_loc);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
        params.setMargins(loc[0], loc[1] - parent_loc[1], 0, 0);
        parent.addView(mv, params);
        return mv;
    }

    private TranslateAnimation getTranslateAnimation(float tX, float tY) {
        TranslateAnimation anim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, tX,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, tY);
        anim.setDuration(ANIM_DURATION);
        anim.setFillAfter(true);
        return anim;
    }

    private int getRecommendFirstPos() {
        for (int i = 0; i < mData.size(); ++i) {
            if (NewsChannel.TYPE_RECOMMENDED_LIST == mData.get(i).getItemType())
                return i;
        }
        return -1;
    }

    private int getMyLastPos() {
        for (int i = mData.size() - 1; i > -1; --i) {
            if (NewsChannel.TYPE_MY_LIST == (mData.get(i)).getItemType())
                return i;
        }
        return -1;
    }

    private void startEditMode(boolean edit) {
        mIsEdit = edit;
        int cntVisChild = mRecyclerView.getChildCount();
        for (int i = 0; i < cntVisChild; ++i) {
            View view = mRecyclerView.getChildAt(i);
            ImageView imgEdit = (ImageView) view.findViewById(R.id.ivDelete);
            if (imgEdit != null) {
                boolean isVis = imgEdit.getTag() != null && (boolean) imgEdit.getTag();
                imgEdit.setVisibility(isVis && edit && !mData.get(i).title.equals(ctx.getResources().getString(R.string.home_news_type_channel_recommend)) ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }
}
