package com.inftyloop.indulger.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class NewsDetailFragment extends QMUIFragment {
    private final static String TAG = NewsDetailFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.iv_fav)
    ImageView mFavBtn;
    @BindView(R.id.iv_share)
    ImageView mShareBtn;
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
        mShareBtn.setOnClickListener(v -> showSharingView());
        mFavBtn.setOnClickListener(v -> {
            mFavBtn.setImageResource(isFav ? R.drawable.ic_favorite : R.drawable.ic_favorite_fill);
            isFav = !isFav;
            QMUITipDialog dialog = new QMUITipDialog.Builder(getContext())
                        .setTipWord(getString(isFav ? R.string.add_to_fav_success : R.string.remove_from_fav_success))
                        .create();
            dialog.show();
            mFavBtn.postDelayed(dialog::dismiss, 1000);
        });
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SCALE_TRANSITION_CONFIG;
    }
}
