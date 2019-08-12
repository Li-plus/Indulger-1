package com.inftyloop.indulger.fragment;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.util.BaseFragmentActivity;
import com.inftyloop.indulger.util.ConfigManager;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class SettingsFragment extends QMUIFragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private int theme_checked_idx = -1;
    private int lang_checked_idx = -1;

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

        /* Theme handler */

        theme_checked_idx = ConfigManager.getInt(Definition.SETTINGS_APP_THEME, -1);
        if(theme_checked_idx < 0) {
            ConfigManager.putIntNow(Definition.SETTINGS_APP_THEME, 0);
        }

        QMUICommonListItemView itemTheme = mSettingsGroupListView.createItemView(getString(R.string.settings_theme));
        itemTheme.setOnClickListener((View v) -> {
            final String[] items = new String[]{
                    getString(R.string.settings_theme_default),
                    getString(R.string.settings_theme_auto),
                    getString(R.string.settings_theme_night)
            };
            new QMUIDialog.CheckableDialogBuilder(getActivity())
                    .setCheckedIndex(theme_checked_idx)
                    .addItems(items, (DialogInterface dialog, int which) -> {
                        ConfigManager.putIntNow(Definition.SETTINGS_APP_THEME, which);
                        QMUIFragmentActivity activity = getBaseFragmentActivity();
                        theme_checked_idx = which;
                        if(activity instanceof BaseFragmentActivity) {
                            // only recreate if mode changes
                            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                            BaseFragmentActivity act = (BaseFragmentActivity) activity;
                            switch (which) {
                                case 0:
                                    if(act.getCurStyleResId() != R.style.AppTheme)
                                        act.recreate(); break;
                                case 1:
                                    if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                                        if(act.getCurStyleResId() != R.style.NightTheme)
                                            act.recreate(); break;
                                    } else {
                                        if(act.getCurStyleResId() != R.style.AppTheme)
                                            act.recreate(); break;
                                    }
                                case 2:
                                    if(act.getCurStyleResId() != R.style.NightTheme)
                                        act.recreate(); break;
                            }
                        } else activity.recreate();
                        dialog.dismiss();
                    })
                    .create(R.style.QMUI_Dialog).show();
        });

        /* Cache handler */

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

        /* Language handler */

        lang_checked_idx = ConfigManager.getInt(Definition.SETTINGS_APP_LANG, -1);
        if(lang_checked_idx < 0)
            ConfigManager.putIntNow(Definition.SETTINGS_APP_LANG, 0);

        QMUICommonListItemView itemLanguage = mSettingsGroupListView.createItemView(getString(R.string.settings_language));
        itemLanguage.setOnClickListener((View v) -> {
            final String[] items = new String[]{
                    getString(R.string.settings_lang_auto),
                    getString(R.string.settings_lang_chn),
                    getString(R.string.settings_lang_en)
            };
            new QMUIDialog.CheckableDialogBuilder(getActivity())
                    .setCheckedIndex(lang_checked_idx)
                    .addItems(items, (DialogInterface dialog, int which) -> {
                        if(lang_checked_idx != which) {
                            lang_checked_idx = which;
                            getActivity().recreate();
                        }
                        ConfigManager.putIntNow(Definition.SETTINGS_APP_LANG, which);
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
                .addItemView(itemTheme, null)
                .addItemView(itemClearCache, null)
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
