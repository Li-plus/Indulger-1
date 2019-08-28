package com.inftyloop.indulger.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.fragment.NewsListFragment;
import com.inftyloop.indulger.model.entity.NewsChannel;

import java.util.List;

public class NewsChannelPagerAdapter extends MutableFragmentPagerAdapter {
    private List<NewsChannel> mChannels;
    private Context mCtx;
    public NewsChannelPagerAdapter(@NonNull Context ctx, @NonNull List<NewsChannel> channelList, FragmentManager fm) {
        super(fm);
        mCtx = ctx;
        mChannels = channelList;
    }

    @Override
    public Fragment getItem(int pos) {
        NewsChannel ch = mChannels.get(pos);
        NewsListFragment f = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Definition.IS_RECOMMEND, ch.channelCode.equals(mCtx.getString(R.string.channel_code_recommend)));
        bundle.putString(Definition.CHANNEL_NAME, ch.title);
        bundle.putString(Definition.CHANNEL_CODE, ch.channelCode);
        bundle.putBoolean(Definition.IS_VIDEO_LIST, ch.channelCode.equals(mCtx.getString(R.string.channel_code_video)));
        f.setArguments(bundle);
        return f;
    }

    @Override
    public long getItemId(int position) {
        return mChannels.get(position).uniqueID;
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mChannels.get(position).title;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        NewsListFragment item = (NewsListFragment) object;
        for(int i = 0; i < mChannels.size(); ++i) {
            if(mChannels.get(i).channelCode.equals(item.getChannelCode()))
                return i;
        }
        return POSITION_NONE;
    }
}
