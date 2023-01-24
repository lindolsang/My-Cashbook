package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.common.base.Preconditions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kr.lindol.mycashbook.data.CashLogDataSource;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogListPresenter implements ListContract.Presenter {
    private final CashLogRepository mRepository;
    private final ListContract.View mView;
    private final Calendar mCalendarFrom;
    private final Calendar mCalendar;
    private Date mFixedDate;
    private int mSelectedCashLogId;
    private ListType mListType;
    private Date mMarkFrom;
    private Date mMarkTo;
    private boolean isNavigated;

    public CashLogListPresenter(@NonNull CashLogRepository repository, @NonNull ListContract.View view) {
        mRepository = checkNotNull(repository, "repository cannot be null");
        mView = checkNotNull(view, "view cannot be null");

        mCalendarFrom = Calendar.getInstance();
        mCalendar = Calendar.getInstance();
        mView.setPresenter(this);
        mListType = ListType.FOR_DAY;

        mSelectedCashLogId = -1;
    }

    @Override
    public void start() {
        setToDate(mCalendar.getTime());
    }

    @Override
    public void previous() {
        if (mFixedDate != null) {
            mCalendarFrom.setTime(mFixedDate);
            mCalendar.setTime(mFixedDate);
        }
        if (mListType == ListType.FOR_DAY) {
            mCalendar.add(Calendar.DAY_OF_MONTH, -1);
            setToDate(mCalendar.getTime());
        } else if (mListType == ListType.FOR_MONTH) {
            mCalendar.add(Calendar.MONTH, -1);
            setToDate(mCalendar.getTime());
        } else {
            long duration = mCalendar.getTimeInMillis() - mCalendarFrom.getTimeInMillis();
            int amount = Math.abs((int) TimeUnit.MILLISECONDS.toDays(duration));
            amount++;

            isNavigated = true;
            mCalendarFrom.add(Calendar.DAY_OF_MONTH, -amount);
            mCalendar.add(Calendar.DAY_OF_MONTH, -amount);
            setToDateRangeInternal(mCalendarFrom.getTime(), mCalendar.getTime());
        }
    }

    @Override
    public void today() {
        Date today = (mFixedDate == null) ? new Date() : mFixedDate;
        if (mListType == ListType.FOR_DATE_RANGE) {
            if (isNavigated) {
                setToDateRangeInternal(mMarkFrom, mMarkTo);
            } else {
                setToDateRangeInternal(today, today);
            }
        } else {
            setToDate(today);
        }
    }

    @Override
    public void next() {
        if (mFixedDate != null) {
            mCalendarFrom.setTime(mFixedDate);
            mCalendar.setTime(mFixedDate);
        }

        if (mListType == ListType.FOR_MONTH) {
            mCalendar.add(Calendar.MONTH, 1);
            setToDate(mCalendar.getTime());
        } else if (mListType == ListType.FOR_DAY) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            setToDate(mCalendar.getTime());
        } else {
            long duration = Math.abs(mCalendar.getTimeInMillis() - mCalendarFrom.getTimeInMillis());
            int amount = (int) TimeUnit.MILLISECONDS.toDays(duration);
            amount++;

            isNavigated = true;
            mCalendarFrom.add(Calendar.DAY_OF_MONTH, amount);
            mCalendar.add(Calendar.DAY_OF_MONTH, amount);
            setToDateRangeInternal(mCalendarFrom.getTime(), mCalendar.getTime());
        }
    }

    @Override
    public void setToDate(@NonNull Date date) {
        checkNotNull(date, "date cannot be null");

        mCalendar.setTime(date);

        if (mListType == ListType.FOR_DAY) {
            mView.showDate(mCalendar.getTime());
        } else if (mListType == ListType.FOR_MONTH) {
            mView.showMonth(mCalendar.getTime());
        }

        mRepository.loadByDate(date, new CashLogDataSource.LoadCashLogCallback() {
            @Override
            public void onCashLogLoaded(List<CashLog> cashLogs) {
                mView.showList(cashLogs);
            }

            @Override
            public void onDataNotAvailable() {
                mView.showNoListData();
            }
        });

        mRepository.balance(mCalendar.getTime(), new CashLogDataSource.GetBalanceForDayCallback() {
            @Override
            public void onBalanceLoaded(long incomeOnMonth, long outlayOnMonth, long balance, long outlay) {
                mView.showBalance(mCalendar.getTime(), incomeOnMonth, outlayOnMonth, balance, outlay);
            }

            @Override
            public void onError() {
                mView.showErrorBalanceLoad();
            }
        });
    }

    @Override
    public void setToDateRange(@NonNull Date from, @NonNull Date to) {
        checkNotNull(from, "from cannot be null");
        checkNotNull(to, "to cannot be null");

        mMarkFrom = from;
        mMarkTo = to;
        isNavigated = false;
        setToDateRangeInternal(from, to);
    }

    private void setToDateRangeInternal(Date from, Date to) {
        mCalendarFrom.setTime(from);
        mCalendar.setTime(to);

        mView.showDateRange(from, to);
    }

    @Override
    public void addLog() {
        mView.showAddLog(mCalendar.getTime());
    }

    @Override
    public void selectDate() {
        mView.showCalendar(mCalendar.getTime());
    }

    @Override
    public void selectCashLog(int id) {
        if (mSelectedCashLogId == id) {
            mSelectedCashLogId = -1;
            mView.hideMemo(id);
        } else {

            if (mSelectedCashLogId > -1) {
                mView.hideMemo(mSelectedCashLogId);
            }
            mSelectedCashLogId = id;
            mView.showMemo(id);
        }
    }

    @Override
    public void deleteLog(@NonNull List<CashLog> logs) {
        checkNotNull(logs, "logs cannot be null");

        mRepository.delete(logs, new CashLogDataSource.OperationCallback() {
            @Override
            public void onFinished() {
                mView.showSuccessfullyDeletedLog();
                setToDate(mCalendar.getTime());
            }

            @Override
            public void onError() {
                mView.showErrorDeleteLog();
                setToDate(mCalendar.getTime());
            }
        });
    }

    @Override
    public void setListType(@NonNull ListType type) {
        mListType = Preconditions.checkNotNull(type, "type can not be null");

        mView.showListType(type);
    }

    @NonNull
    @Override
    public ListType getListType() {
        return mListType;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setFixedDate(@NonNull Date date) {
        checkNotNull(date, "date cannot be null");

        mFixedDate = date;
    }
}
