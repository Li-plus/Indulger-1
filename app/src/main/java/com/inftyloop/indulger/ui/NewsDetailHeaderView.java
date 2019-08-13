package com.inftyloop.indulger.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import android.widget.FrameLayout;
import butterknife.BindView;

public class NewsDetailHeaderView extends FrameLayout {
    private static final String NICK = "news_detail";  // used to bind javascript
    @BindView(R.id.tvTitle)
    TextView mTitle;
    @BindView(R.id.ll_info)
    LinearLayout mllInfo;
    @BindView(R.id.iv_avatar)
    ImageView mAvatar;
    @BindView(R.id.tv_author)
    TextView mAuthor;
    @BindView(R.id.tv_time)
    TextView mTime;
    @BindView(R.id.wv_content)
    WebView mContent;

    private Context mContext;
    private LoadWebListener mWebListener;

    public NewsDetailHeaderView(Context ctx) {
        this(ctx, null);
    }

    public NewsDetailHeaderView(Context ctx, AttributeSet attrs) {
        this(ctx, attrs, 0);
    }

    public NewsDetailHeaderView(Context ctx, AttributeSet attrs, int defStyleAttr) {
        super(ctx, attrs, defStyleAttr);
        mContext = ctx;
    }

    private void initView() {
        inflate(getContext(), R.layout.header_news_detail, this);
        ButterKnife.bind(this, this);
    }

    public void setNewsDetail(LoadWebListener listener) {
        mWebListener = listener;
    }

    public interface LoadWebListener{
        void onLoaded();
    }
}
