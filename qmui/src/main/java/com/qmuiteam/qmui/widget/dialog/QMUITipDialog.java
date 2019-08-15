/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qmuiteam.qmui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;
import com.qmuiteam.qmui.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUILoadingView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 提供一个浮层展示在屏幕中间, 一般使用 {@link QMUITipDialog.Builder} 或 {@link QMUITipDialog.CustomBuilder} 生成。
 * <ul>
 * <li>{@link QMUITipDialog.Builder} 提供了一个图标和一行文字的样式, 其中图标有几种类型可选, 见 {@link QMUITipDialog.Builder.IconType}</li>
 * <li>{@link QMUITipDialog.CustomBuilder} 支持传入自定义的 layoutResId, 达到自定义 TipDialog 的效果。</li>
 * </ul>
 *
 * @author cginechen
 * @date 2016-10-14
 */

public class QMUITipDialog extends Dialog {

    public QMUITipDialog(Context context) {
        this(context, R.style.QMUI_TipDialog);
    }

    public QMUITipDialog(Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
    }

    public void allowCancelOnTouchOutside(boolean b) {
        setCanceledOnTouchOutside(b);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogWidth();
    }

    private void initDialogWidth() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wmLp = window.getAttributes();
            wmLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(wmLp);
        }
    }

    /**
     * 生成默认的 {@link QMUITipDialog}
     * <p>
     * 提供了一个图标和一行文字的样式, 其中图标有几种类型可选。见 {@link IconType}
     * </p>
     *
     * @see CustomBuilder
     */
    public static class Builder {
        /**
         * 不显示任何icon
         */
        public static final int ICON_TYPE_NOTHING = 0;
        /**
         * 显示 Loading 图标
         */
        public static final int ICON_TYPE_LOADING = 1;
        /**
         * 显示成功图标
         */
        public static final int ICON_TYPE_SUCCESS = 2;
        /**
         * 显示失败图标
         */
        public static final int ICON_TYPE_FAIL = 3;
        /**
         * 显示信息图标
         */
        public static final int ICON_TYPE_INFO = 4;

        private static ImageView createImageViewWithIcon(Context mContext, @IconType int mCurrentIconType) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams imageViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(imageViewLP);

            if (mCurrentIconType == ICON_TYPE_SUCCESS) {
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.qmui_icon_notify_done));
            } else if (mCurrentIconType == ICON_TYPE_FAIL) {
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.qmui_icon_notify_error));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.qmui_icon_notify_info));
            }
            return imageView;
        }

        @SuppressWarnings("Duplicates")
        public static Toast makeToast(Context ctx, @IconType int iconType, String prompt, int duration) {
            Toast toast = Toast.makeText(ctx, prompt, duration);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            LinearLayout contentLayout = (LinearLayout) toast.getView();
            contentLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            TextView tv = (TextView) contentLayout.getChildAt(0);
            if (iconType == ICON_TYPE_SUCCESS || iconType == ICON_TYPE_FAIL || iconType == ICON_TYPE_INFO) {
                ImageView img = createImageViewWithIcon(ctx, iconType);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, QMUIDisplayHelper.dp2px(ctx, 10), 0, 0);
                img.setLayoutParams(lp);
                contentLayout.addView(img, 0);
            }
            LinearLayout.LayoutParams textView_LP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (iconType != ICON_TYPE_NOTHING) {
                textView_LP.topMargin = QMUIDisplayHelper.dp2px(ctx, 12);
            } else
                textView_LP.topMargin = QMUIDisplayHelper.dp2px(ctx, 10);
            textView_LP.leftMargin = QMUIDisplayHelper.dp2px(ctx, 25);
            textView_LP.rightMargin = QMUIDisplayHelper.dp2px(ctx, 25);
            textView_LP.bottomMargin = QMUIDisplayHelper.dp2px(ctx, 10);
            tv.setLayoutParams(textView_LP);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setGravity(Gravity.CENTER);
            tv.setMaxLines(2);
            tv.setTextColor(ContextCompat.getColor(ctx, R.color.qmui_config_color_white));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            contentLayout.setBackgroundResource(R.drawable.qmui_tip_dialog_bg);
            return toast;
        }

        @IntDef({ICON_TYPE_NOTHING, ICON_TYPE_LOADING, ICON_TYPE_SUCCESS, ICON_TYPE_FAIL, ICON_TYPE_INFO})
        @Retention(RetentionPolicy.SOURCE)
        public @interface IconType {
        }

        private @IconType int mCurrentIconType = ICON_TYPE_NOTHING;

        private Context mContext;

        private CharSequence mTipWord;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 设置 icon 显示的内容
         *
         * @see IconType
         */
        public Builder setIconType(@IconType int iconType) {
            mCurrentIconType = iconType;
            return this;
        }

        /**
         * 设置显示的文案
         */
        public Builder setTipWord(CharSequence tipWord) {
            mTipWord = tipWord;
            return this;
        }

        public QMUITipDialog create(){
            return create(true);
        }

        /**
         * 创建 Dialog, 但没有弹出来, 如果要弹出来, 请调用返回值的 {@link Dialog#show()} 方法
         *
         * @param cancelable 按系统返回键是否可以取消
         * @return 创建的 Dialog
         */
        public QMUITipDialog create(boolean cancelable) {
            QMUITipDialog dialog = new QMUITipDialog(mContext);
            dialog.setCancelable(cancelable);
            dialog.setContentView(R.layout.qmui_tip_dialog_layout);
            ViewGroup contentWrap = (ViewGroup) dialog.findViewById(R.id.contentWrap);

            if (mCurrentIconType == ICON_TYPE_LOADING) {
                QMUILoadingView loadingView = new QMUILoadingView(mContext);
                loadingView.setColor(Color.WHITE);
                loadingView.setSize(QMUIDisplayHelper.dp2px(mContext, 32));
                LinearLayout.LayoutParams loadingViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                loadingView.setLayoutParams(loadingViewLP);
                contentWrap.addView(loadingView);

            } else if (mCurrentIconType == ICON_TYPE_SUCCESS || mCurrentIconType == ICON_TYPE_FAIL || mCurrentIconType == ICON_TYPE_INFO) {
                ImageView imageView = createImageViewWithIcon(mContext, mCurrentIconType);
                contentWrap.addView(imageView);
            }

            if (mTipWord != null && mTipWord.length() > 0) {
                TextView tipView = new TextView(mContext);
                LinearLayout.LayoutParams tipViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                if (mCurrentIconType != ICON_TYPE_NOTHING) {
                    tipViewLP.topMargin = QMUIDisplayHelper.dp2px(mContext, 12);
                }
                tipView.setLayoutParams(tipViewLP);

                tipView.setEllipsize(TextUtils.TruncateAt.END);
                tipView.setGravity(Gravity.CENTER);
                tipView.setMaxLines(2);
                tipView.setTextColor(ContextCompat.getColor(mContext, R.color.qmui_config_color_white));
                tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tipView.setText(mTipWord);

                contentWrap.addView(tipView);
            }
            return dialog;
        }

    }

    /**
     * 传入自定义的布局并使用这个布局生成 TipDialog
     */
    public static class CustomBuilder {
        private Context mContext;
        private int mContentLayoutId;

        public CustomBuilder(Context context) {
            mContext = context;
        }

        public CustomBuilder setContent(@LayoutRes int layoutId) {
            mContentLayoutId = layoutId;
            return this;
        }

        /**
         * 创建 Dialog, 但没有弹出来, 如果要弹出来, 请调用返回值的 {@link Dialog#show()} 方法
         *
         * @return 创建的 Dialog
         */
        public QMUITipDialog create() {
            QMUITipDialog dialog = new QMUITipDialog(mContext);
            dialog.setContentView(R.layout.qmui_tip_dialog_layout);
            ViewGroup contentWrap = (ViewGroup) dialog.findViewById(R.id.contentWrap);
            LayoutInflater.from(mContext).inflate(mContentLayoutId, contentWrap, true);
            return dialog;
        }
    }
}
