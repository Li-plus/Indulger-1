package com.inftyloop.indulger.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.suke.widget.SwitchButton;

public class SettingsFragment extends QMUIFragment {
    public static int styleResId = R.style.AppTheme;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.settings_group_list_view)
    QMUIGroupListView mSettingsGroupListView;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.settings, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getString(R.string.settings_title));
        mTopBar.addLeftBackImageButton().setOnClickListener((View v) -> {
            popBackStack();
        });

        QMUICommonListItemView itemNightMode = mSettingsGroupListView.createItemView(getString(R.string.settings_night_mode));
        itemNightMode.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.style_name, outValue, true);
        if (outValue.string.equals(getString(R.string.day_theme_name))) {
            itemNightMode.getSwitch().setChecked(false);
        } else {
            itemNightMode.getSwitch().setChecked(true);
        }
        Log.d(getContext().getClass().getName(), outValue + " jjjjjjjj");
        Log.d(getContext().getClass().getName(), outValue.data + " jjjjjjjj");
        Log.d(getContext().getClass().getName(), outValue.toString() + " jjjjjjjj");
        Log.d(getContext().getClass().getName(), outValue.string + " jjjjjjjj");

        itemNightMode.getSwitch().setOnCheckedChangeListener((SwitchButton buttonView, boolean isChecked) -> {
            Toast.makeText(getActivity(), "checked = " + isChecked, Toast.LENGTH_SHORT).show();
            if (isChecked) {
                getContext().setTheme(R.style.NightTheme);
                styleResId = R.style.NightTheme;
            } else {
                getActivity().setTheme(R.style.AppTheme);
                styleResId = R.style.AppTheme;
            }
            getActivity().recreate();
        });
        QMUICommonListItemView itemClearCache = mSettingsGroupListView.createItemView(getString(R.string.settings_clear_cache));
        itemClearCache.setOnClickListener((View v) -> {
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setMessage(getString(R.string.settings_confirm_clear_cache))
                    .addAction(getString(R.string.settings_cancel), (QMUIDialog dialog, int index) -> {
                        dialog.dismiss();
                    })
                    .addAction(0, getString(R.string.settings_delete), QMUIDialogAction.ACTION_PROP_NEGATIVE,
                            (QMUIDialog dialog, int index) -> {
                                QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                        .setTipWord(getString(R.string.settings_cache_clearing))
                                        .create();
                                tipDialog.show();
                                mSettingsGroupListView.postDelayed(() -> {
                                    tipDialog.dismiss();
                                    QMUITipDialog tipDialog1 = new QMUITipDialog.Builder(getContext()).setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                            .setTipWord(getString(R.string.settings_cache_all_cleared))
                                            .create();
                                    tipDialog1.show();
                                    mSettingsGroupListView.postDelayed(tipDialog1::dismiss, 1000);
                                }, 2000);
                                dialog.dismiss();
                            })
                    .create(R.style.QMUI_Dialog).show();
        });

        QMUICommonListItemView itemFontSize = mSettingsGroupListView.createItemView(getString(R.string.settings_font_size));
        itemFontSize.setOnClickListener((View v) -> {
            final String[] items = new String[]{
                    getString(R.string.settings_font_small),
                    getString(R.string.settings_font_middle),
                    getString(R.string.settings_font_large)
            };
            final int checkedIndex = 1;

            new QMUIDialog.CheckableDialogBuilder(getActivity())
                    .setCheckedIndex(checkedIndex)
                    .addItems(items, (DialogInterface dialog, int which) -> {
                        Toast.makeText(getActivity(), "you clicked " + items[which], Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .create(R.style.QMUI_Dialog).show();
        });

        QMUICommonListItemView itemLanguage = mSettingsGroupListView.createItemView(getString(R.string.settings_language));
        itemLanguage.setOnClickListener((View v) -> {
            final String[] items = new String[]{
                    getString(R.string.settings_chinese),
                    getString(R.string.settings_english)
            };
            final int checkedIndex = 0;

            new QMUIDialog.CheckableDialogBuilder(getActivity())
                    .setCheckedIndex(checkedIndex)
                    .addItems(items, (DialogInterface dialog, int which) -> {
                        Toast.makeText(getActivity(), "you clicked " + items[which], Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .create(R.style.QMUI_Dialog).show();
        });

        QMUICommonListItemView itemAbout = mSettingsGroupListView.createItemView(getString(R.string.settings_about));
        itemAbout.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemAbout.setOnClickListener((View v) -> {
            QMUIFragment fragment = new AboutFragment();
            startFragment(fragment);
        });

        QMUIGroupListView.newSection(getContext())
                .addItemView(itemNightMode, null)
                .addItemView(itemClearCache, null)
                .addItemView(itemFontSize, null)
                .addItemView(itemLanguage, null)
                .addTo(mSettingsGroupListView);

        QMUIGroupListView.newSection(getContext()).
                addItemView(itemAbout, null).
                addTo(mSettingsGroupListView);
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
