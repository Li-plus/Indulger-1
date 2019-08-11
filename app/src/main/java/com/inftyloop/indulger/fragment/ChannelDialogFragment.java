package com.inftyloop.indulger.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseViewHolder;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.NewsChannelAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.ItemDragHelperCb;
import com.inftyloop.indulger.listener.OnNewsTypeDragListener;
import com.inftyloop.indulger.listener.OnNewsTypeListener;
import com.inftyloop.indulger.model.entity.NewsChannel;
import com.qmuiteam.qmui.util.QMUIResHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChannelDialogFragment extends DialogFragment implements OnNewsTypeDragListener {
    private final static String TAG = ChannelDialogFragment.class.getSimpleName();

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    private List<NewsChannel> mData = new ArrayList<>();
    private NewsChannelAdapter mAdapter;
    private ItemTouchHelper mHelper;
    private OnNewsTypeListener mNewsTypeListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    public static ChannelDialogFragment newInstance(List<NewsChannel> selected, List<NewsChannel> unselected) {
        ChannelDialogFragment dialogFragment = new ChannelDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Definition.DATA_SELECTED, (Serializable) selected);
        bundle.putSerializable(Definition.DATA_UNSELECTED, (Serializable) unselected);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public void setOnNewsTypeListener(OnNewsTypeListener l) { mNewsTypeListener = l; }

    public void setOnDismissListener(DialogInterface.OnDismissListener l) { mOnDismissListener = l; }

    private void setDataType(List<NewsChannel> d, int type) {
        for(NewsChannel c : d) {
            c.setItemType(type);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CharSequence name = QMUIResHelper.getAttrString(getContext(), R.attr.style_name);
        if(name.equals(getString(R.string.night_theme_name))) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.NightTheme);
        } else if(name.equals(getString(R.string.day_theme_name))) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if(dialog != null)
            dialog.getWindow().setWindowAnimations(R.style.dialogSlideAnim);
        return inflater.inflate(R.layout.fragment_channel, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        // process
        mData.add(new NewsChannel(NewsChannel.TYPE_MY, getResources().getString(R.string.home_news_type_my_channel), ""));
        Bundle bundle = getArguments();
        List<NewsChannel> selected = (List<NewsChannel>) bundle.getSerializable(Definition.DATA_SELECTED);
        List<NewsChannel> unselected = (List<NewsChannel>) bundle.getSerializable(Definition.DATA_UNSELECTED);
        setDataType(selected, NewsChannel.TYPE_MY_LIST);
        setDataType(unselected, NewsChannel.TYPE_RECOMMENDED_LIST);
        mData.addAll(selected);
        mData.add(new NewsChannel(NewsChannel.TYPE_RECOMMENDED, getResources().getString(R.string.home_news_type_channel_recommend), ""));
        mData.addAll(unselected);

        mAdapter = new NewsChannelAdapter(mData, getContext());
        GridLayoutManager man = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(man);
        mRecyclerView.setAdapter(mAdapter);
        man.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                int t = mAdapter.getItemViewType(i);
                return t == NewsChannel.TYPE_MY_LIST || t == NewsChannel.TYPE_RECOMMENDED_LIST ? 1 : 4;
            }
        });
        mHelper = new ItemTouchHelper(new ItemDragHelperCb(this));
        mAdapter.setOnNewsTypeDragListener(this);
        mHelper.attachToRecyclerView(mRecyclerView);
    }

    @OnClick(R.id.icon_collapse)
    public void onClick(View v) { dismiss(); }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mOnDismissListener != null)
            mOnDismissListener.onDismiss(dialog);
    }

    @Override
    public void onStartDrag(BaseViewHolder vh) {
        mHelper.startDrag(vh);
    }

    private void onMove(int st, int end) {
        NewsChannel start = mData.get(st);
        mData.remove(st);
        mData.add(end, start);
        mAdapter.notifyItemMoved(st, end);
    }

    @Override
    public void onItemMove(int start, int end) {
        if(mNewsTypeListener != null)
            mNewsTypeListener.onItemMove(start - 1, end - 1);
        onMove(start, end);
    }

    @Override
    public void onMoveToMyChannel(int start, int end) {
        onMove(start, end);
        if(mNewsTypeListener != null)
            mNewsTypeListener.onMoveToMyChannel(start - 1 - mAdapter.getMyChannelSize(), end - 1);
    }

    @Override
    public void onMoveToRecommendedChannel(int start, int end) {
        onMove(start, end);
        if(mNewsTypeListener != null)
            mNewsTypeListener.onMoveToRecommendedChannel(start - 1, end - 2 - mAdapter.getMyChannelSize());
    }
}
