package com.inftyloop.indulger.fragment;

import android.view.View;
import butterknife.BindView;
import com.github.chrisbanes.photoview.PhotoView;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.ui.BaseFragment;
import com.inftyloop.indulger.ui.progress.CircleProgressView;
import com.inftyloop.indulger.util.GlideImageLoader;

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
        imageLoader.listener(imgUrl, (isComplete, percentage, bytesRead, totalBytes) -> {
            if (isComplete) {
                mCircleProgressView.setVisibility(View.GONE);
            } else {
                mCircleProgressView.setVisibility(View.VISIBLE);
                mCircleProgressView.setProgress(percentage);
            }
        }).loadImage(imgUrl, R.color.placeholder_color, null);
    }
}
