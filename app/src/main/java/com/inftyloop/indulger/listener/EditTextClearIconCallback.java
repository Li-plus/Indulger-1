package com.inftyloop.indulger.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class EditTextClearIconCallback implements TextWatcher {
    private ImageView clearIcon;

    public EditTextClearIconCallback(@NonNull ImageView clearIcon) {
        this.clearIcon = clearIcon;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().length() > 0) {
            clearIcon.setVisibility(View.VISIBLE);
        } else {
            clearIcon.setVisibility(View.INVISIBLE);
        }
    }
}
