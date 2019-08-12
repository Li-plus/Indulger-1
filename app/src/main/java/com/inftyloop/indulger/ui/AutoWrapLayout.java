package com.inftyloop.indulger.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.inftyloop.indulger.R;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public class AutoWrapLayout extends ViewGroup {
    private int textColor;
    private int textBackgroundColor;
    private int textBackgroundResource;
    private float textViewSize;
    private int textViewPadding;
    private int textViewPaddingLeft;
    private int textViewPaddingRight;
    private int textViewPaddingTop;
    private int textViewPaddingBottom;
    private float childSpacing;

    public int contentSize = 0;
    public int lineNum = 1;

    public AutoWrapLayout(Context ctx) {
        super(ctx);
    }

    public AutoWrapLayout(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx, attrs);
    }

    public AutoWrapLayout(Context ctx, AttributeSet attrs, int defStyleAttr) {
        super(ctx, attrs, defStyleAttr);
        init(ctx, attrs);
    }

    public interface ItemListenr{
        void registerListener(int pos, TextView textView);
    }

    private ItemListenr mItemListener;

    public void setItemListener(ItemListenr listener) {
        if(listener != null) {
            mItemListener = listener;
            addItemListener();
        }
    }

    private void addItemListener() {
        int cnt = getChildCount();
        if(cnt > 0 && mItemListener != null) {
            for(int i = 0; i < cnt; ++i) {
                View cv = getChildAt(i);
                if(cv != null) {
                    final int item = i;
                    cv.setOnClickListener((v) -> mItemListener.registerListener(item, (TextView) v));
                }
            }
        }
    }

    private void init(Context ctx, AttributeSet attrs) {
        TypedArray ta = ctx.obtainStyledAttributes(attrs, R.styleable.AutoWrapLayout);
        textColor = ta.getColor(R.styleable.AutoWrapLayout_childTextColor, Color.BLACK);
        textViewSize = ta.getFloat(R.styleable.AutoWrapLayout_childTextSize, 15);
        textBackgroundColor = ta.getColor(R.styleable.AutoWrapLayout_childTextBackgroundColor, 0);
        textBackgroundResource = ta.getResourceId(R.styleable.AutoWrapLayout_childTextBackgroundResource, 0);
        textViewPadding = ta.getInt(R.styleable.AutoWrapLayout_childPadding, 0);
        textViewPaddingLeft = ta.getInt(R.styleable.AutoWrapLayout_childLeftPadding, 0);
        textViewPaddingRight = ta.getInt(R.styleable.AutoWrapLayout_childRightPadding, 0);
        textViewPaddingTop = ta.getInt(R.styleable.AutoWrapLayout_childTopPadding, 0);
        textViewPaddingBottom = ta.getInt(R.styleable.AutoWrapLayout_childBottomPadding, 0);
        childSpacing = ta.getFloat(R.styleable.AutoWrapLayout_childSpacing, 0);
        ta.recycle();
    }

    private boolean isExceedParentHeight() {
        View child = getChildAt(0);
        if(child != null) {
            contentSize = (lineNum * child.getMeasuredHeight()) + (lineNum * QMUIDisplayHelper.dp2px(getContext(), (childSpacing > 0 ? (int)childSpacing : 5)));
            return contentSize + child.getMeasuredHeight() > (getHeight() - getPaddingTop() - getPaddingBottom());
        } else return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        lineNum = 1;
        int lineWidth = getPaddingLeft();
        int lineHeight = getPaddingTop();
        int margin = QMUIDisplayHelper.dp2px(getContext(), (childSpacing > 0 ? (int)childSpacing : 5));
        int childCnt = getChildCount();
        for(int i = 0; i < childCnt; ++i) {
            View cv = getChildAt(i);
            if(cv != null) {
                if(lineWidth + cv.getMeasuredWidth() < getWidth() - getPaddingRight()) {
                    cv.layout(lineWidth, lineHeight, cv.getMeasuredWidth() + lineWidth, cv.getMeasuredHeight() + lineHeight);
                    lineWidth += (margin + cv.getMeasuredWidth());
                } else if(!isExceedParentHeight()) {
                    ++lineNum;
                    lineWidth = getPaddingLeft();
                    lineHeight += (margin + cv.getMeasuredHeight());
                    cv.layout(lineWidth, lineHeight, lineWidth + cv.getMeasuredWidth(), cv.getMeasuredHeight() + lineHeight);
                    lineWidth += (margin + cv.getMeasuredWidth());
                } else {
                    int cnt = getChildCount() - 1;
                    removeViews(1, cnt);
                    break;
                }
            }
        }
    }

    public void loadStringArray(String[] vals) {
        if(vals != null && vals.length > 0) {
            if(!isExceedParentHeight()) {
                int idx = 0;
                for (String val : vals) {
                    if (!TextUtils.isEmpty(val)) {
                        TextView tv = new TextView(getContext());
                        tv.setTextColor(textColor);
                        tv.setTextSize(this.textViewSize);
                        tv.setText(val);
                        if (textBackgroundResource != 0)
                            tv.setBackgroundResource(textBackgroundResource);
                        else if (textBackgroundColor != 0)
                            tv.setBackgroundColor(textBackgroundColor);
                        else
                            tv.setBackgroundResource(R.drawable.search_history_item_bg);
                        if (textViewPadding != 0) {
                            int paddingVal = QMUIDisplayHelper.dp2px(getContext(), textViewPadding);
                            tv.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
                        } else {
                            int pl = QMUIDisplayHelper.dp2px(getContext(), textViewPaddingLeft);
                            int pr = QMUIDisplayHelper.dp2px(getContext(), textViewPaddingRight);
                            int pt = QMUIDisplayHelper.dp2px(getContext(), textViewPaddingTop);
                            int pb = QMUIDisplayHelper.dp2px(getContext(), textViewPaddingBottom);
                            tv.setPadding(pl, pt, pr, pb);
                        }
                        addView(tv, idx++);
                    }
                }
                if (mItemListener != null) addItemListener();
            } else {
                for(int i = 0; i < vals.length; ++i) {
                    TextView tv = (TextView)getChildAt(i);
                    if(tv != null)
                        tv.setText(vals[i]);
                }
            }
        }
    }
}
