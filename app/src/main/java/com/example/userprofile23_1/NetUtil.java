package com.example.userprofile23_1;

import static android.provider.Telephony.Mms.Part.CHARSET;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class NetUtil {
    private static final String TAG = "NET";
    public static String cookie=null;
    public static String urlBase ="https://txy.yutong.site:6002";
    public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height,matrix, true);
        return bitmap;

    }
    public static boolean doGet_pd(String url){
        Log.i(TAG,"url="+url);
        String result="";
        BufferedReader reader=null;
        String bookJSONString=null;
        try {
            HttpURLConnection httpURLConnection=null;
            URL requestUrl=new URL(url);
            httpURLConnection=(HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            if ("https".equalsIgnoreCase(requestUrl.getProtocol())){
                ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(SSLSocketClient.getSSLSocketFactory());
                ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(SSLSocketClient.getHostnameVerifier());
            }
            if(cookie!=null)
            {
                httpURLConnection.setRequestProperty("Cookie", cookie);
            }
            httpURLConnection.connect();

            int code=httpURLConnection.getResponseCode();
            Log.i(TAG, "---------------code is"+String.valueOf(code));
            if(code!=200)
                return false;
            else
                return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public static boolean doGet(String url){
        String result="";
        BufferedReader reader=null;
        String bookJSONString=null;
        try {
            HttpURLConnection httpURLConnection=null;
            URL requestUrl=new URL(url);
            httpURLConnection=(HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            if ("https".equalsIgnoreCase(requestUrl.getProtocol())){
                ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(SSLSocketClient.getSSLSocketFactory());
                ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(SSLSocketClient.getHostnameVerifier());
            }
            if(cookie!=null)
            {
                httpURLConnection.setRequestProperty("Cookie", cookie);
            }
            httpURLConnection.connect();

            int code=httpURLConnection.getResponseCode();
            Log.i(TAG, String.valueOf(code));
            if(code==200)
            {
                InputStream inputStream= httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap b = zoomImage(bitmap,300,300);
                String imageToBase64 = ImageUtil.imageToBase64(b);
                UserInfo.imageBase64=imageToBase64;
                return true;
            }
            else return false;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean download(String url,String id){
        return doGet(url+ "?uid=" +id);
    }
    public static boolean isLoad(String url,String id){
        return doGet_pd(url+ "?uid=" +id);
    }

    public static  String doPost(String urlStr, Map<String,Object>paramMap,boolean isreg){
        HttpURLConnection urlConnection=null;
        OutputStream outputStream=null;
        String result=null;
        BufferedReader reader=null;
        try {
            URL url=new URL(urlStr);
            urlConnection= (HttpURLConnection) url.openConnection();
//            object.put(, )
            String TAG = "LoginActivity";
            String paramData=JsonMap.getjson(paramMap);
            Log.i(TAG, "-----------"+paramData);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(10*1000);
            if ("https".equalsIgnoreCase(url.getProtocol())){
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(SSLSocketClient.getSSLSocketFactory());
                ((HttpsURLConnection) urlConnection).setHostnameVerifier(SSLSocketClient.getHostnameVerifier());
            }
            if(cookie!=null)
            {
                urlConnection.setRequestProperty("Cookie", cookie);
            }
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            outputStream= urlConnection.getOutputStream();
            outputStream.write(paramData.getBytes());
            int code=urlConnection.getResponseCode();
            Log.i(TAG, "-----------"+code);
            if(code==200){
                Map<String, Object> map = new ArrayMap<>();
                String cookieVal=null;
                if(isreg==false)
                {
                    cookieVal = urlConnection.getHeaderField("Set-Cookie").split(";")[0];
                    Log.i(TAG, "-----------"+cookieVal);
                }
                InputStream inputStream= urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder builder=new StringBuilder();
                while ((line=reader.readLine())!=null)
                {
                    builder.append(line);
                    builder.append("\n");
                }
                if(builder.length()==0)
                {
                    return null;
                }
                result=builder.toString();
                map = JsonMap.getMap(result);
                if(cookieVal!=null)
                    map.put("Cookie",cookieVal);
                result=JsonMap.getjson(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(outputStream!=null)
            {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static final String BOUNDARY = "FlPm4LpSXsE" ; //UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
    private static final String PREFIX="--";
    private static final String LINE_END="\r\n";
    private static final String CHARSET = "utf-8";
    public static  String doPostImg(String urlStr, File file){
        HttpURLConnection urlConnection=null;
        OutputStream outputStream=null;
        String result=null;
        BufferedReader reader=null;
        try {
            URL url=new URL(urlStr);
            urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(500*1000);
            if ("https".equalsIgnoreCase(url.getProtocol())){
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(SSLSocketClient.getSSLSocketFactory());
                ((HttpsURLConnection) urlConnection).setHostnameVerifier(SSLSocketClient.getHostnameVerifier());
            }
            if(cookie!=null)
            {
                urlConnection.setRequestProperty("Cookie", cookie);
            }
//            urlConnection.setRequestProperty("Charset", CHARSET);
            urlConnection.setRequestProperty("Content-Type",  "multipart/form-data" + "; boundary=" + BOUNDARY);
            urlConnection.setDoOutput(true);
            outputStream= urlConnection.getOutputStream();
            if(file!=null)
            {
                outputStream=urlConnection.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputStream);

                StringBuffer sb = new StringBuffer();
                String PRE_FIX = ("\r\n--" + BOUNDARY + "\r\n");
                String END_FIX = ("\r\n--" + BOUNDARY + "--\r\n");
                sb.append(PRE_FIX);
                Log.i(TAG, "Content-Disposition: form-data; name=\"data\"; filename=" + "\"" + file.getName() + "\"" + LINE_END);
                Log.i(TAG, "Content-Type: image/jpeg"+LINE_END);
                sb.append("Content-Disposition: form-data; name=\"data\"; filename=" + "\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: image/jpeg"+LINE_END);
                sb.append(LINE_END);

                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1)
                {
                    dos.write(bytes, 0, len);
                }
                is.close();

                byte[] end_data = (END_FIX).getBytes();
                dos.write(end_data);
                dos.flush();

            }

            int code=urlConnection.getResponseCode();

            String TAG = "MainActivity";
            Log.i(TAG, String.valueOf(code));
            if(code==200){
                Log.i(TAG, "sucessful！！！");
                InputStream inputStream= urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder builder=new StringBuilder();
                while ((line=reader.readLine())!=null)
                {
                    builder.append(line);
                    builder.append("\n");
                }
                if(builder.length()==0)
                {
                    return null;
                }
                result=builder.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(outputStream!=null)
            {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static  String doPost1(String urlStr){
        HttpURLConnection urlConnection=null;
        OutputStream outputStream=null;
        JSONObject object =new JSONObject();
        String result=null;
        BufferedReader reader=null;
        try {
            URL url=new URL(urlStr);
            urlConnection= (HttpURLConnection) url.openConnection();

//            object.put(, )
            String TAG = "LoginActivity";

            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(10*1000);
            if ("https".equalsIgnoreCase(url.getProtocol())){
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(SSLSocketClient.getSSLSocketFactory());
                ((HttpsURLConnection) urlConnection).setHostnameVerifier(SSLSocketClient.getHostnameVerifier());
            }
            if(cookie!=null)
            {
                Log.i(TAG, "-----------"+cookie);
                urlConnection.setRequestProperty("Cookie", cookie);
            }
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            outputStream= urlConnection.getOutputStream();
            int code=urlConnection.getResponseCode();
            Log.i(TAG, "-----------"+code);
            if(code==200){
                Log.i(TAG, "23333333");
                InputStream inputStream= urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder builder=new StringBuilder();
                while ((line=reader.readLine())!=null)
                {
                    builder.append(line);
                    builder.append("\n");
                }
                if(builder.length()==0)
                {
                    return null;
                }
                result=builder.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(outputStream!=null)
            {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


}
