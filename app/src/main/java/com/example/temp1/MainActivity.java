package com.example.temp1;

import static java.lang.Math.pow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button add1, add2, add3, reset1;
    private TextView out1;
    int cnt1 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        add1 = findViewById((R.id.add1));
        add2 = findViewById((R.id.add2));
        add3 = findViewById((R.id.add3));
        reset1 = findViewById(R.id.reset1);
        out1 = findViewById(R.id.out1);


        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                String now = (String) out1.getText();
                if(cnt1 == 0){
                    now = "0";
                }
                cnt1 ++;
                int s_n = Integer.parseInt(now);
                s_n += 1;
                String ans = String.format("%d", s_n);
                out1.setText(ans);
            }
        });

        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String now = (String) out1.getText();
                if(cnt1 == 0){
                    now = "0";
                }
                cnt1 ++;
                int s_n = Integer.parseInt(now);
                s_n += 2;
                String ans = String.format("%d", s_n);
                out1.setText(ans);
            }
        });

        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String now = (String) out1.getText();
                if(cnt1 == 0){
                    now = "0";
                }
                cnt1 ++;
                int s_n = Integer.parseInt(now);
                s_n += 3;
                String ans = String.format("%d", s_n);
                out1.setText(ans);
            }
        });

        reset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                out1.setText("0");
                cnt1 ++;
            }
        });

    }
}