package com.inftyloop.indulger.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.model.entity.NewsDetail;
import com.inftyloop.indulger.ui.AutoWrapLayout;
import com.inftyloop.indulger.util.ConfigManager;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

public class HomeSearchFragment extends QMUIFragment {
    private static final String TAG = HomeSearchFragment.class.getSimpleName();
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.img_delete)
    ImageView mDelHistory;
    @BindView(R.id.search_history)
    AutoWrapLayout mSearchHistory;
    @BindView(R.id.search_history_bar)
    LinearLayout mSearchHistoryBar;
    /* Top Bar stuffs */
    ImageButton mTopImgButton;
    ImageView mClearEditText;
    EditText mSearch;
    Gson mGson = new Gson();

    public static String keyword = "";

    @Override
    public void onDestroyView() {
        // ensure that the keyboard is properly hidden before closing this fragment
        super.onDestroyView();
        hideKeyboard();
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
        super.onPause();
        hideKeyboard();
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSearch.hasFocus() && !mSearch.getText().toString().isEmpty())
                    mClearEditText.setVisibility(View.VISIBLE);
                else
                    mClearEditText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mSearch.setOnKeyListener((v, keyCode, evt) -> {
            if (evt.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (mSearch.getText().toString().isEmpty()) {
                        Toast toast = QMUITipDialog.Builder.makeToast(getContext(), QMUITipDialog.Builder.ICON_TYPE_FAIL, getString(R.string.search_empty_input_not_allowed), Toast.LENGTH_SHORT);
                        toast.show();
                        return false;
                    }
                    // start search !
                    Toast.makeText(getContext(), mSearch.getText(), Toast.LENGTH_SHORT).show();
                    mSearchHistory.pushFront(mSearch.getText().toString());
                    String[] arr = mSearchHistory.getItemArray();
                    String temp = mGson.toJson(arr);
                    ConfigManager.putStringNow(Definition.SETTINGS_SEARCH_HISTORY, temp);
                    if (arr.length > 0)
                        mSearchHistoryBar.setVisibility(View.VISIBLE);
                    /*List<String> mUrls = new ArrayList<>();
                    mUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565699555038&di=2710a60131cf092d7869e258954bc099&imgtype=0&src=http%3A%2F%2Fattachments.gfan.com%2Fforum%2Fattachments2%2Fattachments2%2Fday_110120%2F11012008485012851f3b754fad.jpg");
                    mUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1565699555042&di=8bdd3ab58e346c969e5f8b1a15cde3be&imgtype=0&src=http%3A%2F%2Fnews.mydrivers.com%2FImg%2F20120227%2F2012022709150416.jpg");
                    Intent intent = new Intent(getContext(), ImageViewPagerActivity.class);
                    intent.putExtra(ImageViewPagerActivity.POSITION, 0);
                    intent.putStringArrayListExtra(ImageViewPagerActivity.IMG_URLS, (ArrayList<String>) mUrls);
                    getContext().startActivity(intent);*/

                    keyword = mSearch.getText().toString();
                    SearchResultFragment res = new SearchResultFragment();
                    startFragment(res);
                    return true;
                }
            }
            return false;
        });
        mSearchHistory.setSizeLimit(20); // search history limit
        mClearEditText.setVisibility(View.INVISIBLE);
        mClearEditText.setOnClickListener(v -> mSearch.setText(""));
        mSearch.setOnFocusChangeListener((v, hasFocus) -> {
            mClearEditText.setVisibility(hasFocus && !mSearch.getText().toString().isEmpty() ? View.VISIBLE : View.INVISIBLE);
            if (hasFocus)
                showKeyboard();
            else
                hideKeyboard();
        });
        mTopBar.addRightView(view, R.id.topbar_search_input, lp);
        // init search history stuffs
        String historyJson = ConfigManager.getString(Definition.SETTINGS_SEARCH_HISTORY, "");
        if (TextUtils.isEmpty(historyJson)) {
            historyJson = mGson.toJson(new String[0]);
            ConfigManager.putString(Definition.SETTINGS_SEARCH_HISTORY, historyJson);
            mSearchHistoryBar.setVisibility(View.INVISIBLE);
        } else {
            String[] history = mGson.fromJson(historyJson, new TypeToken<String[]>() {
            }.getType());
            if (history.length > 0) {
                mSearchHistoryBar.setVisibility(View.VISIBLE);
                mSearchHistory.loadData(history);
            } else {
                mSearchHistoryBar.setVisibility(View.INVISIBLE);
            }
        }
        mSearchHistory.setItemListener((pos, v) -> {
            mSearch.setText(v.getText().toString());
            mSearch.setSelection(v.getText().length());
        });
        mDelHistory.setOnClickListener(v -> {
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setMessage(getString(R.string.confirm_clear_search_history))
                    .addAction(getString(R.string.cancel), (QMUIDialog dialog, int index) -> {
                        dialog.dismiss();
                    })
                    .addAction(0, getString(R.string.confirm), QMUIDialogAction.ACTION_PROP_NEGATIVE,
                            (QMUIDialog dialog, int index) -> {
                                // clear history
                                mSearchHistoryBar.setVisibility(View.INVISIBLE);
                                mSearchHistory.clearAllItems();
                                // save
                                String temp = mGson.toJson(new String[0]);
                                ConfigManager.putString(Definition.SETTINGS_SEARCH_HISTORY, temp);
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
    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return FADE_TRANSITION_CONFIG;
    }
}
