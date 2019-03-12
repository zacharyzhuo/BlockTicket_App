package com.example.asus.blockchain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {
    private EditText edId, edPsw, edRePsw;
    private Button btnSubmit,btnCancel;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);


        //隱藏通知欄
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        checkLogin();

        //getSupportActionBar().hide();
        setTitle("註冊");
        edId = (EditText)findViewById(R.id.edId);
        edPsw = (EditText)findViewById(R.id.edPsw);
        edRePsw = (EditText)findViewById(R.id.edRePsw);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnSubmit.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                register(edId.getText().toString(), edPsw.getText().toString(), edRePsw.getText().toString());
            }
        });
        btnCancel.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this,LoginPage.class);
                startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkLogin() {
        new NetworkClient(this).post("/mobileCheckNotLogin", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
//                    String message = errorResponse.getString("message");
//                    Toast.makeText(RegisterActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    intent.setClass(RegisterActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    RegisterActivity.this.finish();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String jsonMsg = jsonObject.getString("message");
                    Toast.makeText(RegisterActivity.this, jsonMsg.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void register(String name, String password, String rePassword) {
        RequestParams params = new RequestParams();
        params.put("name",name);
        params.put("password",password);
        params.put("repassword",rePassword);

        new NetworkClient(this).post("/student/signup", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Toast.makeText(RegisterActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, LoginPage.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    String message = errorResponse.getString("message");
                    Toast.makeText(RegisterActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
//                try {
//                    JSONObject jsonObject = new JSONObject(message);
//                    String jsonMsg = jsonObject.getString("message");
//                    Toast.makeText(RegisterActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
        });
    }
}