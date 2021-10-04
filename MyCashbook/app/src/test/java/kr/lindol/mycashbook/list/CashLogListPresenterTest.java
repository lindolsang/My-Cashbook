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
import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogListPresenterTest {

    private CashLogRepository mRepository;
    private ListContract.View mView;

    private Date mToday;
    private Date mYesterday;
    private Date mTomorrow;

    @Before
    public void setUp() throws Exception {
        mRepository = mock(CashLogRepository.class);
        mView = mock(ListContract.View.class);

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
    public void tearDown() throws Exception {
    }

    private void mockOnCashLogLoaded() {
        doAnswer(invocation -> {
            CashLogDataSource.LoadCashLogCallback cb = invocation.getArgument(1, CashLogDataSource.LoadCashLogCallback.class);
            cb.onCashLogLoaded(new ArrayList<CashLog>());
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

        new CashLogListPresenter(mRepository, mView).today();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showList(any());
    }

    @Test
    public void todayThenUpdateViewWhenNoData() {
        mockOnDataNotAvailable();

        new CashLogListPresenter(mRepository, mView).today();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void todayThenShowDateAsToday() {
        mockOnCashLogLoaded();

        CashLogListPresenter presenter = new CashLogListPresenter(mRepository, mView);
        presenter.setFixedDate(mToday);
        presenter.today();

        verify(mView, times(1)).showDate(mToday);
    }

    @Test
    public void todayThenShowDateAsTodayWhenNoData() {
        mockOnDataNotAvailable();

        CashLogListPresenter presenter = new CashLogListPresenter(mRepository, mView);
        presenter.setFixedDate(mToday);
        presenter.today();

        verify(mView, times(1)).showDate(mToday);
    }

    @Test
    public void yesterdayThenShowDateAsYesterday() {
        mockOnCashLogLoaded();

        CashLogListPresenter presenter = new CashLogListPresenter(mRepository, mView);
        presenter.setFixedDate(mToday);
        presenter.yesterday();

        verify(mView, times(1)).showDate(mYesterday);
    }

    @Test
    public void yesterdayThenShowDateAsYesterdayWhenNoData() {
        mockOnDataNotAvailable();

        CashLogListPresenter presenter = new CashLogListPresenter(mRepository, mView);
        presenter.setFixedDate(mToday);
        presenter.yesterday();

        verify(mView, times(1)).showDate(mYesterday);
    }

    @Test
    public void tomorrowThenShowDateAsTomorrow() {
        mockOnCashLogLoaded();

        CashLogListPresenter presenter = new CashLogListPresenter(mRepository, mView);
        presenter.setFixedDate(mToday);
        presenter.tomorrow();

        verify(mView, times(1)).showDate(mTomorrow);
    }

    @Test
    public void tomorrowThenShowDateAsTomorrowWhenNoData() {
        mockOnDataNotAvailable();

        CashLogListPresenter presenter = new CashLogListPresenter(mRepository, mView);
        presenter.setFixedDate(mToday);
        presenter.tomorrow();

        verify(mView, times(1)).showDate(mTomorrow);
    }

    @Test
    public void setToDateThenShowDateAsPassedDate() {
        mockOnCashLogLoaded();

        Date specifiedDate = Date.from(Instant.parse("2021-01-02T03:04:05.00Z"));
        new CashLogListPresenter(mRepository, mView).setToDate(specifiedDate);

        verify(mView, times(1)).showDate(specifiedDate);
    }

    @Test
    public void setToDateThenShowDateAsPassedDateWhenNoData() {
        mockOnDataNotAvailable();

        Date specifiedDate = Date.from(Instant.parse("2021-01-02T03:04:05.00Z"));
        new CashLogListPresenter(mRepository, mView).setToDate(specifiedDate);

        verify(mView, times(1)).showDate(specifiedDate);
    }

    @Test
    public void yesterdayThenUpdateView() {
        mockOnCashLogLoaded();

        new CashLogListPresenter(mRepository, mView).yesterday();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showList(any());
    }

    @Test
    public void yesterdayThenUpdateViewWhenNoData() {
        mockOnDataNotAvailable();

        new CashLogListPresenter(mRepository, mView).yesterday();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void tomorrowThenUpdateView() {
        mockOnCashLogLoaded();

        new CashLogListPresenter(mRepository, mView).tomorrow();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showList(any());
    }

    @Test
    public void tomorrowThenUpdateViewWhenNoData() {
        mockOnDataNotAvailable();

        new CashLogListPresenter(mRepository, mView).tomorrow();

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showNoListData();
    }

    @Test
    public void setToDateThenUpdateView() {
        mockOnCashLogLoaded();

        new CashLogListPresenter(mRepository, mView).setToDate(new Date());

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showList(any());
    }

    @Test
    public void setToDateThenUpdateViewWhenNoData() {
        mockOnDataNotAvailable();

        new CashLogListPresenter(mRepository, mView).setToDate(new Date());

        verify(mView, times(1)).hideDeleteButton();
        verify(mView, times(1)).hideSelectionBox();
        verify(mView, times(1)).showDate(any());
        verify(mView, times(1)).showNoListData();
    }
}