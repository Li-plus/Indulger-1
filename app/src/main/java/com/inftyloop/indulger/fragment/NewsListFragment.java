package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.util.BaseFragment;

public class NewsListFragment extends BaseFragment {
    @BindView(R.id.textView) TextView tV;
    private final static String TAG = NewsListFragment.class.getSimpleName();

    @Override
    public void initView(View rootView) {
        super.initView(rootView);
        Bundle bundle = this.getArguments();
        String name = "";
        int flag = 0;
        if(bundle != null) {
            name = bundle.getString(Definition.CHANNEL_NAME);
            if(bundle.getBoolean(Definition.IS_RECOMMEND, false))
                flag |= 0x01;
            if(bundle.getBoolean(Definition.IS_VIDEO_LIST, false))
                flag |= 0x02;
        }
        tV.setText("TextView " + name + " " + ((flag & 0x01) != 0 ? "IS_RECOMMEND " : " ") + ((flag & 0x02) != 0? "IS_VIDEO" : ""));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.news_list;
    }

    @Override
    protected void loadData() {}
}
