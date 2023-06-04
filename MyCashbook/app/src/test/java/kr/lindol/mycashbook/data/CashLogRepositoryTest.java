package kr.lindol.mycashbook.data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import kr.lindol.mycashbook.data.db.CashLog;
import kr.lindol.mycashbook.data.db.CashLogDao;
import kr.lindol.mycashbook.util.AppExecutors;

@SuppressWarnings("ConstantConditions")
public class CashLogRepositoryTest {

    private CashLogDao dao;
    private CashLogDataSource.LoadCashLogCallback loadCallback;
    private CashLogDataSource.GetBalanceForDayCallback balanceForDayCallback;
    private CashLogDataSource.GetBalanceCallback balanceCallback;
    private CashLogDataSource.OperationCallback operationCallback;

    @Before
    public void setup() {
        dao = mock(CashLogDao.class);
        loadCallback = mock(CashLogDataSource.LoadCashLogCallback.class);
        balanceForDayCallback = mock(CashLogDataSource.GetBalanceForDayCallback.class);
        balanceCallback = mock(CashLogDataSource.GetBalanceCallback.class);
        operationCallback = mock(CashLogDataSource.OperationCallback.class);
    }

    @Test(expected = NullPointerException.class)
    public void loadByDateThrowNullPointerExceptionWhenDateIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDate(null, loadCallback);
    }

    @Test(expected = NullPointerException.class)
    public void loadByDateThrowNullPointerExceptionWhenCallbackIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDate(newDate(1, 1), null);
    }

    private List<CashLog> singleLogs() {
        List<CashLog> logs = new ArrayList<>();
        logs.add(new CashLog());

        return logs;
    }

    private List<CashLog> emptyLogs() {
        return new ArrayList<>();
    }

    @Test
    public void loadByDateCallOnCashLogLoaded() {
        when(dao.loadByDate("20230101")).thenReturn(singleLogs());

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDate(newDate(1, 1), loadCallback);

        verify(loadCallback, times(1)).onCashLogLoaded(any());
    }

    @Test
    public void loadByDateCallOnDataNotAvailableWhenNoData() {
        when(dao.loadByDate("20230101")).thenReturn(emptyLogs());

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDate(newDate(1, 1), loadCallback);

        verify(loadCallback, times(1)).onDataNotAvailable();
    }

    @Test(expected = NullPointerException.class)
    public void loadByMonthThrowNullPointerExceptionWhenDateIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByMonth(null, loadCallback);
    }

    @Test(expected = NullPointerException.class)
    public void loadByMonthThrowNullPointerExceptionWhenCallbackIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByMonth(newDate(1, 1), null);
    }

    @Test
    public void loadByMonthCallOnCashLogLoaded() {
        when(dao.loadByMonth("202301")).thenReturn(singleLogs());

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByMonth(newDate(1, 1), loadCallback);

        verify(loadCallback, times(1)).onCashLogLoaded(any());
    }

    @Test
    public void loadByMonthCallOnDataNotAvailableWhenNoData() {
        when(dao.loadByMonth("202301")).thenReturn(emptyLogs());

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByMonth(newDate(1, 1), loadCallback);

        verify(loadCallback, times(1)).onDataNotAvailable();
    }

    @Test(expected = NullPointerException.class)
    public void loadByDateRangeThrowNullPointerExceptionWhenFromIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDateRange(null, newDate(2, 15), loadCallback);
    }

    @Test(expected = NullPointerException.class)
    public void loadByDateRangeThrowNullPointerExceptionWhenToIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDateRange(newDate(1, 1), null, loadCallback);
    }

    @Test(expected = NullPointerException.class)
    public void loadByDateRangeThrowNullPointerExceptionWhenCallbackIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDateRange(newDate(1, 1), newDate(2, 15), null);
    }

    @Test
    public void loadByDateRangeCallOnCashLogLoaded() {
        when(dao.loadByDateRange(20230101, 20230215)).thenReturn(singleLogs());

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDateRange(newDate(1, 1), newDate(2, 15), loadCallback);

        verify(loadCallback, times(1)).onCashLogLoaded(any());
    }

    @Test
    public void loadByDateRangeCallOnDataNotAvailableWhenNoData() {
        when(dao.loadByDateRange(20230101, 20230215)).thenReturn(emptyLogs());

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.loadByDateRange(newDate(1, 1), newDate(2, 15), loadCallback);

        verify(loadCallback, times(1)).onDataNotAvailable();
    }

    @Test(expected = NullPointerException.class)
    public void balanceThrowNullPointerExceptionWhenDateIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balance(null, balanceForDayCallback);
    }

    @Test(expected = NullPointerException.class)
    public void balanceThrowNullPointerExceptionWhenCallbackIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balance(newDate(1, 1), null);
    }

    @Test
    public void balanceCallOnBalanceLoaded() {
        when(dao.sumOnMonth("202301", 0)).thenReturn(1_000L);
        when(dao.sumOnMonth("202301", 1)).thenReturn(1_500L);
        when(dao.sumOnDate("20230101", 1)).thenReturn(300L);

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balance(newDate(1, 1), balanceForDayCallback);

        verify(balanceForDayCallback, times(1))
                .onBalanceLoaded(1_000L, 1_500L, -500L, 300L);
    }

    @Test(expected = NullPointerException.class)
    public void balanceByMonthThrowNullPointerExceptionWhenDateIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balanceByMonth(null, balanceCallback);
    }

    @Test(expected = NullPointerException.class)
    public void balanceByMonthThrowNullPointerExceptionWhenCallbackIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balance(newDate(1, 1), null);
    }

    @Test
    public void balanceByMonthCallOnBalanceLoaded() {
        when(dao.sumOnMonth("202301", 0)).thenReturn(1_000L);
        when(dao.sumOnMonth("202301", 1)).thenReturn(1_500L);

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balanceByMonth(newDate(1, 1), balanceCallback);

        verify(balanceCallback, times(1))
                .onBalanceLoaded(1_000L, 1_500L, -500L);
    }

    @Test(expected = NullPointerException.class)
    public void balanceByDateRangeThrowNullPointerExceptionWhenFromIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balanceByDateRange(null, newDate(2, 15), balanceCallback);
    }

    @Test(expected = NullPointerException.class)
    public void balanceByDateRangeThrowNullPointerExceptionWhenToIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balanceByDateRange(newDate(1, 1), null, balanceCallback);
    }

    @Test(expected = NullPointerException.class)
    public void balanceByDateRangeThrowNullPointerExceptionWhenCallbackIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balanceByDateRange(newDate(1, 1), newDate(2, 15), null);
    }

    @Test
    public void balanceByDateRangeCallOnBalanceLoaded() {
        when(dao.sumOnDateRange(20230101, 20230215, 0)).thenReturn(1_000L);
        when(dao.sumOnDateRange(20230101, 20230215, 1)).thenReturn(1_500L);

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.balanceByDateRange(newDate(1, 1), newDate(2, 15), balanceCallback);

        verify(balanceCallback, times(1))
                .onBalanceLoaded(1_000L, 1_500L, -500L);
    }

    @Test(expected = NullPointerException.class)
    public void saveThrowNullPointerExceptionWhenLogIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.save(null, operationCallback);
    }

    @Test
    public void saveThenInsertLogUsingDao() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.save(new CashLog(), null);

        verify(dao, times(1)).insertAll(any(CashLog.class));
    }

    @Test
    public void saveCallOnFinishedWhenCallbackIsNotNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.save(new CashLog(), operationCallback);

        verify(operationCallback, times(1)).onFinished();
    }

    @Test
    public void saveCallOnErrorWhenCallbackIsNotNullAndDaoIsFailed() {
        doThrow(new RuntimeException("insert failed!")).when(dao).insertAll(any(CashLog.class));

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.save(new CashLog(), operationCallback);

        verify(operationCallback, times(1)).onError();
    }

    @Test(expected = NullPointerException.class)
    public void deleteThrowNullPointerExceptionWhenLogsIsNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.delete(null, operationCallback);
    }

    private List<CashLog> logs() {
        List<CashLog> logs = new ArrayList<>();
        logs.add(new CashLog());

        return logs;
    }

    @Test
    public void deleteThenDeleteUsingDao() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.delete(logs(), null);

        verify(dao, times(1)).delete(any(CashLog.class));
    }

    @Test
    public void deleteCallOnFinishedWhenCallbackIsNotNull() {
        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.delete(logs(), operationCallback);

        verify(operationCallback, times(1)).onFinished();
    }

    @Test
    public void deleteCallOnErrorWhenCallbackIsNotNullAndDaoIsFailed() {
        doThrow(new RuntimeException("deleting was failed!")).when(dao).delete(any(CashLog.class));

        CashLogRepository repository = new CashLogRepository(dao, new FakeAppExecutors());
        repository.delete(logs(), operationCallback);

        verify(operationCallback, times(1)).onError();
    }

    private Date newDate(int month, int date) {
        Calendar c = Calendar.getInstance();
        c.set(2023, month - 1, date);

        return c.getTime();
    }
}

class FakeAppExecutors extends AppExecutors {

    protected FakeAppExecutors() {
        super(new FakeExecutor(), new FakeExecutor());
    }

    static class FakeExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}