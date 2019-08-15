package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.listener.OnNewsDetailCallback;
import com.inftyloop.indulger.model.entity.NewsDetail;
import com.inftyloop.indulger.ui.NewsDetailHeaderView;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

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
        NewsDetail detail = new NewsDetail();
        detail.publisher.id = "0";
        detail.publisher.displayName = "Stub publisher";
        detail.publisher.avatarUrl = "http://p1.pstatp.com/thumb/411000674c8942528d2";
        detail.title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
        detail.publishTime = 1493009672;
        detail.content = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Libero justo laoreet sit amet cursus sit amet dictum sit. Mattis rhoncus urna neque viverra justo nec. Senectus et netus et malesuada fames ac turpis egestas. Orci a scelerisque purus semper eget. Nibh tortor id aliquet lectus proin nibh nisl condimentum id. Vehicula ipsum a arcu cursus vitae congue. Mauris pharetra et ultrices neque ornare. Enim neque volutpat ac tincidunt. Nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi. Sagittis id consectetur purus ut faucibus pulvinar elementum integer enim. Euismod quis viverra nibh cras pulvinar mattis. Eget arcu dictum varius duis at. Tellus in hac habitasse platea dictumst vestibulum. Quis hendrerit dolor magna eget est lorem ipsum dolor sit. Duis ultricies lacus sed turpis tincidunt id. In ornare quam viverra orci sagittis eu volutpat. Senectus et netus et malesuada. Posuere lorem ipsum dolor sit.\n" +
                "</p>" +
                "<img src=\"https://images.unsplash.com/photo-1500322969630-a26ab6eb64cc?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1650&q=80\" img_width=\"673\" img_height=\"430\" inline=\"0\" alt=\"placeholder\" onerror=\"javascript:errorimg.call(this);\">" +
                "<p>Amet porttitor eget dolor morbi non arcu. Vivamus arcu felis bibendum ut tristique et. Morbi quis commodo odio aenean sed adipiscing. Lectus proin nibh nisl condimentum. Porttitor leo a diam sollicitudin tempor id eu nisl nunc. Cum sociis natoque penatibus et magnis dis parturient. Sed enim ut sem viverra. Tempor commodo ullamcorper a lacus vestibulum sed arcu non. Volutpat sed cras ornare arcu dui vivamus. Viverra suspendisse potenti nullam ac tortor vitae purus faucibus ornare. Pulvinar mattis nunc sed blandit libero.\n" +
                "</p>" +
                "<p>Vulputate ut pharetra sit amet aliquam. In hendrerit gravida rutrum quisque non tellus. Leo vel orci porta non pulvinar neque laoreet. Id venenatis a condimentum vitae. Urna nec tincidunt praesent semper feugiat nibh sed pulvinar proin. Diam quis enim lobortis scelerisque fermentum dui faucibus in ornare. Sed cras ornare arcu dui vivamus arcu felis bibendum. Vitae et leo duis ut diam quam. Habitant morbi tristique senectus et. Leo vel orci porta non pulvinar neque. Nulla facilisi etiam dignissim diam quis enim lobortis scelerisque. Dictumst quisque sagittis purus sit amet volutpat consequat. Turpis egestas integer eget aliquet nibh praesent tristique.\n" +
                "</p>" +
                "<p>Adipiscing tristique risus nec feugiat in. Tellus pellentesque eu tincidunt tortor aliquam. Enim ut tellus elementum sagittis vitae. Nulla posuere sollicitudin aliquam ultrices sagittis. Vitae justo eget magna fermentum iaculis eu non diam. Quis commodo odio aenean sed adipiscing diam. Sit amet consectetur adipiscing elit. Quisque sagittis purus sit amet volutpat consequat mauris nunc. Pretium viverra suspendisse potenti nullam. Pellentesque massa placerat duis ultricies lacus sed. Etiam non quam lacus suspendisse faucibus interdum posuere. Fames ac turpis egestas maecenas pharetra convallis. Egestas purus viverra accumsan in. Eget aliquet nibh praesent tristique. Aliquam malesuada bibendum arcu vitae. Id diam maecenas ultricies mi eget. Id venenatis a condimentum vitae sapien pellentesque habitant.\n" +
                "</p>" +
                "<p>Tellus mauris a diam maecenas sed. Eget lorem dolor sed viverra ipsum nunc aliquet bibendum enim. Egestas quis ipsum suspendisse ultrices gravida dictum. Volutpat est velit egestas dui id ornare arcu. Mauris cursus mattis molestie a iaculis at erat pellentesque adipiscing. Nullam non nisi est sit. Amet mauris commodo quis imperdiet massa tincidunt nunc. Enim nunc faucibus a pellentesque sit amet. Aliquet eget sit amet tellus cras. Porta non pulvinar neque laoreet suspendisse interdum. Amet dictum sit amet justo. Ac tincidunt vitae semper quis lectus nulla. Sagittis aliquam malesuada bibendum arcu vitae elementum. Nibh sit amet commodo nulla facilisi nullam vehicula ipsum. Posuere sollicitudin aliquam ultrices sagittis orci a. Habitasse platea dictumst quisque sagittis purus sit. Posuere lorem ipsum dolor sit amet consectetur adipiscing elit. Aliquet eget sit amet tellus.\n" +
                "</p>" +
                "<p>Maecenas accumsan lacus vel facilisis volutpat est velit egestas. Et malesuada fames ac turpis egestas integer eget. Eu tincidunt tortor aliquam nulla facilisi. Tellus orci ac auctor augue. Ornare arcu odio ut sem nulla pharetra diam. Felis imperdiet proin fermentum leo vel. Gravida in fermentum et sollicitudin ac orci phasellus egestas tellus. Semper viverra nam libero justo laoreet. Dictum non consectetur a erat nam at. Ultricies mi quis hendrerit dolor magna eget est lorem. Bibendum enim facilisis gravida neque convallis a cras semper. Vel orci porta non pulvinar neque laoreet suspendisse. Ut placerat orci nulla pellentesque dignissim. Tincidunt vitae semper quis lectus nulla at volutpat diam. Tellus elementum sagittis vitae et leo duis ut diam quam. Laoreet non curabitur gravida arcu ac tortor dignissim convallis.\n" +
                "</p>" +
                "<p>Massa tincidunt dui ut ornare. Pellentesque habitant morbi tristique senectus. Vitae sapien pellentesque habitant morbi tristique senectus. Feugiat vivamus at augue eget arcu dictum varius duis. Vitae sapien pellentesque habitant morbi tristique senectus et netus et. Interdum consectetur libero id faucibus nisl tincidunt. Rutrum quisque non tellus orci ac auctor augue mauris augue. Aliquam eleifend mi in nulla. At urna condimentum mattis pellentesque id nibh tortor id. Faucibus in ornare quam viverra orci sagittis eu. Varius sit amet mattis vulputate enim.</p>";
        onGetNewsDetailSuccess(detail);
    }

    @Override
    public void onGetNewsDetailSuccess(NewsDetail detail) {
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
