package com.github.jacksonke.lockscreensample;

import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;

import com.github.dubu.lockscreensample.R;
import com.github.dubu.lockscreenusingservice.service.ViewControllerBase;
import com.github.dubu.lockscreenusingservice.service.ViewControllerHelper;

import java.lang.ref.WeakReference;

/**
 * Created by jacksonke on 2016/3/1.
 */
public class TransitionViewController extends ViewControllerBase {
    private View mLockscreenView = null;

    private static final int CMD_DETACH_LOCKSCREEN = 1;

    private static class MsgHandler extends Handler{
        WeakReference<TransitionViewController> mControllerRef;

        public MsgHandler(TransitionViewController controller){
            super();
            mControllerRef = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == CMD_DETACH_LOCKSCREEN){
                if (mControllerRef != null){
                    TransitionViewController controller = mControllerRef.get();
                    if (controller != null){
                        controller.dettachLockScreenView();
                    }
                }
            }
        }
    }

    private MsgHandler mHandler = null;

    @Override
    public void onViewAttached() {
        mHandler = new MsgHandler(this);
        Message msg = new Message();
        msg.what = CMD_DETACH_LOCKSCREEN;
        mHandler.sendMessageDelayed(msg, 2000);
    }

    @Override
    public View onViewInflate(LayoutInflater inflater) {
        mLockscreenView =inflater.inflate(R.layout.custom_view_transition, null);
        return mLockscreenView;
    }

    @Override
    public void onViewDetached() {
        mLockscreenView = null;
        ViewControllerHelper.getDefaultInstance().setViewController(new SimpleViewController());
    }

    @Override
    public ArrayMap<Integer, Runnable> onProvideMsg() {
        return null;
    }
}
