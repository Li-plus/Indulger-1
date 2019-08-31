package com.inftyloop.indulger.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.model.entity.*;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.FileUtils;
import com.inftyloop.indulger.util.ThemeManager;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.suke.widget.SwitchButton;

import org.litepal.LitePal;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.litepal.crud.LitePalSupport;

public class SettingsFragment extends QMUIFragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    /**
     * State variables for settings
     */
    private int theme_checked_idx = -1;
    private int lang_checked_idx = -1;
    private boolean night_mode_auto = false;
    private boolean night_mode_enabled = false;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.settings_group_list_view)
    QMUIGroupListView mSettingsGroupListView;

    private SwitchButton mNightModeManualBtn;

    private void updateNightModeManualBtn() {
        if (mNightModeManualBtn != null) {
            if (night_mode_auto)
                mNightModeManualBtn.setCheckedDirect(ThemeManager.isSystemNightModeEnabled(getContext()));
            else
                mNightModeManualBtn.setCheckedDirect(night_mode_enabled);
            mNightModeManualBtn.setEnabled(!night_mode_auto);
        }
    }

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
        night_mode_auto = ConfigManager.getBoolean(Definition.SETTINGS_APP_NIGHT_MODE_FOLLOW_SYS, false);
        night_mode_enabled = ConfigManager.getBoolean(Definition.SETTINGS_APP_NIGHT_MODE_ENABLED, false);

        QMUICommonListItemView itemTheme = mSettingsGroupListView.createItemView(getString(R.string.settings_theme));
        itemTheme.setOnClickListener((View v) -> {
            final String[] items = new String[]{
                    getString(R.string.settings_theme_default),
                    getString(R.string.settings_theme_toutiao),
                    getString(R.string.settings_theme_jiujing)
            };
            new QMUIDialog.CheckableDialogBuilder(getActivity())
                    .setCheckedIndex(theme_checked_idx)
                    .addItems(items, (DialogInterface dialog, int which) -> {
                        ConfigManager.putIntNow(Definition.SETTINGS_APP_THEME, which);
                        QMUIFragmentActivity activity = getBaseFragmentActivity();
                        theme_checked_idx = which;
                        ThemeManager.changeTheme(activity, which);
                        dialog.dismiss();
                    })
                    .create(R.style.QMUI_Dialog).show();
        });

        QMUICommonListItemView itemEnableNightModeManual = mSettingsGroupListView.createItemView(getString(R.string.settings_night_mode_manual));
        itemEnableNightModeManual.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        mNightModeManualBtn = itemEnableNightModeManual.getSwitch();
        mNightModeManualBtn.setChecked(night_mode_enabled);
        mNightModeManualBtn.setOnCheckedChangeListener((v, checked) -> {
            // user has flipped btn, we do not need to update its status
            if (night_mode_auto || getBaseFragmentActivity() == null)
                return;  // ignore if automatic mode is set
            night_mode_enabled = checked;
            ConfigManager.putBooleanNow(Definition.SETTINGS_APP_NIGHT_MODE_ENABLED, checked);
            ThemeManager.changeTheme(getBaseFragmentActivity(), theme_checked_idx);
        });

        QMUICommonListItemView itemEnableNightModeAuto = mSettingsGroupListView.createItemView(getString(R.string.settings_night_mode_follow_sys));
        itemEnableNightModeAuto.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        itemEnableNightModeAuto.setOrientation(QMUICommonListItemView.VERTICAL);
        itemEnableNightModeAuto.setDetailText(getString(R.string.settings_night_mode_follow_sys_prompt));
        itemEnableNightModeAuto.getSwitch().setChecked(night_mode_auto);
        itemEnableNightModeAuto.getSwitch().setOnCheckedChangeListener((v, checked) -> {
            if (getBaseFragmentActivity() == null) return;
            night_mode_auto = checked;
            updateNightModeManualBtn();
            ConfigManager.putBooleanNow(Definition.SETTINGS_APP_NIGHT_MODE_FOLLOW_SYS, checked);
            ThemeManager.changeTheme(getBaseFragmentActivity(), theme_checked_idx);
        });

        updateNightModeManualBtn();

        /* Cache handler */

        QMUICommonListItemView itemClearCache = mSettingsGroupListView.createItemView(getString(R.string.settings_clear_cache));
        itemEnableNightModeAuto.setOrientation(QMUICommonListItemView.VERTICAL);
        itemClearCache.setDetailText(FileUtils.humanReadableByteCount(FileUtils.getDirSize(new File(MainApplication.getContext().getCacheDir().toString())), true));
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
                                // delete cache
                                try {
                                    LitePal.deleteAll(NewsEntry.class, "1");
                                    LitePal.deleteAll(NewsLoadRecord.class, "1");
                                } catch (Exception e) {}
                                boolean res = FileUtils.deleteDir(new File(MainApplication.getContext().getCacheDir().toString()));
                                mSettingsGroupListView.postDelayed(() -> {
                                    tipDialog.dismiss();
                                    itemClearCache.setDetailText(FileUtils.humanReadableByteCount(FileUtils.getDirSize(new File(MainApplication.getContext().getCacheDir().toString())), true));
                                    Toast toast = QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, getString(R.string.settings_cache_all_cleared),
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                    MainApplication.restart();
                                }, 2000);
                                dialog.dismiss();
                            })
                    .create(R.style.QMUI_Dialog).show();
        });

        /* Language handler */

        lang_checked_idx = ConfigManager.getInt(Definition.SETTINGS_APP_LANG, -1);
        if (lang_checked_idx < 0)
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
                        if (lang_checked_idx != which) {
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

        QMUICommonListItemView itemLogout = mSettingsGroupListView.createItemView(getString(R.string.log_out));
        itemLogout.setOnClickListener(v->
                new QMUIDialog.MessageDialogBuilder(getActivity())
                        .setMessage(getString(R.string.settings_confirm_logout))
                        .addAction(getString(R.string.settings_cancel), (QMUIDialog dialog, int index) -> {
                            dialog.dismiss();
                        })
                        .addAction(0, getString(R.string.settings_delete), QMUIDialogAction.ACTION_PROP_NEGATIVE,
                                (QMUIDialog dialog, int index) -> {
                                    AccountManager accountManager = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
                                    Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
                                    for (Account account : accounts) {
                                        accountManager.removeAccount(account, null, null);
                                    }
                                    ConfigManager.putString(Definition.LOGIN_EMAIL, "");
                                    ConfigManager.putString(Definition.LOGIN_USERNAME, "");
                                    ConfigManager.putString(Definition.LOGIN_ENCODED_PWD, "");
                                    try {
                                        LitePal.deleteAll(NewsFavEntry.class, "1");
                                        LitePal.deleteAll(RecommendWords.class, "1");
                                        LitePal.deleteAll(BlockedWords.class, "1");
                                        FileUtils.deleteDir(new File(MainApplication.getContext().getDataDir().getAbsolutePath() + "/" + "favImgs"));
                                    } catch (Exception e) {}
                                    dialog.dismiss();
                                    QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                                    setFragmentResult(RESULT_OK, new Intent());
                                    popBackStack();
                                })
                        .create(R.style.QMUI_Dialog).show());

        QMUICommonListItemView itemClearPref = mSettingsGroupListView.createItemView(getString(R.string.clear_personal_pref));
        itemClearPref.setOrientation(QMUICommonListItemView.VERTICAL);
        itemClearPref.setDetailText(getString(R.string.clear_personal_pref_extra_info));
        itemClearPref.setOnClickListener(v->
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setMessage(getString(R.string.settings_confirm_clear_pref))
                    .addAction(getString(R.string.settings_cancel), (QMUIDialog dialog, int index) -> {
                        dialog.dismiss();
                    })
                    .addAction(0, getString(R.string.settings_delete), QMUIDialogAction.ACTION_PROP_NEGATIVE,
                            (QMUIDialog dialog, int index) -> {
                                // delete personal prefs
                                try {
                                    LitePal.deleteAll(RecommendWords.class, "1");
                                    LitePal.deleteAll(BlockedWords.class, "1");
                                } catch (Exception e) {}
                                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, getString(R.string.settings_prefs_cleared),
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                MainApplication.restart();
                            })
                    .create(R.style.QMUI_Dialog).show());

        QMUIGroupListView.newSection(getContext())
                .setTitle(getString(R.string.settings_section_title_theme))
                .addItemView(itemTheme, null)
                .addItemView(itemEnableNightModeManual, null)
                .addItemView(itemEnableNightModeAuto, null)
                .addTo(mSettingsGroupListView);

        QMUIGroupListView.newSection(getContext())
                .setTitle(getString(R.string.settings_section_title_other))
                .addItemView(itemClearCache, null)
                .addItemView(itemLanguage, null)
                .addTo(mSettingsGroupListView);

        QMUIGroupListView.Section section = QMUIGroupListView.newSection(getContext())
                .setTitle(getString(R.string.settings_section_user_title));
        section.addItemView(itemClearPref, null);
        if (MeFragment.isLogin)
            section.addItemView(itemLogout, null);
        section.addTo(mSettingsGroupListView);

        QMUIGroupListView.newSection(getContext())
                .addItemView(itemAbout, null)
                .addTo(mSettingsGroupListView);
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
