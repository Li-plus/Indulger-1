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

public class SignUpFragment extends QMUIFragment {
    private final static String TAG = SignUpFragment.class.getSimpleName();

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.tv_login)
    TextView mTextViewLogIn;
    @BindView(R.id.btn_login)
    Button mButtonLogIn;
    @BindView(R.id.et_user_name)
    EditText mUserName;
    @BindView(R.id.iv_user_name_clear)
    ImageView mUserNameClear;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.iv_password_clear)
    ImageView mPasswordClear;
    @BindView(R.id.et_password_confirm)
    EditText mPasswordConfirm;
    @BindView(R.id.iv_password_confirm_clear)
    ImageView mPasswordConfirmClear;

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

        mButtonLogIn.setOnClickListener((View v) -> {
            if (mUserName.getText().toString().isEmpty() || mPassword.getText().toString().isEmpty()
                    || mPasswordConfirm.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Empty user name or password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mPasswordConfirm.getText().toString().equals(mPassword.getText().toString())) {
                Toast.makeText(getContext(), "Two passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "email: " + mUserName.getText() + "\npassword: " + mPassword.getText() +
                    "\nconfirm: " + mPasswordConfirm.getText(), Toast.LENGTH_SHORT).show();
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
