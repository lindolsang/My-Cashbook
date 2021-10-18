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

import kr.lindol.mycashbook.data.CashLogDataSource;
import kr.lindol.mycashbook.data.CashLogRepository;

public class CashLogListPresenterTest {

    private CashLogRepository mRepository;
    private ListContract.View mView;

    private Date mToday;
    private Date mYesterday;
    private Date mTomorrow;

    private CashLogListPresenter presenter;

    @Before
    public void setUp() {
        mRepository = mock(CashLogRepository.class);
        mView = mock(ListContract.View.class);
        presenter = new CashLogListPresenter(mRepository, mView);

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
    public void todayThenUpdateView() {
        mockOnCashLogLoaded();

        presenter.today();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showList(any());
    }

    @Test
    public void todayThenUpdateViewWhenNoData() {
        mockOnDataNotAvailable();

        presenter.today();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
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
    public void yesterdayThenUpdateView() {
        mockOnCashLogLoaded();

        presenter.yesterday();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showList(any());
    }

    @Test
    public void yesterdayThenUpdateViewWhenNoData() {
        mockOnDataNotAvailable();

        presenter.yesterday();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void tomorrowThenUpdateView() {
        mockOnCashLogLoaded();

        presenter.tomorrow();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showList(any());
    }

    @Test
    public void tomorrowThenUpdateViewWhenNoData() {
        mockOnDataNotAvailable();

        presenter.tomorrow();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void setToDateThenUpdateView() {
        mockOnCashLogLoaded();

        presenter.setToDate(new Date());

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showList(any());
    }

    @Test
    public void setToDateThenUpdateViewWhenNoData() {
        mockOnDataNotAvailable();

        presenter.setToDate(new Date());

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
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
    public void selectCashLogThenShowMemoWhenSelected2TimesAfter() {
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
    public void startThenNoShowListDataWhenDataIsNotAvailable() {
        mockOnDataNotAvailable();

        presenter.start();

        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void openCalendarThenShowCalendar() {
        presenter.openCalendar();

        verify(mView, times(1)).showCalendar(any(Date.class));
    }
}