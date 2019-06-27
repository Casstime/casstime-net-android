package com.casstime.net;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.Collection;


/**
 * > Created by Mai on 2019/4/25
 * *
 * > Description: 显示 网络异常 或 空状态 等状态视图
 * *
 */
public class StatusView extends FrameLayout {

    private final int POSITION_ORIGIN = 0;

    private final int POSITION_MESSAGE = 1;


    public static final int STATE_LOADING = 0;

    public static final int STATE_NORMAL = 1;

    public static final int STATE_EMPTY = -1;

    public static final int STATE_ERROR = 403;


    private View originView = null;

    private View loadingView = null;

    private View emptyView = null;

    private View errorView = null;


    public StatusView(@NonNull Context context) {
        super(context);
        addView(new View(context), POSITION_ORIGIN);
        addView(new View(context), POSITION_MESSAGE);
    }

    public void init() {
        addView(null, POSITION_ORIGIN);
        addView(null, POSITION_MESSAGE);
    }

    public void setStatus(View view) {
        if (view == null) return;
        if (getChildAt(POSITION_MESSAGE) != null) {
            removeViewAt(POSITION_MESSAGE);
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            addView(view, POSITION_MESSAGE, layoutParams);
        } else {
            addView(view, POSITION_MESSAGE);
        }
        view.setVisibility(VISIBLE);
        show();
        requestLayout();
    }

    /**
     * 显示在哪个View上
     * @param view
     */
    public void showAt(@NonNull View view) {
        showAt(view, view.getLayoutParams());
    }

    public void showAt(@NonNull View view, int width, int height) {
        showAt(view, view.getLayoutParams());
        getLayoutParams().width = width;
        getLayoutParams().height = height;
        requestLayout();
    }

    public void showAt(@NonNull View view, ViewGroup.LayoutParams layoutParams) {
        originView = view;
        ViewParent viewParent = view.getParent();
        ViewGroup parent = null;
        if (viewParent != null) {
            parent = (ViewGroup) viewParent;
        }
        if (parent != null && parent != this) {
            int index = parent.indexOfChild(view);
            parent.removeView(view);
            parent.addView(this, index, layoutParams);
        }
        if (getChildAt(POSITION_MESSAGE) != null) {
            removeViewAt(POSITION_ORIGIN);
        }
        addView(view, POSITION_ORIGIN, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        if (parent != null) {
            parent.requestLayout();
        } else {
            requestLayout();
        }
    }

    private Comparator mComparator = new Comparator() {
        @Override
        public int compare(Object data) {
            if (data == null) {
                return STATE_ERROR;
            } else if (data instanceof Collection && ((Collection) data).size() <= 0) {
                return STATE_EMPTY;
            }
            return STATE_NORMAL;
        }
    };

    public void react(Object data) {
        react(mComparator.compare(data));
    }

    public void react(int action) {
        switch (action) {
            case STATE_NORMAL:
                hide();
                break;
            case STATE_LOADING:
                setStatus(loadingView);
                break;
            case STATE_EMPTY:
                setStatus(emptyView);
                break;
            case STATE_ERROR:
                setStatus(errorView);
                break;
        }
    }

    public void show() {
        View child = getChildAt(POSITION_MESSAGE);
        if (child != null) {
            child.setVisibility(VISIBLE);
        }
        View originView = getChildAt(POSITION_ORIGIN);
        if (originView != null) {
            originView.setVisibility(INVISIBLE);
        }
    }

    public void hide() {
        View child = getChildAt(POSITION_MESSAGE);
        if (child != null) {
            child.setVisibility(INVISIBLE);
        }
        View originView = getChildAt(POSITION_ORIGIN);
        if (originView != null) {
            originView.setVisibility(VISIBLE);
        }
        if (getParent() != null) {
            getParent().requestLayout();
        } else {
            requestLayout();
        }
    }

    /**
     * 设置Loading视图
     * @return
     */
    public View getLoadingView() {
        return loadingView;
    }

    public void setLoadingView(@NonNull View loadingView) {
        this.loadingView = loadingView;
    }

    public void setLoadingView(@LayoutRes int layoutResId) {
        this.loadingView = inflect(layoutResId);
    }

    public void setLoadingView(@LayoutRes int layoutResId, FrameLayout.LayoutParams params) {
        setLoadingView(layoutResId);
        if (this.loadingView != null && params != null) {
            loadingView.setLayoutParams(params);
        }
    }

    public void setLoadingView(@LayoutRes int layoutResId, int width, int height) {
        setLoadingView(layoutResId, new FrameLayout.LayoutParams(width, height));
    }

    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置空状态视图
     * @param emptyView
     */
    public void setEmptyView(@NonNull View emptyView) {
        this.emptyView = emptyView;
    }

    public void setEmptyView(@LayoutRes int layoutResId) {
        this.emptyView = inflect(layoutResId);
    }

    public void setEmptyView(@LayoutRes int layoutResId, FrameLayout.LayoutParams params) {
        setEmptyView(layoutResId);
        if (this.emptyView != null && params != null) {
            emptyView.setLayoutParams(params);
        }
    }

    public void setEmptyView(@LayoutRes int layoutResId, int width, int height) {
        setEmptyView(layoutResId, new FrameLayout.LayoutParams(width, height));
    }

    public View getErrorView() {
        return errorView;
    }

    /**
     * 设置错误视图
     * @param emptyView
     */
    public void setErrorView(@NonNull View emptyView) {
        this.errorView = emptyView;
    }

    public void setErrorView(@LayoutRes int layoutResId) {
        this.errorView = inflect(layoutResId);
    }

    public void setErrorView(@LayoutRes int layoutResId, FrameLayout.LayoutParams params) {
        setErrorView(layoutResId);
        if (this.errorView != null && params != null) {
            errorView.setLayoutParams(params);
        }
    }

    public void setErrorView(@LayoutRes int layoutResId, int width, int height) {
        setErrorView(layoutResId, new FrameLayout.LayoutParams(width, height));
    }

    public View getOriginView() {
        return originView;
    }

    public Comparator getComparator() {
        return mComparator;
    }

    public void setComparator(@NonNull Comparator mComparator) {
        this.mComparator = mComparator;
    }

    private View inflect(@LayoutRes int layoutResId) {
        return View.inflate(getContext(), layoutResId, null);
    }

    public interface Comparator {
        int compare(Object obj);
    }
}
