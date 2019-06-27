package com.casstime.net;


import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

/**
 * > Created by Mai on 2018/7/27
 * *
 * > Description: 自动监听数据变化，显示空页面、错误提示页面，loading页面的 Observer
 * *
 */
public abstract class StatusObserver<T> implements Observer<T> {

    private StatusView mStatusView;

    public StatusObserver() {
    }

    public StatusObserver(StatusView statusView) {
        this.mStatusView = statusView;
    }

    @Override
    public void onChanged(@Nullable T t) {
        if (mStatusView != null) {
            mStatusView.react(t);
        }
    }
}
