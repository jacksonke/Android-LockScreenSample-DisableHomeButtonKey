package com.github.dubu.lockscreensample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.dubu.lockscreenusingservice.DebugLog;
import com.github.dubu.lockscreenusingservice.Lockscreen;
import com.github.dubu.lockscreenusingservice.SharedPreferencesUtil;
import com.github.dubu.lockscreenusingservice.service.ViewControllerHelper;
import com.github.jacksonke.lockscreensample.SimpleViewController;

/**
 * Created by DUBU on 15. 5. 20..
 */
public class MainActivity extends ActionBarActivity {
    private SwitchCompat mSwitchd = null;
    private Context mContext = null;
    private RadioButton mRadioButtonDefault = null;
    private RadioButton mRadioButtonSimple = null;
    private RadioGroup mRadioGroup = null;

    final static int VIEW_TYPE_DEFAULT = 0;
    final static int VIEW_TYPE_SIMPLE = 1;
    private int mViewType = VIEW_TYPE_DEFAULT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        SharedPreferencesUtil.init(mContext);

        mSwitchd = (SwitchCompat) this.findViewById(R.id.switch_locksetting);
        mSwitchd.setTextOn("yes");
        mSwitchd.setTextOff("no");
        boolean lockState = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
        if (lockState) {
            mSwitchd.setChecked(true);
        } else {
            mSwitchd.setChecked(false);
        }

        mRadioButtonDefault = (RadioButton) findViewById(R.id.radioButtonDefaultView);
        mRadioButtonSimple = (RadioButton) findViewById(R.id.radioButtonCustomSimpleView);



        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                DebugLog.d("onCheckedChanged");
                boolean isLock = SharedPreferencesUtil.get(Lockscreen.ISLOCK);
                if (i != -1 && isLock){
                    if (i == R.id.radioButtonCustomSimpleView){
                        mViewType = VIEW_TYPE_SIMPLE;
                    }
                    else if (i == R.id.radioButtonDefaultView){
                        mViewType = VIEW_TYPE_DEFAULT;
                    }

                    startLockScreen();
                }

            }
        });

        CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.getId() == R.id.radioButtonCustomSimpleView){
                    mViewType = VIEW_TYPE_SIMPLE;
                }
                else if (compoundButton.getId() == R.id.radioButtonDefaultView){
                    mViewType = VIEW_TYPE_DEFAULT;
                }
            }
        };

        mSwitchd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startLockScreen();
                } else {
                    stopLockScreen();
                }

            }
        });
    }

    private void stopLockScreen(){
//                    Intent startLockscreenIntent =  new Intent(mContext, TestService.class);
//                    mContext.stopService(startLockscreenIntent);

        SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, false);
        Lockscreen.getInstance(mContext).stopLockscreenService();

    }

    private void startLockScreen(){
        if (mViewType == VIEW_TYPE_DEFAULT){
//                        Intent startLockscreenIntent =  new Intent(mContext, TestService.class);
//                        mContext.startService(startLockscreenIntent);


            ViewControllerHelper.getDefaultInstance().setViewController(null);
            SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
            Lockscreen.getInstance(mContext).startLockscreenService();
        }
        else {
            ViewControllerHelper.getDefaultInstance().setViewController(new SimpleViewController());
            SharedPreferencesUtil.setBoolean(Lockscreen.ISLOCK, true);
            Lockscreen.getInstance(mContext).startLockscreenService();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
