package com.inftyloop.indulger.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import butterknife.BindView;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.HashMap;

public class MainTabBarFragment extends QMUIFragment {
    private final static String TAG = MainTabBarFragment.class.getSimpleName();

    enum Pager {
        HOME, FAV, PERSONAL;

        public static Pager getPagerFromIndex(int idx) {
            switch (idx) {
                case 1:
                    return FAV;
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
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_collect),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_collect_fill),
                getActivity().getResources().getString(R.string.home), false
        );

        QMUITabSegment.Tab fav = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_star),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_star_fill),
                getActivity().getResources().getString(R.string.fav), false
        );
        QMUITabSegment.Tab personal = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_signal),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_signal_fill),
                getActivity().getResources().getString(R.string.personal), false
        );
        mTabSegment.addTab(home)
                .addTab(fav)
                .addTab(personal);
        mPages.put(Pager.HOME, new StubFragment());
        mPages.put(Pager.FAV, new FavoriteFragment());
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
