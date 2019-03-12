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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class Fragment_Records extends Fragment{
    private static final String TAG = "ConsumptionRecords";
    ListView recordList;
    ArrayList<HashMap<String, Object>> listData=new ArrayList<HashMap<String, Object>>();

    private Activity mActivity;
    ImageView cicon;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;


        queryConsumptionRecords();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumptionrecords,container,false);

        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.program_toolbar);
        tb.setTitle(" 消費紀錄");

        recordList = (ListView)view.findViewById(R.id.recordList);
        cicon = (ImageView)view.findViewById(R.id.cicon);

        return view;
    }
    //====================================================================================================================
    //消費紀錄(server)
    public void queryConsumptionRecords() {
        new NetworkClient(mActivity).get("/student/queryConsumptionRecords", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    Log.e("GG",jsonArray.toString());
                    if(jsonArray.length() == 0) {
                        HashMap<String, Object> myHasMap = new HashMap<String, Object>();
                        myHasMap.put("ticketId","查無資料");
                        myHasMap.put("restaurant","");
                        myHasMap.put("issuedDate","");
                        myHasMap.put("usedDate","");
                        listData.add(myHasMap);
                    }
                    else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String TicketId = jsonObject.getString("ticketId");
                            String Restaurant = jsonObject.getString("restaurant");
                            String IssuedDate = jsonObject.getString("issuedDate");
                            String UsedDate = jsonObject.getString("usedDate");

                            HashMap<String, Object> myHasMap = new HashMap<String, Object>();

                            myHasMap.put("ticketId","餐券編號 : "+TicketId);
                            myHasMap.put("restaurant","消費餐廳 : "+Restaurant);
                            myHasMap.put("issuedDate","發放日期 : "+IssuedDate);
                            myHasMap.put("usedDate","使用日期 : "+UsedDate);
                            listData.add(myHasMap);
                        }
                    }
                    SimpleAdapter adapter = new SimpleAdapter(
                            mActivity,
                            listData,
                            R.layout.records_single_item,
                            new String[]{"ticketId", "restaurant", "issuedDate", "usedDate"},
                            new int[]{R.id.ticketId, R.id.restaurant, R.id.issuedDate, R.id.usedDate});

                    recordList.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                try {
                Log.e("gg", errorResponse.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

}
