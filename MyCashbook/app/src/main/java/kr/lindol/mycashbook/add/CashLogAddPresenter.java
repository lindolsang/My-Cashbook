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
import kr.lindol.mycashbook.util.DateUtils;

public class CashLogAddPresenter implements AddContract.Presenter {

    private CashLogRepository mRepository;
    private AddContract.View mView;

    private Date mDate;
    private int mLogId;
    private CashLog mCashLog;

    public CashLogAddPresenter(@NonNull CashLogRepository repository,
                               @NonNull AddContract.View view,
                               @NonNull Date date,
                               int logId) {
        mRepository = checkNotNull(repository, "repository cannot be null");
        mView = checkNotNull(view, "view cannot be null");
        mDate = checkNotNull(date, "date can not be null");
        mLogId = logId;

        view.setPresenter(this);
    }

    @Override
    public void start() {
        if (isEditMode()) {
            if (loadingRequired()) {
                loadData();
            }
        } else {
            mView.showDate(mDate);
        }
    }

    private boolean isEditMode() {
        return mLogId > 0;
    }

    private boolean loadingRequired() {
        return mCashLog == null;
    }

    private void loadData() {
        mRepository.loadById(mLogId, new CashLogDataSource.LoadSingleCashLogCallback() {
            @Override
            public void onCashLogLoaded(@NonNull CashLog log) {
                checkNotNull(log, "log can not be null");

                mDate = DateUtils.fromStr(log.dayTag);
                mView.showDate(mDate);
                mView.showItem(log.item);
                mView.showAmount(log.amount);
                mView.showMemo(log.description);

                mCashLog = log;
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
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

        CashLog log = new CashLog();
        log.item = item;
        log.type = type;
        log.amount = amount;
        log.dateTag = Integer.parseInt(sfDay.format(date));
        log.dayTag = sfDay.format(date);
        log.monthTag = sfMonth.format(date);
        log.createdBy = System.currentTimeMillis();
        log.description = description;

        if (isEditMode() && mCashLog != null) {
            log.id = mCashLog.id;
            log.createdBy = mCashLog.createdBy;

            mRepository.update(log, opCallBack);
        } else {
            mRepository.save(log, opCallBack);
        }
    }

    private CashLogDataSource.OperationCallback opCallBack =
            new CashLogDataSource.OperationCallback() {
                @Override
                public void onFinished() {

                    if (isEditMode()) {
                        mView.showSuccessWithEdit();
                        mView.closeWindow();
                    } else {
                        mView.showSuccess();
                        mView.clearForms();
                    }
                }

                @Override
                public void onError() {
                    mView.showFailure();
                }
            };

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
