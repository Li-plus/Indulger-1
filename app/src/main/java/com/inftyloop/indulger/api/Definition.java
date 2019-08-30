package com.inftyloop.indulger.api;

/**
 * Constant defs for API and config items
 */
public class Definition {
    public static final String URL_VIDEO = "/video/urls/v/l/toutiao/mp4/%s?r=%s";

    public static final String DATA_SELECTED = "data_selected";
    public static final String DATA_UNSELECTED = "data_unselected";
    public static final String CHANNEL_CODE = "channel_code";
    public static final String IS_RECOMMEND = "is_recommend";
    public static final String CHANNEL_NAME = "channel_name";
    public static final String IS_VIDEO_LIST = "is_video_list";

    /**
     * Fields for Configuration File
     **/
    public static final String SETTINGS_SELECTED_CHANNEL_JSON = "selected_channel_json";
    public static final String SETTINGS_UNSELECTED_CHANNEL_JSON = "unselected_channel_json";
    public static final String SETTINGS_SEARCH_HISTORY = "search_history";
    public static final String SETTINGS_APP_THEME = "app_theme";
    public static final String SETTINGS_APP_NIGHT_MODE_ENABLED = "app_night_mode_enabled";
    public static final String SETTINGS_APP_NIGHT_MODE_FOLLOW_SYS = "app_night_mode_follow_sys";
    public static final String SETTINGS_APP_LANG = "app_lang";

    public static final String WECHAT_APP_ID = "wx2e9b96128669c41e";

    public static final String BLOCKED_KEYS = "block_keys";
    public static final String RECOMMENDED_KEYS = "recommended_keys";

    public static final String WEIBO_APP_ID = "1722100576";
    public static final String WEIBO_REDIRECT_URL = "http://www.sina.com";
    public static final String WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    public static final String IS_FAV_ADAPTER = "is_fav_adapter";

    public static final int REQUEST_CODE_SETTINGS = 1;
    public static final int REQUEST_CODE_LOGIN = 2;
    public static final int REQUEST_CODE_SIGN_UP = 3;

    // user login fields
    public static final String LOGIN_USERNAME = "username";
    public static final String LOGIN_ENCODED_PWD = "encoded_pwd";
}
