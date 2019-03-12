package com.example.asus.blockchain;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import cz.msebera.android.httpclient.Header;

public class Fragment_Apply extends Fragment{

    private static final String TAG = "ApplyTicket";

    EditText edId;
    EditText edName;
    TextView txtPubKey;
    Button btnCreateKey;
    Button btnSubmit;
    ImageView ivCard;
    ImageView ivProve;

    private DisplayMetrics mPhone;
    private final static int CAMERA = 66 ;
    private final static int PHOTO = 99 ;
    /**
     * Called when the activity is first created.
     */

    String clickWhich = "";
    Uri cardPath ;
    Uri provePath ;

    PrivateKey PRIVATEKEY;

    ProgressDialog progress;
    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;



        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//unfocus

        checkLogin();

        //讀取手機解析度
        mPhone = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(mPhone);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_applyticket, null);
        View view = inflater.inflate(R.layout.fragment_applyticket,container,false);

        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.program_toolbar);
        tb.setTitle(" 申請餐券");

        edId = (EditText) view.findViewById(R.id.edId);
        edName = (EditText) view.findViewById(R.id.edName);
        txtPubKey = (TextView) view.findViewById(R.id.txtPubKey);
        btnCreateKey = (Button) view.findViewById(R.id.btnCreateKey);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        ivCard = (ImageView) view.findViewById(R.id.ivCard);
        ivProve = (ImageView) view.findViewById(R.id.ivProve);

        ivCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final CharSequence[] items = {"相簿", "拍照"};

                AlertDialog dlg = new AlertDialog.Builder(mActivity).setTitle("選擇照片").setItems(items,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                if (which == 1) { //拍照
                                    ContentValues value = new ContentValues();
                                    value.put(MediaStore.Audio.Media.MIME_TYPE, "image/jpeg");
                                    Uri uri= mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            value);
                                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                    clickWhich = "card";
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri.getPath());
                                    startActivityForResult(intent, CAMERA);
                                } else { //相簿
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    clickWhich = "card";
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, PHOTO);
                                }
                            }
                        }).create();
                dlg.show();
            }
        });
        ivProve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final CharSequence[] items = {"相簿", "拍照"};

                AlertDialog dlg = new AlertDialog.Builder(mActivity).setTitle("選擇照片").setItems(items,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                if (which == 1) { //拍照
                                    ContentValues value = new ContentValues();
                                    value.put(MediaStore.Audio.Media.MIME_TYPE, "image/jpeg");
                                    Uri uri= mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            value);
                                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                    clickWhich = "prove";
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri.getPath());
                                    startActivityForResult(intent, CAMERA);
                                } else { //相簿
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    clickWhich = "prove";
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, PHOTO);
                                }
                            }
                        }).create();
                dlg.show();
            }
        });

        btnCreateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createKey();
            }
        });

        final ProgressDialog progress = new ProgressDialog(mActivity);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply(edId.getText().toString(), edName.getText().toString(),
                        cardPath, provePath, txtPubKey.getText().toString());

                progress.setMessage("loading....");
                progress.setTitle("資料傳送中 , 請稍後");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.show();
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(5000);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        finally{
                            progress.dismiss();
                        }
                    }
                }).start();
            }
        });

     return view;
    }
    //====================================================================================================================
    //上傳證明文件
    //拍照完畢或選取圖片後呼叫此函式
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
        if ((requestCode == CAMERA || requestCode == PHOTO ) && data != null && clickWhich == "card")
        {
            //取得照片路徑uri
            Uri uri = data.getData();
            cardPath = uri;
            ContentResolver cr = mActivity.getContentResolver();
            try
            {
                //讀取照片，型態為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
                if(bitmap.getWidth()>bitmap.getHeight())CardScalePic(bitmap, mPhone.heightPixels);
                else CardScalePic(bitmap,mPhone.widthPixels);
            }
            catch (FileNotFoundException e)
            {
            }
        }

        else if ((requestCode == CAMERA || requestCode == PHOTO ) && data != null && clickWhich == "prove")
        {
            //取得照片路徑uri
            Uri uri = data.getData();
            provePath = uri;
            ContentResolver cr = mActivity.getContentResolver();
            try
            {
                //讀取照片，型態為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));

                //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
                if(bitmap.getWidth()>bitmap.getHeight())proveScalePic(bitmap, mPhone.heightPixels);
                else proveScalePic(bitmap,mPhone.widthPixels);
            }
            catch (FileNotFoundException e)
            {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void CardScalePic(Bitmap bitmap,int phone)
    {
        //縮放比例預設為1
        float mScale = 1 ;

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if(bitmap.getWidth() > phone )
        {
            //判斷縮放比例
            mScale = (float)phone/(float)bitmap.getWidth();

            Matrix mMat = new Matrix() ;
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);
            ivCard.setImageBitmap(mScaleBitmap);
        }
        else ivCard.setImageBitmap(bitmap);
    }
    private void proveScalePic(Bitmap bitmap,int phone)
    {
        //縮放比例預設為1
        float mScale = 1 ;

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if(bitmap.getWidth() > phone )
        {
            //判斷縮放比例
            mScale = (float)phone/(float)bitmap.getWidth();

            Matrix mMat = new Matrix() ;
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap,
                    0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);
            ivProve.setImageBitmap(mScaleBitmap);
        }
        else ivProve.setImageBitmap(bitmap);
    }
    //====================================================================================================================

    //====================================================================================================================
    //產生公鑰私鑰
    @TargetApi(Build.VERSION_CODES.O)
    public void createKey() {

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int REQUEST_CODE_CONTACT = 101;
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //验证是否许可权限
                for (String str : permissions) {
                    if (mActivity.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                        //申请权限
                        this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                        return;
                    }
                }
            }
            // generating EC public and private keys
            KeyPair keyPair = DigitalSignature.generateECKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            PRIVATEKEY = privateKey;
            Log.e("GG","私鑰: "+PRIVATEKEY.toString());

            // Convert to PEM key format
            // PEM (Privacy Enhanced Mail) is a Base64 encoded DER certificate.
            //String privateKeyStr = new String(Base64.getEncoder().encode(privateKey.getEncoded()), "UTF-8");
            String publicKeyStr = new String(Base64.getEncoder().encode(publicKey.getEncoded()), "UTF-8");
            //String privateKeyPem = convertPrivateKeyToPEM(privateKeyStr); // Can be used by Node.js's "crypto" module
            String publicKeyPem = DigitalSignature.convertPublicKeyToPEM(publicKeyStr);  // Can be used by Node.js's "crypto" module

            // Show the PEM files
            Log.e("gg","公鑰PEM: "+publicKeyPem);
            txtPubKey.setText(publicKeyPem);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //====================================================================================================================

    //====================================================================================================================
    //檢查登入狀態(server)
    public void checkLogin() {
        new NetworkClient(mActivity).post("/mobileCheckLogin", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }

            //            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
//                    String message = errorResponse.getString("message");
//                    Toast.makeText(ApplyTicket.this, message.toString(), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    intent.setClass(ApplyTicket.this, LoginPage.class);
//                    startActivity(intent);
//                    ApplyTicket.this.finish();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String jsonMsg = jsonObject.getString("message");
                    Toast.makeText(mActivity, jsonMsg.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(mActivity, LoginPage.class);
                    startActivity(intent);
                    mActivity.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //====================================================================================================================

    //====================================================================================================================
    //申請餐券(server)
    public void apply(final String stuId, String stuName, Uri cardPath, Uri provePath, String pubKey) {

        RequestParams params = new RequestParams();
        try {
            InputStream cardInputStream = mActivity.getContentResolver().openInputStream(cardPath);
            InputStream proveInputStream = mActivity.getContentResolver().openInputStream(provePath);
            params.put("stuId", stuId);
            params.put("stuName", stuName);
            params.put("card", cardInputStream, "card.jpg");
            params.put("prove", proveInputStream, "prove.jpg");
            params.put("pubKey", pubKey);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new NetworkClient(mActivity).post("/student/apply/submit", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                StudentInfo stuInfo = new StudentInfo();
                stuInfo.STUID = stuId;
                try {
                    FileOutputStream outStream = null;
                    try {
                        File f = new File(Environment.getExternalStorageDirectory(), "/ECPrivateKey.txt");
                        outStream = new FileOutputStream(f);
                        ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
                        objectOutStream.writeObject(PRIVATEKEY);
                        objectOutStream.close();
                        Log.e("GG", "已存入EC私鑰");
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    String message = response.getString("message");
                    Log.e("gg", message.toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setIcon(R.drawable.lo);
                    builder.setTitle("餐券申請狀態");
                    builder.setMessage("已提交申請");
                    builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(mActivity, "請至我的餐券查看申請狀態", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    //================================================================================
                    edId.setText(null);
                    edName.setText(null);
                    ivCard.setImageBitmap(null);
                    ivProve.setImageBitmap(null);
                    txtPubKey.setText(null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    String message = errorResponse.getString("message");
                    Log.e("gg", errorResponse.toString());
                    Toast.makeText(mActivity, message.toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
//                try {
//                    JSONObject jsonObject = new JSONObject(message);
//                    String jsonMsg = jsonObject.getString("message");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                Log.e("gg", message.toString());
                Toast.makeText(mActivity, message.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
