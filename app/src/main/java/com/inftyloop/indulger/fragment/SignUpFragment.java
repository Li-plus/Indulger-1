package com.inftyloop.indulger.fragment;

import android.content.Intent;
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
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends QMUIFragment {
    private final static String TAG = SignUpFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tv_login)
    TextView mTextViewLogIn;
    @BindView(R.id.btn_login)
    Button mButtonLogIn;
    @BindView(R.id.et_email)
    EditText mEmail;
    @BindView(R.id.iv_email_clear)
    ImageView mEmailClear;
    @BindView(R.id.et_username)
    EditText mUserName;
    @BindView(R.id.iv_username_clear)
    ImageView mUserNameClear;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.iv_password_clear)
    ImageView mPasswordClear;
    @BindView(R.id.et_password_confirm)
    EditText mPasswordConfirm;
    @BindView(R.id.iv_password_confirm_clear)
    ImageView mPasswordConfirmClear;
    Pattern pat = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
    private UserApiManager mUserApiManager;
    private QMUITipDialog mTipDialog;
    private String encodedPwd;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sign_up, null);
        ButterKnife.bind(this, root);
        mTopBar.setTitle(getResources().getString(R.string.sign_up_title));
        mTopBar.addLeftBackImageButton().setOnClickListener((View v) -> popBackStack());
        mTextViewLogIn.setOnClickListener((View view) -> popBackStack());

        mUserName.addTextChangedListener(new EditTextClearIconCallback(mUserNameClear));
        mUserNameClear.setOnClickListener((View v) -> mUserName.setText(""));
        mPassword.addTextChangedListener(new EditTextClearIconCallback(mPasswordClear));
        mPasswordClear.setOnClickListener((View v) -> mPassword.setText(""));
        mPasswordConfirm.addTextChangedListener(new EditTextClearIconCallback(mPasswordConfirmClear));
        mPasswordConfirmClear.setOnClickListener((View v) -> mPasswordConfirm.setText(""));
        mEmail.addTextChangedListener(new EditTextClearIconCallback(mEmailClear));
        mEmailClear.setOnClickListener(v -> mEmail.setText(""));

        mTipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(getString(R.string.sign_up_in_progress))
                .create();

        mUserApiManager = new UserApiManager(new UserApiManager.OnUserApiListener() {
            @Override
            public void onAddUser(boolean success, String errMsg) {
                mTipDialog.dismiss();
                if(success) {
                    QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_SUCCESS, getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
                    mTopBar.postDelayed(()->{
                        Intent data = new Intent();
                        data.putExtra(Definition.LOGIN_USERNAME, mUserName.getText().toString());
                        data.putExtra(Definition.LOGIN_ENCODED_PWD, encodedPwd);
                        ConfigManager.putStringNow(Definition.LOGIN_USERNAME, mUserName.getText().toString());
                        ConfigManager.putStringNow(Definition.LOGIN_ENCODED_PWD, encodedPwd);
                        setFragmentResult(RESULT_OK, data);
                        popBackStack();
                    }, 500);
                } else {
                    QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.sign_up_user_exists), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDelUser(boolean success, String errMsg) {}

            @Override
            public void onGetUser(HashMap<String, String> response, String errMsg) {}

            @Override
            public void onCheckUser(boolean success, String errMsg) {}

            @Override
            public void onUpdateUser(boolean success, String errMsg) {}

            @Override
            public void onPutJson(boolean success, String errMsg) {}

            @Override
            public void onGetJson(String response, String errMsg) { }
        });

        mButtonLogIn.setOnClickListener((View v) -> {
            if (mUserName.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty()
                    || mPasswordConfirm.getText().toString().isEmpty() || mEmail.getText().toString().isEmpty()) {
                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.sign_up_fill_all_blanks), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mPasswordConfirm.getText().toString().equals(mPassword.getText().toString())) {
                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.sign_up_password_discrepancy), Toast.LENGTH_SHORT).show();
                return;
            }
            Matcher matcher = pat.matcher(mEmail.getText().toString());
            if(!matcher.matches()) {
                QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.sign_up_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            mTipDialog.show();
            mTopBar.postDelayed(()-> {
                if(mTipDialog.isShowing())
                    mTipDialog.dismiss();
            }, 6000);
            encodedPwd = SecurityUtils.getMd5(SecurityUtils.getMd5(mPassword.getText().toString()) + mUserName.getText().toString());
            mUserApiManager.addUser(mUserName.getText().toString(),
                    encodedPwd, mEmail.getText().toString());
        });
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
