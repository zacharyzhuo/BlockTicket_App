package com.example.asus.blockchain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


public class LoginPage extends AppCompatActivity {

    private Button btnLogin,btnreg;
    private EditText edId, edPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        checkLogin();


        //隱藏標題列
        //getSupportActionBar().hide();
        //隱藏通知欄
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        edId = (EditText) findViewById(R.id.edId);
        edPassword = (EditText) findViewById(R.id.edPsw);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Login(edId.getText().toString(), edPassword.getText().toString());
            }
        });
        btnreg = (Button)findViewById(R.id.btnreg);
        btnreg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginPage.this,RegisterActivity.class);
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
        new NetworkClient(this).post("/mobileCheckLogin", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    Log.e("gg", response.toString());
                    String message = response.getString("message");
                    Toast.makeText(LoginPage.this, message.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(LoginPage.this, MainActivity.class);
                    intent.putExtra("id",1);
                    startActivity(intent);
                    LoginPage.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    Log.e("gg", errorResponse.toString());
                    String message = errorResponse.getString("message");
                    Toast.makeText(LoginPage.this, message.toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void Login(String name, String password) {
        RequestParams params = new RequestParams();
        params.put("name",name);
        params.put("password",password);

        new NetworkClient(this).post("/student/signin", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Toast.makeText(LoginPage.this, "登入成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginPage.this, MainActivity.class);
                startActivity(intent);
                LoginPage.this.finish();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    String message = errorResponse.getString("message");
                    Toast.makeText(LoginPage.this, message.toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String jsonMsg = jsonObject.getString("message");
                    Toast.makeText(LoginPage.this, jsonMsg.toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
