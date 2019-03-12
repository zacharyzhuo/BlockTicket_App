package com.example.asus.blockchain;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class Information extends AppCompatActivity {

    public TextView txtTicketMsg, txtLine;
    public ImageView imgQRcode;
    PrivateKey RSA_PRIVATEKEY;

    @TargetApi(Build.VERSION_CODES.O)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_layout);

//        //隱藏標題列
//        getSupportActionBar().hide();
//        //隱藏通知欄
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        txtTicketMsg = (TextView) findViewById(R.id.txtTicketMsg);
        txtLine = (TextView) findViewById(R.id.txtLine);

        Toolbar tb = (Toolbar) findViewById(R.id.program_toolbar);
        tb.setTitle(" 我的餐券");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        ImageSpan mImageSpan = new ImageSpan(Information.this, R.drawable.cutlery);
        ImageSpan mImageSpan1 = new ImageSpan(Information.this, R.drawable.fruit);
        ImageSpan mImageSpan2 = new ImageSpan(Information.this, R.drawable.salad);
        SpannableString mSpannableString = new SpannableString("\n\n" + "-------------------------------------- " + "\n\n");
        mSpannableString.setSpan(mImageSpan1, 20, 21, 0);
        mSpannableString.setSpan(mImageSpan, 21, 22, 0);
        mSpannableString.setSpan(mImageSpan2, 22, 23, 0);
        //txtTicketMsg.setGravity(Gravity.CENTER);
        txtLine.setText(mSpannableString);
        txtLine.setGravity(Gravity.CENTER);

        Bundle bundle = Information.this.getIntent().getExtras();
        String ticketId = bundle.getString("TicketId");

        // generating RSA public and private keys
        try {
            StudentInfo stuInfo = new StudentInfo();
            KeyPair keyPair = DigitalSignature.generateRSAKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            String publicKeyStr = new String(Base64.getEncoder().encode(publicKey.getEncoded()), "UTF-8");
            String publicKeyPem = DigitalSignature.convertPublicKeyToPEM(publicKeyStr);  // Can be used by Node.js's "crypto" module
            Log.e("公鑰", publicKeyPem);
            RSA_PRIVATEKEY = privateKey;
            Log.e("私鑰", RSA_PRIVATEKEY.toString());

            myTickets(ticketId, publicKeyPem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //====================================================================================================================
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.setClass(Information.this,MainActivity.class);
            startActivity(intent);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //====================================================================================================================

    //====================================================================================================================
    //顯示該張餐券並對他簽章(server)
    public void myTickets(final String ticketKey, String publicKey) {
        RequestParams params = new RequestParams();
        params.put("publicKey", publicKey);

        new NetworkClient(this).post("/student/createQrcode?ticketId=" + ticketKey, params, new JsonHttpResponseHandler() {
            @TargetApi(Build.VERSION_CODES.O)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    String encrypted = jsonArray.getString(0);
                    Log.e("學生RSA公鑰加密訊息", encrypted);
                    byte[] encryptedBuf = Base64.getDecoder().decode(encrypted.getBytes("UTF-8"));
                    Log.e("encryptedBuf", encryptedBuf.toString());
                    // RSA decryption with private key
                    byte[] decryptedBuf = DigitalSignature.rsaDecryptPrivate(RSA_PRIVATEKEY, encryptedBuf);
                    Log.e("decryptedBuf", decryptedBuf.toString());
                    String decrypted = new String(decryptedBuf, "UTF-8");
                    Log.e("解密訊息", decrypted);

                    String[] ticketInfoArr = decrypted.split("%");
                    String ticketMsg = ticketInfoArr[0];
                    String owner = ticketInfoArr[1];
                    String issuedDate = ticketInfoArr[2];
                    String expDate = ticketInfoArr[3];
                    String schSign = jsonArray.getString(1);

                    StudentInfo stuInfo = new StudentInfo();

                    //讀私鑰==================================
                    FileInputStream inStream = null;
                    String signature = "";
                    try {
                        File f = new File(Environment.getExternalStorageDirectory(), "/ECPrivateKey.txt");
                        inStream = new FileInputStream(f);
                        ObjectInputStream objectInStream = new ObjectInputStream(inStream);
                        PrivateKey EC_PTIVATEKEY = (PrivateKey) objectInStream.readObject();
                        objectInStream.close();
                        Log.e("成功抓出私鑰", EC_PTIVATEKEY.toString());

                        // digital signature algorithm
                        final String algorithm = "SHA256withECDSA";

                        // the message to be protected
                        Log.e("餐券訊息", ticketMsg);
                        byte[] msgBuf = ticketMsg.getBytes("UTF-8");

                        // digitally sign the message
                        byte[] signatureBuf = DigitalSignature.sign(msgBuf, EC_PTIVATEKEY, algorithm);
                        signature = new String(signatureBuf, "UTF-8");  // Can be used by Node.js's "crypto" module
                        Log.e("學生簽章", signature);

                        //------------------------------------------------------------------
                        String QRcodeContent = ticketKey + "$" + ticketMsg + "$" + schSign + "$" + signature;
                        Log.e("GG", QRcodeContent);
                        int QRCodeWidth = 800;
                        int QRCodeHeight = 800;

                        //QRCode內容編碼
                        Map hints = new EnumMap(EncodeHintType.class);
                        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

                        MultiFormatWriter writer = new MultiFormatWriter();
                        //ErrorCorrectionLevel容錯率分四級：L(7%) M(15%) Q(25%) H(30%)
                        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//使用小写的编码，大写会出现]Q2\000026开头内容
                        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

                        //建立QRCode的資料矩陣
                        BitMatrix result = writer.encode(QRcodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);

                        //建立矩陣圖
                        Bitmap bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_4444);
                        for (int y = 0; y < QRCodeHeight; y++) {
                            for (int x = 0; x < QRCodeWidth; x++) {
                                bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        //設定給xml
                        imgQRcode = (ImageView) findViewById(R.id.imgQRcode);
                        imgQRcode.setImageBitmap(bitmap);

                        String txtSchSign = schSign.substring(0, 15) + "...";
                        String txtStuSign = signature.substring(0, 15) + "...";
                        String txtTicket = "餐券編號 : " + ticketKey + "\n" +
                                "學生代號 : " + owner + "\n" +
                                "發放日期 : " + issuedDate + "\n" +
                                "有效日期 : " + expDate + "\n" +
                                "學校簽章 : " + txtSchSign + "\n" +
                                "學生簽章 : " + txtStuSign;
                        Log.e("餐券介面文字", txtTicket);
                        txtTicketMsg.setText(txtTicket);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
//                    String message = errorResponse.getString("message");
                Log.e("gg", errorResponse.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
//                try {
//                    JSONObject jsonObject = new JSONObject(message);
//                    String jsonMsg = jsonObject.getString("message");
                Log.e("gg", message);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
    //====================================================================================================================
}
