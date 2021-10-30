package kr.lindol.mycashbook.list;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.lindol.mycashbook.data.CashLogDataSource;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogListPresenterTest {

    private CashLogRepository mRepository;
    private ListContract.View mView;

    private Date mToday;
    private Date mYesterday;
    private Date mTomorrow;

    private CashLogListPresenter presenter;

    private List<CashLog> DELETE_LOGS;

    @Before
    public void setUp() {
        mRepository = mock(CashLogRepository.class);
        mView = mock(ListContract.View.class);
        presenter = new CashLogListPresenter(mRepository, mView);

        DELETE_LOGS = new ArrayList<>();
        DELETE_LOGS.add(new CashLog());

        mToday = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(mToday);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        mYesterday = cal.getTime();

        cal.setTime(mToday);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        mTomorrow = cal.getTime();
    }

    @After
    public void tearDown() {
    }

    private void mockOnCashLogLoaded() {
        doAnswer(invocation -> {
            CashLogDataSource.LoadCashLogCallback cb = invocation.getArgument(1, CashLogDataSource.LoadCashLogCallback.class);
            cb.onCashLogLoaded(new ArrayList<>());
            return null;
        }).when(mRepository).loadByDate(any(), any());
    }

    private void mockOnDataNotAvailable() {
        doAnswer(invocation -> {
            CashLogDataSource.LoadCashLogCallback cb = invocation.getArgument(1, CashLogDataSource.LoadCashLogCallback.class);
            cb.onDataNotAvailable();
            return null;
        }).when(mRepository).loadByDate(any(), any());
    }

    @Test
    public void todayThenShowList() {
        mockOnCashLogLoaded();

        presenter.today();

        verify(mView, times(1)).showList(any());
    }

    @Test
    public void todayThenShowNoListDataWhenNoData() {
        mockOnDataNotAvailable();

        presenter.today();

        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void todayThenShowDateAsToday() {
        mockOnCashLogLoaded();

        presenter.setFixedDate(mToday);
        presenter.today();

        verify(mView, times(1)).showDate(mToday);
    }

    @Test
    public void todayThenShowDateAsTodayWhenNoData() {
        mockOnDataNotAvailable();

        presenter.setFixedDate(mToday);
        presenter.today();

        verify(mView, times(1)).showDate(mToday);
    }

    @Test
    public void yesterdayThenShowDateAsYesterday() {
        mockOnCashLogLoaded();

        presenter.setFixedDate(mToday);
        presenter.yesterday();

        verify(mView, times(1)).showDate(mYesterday);
    }

    @Test
    public void yesterdayThenShowDateAsYesterdayWhenNoData() {
        mockOnDataNotAvailable();

        presenter.setFixedDate(mToday);
        presenter.yesterday();

        verify(mView, times(1)).showDate(mYesterday);
    }

    @Test
    public void tomorrowThenShowDateAsTomorrow() {
        mockOnCashLogLoaded();

        presenter.setFixedDate(mToday);
        presenter.tomorrow();

        verify(mView, times(1)).showDate(mTomorrow);
    }

    @Test
    public void tomorrowThenShowDateAsTomorrowWhenNoData() {
        mockOnDataNotAvailable();

        presenter.setFixedDate(mToday);
        presenter.tomorrow();

        verify(mView, times(1)).showDate(mTomorrow);
    }

    @Test
    public void setToDateThenShowDateAsPassedDate() {
        mockOnCashLogLoaded();

        Date specifiedDate = Date.from(Instant.parse("2021-01-02T03:04:05.00Z"));
        presenter.setToDate(specifiedDate);

        verify(mView, times(1)).showDate(specifiedDate);
    }

    @Test
    public void setToDateThenShowDateAsPassedDateWhenNoData() {
        mockOnDataNotAvailable();

        Date specifiedDate = Date.from(Instant.parse("2021-01-02T03:04:05.00Z"));
        presenter.setToDate(specifiedDate);

        verify(mView, times(1)).showDate(specifiedDate);
    }

    @Test
    public void yesterdayThenShowList() {
        mockOnCashLogLoaded();

        presenter.yesterday();

        verify(mView, times(1)).showList(any());
    }

    @Test
    public void yesterdayThenShowNoListDataWhenNoData() {
        mockOnDataNotAvailable();

        presenter.yesterday();

        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void tomorrowThenShowList() {
        mockOnCashLogLoaded();

        presenter.tomorrow();

        verify(mView, times(1)).showList(any());
    }

    @Test
    public void tomorrowThenShowNoListDataWhenNoData() {
        mockOnDataNotAvailable();

        presenter.tomorrow();

        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void setToDateThenShowList() {
        mockOnCashLogLoaded();

        presenter.setToDate(new Date());

        verify(mView, times(1)).showList(any());
    }

    @Test
    public void setToDateThenShowNoListDataWhenNoData() {
        mockOnDataNotAvailable();

        presenter.setToDate(new Date());

        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void selectCashLogThenShowMemo() {
        presenter.selectCashLog(1);

        verify(mView, times(1)).showMemo(1);
    }

    @Test
    public void selectCashLogThenHideMemoWhenSelectedBefore() {
        presenter.selectCashLog(1);
        presenter.selectCashLog(1);

        verify(mView, times(1)).showMemo(1);
        verify(mView, times(1)).hideMemo(1);
    }

    @Test
    public void selectCashLogThenShowMemoWhenSelectedAfterTwoTimes() {
        presenter.selectCashLog(1);
        presenter.selectCashLog(1);
        presenter.selectCashLog(1);

        verify(mView, times(2)).showMemo(1);
        verify(mView, times(1)).hideMemo(1);
    }

    @Test
    public void selectCashLogThenShowMemoAndHideSelectedMemo() {
        presenter.selectCashLog(1);
        presenter.selectCashLog(2);

        verify(mView, times(1)).showMemo(1);
        verify(mView, times(1)).hideMemo(1);
        verify(mView, times(1)).showMemo(2);
    }

    @Test
    public void addLogThenShowAddLog() {
        presenter.addLog();

        verify(mView, times(1)).showAddLog(any(Date.class));
    }

    @Test
    public void startThenShowList() {
        mockOnCashLogLoaded();

        presenter.start();

        verify(mView, times(1)).showList(any());
    }

    @Test
    public void startThenShowNoListDataWhenDataIsNotAvailable() {
        mockOnDataNotAvailable();

        presenter.start();

        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void selectCalendarThenShowCalendar() {
        presenter.selectDate();

        verify(mView, times(1)).showCalendar(any(Date.class));
    }

    private void mockDeletedSuccessfully() {
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();
            return null;
        }).when(mRepository).delete(any(), any());
    }

    private void mockDeleteFailed() {
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onError();
            return null;
        }).when(mRepository).delete(any(), any());
    }

    @Test
    public void deleteLogAndShowSuccessfullyDeletedLog() {
        mockDeletedSuccessfully();

        presenter.deleteLog(DELETE_LOGS);

        verify(mView, times(1)).showSuccessfullyDeletedLog();
    }

    @Test
    public void deleteLogAndShowErrorDeleteLogWhenErrorOccurred() {
        mockDeleteFailed();

        presenter.deleteLog(DELETE_LOGS);

        verify(mView, times(1)).showErrorDeleteLog();
    }

    @Test
    public void deleteLogAndLoadLogsIntoUi() {
        mockDeletedSuccessfully();
        mockOnCashLogLoaded();

        presenter.deleteLog(DELETE_LOGS);

        verify(mView, times(1)).showList(any());
    }

    @Test
    public void deleteLogAndShowNoDataUiWhenNoLog() {
        mockDeletedSuccessfully();
        mockOnDataNotAvailable();

        presenter.deleteLog(DELETE_LOGS);

        verify(mView, times(1)).showNoListData();
    }
}