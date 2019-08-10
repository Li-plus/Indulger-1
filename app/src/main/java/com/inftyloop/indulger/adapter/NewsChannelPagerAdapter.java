package com.inftyloop.indulger.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.inftyloop.indulger.fragment.NewsListFragment;
import com.inftyloop.indulger.model.entity.NewsChannel;

import java.util.ArrayList;
import java.util.List;

public class NewsChannelPagerAdapter extends FragmentStatePagerAdapter {
    private List<NewsListFragment> mFragments;
    private List<NewsChannel> mChannels;
    public NewsChannelPagerAdapter(List<NewsListFragment> fragmentList, List<NewsChannel> channelList, FragmentManager fm) {
        super(fm);
        mFragments = fragmentList != null ? fragmentList : new ArrayList<>();
        mChannels = channelList != null ? channelList : new ArrayList<>();
    }

    @Override
    public Fragment getItem(int pos) {
        return mFragments.get(pos);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mChannels.get(position).title;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
