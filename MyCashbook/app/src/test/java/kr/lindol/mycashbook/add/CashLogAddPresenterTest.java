package kr.lindol.mycashbook.add;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

public class CashLogAddPresenterTest {

    private CashLogRepository repository;
    private AddContract.View view;
    private ArgumentCaptor<CashLog> argumentCaptor;

    private CashLogAddPresenter presenter;

    @Before
    public void setUp() {
        repository = mock(CashLogRepository.class);
        view = mock(AddContract.View.class);

        argumentCaptor = ArgumentCaptor.forClass(CashLog.class);

        presenter = new CashLogAddPresenter(repository, view);
    }

    private void mockSuccess() {
        doAnswer(invocation -> {
            CashLogDataSource.OperationCallback cb = invocation.getArgument(1);
            cb.onFinished();

            return null;
        }).when(repository).save(any(), any());
    }

    @Test
    public void addAsIncomeThenSuccess() {
        mockSuccess();

        presenter.addAsIncome("Salary", "1000", new Date(), "description");

        verify(view, times(1)).showSuccess();
    }

    private Date date(int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, date);

        return c.getTime();
    }

    @Test
    public void addAsExpenseThenSuccess() {
        mockSuccess();

        presenter.addAsExpense("Ice cream", "1000", new Date(), "description");

        verify(view, times(1)).showSuccess();
    }

    @Test
    public void addAsExpenseWithSpecifiedDateThenSave() {
        presenter.addAsExpense("Ice cream", "1000", date(2021, 1, 1), "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        CashLog cashLog = argumentCaptor.getValue();
        assertThat(cashLog.item, equalTo("Ice cream"));
        assertThat(cashLog.type, equalTo(1));
        assertThat(cashLog.amount, equalTo(1000));
        assertThat(cashLog.dateTag, equalTo(20210101));
        assertThat(cashLog.dayTag, equalTo("20210101"));
        assertThat(cashLog.monthTag, equalTo("202101"));
        assertThat(cashLog.description, equalTo("description"));
    }

    @Test
    public void addAsIncomeWithSpecifiedDateThenSave() {
        presenter.addAsIncome("Salary", "1000", date(2021, 1, 1), "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        CashLog cashLog = argumentCaptor.getValue();
        assertThat(cashLog.item, equalTo("Salary"));
        assertThat(cashLog.type, equalTo(0));
        assertThat(cashLog.amount, equalTo(1000));
        assertThat(cashLog.dateTag, equalTo(20210101));
        assertThat(cashLog.dayTag, equalTo("20210101"));
        assertThat(cashLog.monthTag, equalTo("202101"));
        assertThat(cashLog.description, equalTo("description"));
    }

    @Test
    public void addAsExpenseThenSaveWithType1() {
        presenter.addAsExpense("Ice cream", "1000", new Date(), "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        assertThat(argumentCaptor.getValue().type, equalTo(1));
    }

    @Test
    public void addAsIncomeThenSaveWithType0() {
        presenter.addAsIncome("Salary", "1000", new Date(), "description");

        verify(repository, times(1)).save(argumentCaptor.capture(), any());
        assertThat(argumentCaptor.getValue().type, equalTo(0));
    }

    @Test
    public void addAsIncomeThenClearForms() {
        mockSuccess();

        presenter.addAsIncome("Salary", "1000", new Date(), "description");

        verify(view, times(1)).clearForms();
    }

    @Test
    public void addAsExpenseThenClearForms() {
        mockSuccess();

        presenter.addAsExpense("Ice cream", "1000", new Date(), "description");

        verify(view, times(1)).clearForms();
    }

    @Test
    public void addAsIncomeWithEmptyItemThenShowItemValueEmptyError() {
        presenter.addAsIncome("", "1000", new Date(), "description");

        verify(view, times(1)).showItemValueEmptyError();
    }

    @Test
    public void addAsIncomeWithEmptyAmountThenShowAmountValueEmptyError() {
        presenter.addAsIncome("Salary", "", new Date(), "description");

        verify(view, times(1)).showAmountValueEmptyError();
    }

    @Test
    public void addAsIncomeWithZeroAmountThenShowAmountValueSmallError() {
        presenter.addAsIncome("Salary", "0", new Date(), "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsIncomeWithMinusAmountThenShowAmountValueSmallError() {
        presenter.addAsIncome("Salary", "-1", new Date(), "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsIncomeWithNonNumberAmountThenShowAmountValueFormatError() {
        presenter.addAsIncome("Salary", "10.0", new Date(), "description");

        verify(view, times(1)).showAmountValueFormatError();
    }

    @Test
    public void addAsExpenseWithEmptyItemThenShowItemValueEmptyError() {
        presenter.addAsExpense("", "1000", new Date(), "description");

        verify(view, times(1)).showItemValueEmptyError();
    }

    @Test
    public void addAsExpenseWithEmptyAmountThenShowAmountValueEmptyError() {
        presenter.addAsExpense("Ice cream", "", new Date(), "description");

        verify(view, times(1)).showAmountValueEmptyError();
    }

    @Test
    public void addAsExpenseWithZeroAmountThenShowAmountValueSmallError() {
        presenter.addAsExpense("Ice cream", "0", new Date(), "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsExpenseWithMinusAmountThenShowAmountValueSmallError() {
        presenter.addAsExpense("Ice cream", "-1", new Date(), "description");

        verify(view, times(1)).showAmountValueSmallError();
    }

    @Test
    public void addAsExpenseWithNonNumberAmountThenShowAmountValueFormatError() {
        presenter.addAsExpense("Ice cream", "10.0", new Date(), "description");

        verify(view, times(1)).showAmountValueFormatError();
    }
}