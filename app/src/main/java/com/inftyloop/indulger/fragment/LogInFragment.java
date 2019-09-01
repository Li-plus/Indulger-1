package com.inftyloop.indulger.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.api.UserApiManager;
import com.inftyloop.indulger.listener.EditTextClearIconCallback;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.SecurityUtils;
import com.inftyloop.indulger.util.Utils;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.HashMap;

public class LogInFragment extends QMUIFragment {
    private final static String TAG = LogInFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tv_signup)
    TextView mSignUp;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.et_username)
    EditText mUserName;
    @BindView(R.id.iv_username_clear)
    ImageView mUserNameClear;
    @BindView(R.id.iv_password_clear)
    ImageView mPasswordClear;
    @BindView(R.id.btn_login)
    Button mLogin;
    private QMUITipDialog tipDialog;
    private UserApiManager mUserApiManager;
    private AccountManager mAccountManager;
    private String encodedPwd;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_log_in, null);
        ButterKnife.bind(this, root);
        mAccountManager = (AccountManager)getContext().getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = mAccountManager.getAccountsByType(getString(R.string.account_type));
        if(accounts.length > 0) {
            QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(R.string.login_account_already_added), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        mTopBar.setTitle(getResources().getString(R.string.log_in_title));
        mTopBar.addLeftBackImageButton().setOnClickListener((View v) -> popBackStack());
        mSignUp.setOnClickListener((View view) -> {
            QMUIFragment fragment = new SignUpFragment();
            startFragmentForResult(fragment, Definition.REQUEST_CODE_SIGN_UP);
        });
        mUserName.addTextChangedListener(new EditTextClearIconCallback(mUserNameClear));
        mUserNameClear.setOnClickListener((View v) -> mUserName.setText(""));
        mPassword.addTextChangedListener(new EditTextClearIconCallback(mPasswordClear));
        mPasswordClear.setOnClickListener((View v) -> mPassword.setText(""));

        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getString(R.string.login_in_progress))
                .create();

        mUserApiManager = new UserApiManager(new UserApiManager.OnUserApiListener() {
            @Override
            public void onAddUser(boolean success, String errMsg) {}

            @Override
            public void onDelUser(boolean success, String errMsg) {}

            @Override
            public void onGetUser(HashMap<String, String> response, String errMsg) {}

            @Override
            public void onCheckUser(boolean success, String errMsg) {
                Utils.postTaskSafely(tipDialog::dismiss);
                if(success) {
                    Utils.postTaskSafely(()->{
                        QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        mTopBar.postDelayed(()->{
                            Intent data = new Intent();
                            data.putExtra(Definition.LOGIN_USERNAME, mUserName.getText().toString());
                            data.putExtra(Definition.LOGIN_ENCODED_PWD, encodedPwd);
                            ConfigManager.putStringNow(Definition.LOGIN_USERNAME, mUserName.getText().toString());
                            ConfigManager.putStringNow(Definition.LOGIN_ENCODED_PWD, encodedPwd);
                            Account account = new Account(getString(R.string.app_name), getString(R.string.account_type));
                            Bundle extras = new Bundle();
                            extras.putString(Definition.LOGIN_USERNAME, mUserName.getText().toString());
                            mAccountManager.addAccountExplicitly(account, encodedPwd, extras);
                            ContentResolver.setIsSyncable(account, getString(R.string.content_authority), 1);
                            ContentResolver.setSyncAutomatically(account, getString(R.string.content_authority), true);
                            ContentResolver.addPeriodicSync(account, getString(R.string.content_authority), extras, 1800);
                            setFragmentResult(RESULT_OK, data);
                            popBackStack();
                        }, 500);
                    });
                } else {
                    Utils.postTaskSafely(()->{
                        QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onUpdateUser(boolean success, String errMsg) {}

            @Override
            public void onPutJson(boolean success, String errMsg) {}

            @Override
            public void onGetJson(String response, String errMsg) {}
        });

        mLogin.setOnClickListener((View v) -> {
            if(TextUtils.isEmpty(mUserName.getText()) || TextUtils.isEmpty(mPassword.getText())) {
                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.sign_up_fill_all_blanks), Toast.LENGTH_SHORT).show();
                return;
            }
            encodedPwd = SecurityUtils.getMd5(SecurityUtils.getMd5(mPassword.getText().toString()) + mUserName.getText().toString());
            tipDialog.show();
            mTopBar.postDelayed(()-> {
                if(tipDialog.isShowing())
                    tipDialog.dismiss();
            }, 6000);
            mUserApiManager.checkUser(mUserName.getText().toString(), encodedPwd);
        });
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
