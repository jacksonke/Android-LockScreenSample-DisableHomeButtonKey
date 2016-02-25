package com.github.dubu.lockscreenusingservice.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.dubu.lockscreenusingservice.DebugLog;
import com.github.dubu.lockscreenusingservice.Lockscreen;
import com.github.dubu.lockscreenusingservice.LockscreenUtil;
import com.github.dubu.lockscreenusingservice.R;
import com.github.dubu.lockscreenusingservice.SharedPreferencesUtil;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;


/**
 * Created by DUBULEE on 15. 5. 20..
 */
public class LockscreenViewService extends Service {

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private boolean mIsLockEnable = false;
    private boolean mIsSoftkeyEnable = false;
    private int mServiceStartId = 0;
    private SendMassgeHandler mMainHandler = null;

    private ArrayMap<Integer, Runnable> mMsgMap = new ArrayMap<>();

    ViewControllerBase mViewController = null;


//    private boolean sIsSoftKeyEnable = false;

    private class SendMassgeHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == ViewControllerBase.MSGID_DETACH_LOCKVIEW){
                dettachLockScreenView();
                return;
            }

            if (null == mMsgMap){
                return;
            }

            int idx = mMsgMap.indexOfKey(msg.what);
            if (idx >= 0){
                Runnable runnable = mMsgMap.get(Integer.valueOf(msg.what));
                if (runnable != null){
                    runnable.run();
                }
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SharedPreferencesUtil.init(mContext);
//        sIsSoftKeyEnable = SharedPreferencesUtil.get(Lockscreen.ISSOFTKEY);

        mViewController= ViewControllerHelper.getDefaultInstance().getCurrentVewController();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DebugLog.d("onStartCommand called");
        mMainHandler = new SendMassgeHandler();
        if (isLockScreenAble()) {
            if (null != mWindowManager) {
                if (null != mLockscreenView) {
                    mWindowManager.removeView(mLockscreenView);
                }
                mWindowManager = null;
                mParams = null;
                mInflater = null;
                mLockscreenView = null;
            }
            initState();
            initView();
            attachLockScreenView();
        }
        return LockscreenViewService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        dettachLockScreenView();
    }


    private void initState() {

        DebugLog.d("initState called");
        mIsLockEnable = LockscreenUtil.getInstance(mContext).isStandardKeyguardState();
        if (mIsLockEnable) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                    PixelFormat.TRANSLUCENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mIsLockEnable && mIsSoftkeyEnable) {
                mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            } else {
                mParams.flags = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
            }
        } else {
            mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }

        if (null == mWindowManager) {
            mWindowManager = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE));
        }
    }

    private void initView() {
        DebugLog.d("initView called");

        if (null == mInflater) {
            mInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (null == mLockscreenView) {
            if (mViewController != null){

                mMsgMap = mViewController.onProvideMsg();

                mViewController.onSetContext(mContext);

                mViewController.onSetMessageHandler(mMainHandler);

                mLockscreenView = mViewController.onViewInflate(mInflater);
            }
        }
    }

    private boolean isLockScreenAble() {
        boolean isLock = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
        if (isLock) {
            isLock = true;
        } else {
            isLock = false;
        }
        return isLock;
    }


    private void attachLockScreenView() {
        DebugLog.d("attachLockScreenView called");
        if (null != mWindowManager && null != mLockscreenView && null != mParams) {
            mLockscreenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            mWindowManager.addView(mLockscreenView, mParams);

            if (mViewController != null){
                mViewController.onViewAttached();
            }

        }
    }

    private boolean dettachLockScreenView() {
        if (null != mWindowManager && null != mLockscreenView) {
            mWindowManager.removeView(mLockscreenView);
            mLockscreenView = null;
            mWindowManager = null;

            if (mViewController != null){
                mViewController.onViewDetached();
                mViewController = null;
            }

            mMsgMap = null;

            stopSelf(mServiceStartId);
            return true;
        } else {
            return false;
        }
    }


}
