package kr.lindol.mycashbook.list;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import kr.lindol.mycashbook.R;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLogDatabase;
import kr.lindol.mycashbook.util.ActivityUtils;
import kr.lindol.mycashbook.util.AppExecutors;

public class CashLogListActivity extends AppCompatActivity {

    private CashLogListPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_log_list);

        CashLogListFragment cashLogListFragment = (CashLogListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (cashLogListFragment == null) {
            cashLogListFragment = CashLogListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), cashLogListFragment, R.id.contentFrame);
        }

        CashLogDatabase db = CashLogDatabase.getInstance(this);
        mPresenter = new CashLogListPresenter(new CashLogRepository(db.cashLogDao(), new AppExecutors()), cashLogListFragment);
    }
}