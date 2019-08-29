package com.inftyloop.indulger.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainTabBarFragment extends QMUIFragment {
    private final static String TAG = MainTabBarFragment.class.getSimpleName();

    enum Pager {
        HOME, VIDEO_LIST, PERSONAL;

        public static Pager getPagerFromIndex(int idx) {
            switch (idx) {
                case 1:
                    return VIDEO_LIST;
                case 2:
                    return PERSONAL;
                default:
                    return HOME;
            }
        }
    }

    @BindView(R.id.pager)
    ViewPager mPager;
    @BindView(R.id.tabs)
    QMUITabSegment mTabSegment;
    private HashMap<Pager, QMUIFragment> mPages = new HashMap<>();
    private PagerAdapter mPagerAdapter = null;

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.tabbar, null);
        ButterKnife.bind(this, layout);

        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mPages.get(Pager.getPagerFromIndex(i));
            }

            @Override
            public int getCount() {
                return mPages.size();
            }
        };
        // init tabs
        int normalColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.tabbar_normal_text_color);
        int selectedColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.tabbar_selected_text_color);
        mTabSegment.setDefaultNormalColor(normalColor);
        mTabSegment.setDefaultSelectedColor(selectedColor);

        QMUITabSegment.Tab home = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_collect),
                ContextCompat.getDrawable(getContext(), R.drawable.icon_collect_fill),
                getActivity().getResources().getString(R.string.home), false
        );

        QMUITabSegment.Tab fav = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_star),
                ContextCompat.getDrawable(getContext(), R.drawable.icon_star_fill),
                getActivity().getResources().getString(R.string.video_list_title), false
        );
        QMUITabSegment.Tab personal = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.drawable.icon_signal),
                ContextCompat.getDrawable(getContext(), R.drawable.icon_signal_fill),
                getActivity().getResources().getString(R.string.personal), false
        );

        mTabSegment.addTab(home)
                .addTab(fav)
                .addTab(personal);

        mPages.put(Pager.HOME, new HomeFragment());
        mPages.put(Pager.VIDEO_LIST, new VideoListFragment());
        mPages.put(Pager.PERSONAL, new MeFragment());
        mPager.setAdapter(mPagerAdapter);
        mTabSegment.setupWithViewPager(mPager, false);

        return layout;
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }
}
