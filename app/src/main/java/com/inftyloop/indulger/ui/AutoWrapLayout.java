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

import java.util.LinkedList;

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

    private int contentSize = 0;
    public int lineNum = 1;
    LinkedList<String> itemQueue = new LinkedList<>();
    private int sizeLimit = 0;

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

    public void clearAllItems() {
        itemQueue.clear();
        clearAllViews();
    }

    private void clearAllViews() {
        while(getChildCount() > 0) {
            removeViewAt(0);
        }
    }

    public void setSizeLimit(int limit) {
        sizeLimit = limit;
    }

    private boolean findAndMove(String data, boolean isHead) {
        int idx = 0;
        for(String v : itemQueue) {
            if(v.equals(data)) {
                View view = getChildAt(idx);
                if(view != null) {
                    TextView tv = (TextView) view;
                    // don't move if already in place to avoid flicker
                    if((isHead && idx == 0)||(!isHead && idx == getChildCount() - 1))
                        return true;
                    removeView(tv);
                    itemQueue.remove(idx);
                    if(isHead) {
                        addView(tv, 0);
                        itemQueue.addFirst(v);
                    } else {
                        addView(tv, getChildCount());
                        itemQueue.addLast(v);
                    }
                }
                return true;
            }
            idx++;
        }
        return false;
    }

    /**
     * Load initial data (will clear previous ones)
     */
    public void loadData(String[] data) {
        clearAllItems();
        int upper = sizeLimit == 0 ? data.length : Math.min(sizeLimit, data.length);
        for(int i = 0; i < upper; ++i)
            itemQueue.addLast(data[i]);
        loadViewFromQueue();
    }

    public void pushBack(String data) {
        // if already exists, just move it
        if(findAndMove(data, false))
            return;
        itemQueue.addLast(data);
        if(sizeLimit > 0 && itemQueue.size() >= sizeLimit) {
            itemQueue.pollFirst();
            updateViewFromQueue();
            // no need to recreate the whole view
        } else {
            TextView tv = createTextView(data);
            addView(tv, getChildCount());
            addItemListener();
        }
    }

    public void pushFront(String data) {
        if(findAndMove(data, true))
            return;
        itemQueue.addFirst(data);
        if(sizeLimit > 0 && itemQueue.size() >= sizeLimit) {
            itemQueue.pollLast();
            updateViewFromQueue();
        } else {
            TextView tv = createTextView(data);
            addView(tv, 0);
            addItemListener();
        }
    }

    public String[] getItemArray() {
        String[] res = new String[itemQueue.size()];
        itemQueue.toArray(res);
        return res;
    }

    public void removeFront() {
        if (itemQueue.size() > 0)
            itemQueue.pollFirst();
        else
            return;
        View v = getChildAt(0);
        if (v != null)
            removeView(v);
        addItemListener();
    }

    public void removeBack() {
        if (itemQueue.size() > 0)
            itemQueue.pollLast();
        else
            return;
        View v = getChildAt(getChildCount() - 1);
        if (v != null)
            removeView(v);
        addItemListener();
    }

    public interface ItemListener {
        void registerListener(int pos, TextView textView);
    }

    private ItemListener mItemListener;

    public void setItemListener(ItemListener listener) {
        if (listener != null) {
            mItemListener = listener;
            addItemListener();
        }
    }

    private void addItemListener() {
        int cnt = getChildCount();
        if (cnt > 0 && mItemListener != null) {
            for (int i = 0; i < cnt; ++i) {
                View cv = getChildAt(i);
                if (cv != null) {
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
        if (child != null) {
            contentSize = (lineNum * child.getMeasuredHeight()) + (lineNum * QMUIDisplayHelper.dp2px(getContext(), (childSpacing > 0 ? (int) childSpacing : 5)));
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
        int margin = QMUIDisplayHelper.dp2px(getContext(), (childSpacing > 0 ? (int) childSpacing : 5));
        int childCnt = getChildCount();
        for (int i = 0; i < childCnt; ++i) {
            View cv = getChildAt(i);
            if (cv != null) {
                if (lineWidth + cv.getMeasuredWidth() < getWidth() - getPaddingRight()) {
                    cv.layout(lineWidth, lineHeight, cv.getMeasuredWidth() + lineWidth, cv.getMeasuredHeight() + lineHeight);
                    lineWidth += (margin + cv.getMeasuredWidth());
                } else if (!isExceedParentHeight()) {
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

    /**
     * Update an existing view (must ensure queue size is not changed)
     */
    private void updateViewFromQueue() {
        int idx = 0;
        for(String s : itemQueue) {
            View view = getChildAt(idx++);
            if(view != null) {
                TextView v = (TextView) view;
                v.setText(s);
            }
        }
    }

    private TextView createTextView(String val) {
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
        return tv;
    }

    private void loadViewFromQueue() {
        if (itemQueue != null && itemQueue.size() > 0) {
            clearAllViews();
            int idx = 0;
            for (String val : itemQueue) {
                if (!TextUtils.isEmpty(val)) {
                    addView(createTextView(val), idx++);
                }
            }
            if (mItemListener != null) addItemListener();
        }
    }
}
