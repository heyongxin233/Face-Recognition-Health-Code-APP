package com.example.userprofile23_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.userprofile23_1.databinding.ActivityMain2Binding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    private TextView name,id,tv_show_time;
    private String TAG="MainActivity2";
    private Handler mHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==4){
                SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
                Date curDate =  new Date(System.currentTimeMillis());
                tv_show_time.setText(formatter.format(curDate));
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
    }
    public void init()
    {
        name=findViewById(R.id.textView4);
        id=findViewById(R.id.textView5);
        tv_show_time=findViewById(R.id.textView6);
        char[] r=UserInfo.realname.toCharArray();
        char[] m=UserInfo.ID.toCharArray();
        for(int i=0; i<m.length-4;i++)
            m[i]='*';
        for(int i=0;i<r.length-1;i++)
            r[i]='*';
        name.setText(String.valueOf(r));
        id.setText(String.valueOf(m));
        new Thread(new Runnable() {
            @Override
            public void run() {
                do{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message=new Message();
                    message.what=4;
                    mHandler.sendMessage(message);
                }while (true);

            }
        }).start();
    }
    public void getJourney(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void setUpload(View view){
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
    public void logout(View view) {
        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
        SharedPreferences.Editor edit = spf.edit();
        edit.putBoolean("isLogin", false);
        edit.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}