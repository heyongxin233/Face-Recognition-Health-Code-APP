package com.example.userprofile23_1;

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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RESULT_CODE_REGISTER = 0;
    private static final String TAG = "tag";
    private Button btnRegister;
    private EditText etAccount,etPass,etPassConfirm,id,real_name,phone_num;
    private CheckBox cbAgree;
    private Handler mHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                String str = (String) msg.obj;
                Map<String, Object> map = new ArrayMap<>();
                map = JsonMap.getMap(str);
                Log.d(TAG, "onClick: -------------" + str);
                Log.d(TAG, "onClick: -------------" + map.get("ok"));
                boolean x= (boolean) map.get("ok");
                if (x == false) {
                    Log.d(TAG, "onClick: -------------" + map.get("ok"));
                    Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_LONG).show();
                }
                else{
                    String name = etAccount.getText().toString();
                    String pass = etPass.getText().toString();
                    // 存储注册的用户名 密码
                    SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
                    SharedPreferences.Editor edit = spf.edit();
                    edit.putString("account", name);
                    edit.putString("password", pass);
                    edit.apply();

                    // 数据回传
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("account",name);
                    bundle.putString("password",pass);
                    intent.putExtras(bundle);
                    setResult(RESULT_CODE_REGISTER,intent);


                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
                    RegisterActivity.this.finish();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("注册");

        etAccount = findViewById(R.id.et_account);
        etPass = findViewById(R.id.et_password);
        etPassConfirm = findViewById(R.id.et_password_confirm);
        cbAgree = findViewById(R.id.cb_agree);
        btnRegister = findViewById(R.id.btn_register);
        id=findViewById(R.id.ID);
        real_name=findViewById(R.id.real_name);
        phone_num=findViewById(R.id.phone_num);
        btnRegister.setOnClickListener(this);

    }
    public static boolean isLegalName(String name){
        if (name.contains("·") || name.contains("•")){
            if (name.matches("^[\\u4e00-\\u9fa5]+[·•][\\u4e00-\\u9fa5]+$")){
                return true;
            }else {
                return false;
            }
        }else {
            if (name.matches("^[\\u4e00-\\u9fa5]+$")){
                return true;
            }else {
                return false;
            }
        }
    }
    public static boolean isLegalId(String id){
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")){
            return true;
        }else {
            return false;
        }
    }
    @Override
    public void onClick(View v) {
        String name = etAccount.getText().toString();
        String pass = etPass.getText().toString();
        String passConfirm = etPassConfirm.getText().toString();
        String rename=real_name.getText().toString();
        String ID_num=id.getText().toString();
        String phone=phone_num.getText().toString();
        String telRegex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        if(TextUtils.isEmpty(rename))
        {
            Toast.makeText(RegisterActivity.this, "真实姓名不正确", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(ID_num) && isLegalId(ID_num))
        {
            Toast.makeText(RegisterActivity.this, "身份证号码不正确", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(phone) && phone.matches(telRegex))
        {
            Toast.makeText(RegisterActivity.this, "电话号码不正确", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (!TextUtils.equals(pass,passConfirm)) {
            Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_LONG).show();
            return;
        }

        if (!cbAgree.isChecked()) {
            Toast.makeText(RegisterActivity.this, "请同意用户协议", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,Object> map=new HashMap<>();
                map.put("username", name);
                map.put("password", pass);
                map.put("realname", rename);
                map.put("phone", phone);
                map.put("ID", ID_num);
//                File file=new File("/document/image:33");
                String url=NetUtil.urlBase +"/api/user/register";
                String stringFromNet = null;
                try {
                    stringFromNet=NetUtil.doPost(url, map,true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message message=new Message();
                message.what=1;
                message.obj=stringFromNet;
                mHandler.sendMessage(message);
            }
        }).start();
        Toast.makeText(RegisterActivity.this, "正在注册...", Toast.LENGTH_LONG).show();
    }
}