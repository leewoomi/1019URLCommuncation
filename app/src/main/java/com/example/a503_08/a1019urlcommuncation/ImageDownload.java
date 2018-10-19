package com.example.a503_08.a1019urlcommuncation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownload extends AppCompatActivity {

    ImageView image;
    Button display, download;


    Handler displayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            image.setImageBitmap(bitmap);
        }
    };

    class DisplayThread extends Thread {
        @Override
        public void run() {
            try {

                String addr = "http://image.ajunews.com/content/image/2018/03/06/20180306084151311597.jpg";
                URL url = new URL(addr);
                //url에 연결해서 비트맵만들기
                Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                Message message = new Message();
                message.obj = bitmap;
                displayHandler.sendMessage(message);
            } catch (Exception e) {
                Log.e("이미지 가져오기", e.getMessage());
            }
        }
    }

    Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //파일이 존재하는 경우
            if (msg.obj != null) {

                //안드로이드의 Data 디렉토리 경로를 가져오기
                String path = Environment.getDataDirectory().getAbsolutePath();
                //현재 앱 내의 파일 경로 만들기
                path = path + "/data/com.example.a503_08.a1019urlcommuncation/" + (String) msg.obj;
                //이미지 파일을 image에 출력
                image.setImageBitmap(BitmapFactory.decodeFile(path));
            } else {
                //파일이 존재하지 않는 경우
                Toast.makeText(ImageDownload.this, "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();


            }
        }
    };

    //이미지를 다운로드 받아서 파일로 저장하는 스레드
    class DownloadThread extends Thread {
        String addr;
        String filepath;

        public DownloadThread(String addr, String filepath) {
            this.addr = addr;
            this.filepath = filepath;
            //Log.e("addr",addr);
           // Log.e("filepath",filepath);
        }

        @Override
        public void run() {
            try {
                URL url = new URL(addr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(20000);
                con.setUseCaches(false);

                //내용을 읽을 스트림을 생성
                InputStream is = con.getInputStream();

                //기록할 스트림 생성
                PrintStream pw = new PrintStream(openFileOutput(filepath, 0));

                //Log.e("확인","여기까지");
                //is에서 읽어서 pw에 기록
                while (true) {
                    //Log.e("is에서 읽었는지 확인","확인");
                    byte[] b = new byte[1024];
                    int read = is.read(b);
                    if (read <= 0) {
                        break;
                    }

                    pw.write(b, 0, read);

                }

                pw.close();
                is.close();
                con.disconnect();
                Message message = new Message();
                message.obj = filepath;
                downloadHandler.sendMessage(message);

            } catch (Exception e) {
                Log.e("이미지 다운로드 실패", e.getMessage());
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_download);


        image = (ImageView) findViewById(R.id.image);

        display = (Button) findViewById(R.id.display);

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("display", "display button");
                DisplayThread displayThread = new DisplayThread();
                displayThread.start();
            }
        });
        download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e("download", "download button");
                //이미지를 다운로드 받을 주소
                String imageUrl =
                        "http://cdnweb01.wikitree.co.kr/webdata/editor/201411/23/img_20141123105432_f6c596d2.jpg";
                //파일명 만들기
                int idx = imageUrl.lastIndexOf("/");
                String filename = imageUrl.substring(idx + 1);
               // Log.e("filename",filename);
                //파일 경로 만들기
                String data = Environment.getDataDirectory().getAbsolutePath();

                String path = data + "/data/com.example.a503_08.a1019urlcommuncation/files/" + filename;
                //Log.e("path",path);
                //파일 존재 여부를 확인
                if (new File(path).exists()) {
                    Toast.makeText(ImageDownload.this,
                            "파일이 존재합니다.",
                            Toast.LENGTH_LONG).show();
                    image.setImageBitmap(
                            BitmapFactory.decodeFile(path));
                } else {
                    Toast.makeText(ImageDownload.this,
                            "파일이 존재하지 않습니다..",
                            Toast.LENGTH_LONG).show();

                    //Log.e("이미지 파일 경로", imageUrl);
                    //Log.e("저장할 파일 경로", path);
                    DownloadThread th = new DownloadThread(imageUrl, filename);
                    th.start();
                }
            }

        });
    }
}
