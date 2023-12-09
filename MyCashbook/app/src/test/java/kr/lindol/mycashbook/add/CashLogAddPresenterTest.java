package kr.lindol.mycashbook.add;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Calendar;
import java.util.Date;

import kr.lindol.mycashbook.data.CashLogDataSource;
import kr.lindol.mycashbook.data.CashLogRepository;
import kr.lindol.mycashbook.data.db.CashLog;
import kr.lindol.mycashbook.data.db.CashType;

public class CashLogAddPresenterTest {
    private CashLogRepository repository;
    private AddContract.View view;
    private ArgumentCaptor<CashLog> argumentCaptor;

    private CashLogAddPresenter presenter;

    private Date mFixedDate = new Date();

    @Before
    public void setUp() {
        repository = mock(CashLogRepository.class);
        view = mock(AddContract.View.class);

        argumentCaptor = ArgumentCaptor.forClass(CashLog.class);

        presenter = new CashLogAddPresenter(repository, view, mFixedDate, 0);
    }

    private void setRepositorySaveOk() {
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();

            return null;
        }).when(repository).save(any(), any());
    }

    @Test
    public void addAsIncomeThenSuccess() {
        setRepositorySaveOk();

        presenter.addAsIncome("Salary", "1000", "description");

        verify(view, times(1)).showSuccess();
    }

    @Test
    public void addAsIncomeThenSuccessWithEditWhenEditMode() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                10);

        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);
            CashLog cashLog = new CashLog();
            cashLog.id = 10;
            cashLog.item = "Salary";
            cashLog.type = CashType.INCOME;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);
            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();
            return null;
        }).when(repository)
                .update(any(CashLog.class), any(CashLogDataSource.OperationCallback.class));
        presenter.start();

        presenter.addAsIncome("Salary", "1000", "description");

        verify(view, times(1)).showSuccessWithEdit();
    }

    @Test
    public void addAsExpenseThenSuccess() {
        setRepositorySaveOk();

        presenter.addAsExpense("Ice cream", "1000", "description");

        verify(view, times(1)).showSuccess();
    }

    @Test
    public void addAsExpenseThenSuccessWithEditWhenEditMode() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                11);
        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);

            CashLog cashLog = new CashLog();
            cashLog.id = 11;
            cashLog.item = "Ice cream";
            cashLog.type = CashType.EXPENSE;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);
            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();
            return null;
        }).when(repository)
                .update(any(CashLog.class), any(CashLogDataSource.OperationCallback.class));
        presenter.start();

        presenter.addAsExpense("Ice cream", "1000", "description");

        verify(view, times(1)).showSuccessWithEdit();
    }

    private Date date(int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, date);

        return c.getTime();
    }

    @Test
    public void addAsExpenseWithSpecifiedDateThenSave() {
        presenter.setToDate(date(2021, 1, 1));
        presenter.addAsExpense("Ice cream", "1000", "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        CashLog cashLog = argumentCaptor.getValue();
        assertThat(cashLog.item, equalTo("Ice cream"));
        assertThat(cashLog.type, equalTo(CashType.EXPENSE));
        assertThat(cashLog.amount, equalTo(1000));
        assertThat(cashLog.dateTag, equalTo(20210101));
        assertThat(cashLog.dayTag, equalTo("20210101"));
        assertThat(cashLog.monthTag, equalTo("202101"));
        assertThat(cashLog.description, equalTo("description"));
    }

    @Test
    public void addAsExpenseWithSpecifiedDateThenUpdateWhenCashLogIsLoaded() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                11);
        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);

            CashLog cashLog = new CashLog();
            cashLog.id = 11;
            cashLog.item = "Ice cream";
            cashLog.type = CashType.EXPENSE;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);

            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
        presenter.start();

        presenter.addAsExpense("Ice cream", "1000", "description");

        verify(repository, times(1)).update(argumentCaptor.capture(), any());
        CashLog cashLog = argumentCaptor.getValue();
        assertThat(cashLog.id, equalTo(11));
        assertThat(cashLog.item, equalTo("Ice cream"));
        assertThat(cashLog.type, equalTo(CashType.EXPENSE));
        assertThat(cashLog.amount, equalTo(1000));
        assertThat(cashLog.dateTag, equalTo(20210101));
        assertThat(cashLog.dayTag, equalTo("20210101"));
        assertThat(cashLog.monthTag, equalTo("202101"));
        assertThat(cashLog.description, equalTo("description"));
    }

    @Test
    public void addAsIncomeWithSpecifiedDateThenSave() {
        presenter.setToDate(date(2021, 1, 1));
        presenter.addAsIncome("Salary", "1000", "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        CashLog cashLog = argumentCaptor.getValue();
        assertThat(cashLog.item, equalTo("Salary"));
        assertThat(cashLog.type, equalTo(CashType.INCOME));
        assertThat(cashLog.amount, equalTo(1000));
        assertThat(cashLog.dateTag, equalTo(20210101));
        assertThat(cashLog.dayTag, equalTo("20210101"));
        assertThat(cashLog.monthTag, equalTo("202101"));
        assertThat(cashLog.description, equalTo("description"));
    }

    @Test
    public void addAsIncomeWithSpecifiedDateThenUpdateWhenCashLogIsLoaded() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                10);
        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);

            CashLog cashLog = new CashLog();
            cashLog.id = 10;
            cashLog.item = "Salary";
            cashLog.type = CashType.INCOME;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);

            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
        presenter.start();

        presenter.addAsIncome("Salary", "1000", "description");

        verify(repository, times(1))
                .update(argumentCaptor.capture(), any());
        CashLog cashLog = argumentCaptor.getValue();
        assertThat(cashLog.id, equalTo(10));
        assertThat(cashLog.item, equalTo("Salary"));
        assertThat(cashLog.type, equalTo(CashType.INCOME));
        assertThat(cashLog.amount, equalTo(1000));
        assertThat(cashLog.dateTag, equalTo(20210101));
        assertThat(cashLog.dayTag, equalTo("20210101"));
        assertThat(cashLog.monthTag, equalTo("202101"));
        assertThat(cashLog.description, equalTo("description"));
    }

    @Test
    public void addAsIncomeThenCloseWindowWhenEditMode() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                10);

        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);
            CashLog cashLog = new CashLog();
            cashLog.id = 10;
            cashLog.item = "Salary";
            cashLog.type = CashType.INCOME;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);
            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();
            return null;
        }).when(repository)
                .update(any(CashLog.class), any(CashLogDataSource.OperationCallback.class));
        presenter.start();

        presenter.addAsIncome("Salary", "1000", "description");

        verify(view, times(1)).closeWindow();
    }

    @Test
    public void addAsExpenseThenCloseWindowWhenEditMode() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                11);
        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);

            CashLog cashLog = new CashLog();
            cashLog.id = 11;
            cashLog.item = "Ice cream";
            cashLog.type = CashType.EXPENSE;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);
            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();
            return null;
        }).when(repository)
                .update(any(CashLog.class), any(CashLogDataSource.OperationCallback.class));
        presenter.start();

        presenter.addAsExpense("Ice cream", "1000", "description");

        verify(view, times(1)).closeWindow();
    }

    @Test
    public void addAsIncomeThenNotToClearFormsWhenEditMode() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                10);

        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);
            CashLog cashLog = new CashLog();
            cashLog.id = 10;
            cashLog.item = "Salary";
            cashLog.type = CashType.INCOME;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);
            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();
            return null;
        }).when(repository)
                .update(any(CashLog.class), any(CashLogDataSource.OperationCallback.class));
        presenter.start();

        presenter.addAsIncome("Salary", "1000", "description");

        verify(view, times(0)).clearForms();
    }

    @Test
    public void addAsExpenseThenNotToClearFormsWhenEditMode() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                11);
        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);

            CashLog cashLog = new CashLog();
            cashLog.id = 11;
            cashLog.item = "Ice cream";
            cashLog.type = CashType.EXPENSE;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);
            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();
            return null;
        }).when(repository)
                .update(any(CashLog.class), any(CashLogDataSource.OperationCallback.class));
        presenter.start();

        presenter.addAsExpense("Ice cream", "1000", "description");

        verify(view, times(0)).clearForms();
    }

    @Test
    public void addAsIncomeThenClearForms() {
        setRepositorySaveOk();

        presenter.addAsIncome("Salary", "1000", "description");

        verify(view, times(1)).clearForms();
    }

    @Test
    public void addAsExpenseThenClearForms() {
        setRepositorySaveOk();

        presenter.addAsExpense("Ice cream", "1000", "description");

        verify(view, times(1)).clearForms();
    }

    @Test
    public void addAsIncomeWithEmptyItemThenShowItemValueEmptyError() {
        presenter.addAsIncome("", "1000", "description");

        verify(view, times(1)).showItemValueEmptyError();
    }

    @Test
    public void addAsIncomeWithEmptyAmountThenShowAmountValueEmptyError() {
        presenter.addAsIncome("Salary", "", "description");

        verify(view, times(1)).showAmountValueEmptyError();
    }

    @Test
    public void addAsIncomeWithZeroAmountThenShowAmountValueSmallError() {
        presenter.addAsIncome("Salary", "0", "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsIncomeWithMinusAmountThenShowAmountValueSmallError() {
        presenter.addAsIncome("Salary", "-1", "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsIncomeWithNonNumberAmountThenShowAmountValueFormatError() {
        presenter.addAsIncome("Salary", "10.0", "description");

        verify(view, times(1)).showAmountValueFormatError();
    }

    @Test
    public void addAsExpenseWithEmptyItemThenShowItemValueEmptyError() {
        presenter.addAsExpense("", "1000", "description");

        verify(view, times(1)).showItemValueEmptyError();
    }

    @Test
    public void addAsExpenseWithEmptyAmountThenShowAmountValueEmptyError() {
        presenter.addAsExpense("Ice cream", "", "description");

        verify(view, times(1)).showAmountValueEmptyError();
    }

    @Test
    public void addAsExpenseWithZeroAmountThenShowAmountValueSmallError() {
        presenter.addAsExpense("Ice cream", "0", "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsExpenseWithMinusAmountThenShowAmountValueSmallError() {
        presenter.addAsExpense("Ice cream", "-1", "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsExpenseWithNonNumberAmountThenShowAmountValueFormatError() {
        presenter.addAsExpense("Ice cream", "10.0", "description");

        verify(view, times(1)).showAmountValueFormatError();
    }

    @Test
    public void setToDateThenShowDate() {
        Date specifiedDate = new Date();
        presenter.setToDate(specifiedDate);

        verify(view, times(1)).showDate(specifiedDate);
    }

    @Test(expected = NullPointerException.class)
    public void setToDateThenThrowNullPointerException() {
        presenter.setToDate(null);

        verify(view, times(1)).showDate(any(Date.class));
    }

    @Test
    public void selectDateThenShowCalender() {
        presenter.selectDate();

        verify(view, times(1)).showCalendar(mFixedDate);
    }

    @Test
    public void startThenShowDate() {
        presenter.start();

        verify(view, times(1)).showDate(mFixedDate);
    }

    @Test
    public void startThenLoadDataByIdWhenEditMode() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                10);
        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);
            CashLog cashLog = new CashLog();
            cashLog.id = 10;
            cashLog.item = "Salary";
            cashLog.type = CashType.INCOME;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);
            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));

        presenter.start();

        verify(repository, times(1))
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
    }

    @Test
    public void startThenSkipLoadDataByIdWhenEditModeDataLoaded() {
        presenter = new CashLogAddPresenter(
                repository,
                view,
                date(2021, 1, 1),
                10);
        doAnswer(invocation -> {
            CashLogDataSource.LoadSingleCashLogCallback cb = invocation.getArgument(1);
            CashLog cashLog = new CashLog();
            cashLog.id = 10;
            cashLog.item = "Salary";
            cashLog.type = CashType.INCOME;
            cashLog.amount = 1000;
            cashLog.dateTag = 20210101;
            cashLog.dayTag = "20210101";
            cashLog.monthTag = "202101";
            cashLog.description = "description";

            cb.onCashLogLoaded(cashLog);
            return null;
        }).when(repository)
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));

        presenter.start();
        presenter.start();

        verify(repository, times(1))
                .loadById(anyInt(), any(CashLogDataSource.LoadSingleCashLogCallback.class));
    }
}