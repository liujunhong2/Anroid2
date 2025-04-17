package com.example.temp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Rate_setting extends AppCompatActivity {
    private static final String TAG = "Rate";
    private Button bu1;
    private EditText v_dollar, v_euro, v_won;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_setting);
        init();
        Intent set = getIntent();
        float dollar = set.getFloatExtra("dollar", 0f);
        float euro = set.getFloatExtra("euro", 0f);
        float won = set.getFloatExtra("won", 0f);
        v_dollar.setText(String.valueOf(dollar));
        v_euro.setText(String.valueOf(euro));
        v_won.setText(String.valueOf(won));
        Log.i(TAG,"onCreate + dollar " + dollar);
        Log.i(TAG,"onCreate + euro " + euro);
        Log.i(TAG,"onCreate + won " + won);
        get();
    }
    public void init() {
        bu1 = findViewById(R.id.bu1);
        v_dollar = findViewById(R.id.v_dollar);
        v_euro = findViewById(R.id.v_euro);
        v_won = findViewById(R.id.v_won);
    }
    public void get() {
        bu1.setOnClickListener(v -> handle());
    }
    public void handle() {
        Log.i(TAG, "handle: ");
        String dollar = v_dollar.getText().toString();
        String euro = v_euro.getText().toString();
        String won = v_won.getText().toString();
        Float dollar1 = Float.parseFloat(dollar);
        Float euro1 = Float.parseFloat(euro);
        Float won1 = Float.parseFloat(won);
        SharedPreferences sp = getSharedPreferences("rate_settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("key_dollar", dollar1);
        editor.putFloat("key_euro", euro1);
        editor.putFloat("key_won", won1);
        editor.apply();
        Log.i(TAG, "handle dollar " + dollar);
        Log.i(TAG, "handle euro " + euro);
        Log.i(TAG, "handle won " + won);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putFloat("key_dollar", dollar1);
        bundle.putFloat("key_euro", euro1);
        bundle.putFloat("key_won", won1);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}