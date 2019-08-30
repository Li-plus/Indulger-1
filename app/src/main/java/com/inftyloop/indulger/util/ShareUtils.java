package com.inftyloop.indulger.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.Nullable;
import com.inftyloop.indulger.api.Definition;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class ShareUtils {
    public final static String TAG = ShareUtils.class.getSimpleName();
    private IWXAPI iwxapi;

    public void regToWX(Context ctx) {
        iwxapi = WXAPIFactory.createWXAPI(ctx, Definition.WECHAT_APP_ID, true);
        iwxapi.registerApp(Definition.WECHAT_APP_ID);
    }

    public IWXAPI getIwxapi() {
        return iwxapi;
    }

    public void setIwxapi(IWXAPI iwxapi) {
        this.iwxapi = iwxapi;
    }

    private void shareToWeChatImpl(String url, String shareTitle, String description, @Nullable Bitmap bitmap, int flag) {
        if(iwxapi == null) {
            Log.w(TAG, "Please register API before sharing.");
            return;
        }
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;
        WXMediaMessage mediaMessage = new WXMediaMessage(webpageObject);
        mediaMessage.title = shareTitle;
        mediaMessage.description = description;
        if(bitmap != null)
            mediaMessage.setThumbImage(bitmap);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = mediaMessage;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        iwxapi.sendReq(req);
    }

    public void shareToWeChatFriends(String url, String shareTitle, String description, @Nullable Bitmap bitmap) {
        shareToWeChatImpl(url, shareTitle, description, bitmap, 0);
    }

    public void shareToWeChatMoments(String url, String shareTitle, String description, @Nullable Bitmap bitmap) {
        shareToWeChatImpl(url, shareTitle, description, bitmap, 1);
    }

    public void shareToWeibo(WbShareHandler handler, String content, @Nullable Bitmap bitmap) {
        WeiboMultiMessage weiboMsg = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = content;
        weiboMsg.textObject = textObject;
        if(bitmap != null) {
            ImageObject imageObject = new ImageObject();
            imageObject.setImageObject(bitmap);
            weiboMsg.imageObject = imageObject;
        }
        handler.shareMessage(weiboMsg, false);
    }
}
