package com.example.weahen.wstest.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.MyConstant;
import com.example.weahen.wstest.Utils.NetworkUtility;
import com.example.weahen.wstest.Utils.SharedPreferencesUtil;
import com.example.weahen.wstest.widget.CircleImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class PersonalizedActivity extends BaseActivity {
    private static final String TAG = "PersonalizedActivity";
    private Context mContext;
    private AlertDialog profilePictureDialog;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_PERMISSION_CAMERA = 0x001;
    private static final int REQUEST_PERMISSION_WRITE = 0x002;
    private static final int CROP_REQUEST_CODE = 0x003;
    private ImageView ivAvatar;
    private EditText etNick;
    private String uid;
    String imageChangeNotStored;
    int int_imageChangeNotStored;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    imageChangeNotStored = data.getStringExtra("imageChangeNotStored");
                    Log.e("Tag","从头像选择中传回来的头像id"+imageChangeNotStored);
                    int_imageChangeNotStored = Integer.parseInt(imageChangeNotStored);
                }
                break;
                default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalized);
        Log.e("Tag", TAG + "onCreate");
        initTitle("个性化设置");
        initUid();
        //初始化我的头像
        initImage();
        //初始化我的昵称
        initName();

        mContext = this;
        Button btnchangeheadimage = findViewById(R.id.btn_chooseHeadImage);
        btnchangeheadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PersonalizedActivity.this, ChangeHeadImageActivity.class);
                startActivityForResult(intent1, 1);
            }
        });

        Button btnOk = findViewById(R.id.sure);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //       SharedPreferencesUtil sp = new SharedPreferencesUtil(PersonalizedActivity.this.getApplicationContext());
                //      sp.writeData("nick", etNick.getText().toString());
                SharedPreferences.Editor editor = getSharedPreferences("id", MODE_PRIVATE).edit();
                SharedPreferences.Editor editorImage = getSharedPreferences("MyChange", MODE_PRIVATE).edit();
                int inputName = etNick.getText().toString().trim().length();
                //当只修改昵称
                if (inputName > 0 && int_imageChangeNotStored == 0) {
                    editor.putString("id", etNick.getText().toString().trim());
                    editor.apply();
                    Toast.makeText(PersonalizedActivity.this, R.string.btn_nameSave_ok, Toast.LENGTH_SHORT).show();
                    finish();
                }
                //只修改头像
                else if (inputName == 0 && int_imageChangeNotStored != 0) {
                    editorImage.putString("MyChange", imageChangeNotStored);
                    editorImage.apply();
                    Toast.makeText(PersonalizedActivity.this, R.string.btn_imageSave_ok, Toast.LENGTH_SHORT).show();
                    finish();
                }
                //昵称头像同时修改
                else if (inputName > 0 && int_imageChangeNotStored != 0) {
                    editor.putString("id", etNick.getText().toString().trim());
                    editor.apply();
                    editorImage.putString("MyChange", imageChangeNotStored);
                    editorImage.apply();
                    Toast.makeText(PersonalizedActivity.this, R.string.btn_save_ok, Toast.LENGTH_SHORT).show();
                    finish();
                }
                //什么都不做
                else {
                    finish();
                }
//                if (inputName== 0 || etNick.getText().toString().trim().length() >= 10) {
//                    Log.e("Tag1", "personalization 输入框的输入  " + etNick.getText().toString());
//                    etNick.setText("");
//                    Toast.makeText(PersonalizedActivity.this, R.string.name_cant_empty, Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.e("Tag1", "personalization 输入框的输入  " + etNick.getText().toString());
//                    editor.putString("id", etNick.getText().toString().trim());
//                    editor.apply();
//                    initName();
//                    Toast.makeText(PersonalizedActivity.this, R.string.btn_save_ok, Toast.LENGTH_SHORT).show();
//                    finish();
//                }
            }
        });


    }

    private void initImage() {
        CircleImageView myHeadImage = findViewById(R.id.myHeadImage);
        SharedPreferences myimage = getSharedPreferences("MyChange", MODE_PRIVATE);
        String imageinit = myimage.getString("MyChange", "2131230974");
//        int i = myimage.getInt("MyChange", 2131230974);
        int i = Integer.parseInt(imageinit);
        myHeadImage.setImageResource(i);
    }

    private void initName() {
        etNick = findViewById(R.id.etNick);
        TextView myName = findViewById(R.id.myTextViewOfName);
        SharedPreferences preName = getSharedPreferences("id", MODE_PRIVATE);
        String t = preName.getString("id", uid.substring(0, 5));
        myName.setText(t);
    }


    private void initUid() {         //本机IMEI
        String MYIMEI = NetworkUtility.getIMEI(PersonalizedActivity.this);
        try {
            uid = shaEncode(MYIMEI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //对imei用SHA算法加密
    public static String shaEncode(String inStr) throws Exception {

        MessageDigest sha = null;

        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {

            System.out.println(e.toString());

            e.printStackTrace();

            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {

            int val = ((int) md5Bytes[i]) & 0xff;

            if (val < 16) {

                hexValue.append("0");
            }

            hexValue.append(Integer.toHexString(val));

        }

        return hexValue.toString();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Tag", TAG + "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Tag", TAG + "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.e("Tag", TAG + "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initImage();
        Log.e("Tag", TAG + "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Tag", TAG + "onPause");
    }
}
