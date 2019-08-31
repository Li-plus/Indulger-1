package com.inftyloop.indulger.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.inftyloop.indulger.BuildConfig;
import com.inftyloop.indulger.MainApplication;
import com.inftyloop.indulger.R;
import com.inftyloop.indulger.api.Definition;
import com.inftyloop.indulger.api.UserApiManager;
import com.inftyloop.indulger.model.entity.NewsEntry;
import com.inftyloop.indulger.model.entity.NewsFavEntry;
import com.inftyloop.indulger.util.ConfigManager;
import com.inftyloop.indulger.util.NotificationHelper;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = SyncAdapter.class.getSimpleName();
    private UserApiManager userApiManager;
    private Context mContext;

    private static final String SYNC_STAT_VARNAME = "_sync_token";
    private static final String SYNC_ENTRY_NAME = "_sync_entries";

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
        Context main_ctx = mContext;
        NotificationHelper helper = new NotificationHelper(main_ctx);
        helper.createNotification(main_ctx.getString(R.string.sync_notification_title), main_ctx.getString(R.string.sync_notification_content));
        try {
            boolean write_to_server = true;
            AccountManager accountManager = (AccountManager)mContext.getSystemService(Context.ACCOUNT_SERVICE);
            String username = accountManager.getUserData(account, Definition.LOGIN_USERNAME);
            String pwd = accountManager.getPassword(account);
            Gson gson = new Gson();
            JsonObject phase1 = userApiManager.getJsonBlocking(username, pwd, username + SYNC_STAT_VARNAME);
            if(phase1.get("status").getAsInt() != 0) {
                Log.e(TAG, "Phase 1: err");
                syncResult.stats.numAuthExceptions++;
                helper.createNotification(main_ctx.getString(R.string.sync_notification_title_failure), main_ctx.getString(R.string.sync_notification_failure_content));
                return;
            }
            String phase1_json_str = phase1.getAsJsonObject("payload").get("json").getAsString();
            String sync_token = "";
            if(TextUtils.isEmpty(phase1_json_str)) {
                write_to_server = true; // server is empty, write to it, and generate a token
                sync_token = UUID.randomUUID().toString();
            }
            else {
                String temp = ConfigManager.getString(Definition.SYNC_TOKEN, "");
                if(TextUtils.isEmpty(temp)) {
                    // local is empty, download server contents
                    sync_token = phase1_json_str;
                    write_to_server = false;
                } else if(temp.equals(phase1_json_str)) {
                    // local is the same as server, update server
                    sync_token = phase1_json_str;
                    write_to_server = true;
                } else {
                    // local is different from server, use server contents to overwrite
                    sync_token = phase1_json_str;
                    write_to_server = false;
                }
            }
            if(write_to_server) {
                List<NewsFavEntry> entries = LitePal.findAll(NewsFavEntry.class);
                String serialized = gson.toJson(entries);
                JsonObject phase3 = userApiManager.putJsonBlocking(username, pwd, username + SYNC_ENTRY_NAME, serialized);
                if(phase3.get("status").getAsInt() != 0) {
                    Log.e(TAG, "Phase 3: err");
                    syncResult.stats.numAuthExceptions++;
                    helper.createNotification(main_ctx.getString(R.string.sync_notification_title_failure), main_ctx.getString(R.string.sync_notification_failure_content));
                    return;
                }
                JsonObject phase4 = userApiManager.putJsonBlocking(username, pwd, username + SYNC_STAT_VARNAME, sync_token);
                if(phase4.get("status").getAsInt() != 0) {
                    Log.e(TAG, "Phase 4: err");
                    syncResult.stats.numAuthExceptions++;
                    helper.createNotification(main_ctx.getString(R.string.sync_notification_title_failure), main_ctx.getString(R.string.sync_notification_failure_content));
                    return;
                }
            } else {
                JsonObject phase2 = userApiManager.getJsonBlocking(username, pwd, username + SYNC_ENTRY_NAME);
                if(phase2.get("status").getAsInt() != 0) {
                    Log.e(TAG, "Phase 2: err");
                    syncResult.stats.numAuthExceptions++;
                    helper.createNotification(main_ctx.getString(R.string.sync_notification_title_failure), main_ctx.getString(R.string.sync_notification_failure_content));
                    return;
                }
                String serialized = phase2.getAsJsonObject("payload").get("json").getAsString();
                List<NewsFavEntry> entries = gson.fromJson(serialized, new TypeToken<List<NewsFavEntry>>() {}.getType());
                // clear db and dump entries
                try {
                    LitePal.deleteAll(NewsFavEntry.class, "1");
                    for(NewsFavEntry entry : entries) {
                        entry.downloadImageToBuffer(false);
                        NewsFavEntry new_entry = new NewsFavEntry(entry);
                        new_entry.save();
                    }
                } catch (Exception e){}
            }
            ConfigManager.putStringNow(Definition.SYNC_TOKEN, sync_token);
            helper.createNotification(main_ctx.getString(R.string.sync_notification_title_success), main_ctx.getString(R.string.sync_notification_success_content));
        } catch (Exception e) {
            e.printStackTrace();
            syncResult.stats.numParseExceptions++;
            helper.createNotification(main_ctx.getString(R.string.sync_notification_title_failure), main_ctx.getString(R.string.sync_notification_failure_content));
        }
    }

    public static void perforSync(Context ctx) {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(getSyncAccount(ctx), ctx.getString(R.string.content_authority), b);
    }
}
