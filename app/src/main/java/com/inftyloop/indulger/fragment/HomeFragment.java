package com.inftyloop.indulger.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.adapter.NewsChannelPagerAdapter;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.listener.OnNewsTypeListener;
import com.inftyloop.indulger.model.entity.NewsChannel;
import com.inftyloop.indulger.util.BaseFragment;
import com.inftyloop.indulger.util.ConfigManager;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends BaseFragment implements OnNewsTypeListener {
    private final static String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.tabSegment) QMUITabSegment mTabChannel;
    @BindView(R.id.operationImageView) ImageView mAddChannelIV;
    @BindView(R.id.contentViewPager) ViewPager mContentPager;

    private List<NewsChannel> mSelectedChannels = new ArrayList<>();
    private List<NewsChannel> mUnselectedChannels = new ArrayList<>();
    private List<NewsListFragment> mFragments = new ArrayList<>();
    private Gson mGson = new Gson();
    private NewsChannelPagerAdapter mPagerAdapter;
    private String[] mChannelCodes;

    @Override
    public void initView(View rootView) {
        super.initView(rootView);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(25, 25, 25, 25);
        View view = getLayoutInflater().inflate(R.layout.searchbar_home, null);
        view.setOnClickListener( (v) -> {
            QMUIFragment fragment = new HomeSearchFragment();
            startFragment(fragment);
        });
        mTopBar.addRightView(view, R.id.topbar_search, lp);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void initData() {
        String selectedChannelJson = ConfigManager.getString(Definition.SETTINGS_SELECTED_CHANNEL_JSON, "");
        String unselectedChannelJson = ConfigManager.getString(Definition.SETTINGS_UNSELECTED_CHANNEL_JSON, "");
        String[] channels = getResources().getStringArray(R.array.channel);
        String[] channelCodes = getResources().getStringArray(R.array.channel_code);
        HashMap<String, String> code2name = new HashMap<>();
        for(int i = 0; i < channelCodes.length; ++i) {
            code2name.put(channelCodes[i], channels[i]);
        }
        if(TextUtils.isEmpty(selectedChannelJson) || TextUtils.isEmpty(unselectedChannelJson)) {
            for(int i = 0; i < channelCodes.length; ++i)
                mSelectedChannels.add(new NewsChannel(channels[i], channelCodes[i]));
            selectedChannelJson = mGson.toJson(mSelectedChannels);
            ConfigManager.putString(Definition.SETTINGS_SELECTED_CHANNEL_JSON, selectedChannelJson);
        } else {
            List<NewsChannel> selectedChannel = mGson.fromJson(selectedChannelJson, new TypeToken<List<NewsChannel>>(){}.getType());
            List<NewsChannel> unselectedChannel = mGson.fromJson(unselectedChannelJson, new TypeToken<List<NewsChannel>>(){}.getType());
            for(NewsChannel ch : selectedChannel) {
                ch.title = code2name.getOrDefault(ch.channelCode, "");
            }
            for(NewsChannel ch: unselectedChannel) {
                ch.title = code2name.getOrDefault(ch.channelCode, "");
            }
            mSelectedChannels.addAll(selectedChannel);
            mUnselectedChannels.addAll(unselectedChannel);
        }
        mChannelCodes = getResources().getStringArray(R.array.channel_code);
        for(NewsChannel ch : mSelectedChannels) {
            NewsListFragment frag = new NewsListFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Definition.IS_RECOMMEND, ch.channelCode.equals(getString(R.string.channel_code_recommend)));
            bundle.putString(Definition.CHANNEL_NAME, ch.title);
            bundle.putBoolean(Definition.IS_VIDEO_LIST, ch.channelCode.equals(getString(R.string.channel_code_video)));
            frag.setArguments(bundle);
            mFragments.add(frag);
        }
    }

    @Override
    protected void loadData() {}

    @Override
    protected int getLayoutId() {
        return R.layout.home;
    }

    @Override
    public void initListener() {
        mPagerAdapter = new NewsChannelPagerAdapter(mFragments, mSelectedChannels, getChildFragmentManager());
        mContentPager.setAdapter(mPagerAdapter);
        int normalColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.tabbar_normal_text_color);
        int selectedColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.tabbar_selected_text_color);
        mTabChannel.setDefaultNormalColor(normalColor);
        mTabChannel.setDefaultSelectedColor(selectedColor);
        mTabChannel.setHasIndicator(true);
        mTabChannel.setIndicatorWidthAdjustContent(true);
        mTabChannel.setMode(QMUITabSegment.MODE_SCROLLABLE);
        int space = QMUIDisplayHelper.dp2px(getContext(), 16);
        mTabChannel.setItemSpaceInScrollMode(space);
        mContentPager.setOffscreenPageLimit(mSelectedChannels.size());
        mTabChannel.setupWithViewPager(mContentPager);
        mTabChannel.setPadding(space, 0, space, 0);
        mTabChannel.post(()->{
            ViewGroup vg = (ViewGroup) mTabChannel.getChildAt(0);
            vg.setMinimumWidth(vg.getMeasuredWidth() + mAddChannelIV.getMeasuredWidth());
        });
        // TODO
    }

    public String getCurrentChannelCode() {
        int curr = mContentPager.getCurrentItem();
        return mSelectedChannels.get(curr).channelCode;
    }

    @OnClick({R.id.operationImageView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.operationImageView:
                ChannelDialogFragment dialog = ChannelDialogFragment.newInstance(mSelectedChannels, mUnselectedChannels);
                dialog.setOnNewsTypeListener(this);
                dialog.show(getChildFragmentManager(), "CHANNEL");
                dialog.setOnDismissListener(d -> {
                    mPagerAdapter.notifyDataSetChanged();
                    mContentPager.setOffscreenPageLimit(mSelectedChannels.size());
                    mTabChannel.selectTab(mTabChannel.getSelectedIndex());
                    ViewGroup vg = (ViewGroup)mTabChannel.getChildAt(0);
                    vg.setMinimumWidth(0);
                    vg.measure(0, 0);
                    vg.setMinimumWidth(vg.getMeasuredWidth() + mAddChannelIV.getMeasuredWidth());
                    ConfigManager.putString(Definition.SETTINGS_SELECTED_CHANNEL_JSON, mGson.toJson(mSelectedChannels));
                    ConfigManager.putString(Definition.SETTINGS_UNSELECTED_CHANNEL_JSON, mGson.toJson(mUnselectedChannels));
                });
                break;
        }
    }

    @Override
    public void onItemMove(int start, int end) {
        moveList(mSelectedChannels, start, end);
        moveList(mFragments, start, end);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void onMoveToMyChannel(int start, int end) {
        NewsChannel ch = mUnselectedChannels.remove(start);
        mSelectedChannels.add(end, ch);
        NewsListFragment f = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Definition.IS_RECOMMEND, ch.channelCode.equals(getString(R.string.channel_code_recommend)));
        bundle.putString(Definition.CHANNEL_NAME, ch.title);
        bundle.putBoolean(Definition.IS_VIDEO_LIST, ch.channelCode.equals(getString(R.string.channel_code_video)));
        f.setArguments(bundle);
        mFragments.add(f);
    }

    @Override
    public void onMoveToRecommendedChannel(int start, int end) {
        mUnselectedChannels.add(end, mSelectedChannels.remove(start));
        mFragments.remove(start);
    }

    @SuppressWarnings("unchecked")
    private void moveList(List data, int st, int end) {
        Object o = data.get(st);
        data.remove(st);
        data.add(end, o);
    }
}
