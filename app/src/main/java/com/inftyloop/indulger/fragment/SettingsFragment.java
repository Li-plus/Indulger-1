package com.inftyloop.indulger.fragment;

import android.content.DialogInterface;
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
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class SettingsFragment extends QMUIFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.settings_group_list_view)
    QMUIGroupListView mSettingsGroupListView;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.settings, null);
        ButterKnife.bind(this, root);
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> {
            popBackStack();
        });
        mTopBar.setTitle(getString(R.string.settings_title));

        QMUICommonListItemView itemNightMode = mSettingsGroupListView.createItemView(getString(R.string.settings_night_mode));
        itemNightMode.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        itemNightMode.getSwitch().setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            Toast.makeText(getActivity(), "checked = " + isChecked, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getActivity(), R.string.settings_cache_all_cleared, Toast.LENGTH_SHORT).show();
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
        return SCALE_TRANSITION_CONFIG;
    }
}
