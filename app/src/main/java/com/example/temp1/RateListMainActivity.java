package com.example.temp1;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class RateListMainActivity extends ListActivity {
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> list1 = new ArrayList<String>();
        for(int i = 0; i < 100; i ++){
            list1.add("item" + i);
        }
        String[] list_data  = {"a", "b", "c", "d", "e"};
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list1);
//        setListAdapter(adapter);
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg){
                if(msg.what == 8){
                    Bundle bundle1 = (Bundle) msg.obj;
                    ArrayList<String> list2 = bundle1.getStringArrayList("key_list_rate");
                    Log.i("Rate", "content " + list2);

                    ListAdapter adapter2 = new ArrayAdapter<String>(RateListMainActivity.this, android.R.layout.simple_list_item_1, list2);
                    setListAdapter(adapter2);
                }
                super.handleMessage(msg);
            }
        };
        RateTask t1 = new RateTask(this, handler);
        new Thread(t1).start();
    }
}