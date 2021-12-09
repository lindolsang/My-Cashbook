package kr.lindol.mycashbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import kr.lindol.mycashbook.list.CashLogListActivity;

public class MainActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), CashLogListActivity.class));
                finish();
            }
        }, DELAY_MILLIS);
    }
}