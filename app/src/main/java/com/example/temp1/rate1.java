package com.example.temp1;

import static java.lang.Math.pow;
import static java.lang.System.out;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class rate1 extends AppCompatActivity implements Runnable{
    private static final String TAG = "Rate";
    private Button bu_dollar, bu_euro, bu_won, bu_set;
    private TextView out1;
    private TextInputEditText in1;
    private double val = 0;
    private float rate_dollar = 1.2f, rate_euro = 1.2f, rate_won = 1.2f;
    private ActivityResultLauncher<Intent> rateSettingLauncher;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate1);
        init();
        get();
        // 获取修改的汇率信息
        SharedPreferences sp = getSharedPreferences("rate_settings", MODE_PRIVATE);
        rate_dollar = sp.getFloat("key_dollar", 0.0f);
        rate_euro = sp.getFloat("key_euro", 0.0f);
        rate_won = sp.getFloat("key_won", 0.0f);
        // 线程快递站
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.i(TAG, "接收消息");
                if(msg.what == 8){
                    String str = msg.obj.toString();
                    out1.setText(str);
                }
            }
        };
        // rate_setting汇率设置
        rateSettingLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        rate_dollar = bundle.getFloat("key_dollar", 0f);
                        rate_euro = bundle.getFloat("key_euro", 0f);
                        rate_won = bundle.getFloat("key_won", 0f);
                        Log.i(TAG, "onActivityResult dollar " + rate_dollar);
                        Log.i(TAG, "onActivityResult euro " + rate_euro);
                        Log.i(TAG, "onActivityResult won " + rate_won);
                    }
                }
        );
        // 联网爬取
        Thread th1 = new Thread(this);
        th1.start();
    }
    public void init(){
        bu_dollar = findViewById((R.id.bu_dollar));
        bu_euro = findViewById((R.id.bu_euro));
        bu_won = findViewById((R.id.bu_won));
        out1 = findViewById(R.id.out1);
        in1 = findViewById(R.id.in1);
        bu_set = findViewById(R.id.setting1);
    }
    public void get(){
        bu_dollar.setOnClickListener(v -> update1(rate_dollar));
        bu_euro.setOnClickListener(v -> update1(rate_euro));
        bu_won.setOnClickListener(v -> update1(rate_won));
        bu_set.setOnClickListener(v -> open1());
    }
    public void update1(double rate){
        String s = String.valueOf(in1.getText()).trim();
        if (s.isEmpty()) {
            Toast.makeText(this, "请输入人民币金额", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            val = Float.parseFloat(s) * rate;
            String ans = String.format("%.2f", val);
            out1.setText(ans);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "输入格式有误，请输入有效数字", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "解析失败: " + s, e);
        }
    }
    public void open1 () {
        Intent t1 = new Intent(this, Rate_setting.class);
        t1.putExtra("dollar", rate_dollar);
        t1.putExtra("euro", rate_euro);
        t1.putExtra("won", rate_won);
        Log.i(TAG, "open : + dollar " + rate_dollar);
        Log.i(TAG, "open : + euro " + rate_euro);
        Log.i(TAG, "open : + won " + rate_won);
//        startActivity(t1);
//        被弃用的方法
//        startActivityForResult(t1, 1);
//        最新
        rateSettingLauncher.launch(t1);

    }
    @Override
    public void run() {
        Log.i(TAG, "t1 run");
        // 发送消息
//        URL url = null;
//        try{
//            url = new URL("https://www.gsjb.com/system/2025/04/17/031170044.shtml");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            InputStream in = http.getInputStream();
//            String html = inputStream2String(in);
//            Log.i(TAG, "run: html=" + html);
//
//        } catch (RuntimeException | MalformedURLException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try{
            Document doc = Jsoup.connect("https://www.gsjb.com/system/2025/04/17/031170044.shtml").get();
            Log.i(TAG, "run: title = " + doc.title());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Message msg = handler.obtainMessage(8, "swufe!");
        msg.what = 8;
        msg.obj = "SWUFE!";
        handler.sendMessage(msg);
    }
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        while(true){
            int rsz = in.read(buffer, 0, buffer.length);
            if(rsz < 0){
                break;
            }
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}