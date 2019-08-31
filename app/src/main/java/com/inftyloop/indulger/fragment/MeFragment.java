package com.inftyloop.indulger.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.api.UserApiManager;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.GlideImageLoader;
import com.inftyloop.indulger.util.SecurityUtils;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.HashMap;

public class MeFragment extends QMUIFragment {
    private static final String TAG = MeFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.username)
    TextView mUsername;
    @BindView(R.id.me_list)
    QMUIGroupListView mGroupListView;
    @BindView(R.id.btn_login)
    Button mLogin;
    private AccountManager accountManager;
    private UserApiManager userApiManager;

    public static boolean isLogin = false;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.me, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getResources().getString(R.string.me_title));
        mUsername.setText(getString(R.string.username_placeholder));

        QMUICommonListItemView itemSettings = mGroupListView.createItemView(getString(R.string.me_grouplist_settings));
        itemSettings.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView itemFavorite = mGroupListView.createItemView(getString(R.string.favorite_title));
        itemFavorite.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(getContext())
                .addItemView(itemFavorite, (View v) -> {
                    QMUIFragment fragment = new FavoriteFragment();
                    startFragment(fragment);
                })
                .addItemView(itemSettings, (View v) -> {
                    QMUIFragment fragment = new SettingsFragment();
                    startFragmentForResult(fragment, Definition.REQUEST_CODE_SETTINGS);
                })
                .addTo(mGroupListView);

        mLogin.setOnClickListener((View v) -> {
            QMUIFragment fragment = new LogInFragment();
            startFragmentForResult(fragment, Definition.REQUEST_CODE_LOGIN);
        });

        // check login status
        accountManager = (AccountManager)getContext().getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
        userApiManager = new UserApiManager(new UserApiManager.OnUserApiListener() {
            @Override
            public void onAddUser(boolean success, String errMsg) {}

            @Override
            public void onDelUser(boolean success, String errMsg) {}

            @Override
            public void onGetUser(HashMap<String, String> response, String errMsg) {
                if(response.containsKey("email")) {
                    String email = SecurityUtils.getMd5(response.get("email"));
                    ConfigManager.putString(Definition.LOGIN_EMAIL, email);
                    GlideImageLoader.loadRound(getContext(), "https://sdn.geekzu.org/avatar/" + email, mAvatar, R.mipmap.ic_launcher_round);
                }
            }

            @Override
            public void onCheckUser(boolean success, String errMsg) {}

            @Override
            public void onUpdateUser(boolean success, String errMsg) {}

            @Override
            public void onPutJson(boolean success, String errMsg) {}

            @Override
            public void onGetJson(String response, String errMsg) {}
        });
        if(accounts.length > 0) {
           updateAvatar(accounts[0]);
        }
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }

    private void updateAvatar(Account cnt) {
        if(cnt != null) {
            mLogin.setVisibility(View.GONE);
            String username = accountManager.getUserData(cnt, Definition.LOGIN_USERNAME);
            mUsername.setText(username);
            mUsername.setVisibility(View.VISIBLE);
            mAvatar.setVisibility(View.VISIBLE);
            isLogin = true;
            String email = ConfigManager.getString(Definition.LOGIN_EMAIL, "");
            if(TextUtils.isEmpty(email)) {
                userApiManager.getUser(username, accountManager.getPassword(cnt));
            } else
                GlideImageLoader.loadRound(getContext(), "https://sdn.geekzu.org/avatar/" + email, mAvatar, R.mipmap.ic_launcher_round);
        } else {
            mLogin.setVisibility(View.VISIBLE);
            mUsername.setVisibility(View.GONE);
            mAvatar.setVisibility(View.GONE);
            isLogin = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isLogin) {
            Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
            if(accounts.length > 0)
                updateAvatar(accounts[0]);
            else
                updateAvatar(null);
        }
    }

    protected void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == Definition.REQUEST_CODE_LOGIN) {
            String userName = data.getStringExtra(Definition.LOGIN_USERNAME);
            mLogin.setVisibility(View.GONE);
            mUsername.setText(userName);
            mUsername.setVisibility(View.VISIBLE);
            mAvatar.setVisibility(View.VISIBLE);
            isLogin = true;
        } else if (requestCode == Definition.REQUEST_CODE_SETTINGS) { // log out
            mLogin.setVisibility(View.VISIBLE);
            mUsername.setVisibility(View.GONE);
            mAvatar.setVisibility(View.GONE);
            isLogin = false;
        }
    }
}
