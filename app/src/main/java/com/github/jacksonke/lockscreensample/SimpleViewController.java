package com.github.jacksonke.lockscreensample;

import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.github.dubu.lockscreensample.R;
import com.github.dubu.lockscreenusingservice.service.ViewControllerBase;
import com.github.dubu.lockscreenusingservice.service.ViewControllerHelper;

/**
 * Created by jacksonke on 2016/2/25.
 */
public class SimpleViewController extends ViewControllerBase{

    private View mLockscreenView = null;
    private Button mUnlockButton = null;

    @Override
    public void onViewAttached() {
        mUnlockButton = (Button) mLockscreenView.findViewById(R.id.button_unlock);
        mUnlockButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // destroy lockscreen directly
//                dettachLockScreenView();

                // jump to the transition view.
                ViewControllerHelper.getDefaultInstance().setViewController(new TransitionViewController());
            }
        });
    }

    @Override
    public View onViewInflate(LayoutInflater inflater) {
        mLockscreenView =inflater.inflate(R.layout.custom_view_simple, null);
        return mLockscreenView;
    }

    @Override
    public void onViewDetached() {
        mUnlockButton = null;
        mLockscreenView = null;
    }

    @Override
    public ArrayMap<Integer, Runnable> onProvideMsg() {
        return null;
    }
}
