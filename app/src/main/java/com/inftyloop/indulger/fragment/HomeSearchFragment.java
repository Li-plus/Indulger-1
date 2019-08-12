package com.inftyloop.indulger.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.ui.AutoWrapLayout;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

public class HomeSearchFragment extends QMUIFragment {
    private static final String TAG = HomeSearchFragment.class.getSimpleName();
    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.img_delete) ImageView mDelHistory;
    @BindView(R.id.search_history) AutoWrapLayout mSearchHistory;
    @BindView(R.id.search_history_bar) LinearLayout mSearchHistoryBar;
    /* Top Bar stuffs */
    ImageButton mTopImgButton;
    ImageView mClearEditText;
    EditText mSearch;

    @Override
    public void onDestroyView() {
        // ensure that the keyboard is properly hidden before closing this fragment
        hideKeyboard();
        super.onDestroyView();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onPause() {
        hideKeyboard();
        super.onPause();
    }

    @Override
    public View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.home_search, null);
        ButterKnife.bind(this, root);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(25, 25, 25, 25);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.searchbar_input_home, null);
        mTopImgButton = view.findViewById(R.id.img_search);
        mSearch = view.findViewById(R.id.edit_search);
        mClearEditText = view.findViewById(R.id.icon_clear);
        view.post(() -> {
            Rect area = new Rect();
            mTopImgButton.getHitRect(area);
            final int offset = 30;
            area.right += offset;
            area.left -= offset;
            area.bottom += offset;
            area.left -= offset;
            ((View) mTopImgButton.getParent()).setTouchDelegate(new TouchDelegate(area, mTopImgButton));
        });
        mTopImgButton.setOnClickListener(v -> {
            popBackStack();
            hideKeyboard();
        });
        mTopImgButton.setEnabled(true);
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mSearch.hasFocus() && !mSearch.getText().toString().isEmpty())
                    mClearEditText.setVisibility(View.VISIBLE);
                else
                    mClearEditText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mSearch.setOnKeyListener((v, keyCode, evt) -> {
            if (evt.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(getContext(), mSearch.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });
        mClearEditText.setVisibility(View.INVISIBLE);
        mClearEditText.setOnClickListener(v -> mSearch.setText(""));
        mSearch.setOnFocusChangeListener((v, hasFocus) -> {
            mClearEditText.setVisibility(hasFocus && !mSearch.getText().toString().isEmpty() ? View.VISIBLE : View.INVISIBLE);
        });
        mTopBar.addRightView(view, R.id.topbar_search_input, lp);
        // init search history stuffs
        mSearchHistory.setItemListener((pos, v) -> {
            mSearch.setText(v.getText().toString());
            mSearch.setSelection(v.getText().length());
            if(pos == mSearchHistory.getChildCount() - 1) {
                mSearchHistory.removeAllViews();
                String[] strings_more = {"EXTRA Longgggggggggggggggggggggggggggggggg", "value", "中文测试", "kkkkkkkkl"};
                mSearchHistory.loadStringArray(strings_more);
            }
        });
        String[] strings = {"Val1", "val2", "test2", "extra longggggggggg", "value"};
        mSearchHistory.loadStringArray(strings);
        mDelHistory.setOnClickListener(v-> {
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setMessage(getString(R.string.confirm_clear_search_history))
                    .addAction(getString(R.string.cancel), (QMUIDialog dialog, int index) -> {
                        dialog.dismiss();
                    })
                    .addAction(0, getString(R.string.confirm), QMUIDialogAction.ACTION_PROP_NEGATIVE,
                            (QMUIDialog dialog, int index) -> {
                                // clear history
                                mSearchHistoryBar.setVisibility(View.INVISIBLE);
                                mSearchHistory.removeAllViews();
                                dialog.dismiss();
                            })
                    .create(R.style.QMUI_Dialog).show();
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearch.requestFocus();
        showKeyboard();
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return FADE_TRANSITION_CONFIG;
    }
}
