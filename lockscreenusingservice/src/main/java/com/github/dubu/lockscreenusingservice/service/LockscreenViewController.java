package com.github.dubu.lockscreenusingservice.service;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.dubu.lockscreenusingservice.DebugLog;
import com.github.dubu.lockscreenusingservice.LockscreenUtil;
import com.github.dubu.lockscreenusingservice.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

/**
 * Created by Administrator on 2016/2/19.
 */
public class LockscreenViewController extends ViewControllerBase {
    private final int LOCK_OPEN_OFFSET_VALUE = 50;

    private final int MSGID_CHANGE_BACKGROUND_LOCK_VIEW = ViewControllerBase.MSGID_CUSTOM_MSG_START + 1;

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private RelativeLayout mBackgroundLayout = null;
    private RelativeLayout mBackgroundInLayout = null;
    private ImageView mBackgroundLockImageView = null;
    private RelativeLayout mForgroundLayout = null;
    private RelativeLayout mStatusBackgruondDummyView = null;
    private RelativeLayout mStatusForgruondDummyView = null;
    private ShimmerTextView mShimmerTextView = null;
    private boolean mIsLockEnable = false;
    private int mDeviceWidth = 0;
    private int mDevideDeviceWidth = 0;
    private float mLastLayoutX = 0;


    public LockscreenViewController(){

    }

    Runnable mOnChangeBackgroundLockView = new Runnable() {
        @Override
        public void run() {
            changeBackGroundLockView(mLastLayoutX);
        }
    };

    ArrayMap<Integer, Runnable> mMsgMap;


    private void initMsgMap(){
        mMsgMap = new ArrayMap<>();
        mMsgMap.put(Integer.valueOf(MSGID_CHANGE_BACKGROUND_LOCK_VIEW), mOnChangeBackgroundLockView);
    }



    private void settingLockView() {
        mBackgroundLayout = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_background_layout);
        mBackgroundInLayout = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_background_in_layout);
        mBackgroundLockImageView = (ImageView) mLockscreenView.findViewById(R.id.lockscreen_background_image);
        mForgroundLayout = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_forground_layout);
        mShimmerTextView = (ShimmerTextView) mLockscreenView.findViewById(R.id.shimmer_tv);
        (new Shimmer()).start(mShimmerTextView);
        mForgroundLayout.setOnTouchListener(mViewTouchListener);

        mStatusBackgruondDummyView = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_background_status_dummy);
        mStatusForgruondDummyView = (RelativeLayout) mLockscreenView.findViewById(R.id.lockscreen_forground_status_dummy);
        setBackGroundLockView();

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mDeviceWidth = displayMetrics.widthPixels;
        mDevideDeviceWidth = (mDeviceWidth / 2);
        mBackgroundLockImageView.setX((int) (((mDevideDeviceWidth) * -1)));

        //kitkat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int val = LockscreenUtil.getInstance(mContext).getStatusBarHeight();
            RelativeLayout.LayoutParams forgroundParam = (RelativeLayout.LayoutParams) mStatusForgruondDummyView.getLayoutParams();
            forgroundParam.height = val;
            mStatusForgruondDummyView.setLayoutParams(forgroundParam);
            AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F);
            alpha.setDuration(0); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            mStatusForgruondDummyView.startAnimation(alpha);
            RelativeLayout.LayoutParams backgroundParam = (RelativeLayout.LayoutParams) mStatusBackgruondDummyView.getLayoutParams();
            backgroundParam.height = val;
            mStatusBackgruondDummyView.setLayoutParams(backgroundParam);
        }
    }

    private void setBackGroundLockView() {
        if (mIsLockEnable) {
            mBackgroundInLayout.setBackgroundColor(getResources().getColor(R.color.lock_background_color));
            mBackgroundLockImageView.setVisibility(View.VISIBLE);

        } else {
            mBackgroundInLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mBackgroundLockImageView.setVisibility(View.GONE);
        }
    }


    private void changeBackGroundLockView(float forgroundX) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (forgroundX < mDeviceWidth) {
                mBackgroundLockImageView.setBackground(getResources().getDrawable(R.drawable.lock));
            } else {
                mBackgroundLockImageView.setBackground(getResources().getDrawable(R.drawable.unlock));
            }
        } else {
            if (forgroundX < mDeviceWidth) {
                mBackgroundLockImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.lock));
            } else {
                mBackgroundLockImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.unlock));
            }
        }
    }

    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        private float firstTouchX = 0;
        private float layoutPrevX = 0;
        private float lastLayoutX = 0;
        private float layoutInPrevX = 0;
        private boolean isLockOpen = false;
        private int touchMoveX = 0;
        private int touchInMoveX = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {// 0
                    firstTouchX = event.getX();
                    layoutPrevX = mForgroundLayout.getX();
                    layoutInPrevX = mBackgroundLockImageView.getX();
                    if (firstTouchX <= LOCK_OPEN_OFFSET_VALUE) {
                        isLockOpen = true;
                    }
                }
                break;
                case MotionEvent.ACTION_MOVE: { // 2
                    if (isLockOpen) {
                        touchMoveX = (int) (event.getRawX() - firstTouchX);
                        if (mForgroundLayout.getX() >= 0) {
                            mForgroundLayout.setX((int) (layoutPrevX + touchMoveX));
                            mBackgroundLockImageView.setX((int) (layoutInPrevX + (touchMoveX / 1.8)));
                            mLastLayoutX = lastLayoutX;
                            LockscreenViewController.this.postMessage(MSGID_CHANGE_BACKGROUND_LOCK_VIEW);
//                            mMainHandler.sendEmptyMessage(0);
                            if (mForgroundLayout.getX() < 0) {
                                mForgroundLayout.setX(0);
                            }
                            lastLayoutX = mForgroundLayout.getX();
                        }
                    } else {
                        return false;
                    }
                }
                break;
                case MotionEvent.ACTION_UP: { // 1
                    if (isLockOpen) {
                        mForgroundLayout.setX(lastLayoutX);
                        mForgroundLayout.setY(0);
                        optimizeForground(lastLayoutX);
                    }
                    isLockOpen = false;
                    firstTouchX = 0;
                    layoutPrevX = 0;
                    layoutInPrevX = 0;
                    touchMoveX = 0;
                    lastLayoutX = 0;
                }
                break;
                default:
                    break;
            }

            return true;
        }
    };

    private void optimizeForground(float forgroundX) {
//        final int devideDeviceWidth = (mDeviceWidth / 2);
        if (forgroundX < mDevideDeviceWidth) {
            int startPostion = 0;
            for (startPostion = mDevideDeviceWidth; startPostion >= 0; startPostion--) {
                mForgroundLayout.setX(startPostion);
            }
        } else {
            TranslateAnimation animation = new TranslateAnimation(0, mDevideDeviceWidth, 0, 0);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mForgroundLayout.setX(mDevideDeviceWidth);
                    mForgroundLayout.setY(0);
                    dettachLockScreenView();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            mForgroundLayout.startAnimation(animation);
        }
    }

    @Override
    public ViewControllerBase getInstance() {
        return new LockscreenViewController();
    }

    @Override
    public void onViewAttached() {
        settingLockView();
    }

    @Override
    public View onViewInflate(LayoutInflater inflater) {
        mInflater = inflater;
        mLockscreenView = mInflater.inflate(R.layout.view_locokscreen, null);
        return mLockscreenView;
    }

    @Override
    public void onViewDetached() {
        DebugLog.d("onViewDetached");
    }

    @Override
    public ArrayMap<Integer, Runnable> onProvideMsg() {
        if (mMsgMap == null){
            initMsgMap();
        }

        return mMsgMap;
    }

    @Override
    public void onSetContext(Context context){
        mContext = context;
    }

    Resources getResources(){
        if (mContext != null){
            return mContext.getResources();
        }
        else{
            return null;
        }
    }
}
