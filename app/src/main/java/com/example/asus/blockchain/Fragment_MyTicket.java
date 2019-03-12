package com.example.asus.blockchain;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import cz.msebera.android.httpclient.Header;


public class Fragment_MyTicket extends Fragment{

    private static final String TAG = "MyTicket";

    public TextView txtUsedTicket;
    public TextView txtNotUsedTicket;
    protected GridView gv;
    private int image = R.drawable.ticket3;

    ArrayList<HashMap<String, Object>> listData=new ArrayList<HashMap<String, Object>>();

    private Activity mActivity;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;


        myTickets();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myticket,container,false);

        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.program_toolbar);
        tb.setTitle(" 我的餐券");

        gv = (GridView) view.findViewById(R.id.gv);
        txtUsedTicket = (TextView)view.findViewById(R.id.txtUsedTicket);
        txtNotUsedTicket = (TextView)view.findViewById(R.id.txtNotUsedTicket);

        return view;
    }
    //====================================================================================================================
    //餐券gridview(server)
    public void myTickets() {
        new NetworkClient(mActivity).get("/student/queryTicketsByOwner", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray jsonArray = response.getJSONArray("message");
                    JSONArray childArr = jsonArray.getJSONArray(0);
                    int usedTicket = jsonArray.getInt(1);

                    txtUsedTicket.setText("已使用 : " + usedTicket);
                    txtNotUsedTicket.setText("未使用 : " + childArr.length());

                    for (int i = 0; i < childArr.length(); i++) {
                        JSONObject jsonObject = childArr.getJSONObject(i);

                        TicketInfo ticketInfo = new TicketInfo();
                        ticketInfo.TicketId = jsonObject.getString("Key");

                        HashMap<String, Object> myHasMap = new HashMap<String, Object>();

                        myHasMap.put("image",image);
                        myHasMap.put("TicketId",ticketInfo.TicketId);
                        myHasMap.put("Title","餐券編號 : " + ticketInfo.TicketId);
                        listData.add(myHasMap);
                    }
                    SimpleAdapter adapter = new SimpleAdapter(
                            mActivity,
                            listData,
                            R.layout.gridview_item,
                            new String[]{"image", "Title"},
                            new int[]{R.id.image, R.id.ticketId});

                    gv.setAdapter(adapter);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                            new AlertDialog.Builder(mActivity)
                                    .setTitle("確認使用")
                                    //.setMessage(detail)
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            for(int i=0;i<parent.getCount();i++)
                                            {
                                                View v=parent.getChildAt(parent.getCount()-1-i);
                                                if (position == i)
                                                {
                                                    Log.e("position", ""+position);
                                                    Intent it = new Intent(mActivity, Information.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("TicketId",listData.get(position).get("TicketId").toString());
                                                    Log.e("listData.get(position)", listData.get(position).get("TicketId").toString());

                                                    it.putExtras(bundle);
                                                    startActivity(it);
                                                }
                                                else
                                                {

                                                }
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    String message = errorResponse.getString("message");
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
                Log.e("gg",message);
                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}
