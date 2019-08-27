package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.BaseNewsAdapter;
import com.inftyloop.indulger.listener.OnNewsDetailCallback;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.ui.NewsDetailHeaderView;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsDetailFragment extends QMUIFragment implements OnNewsDetailCallback {
    private final static String TAG = NewsDetailFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.iv_fav)
    ImageView mFavBtn;
    @BindView(R.id.iv_share)
    ImageView mShareBtn;
    @BindView(R.id.fl_content)
    NewsDetailHeaderView mHeaderView;
    private boolean isFav = false;

    private void showSharingView() {
        final int TAG_SHARE_WECHAT_FRIEND = 0;
        final int TAG_SHARE_WECHAT_MOMENT = 1;
        final int TAG_SHARE_WEIBO = 2;
        final int TAG_SHARE_EMAIL = 3;
        QMUIBottomSheet.BottomGridSheetBuilder builder = new QMUIBottomSheet.BottomGridSheetBuilder(getActivity());
        builder.addItem(R.mipmap.icon_more_operation_share_friend, getString(R.string.share_wechat_friend), TAG_SHARE_WECHAT_FRIEND, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.icon_more_operation_share_moment, getString(R.string.share_wechat_moment), TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.icon_more_operation_share_weibo, getString(R.string.share_weibo), TAG_SHARE_WEIBO, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.icon_more_operation_share_email, getString(R.string.share_email), TAG_SHARE_EMAIL, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .setOnSheetItemClickListener((QMUIBottomSheet dialog, View itemView) -> {
                    dialog.dismiss();
                    int tag = (int) itemView.getTag();
                    switch (tag) {
                        case TAG_SHARE_WECHAT_FRIEND:
                            Toast.makeText(getActivity(), getString(R.string.share_wechat_friend), Toast.LENGTH_SHORT).show();
                            break;
                        case TAG_SHARE_WECHAT_MOMENT:
                            Toast.makeText(getActivity(), getString(R.string.share_wechat_moment), Toast.LENGTH_SHORT).show();
                            break;
                        case TAG_SHARE_WEIBO:
                            Toast.makeText(getActivity(), getString(R.string.share_weibo), Toast.LENGTH_SHORT).show();
                            break;
                        case TAG_SHARE_EMAIL:
                            Toast.makeText(getActivity(), getString(R.string.share_email), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }).build().show();
    }

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.news_detail, null);
        ButterKnife.bind(this, root);
        mTopBar.addLeftBackImageButton().setOnClickListener((View v) -> {
            popBackStack();
        });
        mTopBar.setTitle(R.string.news_detail_title);
        mShareBtn.setOnClickListener(v -> showSharingView());
        mFavBtn.setOnClickListener(v -> {
            mFavBtn.setImageResource(isFav ? R.drawable.ic_favorite : R.drawable.ic_favorite_fill);
            isFav = !isFav;
            Toast toast = QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(isFav ? R.string.add_to_fav_success : R.string.remove_from_fav_success),
                    Toast.LENGTH_SHORT);
            toast.show();
        });
        return root;
    }

    // TODO - remove this
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NewsEntry detail = BaseNewsAdapter.currentNewsEntry;
        onGetNewsDetailSuccess(detail);
    }

    @Override
    public void onGetNewsDetailSuccess(NewsEntry detail) {
        mHeaderView.setNewsDetail(detail, new NewsDetailHeaderView.LoadWebListener() {
            @Override
            public void onLoaded() {
                // TODO, show content
            }
        });
    }

    @Override
    public void onError() {
        // TODO - switch to retry view
        Log.e(TAG, "Error loading news detail");
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SCALE_TRANSITION_CONFIG;
    }
}
