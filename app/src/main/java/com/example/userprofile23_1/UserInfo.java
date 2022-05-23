package com.example.userprofile23_1;

import static android.content.ContentValues.TAG;

import android.util.Log;

public class UserInfo {
    public static String uid=null;
    public static String phone=null;
    public static String ID=null;
    public static String username=null;
    public static String realname=null;
    public static String imageBase64=null;
    public static void setval(String a,String b)
    {
        Log.i(TAG, a);
        Log.i(TAG, String.valueOf(b));
        if(a.equals("uid")){
            uid=String.valueOf(b);
        }
        if(a.equals("phone")){
            phone=b;
        }
        if(a.equals("ID")){
            ID=b;
        }
        if(a.equals("username")){
            username=b;
        }
        if(a.equals("realname")){
            realname=b;
        }
    }
}
