package kr.lindol.mycashbook.add;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.lindol.mycashbook.data.CashLogDataSource;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLog;
import kr.lindol.mycashbook.data.db.CashType;

public class CashLogAddPresenter implements AddContract.Presenter {

    private CashLogRepository mRepository;
    private AddContract.View mView;

    private Date mDate;

    public CashLogAddPresenter(@NonNull CashLogRepository repository,
                               @NonNull AddContract.View view,
                               @NonNull Date date) {
        mRepository = checkNotNull(repository, "repository cannot be null");
        mView = checkNotNull(view, "view cannot be null");
        mDate = checkNotNull(date, "date can not be null");

        view.setPresenter(this);
    }

    @Override
    public void start() {
        mView.showDate(mDate);
    }

    private boolean checkParameters(String item, String amount, Date date) {
        checkNotNull(item, "item can not be null");
        checkNotNull(amount, "amount can not be null");
        checkNotNull(date, "date can not be null");

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
    public void addAsIncome(@NonNull String item,
                            @NonNull String amount,
                            @Nullable String description) {
        if (checkParameters(item, amount, mDate)) {
            saveCashLog(item, CashType.INCOME, Integer.parseInt(amount), mDate, description);
        }
    }

    private void saveCashLog(String item, CashType type, int amount, Date date, String description) {
        SimpleDateFormat sfMonth = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sfDay = new SimpleDateFormat("yyyyMMdd");

        CashLog newLog = new CashLog();
        newLog.item = item;
        newLog.type = type;
        newLog.amount = amount;
        newLog.dateTag = Integer.parseInt(sfDay.format(date));
        newLog.dayTag = sfDay.format(date);
        newLog.monthTag = sfMonth.format(date);
        newLog.createdBy = System.currentTimeMillis();
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
    public void addAsExpense(@NonNull String item,
                             @NonNull String amount,
                             @Nullable String description) {
        if (checkParameters(item, amount, mDate)) {
            saveCashLog(item, CashType.EXPENSE, Integer.parseInt(amount), mDate, description);
        }
    }

    @Override
    public void selectDate() {
        mView.showCalendar(mDate);
    }

    @Override
    public void setToDate(@NonNull Date date) {
        checkNotNull(date, "date can not be null");
        mDate = date;

        mView.showDate(date);
    }
}
