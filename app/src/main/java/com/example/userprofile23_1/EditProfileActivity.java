package com.example.userprofile23_1;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    public static final int REQUEST_CODE_TAKE = 1;
    public static final int REQUEST_CODE_CHOOSE = 0;
    private ImageView ivAvatar;
    private Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initView();
        initData();
    }

    private void initView() {
        ivAvatar = findViewById(R.id.iv_avatar);
    }

    private void initData() {
        if(UserInfo.imageBase64!=null)
        {
            ivAvatar.setImageBitmap(ImageUtil.base64ToImage(UserInfo.imageBase64));
            Log.i(TAG, "NNNN");
        }
    }

    public void save(View view) {
        if(UserInfo.imageBase64!=null)
        {
            Intent intent = new Intent(this, MainActivity2.class);
            startActivity(intent);
            this.finish();
        }
        else {
            Toast.makeText(EditProfileActivity.this, "还没有上传图片！", Toast.LENGTH_LONG).show();
        }
    }
    private Handler mHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==3){
                String str= (String) msg.obj;
                Log.i(TAG, "-----------"+str);
                Map<String, Object> map = new ArrayMap<>();
                map = JsonMap.getMap(str);
                Log.d(TAG, "onClick: -------------" + str);
                Log.d(TAG, "onClick: -------------" + map.get("ok"));
                boolean x= (boolean) map.get("ok");
                if(x==true){
                    Toast.makeText(EditProfileActivity.this, "上传成功！", Toast.LENGTH_LONG).show();
                }
//                textView.setText(str);
            }
        }
    };


    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 真正的执行去拍照
            doTake();
        } else {
            // 去申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override//接收权限申请结果
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doTake();
            } else {
                Toast.makeText(this, "你没有获得摄像头权限~", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else {
                Toast.makeText(this, "你没有获得访问相册的权限~", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void doTake() {
        File imageTemp = new File(getExternalCacheDir(), "imageOut.jpeg");
        if (imageTemp.exists()) {
            imageTemp.delete();
        }
        try {
            imageTemp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT > 24) {
            // contentProvider
            imageUri = FileProvider.getUriForFile(this, "com.example.userprofile23_1.fileprovider", imageTemp);
        } else {
            imageUri = Uri.fromFile(imageTemp);
        }
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_TAKE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE) {
            if (resultCode == RESULT_OK) {
                // 获取拍摄的照片
                try {
                    File imageTemp = new File(getExternalCacheDir(), "imageOut.jpeg");
                    Log.i(TAG, "-----------"+imageTemp.exists());
                    Log.i(TAG, "-----------"+imageTemp.length());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url=NetUtil.urlBase +"/api/user/upload_image";
                            String stringFromNet = null;
                            try {
                                stringFromNet=NetUtil.doPostImg(url,imageTemp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message=new Message();
                            message.what=3;
                            message.obj=stringFromNet;
                            mHandler.sendMessage(message);
                        }
                    }).start();
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Bitmap b = NetUtil.zoomImage(bitmap,300,300);
                    ivAvatar.setImageBitmap(b);
                    String imageToBase64 = ImageUtil.imageToBase64(b);
                    UserInfo.imageBase64 = imageToBase64;
                } catch (FileNotFoundException e) {

                }
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE) {

            if (Build.VERSION.SDK_INT < 19) {
                handleImageBeforeApi19(data);
            } else {
                handleImageOnApi19(data);
            }

        }
    }

    private void handleImageBeforeApi19(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @TargetApi(19)
    private void handleImageOnApi19(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);

            if (TextUtils.equals(uri.getAuthority(), "com.android.providers.media.documents")) {
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if (TextUtils.equals(uri.getAuthority(), "com.android.providers.downloads.documents")) {
                if (documentId != null && documentId.startsWith("msf:")) {
                    resolveMSFContent(uri, documentId);
                    return;
                }
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                imagePath = getImagePath(contentUri, null);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }

        displayImage(imagePath);
    }

    private void resolveMSFContent(Uri uri, String documentId) {

        File file = new File(getCacheDir(), "temp_file" + getContentResolver().getType(uri).split("/")[1]);

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Bitmap b = NetUtil.zoomImage(bitmap,300,300);
            ivAvatar.setImageBitmap(b);
            UserInfo.imageBase64 = ImageUtil.imageToBase64(b);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        Log.d(TAG, "displayImage: ------------" + imagePath);
        if (imagePath != null) {
            File imageTemp = new File(getExternalCacheDir(), "imageOut.jpeg");
            Log.i(TAG, "-----------"+imageTemp.exists());
            Log.i(TAG, "-----------"+imageTemp.length());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String url=NetUtil.urlBase +"/api/user/upload_image";
                    String stringFromNet = null;
                    try {
                        stringFromNet=NetUtil.doPostImg(url,imageTemp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message=new Message();
                    message.what=3;
                    message.obj=stringFromNet;
                    mHandler.sendMessage(message);
                }
            }).start();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap b = NetUtil.zoomImage(bitmap,300,300);
            ivAvatar.setImageBitmap(b);
            String imageToBase64 = ImageUtil.imageToBase64(b);
            UserInfo.imageBase64 = imageToBase64;
        }
    }


    public void choosePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 真正的去打开相册
            openAlbum();
        } else {
            // 去申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }


}