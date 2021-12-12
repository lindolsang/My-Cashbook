package kr.lindol.mycashbook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import kr.lindol.mycashbook.list.CashLogListActivity;

public class MainActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            startListActivity();
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    private void startListActivity() {
        startActivity(new Intent(getApplicationContext(), CashLogListActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            new Handler().postDelayed(() -> {
                startListActivity();
            }, DELAY_MILLIS);
        }
    }
}