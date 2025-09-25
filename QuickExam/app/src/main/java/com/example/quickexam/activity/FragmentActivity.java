package com.example.quickexam.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.quickexam.BuildConfig;
import com.example.quickexam.fragment.BloodFragment;
import com.seeta.mvp.MainFragment;
import com.seeta.mvp.PresenterImpl;
import com.example.quickexam.R;

public class FragmentActivity extends AppCompatActivity {

    public static final String TAG = "FragmentActivity";
    private Fragment mMainFragment;
    private Fragment mBloodFragment;
    private Fragment currentFragment = new Fragment();//全局

    public String _id = "";
    public String recognizedName = "";
    public String id = "";
    public String age = "";
    public String sex = "";
    public String height = "";
    public String weight = "";
    public float[] feats;
    public boolean changeBP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fragment);
        mMainFragment = FragmentFactory.createMain(this, BuildConfig.BUILD_TYPE);
        mBloodFragment = FragmentFactory.createXBlood(this);
        changeMain();
        this.setFinishOnTouchOutside(false);
    }

    public FragmentTransaction switchFragment(int id, Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(id, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }

    public void changeMain() {
        switchFragment(android.R.id.content, mMainFragment).commit();
    }


    public void changeBlood() {
        switchFragment(android.R.id.content, mBloodFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        View decorView = getWindow().getDecorView();
        int uiOptions = decorView.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged");
    }

    private static class FragmentFactory {

        private static MainFragment fragment;
        private static BloodFragment bfragment;
        private static PresenterImpl presenter;

        public static Fragment createMain(Context context, String buildType) {
            if (fragment == null) {
                fragment = new MainFragment();
                presenter = new PresenterImpl(context, fragment);
            }
            return fragment;
        }

        public static Fragment createXBlood(Context context) {
            if (bfragment == null) {
                bfragment = new BloodFragment();
            }
            return bfragment;
        }
    }
}