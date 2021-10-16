package kr.lindol.mycashbook.add;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.lindol.mycashbook.data.CashLogDataSource;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogAddPresenter implements AddContract.Presenter {

    private CashLogRepository mRepository;
    private AddContract.View mView;

    public CashLogAddPresenter(CashLogRepository repository, AddContract.View view) {
        mRepository = checkNotNull(repository, "repository cannot be null");
        mView = checkNotNull(view, "view cannot be null");

        view.setPresenter(this);
    }

    @Override
    public void start() {

    }

    private boolean checkParameters(String item, String amount, Date date) {
        checkNotNull(item, "item cannot be null");
        checkNotNull(amount, "amount cannot be null");
        checkNotNull(date, "date cannot be null");

        if (item.isEmpty()) {
            mView.showItemValueEmptyError();
            return false;
        }

        if (amount.isEmpty()) {
            mView.showAmountValueEmptyError();
            return false;
        }

        int amountInt = 0;
        try {
            amountInt = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            mView.showAmountValueFormatError();
            return false;
        }
        if (amountInt <= 0) {
            mView.showAmountValueSmallError();
            return false;
        }
        return true;
    }

    @Override
    public void addAsIncome(@NonNull String item, @NonNull String amount, @NonNull Date date, @Nullable String description) {
        if (checkParameters(item, amount, date)) {
            saveCashLog(item, 0, Integer.parseInt(amount), date, description);
        }
    }

    private void saveCashLog(String item, int type, int amount, Date date, String description) {
        SimpleDateFormat sfMonth = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sfDay = new SimpleDateFormat("yyyyMMdd");

        CashLog newLog = new CashLog();
        newLog.item = item;
        newLog.type = type;
        newLog.amount = amount;
        newLog.dayTag = sfDay.format(date);
        newLog.monthTag = sfMonth.format(date);
        newLog.description = description;

        mRepository.save(newLog, new CashLogDataSource.OperationCallback() {
            @Override
            public void onFinished() {
                mView.clearForms();
                mView.showSuccess();
            }

            @Override
            public void onError() {
                mView.showFailure();
            }
        });
    }

    @Override
    public void addAsOutlay(@NonNull String item, @NonNull String amount, @NonNull Date date, @Nullable String description) {
        if (checkParameters(item, amount, date)) {
            saveCashLog(item, 1, Integer.parseInt(amount), date, description);
        }
    }
}
