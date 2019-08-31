package com.inftyloop.indulger.activity;

import android.content.Context;
import android.os.Bundle;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.fragment.LogInFragment;
import com.inftyloop.indulger.ui.BaseFragmentActivity;
import com.qmuiteam.qmui.arch.annotation.DefaultFirstFragment;
import com.qmuiteam.qmui.arch.annotation.FirstFragments;
import com.qmuiteam.qmui.arch.annotation.LatestVisitRecord;

@FirstFragments(
        value = {
                LogInFragment.class
        })
@DefaultFirstFragment(LogInFragment.class)
@LatestVisitRecord
public class AuthenticatorActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContextViewId() {
        return R.id.authenticator_activity;
    }
}
