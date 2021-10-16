package kr.lindol.mycashbook.add;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import kr.lindol.mycashbook.R;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLogDatabase;
import kr.lindol.mycashbook.util.ActivityUtils;
import kr.lindol.mycashbook.util.AppExecutors;

public class CashLogAddActivity extends AppCompatActivity {

    public static final String EXTRA_DATE = "extra_date";

    private AddContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_log_add);

        Date setDate = new Date();
        if (getIntent() != null) {
            long timeInMilli = getIntent().getLongExtra(EXTRA_DATE, 0);
            if (timeInMilli > 0) {
                setDate = new Date(timeInMilli);
            }
        }

        CashLogAddFragment fragment = (CashLogAddFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = CashLogAddFragment.newInstance(setDate);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        CashLogDatabase db = CashLogDatabase.getInstance(this);
        mPresenter = new CashLogAddPresenter(
                new CashLogRepository(db.cashLogDao(), new AppExecutors()),
                fragment);
    }
}