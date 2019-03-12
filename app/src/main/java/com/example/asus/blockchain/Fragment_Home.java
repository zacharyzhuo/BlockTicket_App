package com.example.asus.blockchain;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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

public class Fragment_Home extends Fragment{
    private static final String TAG = "Fragment_Home";
    private ListView mListView;
    private Activity mActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        //return inflater.inflate(R.layout.fragment_home, null);

        Toolbar tb = (Toolbar) getActivity().findViewById(R.id.program_toolbar);
        tb.setTitle(" Block券");

        mListView = (ListView) view.findViewById(R.id.listView);
        ArrayList<Card> list = new ArrayList<>();

        list.add(new Card("drawable://" + R.drawable.g, "第一餐廳"));
        list.add(new Card("drawable://" + R.drawable.f, "餐廳座位"));
        list.add(new Card("drawable://" + R.drawable.e, "茶壜"));
        list.add(new Card("drawable://" + R.drawable.d, "第二餐廳"));
        list.add(new Card("drawable://" + R.drawable.a, "二樓傳承"));
        list.add(new Card("drawable://" + R.drawable.c, "二樓自助餐"));
        list.add(new Card("drawable://" + R.drawable.b, "三樓八方雲集"));

        CustomListAdapter adapter = new CustomListAdapter(mActivity, R.layout.cardview_layout, list);
        mListView.setAdapter(adapter);

        return view;
    }
}
