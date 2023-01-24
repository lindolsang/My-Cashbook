package kr.lindol.mycashbook.list;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

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

    private Date mLastMonth;
    private Date mNextMonth;

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

        cal.setTime(mToday);
        cal.add(Calendar.MONTH, -1);
        mLastMonth = cal.getTime();

        cal.setTime(mToday);
        cal.add(Calendar.MONTH, 1);
        mNextMonth = cal.getTime();
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
        presenter.setFixedDate(mToday);
        presenter.today();

        verify(mView, times(1)).showDate(mToday);
    }

    @Test
    public void previousThenShowDateAsYesterday() {
        presenter.setFixedDate(mToday);
        presenter.previous();

        verify(mView, times(1)).showDate(mYesterday);
    }

    @Test
    public void nextThenShowDateAsTomorrow() {
        presenter.setFixedDate(mToday);
        presenter.next();

        verify(mView, times(1)).showDate(mTomorrow);
    }

    @Test
    public void setToDateThenShowDateAsPassedDate() {
        Date specifiedDate = Date.from(Instant.parse("2021-01-02T03:04:05.00Z"));
        presenter.setToDate(specifiedDate);

        verify(mView, times(1)).showDate(specifiedDate);
    }

    @Test
    public void previousThenShowList() {
        mockOnCashLogLoaded();

        presenter.previous();

        verify(mView, times(1)).showList(any());
    }

    @Test
    public void previousThenShowNoListDataWhenNoData() {
        mockOnDataNotAvailable();

        presenter.previous();

        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void nextThenShowList() {
        mockOnCashLogLoaded();

        presenter.next();

        verify(mView, times(1)).showList(any());
    }

    @Test
    public void nextThenShowNoListDataWhenNoData() {
        mockOnDataNotAvailable();

        presenter.next();

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

    private void mockBalanceLoaded() {
        doAnswer(invocation -> {
            CashLogDataSource.GetBalanceForDayCallback cb = invocation.getArgument(1);
            cb.onBalanceLoaded(1, 0, 0, 0);
            return null;
        }).when(mRepository).balance(any(Date.class), any());
    }

    private void mockBalanceLoadFailed() {
        doAnswer(invocation -> {
            CashLogDataSource.GetBalanceForDayCallback cb = invocation.getArgument(1);
            cb.onError();
            return null;
        }).when(mRepository).balance(any(Date.class), any());
    }

    @Test
    public void setToDateThenShowBalance() {
        mockBalanceLoaded();

        presenter.setToDate(new Date());

        verify(mView, times(1)).showBalance(any(Date.class), anyLong(), anyLong(), anyLong(), anyLong());
    }

    @Test
    public void setToDateThenShowErrorBalanceLoadWhenBalanceLoadFailed() {
        mockBalanceLoadFailed();

        presenter.setToDate(new Date());

        verify(mView, times(1)).showErrorBalanceLoad();
    }

    @Test
    public void previousThenShowBalance() {
        mockBalanceLoaded();

        presenter.previous();

        verify(mView, times(1)).showBalance(any(Date.class), anyLong(), anyLong(), anyLong(), anyLong());
    }

    @Test
    public void previousThenShowErrorBalanceLoadWhenBalanceLoadFailed() {
        mockBalanceLoadFailed();

        presenter.previous();

        verify(mView, times(1)).showErrorBalanceLoad();
    }

    @Test
    public void todayThenShowBalance() {
        mockBalanceLoaded();

        presenter.today();

        verify(mView, times(1)).showBalance(any(Date.class), anyLong(), anyLong(), anyLong(), anyLong());
    }

    @Test
    public void todayThenShowErrorBalanceLoadWhenBalanceLoadFailed() {
        mockBalanceLoadFailed();

        presenter.today();

        verify(mView, times(1)).showErrorBalanceLoad();
    }

    @Test
    public void nextThenShowBalance() {
        mockBalanceLoaded();

        presenter.next();

        verify(mView, times(1)).showBalance(any(Date.class), anyLong(), anyLong(), anyLong(), anyLong());
    }

    @Test
    public void nextThenShowErrorBalanceLoadWhenBalanceLoadFailed() {
        mockBalanceLoadFailed();

        presenter.next();

        verify(mView, times(1)).showErrorBalanceLoad();
    }

    @Test
    public void getListTypeThenReturnDefaultTypeForDay() {
        assertThat(presenter.getListType(), equalTo(ListType.FOR_DAY));
    }

    @Test
    public void setListTypeForDay() {
        presenter.setListType(ListType.FOR_DAY);

        assertThat(presenter.getListType(), equalTo(ListType.FOR_DAY));
    }

    @Test
    public void setListTypeForMonth() {
        presenter.setListType(ListType.FOR_MONTH);

        assertThat(presenter.getListType(), equalTo(ListType.FOR_MONTH));
    }

    @Test
    public void setListTypeForDateRange() {
        presenter.setListType(ListType.FOR_DATE_RANGE);

        assertThat(presenter.getListType(), equalTo(ListType.FOR_DATE_RANGE));
    }

    @Test
    public void setListTypeWithForDayThenShowListTypeWithForDay() {
        presenter.setListType(ListType.FOR_DAY);

        verify(mView, times(1)).showListType(ListType.FOR_DAY);
    }

    @Test
    public void setListTypeWithForMonthThenShowListTypeWithForMonth() {
        presenter.setListType(ListType.FOR_MONTH);

        verify(mView, times(1)).showListType(ListType.FOR_MONTH);
    }

    @Test
    public void setListTypeWithForDateRangeThenShowListTypeWithForDateRange() {
        presenter.setListType(ListType.FOR_DATE_RANGE);

        verify(mView, times(1)).showListType(ListType.FOR_DATE_RANGE);
    }

    @Test
    public void todayThenShowMonthAsThisMonth() {
        presenter.setListType(ListType.FOR_MONTH);
        presenter.setFixedDate(mToday);
        presenter.today();

        verify(mView, times(1)).showMonth(mToday);
    }

    @Test
    public void previousThenShowMonthAsLastMonth() {
        presenter.setListType(ListType.FOR_MONTH);
        presenter.setFixedDate(mToday);
        presenter.previous();

        verify(mView, times(1)).showMonth(mLastMonth);
    }

    @Test
    public void nextThenShowMonthAsNextMonth() {
        presenter.setListType(ListType.FOR_MONTH);
        presenter.setFixedDate(mToday);
        presenter.next();

        verify(mView, times(1)).showMonth(mNextMonth);
    }

    @Test
    public void setToDateThenShowMonthAsPassedDate() {
        Date specifiedDate = Date.from(Instant.parse("2021-01-02T03:04:05.00Z"));

        presenter.setListType(ListType.FOR_MONTH);
        presenter.setToDate(specifiedDate);

        verify(mView, times(1)).showMonth(specifiedDate);
    }

    @Test
    public void todayThenShowDateRangeAsTodayWhenNotToSetDateRange() {
        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setFixedDate(mToday);
        presenter.today();

        verify(mView, times(1)).showDateRange(mToday, mToday);
    }

    @Test
    public void todayThenShowDateRangeAsTodayWhenNotToCallPreviousOrNext() {
        Date fromDate = Date.from(Instant.parse("2022-11-30T03:04:05.00Z"));
        Date toDate = Date.from(Instant.parse("2022-12-31T03:04:05.00Z"));

        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setFixedDate(mToday);
        presenter.setToDateRange(fromDate, toDate);
        presenter.today();

        InOrder inOrder = Mockito.inOrder(mView);
        inOrder.verify(mView).showDateRange(fromDate, toDate);
        inOrder.verify(mView).showDateRange(mToday, mToday);
    }

    @Test
    public void todayThenShowDateRangeAsPassedDateAfterPrevious() {
        Date fromDate = Date.from(Instant.parse("2022-11-30T03:04:05.00Z"));
        Date toDate = Date.from(Instant.parse("2022-12-01T03:04:05.00Z"));

        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setToDateRange(fromDate, toDate); // 1
        presenter.previous();
        presenter.today(); // 2

        Date previousFrom = addDays(-2, fromDate);
        Date previousTo = addDays(-2, toDate);

        InOrder inOrder = Mockito.inOrder(mView);
        inOrder.verify(mView).showDateRange(fromDate, toDate);
        inOrder.verify(mView).showDateRange(previousFrom, previousTo);
        inOrder.verify(mView).showDateRange(fromDate, toDate);
    }

    @Test
    public void todayThenShowDateRangeAsPassedDateAfterNext() {
        Date fromDate = Date.from(Instant.parse("2022-11-30T03:04:05.00Z"));
        Date toDate = Date.from(Instant.parse("2022-12-01T03:04:05.00Z"));

        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setToDateRange(fromDate, toDate); // 1
        presenter.next();
        presenter.today(); // 2

        Date nextFrom = addDays(2, fromDate);
        Date nextTo = addDays(2, toDate);

        InOrder inOrder = Mockito.inOrder(mView);
        inOrder.verify(mView).showDateRange(fromDate, toDate);
        inOrder.verify(mView).showDateRange(nextFrom, nextTo);
        inOrder.verify(mView).showDateRange(fromDate, toDate);
    }

    @Test
    public void previousThenShowDateRangeAsYesterdayWhenNotToSetDateRange() {
        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setFixedDate(mToday);
        presenter.previous();

        verify(mView, times(1)).showDateRange(mYesterday, mYesterday);
    }

    @Test
    public void previousThenShowDateRangeAsPreviousDateRange() {
        Date fromDate = Date.from(Instant.parse("2022-11-30T03:04:05.00Z"));
        Date toDate = Date.from(Instant.parse("2022-12-01T03:04:05.00Z"));

        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setToDateRange(fromDate, toDate);
        presenter.previous();

        Date previousFrom = addDays(-2, fromDate);
        Date previousTo = addDays(-2, toDate);
        verify(mView, times(1)).showDateRange(previousFrom, previousTo);
    }

    @Test
    public void nextThenShowDateRangeAsTomorrowWhenNotToSetDateRange() {
        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setFixedDate(mToday);
        presenter.next();

        verify(mView, times(1)).showDateRange(mTomorrow, mTomorrow);
    }

    @Test
    public void nextThenShowDateRangeAsNextDateRange() {
        Date fromDate = Date.from(Instant.parse("2022-11-30T03:04:05.00Z"));
        Date toDate = Date.from(Instant.parse("2022-12-01T03:04:05.00Z"));

        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setToDateRange(fromDate, toDate);
        presenter.next();

        Date nextFrom = addDays(2, fromDate);
        Date nextTo = addDays(2, toDate);
        verify(mView, times(1)).showDateRange(nextFrom, nextTo);
    }

    @Test
    public void setToDateRangeThenShowDateRangeAsPassedDate() {
        Date fromDate = Date.from(Instant.parse("2022-11-30T03:04:05.00Z"));
        Date toDate = Date.from(Instant.parse("2022-12-01T03:04:05.00Z"));

        presenter.setListType(ListType.FOR_DATE_RANGE);
        presenter.setToDateRange(fromDate, toDate);

        verify(mView, times(1)).showDateRange(fromDate, toDate);
    }

    private Date addDays(int plus, Date from) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        cal.add(Calendar.DAY_OF_MONTH, plus);

        return cal.getTime();
    }
}