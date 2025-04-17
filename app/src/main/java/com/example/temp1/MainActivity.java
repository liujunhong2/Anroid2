package com.example.temp1;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private Button add1_a, add2_a, add3_a, reset, add1_b, add2_b, add3_b;
    private TextView out_a, out_b;
    private int cnt_a = 0, cnt_b = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        set();
//        if(savedInstanceState != null){
//            cnt_a = savedInstanceState.getInt("score_a", 0);
//            cnt_b = savedInstanceState.getInt("scour_b", 0);
//            show();
//        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score_a", cnt_a);
        outState.putInt("score_b", cnt_b);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cnt_a = savedInstanceState.getInt("score_a", 0);
        cnt_b = savedInstanceState.getInt("score_b", 0);
        show();
    }

    private void init(){
        add1_a = findViewById((R.id.add1_a));
        add2_a = findViewById((R.id.add2_a));
        add3_a = findViewById((R.id.add3_a));
        add1_b = findViewById((R.id.add1_b));
        add2_b = findViewById((R.id.add2_b));
        add3_b = findViewById((R.id.add3_b));
        reset = findViewById(R.id.reset1);
        out_a = findViewById(R.id.out_a);
        out_b = findViewById(R.id.out_b);
    }
    private void set(){
        add1_a.setOnClickListener(v -> update('A', 1));
        add2_a.setOnClickListener(v -> update('A', 2));
        add3_a.setOnClickListener(v -> update('A', 3));
        add1_b.setOnClickListener(v -> update('B', 1));
        add2_b.setOnClickListener(v -> update('B', 2));
        add3_b.setOnClickListener(v -> update('B', 3));
        reset.setOnClickListener(v -> reset1());
    }

    private void update(char op, int val){
        if(op == 'A'){
            cnt_a += val;
        }else{
            cnt_b += val;
        }
        show();
    }
    private void reset1(){
        cnt_a = cnt_b = 0;
        show();
    }
    private void show(){
        out_a.setText("Score:" + String.valueOf(cnt_a));
        out_b.setText("Score:" + String.valueOf(cnt_b));
    }
}