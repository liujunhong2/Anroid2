package com.example.temp1;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
public class RateTask implements Runnable {
    private static final String TAG = "Rate";
    private final Context context;
    private final Handler handler;
    public RateTask(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }
    @Override
    public void run() {
        Log.i(TAG, "开始爬取汇率");
        double usd = 0.0;
        double eur = 0.0;
        double krw = 0.0;
        ArrayList<String> list = new ArrayList<>();
        String currentCurrency = null;
        try {
            Document doc = Jsoup.connect("https://www.huilvbiao.com/").userAgent("Mozilla/5.0").get();
            Elements rows = doc.select("table#index_table tbody tr");
            for (Element row : rows) {
                Element th = row.selectFirst("th.table-coin.align-middle");
                if (th != null) {
                    currentCurrency = th.text().trim();
                } else {
                    if (currentCurrency != null) {
                        Elements tds = row.select("td");
                        try {
                            double rate = Double.parseDouble(tds.get(1).text());
                            if (currentCurrency.contains("美元")) {
                                usd = rate;
                            } else if (currentCurrency.contains("欧元")) {
                                eur = rate;
                            } else if (currentCurrency.contains("韩国元")) {
                                krw = rate;
                            }
                        } catch (NumberFormatException e) {
                            Log.w(TAG, "汇率解析失败: " + currentCurrency);
                        }
                        if (tds.size() >= 2) {
                            String rate = tds.get(1).text().trim();
                            list.add(currentCurrency + " : " + rate);
                            currentCurrency = null;
                        }
                    }
                }
            }
            if (usd == 0.0){{
                Log.i(TAG, "dollar fail");
                usd = readSaved("usd", 7.0);
            }}
            if (eur == 0.0){
                Log.i(TAG, "euro fail");
                eur = readSaved("eur", 8.0);
            }
            if (krw == 0.0){
                Log.i(TAG, "won fail");
                krw = readSaved("krw", 180.0);
            }
            // 保存
            saveRate("usd", usd);
            saveRate("eur", eur);
            saveRate("krw", krw);
        } catch (IOException e) {
            Log.e(TAG, "网络异常，使用缓存", e);
            usd = readSaved("usd", 7.0);
            eur = readSaved("eur", 8.0);
            krw = readSaved("krw", 180.0);
        }
        double usdRate = usd / 100.0;
        double eurRate = eur / 100.0;
        double krwRate = krw / 100.0;
        Bundle result = new Bundle();
        result.putStringArrayList("key_list_rate", list);
        result.putFloat("web_dollar", (float) usdRate);
        result.putFloat("web_euro", (float) eurRate);
        result.putFloat("web_won", (float) krwRate);
        Message msg = handler.obtainMessage(8, result);
        handler.sendMessage(msg);
    }
    private double readSaved(String type, double fallback) {
        SharedPreferences sp = context.getSharedPreferences("rate_cache", Context.MODE_PRIVATE);
        if (sp.contains(type)) {
            try {
                return Double.parseDouble(sp.getString(type, String.valueOf(fallback)));
            } catch (NumberFormatException e) {
                return fallback;
            }
        }
        return fallback;
    }
    private void saveRate(String type, double val) {
        SharedPreferences sp = context.getSharedPreferences("rate_cache", Context.MODE_PRIVATE);
        sp.edit().putString(type, String.valueOf(val)).apply();
    }
}