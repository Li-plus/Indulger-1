package com.inftyloop.indulger.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import com.inftyloop.indulger.BuildConfig;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.api.UserApiManager;
import java.util.HashMap;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = SyncAdapter.class.getSimpleName();
    private UserApiManager userApiManager;
    private Context mContext;

    private void init(Context c) {
        mContext = c;
        userApiManager = new UserApiManager(new UserApiManager.OnUserApiListener() {
            @Override
            public void onAddUser(boolean success, String errMsg) {

            }

            @Override
            public void onDelUser(boolean success, String errMsg) {

            }

            @Override
            public void onGetUser(HashMap<String, String> response, String errMsg) {

            }

            @Override
            public void onCheckUser(boolean success, String errMsg) {

            }

            @Override
            public void onUpdateUser(boolean success, String errMsg) {

            }

            @Override
            public void onPutJson(boolean success, String errMsg) {

            }

            @Override
            public void onGetJson(String response, String errMsg) {

            }
        });
    }

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.account_type));
        return newAccount;
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if(BuildConfig.DEBUG) {
            Log.w(TAG, "Starting synchronization...");
        }
        AccountManager accountManager = (AccountManager)mContext.getSystemService(Context.ACCOUNT_SERVICE);
        String username = accountManager.getUserData(account, Definition.LOGIN_USERNAME);
        String pwd = accountManager.getPassword(account);
    }

    public static void perforSync(Context ctx) {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(getSyncAccount(ctx), ctx.getString(R.string.content_authority), b);
    }
}
