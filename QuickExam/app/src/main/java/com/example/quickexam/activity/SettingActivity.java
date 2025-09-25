package com.example.quickexam.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.quickexam.MainApplication;
import com.example.quickexam.R;
import com.example.quickexam.bean.ConfigBean;
import com.example.quickexam.databinding.ActivitySettingBinding;
import com.example.quickexam.utils.FileUtils;
import com.example.quickexam.utils.NavigationBarUtil;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationBarUtil.focusNotAle(getWindow());
        binding = ActivitySettingBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());
        NavigationBarUtil.hideNavigationBar(getWindow());
        NavigationBarUtil.clearFocusNotAle(getWindow());
        initView();
        initData();
    }

    private void initView() {
        binding.back.setOnClickListener(this);
        binding.commit.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.commit:
                //保存
                ArrayList<ConfigBean.SettingBean.ProjectBean> projectBeans = new ArrayList<>();
                ConfigBean.SettingBean.ProjectBean projectBean = null;
                projectBean = new ConfigBean.SettingBean.ProjectBean();
                if (binding.checkBoxTw.isChecked()) {
                    projectBean.setName("体温");
                    projectBean.setIs_open(1);
                }
                projectBeans.add(projectBean);
                projectBean = new ConfigBean.SettingBean.ProjectBean();
                if (binding.checkBoxXy.isChecked()) {
                    projectBean.setName("血压");
                    projectBean.setIs_open(1);
                }
                projectBeans.add(projectBean);
                projectBean = new ConfigBean.SettingBean.ProjectBean();
                if (binding.checkboxXy.isChecked()) {
                    projectBean.setName("血氧");
                    projectBean.setIs_open(1);
                }
                projectBeans.add(projectBean);
                projectBean = new ConfigBean.SettingBean.ProjectBean();
                if (binding.checkboxJj.isChecked()) {
                    projectBean.setName("酒精");
                    projectBean.setIs_open(1);
                }
                projectBeans.add(projectBean);
                projectBean = null;
                ConfigBean.SettingBean settingBean = new ConfigBean.SettingBean();
                settingBean.setProject(projectBeans);
                ConfigBean configBean = new ConfigBean();
                configBean.setSetting(settingBean);
                MainApplication.configMainBean = configBean;
                FileUtils.write(FileUtils.pathHead, FileUtils.settingFileName);
                setResult(2000);
                finish();
                break;
        }
    }
}