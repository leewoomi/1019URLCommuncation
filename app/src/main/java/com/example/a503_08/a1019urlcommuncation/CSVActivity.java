package com.example.a503_08.a1019urlcommuncation;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CSVActivity extends AppCompatActivity {

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ListView listView;

    //핸들러
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //리스트 뷰 재출력
            adapter.notifyDataSetChanged();
        }
    };


    //스레드
    class ThreadEx extends Thread {
        @Override
        public void run() {
            try {
                String addr = "http://192.168.0.118:8080/android/data.csv";
                URL url = new URL(addr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(20000);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

              list.clear();
              while (true){
                  String line = br.readLine();
                  if(line == null){
                      break;
                  }
                  String[] ar = line.split(",");
                  for(String temp : ar){
                      list.add(temp);
                  }
              }
              br.close();
              con.disconnect();
              handler.sendEmptyMessage(0);
            } catch (Exception e) {
                Log.e("다운로드 실패", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv);

        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(CSVActivity.this, android.R.layout.simple_list_item_1, list);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        ThreadEx threadEx = new ThreadEx();
        threadEx.start();
    }
}
