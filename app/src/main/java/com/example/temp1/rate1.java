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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
//    全量清洗段落文本里的异常字符
    private String cleanText(String raw) {
        return raw
                .replace("．", ".")            // 全角点 → 半角点
                .replace("\u3000", " ")        // 全角空格
                .replace("\u00A0", " ")        // &nbsp;
                .replaceAll("[\\s ]+", " ")    // 多空白合并
                .trim();
    }
    // 从“100美元 … 720.85人民币”这类文本中提取数字 720.85
    private BigDecimal extractAfterKeyword(String text, String keyword) {
        // 关键字后出现的第一个浮点数
        Pattern p = Pattern.compile(keyword + ".*?(\\d+(?:\\.\\d+)?)");
        Matcher m = p.matcher(text);
        if (m.find()) {
            return new BigDecimal(m.group(1));
        }
        return BigDecimal.ZERO;
    }
    // 读取或保存上次成功的汇率，type 可取 usd/eur/krw
    private BigDecimal readSaved(String type, BigDecimal fallback) {
        SharedPreferences sp = getSharedPreferences("rate_cache", MODE_PRIVATE);
        if (sp.contains(type)) {
            return new BigDecimal(sp.getString(type, "0"));
        }
        return fallback;
    }
    private void saveRate(String type, BigDecimal val) {
        SharedPreferences sp = getSharedPreferences("rate_cache", MODE_PRIVATE);
        sp.edit().putString(type, val.toPlainString()).apply();
    }
    @Override
    public void run() {
        Log.i(TAG, "t1 run（开始爬取）");
        BigDecimal usd = BigDecimal.ZERO;
        BigDecimal eur = BigDecimal.ZERO;
        BigDecimal krw = BigDecimal.ZERO;
        try {
            Document doc = Jsoup.connect(
                            "https://www.gsjb.com/system/2025/04/17/031170044.shtml")
                    .header("User-Agent", "Mozilla/5.0")
                    .timeout(8_000)
                    .get();
            Elements ps = doc.select("#conter2018 p");
            for (Element p : ps) {
                String line = cleanText(p.text());
                if (line.startsWith("100美元")) {
                    usd = extractAfterKeyword(line, "100美元");
                } else if (line.startsWith("100欧元")) {
                    eur = extractAfterKeyword(line, "100欧元");
                } else if (line.startsWith("100人民币") && line.contains("韩元")) {
                    BigDecimal per100 = extractAfterKeyword(line, "100人民币");
                    if (per100.compareTo(BigDecimal.ZERO) > 0) {
                        krw = BigDecimal.valueOf(100).divide(per100, 10, RoundingMode.HALF_UP);
                    }
                }
            }
            // 若某项解析失败，用缓存兜底
            if (usd.compareTo(BigDecimal.ZERO) == 0)
                usd = readSaved("usd", BigDecimal.ONE);
            if (eur.compareTo(BigDecimal.ZERO) == 0)
                eur = readSaved("eur", BigDecimal.ONE);
            if (krw.compareTo(BigDecimal.ZERO) == 0)
                krw = readSaved("krw", BigDecimal.ONE);
            // 保存最新成功值
            saveRate("usd", usd);
            saveRate("eur", eur);
            saveRate("krw", krw);
        } catch (IOException e) {
            Log.e(TAG, "联网/解析失败，用缓存值顶替", e);
            usd = readSaved("usd", BigDecimal.ONE);
            eur = readSaved("eur", BigDecimal.ONE);
            krw = readSaved("krw", BigDecimal.ONE);
        }
        BigDecimal usdRate = usd.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal eurRate = eur.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        String show = "最新汇率：\n" +
                "USD: " + usdRate + "\n" +
                "EUR: " + eurRate + "\n" +
                "KRW: " + krw.setScale(6, RoundingMode.HALF_UP);
        handler.sendMessage(handler.obtainMessage(8, show));
        // 更新全局变量供按钮换算
        rate_dollar = usdRate.floatValue();
        rate_euro   = eurRate.floatValue();
        rate_won    = krw.floatValue();
    }
//    // 示例留下
//    @Override
//    public void run() {
//        Log.i(TAG, "t1 run");
//        // 发送消息
////        URL url = null;
////        try{
////            url = new URL("https://www.gsjb.com/system/2025/04/17/031170044.shtml");
////            HttpURLConnection http = (HttpURLConnection) url.openConnection();
////            InputStream in = http.getInputStream();
////            String html = inputStream2String(in);
////            Log.i(TAG, "run: html=" + html);
////
////        } catch (RuntimeException | MalformedURLException e) {
////            throw new RuntimeException(e);
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        }
////        Message msg = handler.obtainMessage(8, "swufe!");
////        msg.what = 8;
////        msg.obj = "SWUFE!";
////        handler.sendMessage(msg);
//    }
//    // 提取文本中的第二个数（人民币汇率）
//    private String inputStream2String(InputStream inputStream) throws IOException {
//        final int bufferSize = 1024;
//        final char[] buffer = new char[bufferSize];
//        final StringBuilder out = new StringBuilder();
//        Reader in = new InputStreamReader(inputStream, "gb2312");
//        while(true){
//            int rsz = in.read(buffer, 0, buffer.length);
//            if(rsz < 0){
//                break;
//            }
//            out.append(buffer, 0, rsz);
//        }
//        return out.toString();
//    }
}