package kr.lindol.mycashbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import kr.lindol.mycashbook.list.CashLogListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButton1_clicked(View view) {
        Intent i = new Intent(this, CashLogListActivity.class);
        startActivity(i);
    }
}