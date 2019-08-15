package com.inftyloop.indulger.fragment;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.util.BaseFragment;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.sunfusheng.glideimageview.GlideImageLoader;
import com.sunfusheng.glideimageview.progress.CircleProgressView;
import com.sunfusheng.glideimageview.util.DisplayUtil;

public class BigImageFragment extends BaseFragment {
    public static final String IMG_URL = "imgUrl";
    @BindView(R.id.pv_pic)
    PhotoView mIvPic;
    @BindView(R.id.progressView)
    CircleProgressView mCircleProgressView;

    @Override
    protected int getLayoutId() {
        return R.layout.big_image;
    }

    @Override
    public void initListener() {
        mIvPic.setOnPhotoTapListener((view, x, y) -> mActivity.finish());
    }

    @Override
    protected void loadData() {
        String imgUrl = getArguments().getString(IMG_URL);
        GlideImageLoader imageLoader = GlideImageLoader.create(mIvPic);
        imageLoader.setOnGlideImageViewListener(imgUrl, (int percent, boolean isDone, GlideException exception) -> {
            if (exception != null && !TextUtils.isEmpty(exception.getMessage())) {
                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.bigimage_network_exception), Toast.LENGTH_SHORT).show();
            }
            mCircleProgressView.setVisibility(isDone ? View.GONE : View.VISIBLE);
            mCircleProgressView.setProgress(percent);
        });
        RequestOptions options = imageLoader.requestOptions(R.color.placeholder_color)
                .centerCrop().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        RequestBuilder<Drawable> requestBuilder = imageLoader.requestBuilder(imgUrl, options);
        requestBuilder.transition(DrawableTransitionOptions.withCrossFade())
                .into(new SimpleTarget<Drawable>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        if (resource.getIntrinsicHeight() > DisplayUtil.getScreenHeight(mActivity)) {
                            mIvPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                        requestBuilder.into(mIvPic);
                    }
                });
    }
}
