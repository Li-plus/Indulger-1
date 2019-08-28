package com.inftyloop.indulger.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
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
        if(object instanceof NewsListFragment) {
            int idx = -1;
            for(int i = 0; i < mChannels.size(); ++i) {
                if(mChannels.get(i).channelCode.equals(((NewsListFragment)object).getChannelCode())) {
                    idx = i; break;
                }
            }
            if(idx >= 0)
                return idx;
        }
        return POSITION_NONE;
    }
}
