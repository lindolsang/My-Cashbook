package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.lindol.mycashbook.data.CashLogDataSource;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogListPresenter implements ListContract.Presenter {
    private final CashLogRepository mRepository;
    private final ListContract.View mView;
    private final Calendar mCalendar;
    private Date mFixedDate;
    private int mSelectedCashLogId;

    public CashLogListPresenter(@NonNull CashLogRepository repository, @NonNull ListContract.View view) {
        mRepository = checkNotNull(repository, "repository cannot be null");
        mView = checkNotNull(view, "view cannot be null");

        mCalendar = Calendar.getInstance();
        mView.setPresenter(this);

        mSelectedCashLogId = -1;
    }

    @Override
    public void start() {
        setToDate(mCalendar.getTime());
    }

    @Override
    public void yesterday() {
        if (mFixedDate != null) {
            mCalendar.setTime(mFixedDate);
        }
        mCalendar.add(Calendar.DAY_OF_MONTH, -1);
        setToDate(mCalendar.getTime());
    }

    @Override
    public void today() {
        setToDate((mFixedDate == null) ? new Date() : mFixedDate);
    }

    @Override
    public void tomorrow() {
        if (mFixedDate != null) {
            mCalendar.setTime(mFixedDate);
        }
        mCalendar.add(Calendar.DAY_OF_MONTH, 1);

        setToDate(mCalendar.getTime());
    }

    @Override
    public void setToDate(@NonNull Date date) {
        checkNotNull(date, "date cannot be null");

        mCalendar.setTime(date);
        mRepository.loadByDate(date, new CashLogDataSource.LoadCashLogCallback() {
            @Override
            public void onCashLogLoaded(List<CashLog> cashLogs) {
                mView.showDate(mCalendar.getTime());
                mView.showList(cashLogs);
            }

            @Override
            public void onDataNotAvailable() {
                mView.showDate(mCalendar.getTime());
                mView.showNoListData();
            }
        });
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

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setFixedDate(@NonNull Date date) {
        checkNotNull(date, "date cannot be null");

        mFixedDate = date;
    }
}
