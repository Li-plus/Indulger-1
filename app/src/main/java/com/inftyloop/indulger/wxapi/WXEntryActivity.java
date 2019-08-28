package com.inftyloop.indulger.wxapi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.util.LocaleHelper;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    public static final String TAG = WXEntryActivity.class.getSimpleName();
    private IWXAPI api;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, Definition.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReq(BaseReq baseReq) {}

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                QMUITipDialog.Builder.makeToast(MainApplication.getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(R.string.share_success), Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                QMUITipDialog.Builder.makeToast(MainApplication.getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                QMUITipDialog.Builder.makeToast(MainApplication.getContext(), QMUITipDialog.Builder.ICON_TYPE_NOTHING, getString(R.string.share_failure), Toast.LENGTH_SHORT).show();
                break;
        }
        finish();
    }
}
