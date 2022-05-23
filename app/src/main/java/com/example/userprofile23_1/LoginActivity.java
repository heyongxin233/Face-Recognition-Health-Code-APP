package com.example.userprofile23_1;

import static java.lang.Character.getType;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    public static final int REQUEST_CODE_REGISTER = 1;
    private Button btnLogin;
    private EditText etAccount,etPassword;
    private CheckBox cbRemember,cbAutoLogin;

    private String userName = "fxjzzyo";
    private String pass = "123";
    public String cookie=null;
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String str = (String) msg.obj;
                Map<String, Object> map = new ArrayMap<>();
                map = JsonMap.getMap(str);
                Log.d(TAG, "onClick: -------------" + str);
                Log.d(TAG, "onClick: -------------" + map.get("ok"));
                boolean x= (boolean) map.get("ok");
                if (x == false) {
                    Log.d(TAG, "onClick: -------------" + map.get("ok"));
                    Toast.makeText(LoginActivity.this, "没有注册账号或者账号密码错误！", Toast.LENGTH_LONG).show();
                }
                else{
                    String account = etAccount.getText().toString();
                    String password = etPassword.getText().toString();
                    NetUtil.cookie= (String) map.get("Cookie");
                    if (cbRemember.isChecked()) {
                        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
                        SharedPreferences.Editor edit = spf.edit();
                        edit.putString("account", account);
                        edit.putString("password", password);
                        edit.putBoolean("isRemember", true);
                        if (cbAutoLogin.isChecked()) {
                            edit.putBoolean("isLogin", true);
                        }else {
                            edit.putBoolean("isLogin", false);
                        }
                        edit.apply();

                    }else {
                        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
                        SharedPreferences.Editor edit = spf.edit();
                        edit.putBoolean("isRemember", false);
                        edit.apply();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url=NetUtil.urlBase +"/api/user/get_current_user";
                            String stringFromNet = null;
                            try {
                                stringFromNet=NetUtil.doPost1(url);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message=new Message();
                            message.what=6;
                            message.obj=stringFromNet;
                            mHandler.sendMessage(message);
                        }
                    }).start();
                }
//                textView.setText(str);
            }
            if (msg.what == 6){
//                SharedPreferences spf = getSharedPreferences("newdata", MODE_PRIVATE);
//                SharedPreferences.Editor edit = spf.edit();
                String str = (String) msg.obj;
                Map<String, Object> map1 = new ArrayMap<>();
                map1 = JsonMap.getMap(str);
                map1=JsonMap.getMap(map1.get("data").toString());
                for (Map.Entry<String, Object> entry : map1.entrySet()) {
//                    edit.putString(entry.getKey(), entry.getValue().toString());
                    UserInfo.setval(entry.getKey(),entry.getValue().toString());
                    Log.i(TAG,entry.getKey().getClass().toString());

                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url=NetUtil.urlBase +"/api/user/get_image";
                        boolean stringFromNet = false;
                        try {
                            stringFromNet=NetUtil.isLoad(url,UserInfo.uid);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(stringFromNet==true)
                        {
                            url=NetUtil.urlBase +"/api/user/get_image";
                            stringFromNet=NetUtil.download(url,UserInfo.uid);
                        }
                        Message message=new Message();
                        message.what=7;
                        message.obj=stringFromNet;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
            if (msg.what == 7){
                boolean isok = (boolean) msg.obj;
                String account = etAccount.getText().toString();
                if(isok==true)
                {
                    Toast.makeText(LoginActivity.this, "恭喜你，登录成功！", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                    intent.putExtra("account", account);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "没有上传照片，请上传照片！", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("登录");

        initView();

        initData();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString();
                String password = etPassword.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String,Object>map=new HashMap<>();
                        map.put("username", account);
                        map.put("password", password);
                        String url=NetUtil.urlBase +"/api/user/login";
                        String stringFromNet = null;
                        try {
                            stringFromNet=NetUtil.doPost(url, map,false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message message=new Message();
                        message.what=0;
                        message.obj=stringFromNet;
                        mHandler.sendMessage(message);
                    }
                }).start();
                Log.d(TAG, "onClick: -------------" + account);
                Log.d(TAG, "password: -------------" + password);
            }
        });

        cbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbRemember.setChecked(true);
                }
            }
        });

        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    cbAutoLogin.setChecked(false);
                }
            }
        });

    }



    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
        cbAutoLogin = findViewById(R.id.cb_auto_login);

    }

    private void initData() {
        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
        boolean isRemember = spf.getBoolean("isRemember", false);
        boolean isLogin = spf.getBoolean("isLogin", false);
        String account = spf.getString("account", "");
        String password = spf.getString("password", "");



        userName = account;
        pass = password;

        if (isRemember) {
            etAccount.setText(account);
            etPassword.setText(password);
            cbRemember.setChecked(true);
        }


    }

    public void toRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);

        startActivityForResult(intent, REQUEST_CODE_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == RegisterActivity.RESULT_CODE_REGISTER && data != null) {
            Bundle extras = data.getExtras();

            String account = extras.getString("account", "");
            String password = extras.getString("password", "");

            etAccount.setText(account);
            etPassword.setText(password);

            userName = account;
            pass = password;
        }
    }
}