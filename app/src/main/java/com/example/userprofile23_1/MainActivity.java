package com.example.userprofile23_1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Data {

    private static final String TAG = "time:";
    private long timestamp;
    private String location;
    private Double temperature;
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Double getTemperature() {
        return temperature;
    }
    public void setAge(Double temperature) {
        this.temperature = temperature;
    }
    public Data(int timestamp, String location, Double temperature) {
        super();
        this.timestamp = timestamp;
        this.location = location;
        this.temperature = temperature;
    }
    public Data() {
        super();
    }
    @Override
    public String toString() {
        Log.i(TAG, String.valueOf(timestamp));
        String sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp * 1000));
        return "时间：" + sd +"\r\n"+ " 地点：" + location + "\r\n"+" 温度：" + temperature +"\r\n";
    }
}

class Grade {

    private  boolean isok;
    private ArrayList<Data> data;
    public Grade(boolean isok, ArrayList<Data> data) {
        super();
        this.isok = isok;
        this.data = data;
    }
    public Grade() {
        super();
    }
    public boolean getIsok() {
        return isok;
    }
    public void setIsok(boolean isok) {
        this.isok = isok;
    }
    public ArrayList<Data> getData() {
        return data;
    }
    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

}

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private List<String> mStringList;
    private ArrayAdapter mArrayAdapter;

    private String TAG="MainActivity";
    private Handler mHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==9){
                String str= (String) msg.obj;
                Log.i(TAG, "-----------"+str);
                try {
                    JSONObject jObject1=new JSONObject(str);
                    Grade grade=new Grade();
                    grade.setIsok(jObject1.getBoolean("ok"));
                    ArrayList<Data> data=new ArrayList<>();
                    grade.setData(data);
                    JSONArray jsonArray2=jObject1.getJSONArray("data");
                    for(int i=0;i<jsonArray2.length();i++){
                        //解析第三层----对象
                        JSONObject jObject3=jsonArray2.getJSONObject(i);
                        Data student=new Data(jObject3.getInt("timestamp"), jObject3.getString("location"), jObject3.getDouble("temperature"));
                        grade.getData().add(student);
                    }
                    for(int i = 0;i < grade.getData().size(); i ++)
                    {
                        mStringList.add(String.valueOf(grade.getData().get(i)));
                        Log.i(TAG, String.valueOf(grade.getData().get(i)));
                    }
                    mArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, mStringList);
                    mListView.setAdapter(mArrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.lv);
        mStringList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,String> map=new HashMap<>();
                String url=NetUtil.urlBase +"/api/user/query";
                String stringFromNet = null;
                try {
                    stringFromNet=NetUtil.doPost1(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message message=new Message();
                message.what=9;
                message.obj=stringFromNet;
                mHandler.sendMessage(message);
            }
        }).start();

    }

    public void logout(View view) {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
        this.finish();
    }
}