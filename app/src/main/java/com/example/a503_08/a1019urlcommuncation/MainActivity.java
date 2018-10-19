package com.example.a503_08.a1019urlcommuncation;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    EditText url;
    Button download;
    TextView html;


    ProgressDialog progressDialog;

    //데이터를 출력할 핸들러 만들기
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //스레드가 넘겨준 데이터를 텍스트 뷰에 출력
            //데이터는 obj
            html.setText(msg.obj.toString());
            Log.e("핸들러",html.getText().toString());
            progressDialog.dismiss();
        }
    };

    //Thread thread = new Thread(){};
    //여러번 호출해야 하므로 클래스를 만들고 나중에 객체를 생성
    class ThreadEx extends Thread {
        @Override
        public void run() {
            try {
                Log.e("스레드", "시작");
                //다운로드 받을 주소 가져오기
                String addr = url.getText().toString();
                Log.e("다운로드 받을 주소", addr);
                //문자열 주소로 URL 객체 생성
                URL downloadURL = new URL(addr);
                //연결객체 생성
                HttpURLConnection con = (HttpURLConnection) downloadURL.openConnection();
                Log.e("커넥션", con.toString());
                //옵션 설정 -접속시간 캐시사용여부
                con.setConnectTimeout(20000);
                con.setUseCaches(false);

                //문자열 다운로드 받기 위한 스트림 생성
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = br.readLine();
                    //Log.e("읽은 내용",line);
                    if (line == null) {
                        break;

                    }

                    sb.append(line + "\n");

                    //전부 가져왔으면 닫기

                    br.close();
                    con.disconnect();
                    //Message에 저장해서 handler에게 메시지 전송
                    Message message = new Message();
                    message.obj = sb.toString();
                    handler.sendMessage(message);
                    Log.e("message",message.toString());

                }
            } catch (Exception e) {
                Log.e("예외 발생", e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("onCreate","onCreate");
        url = (EditText) findViewById(R.id.url);
        download = (Button) findViewById(R.id.download);
        html = (TextView) findViewById(R.id.html);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("버튼","버튼 클릭");
                ThreadEx threadEx = new ThreadEx();
                threadEx.start();

                progressDialog= ProgressDialog.show(MainActivity.this,"URL 다운로드","다운로드 중...");

            }
        });
    }
}
