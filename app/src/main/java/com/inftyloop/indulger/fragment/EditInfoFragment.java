package com.inftyloop.indulger.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.api.UserApiManager;
import com.inftyloop.indulger.listener.EditTextClearIconCallback;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.SecurityUtils;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditInfoFragment extends QMUIFragment {
    private final static String TAG = EditInfoFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.btn_submit)
    Button mButtonSubmit;
    @BindView(R.id.et_email)
    EditText mEmail;
    @BindView(R.id.iv_email_clear)
    ImageView mEmailClear;
    @BindView(R.id.et_old_pwd)
    EditText mOldPwd;
    @BindView(R.id.iv_old_pwd_clear)
    ImageView mOldPwdClear;
    @BindView(R.id.et_new_pwd)
    EditText mNewPwd;
    @BindView(R.id.iv_new_pwd_clear)
    ImageView mNewPwdClear;
    @BindView(R.id.et_new_pwd_confirm)
    EditText mNewPwdConfirm;
    @BindView(R.id.iv_new_pwd_confirm_clear)
    ImageView mNewPwdConfirmClear;
    Pattern pat = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
    private UserApiManager mUserApiManager;
    private QMUITipDialog mTipDialog;
    private String encodedPwd;
    private String username = "";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AccountManager accountManager = (AccountManager)getContext().getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
        if(accounts.length == 0) {
            getFragmentManager().popBackStack();
        } else {
            username = accountManager.getUserData(accounts[0], Definition.LOGIN_USERNAME);
        }
    }

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_info, null);
        ButterKnife.bind(this, root);

        mTopBar.setTitle(getResources().getString(R.string.edit_info_title));
        mTopBar.addLeftBackImageButton().setOnClickListener((View v) -> popBackStack());

        mOldPwd.addTextChangedListener(new EditTextClearIconCallback(mOldPwdClear));
        mOldPwdClear.setOnClickListener((View v) -> mOldPwd.setText(""));
        mNewPwd.addTextChangedListener(new EditTextClearIconCallback(mNewPwdClear));
        mNewPwdClear.setOnClickListener((View v) -> mNewPwd.setText(""));
        mNewPwdConfirm.addTextChangedListener(new EditTextClearIconCallback(mNewPwdConfirmClear));
        mNewPwdConfirmClear.setOnClickListener((View v) -> mNewPwdConfirm.setText(""));
        mEmail.setText(ConfigManager.getString(Definition.LOGIN_EMAIL, ""));
        mEmail.addTextChangedListener(new EditTextClearIconCallback(mEmailClear));
        mEmailClear.setOnClickListener(v -> mEmail.setText(""));

        mTipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getString(R.string.edit_info_in_progress))
                .create();

        mUserApiManager = new UserApiManager(new UserApiManager.OnUserApiListener() {
            @Override
            public void onAddUser(boolean success, String errMsg) {}

            @Override
            public void onDelUser(boolean success, String errMsg) {}

            @Override
            public void onGetUser(HashMap<String, String> response, String errMsg) {}

            @Override
            public void onCheckUser(boolean success, String errMsg) {}

            @Override
            public void onUpdateUser(boolean success, String errMsg) {
                mTipDialog.dismiss();
                if(success) {
                    QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, getString(R.string.edit_info_success), Toast.LENGTH_SHORT).show();
                    mTopBar.postDelayed(()->{
                        Intent data = new Intent();
                        data.putExtra(Definition.LOGIN_ENCODED_PWD, encodedPwd);
                        data.putExtra(Definition.LOGIN_USERNAME, username);
                        data.putExtra(Definition.LOGIN_EMAIL, "");
                        AccountManager accountManager = (AccountManager)getContext().getSystemService(Context.ACCOUNT_SERVICE);
                        Account[] accounts = accountManager.getAccountsByType(getString(R.string.account_type));
                        if(accounts.length > 0)
                            accountManager.setPassword(accounts[0], encodedPwd);
                        ConfigManager.putStringNow(Definition.LOGIN_ENCODED_PWD, encodedPwd);
                        ConfigManager.putStringNow(Definition.LOGIN_EMAIL, mEmail.getText().toString());
                        setFragmentResult(RESULT_OK, data);
                        popBackStack();
                    }, 500);
                } else {
                    QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.edit_info_failure), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPutJson(boolean success, String errMsg) {}

            @Override
            public void onGetJson(String response, String errMsg) { }
        });

        mButtonSubmit.setOnClickListener((View v) -> {
            if (mOldPwd.getText().toString().isEmpty() || mEmail.getText().toString().isEmpty()) {
                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.sign_up_fill_all_blanks), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mNewPwd.getText().toString().isEmpty() && !mNewPwdConfirm.getText().toString().equals(mNewPwd.getText().toString())) {
                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.sign_up_password_discrepancy), Toast.LENGTH_SHORT).show();
                return;
            }
            String new_pwd = mNewPwd.getText().toString().isEmpty() ? mOldPwd.getText().toString() : mNewPwd.getText().toString();
            String email = mEmail.getText().toString();
            Matcher matcher = pat.matcher(email);
            if(!matcher.matches()) {
                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.sign_up_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            mTipDialog.show();
            mTopBar.postDelayed(()-> {
                if(mTipDialog.isShowing())
                    mTipDialog.dismiss();
            }, 6000);
            String old_pwd = mOldPwd.getText().toString();
            String encodedPwdOld = SecurityUtils.getMd5(SecurityUtils.getMd5(old_pwd) + username);
            encodedPwd = SecurityUtils.getMd5(SecurityUtils.getMd5(new_pwd) + username);
            mUserApiManager.updateUser(username, encodedPwdOld, encodedPwd, email);
        });
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
