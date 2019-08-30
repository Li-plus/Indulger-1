package com.inftyloop.indulger.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.OnClick;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.activity.MainActivity;
import com.inftyloop.indulger.adapter.BaseNewsAdapter;
import com.inftyloop.indulger.adapter.FavoriteItemAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.OnNewsDetailCallback;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.model.entity.NewsFavEntry;
import com.inftyloop.indulger.ui.NewsDetailHeaderView;
import com.inftyloop.indulger.util.FileUtils;
import com.inftyloop.indulger.util.GlideApp;
import com.inftyloop.indulger.util.ShareUtils;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.litepal.LitePal;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    @BindView(R.id.tv_view_original)
    TextView mViewOriginal;
    private boolean isFav = false;
    private ShareUtils shareUtils;
    private Bitmap mMainImage;
    private boolean isLoadFromFav;
    private NewsFavEntry mFavEntry;
    private NewsEntry mEntry;

    private void showSharingView() {
        final int TAG_SHARE_WECHAT_FRIEND = 0;
        final int TAG_SHARE_WECHAT_MOMENT = 1;
        final int TAG_SHARE_WEIBO = 2;
        final int TAG_SHARE_EMAIL = 3;
        String content = isLoadFromFav ?
                mFavEntry.getContent().replaceAll("<.*?>", "") :
                mEntry.getContent().replaceAll("<.*?>", "");
        String gist = content.substring(0, Math.min(100, content.length())).replaceAll("\n", "").trim();
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
                            if (shareUtils.getIwxapi() != null) {
                                if (!shareUtils.getIwxapi().isWXAppInstalled()) {
                                    QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(R.string.share_wechat_not_installed), Toast.LENGTH_SHORT).show();
                                } else {
                                    shareUtils.shareToWeChatFriends(isLoadFromFav ? mFavEntry.getUrl() : mEntry.getUrl(), isLoadFromFav ? mFavEntry.getTitle() : mEntry.getTitle(), gist, mMainImage);
                                }
                            }
                            break;
                        case TAG_SHARE_WECHAT_MOMENT:
                            if (shareUtils.getIwxapi() != null) {
                                if (!shareUtils.getIwxapi().isWXAppInstalled()) {
                                    QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(R.string.share_wechat_not_installed), Toast.LENGTH_SHORT).show();
                                } else {
                                    shareUtils.shareToWeChatMoments(isLoadFromFav ? mFavEntry.getUrl() : mEntry.getUrl(), isLoadFromFav ? mFavEntry.getTitle() : mEntry.getTitle(), gist, mMainImage);
                                }
                            }
                            break;
                        case TAG_SHARE_WEIBO:
                            Activity act = getActivity();
                            if(act instanceof MainActivity) {
                                MainActivity ac = (MainActivity)act;
                                StringBuilder bs = new StringBuilder();
                                bs.append(isLoadFromFav ? mFavEntry.getTitle() : mEntry.getTitle());
                                bs.append("\n");
                                bs.append(getString(R.string.original_article_link, isLoadFromFav ? mFavEntry.getUrl() : mEntry.getUrl()));
                                shareUtils.shareToWeibo(ac.shareHandler, bs.toString(), mMainImage);
                            }
                            break;
                        case TAG_SHARE_EMAIL:
                            Intent email = new Intent(Intent.ACTION_SENDTO);
                            email.setType("text/plain");
                            email.setData(Uri.parse("mailto:"));
                            email.putExtra(Intent.EXTRA_SUBJECT, isLoadFromFav ? mFavEntry.getTitle() : mEntry.getTitle());
                            StringBuilder bs = new StringBuilder();
                            bs.append(content);
                            bs.append("\n");
                            bs.append(getString(R.string.original_article_link, isLoadFromFav ? mFavEntry.getUrl() : mEntry.getUrl()));
                            email.putExtra(Intent.EXTRA_TEXT, bs.toString());
                            try {
                                startActivity(Intent.createChooser(email, getString(R.string.send_email_prompt)));
                            } catch (ActivityNotFoundException ex) {
                                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.no_email_client_installed), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }).build().show();
    }

    private void toggleNewsFav(NewsEntry entry, boolean fav) {
        if(!fav) {
            LitePal.deleteAll(NewsFavEntry.class, "uuid = ?", entry.getUuid());
            isFav = false;
            QMUITipDialog.Builder.
                    makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING,
                            getString(R.string.remove_from_fav_success), Toast.LENGTH_SHORT).show();
            mFavBtn.setImageResource(R.drawable.ic_favorite);
        } else {
            QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING,
                    getString(R.string.add_to_fav_success), Toast.LENGTH_SHORT).show();
            mFavBtn.setImageResource(R.drawable.ic_favorite_fill);
            new Thread(() -> {
                NewsFavEntry favEntry = new NewsFavEntry(entry);
                favEntry.save();
                isFav = true;
            }).start();
        }
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

        Bundle bundle = getArguments();
        if(bundle != null && bundle.getBoolean(Definition.IS_FAV_ADAPTER, false)) {
            isFav = true;
            isLoadFromFav = true;
            mFavEntry = FavoriteItemAdapter.currentFavEntry;
            mFavBtn.setImageResource(R.drawable.ic_favorite_fill);
            mFavBtn.setOnClickListener(v -> {
                isFav = !isFav;
                if(isFav)
                    mFavEntry.save();
                else
                    mFavEntry.delete();
                QMUITipDialog.Builder.
                        makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING,
                                getString(isFav ? R.string.add_to_fav_success : R.string.remove_from_fav_success), Toast.LENGTH_SHORT).show();
                mFavBtn.setImageResource(isFav ? R.drawable.ic_favorite_fill : R.drawable.ic_favorite);
            });
        } else {
            mEntry = BaseNewsAdapter.currentNewsEntry;
            NewsFavEntry favEntry = LitePal.where("uuid = ?", mEntry.getUuid()).findFirst(NewsFavEntry.class);
            isFav = favEntry != null;
            mFavBtn.setImageResource(isFav ? R.drawable.ic_favorite_fill : R.drawable.ic_favorite);
            mFavBtn.setOnClickListener(v -> {
                toggleNewsFav(mEntry, !isFav);
            });
        }
        shareUtils = new ShareUtils();
        shareUtils.regToWX(getContext());
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isLoadFromFav) {
            if(mEntry.getImageUrls().size() > 0) {
                GlideApp.with(getContext()).asBitmap().load(mEntry.getImageUrls().get(0)).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mMainImage = resource;
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
            }
            onGetNewsDetailSuccess(mEntry);
        } else {
            if(mFavEntry.getImgUrls().size() > 0) {
                try {
                    String url = mFavEntry.getImgUrls().get(0);
                    String fname = url.replaceFirst("http://127.0.0.1/", "").replaceFirst(":", "_");
                    File f = new File(MainApplication.getContext().getDataDir().getAbsolutePath() + "/" + "favImgs", fname);
                    mMainImage = BitmapFactory.decodeFile(f.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            onGetNewsDetailSuccess(new NewsEntry(mFavEntry));
        }
    }

    @Override
    public void onGetNewsDetailSuccess(NewsEntry detail) {
        mHeaderView.setNewsDetail(detail, null);
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

    @OnClick(R.id.tv_view_original)
    void viewOriginal() {
        String url = isLoadFromFav ? mFavEntry.getUrl() : mEntry.getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
