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
import com.inftyloop.indulger.listener.EditTextClearIconCallback;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogInFragment extends QMUIFragment {
    private final static String TAG = LogInFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tv_signup)
    TextView mSignUp;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.et_user_name)
    EditText mUserName;
    @BindView(R.id.iv_user_name_clear)
    ImageView mUserNameClear;
    @BindView(R.id.iv_password_clear)
    ImageView mPasswordClear;
    @BindView(R.id.btn_login)
    Button mLogin;

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_log_in, null);
        ButterKnife.bind(this, root);
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

        mLogin.setOnClickListener((View v) -> {
            Toast.makeText(getContext(), "email: " + mUserName.getText() +
                    "\npassword: " + mPassword.getText(), Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra("username", mUserName.getText().toString());
            setFragmentResult(RESULT_OK, data);
            popBackStack();
        });
        return root;
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SLIDE_TRANSITION_CONFIG;
    }
}
