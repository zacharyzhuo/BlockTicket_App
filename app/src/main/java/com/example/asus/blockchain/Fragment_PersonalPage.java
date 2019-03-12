package com.example.asus.blockchain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Fragment_PersonalPage extends Fragment{

    private static final String TAG = "PersonalPage";

    public TextView txtStuId;
    public TextView txtNotUsed;
    public TextView txtUsed;
    public Button btnlogout;

    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;

        personalInfo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal,container,false);

        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.program_toolbar);
        tb.setTitle(" 個人資訊");

        txtStuId = (TextView)view.findViewById(R.id.txtStuId);
        txtNotUsed = (TextView)view.findViewById(R.id.txtNotUsed);
        txtUsed = (TextView)view.findViewById(R.id.txtUsed);
        btnlogout = (Button)view.findViewById(R.id.btnlogout);

        btnlogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkClient.myCookieStore.clear();
                Intent intent = new Intent(getActivity(),LoginPage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

    //====================================================================================================================
    //餐券剩餘(server)
    public void personalInfo() {
        new NetworkClient(mActivity).get("/student/personalInfo", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    String stuId = jsonArray.getString(0);
                    int notUsedTicketNum = jsonArray.getInt(1);
                    int usedTicketNum = jsonArray.getInt(2);

                    txtStuId.setText(stuId);
                    txtNotUsed.setText(" 餐券剩餘 : " + notUsedTicketNum + "張");
                    txtUsed.setText(" 餐券使用 : " + usedTicketNum + "張");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    String message = errorResponse.getString("message");
                    Log.e("onFailure",message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
