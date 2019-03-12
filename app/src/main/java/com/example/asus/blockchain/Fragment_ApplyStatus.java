package com.example.asus.blockchain;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Fragment_ApplyStatus extends Fragment {

    TextView txtStatus;
    private static final String TAG = "ApplyStatus";

    private Activity mActivity;
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;

        checkStatus();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applystatus, container, false);
        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.program_toolbar);
        tb.setTitle(" 申請狀態");
        txtStatus = (TextView)view.findViewById(R.id.txtStatus);
        return view;
    }
    //====================================================================================================================
    //確認申請狀況(server)
    public void checkStatus() {

        new NetworkClient(getActivity()).get("/student/queryApplyStatus", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                JSONObject jsonObject = null;
                try {
                    jsonObject = response.getJSONObject("message");
                    int applyStatus = jsonObject.getInt("VerifyResult");
                    Log.e("GG","VerifyResult: " + applyStatus);
                    if (applyStatus == 3) {
                        txtStatus.setText("尚無申請");
                    } else if (applyStatus == 0) {
                        txtStatus.setText("審核中");
                    } else if (applyStatus == 1) {
                        txtStatus.setText("審核通過");
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new Fragment_MyTicket(), null)
                                .addToBackStack(null)
                                .commit();
                    } else if (applyStatus == 2) {
                        txtStatus.setText("審核未通過");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
//                    String message = errorResponse.getString("message");
                Log.e("gg",errorResponse.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}
