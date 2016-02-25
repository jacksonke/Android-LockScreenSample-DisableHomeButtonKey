package com.github.dubu.lockscreenusingservice.service;

import android.content.Context;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/2/23.
 */
public abstract class ViewControllerBase {
    private WeakReference<Handler> mHandlerRef = null;

    public static final int MSGID_DETACH_LOCKVIEW = 100;
    public static final  int MSGID_CUSTOM_MSG_START = 200;

    public abstract ViewControllerBase getInstance();

    public abstract void onViewAttached();

    public abstract View onViewInflate(LayoutInflater inflater);

    public abstract void onViewDetached();

    public abstract ArrayMap<Integer, Runnable> onProvideMsg();

    public void onSetContext(Context context){

    }

    public void onSetMessageHandler(Handler handler){
        if (null == handler){
            return;
        }

        mHandlerRef = new WeakReference<Handler>(handler);
    }

    public boolean postMessage(int msgId){
        boolean ret = false;
        do {
            if (mHandlerRef == null){
                break;
            }

            Handler handler = mHandlerRef.get();
            if (handler == null){
                break;
            }

            handler.sendEmptyMessage(msgId);
            ret = true;
        } while (false);

        return ret;
    }

    public void dettachLockScreenView(){
        postMessage(MSGID_DETACH_LOCKVIEW);
    }
}
