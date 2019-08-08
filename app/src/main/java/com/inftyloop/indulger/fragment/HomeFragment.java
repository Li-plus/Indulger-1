package com.inftyloop.indulger.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import butterknife.BindView;
import com.inftyloop.indulger.util.BaseHomeController;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.HashMap;

class HomeStubController extends BaseHomeController {
    public HomeStubController(Context ctx) {
        super(ctx);
    }

    @Override
    protected String getTitle() {
        return "StubController" + this.hashCode();
    }
}

public class HomeFragment extends QMUIFragment {
    private final static String TAG = HomeFragment.class.getSimpleName();
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

    @BindView(R.id.pager) ViewPager mPager;
    @BindView(R.id.tabs) QMUITabSegment mTabSegment;
    private HashMap<Pager, BaseHomeController> mPages = new HashMap<>();
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        private int mChildCount = 0;

        @Override
        public int getCount() {
            return mPages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            BaseHomeController page = mPages.get(Pager.getPagerFromIndex(position));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(page, params);
            return page;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if(mChildCount == 0) {
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }
    };

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.tabbar, null);
        ButterKnife.bind(this, layout);
        // init tabs
        int normalColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_gray_6);
        int selectedColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_blue);
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
        // init pagers
        BaseHomeController.BaseHomeControlListener listener = HomeFragment.this::startFragment;
        // TO-DO: replace controller with real ones
        BaseHomeController homeController = new HomeStubController(getActivity());
        BaseHomeController favController = new HomeStubController(getActivity());
        BaseHomeController personalController = new HomeStubController(getActivity());
        homeController.setListener(listener);
        favController.setListener(listener);
        personalController.setListener(listener);
        mPages.put(Pager.HOME, homeController);
        mPages.put(Pager.FAV, favController);
        mPages.put(Pager.PERSONAL, personalController);
        mPager.setAdapter(mPagerAdapter);
        mTabSegment.setupWithViewPager(mPager, false);
        return layout;
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }
}
